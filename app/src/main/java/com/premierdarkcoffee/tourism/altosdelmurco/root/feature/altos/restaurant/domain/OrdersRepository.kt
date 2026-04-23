package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    suspend fun submit(order: Order)
    fun observeOrders(nationalId: String): Flow<List<Order>>
}
