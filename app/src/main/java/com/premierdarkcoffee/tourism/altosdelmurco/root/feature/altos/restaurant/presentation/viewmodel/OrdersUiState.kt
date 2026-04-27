package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view.recalculatedAgendaStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus

data class OrdersUiState(
    val nationalId: String = "",
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val grouping: OrdersGroupingOption = OrdersGroupingOption.DATE,
    val sortOption: OrdersSortOption = OrdersSortOption.NEAREST_SERVICE,
    val statusFilter: OrderStatus? = null,
    val errorMessage: String? = null,
) {
    val visibleOrders: List<Order>
        get() {
            val filtered = statusFilter?.let { status ->
                orders.filter { it.recalculatedAgendaStatus() == status }
            } ?: orders

            return when (sortOption) {
                OrdersSortOption.NEAREST_SERVICE -> filtered.sortedWith(compareBy<Order> { it.scheduledAt.time }.thenBy { it.createdAt.time })

                OrdersSortOption.FARTHEST_SERVICE -> filtered.sortedWith(compareByDescending<Order> { it.scheduledAt.time }.thenByDescending { it.createdAt.time })

                OrdersSortOption.NEWEST_CREATED -> filtered.sortedWith(compareByDescending<Order> { it.createdAt.time }.thenByDescending { it.scheduledAt.time })

                OrdersSortOption.HIGHEST_TOTAL -> filtered.sortedWith(compareByDescending<Order> { it.totalAmount }.thenBy { it.scheduledAt.time })
            }
        }
}

enum class OrdersGroupingOption(val title: String) {
    DATE("Fecha de servicio"), STATUS("Estado"),
}

enum class OrdersSortOption(val title: String) {
    NEAREST_SERVICE("Más cercana"), FARTHEST_SERVICE("Más lejana"), NEWEST_CREATED("Más reciente"), HIGHEST_TOTAL(
        "Mayor total"
    ),
}
