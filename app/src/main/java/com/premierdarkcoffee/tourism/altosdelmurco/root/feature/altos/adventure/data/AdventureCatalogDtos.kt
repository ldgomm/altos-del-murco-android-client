package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityDefaults
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackageFoodItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft

data class AdventureActivityDefaultsDto(
    val durationMinutes: Int = 0,
    val peopleCount: Int = 0,
    val vehicleCount: Int = 0,
    val offRoadRiderCount: Int = 0,
    val nights: Int = 0,
) {
    fun toDomain(): AdventureActivityDefaults = AdventureActivityDefaults(
        durationMinutes = durationMinutes,
        peopleCount = peopleCount,
        vehicleCount = vehicleCount,
        offRoadRiderCount = offRoadRiderCount,
        nights = nights,
    )
}

data class AdventureActivityCatalogDto(
    val id: String = "",
    val title: String = "",
    val systemImage: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val includes: List<String> = emptyList(),
    val durationOptions: List<Int> = emptyList(),
    val pricingMode: String = "",
    val basePrice: Double = 0.0,
    val discountAmount: Double = 0.0,
    val currency: String = "USD",
    val defaults: AdventureActivityDefaultsDto = AdventureActivityDefaultsDto(),
    val isActive: Boolean = false,
    val sortOrder: Int = 0,
    val updatedAt: Timestamp = Timestamp.now(),
) {
    fun toDomain(): AdventureActivityCatalogItem? {
        val activityType = AdventureActivityType.fromRaw(id) ?: return null
        val parsedPricingMode = AdventurePricingMode.fromRaw(pricingMode) ?: return null

        return AdventureActivityCatalogItem(
            id = id.ifBlank { activityType.rawValue },
            activityType = activityType,
            title = title.ifBlank { activityType.legacyTitle },
            systemImage = systemImage.ifBlank { activityType.legacySystemImage },
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            includes = includes,
            durationOptions = durationOptions,
            pricingMode = parsedPricingMode,
            basePrice = basePrice,
            discountAmount = discountAmount,
            currency = currency.ifBlank { "USD" },
            defaults = defaults.toDomain(),
            isActive = isActive,
            sortOrder = sortOrder,
            updatedAt = updatedAt.toDate(),
        )
    }
}

data class AdventureFeaturedPackageItemDto(
    val activity: String = "",
    val durationMinutes: Int = 0,
    val peopleCount: Int = 0,
    val vehicleCount: Int = 0,
    val offRoadRiderCount: Int = 0,
    val nights: Int = 0,
) {
    constructor(item: AdventureReservationItemDraft) : this(
        activity = item.activity.rawValue,
        durationMinutes = item.durationMinutes,
        peopleCount = item.peopleCount,
        vehicleCount = item.vehicleCount,
        offRoadRiderCount = item.offRoadRiderCount,
        nights = item.nights,
    )

    fun toDomain(): AdventureReservationItemDraft? {
        val activityType = AdventureActivityType.fromRaw(activity) ?: return null
        return AdventureReservationItemDraft(
            activity = activityType,
            durationMinutes = durationMinutes,
            peopleCount = peopleCount,
            vehicleCount = vehicleCount,
            offRoadRiderCount = offRoadRiderCount,
            nights = nights,
        )
    }
}

data class AdventureFeaturedPackageFoodItemDto(
    val menuItemId: String = "",
    val quantity: Int = 1,
) {
    fun toDomain(): AdventureFeaturedPackageFoodItem? {
        val cleanId = menuItemId.trim()
        if (cleanId.isEmpty()) return null
        return AdventureFeaturedPackageFoodItem(
            menuItemId = cleanId,
            quantity = quantity.coerceAtLeast(1),
        )
    }
}

data class AdventureFeaturedPackageDto(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val badge: String? = null,
    val isActive: Boolean = false,
    val sortOrder: Int = 0,
    val packageDiscountAmount: Double = 0.0,
    val items: List<AdventureFeaturedPackageItemDto> = emptyList(),
    val foodItems: List<AdventureFeaturedPackageFoodItemDto> = emptyList(),
    val updatedAt: Timestamp = Timestamp.now(),
) {
    fun toDomain(): AdventureFeaturedPackage? {
        val mappedItems = items.mapNotNull { it.toDomain() }
        if (mappedItems.size != items.size) return null

        val mappedFoodItems = foodItems.mapNotNull { it.toDomain() }
        if (mappedFoodItems.size != foodItems.size) return null

        return AdventureFeaturedPackage(
            id = id,
            title = title,
            subtitle = subtitle,
            badge = badge,
            isActive = isActive,
            sortOrder = sortOrder,
            packageDiscountAmount = packageDiscountAmount.coerceAtLeast(0.0),
            items = mappedItems,
            foodItems = mappedFoodItems,
            updatedAt = updatedAt.toDate(),
        )
    }
}
