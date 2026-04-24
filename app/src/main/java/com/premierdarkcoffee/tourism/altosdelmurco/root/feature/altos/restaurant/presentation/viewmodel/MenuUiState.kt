package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardScope
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

    val allItems: List<MenuItem>
        get() = sections.flatMap { it.items }.distinctBy { it.id }

    val featuredItems: List<MenuItem>
        get() = allItems.filter { it.isFeatured }
            .sortedWith(compareBy<MenuItem> { it.sortOrder }.thenBy { it.name })

    val visibleSections: List<MenuSection>
        get() = if (selectedCategoryId.isNullOrBlank()) {
            sections
        } else {
            sections.filter { it.category.id == selectedCategoryId }
        }

    val restaurantRewardTemplates: List<LoyaltyRewardTemplate>
        get() = walletSnapshot.availableTemplates.filter { template ->
            template.scope == LoyaltyRewardScope.RESTAURANT || template.scope == LoyaltyRewardScope.BOTH
        }.filterNot { it.isExpired }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

    fun itemById(id: String): MenuItem? = allItems.firstOrNull { it.id == id }
}
