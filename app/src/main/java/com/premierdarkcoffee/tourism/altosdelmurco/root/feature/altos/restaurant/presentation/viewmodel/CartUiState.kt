package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import kotlin.math.min

data class CartUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoading: Boolean = true,
    val isLoadingRewards: Boolean = false,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(
        RewardWalletSnapshot.empty("")
    ),
    val errorMessage: String? = null,
    val lastAddedItemName: String? = null,
) {
    val items: List<CartItem> get() = draft.items
    val totalItems: Int get() = draft.totalItems
    val subtotal: Double get() = draft.subtotal.roundMoney()
    val discount: Double get() = rewardPreview.totalDiscount.coerceIn(0.0, subtotal).roundMoney()
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0).roundMoney()
    val appliedRewards: List<AppliedReward> get() = rewardPreview.appliedRewards
    val isEmpty: Boolean get() = draft.isEmpty
    val canCheckout: Boolean get() = !draft.isEmpty

    fun allocatedDiscountByCartItemId(): Map<String, Double> {
        if (items.isEmpty() || appliedRewards.isEmpty()) return emptyMap()

        val allocations = items.associate { it.id to 0.0 }.toMutableMap()

        appliedRewards.forEach { reward ->
            val affectedMenuIds = reward.affectedMenuItemIds.toSet()
            if (affectedMenuIds.isEmpty() || reward.amount <= 0.0) return@forEach

            val candidates = items.filter { item ->
                item.menuItem.id in affectedMenuIds && item.totalPrice > 0.0
            }
            if (candidates.isEmpty()) return@forEach

            val availableTotal = candidates.sumOf { item ->
                (item.totalPrice - (allocations[item.id] ?: 0.0)).coerceAtLeast(0.0)
            }
            if (availableTotal <= 0.0) return@forEach

            var remainingRewardAmount = reward.amount.coerceAtLeast(0.0).roundMoney()

            candidates.forEachIndexed { index, item ->
                val alreadyAllocated = allocations[item.id] ?: 0.0
                val lineCapacity = (item.totalPrice - alreadyAllocated).coerceAtLeast(0.0)

                if (lineCapacity <= 0.0 || remainingRewardAmount <= 0.0) return@forEachIndexed

                val rawShare = if (index == candidates.lastIndex) {
                    remainingRewardAmount
                } else {
                    reward.amount * (lineCapacity / availableTotal)
                }

                val allocation = min(
                    lineCapacity,
                    rawShare.coerceAtMost(remainingRewardAmount),
                ).coerceAtLeast(0.0).roundMoney()

                allocations[item.id] = (alreadyAllocated + allocation).roundMoney()
                remainingRewardAmount =
                    (remainingRewardAmount - allocation).coerceAtLeast(0.0).roundMoney()
            }
        }

        return allocations
            .filterValues { it > 0.0 }
            .mapValues { (_, value) -> value.roundMoney() }
    }

    fun appliedRewardPresentations(menuItemId: String): List<RewardPresentation> = appliedRewards
        .filter { reward -> reward.affectedMenuItemIds.contains(menuItemId) }
        .map(RewardPresentation::fromAppliedReward)
}