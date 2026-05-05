package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoyaltyRewardsRepositoriable {
    suspend fun loadWalletSnapshot(userId: String): RewardWalletSnapshot

    fun observeWalletSnapshot(userId: String): Flow<RewardWalletSnapshot> = flow {
        emit(loadWalletSnapshot(userId))
    }

    suspend fun previewRestaurantRewards(
        userId: String,
        items: List<OrderItem>,
    ): RewardComputationResult

    suspend fun previewAdventureRewards(
        userId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult

    suspend fun reserveRewards(
        userId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    )

    suspend fun consumeRewards(
        userId: String,
        referenceId: String,
    )

    suspend fun releaseRewards(
        userId: String,
        referenceId: String,
    )
}
