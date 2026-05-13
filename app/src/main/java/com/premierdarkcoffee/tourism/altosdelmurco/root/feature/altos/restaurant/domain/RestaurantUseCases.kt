package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import javax.inject.Inject

class ObserveOrdersUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    fun execute(userId: String) = repository.observeOrders(userId)
}

class ObserveTodayOrdersUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    fun execute() = repository.observeTodayOrders()
}

class ObserveOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    fun execute(orderId: String) = repository.observeOrder(orderId)
}

class SubmitOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(order: Order) = repository.submit(order)
}

class UpdateOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(order: Order) = repository.updateOrder(order)
}

class AdminUpdateOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(order: Order) = repository.adminUpdateOrder(order)
}

class ConfirmOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String) = repository.confirmOrder(orderId)
}

class CancelOrderUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, reason: String? = null) =
        repository.cancelOrder(orderId, reason)
}

class MarkOrderItemPreparingUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String) =
        repository.markItemPreparing(orderId, itemId)
}

class MarkOrderItemReadyForDeliveryUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String) =
        repository.markItemReadyForDelivery(orderId, itemId)
}

class MarkOrderItemDeliveredUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String) =
        repository.markItemDelivered(orderId, itemId)
}

class MarkOrderItemCanceledUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String, reason: String? = null) =
        repository.markItemCanceled(orderId, itemId, reason)
}

class UndoOrderItemReadyForDeliveryUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String) =
        repository.undoItemReadyForDelivery(orderId, itemId)
}

class UndoOrderItemDeliveredUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(orderId: String, itemId: String) =
        repository.undoItemDelivered(orderId, itemId)
}

class MarkOrderPaidUseCase @Inject constructor(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(
        orderId: String,
        paymentMethod: String? = null,
        paymentReference: String? = null,
        paidByAdminId: String? = null,
    ) = repository.markOrderPaid(orderId, paymentMethod, paymentReference, paidByAdminId)
}

class ObserveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    fun execute() = repository.observeDraft()
}

class SaveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute(draft: OrderDraft) = repository.saveDraft(draft)
}

class ClearCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute() = repository.clear()
}