package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrdersRepository
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class FirebaseOrdersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : OrdersRepository {

    override suspend fun submit(order: Order) {
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
                val dto =
                    requireNotNull(snapshot.toObject(MenuItemDto::class.java)) { "Missing menu item ${ref.id}." }
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

        val nationalId = order.nationalId?.trim().orEmpty()
        if (nationalId.isNotEmpty() && order.appliedRewards.isNotEmpty()) {
            loyaltyRewardsRepository.reserveRewards(
                nationalId = nationalId,
                referenceType = LoyaltyRewardReferenceType.ORDER,
                referenceId = order.id,
                appliedRewards = order.appliedRewards,
            )
        }
    }

    override fun observeOrders(nationalId: String): Flow<List<Order>> = callbackFlow {
        val cleanNationalId = nationalId.trim()
        if (cleanNationalId.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", cleanNationalId)
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val orders = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(OrderDto::class.java)?.toDomain()
                        }.sortedByDescending { it.createdAt.time }
                        trySend(orders).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }
}
