package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem

data class OrderItemDto(
    val id: String = "",
    val menuItemId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
    val preparedQuantity: Int? = 0,
    val totalPrice: Double? = null,
    val notes: String? = null,
) {
    constructor(domain: OrderItem) : this(
        id = domain.id,
        menuItemId = domain.menuItemId,
        name = domain.name,
        unitPrice = domain.unitPrice,
        quantity = domain.quantity,
        preparedQuantity = domain.preparedQuantity,
        totalPrice = domain.totalPrice,
        notes = domain.notes,
    )

    fun toDomain(): OrderItem = OrderItem(
        id = id,
        menuItemId = menuItemId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        preparedQuantity = preparedQuantity ?: 0,
        notes = notes,
    )
}
