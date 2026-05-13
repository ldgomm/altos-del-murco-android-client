package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

enum class OrderServiceMode(val rawValue: String, val title: String) {
    NOW("now", "Pedido inmediato"),
    SCHEDULED("scheduled", "Reserva de comida");

    companion object {
        fun fromRaw(rawValue: String?): OrderServiceMode {
            return entries.firstOrNull {
                it.rawValue.equals(rawValue, ignoreCase = true) ||
                        it.name.equals(rawValue, ignoreCase = true)
            } ?: NOW
        }
    }
}

data class Order(
    val id: String,
    /** Firebase Auth UID. Canonical owner field for Firestore rules and user queries. */
    val userId: String,
    val clientName: String,
    val whatsappNumber: String = "",
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
    val revision: Int = 0,
    val lastConfirmedRevision: Int? = null,
    val readyForPaymentAt: Date? = null,
    val paidAt: Date? = null,
    val paymentMethod: String? = null,
    val paymentReference: String? = null,
    val paidByAdminId: String? = null,
) {
    val normalizedItems: List<OrderItem>
        get() = normalizedItemLines(items)

    val activeItems: List<OrderItem>
        get() = items.filter { it.status != OrderItemStatus.CANCELED }

    val readyForDeliveryItems: List<OrderItem>
        get() = activeItems
            .filter { it.status == OrderItemStatus.READY_FOR_DELIVERY }
            .sortedByDescending { it.readyForDeliveryAt?.time ?: it.createdAt.time }

    val deliveredItems: List<OrderItem>
        get() = activeItems.filter { it.status == OrderItemStatus.DELIVERED }

    val pendingOrPreparingItems: List<OrderItem>
        get() = activeItems.filter {
            it.status == OrderItemStatus.PENDING ||
                    it.status == OrderItemStatus.PREPARING
        }

    val hasReadyForDeliveryItems: Boolean
        get() = readyForDeliveryItems.isNotEmpty()

    val hasLoyaltyRewards: Boolean
        get() = appliedRewards.isNotEmpty() || loyaltyDiscountAmount > 0.0

    val totalItems: Int
        get() = activeItems.sumOf { it.quantity.coerceAtLeast(1) }

    val deliveredItemsCount: Int
        get() = deliveredItems.size

    val progress: Double
        get() {
            if (activeItems.isEmpty()) return 0.0
            return deliveredItems.size.toDouble() / activeItems.size.toDouble()
        }

    val requiresReconfirmation: Boolean
        get() = lastConfirmedRevision != revision

    val wasEditedAfterConfirmation: Boolean
        get() = lastConfirmedRevision?.let { revision > it } ?: false

    val isScheduledForLater: Boolean
        get() = serviceMode == OrderServiceMode.SCHEDULED ||
                scheduledAt.time - createdAt.time > OrderScheduleFormatter.LATER_THRESHOLD_MS

    val shouldConsumeCurrentMenuStock: Boolean
        get() = !isScheduledForLater

    val scheduledDateText: String
        get() = OrderScheduleFormatter.displayText(scheduledAt)

    val contactDisplayText: String
        get() = whatsappNumber.trim().ifBlank { "Cliente escribirá por WhatsApp" }

    val newestReadyForDeliveryAt: Date?
        get() = readyForDeliveryItems.mapNotNull { it.readyForDeliveryAt }.maxByOrNull { it.time }

    val operationalReferenceDate: Date
        get() = newestReadyForDeliveryAt ?: readyForPaymentAt ?: updatedAt

    fun recalculatedStatus(): OrderStatus {
        if (status == OrderStatus.PAID) return OrderStatus.PAID
        if (status == OrderStatus.CANCELED) return OrderStatus.CANCELED
        if (status == OrderStatus.PENDING) return OrderStatus.PENDING

        val active = items.filter { it.status != OrderItemStatus.CANCELED }

        if (active.isEmpty()) return status

        if (active.all { it.status == OrderItemStatus.DELIVERED }) {
            return OrderStatus.READY_FOR_PAYMENT
        }

        if (active.any {
                it.status == OrderItemStatus.PREPARING ||
                        it.status == OrderItemStatus.READY_FOR_DELIVERY ||
                        it.status == OrderItemStatus.DELIVERED
            }
        ) {
            return OrderStatus.PREPARING
        }

        return OrderStatus.CONFIRMED
    }

    fun recalculatedAgendaStatus(): OrderStatus = recalculatedStatus()

    fun withUserId(uid: String): Order = copy(userId = uid.trim())

    fun withClientId(uid: String): Order = withUserId(uid)

    fun withTrustedPricing(
        trustedItems: List<OrderItem>,
        appliedRewards: List<AppliedReward>,
        discount: Double,
    ): Order {
        val normalized = normalizedItemLines(trustedItems)
        val trustedSubtotal = normalized.sumOf { it.totalPrice }.roundMoney()
        val safeDiscount = discount.coerceIn(0.0, trustedSubtotal).roundMoney()

        return copy(
            updatedAt = Date(),
            items = normalized,
            subtotal = trustedSubtotal,
            loyaltyDiscountAmount = safeDiscount,
            appliedRewards = appliedRewards,
            totalAmount = (trustedSubtotal - safeDiscount).coerceAtLeast(0.0).roundMoney(),
        )
    }

    fun withLoyalty(
        appliedRewards: List<AppliedReward>,
        discount: Double,
    ): Order {
        val safeDiscount = discount.coerceIn(0.0, subtotal).roundMoney()
        return copy(
            updatedAt = Date(),
            loyaltyDiscountAmount = safeDiscount,
            appliedRewards = appliedRewards,
            totalAmount = (subtotal - safeDiscount).coerceAtLeast(0.0).roundMoney(),
        )
    }

    fun confirming(now: Date = Date()): Order {
        if (status != OrderStatus.PENDING) return this
        return copy(
            status = OrderStatus.CONFIRMED,
            updatedAt = now,
            lastConfirmedRevision = revision,
        )
    }

    fun updatingItem(
        itemId: String,
        now: Date = Date(),
        transform: (OrderItem) -> OrderItem,
    ): Order {
        val updatedItems = items.map { item ->
            if (item.id == itemId) transform(item) else item
        }

        val draft = copy(
            items = updatedItems,
            updatedAt = now,
        )

        val nextStatus = draft.recalculatedStatus()
        val nextReadyForPaymentAt =
            if (nextStatus == OrderStatus.READY_FOR_PAYMENT && readyForPaymentAt == null) {
                now
            } else {
                readyForPaymentAt
            }

        return draft.copy(
            status = nextStatus,
            readyForPaymentAt = nextReadyForPaymentAt,
        )
    }

    fun updatingItems(
        newItems: List<OrderItem>,
        subtotal: Double,
        totalAmount: Double,
        now: Date = Date(),
    ): Order {
        val normalized = normalizedItemLines(newItems)
        val draft = copy(
            updatedAt = now,
            items = normalized,
            subtotal = subtotal.roundMoney(),
            totalAmount = totalAmount.roundMoney(),
            revision = revision + 1,
        )

        val nextStatus =
            if (draft.status == OrderStatus.PENDING) OrderStatus.PENDING else draft.recalculatedStatus()

        return draft.copy(status = nextStatus)
    }

    fun canceling(reason: String? = null, now: Date = Date()): Order {
        val canceledItems = items.map { item ->
            if (item.status == OrderItemStatus.CANCELED) {
                item
            } else {
                item.updatingStatus(
                    newStatus = OrderItemStatus.CANCELED,
                    now = now,
                    reason = reason,
                )
            }
        }

        return copy(
            status = OrderStatus.CANCELED,
            updatedAt = now,
            items = canceledItems,
        )
    }

    fun markingPaid(
        paymentMethod: String?,
        paymentReference: String?,
        paidByAdminId: String?,
        now: Date = Date(),
    ): Order {
        return copy(
            status = OrderStatus.PAID,
            updatedAt = now,
            paidAt = paidAt ?: now,
            paymentMethod = paymentMethod?.trim()?.takeIf { it.isNotEmpty() },
            paymentReference = paymentReference?.trim()?.takeIf { it.isNotEmpty() },
            paidByAdminId = paidByAdminId?.trim()?.takeIf { it.isNotEmpty() },
        )
    }

    companion object {
        fun normalizedItemLines(source: List<OrderItem>): List<OrderItem> {
            return source.flatMap { item ->
                val safeQuantity = item.quantity.coerceAtLeast(1)

                if (safeQuantity == 1) {
                    listOf(item.copy(quantity = 1))
                } else {
                    OrderItem.normalizedUnits(
                        sourceCartItemId = item.sourceCartItemId,
                        menuItemId = item.menuItemId,
                        name = item.name,
                        itemDescription = item.itemDescription,
                        unitPrice = item.unitPrice,
                        quantity = safeQuantity,
                        notes = item.notes,
                        createdAt = item.createdAt,
                    )
                }
            }
        }
    }
}

object OrderScheduleFormatter {
    const val LATER_THRESHOLD_MS: Long = 5 * 60 * 1000L

    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormatter = SimpleDateFormat("EEE d MMM, h:mm a", Locale("es", "EC"))

    fun dayKey(date: Date): String = dayFormatter.format(date)

    fun displayText(date: Date): String = displayFormatter.format(date)

    fun mode(createdAt: Date, scheduledAt: Date): OrderServiceMode =
        if (scheduledAt.time - createdAt.time > LATER_THRESHOLD_MS) {
            OrderServiceMode.SCHEDULED
        } else {
            OrderServiceMode.NOW
        }

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

private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0