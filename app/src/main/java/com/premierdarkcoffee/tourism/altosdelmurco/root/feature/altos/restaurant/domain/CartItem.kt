package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    val quantity: Int,
    val notes: String? = null,
) {
    val unitPrice: Double = menuItem.finalPrice

    val totalPrice: Double = quantity * unitPrice
}
