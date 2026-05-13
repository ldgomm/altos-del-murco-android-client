package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

interface OrdersRepositoriable {
    suspend fun submit(order: Order)
    suspend fun createOrder(order: Order) = submit(order)

    suspend fun updateOrder(order: Order)
    suspend fun adminUpdateOrder(order: Order)

    fun observeOrders(userId: String): Flow<List<Order>>
    fun observeTodayOrders(): Flow<List<Order>>
    fun observeOrder(orderId: String): Flow<Order?>

    suspend fun confirmOrder(orderId: String)
    suspend fun cancelOrder(orderId: String, reason: String? = null)

    suspend fun markItemPreparing(orderId: String, itemId: String)
    suspend fun markItemReadyForDelivery(orderId: String, itemId: String)
    suspend fun markItemDelivered(orderId: String, itemId: String)
    suspend fun markItemCanceled(orderId: String, itemId: String, reason: String? = null)

    suspend fun undoItemReadyForDelivery(orderId: String, itemId: String)
    suspend fun undoItemDelivered(orderId: String, itemId: String)

    suspend fun markOrderPaid(
        orderId: String,
        paymentMethod: String? = null,
        paymentReference: String? = null,
        paidByAdminId: String? = null,
    )
}