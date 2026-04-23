package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import java.util.Date

data class Order(
    val id: String,
    val nationalId: String?,
    val clientName: String,
    val tableNumber: String,
    val createdAt: Date,
    val updatedAt: Date,
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

    fun withLoyalty(
        appliedRewards: List<AppliedReward>,
        discount: Double,
    ): Order = copy(
        loyaltyDiscountAmount = discount.coerceAtLeast(0.0),
        appliedRewards = appliedRewards,
        totalAmount = (subtotal - discount.coerceAtLeast(0.0)).coerceAtLeast(0.0),
    )
}
