package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrdersRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : OrdersRepositoriable {

    override suspend fun submit(order: Order) {
        val trustedOrder = buildTrustedOrder(order)

        if (trustedOrder.scheduledAt.before(Date(System.currentTimeMillis() - 120_000L))) {
            error("La fecha de la reserva ya pasó. Elige una hora actual o futura.")
        }

        if (trustedOrder.shouldConsumeCurrentMenuStock) {
            submitAndConsumeCurrentStock(trustedOrder)
        } else {
            submitFutureFoodReservation(trustedOrder)
        }

        val nationalId = trustedOrder.nationalId?.filter(Char::isDigit).orEmpty()
        if (nationalId.isNotEmpty() && trustedOrder.appliedRewards.isNotEmpty()) {
            loyaltyRewardsRepository.reserveRewards(
                nationalId = nationalId,
                referenceType = LoyaltyRewardReferenceType.ORDER,
                referenceId = trustedOrder.id,
                appliedRewards = trustedOrder.appliedRewards,
            )
        }
    }

    private suspend fun buildTrustedOrder(order: Order): Order {
        val uid = requireCurrentUid()
        val cleanNationalId = order.nationalId?.filter(Char::isDigit)?.takeIf { it.isNotEmpty() }
            ?: error("No se encontró una cédula asociada a esta cuenta.")

        val trustedItems = order.items.map { requestedItem ->
            val menuRef = firestore
                .collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
                .document(requestedItem.menuItemId)

            val menuDto = menuRef.get().awaitResult().toObject(MenuItemDto::class.java)
                ?: error("No se encontró ${requestedItem.name}.")

            val menuItem = menuDto.toDomain(menuRef.id)
            require(menuItem.id.isNotBlank()) { "Producto inválido." }

            OrderItem(
                menuItemId = menuItem.id,
                name = menuItem.name,
                unitPrice = menuItem.finalPrice,
                quantity = requestedItem.quantity.coerceAtLeast(1),
                preparedQuantity = 0,
                notes = requestedItem.notes?.trim()?.takeIf { it.isNotEmpty() },
            )
        }

        val preview = loyaltyRewardsRepository.previewRestaurantRewards(
            nationalId = cleanNationalId,
            items = trustedItems,
        )

        return order
            .copy(
                clientId = uid,
                nationalId = cleanNationalId,
                clientName = order.clientName.trim(),
                tableNumber = order.tableNumber.trim().ifBlank {
                    if (order.isScheduledForLater) "Por asignar" else error("Completa la mesa.")
                },
                updatedAt = Date(),
            )
            .withTrustedPricing(
                trustedItems = trustedItems,
                appliedRewards = preview.appliedRewards,
                discount = preview.totalDiscount,
            )
    }

    private suspend fun submitFutureFoodReservation(order: Order) {
        firestore.collection(FirestoreCollections.RESTAURANT_ORDERS)
            .document(order.id)
            .set(OrderDto(order))
            .awaitResult()
    }

    private suspend fun submitAndConsumeCurrentStock(order: Order) {
        val quantitiesByMenuItemId = order.items
            .groupBy { it.menuItemId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }
            .filterValues { it > 0 }

        val menuItemsToProcess: List<Pair<DocumentReference, Int>> =
            quantitiesByMenuItemId.map { (menuItemId, totalQuantity) ->
                firestore.collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
                    .document(menuItemId) to totalQuantity
            }

        firestore.runTransaction { transaction ->
            val loadedItems = menuItemsToProcess.map { (ref, totalQuantity) ->
                val snapshot = transaction.get(ref)
                val dto = requireNotNull(snapshot.toObject(MenuItemDto::class.java)) {
                    "Missing menu item ${ref.id}."
                }
                Triple(ref, dto, totalQuantity)
            }

            loadedItems.forEach { (ref, dto, totalQuantity) ->
                require(dto.isAvailable) { "${dto.name} no está disponible." }
                require(dto.remainingQuantity >= totalQuantity) { "Ya no hay suficiente stock de ${dto.name}." }

                val newRemainingQuantity = dto.remainingQuantity - totalQuantity
                transaction.update(
                    ref,
                    mapOf(
                        "remainingQuantity" to newRemainingQuantity,
                        "isAvailable" to (newRemainingQuantity > 0),
                        "updatedAt" to Timestamp.now(),
                    ),
                )
            }

            val orderRef =
                firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(order.id)
            transaction.set(orderRef, OrderDto(order))
            null
        }.awaitResult()
    }

    override fun observeOrders(nationalId: String): Flow<List<Order>> = callbackFlow {
        val uid = auth.currentUser?.uid?.trim().orEmpty()

        if (uid.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("clientId", uid)
            .orderBy("scheduledAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val orders = snapshot.documents.mapNotNull { doc ->
                            runCatching { doc.toObject(OrderDto::class.java)?.toDomain() }
                                .onFailure {
                                    Log.e(
                                        "AltosOrders",
                                        "Could not decode order ${doc.id}",
                                        it
                                    )
                                }
                                .getOrNull()
                        }.sortedWith(
                            compareByDescending<Order> { it.scheduledAt.time }
                                .thenByDescending { it.createdAt.time },
                        )
                        trySend(orders).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }

    private fun Double.roundMoney(): Double = kotlin.math.round(this * 100.0) / 100.0

    private fun requireCurrentUid(): String {
        return auth.currentUser?.uid?.trim()?.takeIf { it.isNotEmpty() }
            ?: error("Debes iniciar sesión nuevamente para enviar el pedido.")
    }
}
