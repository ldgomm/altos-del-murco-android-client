package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
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

object RewardPresentationFactory {
    fun menuPresentation(
        item: MenuItem,
        wallet: RewardWalletSnapshot,
    ): RewardPresentation? = buildMenuPresentation(
        item = item,
        wallet = wallet,
        includeAdventureScopedTemplates = false,
    )

    fun adventureMenuPresentation(
        item: MenuItem,
        wallet: RewardWalletSnapshot,
    ): RewardPresentation? = buildMenuPresentation(
        item = item,
        wallet = wallet,
        includeAdventureScopedTemplates = true,
    )

    fun activityPresentation(
        activity: AdventureActivityCatalogItem,
        wallet: RewardWalletSnapshot,
    ): RewardPresentation? {
        wallet.availableTemplates
            .filter { it.scope.matchesAdventure() && !it.isExpired }
            .forEach { template ->
                if (template.rule.type == LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE &&
                    template.targetActivityId == activity.id
                ) {
                    val percentage = (template.rule.percentage ?: 0.0).toInt()
                    if (percentage > 0) {
                        return RewardPresentation(
                            id = template.id,
                            badge = "$percentage% OFF",
                            title = template.title,
                            message = "${activity.title} tiene $percentage% de descuento automático por tu nivel ${wallet.currentLevel.title}.",
                        )
                    }
                }
            }
        return null
    }

    fun packagePresentation(
        packageModel: AdventureFeaturedPackage,
        catalog: AdventureCatalogSnapshot,
        menuItemsById: Map<String, MenuItem>,
        wallet: RewardWalletSnapshot,
    ): RewardPresentation? {
        packageModel.items.forEach { item ->
            val activity = catalog.activity(item.activity) ?: return@forEach
            activityPresentation(activity, wallet)?.let { return it }
        }

        packageModel.foodItems.forEach { foodItem ->
            val menuItem = menuItemsById[foodItem.menuItemId] ?: return@forEach
            adventureMenuPresentation(menuItem, wallet)?.let { return it }
        }

        return null
    }

    private fun buildMenuPresentation(
        item: MenuItem,
        wallet: RewardWalletSnapshot,
        includeAdventureScopedTemplates: Boolean,
    ): RewardPresentation? {
        val templates = wallet.availableTemplates.filter {
            templateMatchesMenuContext(it, includeAdventureScopedTemplates) && !it.isExpired
        }

        templates.forEach { template ->
            when (template.rule.type) {
                LoyaltyRewardRuleType.FREE_MENU_ITEM -> {
                    if (template.targetMenuItemId == item.id) {
                        return RewardPresentation(
                            id = template.id,
                            badge = "Gratis",
                            title = template.title,
                            message = "${item.name} puede salir gratis por tu nivel ${wallet.currentLevel.title}.",
                        )
                    }
                }

                LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE -> {
                    if (template.targetMenuItemId == item.id) {
                        val percentage = (template.rule.percentage ?: 0.0).toInt()
                        if (percentage > 0) {
                            return RewardPresentation(
                                id = template.id,
                                badge = "$percentage% OFF",
                                title = template.title,
                                message = "${item.name} tiene $percentage% de descuento por tu nivel ${wallet.currentLevel.title}.",
                            )
                        }
                    }
                }

                LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> {
                    if (template.targetMenuItemId == item.id) {
                        val buyQty = (template.rule.buyQuantity ?: 1).coerceAtLeast(1)
                        val freeQty = (template.rule.freeQuantity ?: 1).coerceAtLeast(1)
                        return RewardPresentation(
                            id = template.id,
                            badge = "Promo",
                            title = template.title,
                            message = "Compra $buyQty y recibe $freeQty gratis en ${item.name}.",
                        )
                    }
                }

                LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
                LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE,
                    -> Unit
            }
        }

        return null
    }

    private fun templateMatchesMenuContext(
        template: LoyaltyRewardTemplate,
        includeAdventureScopedTemplates: Boolean,
    ): Boolean = when (template.scope) {
        LoyaltyRewardScope.RESTAURANT -> true
        LoyaltyRewardScope.ADVENTURE -> includeAdventureScopedTemplates
        LoyaltyRewardScope.BOTH -> true
    }
}
