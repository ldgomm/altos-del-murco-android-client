package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityDefaults
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackageFoodItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdventureCatalogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AdventureCatalogRepositoriable {

    companion object {
        private const val TAG = "AltosAdventureCatalog"
    }

    override suspend fun fetchCatalog(): AdventureCatalogSnapshot {
        val activitiesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_ACTIVITIES).get().awaitResult()

        val packagesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES).get()
                .awaitResult()

        return makeCatalogSnapshot(
            activitiesSnapshot = activitiesSnapshot,
            packagesSnapshot = packagesSnapshot,
        )
    }

    override fun observeCatalog(): Flow<AdventureCatalogSnapshot> = callbackFlow {
        var latestActivities: QuerySnapshot? = null
        var latestPackages: QuerySnapshot? = null

        fun emitIfReady() {
            val activities = latestActivities ?: return
            val packages = latestPackages ?: return

            runCatching {
                makeCatalogSnapshot(
                    activitiesSnapshot = activities,
                    packagesSnapshot = packages,
                )
            }.onSuccess { snapshot ->
                trySend(snapshot).isSuccess
            }.onFailure { error ->
                Log.e(TAG, "observeCatalog -> could not build catalog snapshot", error)
                close(error)
            }
        }

        val activitiesRegistration: ListenerRegistration =
            firestore.collection(FirestoreCollections.ADVENTURE_ACTIVITIES)
                .addSnapshotListener { snapshot, error ->
                    when {
                        error != null -> {
                            Log.e(TAG, "activities listener failed", error)
                            close(error)
                        }

                        snapshot != null -> {
                            Log.d(
                                TAG,
                                "activities listener -> size=${snapshot.size()} ids=${snapshot.documents.map { it.id }}"
                            )
                            latestActivities = snapshot
                            emitIfReady()
                        }
                    }
                }

        val packagesRegistration: ListenerRegistration =
            firestore.collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES)
                .addSnapshotListener { snapshot, error ->
                    when {
                        error != null -> {
                            Log.e(TAG, "packages listener failed", error)
                            close(error)
                        }

                        snapshot != null -> {
                            Log.d(
                                TAG,
                                "packages listener -> size=${snapshot.size()} ids=${snapshot.documents.map { it.id }}"
                            )
                            latestPackages = snapshot
                            emitIfReady()
                        }
                    }
                }

        awaitClose {
            activitiesRegistration.remove()
            packagesRegistration.remove()
        }
    }

    private fun makeCatalogSnapshot(
        activitiesSnapshot: QuerySnapshot,
        packagesSnapshot: QuerySnapshot,
    ): AdventureCatalogSnapshot {
        Log.d(
            TAG,
            "makeCatalogSnapshot -> rawActivities=${activitiesSnapshot.size()}, rawPackages=${packagesSnapshot.size()}"
        )

        val activities: List<AdventureActivityCatalogItem> =
            activitiesSnapshot.documents.mapNotNull { document ->
                document.toActivityCatalogItemOrNull()
            }
                .sortedWith(compareBy<AdventureActivityCatalogItem> { it.sortOrder }.thenBy { it.title })

        val activitiesByType = activities.associateBy { it.activityType }

        val packages: List<AdventureFeaturedPackage> =
            packagesSnapshot.documents.mapNotNull { document ->
                val packageModel = document.toFeaturedPackageOrNull() ?: return@mapNotNull null

                if (!packageModel.isActive) {
                    Log.d(TAG, "Skipping package ${document.id}: inactive")
                    return@mapNotNull null
                }

                val allItemsActive = packageModel.items.all { item ->
                    activitiesByType[item.activity]?.isActive == true
                }

                if (!allItemsActive) {
                    Log.w(
                        TAG,
                        "Skipping package ${document.id}: one or more package activities are missing/inactive. " + "items=${packageModel.items.map { it.activity.rawValue }}, " + "mappedActivities=${activitiesByType.keys.map { it.rawValue }}"
                    )
                    return@mapNotNull null
                }

                packageModel
            }.sortedWith(compareBy<AdventureFeaturedPackage> { it.sortOrder }.thenBy { it.title })

        Log.d(
            TAG,
            "makeCatalogSnapshot -> mappedActivities=${activities.size}, activeActivities=${activities.count { it.isActive }}, " + "mappedPackages=${packages.size}, activePackages=${packages.count { it.isActive }}"
        )

        return AdventureCatalogSnapshot(
            activities = activities,
            featuredPackages = packages,
        )
    }

    private fun DocumentSnapshot.toActivityCatalogItemOrNull(): AdventureActivityCatalogItem? {
        val rawId = stringValueOrNull("id") ?: stringValueOrNull("activity")
        ?: stringValueOrNull("activityType") ?: stringValueOrNull("activity_type") ?: id

        val activityType = AdventureActivityType.fromRaw(rawId)
        if (activityType == null) {
            Log.w(
                TAG,
                "Skipping activity document=$id: invalid id/activity='$rawId'. dataKeys=${data?.keys?.sorted()}"
            )
            return null
        }

        val rawPricingMode = stringValueOrNull("pricingMode") ?: stringValueOrNull("pricing_mode")
        ?: defaultPricingModeFor(activityType).rawValue

        val pricingMode = AdventurePricingMode.fromRaw(rawPricingMode)
        if (pricingMode == null) {
            Log.w(
                TAG,
                "Skipping activity document=$id: invalid pricingMode='$rawPricingMode'. dataKeys=${data?.keys?.sorted()}"
            )
            return null
        }

        val defaultsMap = mapValue("defaults")
        val defaults = AdventureActivityDefaults(
            durationMinutes = defaultsMap?.intValueOrNull("durationMinutes")
                ?: defaultsMap?.intValueOrNull("duration_minutes")
                ?: activityType.legacyDurationOptions.firstOrNull() ?: 0,
            peopleCount = defaultsMap?.intValueOrNull("peopleCount") ?: defaultsMap?.intValueOrNull(
                "people_count"
            ) ?: when (activityType) {
                AdventureActivityType.OFF_ROAD -> 0
                else -> 2
            },
            vehicleCount = defaultsMap?.intValueOrNull("vehicleCount")
                ?: defaultsMap?.intValueOrNull("vehicle_count")
                ?: if (activityType == AdventureActivityType.OFF_ROAD) 1 else 0,
            offRoadRiderCount = defaultsMap?.intValueOrNull("offRoadRiderCount")
                ?: defaultsMap?.intValueOrNull("off_road_rider_count")
                ?: if (activityType == AdventureActivityType.OFF_ROAD) 2 else 0,
            nights = defaultsMap?.intValueOrNull("nights")
                ?: if (activityType == AdventureActivityType.CAMPING) 1 else 0,
        )

        return AdventureActivityCatalogItem(
            id = rawId.ifBlank { activityType.rawValue },
            activityType = activityType,
            title = stringValueOrNull("title")?.takeIf { it.isNotBlank() }
                ?: activityType.legacyTitle,
            systemImage = stringValueOrNull("systemImage") ?: stringValueOrNull("system_image")
            ?: activityType.legacySystemImage,
            shortDescription = stringValueOrNull("shortDescription")
                ?: stringValueOrNull("short_description") ?: "",
            fullDescription = stringValueOrNull("fullDescription")
                ?: stringValueOrNull("full_description") ?: "",
            includes = stringListValue("includes"),
            durationOptions = intListValue("durationOptions").ifEmpty { intListValue("duration_options") }
                .ifEmpty { activityType.legacyDurationOptions },
            pricingMode = pricingMode,
            basePrice = doubleValueOrNull("basePrice") ?: doubleValueOrNull("base_price") ?: 0.0,
            discountAmount = doubleValueOrNull("discountAmount")
                ?: doubleValueOrNull("discount_amount") ?: 0.0,
            currency = stringValueOrNull("currency")?.takeIf { it.isNotBlank() } ?: "USD",
            defaults = defaults,
            isActive = boolValueOrNull("isActive") ?: boolValueOrNull("is_active")
            ?: boolValueOrNull("active") ?: true,
            sortOrder = intValueOrNull("sortOrder") ?: intValueOrNull("sort_order") ?: 0,
            updatedAt = dateValue("updatedAt") ?: dateValue("updated_at") ?: Date(),
        )
    }

    private fun DocumentSnapshot.toFeaturedPackageOrNull(): AdventureFeaturedPackage? {
        val rawItems = listMapValue("items")

        val items = rawItems.mapNotNull { raw ->
            val activityRaw =
                raw.stringValueOrNull("activity") ?: raw.stringValueOrNull("activityType")
                ?: raw.stringValueOrNull("activity_type")

            val activity = AdventureActivityType.fromRaw(activityRaw)
            if (activity == null) {
                Log.w(
                    "AltosAdventureCatalog",
                    "Skipping package item in package=$id: invalid activity='$activityRaw'"
                )
                return@mapNotNull null
            }

            AdventureReservationItemDraft(
                activity = activity,
                durationMinutes = raw.intValueOrNull("durationMinutes")
                    ?: raw.intValueOrNull("duration_minutes")
                    ?: activity.legacyDurationOptions.firstOrNull() ?: 0,
                peopleCount = raw.intValueOrNull("peopleCount")
                    ?: raw.intValueOrNull("people_count")
                    ?: if (activity == AdventureActivityType.OFF_ROAD) 0 else 2,
                vehicleCount = raw.intValueOrNull("vehicleCount")
                    ?: raw.intValueOrNull("vehicle_count")
                    ?: if (activity == AdventureActivityType.OFF_ROAD) 1 else 0,
                offRoadRiderCount = raw.intValueOrNull("offRoadRiderCount")
                    ?: raw.intValueOrNull("off_road_rider_count")
                    ?: if (activity == AdventureActivityType.OFF_ROAD) 2 else 0,
                nights = raw.intValueOrNull("nights")
                    ?: if (activity == AdventureActivityType.CAMPING) 1 else 0,
            )
        }

        if (items.size != rawItems.size) {
            Log.w(TAG, "Skipping package document=$id: some activity items could not be mapped")
            return null
        }

        val rawFoodItems = listMapValue("foodItems").ifEmpty { listMapValue("food_items") }
        val foodItems = rawFoodItems.mapNotNull { raw ->
            val menuItemId =
                raw.stringValueOrNull("menuItemId") ?: raw.stringValueOrNull("menu_item_id")
                ?: return@mapNotNull null

            AdventureFeaturedPackageFoodItem(
                menuItemId = menuItemId,
                quantity = raw.intValueOrNull("quantity")?.coerceAtLeast(1) ?: 1,
            )
        }

        if (foodItems.size != rawFoodItems.size) {
            Log.w(TAG, "Skipping package document=$id: some food items could not be mapped")
            return null
        }

        return AdventureFeaturedPackage(
            id = stringValueOrNull("id") ?: id,
            title = stringValueOrNull("title").orEmpty(),
            subtitle = stringValueOrNull("subtitle").orEmpty(),
            badge = stringValueOrNull("badge"),
            isActive = boolValueOrNull("isActive") ?: boolValueOrNull("is_active")
            ?: boolValueOrNull("active") ?: true,
            sortOrder = intValueOrNull("sortOrder") ?: intValueOrNull("sort_order") ?: 0,
            packageDiscountAmount = doubleValueOrNull("packageDiscountAmount") ?: doubleValueOrNull(
                "package_discount_amount"
            ) ?: 0.0,
            items = items,
            foodItems = foodItems,
            updatedAt = dateValue("updatedAt") ?: dateValue("updated_at") ?: Date(),
        )
    }

    private fun defaultPricingModeFor(activity: AdventureActivityType): AdventurePricingMode {
        return when (activity) {
            AdventureActivityType.OFF_ROAD -> AdventurePricingMode.PER_HOUR_PER_VEHICLE
            AdventureActivityType.PAINTBALL, AdventureActivityType.GO_KARTS, AdventureActivityType.SHOOTING_RANGE, AdventureActivityType.EXTREME_SLIDE -> AdventurePricingMode.PER_30_MIN_PER_PERSON

            AdventureActivityType.CAMPING -> AdventurePricingMode.PER_NIGHT_PER_PERSON
        }
    }

    private fun DocumentSnapshot.stringValueOrNull(field: String): String? =
        getString(field)?.trim()

    private fun DocumentSnapshot.boolValueOrNull(field: String): Boolean? = getBoolean(field)

    private fun DocumentSnapshot.intValueOrNull(field: String): Int? =
        when (val value = get(field)) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            is Number -> value.toInt()
            else -> null
        }

    private fun DocumentSnapshot.doubleValueOrNull(field: String): Double? =
        when (val value = get(field)) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }

    private fun DocumentSnapshot.dateValue(field: String): Date? = when (val value = get(field)) {
        is Timestamp -> value.toDate()
        is Date -> value
        else -> null
    }

    private fun DocumentSnapshot.mapValue(field: String): Map<*, *>? = get(field) as? Map<*, *>

    private fun DocumentSnapshot.stringListValue(field: String): List<String> =
        (get(field) as? List<*>).orEmpty().mapNotNull { it as? String }

    private fun DocumentSnapshot.intListValue(field: String): List<Int> =
        (get(field) as? List<*>).orEmpty().mapNotNull { value ->
            when (value) {
                is Int -> value
                is Long -> value.toInt()
                is Double -> value.toInt()
                is Number -> value.toInt()
                else -> null
            }
        }

    private fun DocumentSnapshot.listMapValue(field: String): List<Map<*, *>> =
        (get(field) as? List<*>).orEmpty().mapNotNull { it as? Map<*, *> }

    private fun Map<*, *>.stringValueOrNull(field: String): String? =
        (this[field] as? String)?.trim()

    private fun Map<*, *>.intValueOrNull(field: String): Int? = when (val value = this[field]) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        else -> null
    }
}