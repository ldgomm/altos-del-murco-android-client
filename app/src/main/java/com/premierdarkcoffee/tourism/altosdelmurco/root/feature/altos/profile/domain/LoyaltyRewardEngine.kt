package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import java.util.UUID
import kotlin.math.round

object LoyaltyRewardEngine {

    fun evaluateRestaurant(
        templates: List<LoyaltyRewardTemplate>,
        wallet: RewardWalletSnapshot,
        menuLines: List<RewardMenuLine>,
    ): RewardComputationResult {
        val eligible = templates
            .filter {
                it.isActive &&
                        it.triggerMode == LoyaltyRewardTriggerMode.AUTOMATIC &&
                        it.scope.matchesRestaurant() &&
                        it.isEligible(wallet.currentLevel) &&
                        !it.isExpired
            }

        val stackable = eligible
            .filter { it.canStack }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        val exclusive = eligible
            .filterNot { it.canStack }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        val stackableResult = applyRestaurantTemplates(stackable, menuLines)
        val bestExclusive = exclusive
            .map { applyRestaurantTemplates(listOf(it), menuLines) }
            .maxByOrNull { it.totalDiscount }

        val winner = if ((bestExclusive?.totalDiscount ?: 0.0) > stackableResult.totalDiscount) {
            bestExclusive!!
        } else {
            stackableResult
        }

        return RewardComputationResult(
            appliedRewards = winner.appliedRewards,
            totalDiscount = roundMoney(winner.totalDiscount),
            walletSnapshot = wallet,
        )
    }

    fun evaluateAdventure(
        templates: List<LoyaltyRewardTemplate>,
        wallet: RewardWalletSnapshot,
        activityLines: List<RewardActivityLine>,
        foodLines: List<RewardMenuLine>,
    ): RewardComputationResult {
        val eligible = templates
            .filter {
                it.isActive &&
                        it.triggerMode == LoyaltyRewardTriggerMode.AUTOMATIC &&
                        it.isEligible(wallet.currentLevel) &&
                        !it.isExpired &&
                        templateAppliesInAdventureContext(it)
            }

        val stackable = eligible
            .filter { it.canStack }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        val exclusive = eligible
            .filterNot { it.canStack }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        val stackableResult = applyAdventureTemplates(stackable, activityLines, foodLines)
        val bestExclusive = exclusive
            .map { applyAdventureTemplates(listOf(it), activityLines, foodLines) }
            .maxByOrNull { it.totalDiscount }

        val winner = if ((bestExclusive?.totalDiscount ?: 0.0) > stackableResult.totalDiscount) {
            bestExclusive!!
        } else {
            stackableResult
        }

        return RewardComputationResult(
            appliedRewards = winner.appliedRewards,
            totalDiscount = roundMoney(winner.totalDiscount),
            walletSnapshot = wallet,
        )
    }

    private fun templateAppliesInAdventureContext(template: LoyaltyRewardTemplate): Boolean {
        return when (template.rule.type) {
            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> template.scope.matchesAdventure()
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
            LoyaltyRewardRuleType.FREE_MENU_ITEM,
            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
                -> true
        }
    }

    private fun applyRestaurantTemplates(
        templates: List<LoyaltyRewardTemplate>,
        menuLines: List<RewardMenuLine>,
    ): InternalRewardResult {
        val workingLines = menuLines.map {
            MutableMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                remainingRewardableUnits = it.quantity.coerceAtLeast(0),
            )
        }.toMutableList()

        val appliedRewards = mutableListOf<AppliedReward>()
        var totalDiscount = 0.0

        templates.forEach { template ->
            val reward = applyRestaurantTemplate(template, workingLines) ?: return@forEach
            appliedRewards.add(reward)
            totalDiscount += reward.amount
        }

