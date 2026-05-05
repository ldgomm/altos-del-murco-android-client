package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

interface OrdersRepositoriable {
    suspend fun submit(order: Order)
    fun observeOrders(userId: String): Flow<List<Order>>
}
