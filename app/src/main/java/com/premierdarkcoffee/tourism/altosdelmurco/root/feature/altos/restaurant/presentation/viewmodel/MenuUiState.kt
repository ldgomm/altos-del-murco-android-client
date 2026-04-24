package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardTemplate
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection

data class MenuUiState(
    val isLoading: Boolean = true,
    val isLoadingRewards: Boolean = false,
    val sections: List<MenuSection> = emptyList(),
    val selectedCategoryId: String? = null,
    val walletSnapshot: RewardWalletSnapshot = RewardWalletSnapshot.empty(""),
    val errorMessage: String? = null,
) {
    val categories: List<MenuCategory>
        get() = sections.map { it.category }

    val featuredItems: List<MenuItem>
        get() = sections
            .flatMap { it.items }
            .filter { it.isFeatured }

    val visibleSections: List<MenuSection>
        get() = if (selectedCategoryId.isNullOrBlank()) {
            sections
        } else {
            sections.filter { it.category.id == selectedCategoryId }
        }

    val allItems: List<MenuItem>
        get() = sections.flatMap { it.items }

    val restaurantRewardTemplates: List<LoyaltyRewardTemplate>
        get() = walletSnapshot.availableTemplates
            .filter { it.scope.matchesRestaurant() && !it.isExpired }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

    fun itemById(id: String): MenuItem? = allItems.firstOrNull { it.id == id }
}
