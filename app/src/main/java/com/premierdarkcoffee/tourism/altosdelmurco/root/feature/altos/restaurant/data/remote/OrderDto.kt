package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedRewardDto
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderScheduleFormatter
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderServiceMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import kotlin.collections.map


data class OrderDto(
    val id: String = "",
    val nationalId: String? = null,
    val clientName: String = "",
    val tableNumber: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val scheduledAt: Timestamp? = null,
    val scheduledDayKey: String? = null,
    val serviceMode: String? = null,
    val items: List<OrderItemDto> = emptyList(),
    val subtotal: Double = 0.0,
    val loyaltyDiscountAmount: Double? = null,
    val appliedRewards: List<AppliedRewardDto>? = null,
    val totalAmount: Double = 0.0,
    val status: String? = null,
    val revision: Int? = null,
    val lastConfirmedRevision: Int? = null,
) {
    constructor(domain: Order) : this(
        id = domain.id,
        nationalId = domain.nationalId,
        clientName = domain.clientName,
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
        status = domain.status.name.lowercase(),
        revision = domain.revision,
        lastConfirmedRevision = domain.lastConfirmedRevision,
    )

    fun toDomain(): Order {
        val safeCreatedAt = createdAt.toDate()
        val safeScheduledAt = scheduledAt?.toDate() ?: safeCreatedAt
        return Order(
            id = id,
            nationalId = nationalId,
            clientName = clientName,
            tableNumber = tableNumber,
            createdAt = safeCreatedAt,
            updatedAt = updatedAt?.toDate() ?: safeCreatedAt,
            scheduledAt = safeScheduledAt,
            scheduledDayKey = scheduledDayKey ?: OrderScheduleFormatter.dayKey(safeScheduledAt),
            serviceMode = serviceMode?.let(OrderServiceMode::fromRaw)
                ?: OrderScheduleFormatter.mode(safeCreatedAt, safeScheduledAt),
            items = items.map { it.toDomain() },
            subtotal = subtotal,
            loyaltyDiscountAmount = (loyaltyDiscountAmount ?: 0.0).coerceAtLeast(0.0),
            appliedRewards = appliedRewards?.map { it.toDomain() } ?: emptyList(),
            totalAmount = totalAmount,
            status = OrderStatus.fromRaw(status),
            revision = revision ?: 1,
            lastConfirmedRevision = lastConfirmedRevision,
        )
    }
}
