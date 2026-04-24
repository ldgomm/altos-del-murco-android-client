package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft

data class CheckoutUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoadingCart: Boolean = true,
    val isSubmitting: Boolean = false,
    val isLoadingRewards: Boolean = false,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(
        RewardWalletSnapshot.empty(
            ""
        )
    ),
    val errorMessage: String? = null,
) {
    val subtotal: Double get() = draft.subtotal
    val discount: Double get() = rewardPreview.totalDiscount.coerceAtLeast(0.0)
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0)
    val canSubmit: Boolean get() = draft.canSubmit && !isSubmitting
}
