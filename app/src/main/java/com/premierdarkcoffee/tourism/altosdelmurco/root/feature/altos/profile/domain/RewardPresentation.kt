package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText

data class RewardPresentation(
    val id: String,
    val badge: String,
    val title: String,
    val message: String,
    val amountText: String? = null,
) {
    companion object {
        fun fromAppliedReward(reward: AppliedReward): RewardPresentation {
            val lowered = reward.note.lowercase()
            val badge = when {
                lowered.contains("gratis") -> "Gratis"
                lowered.contains("%") -> "Descuento"
                else -> "Premio"
            }

            return RewardPresentation(
                id = reward.id,
                badge = badge,
                title = reward.title,
                message = reward.note,
                amountText = reward.amount.priceText(),
            )
        }
    }
}
