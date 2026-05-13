package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedRewardDto
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderScheduleFormatter
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderServiceMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus

data class OrderDto(
    val id: String = "",
    val userId: String = "",
    val clientName: String = "",
    val whatsappNumber: String? = null,
    val tableNumber: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val scheduledAt: Timestamp? = null,
    val scheduledDayKey: String? = null,
    val serviceMode: String? = null,
    val items: List<OrderItemDto> = emptyList(),
    val subtotal: Double = 0.0,
    val loyaltyDiscountAmount: Double? = 0.0,
    val appliedRewards: List<AppliedRewardDto>? = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = OrderStatus.PENDING.rawValue,
    val revision: Int? = 0,
    val lastConfirmedRevision: Int? = null,
    val readyForPaymentAt: Timestamp? = null,
    val paidAt: Timestamp? = null,
    val paymentMethod: String? = null,
    val paymentReference: String? = null,
    val paidByAdminId: String? = null,
) {
    constructor(domain: Order) : this(
        id = domain.id,
        userId = domain.userId,
        clientName = domain.clientName,
        whatsappNumber = if (domain.isScheduledForLater) {
            domain.whatsappNumber.trim().takeIf { it.isNotEmpty() }
        } else {
            ""
        },
        tableNumber = domain.tableNumber,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
        scheduledAt = Timestamp(domain.scheduledAt),
        scheduledDayKey = domain.scheduledDayKey,
        serviceMode = domain.serviceMode.rawValue,
        items = domain.items.map(::OrderItemDto),
        subtotal = domain.subtotal,
        loyaltyDiscountAmount = domain.loyaltyDiscountAmount,
        appliedRewards = domain.appliedRewards.map(::AppliedRewardDto),
        totalAmount = domain.totalAmount,
        status = domain.status.rawValue,
        revision = domain.revision,
        lastConfirmedRevision = domain.lastConfirmedRevision,
        readyForPaymentAt = domain.readyForPaymentAt?.let(::Timestamp),
        paidAt = domain.paidAt?.let(::Timestamp),
        paymentMethod = domain.paymentMethod,
        paymentReference = domain.paymentReference,
        paidByAdminId = domain.paidByAdminId,
    )

    fun toDomain(): Order {
        val safeCreatedAt = createdAt.toDate()
        val safeScheduledAt = scheduledAt?.toDate() ?: safeCreatedAt
        val safeUpdatedAt = updatedAt?.toDate() ?: safeCreatedAt
        val safeMode = serviceMode?.let(OrderServiceMode::fromRaw) ?: OrderScheduleFormatter.mode(
            safeCreatedAt,
            safeScheduledAt
        )

        return Order(
            id = id,
            userId = userId.trim(),
            clientName = clientName,
            whatsappNumber = if (safeMode == OrderServiceMode.SCHEDULED) {
                whatsappNumber.orEmpty().trim()
            } else {
                ""
            },
            tableNumber = tableNumber,
            createdAt = safeCreatedAt,
            updatedAt = safeUpdatedAt,
            scheduledAt = safeScheduledAt,
            scheduledDayKey = scheduledDayKey ?: OrderScheduleFormatter.dayKey(safeScheduledAt),
            serviceMode = safeMode,
            items = items.map { it.toDomain() },
            subtotal = subtotal,
            loyaltyDiscountAmount = (loyaltyDiscountAmount ?: 0.0).coerceAtLeast(0.0),
            appliedRewards = appliedRewards?.map { it.toDomain() } ?: emptyList(),
            totalAmount = totalAmount,
            status = OrderStatus.fromRaw(status),
            revision = revision ?: 0,
            lastConfirmedRevision = lastConfirmedRevision,
            readyForPaymentAt = readyForPaymentAt?.toDate(),
            paidAt = paidAt?.toDate(),
            paymentMethod = paymentMethod,
            paymentReference = paymentReference,
            paidByAdminId = paidByAdminId,
        )
    }
}
