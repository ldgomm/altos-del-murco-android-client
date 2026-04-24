package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus

data class OrdersUiState(
    val nationalId: String = "",
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val grouping: OrdersGroupingOption = OrdersGroupingOption.DATE,
    val sortOption: OrdersSortOption = OrdersSortOption.NEWEST,
    val statusFilter: OrderStatus? = null,
    val errorMessage: String? = null,
) {
    val visibleOrders: List<Order>
        get() {
            val filtered = statusFilter?.let { status -> orders.filter { it.status == status } } ?: orders
            return when (sortOption) {
                OrdersSortOption.NEWEST -> filtered.sortedByDescending { it.createdAt.time }
                OrdersSortOption.OLDEST -> filtered.sortedBy { it.createdAt.time }
                OrdersSortOption.HIGHEST_TOTAL -> filtered.sortedByDescending { it.totalAmount }
            }
        }
}

enum class OrdersGroupingOption(val title: String) {
    DATE("Fecha"),
    STATUS("Estado"),
}

enum class OrdersSortOption(val title: String) {
    NEWEST("Recientes"),
    OLDEST("Antiguos"),
    HIGHEST_TOTAL("Mayor total"),
}