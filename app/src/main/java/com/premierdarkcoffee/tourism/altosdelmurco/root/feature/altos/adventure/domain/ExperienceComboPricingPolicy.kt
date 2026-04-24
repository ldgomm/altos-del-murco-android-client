package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import kotlin.math.max

/**
 * Premium pricing policy for the redesigned Experience Builder.
 *
 * Goal:
 * - A package discount belongs only to the activities that still match the original featured package.
 * - Removing an activity can remove the combo discount, but must not remove individual activity discounts.
 * - Adding an extra activity must keep the valid package discount and price the extra activity separately.
 * - One activity alone is never a combo.
 */
data class ExperienceComboPricingBreakdown(
    val matchedPackageId: String?,
    val matchedPackageTitle: String?,
    val packageMatchedItems: List<AdventureReservationItemDraft>,
    val extraItems: List<AdventureReservationItemDraft>,
    val activityBaseSubtotal: Double,
    val activityDiscountAmount: Double,
    val activitySubtotalAfterIndividualDiscounts: Double,
    val foodSubtotal: Double,
    val comboDiscountAmount: Double,
    val loyaltyDiscountAmount: Double,
    val finalTotal: Double,
) {
    val hasValidCombo: Boolean get() = matchedPackageId != null && comboDiscountAmount > 0 && packageMatchedItems.size > 1
    val subtotalBeforeComboAndLoyalty: Double get() = activitySubtotalAfterIndividualDiscounts + foodSubtotal
    val totalSavings: Double get() = activityDiscountAmount + comboDiscountAmount + loyaltyDiscountAmount
}

object ExperienceComboPricingPolicy {

    fun calculate(
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        catalog: AdventureCatalogSnapshot,
        featuredPackages: List<AdventureFeaturedPackage> = catalog.activePackagesSorted,
        preferredPackageId: String? = null,
        loyaltyDiscountAmount: Double = 0.0,
    ): ExperienceComboPricingBreakdown {
        val normalizedItems = items.filter { catalog.activity(it.activity)?.isActive == true }
        val foodSubtotal = AdventurePricingEngine.foodSubtotal(foodReservation)
        val activityBaseSubtotal = normalizedItems.sumOf { item ->
            catalog.activity(item.activity)?.let { config ->
                AdventurePricingEngine.lineBaseSubtotal(item, config)
            } ?: 0.0
        }.adventureRoundMoney()
        val activitySubtotalAfterIndividualDiscounts = AdventurePricingEngine
            .estimatedSubtotal(normalizedItems, catalog)
            .adventureRoundMoney()
        val activityDiscountAmount =
            (activityBaseSubtotal - activitySubtotalAfterIndividualDiscounts)
                .coerceAtLeast(0.0)
                .adventureRoundMoney()

        val match = bestPackageMatch(
            selectedItems = normalizedItems,
            packages = featuredPackages,
            preferredPackageId = preferredPackageId,
        )

        val comboDiscount = when {
            normalizedItems.size <= 1 -> 0.0
            match == null -> 0.0
            else -> match.packageModel.packageDiscountAmount.coerceAtLeast(0.0)
        }.adventureRoundMoney()

        val subtotalBeforeLoyalty =
            (activitySubtotalAfterIndividualDiscounts + foodSubtotal - comboDiscount)
                .coerceAtLeast(0.0)
                .adventureRoundMoney()
        val safeLoyalty =
            loyaltyDiscountAmount.coerceIn(0.0, subtotalBeforeLoyalty).adventureRoundMoney()
        val finalTotal =
            (subtotalBeforeLoyalty - safeLoyalty).coerceAtLeast(0.0).adventureRoundMoney()

        return ExperienceComboPricingBreakdown(
            matchedPackageId = if (comboDiscount > 0) match?.packageModel?.id else null,
            matchedPackageTitle = if (comboDiscount > 0) match?.packageModel?.title else null,
            packageMatchedItems = match?.matchedItems.orEmpty(),
            extraItems = match?.extraItems ?: normalizedItems,
            activityBaseSubtotal = activityBaseSubtotal,
            activityDiscountAmount = activityDiscountAmount,
            activitySubtotalAfterIndividualDiscounts = activitySubtotalAfterIndividualDiscounts,
            foodSubtotal = foodSubtotal,
            comboDiscountAmount = comboDiscount,
            loyaltyDiscountAmount = safeLoyalty,
            finalTotal = finalTotal,
        )
    }

    private data class PackageMatch(
        val packageModel: AdventureFeaturedPackage,
        val matchedItems: List<AdventureReservationItemDraft>,
        val extraItems: List<AdventureReservationItemDraft>,
        val score: Int,
    )

    private fun bestPackageMatch(
        selectedItems: List<AdventureReservationItemDraft>,
        packages: List<AdventureFeaturedPackage>,
        preferredPackageId: String?,
    ): PackageMatch? {
        if (selectedItems.size <= 1) return null

        val matches = packages
            .filter { it.isActive && it.items.size > 1 }
            .mapNotNull { packageModel -> matchPackage(selectedItems, packageModel) }
            .filter { it.matchedItems.size == it.packageModel.items.size }

        if (matches.isEmpty()) return null

        val preferred =
            preferredPackageId?.let { id -> matches.firstOrNull { it.packageModel.id == id } }
        if (preferred != null) return preferred

        return matches.maxWithOrNull(
            compareBy<PackageMatch> { it.score }
                .thenBy { it.extraItems.size * -1 }
                .thenBy { it.packageModel.packageDiscountAmount },
        )
    }

    private fun matchPackage(
        selectedItems: List<AdventureReservationItemDraft>,
        packageModel: AdventureFeaturedPackage,
    ): PackageMatch? {
        val remaining = selectedItems.toMutableList()
        val matched = mutableListOf<AdventureReservationItemDraft>()
        var score = 0

        packageModel.items.forEach { packageItem ->
            val index = remaining.indexOfFirst { selected ->
                sameActivitySignature(selected, packageItem)
            }
            if (index < 0) return null
            val selected = remaining.removeAt(index)
            matched += selected
            score += 10
            score += max(0, 5 - durationDistance(selected, packageItem))
        }

        return PackageMatch(
            packageModel = packageModel,
            matchedItems = matched,
            extraItems = remaining,
            score = score,
        )
    }

    private fun sameActivitySignature(
        selected: AdventureReservationItemDraft,
        packageItem: AdventureReservationItemDraft,
    ): Boolean {
        if (selected.activity != packageItem.activity) return false
        return when (selected.activity) {
            AdventureActivityType.OFF_ROAD ->
                selected.durationMinutes == packageItem.durationMinutes &&
                        selected.vehicleCount >= packageItem.vehicleCount &&
                        selected.offRoadRiderCount >= packageItem.offRoadRiderCount

            AdventureActivityType.CAMPING ->
                selected.nights >= packageItem.nights && selected.peopleCount >= packageItem.peopleCount

            else ->
                selected.durationMinutes == packageItem.durationMinutes && selected.peopleCount >= packageItem.peopleCount
        }
    }

    private fun durationDistance(
        selected: AdventureReservationItemDraft,
        packageItem: AdventureReservationItemDraft,
    ): Int = kotlin.math.abs(selected.durationMinutes - packageItem.durationMinutes) / 30
}