        return InternalRewardResult(
            appliedRewards = appliedRewards,
            totalDiscount = roundMoney(totalDiscount),
        )
    }

    private fun applyAdventureTemplates(
        templates: List<LoyaltyRewardTemplate>,
        activityLines: List<RewardActivityLine>,
        foodLines: List<RewardMenuLine>,
    ): InternalRewardResult {
        val workingActivities = activityLines.map {
            MutableActivityLine(
                activityId = it.activityId,
                title = it.title,
                remainingRewardableAmount = it.linePrice.coerceAtLeast(0.0),
            )
        }.toMutableList()

        val workingFood = foodLines.map {
            MutableMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                remainingRewardableUnits = it.quantity.coerceAtLeast(0),
            )
        }.toMutableList()

        val appliedRewards = mutableListOf<AppliedReward>()
        var totalDiscount = 0.0

        templates.forEach { template ->
            val reward = when (template.rule.type) {
                LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> applyActivityTemplate(
                    template,
                    workingActivities
                )

                LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
                LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
                LoyaltyRewardRuleType.FREE_MENU_ITEM,
                LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
                    -> applyRestaurantTemplate(template, workingFood)
            } ?: return@forEach

            appliedRewards.add(reward)
            totalDiscount += reward.amount
        }

        return InternalRewardResult(
            appliedRewards = appliedRewards,
            totalDiscount = roundMoney(totalDiscount),
        )
    }

    private fun applyRestaurantTemplate(
        template: LoyaltyRewardTemplate,
        lines: MutableList<MutableMenuLine>,
    ): AppliedReward? {
        return when (template.rule.type) {
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> {
                val percentage = template.rule.percentage ?: return null
                val index = lines.indices
                    .filter { lines[it].remainingRewardableUnits > 0 }
                    .maxByOrNull { lines[it].unitPrice }
                    ?: return null

                val line = lines[index]
                val amount = roundMoney(line.unitPrice * (percentage / 100.0))
                if (amount <= 0.0) return null

                lines[index] = line.copy(
                    remainingRewardableUnits = line.remainingRewardableUnits - 1,
                )

                AppliedReward(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    title = template.title,
                    amount = amount,
                    note = "${percentage.toInt()}% en ${line.name}",
                    affectedMenuItemIds = listOf(line.menuItemId),
                    affectedActivityIds = emptyList(),
                )
            }

            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE -> {
                val percentage = template.rule.percentage ?: return null
                val targetId = template.targetMenuItemId ?: return null
                val quantity = (template.rule.quantity ?: 1).coerceAtLeast(1)

                val index = lines.indices.firstOrNull {
                    lines[it].menuItemId == targetId && lines[it].remainingRewardableUnits > 0
                } ?: return null

                val line = lines[index]
                val units = minOf(quantity, line.remainingRewardableUnits)
                val amount = roundMoney(line.unitPrice * units * (percentage / 100.0))
                if (amount <= 0.0) return null

                lines[index] = line.copy(
                    remainingRewardableUnits = line.remainingRewardableUnits - units,
                )

                AppliedReward(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    title = template.title,
                    amount = amount,
                    note = "${percentage.toInt()}% en ${line.name}",
                    affectedMenuItemIds = listOf(line.menuItemId),
                    affectedActivityIds = emptyList(),
                )
            }

            LoyaltyRewardRuleType.FREE_MENU_ITEM -> {
                val targetId = template.targetMenuItemId ?: return null
                val quantity = (template.rule.quantity ?: 1).coerceAtLeast(1)

                val index = lines.indices.firstOrNull {
                    lines[it].menuItemId == targetId && lines[it].remainingRewardableUnits > 0
                } ?: return null

                val line = lines[index]
                val units = minOf(quantity, line.remainingRewardableUnits)
                val amount = roundMoney(line.unitPrice * units)
                if (amount <= 0.0) return null

                lines[index] = line.copy(
                    remainingRewardableUnits = line.remainingRewardableUnits - units,
                )

                AppliedReward(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    title = template.title,
                    amount = amount,
                    note = "${units}x ${line.name} gratis",
                    affectedMenuItemIds = listOf(line.menuItemId),
                    affectedActivityIds = emptyList(),
                )
            }

            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> {
                val targetId = template.targetMenuItemId ?: return null
                val buyQuantity = (template.rule.buyQuantity ?: 1).coerceAtLeast(1)
                val freeQuantity = (template.rule.freeQuantity ?: 1).coerceAtLeast(1)
                val repeatable = template.rule.repeatable ?: true

                val index =
                    lines.indices.firstOrNull { lines[it].menuItemId == targetId } ?: return null
                val line = lines[index]
                val totalUnits = line.remainingRewardableUnits
                if (totalUnits < buyQuantity) return null

                val freeUnits = if (repeatable) {
                    minOf(totalUnits, (totalUnits / buyQuantity) * freeQuantity)
                } else {
                    if (totalUnits >= buyQuantity) minOf(totalUnits, freeQuantity) else 0
                }
                if (freeUnits <= 0) return null

                val amount = roundMoney(line.unitPrice * freeUnits)
                if (amount <= 0.0) return null

                lines[index] = line.copy(
                    remainingRewardableUnits = (line.remainingRewardableUnits - freeUnits).coerceAtLeast(
                        0
                    ),
                )

                AppliedReward(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    title = template.title,
                    amount = amount,
                    note = "Compra $buyQuantity y recibe $freeUnits gratis en ${line.name}",
                    affectedMenuItemIds = listOf(line.menuItemId),
                    affectedActivityIds = emptyList(),
                )
            }

            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> null
        }
    }

    private fun applyActivityTemplate(
        template: LoyaltyRewardTemplate,
        lines: MutableList<MutableActivityLine>,
    ): AppliedReward? {
        if (template.rule.type != LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE) return null

        val percentage = template.rule.percentage ?: return null
        val targetId = template.targetActivityId ?: return null

        val index = lines.indices.firstOrNull {
            lines[it].activityId == targetId && lines[it].remainingRewardableAmount > 0.0
        } ?: return null

        val line = lines[index]
        val amount = roundMoney(line.remainingRewardableAmount * (percentage / 100.0))
        if (amount <= 0.0) return null

        lines[index] = line.copy(remainingRewardableAmount = 0.0)

        return AppliedReward(
            id = UUID.randomUUID().toString(),
            templateId = template.id,
            title = template.title,
            amount = amount,
            note = "${percentage.toInt()}% en ${line.title}",
            affectedMenuItemIds = emptyList(),
            affectedActivityIds = listOf(targetId),
        )
    }

    private data class MutableMenuLine(
        val menuItemId: String,
        val name: String,
        val unitPrice: Double,
        val remainingRewardableUnits: Int,
    )

    private data class MutableActivityLine(
        val activityId: String,
        val title: String,
        val remainingRewardableAmount: Double,
    )

    private data class InternalRewardResult(
        val appliedRewards: List<AppliedReward>,
        val totalDiscount: Double,
    )

    private fun roundMoney(value: Double): Double = round(value * 100.0) / 100.0
}
