package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.Date
import java.util.UUID

private const val DEFAULT_DRAFT_ID = "active_cart"

data class OrderDraft(
    val id: String = DEFAULT_DRAFT_ID,
    /** Firebase Auth UID. Canonical owner field. */
    val userId: String = "",
    val clientName: String = "",
    val whatsappNumber: String = "",
    val tableNumber: String = "",
    val scheduledAt: Date = Date(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val items: List<CartItem> = emptyList(),
    val revision: Int? = null,
    val lastConfirmedRevision: Int? = null,
) {
    val totalItems: Int = items.sumOf { it.safeQuantity }
    val subtotal: Double = items.sumOf { it.totalPrice }.roundMoney()
    val totalAmount: Double = subtotal
    val isEmpty: Boolean = items.isEmpty()
    val hasValidClientName: Boolean = clientName.trim().isNotEmpty()
    val hasValidTableNumber: Boolean = tableNumber.trim().isNotEmpty()

    val normalizedScheduledAt: Date = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt)
    val serviceMode: OrderServiceMode = OrderScheduleFormatter.mode(Date(), normalizedScheduledAt)
    val isScheduledForLater: Boolean = serviceMode == OrderServiceMode.SCHEDULED
    val canSubmit: Boolean =
        !isEmpty && hasValidClientName && (hasValidTableNumber || isScheduledForLater)

    fun normalizedForSubmit(now: Date = Date()): OrderDraft = copy(
        userId = userId.trim(),
        whatsappNumber = if (isScheduledForLater) whatsappNumber.trim() else "",
        scheduledAt = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt, now),
        updatedAt = now,
    )

    fun toOrder(
        orderId: String = UUID.randomUUID().toString(),
        userId: String = this.userId,
        status: OrderStatus = OrderStatus.PENDING,
    ): Order {
        val now = Date()
        val safeScheduledAt = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt, now)
        val safeMode = OrderScheduleFormatter.mode(now, safeScheduledAt)
        val orderItems = items.map { item ->
            OrderItem(
                menuItemId = item.menuItem.id,
                name = item.menuItem.name,
                unitPrice = item.unitPrice,
                quantity = item.safeQuantity,
                notes = item.notes,
            )
        }

        val cleanTable = tableNumber.trim()
        val cleanWhatsApp = whatsappNumber.trim()

        return Order(
            id = orderId,
            userId = userId.trim(),
            clientName = clientName.trim(),
            whatsappNumber = if (safeMode == OrderServiceMode.SCHEDULED) cleanWhatsApp else "",
            tableNumber = if (cleanTable.isEmpty() && safeMode == OrderServiceMode.SCHEDULED) "Por asignar" else cleanTable,
            createdAt = now,
            updatedAt = now,
            scheduledAt = safeScheduledAt,
            scheduledDayKey = OrderScheduleFormatter.dayKey(safeScheduledAt),
            serviceMode = safeMode,
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

private fun Double.roundMoney(): Double = kotlin.math.round(this * 100.0) / 100.0
