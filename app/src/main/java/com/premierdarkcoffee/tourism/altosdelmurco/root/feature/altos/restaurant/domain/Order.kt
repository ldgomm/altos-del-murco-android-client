package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class OrderServiceMode(val rawValue: String, val title: String) {
    NOW("now", "Pedido inmediato"),
    SCHEDULED("scheduled", "Reserva de comida");

    companion object {
        fun fromRaw(rawValue: String?): OrderServiceMode {
            return entries.firstOrNull { it.rawValue.equals(rawValue, ignoreCase = true) || it.name.equals(rawValue, ignoreCase = true) }
                ?: NOW
        }
    }
}

data class Order(
    val id: String,
    val nationalId: String?,
    val clientName: String,
    val tableNumber: String,
    val createdAt: Date,
    val updatedAt: Date,
    val scheduledAt: Date = createdAt,
    val scheduledDayKey: String = OrderScheduleFormatter.dayKey(scheduledAt),
    val serviceMode: OrderServiceMode = OrderScheduleFormatter.mode(createdAt, scheduledAt),
    val items: List<OrderItem>,
    val subtotal: Double,
    val loyaltyDiscountAmount: Double = 0.0,
    val appliedRewards: List<AppliedReward> = emptyList(),
    val totalAmount: Double,
    val status: OrderStatus,
    val revision: Int,
    val lastConfirmedRevision: Int?,
) {
    val totalItems: Int = items.sumOf { it.quantity }
    val preparedItemsCount: Int = items.sumOf { it.safePreparedQuantity }
    val allItemsCompleted: Boolean = items.isNotEmpty() && items.all { it.isCompleted }
    val hasStartedPreparing: Boolean = items.any { it.isStarted }
    val requiresReconfirmation: Boolean = lastConfirmedRevision != revision
    val wasEditedAfterConfirmation: Boolean = lastConfirmedRevision?.let { revision > it } ?: false

    val isScheduledForLater: Boolean = serviceMode == OrderServiceMode.SCHEDULED ||
            scheduledAt.time - createdAt.time > OrderScheduleFormatter.LATER_THRESHOLD_MS

    val shouldConsumeCurrentMenuStock: Boolean =
        !isScheduledForLater || OrderScheduleFormatter.sameDay(scheduledAt, Date())

    val scheduledDateText: String get() = OrderScheduleFormatter.displayText(scheduledAt)

    fun withLoyalty(
        appliedRewards: List<AppliedReward>,
        discount: Double,
    ): Order {
        val safeDiscount = discount.coerceIn(0.0, subtotal)
        return copy(
            loyaltyDiscountAmount = safeDiscount,
            appliedRewards = appliedRewards,
            totalAmount = (subtotal - safeDiscount).coerceAtLeast(0.0),
        )
    }
}

object OrderScheduleFormatter {
    const val LATER_THRESHOLD_MS: Long = 5 * 60 * 1000L

    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormatter = SimpleDateFormat("EEE d MMM, h:mm a", Locale("es", "EC"))

    fun dayKey(date: Date): String = dayFormatter.format(date)

    fun displayText(date: Date): String = displayFormatter.format(date)

    fun mode(createdAt: Date, scheduledAt: Date): OrderServiceMode =
        if (scheduledAt.time - createdAt.time > LATER_THRESHOLD_MS) OrderServiceMode.SCHEDULED else OrderServiceMode.NOW

    fun sanitizedScheduledAt(value: Date, now: Date = Date()): Date =
        if (value.time < now.time - 120_000L) now else value

    fun sameDay(lhs: Date, rhs: Date): Boolean = dayKey(lhs) == dayKey(rhs)

    fun combineDateAndTime(day: Date, hourOfDay: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = day
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}
