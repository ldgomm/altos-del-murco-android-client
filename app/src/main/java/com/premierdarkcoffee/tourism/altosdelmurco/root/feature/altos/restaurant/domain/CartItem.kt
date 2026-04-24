package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    val quantity: Int,
    val notes: String? = null,
) {
    val safeQuantity: Int = quantity.coerceAtLeast(1)
    val unitPrice: Double = menuItem.finalPrice
    val totalPrice: Double = safeQuantity * unitPrice

    fun withQuantity(newQuantity: Int): CartItem = copy(
        quantity = newQuantity.coerceAtLeast(1),
    )

    fun withNotes(newNotes: String?): CartItem = copy(
        notes = newNotes?.trim()?.takeIf { it.isNotEmpty() },
    )
}