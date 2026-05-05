package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

data class ProfileStats(
    val points: Int,
    val completedOrders: Int,
    val completedBookings: Int,
    val restaurantSpent: Double,
    val adventureSpent: Double,
    val totalSpent: Double,
    val level: LoyaltyLevel,
    val wallet: RewardWalletSnapshot,
) {
    companion object {
        val EMPTY = ProfileStats(
            points = 0,
            completedOrders = 0,
            completedBookings = 0,
            restaurantSpent = 0.0,
            adventureSpent = 0.0,
            totalSpent = 0.0,
            level = LoyaltyLevel.BRONZE,
            wallet = RewardWalletSnapshot.empty(userId = ""),
        )
    }
}
