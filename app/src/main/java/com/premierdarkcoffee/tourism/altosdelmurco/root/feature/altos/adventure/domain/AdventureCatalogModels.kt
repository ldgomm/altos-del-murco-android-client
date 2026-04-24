package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import java.util.Date
import java.util.Locale

private fun String.catalogEnumKey(): String =
    filter(Char::isLetterOrDigit).lowercase(Locale.US)

enum class AdventurePricingMode(val rawValue: String) {
    PER_HOUR_PER_VEHICLE("perHourPerVehicle"),
    PER_30_MIN_PER_PERSON("per30MinPerPerson"),
    PER_NIGHT_PER_PERSON("perNightPerPerson"),
    FIXED_PER_PERSON("fixedPerPerson");

    companion object {
        fun fromRaw(rawValue: String?): AdventurePricingMode? {
            val key = rawValue?.catalogEnumKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.catalogEnumKey() == key || it.name.catalogEnumKey() == key
            }
        }
    }
}

data class AdventureActivityDefaults(
    val durationMinutes: Int,
    val peopleCount: Int,
    val vehicleCount: Int,
    val offRoadRiderCount: Int,
    val nights: Int,
)

data class AdventureActivityCatalogItem(
    val id: String,
    val activityType: AdventureActivityType,
    val title: String,
    val systemImage: String,
    val shortDescription: String,
    val fullDescription: String,
    val includes: List<String>,
    val durationOptions: List<Int>,
    val pricingMode: AdventurePricingMode,
    val basePrice: Double,
    val discountAmount: Double,
    val currency: String,
    val defaults: AdventureActivityDefaults,
    val isActive: Boolean,
    val sortOrder: Int,
    val updatedAt: Date,
) {
    val finalUnitPrice: Double
        get() = AdventurePricingEngine.finalUnitPrice(this)

    val hasDiscount: Boolean
        get() = discountAmount > 0.0

    val defaultDraft: AdventureReservationItemDraft
        get() = AdventureReservationItemDraft(
            activity = activityType,
            durationMinutes = defaults.durationMinutes,
            peopleCount = defaults.peopleCount,
            vehicleCount = defaults.vehicleCount,
            offRoadRiderCount = defaults.offRoadRiderCount,
            nights = defaults.nights,
        )
}

data class AdventureFeaturedPackageFoodItem(
    val menuItemId: String,
    val quantity: Int,
) {
    val id: String get() = menuItemId

    init {
        require(menuItemId.trim().isNotEmpty()) { "menuItemId cannot be blank" }
    }
}

data class AdventureFeaturedPackage(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String?,
    val isActive: Boolean,
    val sortOrder: Int,
    val packageDiscountAmount: Double,
    val items: List<AdventureReservationItemDraft>,
    val foodItems: List<AdventureFeaturedPackageFoodItem>,
    val updatedAt: Date,
)

data class AdventureCatalogSnapshot(
    val activities: List<AdventureActivityCatalogItem>,
    val featuredPackages: List<AdventureFeaturedPackage>,
) {
    private val activitiesByType: Map<AdventureActivityType, AdventureActivityCatalogItem> =
        activities.associateBy { it.activityType }

    fun activity(activity: AdventureActivityType): AdventureActivityCatalogItem? =
        activitiesByType[activity]

    val activeActivitiesSorted: List<AdventureActivityCatalogItem>
        get() = activities
            .filter { it.isActive }
            .sortedWith(compareBy<AdventureActivityCatalogItem> { it.sortOrder }.thenBy { it.title })

    val activePackagesSorted: List<AdventureFeaturedPackage>
        get() = featuredPackages
            .filter { it.isActive }
            .sortedWith(compareBy<AdventureFeaturedPackage> { it.sortOrder }.thenBy { it.title })

    companion object {
        val EMPTY = AdventureCatalogSnapshot(
            activities = emptyList(),
            featuredPackages = emptyList(),
        )
    }
}
