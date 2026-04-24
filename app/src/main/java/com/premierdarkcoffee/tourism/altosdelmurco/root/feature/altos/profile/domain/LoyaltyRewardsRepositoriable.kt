package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoyaltyRewardsRepositoriable {
    suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot

    fun observeWalletSnapshot(nationalId: String): Flow<RewardWalletSnapshot> = flow {
        emit(loadWalletSnapshot(nationalId))
    }

    suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult

    suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult

    suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    )

    suspend fun consumeRewards(
        nationalId: String,
        referenceId: String,
    )

    suspend fun releaseRewards(
        nationalId: String,
        referenceId: String,
    )
}