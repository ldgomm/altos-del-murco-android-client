package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.UUID

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val preparedQuantity: Int = 0,
    val notes: String? = null,
) {
    init {
        require(quantity >= 0) { "quantity must be >= 0" }
    }

    val safePreparedQuantity: Int = preparedQuantity.coerceIn(0, quantity)

    val totalPrice: Double = quantity * unitPrice

    val remainingQuantity: Int = quantity - safePreparedQuantity

    val isStarted: Boolean = safePreparedQuantity > 0

    val isCompleted: Boolean = safePreparedQuantity == quantity

    fun updatingPreparedQuantity(newValue: Int): OrderItem = copy(
        preparedQuantity = newValue.coerceIn(0, quantity),
    )
}
