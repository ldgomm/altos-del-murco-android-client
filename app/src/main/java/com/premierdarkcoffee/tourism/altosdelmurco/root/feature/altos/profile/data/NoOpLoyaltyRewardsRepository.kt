package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpLoyaltyRewardsRepository @Inject constructor() : LoyaltyRewardsRepository {

    override suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot {
        return RewardWalletSnapshot.empty(nationalId.trim())
    }

    override suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult {
        return RewardComputationResult.empty(RewardWalletSnapshot.empty(nationalId.trim()))
    }

    override suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult {
        return RewardComputationResult.empty(RewardWalletSnapshot.empty(nationalId.trim()))
    }

    override suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    ) = Unit

    override suspend fun consumeRewards(nationalId: String, referenceId: String) = Unit

    override suspend fun releaseRewards(nationalId: String, referenceId: String) = Unit
}
