package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItemStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrdersRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

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

        if (trustedOrder.appliedRewards.isNotEmpty()) {
            loyaltyRewardsRepository.reserveRewards(
                userId = trustedOrder.userId,
                referenceType = LoyaltyRewardReferenceType.ORDER,
                referenceId = trustedOrder.id,
                appliedRewards = trustedOrder.appliedRewards,
            )
        }
    }

    override suspend fun updateOrder(order: Order) {
        val uid = requireCurrentUid()
        require(order.userId == uid) { "No puedes editar este pedido." }

        val cleanOrder = order.copy(
            userId = uid,
            updatedAt = Date(),
            items = Order.normalizedItemLines(order.items),
        )

        firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(cleanOrder.id)
            .set(OrderDto(cleanOrder), SetOptions.merge()).awaitResult()
    }

    override suspend fun adminUpdateOrder(order: Order) {
        val cleanOrder = order.copy(
            updatedAt = Date(),
            items = Order.normalizedItemLines(order.items),
        )

        firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(cleanOrder.id)
            .set(OrderDto(cleanOrder), SetOptions.merge()).awaitResult()
    }

    override fun observeOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val uid = auth.currentUser?.uid?.trim().orEmpty()
        val trustedUserId = uid.ifBlank { userId.trim() }

        if (trustedUserId.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration =
            firestore.collection(FirestoreCollections.RESTAURANT_ORDERS)
                .whereEqualTo("userId", trustedUserId)
                .orderBy("scheduledAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    when {
                        error != null -> close(error)
                        snapshot == null -> trySend(emptyList()).isSuccess
                        else -> {
                            val orders = snapshot.documents.mapNotNull { doc ->
                                runCatching {
                                    doc.toObject(OrderDto::class.java)?.toDomain()
                                }.onFailure {
                                    Log.e("AltosOrders", "Could not decode order ${doc.id}", it)
                                }.getOrNull()
                            }.sortedWith(
                                compareByDescending<Order> { it.scheduledAt.time }.thenByDescending { it.createdAt.time },
                            )
                            trySend(orders).isSuccess
                        }
                    }
                }

        awaitClose { registration.remove() }
    }

    override fun observeTodayOrders(): Flow<List<Order>> = callbackFlow {
        val calendar = Calendar.getInstance()
        val now = Date()
        val start = calendar.apply {
            time = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val end = Calendar.getInstance().apply {
            time = start
            add(Calendar.DAY_OF_YEAR, 1)
        }.time

        val registration = firestore.collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereGreaterThanOrEqualTo("scheduledAt", Timestamp(start))
            .whereLessThan("scheduledAt", Timestamp(end)).addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val orders = snapshot.documents.mapNotNull { document ->
                            runCatching {
                                document.toObject(OrderDto::class.java)?.toDomain()
                            }.getOrNull()
                        }.sortedWith(::operationalSort)

                        trySend(orders).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }

    override fun observeOrder(orderId: String): Flow<Order?> = callbackFlow {
        val cleanId = orderId.trim()
        if (cleanId.isEmpty()) {
            trySend(null).isSuccess
            close()
            return@callbackFlow
        }

        val registration =
            firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(cleanId)
                .addSnapshotListener { snapshot, error ->
                    when {
                        error != null -> close(error)
                        snapshot == null || !snapshot.exists() -> trySend(null).isSuccess
                        else -> {
                            val order = runCatching {
                                snapshot.toObject(OrderDto::class.java)?.toDomain()
                            }.getOrNull()
                            trySend(order).isSuccess
                        }
                    }
                }

        awaitClose { registration.remove() }
    }

    override suspend fun confirmOrder(orderId: String) {
        val ref = orderRef(orderId)

        firestore.runTransaction { transaction ->
            val order = readOrder(ref, transaction)
            val updated = order.confirming(now = Date())

            transaction.set(ref, OrderDto(updated), SetOptions.merge())
            null
        }.awaitResult()
    }

    override suspend fun cancelOrder(orderId: String, reason: String?) {
        val uid = auth.currentUser?.uid?.trim().orEmpty()
        val ref = orderRef(orderId)
        var releaseUserId: String? = null
        var releaseReferenceId: String? = null

        firestore.runTransaction { transaction ->
            val order = readOrder(ref, transaction)

            if (uid.isNotEmpty()) {
                require(order.userId == uid || isAdminLikeAction()) {
                    "No puedes cancelar este pedido."
                }
            }

            // Client rule: pending only. Admin can still call this repository from admin/kitchen context
            // if rules allow it, but the Android client UI must only expose pending cancellation.
            require(order.status == OrderStatus.PENDING || isAdminLikeAction()) {
                "Este pedido ya está en cocina y no puede cancelarse desde la app."
            }

            val updated = order.canceling(reason = reason, now = Date())

            if (updated.hasLoyaltyRewards) {
                releaseUserId = updated.userId
                releaseReferenceId = updated.id
            }

            transaction.set(ref, OrderDto(updated), SetOptions.merge())
            null
        }.awaitResult()

        if (!releaseUserId.isNullOrBlank() && !releaseReferenceId.isNullOrBlank()) {
            loyaltyRewardsRepository.releaseRewards(
                userId = releaseUserId.orEmpty(),
                referenceId = releaseReferenceId.orEmpty(),
            )
        }
    }

    override suspend fun markItemPreparing(orderId: String, itemId: String) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.PREPARING,
        ) { item ->
            require(
                item.status == OrderItemStatus.PENDING || item.status == OrderItemStatus.PREPARING,
            ) {
                "Solo puedes iniciar un plato pendiente."
            }
        }
    }

    override suspend fun markItemReadyForDelivery(orderId: String, itemId: String) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.READY_FOR_DELIVERY,
        ) { item ->
            require(
                item.status == OrderItemStatus.PENDING || item.status == OrderItemStatus.PREPARING || item.status == OrderItemStatus.READY_FOR_DELIVERY,
            ) {
                "Solo puedes marcar listo un plato pendiente o en preparación."
            }
        }
    }

    override suspend fun markItemDelivered(orderId: String, itemId: String) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.DELIVERED,
        ) { item ->
            require(item.status == OrderItemStatus.READY_FOR_DELIVERY) {
                "Solo puedes servir un plato que ya está listo."
            }
        }
    }

    override suspend fun markItemCanceled(orderId: String, itemId: String, reason: String?) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.CANCELED,
            reason = reason,
        ) { item ->
            require(item.status != OrderItemStatus.DELIVERED) {
                "No puedes cancelar un plato ya servido sin flujo de corrección explícito."
            }
            require(item.status != OrderItemStatus.CANCELED) {
                "Este plato ya fue cancelado."
            }
        }
    }

    override suspend fun undoItemReadyForDelivery(orderId: String, itemId: String) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.PREPARING,
        ) { item ->
            require(item.status == OrderItemStatus.READY_FOR_DELIVERY) {
                "Solo puedes deshacer listo desde un plato listo."
            }
        }
    }

    override suspend fun undoItemDelivered(orderId: String, itemId: String) {
        updateItemStatus(
            orderId = orderId,
            itemId = itemId,
            targetStatus = OrderItemStatus.READY_FOR_DELIVERY,
        ) { item ->
            require(item.status == OrderItemStatus.DELIVERED) {
                "Solo puedes deshacer servido desde un plato servido."
            }
        }
    }

    override suspend fun markOrderPaid(
        orderId: String,
        paymentMethod: String?,
        paymentReference: String?,
        paidByAdminId: String?,
    ) {
        val ref = orderRef(orderId)
        var paidUserId: String? = null
        var paidReferenceId: String? = null

        firestore.runTransaction { transaction ->
            val order = readOrder(ref, transaction)

            require(order.status != OrderStatus.PAID && order.status != OrderStatus.CANCELED) {
                "El pedido ya está cerrado."
            }

            val recalculated = order.copy(status = order.recalculatedStatus())
            require(recalculated.status == OrderStatus.READY_FOR_PAYMENT) {
                "Todavía faltan platos por servir."
            }

            val updated = recalculated.markingPaid(
                paymentMethod = paymentMethod,
                paymentReference = paymentReference,
                paidByAdminId = paidByAdminId,
                now = Date(),
            )

            paidUserId = updated.userId
            paidReferenceId = updated.id

            transaction.set(ref, OrderDto(updated), SetOptions.merge())
            null
        }.awaitResult()

        if (!paidUserId.isNullOrBlank() && !paidReferenceId.isNullOrBlank()) {
            loyaltyRewardsRepository.consumeRewards(
                userId = paidUserId.orEmpty(),
                referenceId = paidReferenceId.orEmpty(),
            )
        }
    }

    private suspend fun buildTrustedOrder(order: Order): Order {
        val uid = requireCurrentUid()
        val requestedItems = Order.normalizedItemLines(order.items)

        val trustedItems = requestedItems.map { requestedItem ->
            val menuRef = firestore.collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
                .document(requestedItem.menuItemId)

            val menuDto = menuRef.get().awaitResult().toObject(MenuItemDto::class.java)
                ?: error("No se encontró ${requestedItem.name}.")

            val menuItem = menuDto.toDomain(menuRef.id)
            require(menuItem.id.isNotBlank()) { "Producto inválido." }

            OrderItem(
                id = requestedItem.id,
                groupId = requestedItem.groupId,
                sourceCartItemId = requestedItem.sourceCartItemId,
                menuItemId = menuItem.id,
                name = menuItem.name,
                itemDescription = menuItem.description,
                unitPrice = menuItem.finalPrice,
                quantity = 1,
                notes = requestedItem.notes?.trim()?.takeIf { it.isNotEmpty() },
                status = OrderItemStatus.PENDING,
                createdAt = requestedItem.createdAt,
            )
        }

        val preview = loyaltyRewardsRepository.previewRestaurantRewards(
            userId = uid,
            items = trustedItems,
        )

        return order.copy(
            userId = uid,
            clientName = order.clientName.trim(),
            whatsappNumber = if (order.isScheduledForLater) {
                normalizedOptionalEcuadorWhatsApp(order.whatsappNumber)
            } else {
                ""
            },
            tableNumber = order.tableNumber.trim().ifBlank {
                if (order.isScheduledForLater) "Por asignar" else error("Completa la mesa.")
            },
            status = OrderStatus.PENDING,
            updatedAt = Date(),
            readyForPaymentAt = null,
            paidAt = null,
            paymentMethod = null,
            paymentReference = null,
            paidByAdminId = null,
        ).withTrustedPricing(
            trustedItems = trustedItems,
            appliedRewards = preview.appliedRewards,
            discount = preview.totalDiscount,
        )
    }

    private suspend fun submitFutureFoodReservation(order: Order) {
        firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(order.id)
            .set(OrderDto(order)).awaitResult()
    }

    private suspend fun submitAndConsumeCurrentStock(order: Order) {
        val quantitiesByMenuItemId =
            order.items.filter { it.status != OrderItemStatus.CANCELED }.groupBy { it.menuItemId }
                .mapValues { (_, items) -> items.sumOf { it.quantity.coerceAtLeast(1) } }
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
                require(dto.remainingQuantity >= totalQuantity) {
                    "Ya no hay suficiente stock de ${dto.name}."
                }

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
            transaction.set(orderRef, OrderDto(order.copy(status = OrderStatus.PENDING)))
            null
        }.awaitResult()
    }

    private suspend fun updateItemStatus(
        orderId: String,
        itemId: String,
        targetStatus: OrderItemStatus,
        reason: String? = null,
        validate: (OrderItem) -> Unit,
    ) {
        val ref = orderRef(orderId)

        firestore.runTransaction { transaction ->
            val order = readOrder(ref, transaction)

            require(order.status != OrderStatus.PAID && order.status != OrderStatus.CANCELED) {
                "El pedido ya está cerrado."
            }

            require(order.status != OrderStatus.PENDING) {
                "Primero confirma el pedido antes de mover platos en cocina."
            }

            val currentItem =
                order.items.firstOrNull { it.id == itemId } ?: error("No encontré el plato exacto.")

            validate(currentItem)

            val now = Date()
            val updated = order.updatingItem(
                itemId = itemId,
                now = now,
            ) { item ->
                item.updatingStatus(
                    newStatus = targetStatus,
                    now = now,
                    reason = reason,
                )
            }

            transaction.set(ref, OrderDto(updated), SetOptions.merge())
            null
        }.awaitResult()
    }

    private fun readOrder(
        ref: DocumentReference,
        transaction: com.google.firebase.firestore.Transaction,
    ): Order {
        val snapshot = transaction.get(ref)
        require(snapshot.exists()) { "Pedido no encontrado." }

        return snapshot.toObject(OrderDto::class.java)?.toDomain()
            ?: error("No se pudo leer el pedido.")
    }

    private fun orderRef(orderId: String): DocumentReference {
        val cleanId = orderId.trim()
        require(cleanId.isNotEmpty()) { "orderId vacío." }

        return firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(cleanId)
    }

    private fun operationalSort(lhs: Order, rhs: Order): Int {
        val lhsKey = sortKey(lhs)
        val rhsKey = sortKey(rhs)

        if (lhsKey.rank != rhsKey.rank) return lhsKey.rank - rhsKey.rank
        return rhsKey.date.compareTo(lhsKey.date)
    }

    private data class SortKey(
        val rank: Int,
        val date: Date,
    )

    private fun sortKey(order: Order): SortKey {
        return when {
            order.hasReadyForDeliveryItems -> SortKey(
                0, order.newestReadyForDeliveryAt ?: order.updatedAt
            )

            order.status == OrderStatus.READY_FOR_PAYMENT -> SortKey(
                1, order.readyForPaymentAt ?: order.updatedAt
            )

            order.status == OrderStatus.PREPARING -> SortKey(2, order.updatedAt)

            order.status == OrderStatus.CONFIRMED -> SortKey(3, order.updatedAt)

            order.status == OrderStatus.PENDING -> SortKey(4, order.createdAt)

            order.status == OrderStatus.PAID -> SortKey(5, order.paidAt ?: order.updatedAt)

            order.status == OrderStatus.CANCELED -> SortKey(6, order.updatedAt)

            else -> SortKey(9, order.updatedAt)
        }
    }

    private fun normalizedOptionalEcuadorWhatsApp(rawValue: String): String {
        val digits = rawValue.filter(Char::isDigit)
        if (digits.isEmpty()) return ""

        return when {
            digits.length == 10 && digits.startsWith("09") -> "593${digits.drop(1)}"
            digits.length == 12 && digits.startsWith("5939") -> digits
            digits.length == 9 && digits.startsWith("9") -> "593$digits"
            else -> error("El WhatsApp ingresado no parece válido. Corrígelo o déjalo vacío para escribirnos después por WhatsApp.")
        }
    }

    private fun requireCurrentUid(): String {
        return auth.currentUser?.uid?.trim()?.takeIf { it.isNotEmpty() }
            ?: error("Debes iniciar sesión nuevamente para enviar el pedido.")
    }

    /**
     * Placeholder for shared repository usage by Admin/Kitchen builds.
     * In the Android client UI, do not expose admin-only operations.
     */
    private fun isAdminLikeAction(): Boolean = false
}

private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0