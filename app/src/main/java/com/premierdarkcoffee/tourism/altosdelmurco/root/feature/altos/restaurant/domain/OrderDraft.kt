package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.Date

private const val DEFAULT_DRAFT_ID = "active_cart"

data class OrderDraft(
    val id: String = DEFAULT_DRAFT_ID,
    val nationalId: String? = null,
    val clientName: String = "",
    val tableNumber: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val items: List<CartItem> = emptyList(),
    val revision: Int? = null,
    val lastConfirmedRevision: Int? = null,
) {
    val totalItems: Int = items.sumOf { it.quantity }

    val subtotal: Double = items.sumOf { it.totalPrice }

    val totalAmount: Double = subtotal

    val isEmpty: Boolean = items.isEmpty()

    val hasValidClientName: Boolean = clientName.trim().isNotEmpty()

    val hasValidTableNumber: Boolean = tableNumber.trim().isNotEmpty()

    val canSubmit: Boolean = !isEmpty && hasValidClientName && hasValidTableNumber

    fun toOrder(
        orderId: String,
        status: OrderStatus = OrderStatus.PENDING,
    ): Order {
        val orderItems = items.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                name = it.menuItem.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
                notes = it.notes,
            )
        }

        return Order(
            id = orderId,
            nationalId = nationalId,
            clientName = clientName.trim(),
            tableNumber = tableNumber.trim(),
            createdAt = Date(),
            updatedAt = Date(),
            items = orderItems,
            subtotal = subtotal,
            totalAmount = totalAmount,
            status = status,
            revision = revision ?: 0,
            lastConfirmedRevision = lastConfirmedRevision,
        )
    }
}
