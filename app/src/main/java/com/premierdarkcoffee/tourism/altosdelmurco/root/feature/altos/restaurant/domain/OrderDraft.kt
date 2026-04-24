package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.Date
import java.util.UUID

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
    val totalItems: Int = items.sumOf { it.safeQuantity }
    val subtotal: Double = items.sumOf { it.totalPrice }
    val totalAmount: Double = subtotal
    val isEmpty: Boolean = items.isEmpty()
    val hasValidClientName: Boolean = clientName.trim().isNotEmpty()
    val hasValidTableNumber: Boolean = tableNumber.trim().isNotEmpty()
    val canSubmit: Boolean = !isEmpty && hasValidClientName && hasValidTableNumber

    fun toOrder(
        orderId: String = UUID.randomUUID().toString(),
        status: OrderStatus = OrderStatus.PENDING,
    ): Order {
        val orderItems = items.map { item ->
            OrderItem(
                menuItemId = item.menuItem.id,
                name = item.menuItem.name,
                unitPrice = item.unitPrice,
                quantity = item.safeQuantity,
                notes = item.notes,
            )
        }

        return Order(
            id = orderId,
            nationalId = nationalId?.trim()?.takeIf { it.isNotEmpty() },
            clientName = clientName.trim(),
            tableNumber = tableNumber.trim(),
            createdAt = Date(),
            updatedAt = Date(),
            items = orderItems,
            subtotal = subtotal,
            loyaltyDiscountAmount = 0.0,
            appliedRewards = emptyList(),
            totalAmount = totalAmount,
            status = status,
            revision = revision ?: 0,
            lastConfirmedRevision = lastConfirmedRevision,
        )
    }
}