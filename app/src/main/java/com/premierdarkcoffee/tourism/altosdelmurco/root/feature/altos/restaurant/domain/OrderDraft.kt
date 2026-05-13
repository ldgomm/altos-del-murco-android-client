package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import java.util.Date
import java.util.UUID
import kotlin.math.round

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

    val normalizedScheduledAt: Date
        get() = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt)

    val serviceMode: OrderServiceMode
        get() = OrderScheduleFormatter.mode(
            createdAt = Date(),
            scheduledAt = normalizedScheduledAt,
        )

    val isScheduledForLater: Boolean
        get() = serviceMode == OrderServiceMode.SCHEDULED

    /**
     * WhatsApp is intentionally not required here.
     * If the order is scheduled and the number is empty, UI can submit and then open WhatsApp.
     */
    val canSubmit: Boolean
        get() = !isEmpty &&
                hasValidClientName &&
                (hasValidTableNumber || isScheduledForLater)

    fun normalizedForSubmit(now: Date = Date()): OrderDraft {
        val resolvedScheduledAt = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt, now)
        val resolvedMode = OrderScheduleFormatter.mode(now, resolvedScheduledAt)

        return copy(
            userId = userId.trim(),
            clientName = clientName.trim(),
            tableNumber = tableNumber.trim(),
            whatsappNumber = if (resolvedMode == OrderServiceMode.SCHEDULED) whatsappNumber.trim() else "",
            scheduledAt = resolvedScheduledAt,
            updatedAt = now,
        )
    }

    fun toOrder(
        orderId: String = UUID.randomUUID().toString(),
        userId: String = this.userId,
        status: OrderStatus = OrderStatus.PENDING,
        loyaltyDiscountAmount: Double = 0.0,
        appliedRewards: List<AppliedReward> = emptyList(),
    ): Order {
        val now = Date()
        val safeScheduledAt = OrderScheduleFormatter.sanitizedScheduledAt(scheduledAt, now)
        val safeMode = OrderScheduleFormatter.mode(now, safeScheduledAt)

        val orderItems = items.flatMap { item ->
            OrderItem.normalizedUnits(
                sourceCartItemId = item.id,
                menuItemId = item.menuItem.id,
                name = item.menuItem.name,
                itemDescription = item.menuItem.description,
                unitPrice = item.unitPrice,
                quantity = item.safeQuantity,
                notes = item.notes,
                createdAt = now,
            )
        }

        val subtotal = orderItems.sumOf { it.totalPrice }.roundMoney()
        val safeLoyaltyDiscount = loyaltyDiscountAmount
            .coerceIn(0.0, subtotal)
            .roundMoney()
        val totalAmount = (subtotal - safeLoyaltyDiscount)
            .coerceAtLeast(0.0)
            .roundMoney()

        val cleanTable = tableNumber.trim()
        val cleanWhatsApp = whatsappNumber.trim()

        return Order(
            id = orderId,
            userId = userId.trim(),
            clientName = clientName.trim(),
            whatsappNumber = if (safeMode == OrderServiceMode.SCHEDULED) cleanWhatsApp else "",
            tableNumber = if (cleanTable.isEmpty() && safeMode == OrderServiceMode.SCHEDULED) {
                "Por asignar"
            } else {
                cleanTable
            },
            createdAt = now,
            updatedAt = now,
            scheduledAt = safeScheduledAt,
            scheduledDayKey = OrderScheduleFormatter.dayKey(safeScheduledAt),
            serviceMode = safeMode,
            items = orderItems,
            subtotal = subtotal,
            loyaltyDiscountAmount = safeLoyaltyDiscount,
            appliedRewards = appliedRewards,
            totalAmount = totalAmount,
            status = status,
            revision = revision ?: 0,
            lastConfirmedRevision = lastConfirmedRevision,
            readyForPaymentAt = null,
            paidAt = null,
            paymentMethod = null,
            paymentReference = null,
            paidByAdminId = null,
        )
    }
}

private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0