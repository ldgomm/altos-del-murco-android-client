# app/src/androidTest/java/com/premierdarkcoffee/tourism/altosdelmurco/ExampleInstrumentedTest.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco




/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.premierdarkcoffee.tourism.altosdelmurco", appContext.packageName)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/AltosApplication.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco


@HiltAndroidApp
class AltosApplication : Application()

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/MainActivity.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseApp.initializeApp(this)
            AltosApp()
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/data/AdventureBookingDto.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data


data class AdventureReservationItemDraftDto(
    val id: String = "",
    val activity: String = "",
    val durationMinutes: Int = 0,
    val peopleCount: Int = 0,
    val vehicleCount: Int = 0,
    val offRoadRiderCount: Int = 0,
    val nights: Int = 0,
) {
    constructor(item: AdventureReservationItemDraft) : this(
        id = item.id,
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
            id = id.ifBlank { UUID.randomUUID().toString() },
            activity = activityType,
            durationMinutes = durationMinutes,
            peopleCount = peopleCount,
            vehicleCount = vehicleCount,
            offRoadRiderCount = offRoadRiderCount,
            nights = nights,
        )
    }
}

data class ReservationFoodItemDraftDto(
    val id: String = "",
    val menuItemId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val notes: String? = null,
) {
    constructor(item: ReservationFoodItemDraft) : this(
        id = item.id,
        menuItemId = item.menuItemId,
        name = item.name,
        unitPrice = item.unitPrice,
        quantity = item.quantity,
        notes = item.notes,
    )

    fun toDomain(): ReservationFoodItemDraft = ReservationFoodItemDraft(
        id = id.ifBlank { UUID.randomUUID().toString() },
        menuItemId = menuItemId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity.coerceAtLeast(1),
        notes = notes,
    )
}

data class ReservationFoodDraftDto(
    val items: List<ReservationFoodItemDraftDto> = emptyList(),
    val servingMoment: String = ReservationServingMoment.AFTER_ACTIVITIES.rawValue,
    val servingTime: Timestamp? = null,
    val notes: String? = null,
) {
    constructor(food: ReservationFoodDraft) : this(
        items = food.items.map(::ReservationFoodItemDraftDto),
        servingMoment = food.servingMoment.rawValue,
        servingTime = food.servingTime?.let(::Timestamp),
        notes = food.notes,
    )

    fun toDomain(): ReservationFoodDraft = ReservationFoodDraft(
        items = items.map { it.toDomain() },
        servingMoment = ReservationServingMoment.fromRaw(servingMoment),
        servingTime = servingTime?.toDate(),
        notes = notes,
    )
}

data class AdventureBookingBlockDto(
    val id: String = "",
    val title: String = "",
    val activity: String = "",
    val resourceType: String = "",
    val startAt: Timestamp = Timestamp.now(),
    val endAt: Timestamp = Timestamp.now(),
    val reservedUnits: Int = 0,
    val subtotal: Double = 0.0,
) {
    constructor(block: AdventureBookingBlock) : this(
        id = block.id,
        title = block.title,
        activity = block.activity.rawValue,
        resourceType = block.resourceType.rawValue,
        startAt = Timestamp(block.startAt),
        endAt = Timestamp(block.endAt),
        reservedUnits = block.reservedUnits,
        subtotal = block.subtotal,
    )

    fun toDomain(): AdventureBookingBlock? {
        val activityType = AdventureActivityType.fromRaw(activity) ?: return null
        val resource = AdventureResourceType.fromRaw(resourceType) ?: return null
        return AdventureBookingBlock(
            id = id.ifBlank { UUID.randomUUID().toString() },
            title = title,
            activity = activityType,
            resourceType = resource,
            startAt = startAt.toDate(),
            endAt = endAt.toDate(),
            reservedUnits = reservedUnits,
            subtotal = subtotal,
        )
    }
}

data class AdventureAppliedRewardDto(
    val id: String = "",
    val templateId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val affectedMenuItemIds: List<String> = emptyList(),
    val affectedActivityIds: List<String> = emptyList(),
) {
    constructor(domain: AppliedReward) : this(
        id = domain.id,
        templateId = domain.templateId,
        title = domain.title,
        amount = domain.amount,
        note = domain.note,
        affectedMenuItemIds = domain.affectedMenuItemIds,
        affectedActivityIds = domain.affectedActivityIds,
    )

    fun toDomain(): AppliedReward = AppliedReward(
        id = id,
        templateId = templateId,
        title = title,
        amount = amount,
        note = note,
        affectedMenuItemIds = affectedMenuItemIds,
        affectedActivityIds = affectedActivityIds,
    )
}

data class AdventureBookingDto(
    val clientId: String? = null,
    val clientName: String = "",
    val whatsappNumber: String = "",
    val nationalId: String = "",
    val startDayKey: String = "",
    val startAt: Timestamp = Timestamp.now(),
    val endAt: Timestamp = Timestamp.now(),
    val guestCount: Int? = null,
    val eventType: String? = null,
    val customEventTitle: String? = null,
    val eventNotes: String? = null,
    val items: List<AdventureReservationItemDraftDto> = emptyList(),
    val foodReservation: ReservationFoodDraftDto? = null,
    val blocks: List<AdventureBookingBlockDto> = emptyList(),
    val adventureSubtotal: Double? = null,
    val foodSubtotal: Double? = null,
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val loyaltyDiscountAmount: Double? = null,
    val appliedRewards: List<AdventureAppliedRewardDto>? = null,
    val nightPremium: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: String = AdventureBookingStatus.PENDING.rawValue,
    val createdAt: Timestamp = Timestamp.now(),
    val notes: String? = null,
) {
    fun toDomain(documentId: String): AdventureBooking = AdventureBooking(
        id = documentId,
        clientId = clientId,
        clientName = clientName,
        whatsappNumber = whatsappNumber,
        nationalId = nationalId,
        startDayKey = startDayKey,
        startAt = startAt.toDate(),
        endAt = endAt.toDate(),
        guestCount = guestCount ?: 1,
        eventType = ReservationEventType.fromRaw(eventType),
        customEventTitle = customEventTitle,
        eventNotes = eventNotes,
        items = items.mapNotNull { it.toDomain() },
        foodReservation = foodReservation?.toDomain(),
        blocks = blocks.mapNotNull { it.toDomain() },
        adventureSubtotal = adventureSubtotal ?: subtotal,
        foodSubtotal = foodSubtotal ?: 0.0,
        subtotal = subtotal,
        discountAmount = discountAmount,
        loyaltyDiscountAmount = loyaltyDiscountAmount ?: 0.0,
        appliedRewards = appliedRewards?.map { it.toDomain() } ?: emptyList(),
        nightPremium = nightPremium,
        totalAmount = totalAmount,
        status = AdventureBookingStatus.fromRaw(status),
        createdAt = createdAt.toDate(),
        notes = notes,
    )

    companion object {
        fun from(
            request: AdventureBookingRequest,
            plan: AdventureBuildPlan,
            createdAt: Date,
            status: AdventureBookingStatus = AdventureBookingStatus.PENDING,
        ): AdventureBookingDto = AdventureBookingDto(
            clientId = request.clientId,
            clientName = request.clientName,
            whatsappNumber = request.whatsappNumber,
            nationalId = request.nationalId,
            startDayKey = AdventureDateHelper.dayKey(plan.startAt),
            startAt = Timestamp(plan.startAt),
            endAt = Timestamp(plan.endAt),
            guestCount = request.guestCount,
            eventType = request.eventType.rawValue,
            customEventTitle = request.customEventTitle,
            eventNotes = request.eventNotes,
            items = request.items.map(::AdventureReservationItemDraftDto),
            foodReservation = request.foodReservation?.let(::ReservationFoodDraftDto),
            blocks = plan.blocks.map(::AdventureBookingBlockDto),
            adventureSubtotal = plan.adventureSubtotal,
            foodSubtotal = plan.foodSubtotal,
            subtotal = plan.subtotal,
            discountAmount = plan.discountAmount,
            loyaltyDiscountAmount = request.loyaltyDiscountAmount,
            appliedRewards = request.appliedRewards.map(::AdventureAppliedRewardDto),
            nightPremium = plan.nightPremium,
            totalAmount = plan.totalAmount,
            status = status.rawValue,
            createdAt = Timestamp(createdAt),
            notes = request.notes,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/data/AdventureCatalogDtos.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data


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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/data/AdventureCatalogRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data


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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCatalogModels.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCatalogRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


interface AdventureCatalogRepositoriable {
    suspend fun fetchCatalog(): AdventureCatalogSnapshot
    fun observeCatalog(): Flow<AdventureCatalogSnapshot>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCoreModels.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


private fun String.normalizedAdventureKey(): String =
    filter(Char::isLetterOrDigit).lowercase(Locale.US)

fun Double.adventureRoundMoney(): Double = round(this * 100.0) / 100.0

enum class AdventureActivityType(
    val rawValue: String,
    val legacyTitle: String,
    val legacySystemImage: String,
    val legacyDurationOptions: List<Int>,
) {
    OFF_ROAD("offRoad", "Off-road 4x4", "car.fill", listOf(60, 120, 180)),
    PAINTBALL("paintball", "Paintball", "shield.lefthalf.filled", listOf(30, 60, 90, 120)),
    GO_KARTS("goKarts", "Go karts", "flag.checkered", listOf(30, 60, 90, 120)),
    SHOOTING_RANGE("shootingRange", "Campo de tiro", "target", listOf(30, 60, 90, 120)),
    CAMPING("camping", "Camping", "tent.fill", emptyList()),
    EXTREME_SLIDE("extremeSlide", "Resbaladera extrema", "figure.fall", listOf(30));

    companion object {
        fun fromRaw(rawValue: String?): AdventureActivityType? {
            val key = rawValue?.normalizedAdventureKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedAdventureKey() == key || it.name.normalizedAdventureKey() == key
            }
        }

        fun defaultDraft(activity: AdventureActivityType): AdventureReservationItemDraft =
            when (activity) {
                OFF_ROAD -> AdventureReservationItemDraft(
                    activity = OFF_ROAD,
                    durationMinutes = 60,
                    peopleCount = 0,
                    vehicleCount = 1,
                    offRoadRiderCount = 2,
                    nights = 0,
                )

                PAINTBALL -> AdventureReservationItemDraft(
                    activity = PAINTBALL,
                    durationMinutes = 30,
                    peopleCount = 2,
                    vehicleCount = 0,
                    offRoadRiderCount = 0,
                    nights = 0,
                )

                GO_KARTS -> AdventureReservationItemDraft(
                    activity = GO_KARTS,
                    durationMinutes = 30,
                    peopleCount = 2,
                    vehicleCount = 0,
                    offRoadRiderCount = 0,
                    nights = 0,
                )

                SHOOTING_RANGE -> AdventureReservationItemDraft(
                    activity = SHOOTING_RANGE,
                    durationMinutes = 30,
                    peopleCount = 2,
                    vehicleCount = 0,
                    offRoadRiderCount = 0,
                    nights = 0,
                )

                CAMPING -> AdventureReservationItemDraft(
                    activity = CAMPING,
                    durationMinutes = 0,
                    peopleCount = 2,
                    vehicleCount = 0,
                    offRoadRiderCount = 0,
                    nights = 1,
                )

                EXTREME_SLIDE -> AdventureReservationItemDraft(
                    activity = EXTREME_SLIDE,
                    durationMinutes = 30,
                    peopleCount = 2,
                    vehicleCount = 0,
                    offRoadRiderCount = 0,
                    nights = 0,
                )
            }

        fun defaultDraft(
            activity: AdventureActivityType,
            catalog: AdventureCatalogSnapshot?
        ): AdventureReservationItemDraft =
            catalog?.activity(activity)?.defaultDraft ?: defaultDraft(activity)
    }
}

enum class AdventureResourceType(val rawValue: String) {
    OFF_ROAD_VEHICLES("offRoadVehicles"),
    PAINTBALL_PEOPLE("paintballPeople"),
    GO_KART_PEOPLE("goKartPeople"),
    SHOOTING_PEOPLE("shootingPeople"),
    CAMPING_PEOPLE("campingPeople"),
    EXTREME_SLIDE_PEOPLE("extremeSlidePeople");

    companion object {
        fun fromRaw(rawValue: String?): AdventureResourceType? {
            val key = rawValue?.normalizedAdventureKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedAdventureKey() == key || it.name.normalizedAdventureKey() == key
            }
        }
    }
}

enum class AdventureBookingStatus(val rawValue: String, val title: String) {
    PENDING("pending", "Pendiente"),
    CONFIRMED("confirmed", "Confirmada"),
    COMPLETED("completed", "Completada"),
    CANCELED("canceled", "Cancelada");

    companion object {
        fun fromRaw(rawValue: String?): AdventureBookingStatus {
            val key = rawValue?.normalizedAdventureKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedAdventureKey() == key || it.name.normalizedAdventureKey() == key
            } ?: CONFIRMED
        }
    }
}

enum class ReservationEventType(val rawValue: String, val title: String) {
    REGULAR_VISIT("regularVisit", "Visita regular"),
    BIRTHDAY("birthday", "Cumpleaños"),
    ANNIVERSARY("anniversary", "Aniversario"),
    CORPORATE("corporate", "Evento corporativo"),
    FAMILY_GATHERING("familyGathering", "Reunión familiar"),
    CUSTOM("custom", "Otro");

    companion object {
        fun fromRaw(rawValue: String?): ReservationEventType {
            val key = rawValue?.normalizedAdventureKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedAdventureKey() == key || it.name.normalizedAdventureKey() == key
            } ?: REGULAR_VISIT
        }
    }
}

enum class ReservationServingMoment(val rawValue: String, val title: String) {
    ON_ARRIVAL("onArrival", "Al llegar"),
    AFTER_ACTIVITIES("afterActivities", "Después de actividades"),
    SPECIFIC_TIME("specificTime", "Hora específica");

    companion object {
        fun fromRaw(rawValue: String?): ReservationServingMoment {
            val key = rawValue?.normalizedAdventureKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedAdventureKey() == key || it.name.normalizedAdventureKey() == key
            } ?: AFTER_ACTIVITIES
        }
    }
}

data class AdventureReservationItemDraft(
    val id: String = UUID.randomUUID().toString(),
    val activity: AdventureActivityType,
    val durationMinutes: Int,
    val peopleCount: Int,
    val vehicleCount: Int,
    val offRoadRiderCount: Int,
    val nights: Int,
) {
    val title: String get() = activity.legacyTitle

    val summaryText: String
        get() = when (activity) {
            AdventureActivityType.OFF_ROAD -> "${durationMinutes / 60}h • $vehicleCount vehículo(s) • $offRoadRiderCount persona(s)"
            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE -> "$durationMinutes min • $peopleCount persona(s)"

            AdventureActivityType.CAMPING -> "$nights noche(s) • $peopleCount persona(s)"
            AdventureActivityType.EXTREME_SLIDE -> "1 sesión • $peopleCount persona(s) • transporte incluido"
        }
}

data class ReservationFoodItemDraft(
    val id: String = UUID.randomUUID().toString(),
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val notes: String? = null,
) {
    constructor(menuItem: MenuItem, quantity: Int = 1, notes: String? = null) : this(
        menuItemId = menuItem.id,
        name = menuItem.name,
        unitPrice = menuItem.finalPrice,
        quantity = quantity.coerceAtLeast(1),
        notes = notes,
    )

    val safeQuantity: Int get() = quantity.coerceAtLeast(1)
    val subtotal: Double get() = (safeQuantity * unitPrice).adventureRoundMoney()
}

data class ReservationFoodDraft(
    val items: List<ReservationFoodItemDraft>,
    val servingMoment: ReservationServingMoment,
    val servingTime: Date?,
    val notes: String?,
) {
    val subtotal: Double get() = items.sumOf { it.subtotal }.adventureRoundMoney()
    val isEmpty: Boolean get() = items.isEmpty()
}

data class AdventureBookingBlock(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val activity: AdventureActivityType,
    val resourceType: AdventureResourceType,
    val startAt: Date,
    val endAt: Date,
    val reservedUnits: Int,
    val subtotal: Double,
)

data class AdventureBuildPlan(
    val startAt: Date,
    val endAt: Date,
    val blocks: List<AdventureBookingBlock>,
    val adventureSubtotal: Double,
    val foodSubtotal: Double,
    val subtotal: Double,
    val discountAmount: Double,
    val loyaltyDiscountAmount: Double,
    val appliedRewards: List<AppliedReward>,
    val nightPremium: Double,
    val totalAmount: Double,
    val hasNightPremium: Boolean,
)

data class AdventureAvailabilitySlot(
    val id: String = UUID.randomUUID().toString(),
    val startAt: Date,
    val endAt: Date,
    val blocks: List<AdventureBookingBlock>,
    val adventureSubtotal: Double,
    val foodSubtotal: Double,
    val subtotal: Double,
    val discountAmount: Double,
    val nightPremium: Double,
    val totalAmount: Double,
)

data class AdventureBookingRequest(
    val clientId: String?,
    val clientName: String,
    val whatsappNumber: String,
    val nationalId: String,
    val date: Date,
    val selectedStartAt: Date,
    val guestCount: Int,
    val eventType: ReservationEventType,
    val customEventTitle: String?,
    val eventNotes: String?,
    val items: List<AdventureReservationItemDraft>,
    val foodReservation: ReservationFoodDraft?,
    val packageDiscountAmount: Double = 0.0,
    val loyaltyDiscountAmount: Double = 0.0,
    val appliedRewards: List<AppliedReward> = emptyList(),
    val notes: String?,
) {
    val hasActivities: Boolean get() = items.isNotEmpty()
    val hasFoodReservation: Boolean get() = foodReservation?.isEmpty == false
}

data class AdventureBooking(
    val id: String,
    val clientId: String?,
    val clientName: String,
    val whatsappNumber: String,
    val nationalId: String,
    val startDayKey: String,
    val startAt: Date,
    val endAt: Date,
    val guestCount: Int,
    val eventType: ReservationEventType,
    val customEventTitle: String?,
    val eventNotes: String?,
    val items: List<AdventureReservationItemDraft>,
    val foodReservation: ReservationFoodDraft?,
    val blocks: List<AdventureBookingBlock>,
    val adventureSubtotal: Double,
    val foodSubtotal: Double,
    val subtotal: Double,
    val discountAmount: Double,
    val loyaltyDiscountAmount: Double,
    val appliedRewards: List<AppliedReward>,
    val nightPremium: Double,
    val totalAmount: Double,
    val status: AdventureBookingStatus,
    val createdAt: Date,
    val notes: String?,
) {
    val hasActivities: Boolean get() = items.isNotEmpty()
    val hasFoodReservation: Boolean get() = foodReservation?.isEmpty == false

    val eventDisplayTitle: String
        get() = if (eventType == ReservationEventType.CUSTOM) {
            customEventTitle?.trim()?.takeIf { it.isNotEmpty() } ?: "Evento personalizado"
        } else {
            eventType.title
        }

    val visitTypeTitle: String
        get() = when {
            hasActivities && hasFoodReservation -> "Aventura + comida"
            hasActivities -> "Solo aventura"
            hasFoodReservation -> "Solo comida"
            else -> "Reserva"
        }
}

object AdventureSchedule {
    const val SLOT_MINUTES = 30
    const val DAYTIME_START_HOUR = 7
    const val DAYTIME_END_HOUR = 20
    const val NIGHT_PREMIUM_START_HOUR = 18
    const val OFF_ROAD_PEOPLE_PER_VEHICLE = 2
    const val FOOD_ONLY_DEFAULT_DURATION_MINUTES = 90

    fun capacity(resource: AdventureResourceType): Int = when (resource) {
        AdventureResourceType.OFF_ROAD_VEHICLES -> 600
        AdventureResourceType.PAINTBALL_PEOPLE -> 1000
        AdventureResourceType.GO_KART_PEOPLE -> 1000
        AdventureResourceType.SHOOTING_PEOPLE -> 1000
        AdventureResourceType.CAMPING_PEOPLE -> 100
        AdventureResourceType.EXTREME_SLIDE_PEOPLE -> 100
    }
}

object AdventureDateHelper {
    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale("es", "EC"))
    private val shortDateFormatter = SimpleDateFormat("EEE d MMM", Locale("es", "EC"))

    fun dayKey(date: Date): String = dayFormatter.format(date)
    fun timeText(date: Date): String = timeFormatter.format(date)
    fun shortDateText(date: Date): String = shortDateFormatter.format(date)

    fun dateOn(day: Date, hour: Int, minute: Int): Date =
        Calendar.getInstance().apply {
            time = day
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

    fun addMinutes(value: Int, date: Date): Date = Calendar.getInstance().apply {
        time = date
        add(Calendar.MINUTE, value)
    }.time

    fun addDays(value: Int, date: Date): Date = Calendar.getInstance().apply {
        time = date
        add(Calendar.DAY_OF_YEAR, value)
    }.time

    fun startOfDay(date: Date): Date = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    fun sameDay(lhs: Date, rhs: Date): Boolean = dayKey(lhs) == dayKey(rhs)
    fun isDateInToday(date: Date): Boolean = sameDay(date, Date())

    fun slotIndex(date: Date): Int {
        val cal = Calendar.getInstance().apply { time = date }
        val totalMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        return totalMinutes / AdventureSchedule.SLOT_MINUTES
    }

    fun isNightPremiumTime(startAt: Date, endAt: Date): Boolean {
        val startHour = Calendar.getInstance().apply { time = startAt }.get(Calendar.HOUR_OF_DAY)
        val endHour = Calendar.getInstance().apply { time = endAt }.get(Calendar.HOUR_OF_DAY)
        return startHour >= AdventureSchedule.NIGHT_PREMIUM_START_HOUR ||
                endHour >= AdventureSchedule.NIGHT_PREMIUM_START_HOUR ||
                startHour < AdventureSchedule.DAYTIME_START_HOUR
    }

    fun combineDayAndTime(day: Date, time: Date): Date {
        val timeCal = Calendar.getInstance().apply { this.time = time }
        return dateOn(
            day = day,
            hour = timeCal.get(Calendar.HOUR_OF_DAY),
            minute = timeCal.get(Calendar.MINUTE),
        )
    }
}

object AdventurePricingEngine {
    const val NIGHT_PREMIUM_RATE = 0.25

    fun finalUnitPrice(config: AdventureActivityCatalogItem): Double =
        (config.basePrice - config.discountAmount).coerceAtLeast(0.0).adventureRoundMoney()

    fun lineBaseSubtotal(
        item: AdventureReservationItemDraft,
        config: AdventureActivityCatalogItem
    ): Double =
        when (item.activity) {
            AdventureActivityType.OFF_ROAD -> config.basePrice * (item.durationMinutes.toDouble() / 60.0) * item.vehicleCount
            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE -> config.basePrice * (item.durationMinutes.toDouble() / 30.0) * item.peopleCount

            AdventureActivityType.CAMPING -> config.basePrice * item.peopleCount * item.nights.coerceAtLeast(
                1
            )

            AdventureActivityType.EXTREME_SLIDE -> config.basePrice * item.peopleCount
        }.adventureRoundMoney()

    fun subtotal(
        item: AdventureReservationItemDraft,
        config: AdventureActivityCatalogItem
    ): Double =
        when (item.activity) {
            AdventureActivityType.OFF_ROAD -> finalUnitPrice(config) * (item.durationMinutes.toDouble() / 60.0) * item.vehicleCount
            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE -> finalUnitPrice(config) * (item.durationMinutes.toDouble() / 30.0) * item.peopleCount

            AdventureActivityType.CAMPING -> finalUnitPrice(config) * item.peopleCount * item.nights.coerceAtLeast(
                1
            )

            AdventureActivityType.EXTREME_SLIDE -> finalUnitPrice(config) * item.peopleCount
        }.adventureRoundMoney()

    fun subtotal(item: AdventureReservationItemDraft, catalog: AdventureCatalogSnapshot): Double {
        val config = catalog.activity(item.activity) ?: return 0.0
        return subtotal(item, config)
    }

    fun lineDiscountAmount(
        item: AdventureReservationItemDraft,
        catalog: AdventureCatalogSnapshot
    ): Double {
        val config = catalog.activity(item.activity) ?: return 0.0
        return (lineBaseSubtotal(item, config) - subtotal(item, config)).coerceAtLeast(0.0)
            .adventureRoundMoney()
    }

    fun estimatedSubtotal(
        items: List<AdventureReservationItemDraft>,
        catalog: AdventureCatalogSnapshot
    ): Double =
        items.sumOf { subtotal(it, catalog) }.adventureRoundMoney()

    fun estimatedDiscountAmount(
        items: List<AdventureReservationItemDraft>,
        catalog: AdventureCatalogSnapshot
    ): Double =
        items.sumOf { lineDiscountAmount(it, catalog) }.adventureRoundMoney()

    fun foodSubtotal(foodReservation: ReservationFoodDraft?): Double =
        (foodReservation?.subtotal ?: 0.0).adventureRoundMoney()

    fun packageTotal(
        items: List<AdventureReservationItemDraft>,
        packageDiscountAmount: Double,
        catalog: AdventureCatalogSnapshot,
    ): Double = (estimatedSubtotal(items, catalog) - packageDiscountAmount.coerceAtLeast(0.0))
        .coerceAtLeast(0.0)
        .adventureRoundMoney()
}

object AdventurePlanner {
    fun buildPlan(
        day: Date,
        startAt: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
        catalog: AdventureCatalogSnapshot,
    ): AdventureBuildPlan? {
        val hasFood = foodReservation?.isEmpty == false
        if (items.isEmpty() && !hasFood) return null

        val foodSubtotal = AdventurePricingEngine.foodSubtotal(foodReservation)
        val dayStart = AdventureDateHelper.dateOn(day, AdventureSchedule.DAYTIME_START_HOUR, 0)
        val dayEnd = AdventureDateHelper.dateOn(day, AdventureSchedule.DAYTIME_END_HOUR, 0)

        if (startAt.before(dayStart)) return null

        if (items.isEmpty()) {
            val end = AdventureDateHelper.addMinutes(
                AdventureSchedule.FOOD_ONLY_DEFAULT_DURATION_MINUTES,
                startAt
            )
            if (end.after(dayEnd)) return null
            return AdventureBuildPlan(
                startAt = startAt,
                endAt = end,
                blocks = emptyList(),
                adventureSubtotal = 0.0,
                foodSubtotal = foodSubtotal,
                subtotal = foodSubtotal,
                discountAmount = 0.0,
                loyaltyDiscountAmount = 0.0,
                appliedRewards = emptyList(),
                nightPremium = 0.0,
                totalAmount = foodSubtotal,
                hasNightPremium = false,
            )
        }

        var cursor = startAt
        val blocks = mutableListOf<AdventureBookingBlock>()
        var discountedAdventureSubtotal = 0.0
        var activityDiscountAmount = 0.0

        items.forEachIndexed { index, item ->
            val config = catalog.activity(item.activity) ?: return null
            if (!config.isActive) return null

            fun addSimpleBlock(resourceType: AdventureResourceType): Boolean {
                val end = AdventureDateHelper.addMinutes(item.durationMinutes, cursor)
                if (end.after(dayEnd)) return false
                val lineSubtotal = AdventurePricingEngine.subtotal(item, catalog)
                val lineDiscount = AdventurePricingEngine.lineDiscountAmount(item, catalog)
                discountedAdventureSubtotal += lineSubtotal
                activityDiscountAmount += lineDiscount
                blocks += AdventureBookingBlock(
                    title = config.title,
                    activity = item.activity,
                    resourceType = resourceType,
                    startAt = cursor,
                    endAt = end,
                    reservedUnits = item.peopleCount,
                    subtotal = lineSubtotal,
                )
                cursor = end
                return true
            }

            when (item.activity) {
                AdventureActivityType.OFF_ROAD -> {
                    if (item.vehicleCount <= 0 || item.offRoadRiderCount <= 0) return null
                    if (item.offRoadRiderCount > item.vehicleCount * AdventureSchedule.OFF_ROAD_PEOPLE_PER_VEHICLE) return null
                    val end = AdventureDateHelper.addMinutes(item.durationMinutes, cursor)
                    if (end.after(dayEnd)) return null
                    val lineSubtotal = AdventurePricingEngine.subtotal(item, catalog)
                    val lineDiscount = AdventurePricingEngine.lineDiscountAmount(item, catalog)
                    discountedAdventureSubtotal += lineSubtotal
                    activityDiscountAmount += lineDiscount
                    blocks += AdventureBookingBlock(
                        title = config.title,
                        activity = AdventureActivityType.OFF_ROAD,
                        resourceType = AdventureResourceType.OFF_ROAD_VEHICLES,
                        startAt = cursor,
                        endAt = end,
                        reservedUnits = item.vehicleCount,
                        subtotal = lineSubtotal,
                    )
                    cursor = end
                }

                AdventureActivityType.PAINTBALL ->
                    if (!addSimpleBlock(AdventureResourceType.PAINTBALL_PEOPLE)) return null

                AdventureActivityType.GO_KARTS ->
                    if (!addSimpleBlock(AdventureResourceType.GO_KART_PEOPLE)) return null

                AdventureActivityType.SHOOTING_RANGE ->
                    if (!addSimpleBlock(AdventureResourceType.SHOOTING_PEOPLE)) return null

                AdventureActivityType.EXTREME_SLIDE -> {
                    val transportVehicles = max(
                        1,
                        ceil(item.peopleCount.toDouble() / AdventureSchedule.OFF_ROAD_PEOPLE_PER_VEHICLE.toDouble()).toInt(),
                    )
                    val transportEnd = AdventureDateHelper.addMinutes(30, cursor)
                    val slideEnd = AdventureDateHelper.addMinutes(30, transportEnd)
                    if (slideEnd.after(dayEnd)) return null

                    blocks += AdventureBookingBlock(
                        title = "Transporte al columpio extremo",
                        activity = AdventureActivityType.EXTREME_SLIDE,
                        resourceType = AdventureResourceType.OFF_ROAD_VEHICLES,
                        startAt = cursor,
                        endAt = transportEnd,
                        reservedUnits = transportVehicles,
                        subtotal = 0.0,
                    )

                    val lineSubtotal = AdventurePricingEngine.subtotal(item, catalog)
                    val lineDiscount = AdventurePricingEngine.lineDiscountAmount(item, catalog)
                    discountedAdventureSubtotal += lineSubtotal
                    activityDiscountAmount += lineDiscount

                    blocks += AdventureBookingBlock(
                        title = config.title,
                        activity = AdventureActivityType.EXTREME_SLIDE,
                        resourceType = AdventureResourceType.EXTREME_SLIDE_PEOPLE,
                        startAt = transportEnd,
                        endAt = slideEnd,
                        reservedUnits = item.peopleCount,
                        subtotal = lineSubtotal,
                    )
                    cursor = slideEnd
                }

                AdventureActivityType.CAMPING -> {
                    if (index != items.lastIndex) return null
                    val campingStart = AdventureDateHelper.dateOn(day, 19, 0)
                    if (cursor.after(campingStart)) return null

                    repeat(item.nights.coerceAtLeast(1)) { night ->
                        val start = AdventureDateHelper.addDays(night, campingStart)
                        val end = AdventureDateHelper.addMinutes(12 * 60, start)
                        val nightItem = AdventureReservationItemDraft(
                            activity = AdventureActivityType.CAMPING,
                            durationMinutes = 0,
                            peopleCount = item.peopleCount,
                            vehicleCount = 0,
                            offRoadRiderCount = 0,
                            nights = 1,
                        )
                        val nightSubtotal = AdventurePricingEngine.subtotal(nightItem, catalog)
                        val nightDiscount =
                            AdventurePricingEngine.lineDiscountAmount(nightItem, catalog)
                        discountedAdventureSubtotal += nightSubtotal
                        activityDiscountAmount += nightDiscount

                        blocks += AdventureBookingBlock(
                            title = "${config.title} Noche ${night + 1}",
                            activity = AdventureActivityType.CAMPING,
                            resourceType = AdventureResourceType.CAMPING_PEOPLE,
                            startAt = start,
                            endAt = end,
                            reservedUnits = item.peopleCount,
                            subtotal = nightSubtotal,
                        )
                    }
                    cursor = blocks.lastOrNull()?.endAt ?: cursor
                }
            }
        }

        val last = blocks.lastOrNull() ?: return null
        val hasNightPremium = items.any { it.activity == AdventureActivityType.CAMPING } ||
                blocks.any { AdventureDateHelper.isNightPremiumTime(it.startAt, it.endAt) }

        val packageDiscount = packageDiscountAmount.coerceAtLeast(0.0)
        val totalDiscountAmount = (activityDiscountAmount + packageDiscount).adventureRoundMoney()
        val packageAdjustedAdventureSubtotal =
            (discountedAdventureSubtotal - packageDiscount).coerceAtLeast(0.0).adventureRoundMoney()
        val totalSubtotal = (discountedAdventureSubtotal + foodSubtotal).adventureRoundMoney()
        val totalAmount = (packageAdjustedAdventureSubtotal + foodSubtotal).adventureRoundMoney()

        return AdventureBuildPlan(
            startAt = startAt,
            endAt = last.endAt,
            blocks = blocks,
            adventureSubtotal = discountedAdventureSubtotal.adventureRoundMoney(),
            foodSubtotal = foodSubtotal,
            subtotal = totalSubtotal,
            discountAmount = totalDiscountAmount,
            loyaltyDiscountAmount = 0.0,
            appliedRewards = emptyList(),
            nightPremium = 0.0,
            totalAmount = totalAmount,
            hasNightPremium = hasNightPremium,
        )
    }

    fun affectedDayKeys(day: Date, items: List<AdventureReservationItemDraft>): List<String> {
        val campingNights =
            items.firstOrNull { it.activity == AdventureActivityType.CAMPING }?.nights ?: 0
        val days = max(1, campingNights + 1)
        return (0 until days).map {
            AdventureDateHelper.dayKey(
                AdventureDateHelper.addDays(
                    it,
                    day
                )
            )
        }
    }

    fun buildAvailability(
        day: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
        catalog: AdventureCatalogSnapshot,
    ): List<AdventureAvailabilitySlot> {
        val hasFood = foodReservation?.isEmpty == false
        if (items.isEmpty() && !hasFood) return emptyList()

        val startWindow = AdventureDateHelper.dateOn(day, AdventureSchedule.DAYTIME_START_HOUR, 0)
        val endWindow = AdventureDateHelper.dateOn(day, AdventureSchedule.DAYTIME_END_HOUR, 0)
        val now = Date()
        val isToday = AdventureDateHelper.sameDay(day, now)

        var current = startWindow
        val slots = mutableListOf<AdventureAvailabilitySlot>()

        while (!current.after(endWindow)) {
            val plan =
                buildPlan(day, current, items, foodReservation, packageDiscountAmount, catalog)
            if (!(isToday && current.before(now)) && plan != null) {
                slots += AdventureAvailabilitySlot(
                    startAt = plan.startAt,
                    endAt = plan.endAt,
                    blocks = plan.blocks,
                    adventureSubtotal = plan.adventureSubtotal,
                    foodSubtotal = plan.foodSubtotal,
                    subtotal = plan.subtotal,
                    discountAmount = plan.discountAmount,
                    nightPremium = plan.nightPremium,
                    totalAmount = plan.totalAmount,
                )
            }
            current = AdventureDateHelper.addMinutes(AdventureSchedule.SLOT_MINUTES, current)
        }

        return slots
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureUseCases.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


class FetchAdventureCatalogUseCase @Inject constructor(
    private val repository: AdventureCatalogRepositoriable,
) {
    suspend fun execute(): AdventureCatalogSnapshot = repository.fetchCatalog()
}

class ObserveAdventureCatalogUseCase @Inject constructor(
    private val repository: AdventureCatalogRepositoriable,
) {
    fun execute(): Flow<AdventureCatalogSnapshot> = repository.observeCatalog()
}

class GetAdventureAvailabilityUseCase @Inject constructor(
    private val repository: AdventureBookingsRepositoriable,
) {
    suspend fun execute(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot> = repository.fetchAvailability(
        date = date,
        items = items,
        foodReservation = foodReservation,
        packageDiscountAmount = packageDiscountAmount,
    )
}

class CreateAdventureBookingUseCase @Inject constructor(
    private val repository: AdventureBookingsRepositoriable,
) {
    suspend fun execute(request: AdventureBookingRequest): AdventureBooking =
        repository.createBooking(request)
}

class ObserveAdventureBookingsUseCase @Inject constructor(
    private val repository: AdventureBookingsRepositoriable,
) {
    fun execute(nationalId: String): Flow<List<AdventureBooking>> =
        repository.observeBookings(nationalId = nationalId)
}

class CancelAdventureBookingUseCase @Inject constructor(
    private val repository: AdventureBookingsRepositoriable,
) {
    suspend fun execute(id: String, nationalId: String) {
        repository.cancelBooking(id = id, nationalId = nationalId)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/view/AdventureFoodPickerSheet.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view


private val adventureFoodCategoryDisplayOrder = listOf(
    "Entradas",
    "Sopas",
    "Platos Fuertes",
    "Extras",
    "Postres",
    "Bebidas",
    "Bebidas Alcohólicas",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureFoodPickerSheet(
    menuSections: List<MenuSection>,
    selectedDate: Date,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onDismiss: () -> Unit,
    onAdd: (MenuItem, Int, String?) -> Unit,
) {
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    val orderedSections = menuSections.sortedWith(
        compareBy<MenuSection> { section ->
            adventureFoodCategoryDisplayOrder.indexOf(section.category.title).takeIf { it >= 0 }
                ?: Int.MAX_VALUE
        }.thenBy { it.category.title },
    )

    val visibleSections = orderedSections
        .filter { section -> selectedCategoryId == null || section.category.id == selectedCategoryId }
        .mapNotNull { section ->
            val query = searchText.trim().lowercase()
            val items = if (query.isEmpty()) {
                section.items
            } else {
                section.items.filter { item ->
                    item.name.lowercase().contains(query) ||
                            item.description.lowercase().contains(query) ||
                            item.ingredients.any { it.lowercase().contains(query) }
                }
            }
            if (items.isEmpty()) null else section.copy(items = items)
        }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AdventureSectionTitle(
                    title = "Menú del restaurante",
                    subtitle = "Agrega platos a tu reserva de aventura.",
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cerrar")
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                label = { Text("Buscar plato, bebida o ingrediente") },
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = { selectedCategoryId = null },
                    label = { Text("Todo") },
                    leadingIcon = if (selectedCategoryId == null) {
                        { Icon(Icons.Rounded.LocalDining, contentDescription = null) }
                    } else null,
                )
                orderedSections.map { it.category }.distinctBy { it.id }.forEach { category ->
                    AssistChip(
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.title) },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (visibleSections.isEmpty()) {
                    AdventureEmptyState(
                        title = "No se encontraron platos",
                        body = "Prueba otra búsqueda o cambia de categoría.",
                        icon = Icons.Rounded.Search,
                    )
                } else {
                    visibleSections.forEach { section ->
                        Text(
                            text = section.category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        section.items.forEach { item ->
                            AdventureFoodPickerRow(
                                item = item,
                                selectedDate = selectedDate,
                                rewardPresentation = rewardPresentationProvider(item, 1),
                                displayedPrice = displayedPriceProvider(item, 1),
                                incrementalDiscount = incrementalDiscountProvider(item, 1),
                                onAdd = { quantity, notes ->
                                    onAdd(item, quantity, notes)
                                    onDismiss()
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun AdventureFoodPickerRow(
    item: MenuItem,
    selectedDate: Date,
    rewardPresentation: RewardPresentation?,
    displayedPrice: Double,
    incrementalDiscount: Double,
    onAdd: (Int, String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    val blockedToday = AdventureDateHelper.isDateInToday(selectedDate) && !item.canBeOrdered

    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = Icons.Rounded.LocalDining)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (incrementalDiscount > 0) {
                        Text(
                            text = item.finalPrice.priceText(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        text = (if (incrementalDiscount > 0) displayedPrice else item.finalPrice).priceText(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                rewardPresentation?.let { reward ->
                    Text(
                        text = "${reward.badge}: ${reward.message}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                if (blockedToday) {
                    Text(
                        text = "Por hoy está agotado y no se puede pedir.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        if (expanded) {
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { quantity = (quantity - 1).coerceAtLeast(1) }) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Menos")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { quantity += 1 }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Más")
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                label = { Text("Notas para cocina") },
            )
            Button(
                onClick = { onAdd(quantity, notes.trim().takeIf { it.isNotEmpty() }) },
                enabled = !blockedToday,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Agregar a la reserva")
            }
        } else {
            TextButton(onClick = { expanded = true }, enabled = !blockedToday) {
                Text("Elegir cantidad y notas")
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/view/AdventureScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view


private sealed interface AdventureMode {
    data object Catalog : AdventureMode
    data object Builder : AdventureMode
}

@Composable
fun AdventureScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    catalogViewModel: AdventureCatalogViewModel = hiltViewModel(),
    builderViewModel: AdventureComboBuilderViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
) {
    val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()
    val builderState by builderViewModel.uiState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    var mode by remember { mutableStateOf<AdventureMode>(AdventureMode.Catalog) }
    var showFoodPicker by remember { mutableStateOf(false) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        catalogViewModel.onAppear()
        builderViewModel.onAppear(sessionState.profile)
        menuViewModel.onAppear(sessionState.profile.nationalId)
    }

    DisposableEffect(Unit) {
        onDispose {
            catalogViewModel.onDisappear()
            builderViewModel.onDisappear()
        }
    }

    if (showFoodPicker) {
        AdventureFoodPickerSheet(
            menuSections = menuState.sections,
            selectedDate = builderState.selectedDate,
            rewardPresentationProvider = builderViewModel::foodPickerRewardPresentation,
            displayedPriceProvider = builderViewModel::foodPickerDisplayedPrice,
            incrementalDiscountProvider = builderViewModel::foodPickerIncrementalDiscount,
            onDismiss = { showFoodPicker = false },
            onAdd = { item, quantity, notes ->
                builderViewModel.addFoodItem(item, quantity, notes)
            },
        )
    }

    val message =
        builderState.errorMessage ?: builderState.successMessage ?: catalogState.errorMessage
    if (message != null) {
        AlertDialog(
            onDismissRequest = {
                builderViewModel.dismissMessage()
                catalogViewModel.clearError()
            },
            confirmButton = {
                TextButton(onClick = {
                    builderViewModel.dismissMessage()
                    catalogViewModel.clearError()
                }) { Text("OK") }
            },
            title = { Text("Mensaje") },
            text = { Text(message) },
        )
    }

    when (mode) {
        AdventureMode.Catalog -> AdventureCatalogContent(
            modifier = modifier,
            isLoading = catalogState.isLoading,
            catalog = catalogState.catalog,
            menuSections = menuState.sections,
            builderViewModel = builderViewModel,
            onCustomCombo = {
                builderViewModel.prepareCustomDraftIfNeeded()
                mode = AdventureMode.Builder
            },
            onOpenSingle = { activity ->
                builderViewModel.replaceItems(listOf(activity.defaultDraft), 0.0)
                mode = AdventureMode.Builder
            },
            onOpenPackage = { packageModel ->
                builderViewModel.replacePackage(packageModel, menuState.sections)
                mode = AdventureMode.Builder
            },
        )

        AdventureMode.Builder -> AdventureBuilderContent(
            modifier = modifier,
            viewModel = builderViewModel,
            menuSections = menuState.sections,
            onBack = { mode = AdventureMode.Catalog },
            onAddFood = { showFoodPicker = true },
            clientId = sessionState.profile.id,
        )
    }
}

@Composable
private fun AdventureCatalogContent(
    isLoading: Boolean,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    builderViewModel: AdventureComboBuilderViewModel,
    onCustomCombo: () -> Unit,
    onOpenSingle: (AdventureActivityCatalogItem) -> Unit,
    onOpenPackage: (AdventureFeaturedPackage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        AdventureGradientHero(
            title = "Construye tu combo perfecto",
            subtitle = "Actividades, paquetes destacados, comida del restaurante, horarios y premios Murco Loyalty en una sola reserva.",
            action = {
                Button(
                    onClick = onCustomCombo,
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar combo personalizado")
                }
            },
        )

        if (isLoading && catalog.activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AdventureSectionTitle(
                title = "Paquetes destacados",
                subtitle = "Combos sugeridos cargados desde Firestore.",
            )
            if (catalog.activePackagesSorted.isEmpty()) {
                AdventureEmptyState(
                    title = "No hay paquetes destacados",
                    body = "Cuando actives paquetes en Firestore aparecerán aquí.",
                    icon = Icons.Rounded.Explore,
                )
            } else {
                catalog.activePackagesSorted.forEach { packageModel ->
                    FeaturedPackageCard(
                        packageModel = packageModel,
                        catalog = catalog,
                        menuSections = menuSections,
                        reward = builderViewModel.packageRewardPresentation(
                            packageModel,
                            menuSections
                        ),
                        onClick = { onOpenPackage(packageModel) },
                    )
                }
            }

            AdventureSectionTitle(
                title = "Actividades individuales",
                subtitle = "Reserva una actividad o úsala como base para tu combo.",
            )
            catalog.activeActivitiesSorted.forEach { activity ->
                SingleActivityCard(
                    activity = activity,
                    reward = builderViewModel.catalogRewardPresentation(activity),
                    onClick = { onOpenSingle(activity) },
                )
            }

            AdventureCard {
                AdventureSectionTitle(
                    title = "¿Necesitas algo diferente?",
                    subtitle = "Crea una combinación a medida con tiempos, personas, comida y notas del evento.",
                )
                OutlinedButton(onClick = onCustomCombo, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.Explore, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir creador de aventuras")
                }
            }
        }
    }
}

@Composable
private fun FeaturedPackageCard(
    packageModel: AdventureFeaturedPackage,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id }
    val activitySubtotal = AdventurePricingEngine.estimatedSubtotal(packageModel.items, catalog)
    val foodSubtotal = packageModel.foodItems.sumOf { food ->
        (menuItemsById[food.menuItemId]?.finalPrice ?: 0.0) * food.quantity
    }
    val total =
        (activitySubtotal + foodSubtotal - packageModel.packageDiscountAmount).coerceAtLeast(0.0)
    val foodSummary = packageModel.foodItems.joinToString(" • ") { food ->
        "${food.quantity}x ${menuItemsById[food.menuItemId]?.name ?: food.menuItemId}"
    }

    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = Icons.Rounded.Explore)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        packageModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    packageModel.badge?.takeIf { it.isNotBlank() }
                        ?.let { AdventureBadge(text = it) }
                }
                Text(
                    packageModel.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (foodSummary.isNotBlank()) Text(
                    foodSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Aventura ${activitySubtotal.priceText()}${if (foodSubtotal > 0) " • Comida ${foodSubtotal.priceText()}" else ""}",
                    style = MaterialTheme.typography.labelMedium
                )
                if (packageModel.packageDiscountAmount > 0) Text(
                    "Descuento del paquete: ${packageModel.packageDiscountAmount.priceText()}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                reward?.let {
                    Text(
                        "${it.badge}: ${it.message}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Desde ${total.priceText()} • Ver combo")
        }
    }
}

@Composable
private fun SingleActivityCard(
    activity: AdventureActivityCatalogItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = adventureIconFor(activity.activityType))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    activity.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Desde ${activity.finalUnitPrice.priceText()}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (activity.hasDiscount) {
                        Text(
                            "Antes ${activity.basePrice.priceText()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                reward?.let {
                    Text(
                        "${it.badge}: ${it.message}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Reservar")
        }
    }
}

@Composable
private fun AdventureBuilderContent(
    viewModel: AdventureComboBuilderViewModel,
    menuSections: List<MenuSection>,
    onBack: () -> Unit,
    onAddFood: () -> Unit,
    clientId: String?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingItem by remember { mutableStateOf<AdventureReservationItemDraft?>(null) }
    var editingFood by remember { mutableStateOf<ReservationFoodItemDraft?>(null) }

    editingItem?.let { item ->
        AdventureItemEditorDialog(
            item = item,
            config = viewModel.config(item.activity),
            onDismiss = { editingItem = null },
            onSave = {
                viewModel.updateItem(it)
                editingItem = null
            },
        )
    }

    editingFood?.let { item ->
        FoodItemEditorDialog(
            item = item,
            onDismiss = { editingFood = null },
            onSave = {
                viewModel.updateFoodItem(it)
                editingFood = null
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.submit(clientId) },
                    enabled = !state.isSubmitting && state.selectedSlot != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isSubmitting) CircularProgressIndicator() else Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.isSubmitting) "Confirmando..." else "Confirmar reserva")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Crear reserva",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }

            if (state.isLoadingCatalog || state.isLoadingAvailability || state.isLoadingRewards) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            AdventureDateAndSlotsSection(viewModel = viewModel)
            AdventureEventSection(viewModel = viewModel)
            AdventureActivitiesSection(viewModel = viewModel, onEditItem = { editingItem = it })
            AdventureFoodSection(
                viewModel = viewModel,
                onAddFood = onAddFood,
                onEditFood = { editingFood = it })
            AdventureContactSection(viewModel = viewModel)
            AdventureSummarySection(viewModel = viewModel)
            Spacer(Modifier.height(84.dp))
        }
    }
}

@Composable
private fun AdventureDateAndSlotsSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    AdventureCard {
        AdventureSectionTitle("Fecha", "Elige el día de visita y luego un horario disponible.")
        Button(
            onClick = {
                val calendar = Calendar.getInstance().apply { time = state.selectedDate }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = Calendar.getInstance().apply {
                            set(year, month, day, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        viewModel.setDate(picked.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            },
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(AdventureDateHelper.shortDateText(state.selectedDate))
        }
    }

    AdventureCard {
        AdventureSectionTitle("Horarios disponibles", "Selecciona inicio o llegada preferida.")
        if (state.isLoadingAvailability) {
            CircularProgressIndicator()
        } else if (state.availableSlots.isEmpty()) {
            Text(
                "Agrega una actividad o comida, o prueba otra fecha.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.availableSlots.forEach { slot ->
                    SlotChip(
                        slot = slot,
                        selected = state.selectedSlot?.startAt == slot.startAt,
                        total = viewModel.effectiveTotal(slot),
                        onClick = { viewModel.selectSlot(slot) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotChip(
    slot: AdventureAvailabilitySlot,
    selected: Boolean,
    total: Double,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Text(AdventureDateHelper.timeText(slot.startAt), fontWeight = FontWeight.Bold)
                Text("Termina ${AdventureDateHelper.timeText(slot.endAt)}")
                Text(
                    total.priceText(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
    )
}

@Composable
private fun AdventureEventSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Evento", "Invitados, tipo de evento y notas especiales.")
        CounterRow(
            title = "Invitados",
            value = state.guestCount,
            onDecrease = { viewModel.setGuestCount(state.guestCount - 1) },
            onIncrease = { viewModel.setGuestCount(state.guestCount + 1) })
        EnumDropdown(
            title = "Tipo de evento",
            current = state.eventType,
            values = ReservationEventType.entries,
            label = { it.title },
            onSelected = viewModel::setEventType,
        )
        if (state.eventType == ReservationEventType.CUSTOM) {
            OutlinedTextField(
                value = state.customEventTitle,
                onValueChange = viewModel::setCustomEventTitle,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del evento") })
        }
        OutlinedTextField(
            value = state.eventNotes,
            onValueChange = viewModel::setEventNotes,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Notas del evento") })
    }
}

@Composable
private fun AdventureActivitiesSection(
    viewModel: AdventureComboBuilderViewModel,
    onEditItem: (AdventureReservationItemDraft) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle(
            "Actividades",
            "Opcionales. Puedes reservar aventura, comida o ambas."
        )
        if (state.items.isEmpty()) {
            Text(
                "No hay actividades agregadas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.items.forEach { item ->
                ActivityDraftRow(
                    item = item,
                    viewModel = viewModel,
                    onEdit = { onEditItem(item) },
                    onDelete = { viewModel.removeItem(item.id) })
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.availableActivitiesToAdd.forEach { activity ->
                AssistChip(
                    onClick = { viewModel.addItem(activity.activityType) },
                    label = { Text("+ ${activity.title}") })
            }
        }
    }
}

@Composable
private fun ActivityDraftRow(
    item: AdventureReservationItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AdventureIconBubble(icon = adventureIconFor(item.activity))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold)
            Text(
                item.summaryText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val base = viewModel.baseAdventureSubtotal(item)
            val shown = viewModel.displayedAdventureSubtotal(item)
            Text(
                if (shown < base) "${base.priceText()} → ${shown.priceText()}" else base.priceText(),
                color = MaterialTheme.colorScheme.primary
            )
            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, contentDescription = "Editar") }
        IconButton(onClick = onDelete) { Icon(Icons.Rounded.Delete, contentDescription = "Quitar") }
    }
}

@Composable
private fun AdventureFoodSection(
    viewModel: AdventureComboBuilderViewModel,
    onAddFood: () -> Unit,
    onEditFood: (ReservationFoodItemDraft) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Comida", "Agrega platos del restaurante a la reserva.")
        if (state.foodItems.isEmpty()) {
            Text(
                "No hay platos agregados todavía.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.foodItems.forEach { item ->
                FoodDraftRow(item = item, viewModel = viewModel, onEdit = { onEditFood(item) })
            }
            Divider()
            EnumDropdown(
                title = "Momento de servicio",
                current = state.foodServingMoment,
                values = ReservationServingMoment.entries,
                label = { it.title },
                onSelected = viewModel::setFoodServingMoment,
            )
            if (state.foodServingMoment == ReservationServingMoment.SPECIFIC_TIME) {
                val context = LocalContext.current
                Button(onClick = {
                    val calendar = Calendar.getInstance().apply { time = state.foodServingTime }
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val picked = Calendar.getInstance().apply {
                                time = state.foodServingTime
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            viewModel.setFoodServingTime(picked.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false,
                    ).show()
                }) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Hora: ${AdventureDateHelper.timeText(state.foodServingTime)}")
                }
            }
            OutlinedTextField(
                value = state.foodNotes,
                onValueChange = viewModel::setFoodNotes,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                label = { Text("Notas de comida") })
        }
        OutlinedButton(onClick = onAddFood, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Restaurant, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar comida")
        }
    }
}

@Composable
private fun FoodDraftRow(
    item: ReservationFoodItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AdventureIconBubble(icon = Icons.Rounded.LocalDining)
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text(
                "${item.quantity} x ${item.unitPrice.priceText()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                viewModel.displayedFoodSubtotal(item).priceText(),
                color = MaterialTheme.colorScheme.primary
            )
            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = { viewModel.decreaseFoodQuantity(item.id) }) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = "Menos"
            )
        }
        IconButton(onClick = { viewModel.increaseFoodQuantity(item.id) }) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Más"
            )
        }
        IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, contentDescription = "Editar") }
        IconButton(onClick = { viewModel.removeFoodItem(item.id) }) {
            Icon(
                Icons.Rounded.Delete,
                contentDescription = "Quitar"
            )
        }
    }
}

@Composable
private fun AdventureContactSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Contacto", "Datos sincronizados desde tu perfil.")
        ContactLine(Icons.Rounded.Person, "Nombre", state.clientName)
        ContactLine(Icons.Rounded.Phone, "WhatsApp", state.whatsappNumber)
        ContactLine(Icons.Rounded.Event, "Cédula", state.nationalId)
        OutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::setNotes,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Notas generales") })
    }
}

@Composable
private fun ContactLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(value.ifBlank { "No registrado" }, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AdventureSummarySection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Resumen", "Revisa el total antes de confirmar.")
        val slot = state.selectedSlot
        if (slot != null) {
            AdventurePriceRow("Aventura", slot.adventureSubtotal)
            AdventurePriceRow("Comida", slot.foodSubtotal)
            AdventurePriceRow("Subtotal", slot.subtotal)
            AdventurePriceRow("Descuento aventura", slot.discountAmount, negative = true)
            if (state.rewardPreview.totalDiscount > 0) AdventurePriceRow(
                "Murco Loyalty",
                state.rewardPreview.totalDiscount,
                negative = true
            )
            Divider()
            AdventurePriceRow("Total", viewModel.effectiveTotal(slot), bold = true)
        } else {
            AdventurePriceRow("Aventura estimada", viewModel.estimatedAdventureSubtotal)
            AdventurePriceRow("Comida estimada", viewModel.estimatedFoodSubtotal)
            AdventurePriceRow(
                "Descuento estimado",
                viewModel.estimatedDiscountAmount,
                negative = true
            )
            Divider()
            AdventurePriceRow("Total estimado", viewModel.estimatedTotal, bold = true)
        }
        viewModel.activeRewardPresentations.forEach { reward ->
            Text(
                "${reward.badge}: ${reward.message}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CounterRow(title: String, value: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$title: $value", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onDecrease) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = "Menos"
            )
        }
        IconButton(onClick = onIncrease) { Icon(Icons.Rounded.Add, contentDescription = "Más") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    title: String,
    current: T,
    values: List<T>,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = label(current),
            onValueChange = {},
            readOnly = true,
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEach { value ->
                DropdownMenuItem(text = { Text(label(value)) }, onClick = {
                    onSelected(value)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun AdventureItemEditorDialog(
    item: AdventureReservationItemDraft,
    config: AdventureActivityCatalogItem?,
    onDismiss: () -> Unit,
    onSave: (AdventureReservationItemDraft) -> Unit
) {
    var draft by remember(item.id) { mutableStateOf(item) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(draft) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(config?.title ?: item.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (item.activity != AdventureActivityType.CAMPING) {
                    DurationSelector(
                        draft = draft,
                        options = config?.durationOptions ?: item.activity.legacyDurationOptions,
                        onChanged = { draft = draft.copy(durationMinutes = it) })
                }
                when (item.activity) {
                    AdventureActivityType.OFF_ROAD -> {
                        CounterRow(
                            "Vehículos",
                            draft.vehicleCount,
                            {
                                draft = draft.copy(
                                    vehicleCount = (draft.vehicleCount - 1).coerceAtLeast(1),
                                    offRoadRiderCount = draft.offRoadRiderCount.coerceAtMost(
                                        ((draft.vehicleCount - 1).coerceAtLeast(1)) * 2
                                    )
                                )
                            },
                            { draft = draft.copy(vehicleCount = draft.vehicleCount + 1) })
                        CounterRow(
                            "Personas",
                            draft.offRoadRiderCount,
                            {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount - 1).coerceAtLeast(
                                        1
                                    )
                                )
                            },
                            {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount + 1).coerceAtMost(
                                        draft.vehicleCount * 2
                                    )
                                )
                            })
                    }

                    AdventureActivityType.CAMPING -> {
                        CounterRow(
                            "Noches",
                            draft.nights,
                            { draft = draft.copy(nights = (draft.nights - 1).coerceAtLeast(1)) },
                            { draft = draft.copy(nights = draft.nights + 1) })
                        CounterRow(
                            "Personas",
                            draft.peopleCount,
                            {
                                draft =
                                    draft.copy(peopleCount = (draft.peopleCount - 1).coerceAtLeast(1))
                            },
                            { draft = draft.copy(peopleCount = draft.peopleCount + 1) })
                    }

                    else -> CounterRow(
                        "Personas",
                        draft.peopleCount,
                        {
                            draft =
                                draft.copy(peopleCount = (draft.peopleCount - 1).coerceAtLeast(1))
                        },
                        { draft = draft.copy(peopleCount = draft.peopleCount + 1) })
                }
            }
        },
    )
}

@Composable
private fun DurationSelector(
    draft: AdventureReservationItemDraft,
    options: List<Int>,
    onChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.distinct().sorted().forEach { minutes ->
            FilterChip(
                selected = draft.durationMinutes == minutes,
                onClick = { onChanged(minutes) },
                label = { Text(if (minutes >= 60) "${minutes / 60}h" else "$minutes min") })
        }
    }
}

@Composable
private fun FoodItemEditorDialog(
    item: ReservationFoodItemDraft,
    onDismiss: () -> Unit,
    onSave: (ReservationFoodItemDraft) -> Unit
) {
    var draft by remember(item.id) { mutableStateOf(item) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(draft) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar comida") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(draft.name, fontWeight = FontWeight.Bold)
                CounterRow(
                    "Cantidad",
                    draft.quantity,
                    { draft = draft.copy(quantity = (draft.quantity - 1).coerceAtLeast(1)) },
                    { draft = draft.copy(quantity = draft.quantity + 1) })
                OutlinedTextField(
                    value = draft.notes.orEmpty(),
                    onValueChange = {
                        draft = draft.copy(notes = it.trim().takeIf { value -> value.isNotEmpty() })
                    },
                    minLines = 2,
                    label = { Text("Notas") })
            }
        },
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/view/AdventureUiComponents.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view


@Composable
fun AdventureGradientHero(
    title: String,
    subtitle: String,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .padding(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AdventureIconBubble(
                        icon = Icons.Rounded.Terrain,
                        contentColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.18f),
                    )
                    Spacer(Modifier.weight(1f))
                    AdventureBadge(text = "Outdoor", selected = true)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.92f),
                )
                action()
            }
        }
    }
}

@Composable
fun AdventureSectionTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AdventureCard(
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (emphasized) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (emphasized) 4.dp else 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            content()
        }
    }
}

@Composable
fun AdventureIconBubble(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(25.dp),
        )
    }
}

@Composable
fun AdventureBadge(
    text: String,
    selected: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun AdventurePriceRow(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    negative: Boolean = false,
    bold: Boolean = false,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = if (negative) "-${amount.priceText()}" else amount.priceText(),
            style = if (bold) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

fun adventureIconFor(activity: AdventureActivityType): ImageVector = when (activity) {
    AdventureActivityType.OFF_ROAD -> Icons.Rounded.DirectionsCar
    AdventureActivityType.PAINTBALL -> Icons.Rounded.SportsKabaddi
    AdventureActivityType.GO_KARTS -> Icons.Rounded.Flag
    AdventureActivityType.SHOOTING_RANGE -> Icons.Rounded.MyLocation
    AdventureActivityType.CAMPING -> Icons.Rounded.Forest
    AdventureActivityType.EXTREME_SLIDE -> Icons.Rounded.Terrain
}

@Composable
fun AdventureEmptyState(
    title: String,
    body: String,
    icon: ImageVector = Icons.Rounded.CalendarMonth,
) {
    AdventureCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            AdventureIconBubble(icon = icon)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/viewmodel/AdventureCatalogUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel


data class AdventureCatalogUiState(
    val isLoading: Boolean = true,
    val catalog: AdventureCatalogSnapshot = AdventureCatalogSnapshot.EMPTY,
    val errorMessage: String? = null,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/viewmodel/AdventureCatalogViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel


@HiltViewModel
class AdventureCatalogViewModel @Inject constructor(
    private val fetchAdventureCatalogUseCase: FetchAdventureCatalogUseCase,
    private val observeAdventureCatalogUseCase: ObserveAdventureCatalogUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureCatalogUiState())
    val uiState: StateFlow<AdventureCatalogUiState> = _uiState.asStateFlow()

    private var catalogJob: Job? = null
    private var refreshJob: Job? = null

    fun onAppear() {
        if (catalogJob?.isActive == true) return

        catalogJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            observeAdventureCatalogUseCase.execute()
                .catch { error ->
                    if (error is CancellationException) throw error

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudo cargar el catálogo de aventura.",
                        )
                    }
                }
                .collectLatest { catalog ->
                    _uiState.update {
                        it.copy(
                            catalog = catalog,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun onDisappear() {
        catalogJob?.cancel()
        catalogJob = null

        refreshJob?.cancel()
        refreshJob = null
    }

    fun refresh() {
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching {
                fetchAdventureCatalogUseCase.execute()
            }.onSuccess { catalog ->
                _uiState.update {
                    it.copy(
                        catalog = catalog,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "No se pudo actualizar el catálogo de aventura.",
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/viewmodel/AdventureComboBuilderUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel


data class AdventureComboBuilderUiState(
    val selectedDate: Date = Date(),
    val items: List<AdventureReservationItemDraft> = emptyList(),
    val guestCount: Int = 2,
    val eventType: ReservationEventType = ReservationEventType.REGULAR_VISIT,
    val customEventTitle: String = "",
    val eventNotes: String = "",
    val foodItems: List<ReservationFoodItemDraft> = emptyList(),
    val foodServingMoment: ReservationServingMoment = ReservationServingMoment.AFTER_ACTIVITIES,
    val foodServingTime: Date = Date(),
    val foodNotes: String = "",
    val clientName: String = "",
    val whatsappNumber: String = "",
    val nationalId: String = "",
    val notes: String = "",
    val packageDiscountAmount: Double = 0.0,
    val catalog: AdventureCatalogSnapshot = AdventureCatalogSnapshot.EMPTY,
    val availableSlots: List<AdventureAvailabilitySlot> = emptyList(),
    val selectedSlot: AdventureAvailabilitySlot? = null,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(RewardWalletSnapshot.empty("")),
    val isLoadingCatalog: Boolean = false,
    val isLoadingAvailability: Boolean = false,
    val isLoadingRewards: Boolean = false,
    val isSubmitting: Boolean = false,
    val createdBooking: AdventureBooking? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/viewmodel/AdventureComboBuilderViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AdventureComboBuilderViewModel @Inject constructor(
    private val getAvailabilityUseCase: GetAdventureAvailabilityUseCase,
    private val createBookingUseCase: CreateAdventureBookingUseCase,
    private val observeAdventureCatalogUseCase: ObserveAdventureCatalogUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureComboBuilderUiState())
    val uiState: StateFlow<AdventureComboBuilderUiState> = _uiState.asStateFlow()

    private val rewardPreviewRequests = MutableStateFlow<RewardPreviewInput?>(null)
    private var catalogJob: Job? = null
    private var availabilityJob: Job? = null
    private var rewardPreviewJob: Job? = null

    init {
        startRewardPreviewLoop()
    }

    fun onAppear(profile: ClientProfile? = null) {
        profile?.let(::syncProfile)
        startCatalogObservationIfNeeded()
    }

    fun onDisappear() {
        catalogJob?.cancel()
        catalogJob = null
    }

    fun syncProfile(profile: ClientProfile) {
        _uiState.update {
            it.copy(
                clientName = profile.fullName,
                whatsappNumber = profile.phoneNumber,
                nationalId = profile.nationalId.filter(Char::isDigit),
            )
        }
        requestRewardPreview()
    }

    fun setDate(date: Date) {
        val startOfDay = AdventureDateHelper.startOfDay(date)
        _uiState.update {
            it.copy(
                selectedDate = startOfDay,
                selectedSlot = null,
            )
        }
        refreshAvailability()
    }

    fun setGuestCount(value: Int) = updateState { copy(guestCount = value.coerceIn(1, 300)) }
    fun setEventType(value: ReservationEventType) = updateState { copy(eventType = value) }
    fun setCustomEventTitle(value: String) = updateState { copy(customEventTitle = value) }
    fun setEventNotes(value: String) = updateState { copy(eventNotes = value) }
    fun setFoodServingMoment(value: ReservationServingMoment) =
        updateState { copy(foodServingMoment = value) }

    fun setFoodServingTime(value: Date) = updateState { copy(foodServingTime = value) }
    fun setFoodNotes(value: String) = updateState { copy(foodNotes = value) }
    fun setNotes(value: String) = updateState { copy(notes = value) }
    fun setClientName(value: String) = updateState { copy(clientName = value) }
    fun setWhatsapp(value: String) = updateState { copy(whatsappNumber = value) }

    fun setNationalId(value: String) {
        updateState { copy(nationalId = value.filter(Char::isDigit)) }
        requestRewardPreview()
    }

    fun prepareCustomDraftIfNeeded() {
        val state = _uiState.value
        if (state.items.isEmpty() && state.foodItems.isEmpty()) {
            val firstActivity = state.catalog.activeActivitiesSorted.firstOrNull()
            if (firstActivity != null) {
                replaceItems(listOf(firstActivity.defaultDraft), 0.0)
            } else {
                refreshAvailability()
            }
        }
    }

    fun replaceItems(
        items: List<AdventureReservationItemDraft>,
        packageDiscountAmount: Double,
    ) {
        _uiState.update {
            it.copy(
                items = items,
                packageDiscountAmount = packageDiscountAmount.coerceAtLeast(0.0),
                selectedSlot = null,
                createdBooking = null,
                successMessage = null,
                errorMessage = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    fun replacePackage(
        packageModel: AdventureFeaturedPackage,
        menuSections: List<MenuSection>,
    ) {
        val foodItems = packageModel.foodItems.mapNotNull { food ->
            val menuItem =
                menuSections.flatMap { it.items }.firstOrNull { it.id == food.menuItemId }
                    ?: return@mapNotNull null
            ReservationFoodItemDraft(menuItem = menuItem, quantity = food.quantity)
        }

        _uiState.update {
            it.copy(
                items = packageModel.items,
                foodItems = foodItems,
                packageDiscountAmount = packageModel.packageDiscountAmount.coerceAtLeast(0.0),
                selectedSlot = null,
                createdBooking = null,
                successMessage = null,
                errorMessage = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    val availableActivitiesToAdd: List<AdventureActivityCatalogItem>
        get() {
            val selected = _uiState.value.items.map { it.activity }.toSet()
            return _uiState.value.catalog.activeActivitiesSorted.filterNot { it.activityType in selected }
        }

    fun addItem(activity: AdventureActivityType) {
        val state = _uiState.value
        if (state.items.any { it.activity == activity }) return
        val draft = AdventureActivityType.defaultDraft(activity, state.catalog)
        updateItems(state.items + draft)
    }

    fun updateItem(updated: AdventureReservationItemDraft) {
        val current = _uiState.value.items
        updateItems(current.map { if (it.id == updated.id) updated else it })
    }

    fun removeItem(itemId: String) {
        updateItems(_uiState.value.items.filterNot { it.id == itemId })
    }

    fun removeItemAt(index: Int) {
        val current = _uiState.value.items.toMutableList()
        if (index !in current.indices) return
        current.removeAt(index)
        updateItems(current)
    }

    fun moveItem(from: Int, to: Int) {
        val current = _uiState.value.items.toMutableList()
        if (from !in current.indices || to !in 0..current.size) return
        val item = current.removeAt(from)
        current.add(if (to > from) to - 1 else to, item)
        updateItems(current)
    }

    fun addFoodItem(
        menuItem: MenuItem,
        quantity: Int,
        notes: String?,
        selectedDate: Date = _uiState.value.selectedDate,
    ) {
        if (AdventureDateHelper.sameDay(selectedDate, Date()) && !menuItem.canBeOrdered) {
            presentError("Por hoy, ${menuItem.name} está agotado y no se puede pedir. Elige otro día para reservarlo.")
            return
        }

        val cleanNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
        val current = _uiState.value.foodItems.toMutableList()
        val index =
            current.indexOfFirst { it.menuItemId == menuItem.id && it.notes.orEmpty() == cleanNotes.orEmpty() }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity.coerceAtLeast(1))
        } else {
            current.add(
                ReservationFoodItemDraft(
                    menuItem = menuItem,
                    quantity = quantity,
                    notes = cleanNotes
                )
            )
        }
        updateFoodItems(current)
    }

    fun updateFoodItem(updated: ReservationFoodItemDraft) {
        val current = _uiState.value.foodItems.map {
            if (it.id == updated.id) updated.copy(
                quantity = updated.quantity.coerceAtLeast(1)
            ) else it
        }
        updateFoodItems(current)
    }

    fun increaseFoodQuantity(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.map { if (it.id == itemId) it.copy(quantity = it.quantity + 1) else it })
    }

    fun decreaseFoodQuantity(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.map { item ->
            if (item.id == itemId) item.copy(quantity = (item.quantity - 1).coerceAtLeast(1)) else item
        })
    }

    fun removeFoodItem(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.filterNot { it.id == itemId })
    }

    fun selectSlot(slot: AdventureAvailabilitySlot) {
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    fun submit(clientId: String?) {
        val state = _uiState.value
        val selectedSlot = state.selectedSlot
        val validationMessage = validateBeforeSubmit(state)
        if (validationMessage != null) {
            presentError(validationMessage)
            return
        }
        if (selectedSlot == null) {
            presentError("Selecciona un horario disponible antes de confirmar.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val request = AdventureBookingRequest(
                clientId = clientId,
                clientName = state.clientName.trim(),
                whatsappNumber = state.whatsappNumber.trim(),
                nationalId = state.nationalId.filter(Char::isDigit),
                date = state.selectedDate,
                selectedStartAt = selectedSlot.startAt,
                guestCount = state.guestCount.coerceAtLeast(1),
                eventType = state.eventType,
                customEventTitle = state.customEventTitle.trim().takeIf { it.isNotEmpty() },
                eventNotes = state.eventNotes.trim().takeIf { it.isNotEmpty() },
                items = state.items,
                foodReservation = buildFoodDraft(state),
                packageDiscountAmount = state.packageDiscountAmount.coerceAtLeast(0.0),
                loyaltyDiscountAmount = state.rewardPreview.totalDiscount,
                appliedRewards = state.rewardPreview.appliedRewards,
                notes = state.notes.trim().takeIf { it.isNotEmpty() },
            )

            runCatching {
                createBookingUseCase.execute(request)
            }.onSuccess { booking ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        createdBooking = booking,
                        successMessage = "Reserva enviada. Te confirmaremos pronto.",
                        selectedSlot = null,
                    )
                }
                refreshAvailability()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "No se pudo crear la reserva.",
                    )
                }
            }
        }
    }

    fun dismissMessage() = updateState { copy(errorMessage = null, successMessage = null) }

    fun presentError(message: String) {
        _uiState.update { it.copy(errorMessage = message, successMessage = null) }
    }

    fun config(activity: AdventureActivityType): AdventureActivityCatalogItem? =
        _uiState.value.catalog.activity(activity)

    val estimatedAdventureSubtotal: Double
        get() = AdventurePricingEngine.estimatedSubtotal(
            _uiState.value.items,
            _uiState.value.catalog
        )

    val estimatedFoodSubtotal: Double
        get() = _uiState.value.foodItems.sumOf { it.subtotal }.adventureRoundMoney()

    val estimatedDiscountAmount: Double
        get() {
            val state = _uiState.value
            val activityDiscount =
                AdventurePricingEngine.estimatedDiscountAmount(state.items, state.catalog)
            return (activityDiscount + state.packageDiscountAmount + state.rewardPreview.totalDiscount).adventureRoundMoney()
        }

    val estimatedTotal: Double
        get() {
            val state = _uiState.value
            val plan = AdventurePlanner.buildPlan(
                day = state.selectedDate,
                startAt = AdventureDateHelper.dateOn(state.selectedDate, 7, 0),
                items = state.items,
                foodReservation = buildFoodDraft(state),
                packageDiscountAmount = state.packageDiscountAmount,
                catalog = state.catalog,
            )
            val base = plan?.totalAmount
                ?: (estimatedAdventureSubtotal + estimatedFoodSubtotal - state.packageDiscountAmount).coerceAtLeast(
                    0.0
                )
            return (base - state.rewardPreview.totalDiscount).coerceAtLeast(0.0)
                .adventureRoundMoney()
        }

    val activeRewardPresentations: List<RewardPresentation>
        get() = _uiState.value.rewardPreview.appliedRewards.map {
            RewardPresentation.fromAppliedReward(
                it
            )
        }

    fun effectiveTotal(slot: AdventureAvailabilitySlot): Double =
        (slot.totalAmount - _uiState.value.rewardPreview.totalDiscount).coerceAtLeast(0.0)
            .adventureRoundMoney()

    fun baseAdventureSubtotal(item: AdventureReservationItemDraft): Double =
        _uiState.value.catalog.activity(item.activity)?.let { config ->
            AdventurePricingEngine.lineBaseSubtotal(item, config)
        } ?: 0.0

    fun displayedAdventureSubtotal(item: AdventureReservationItemDraft): Double {
        val raw = AdventurePricingEngine.subtotal(item, _uiState.value.catalog)
        val reward = rewardAmountForActivity(item.activity)
        return (raw - reward).coerceAtLeast(0.0).adventureRoundMoney()
    }

    fun displayedFoodSubtotal(item: ReservationFoodItemDraft): Double =
        (item.subtotal - rewardAmount(item)).coerceAtLeast(0.0).adventureRoundMoney()

    fun rewardAmount(item: ReservationFoodItemDraft): Double =
        _uiState.value.rewardPreview.appliedRewards
            .filter { reward -> reward.affectedMenuItemIds.contains(item.menuItemId) }
            .sumOf { it.amount }
            .adventureRoundMoney()

    fun appliedRewardPresentation(item: AdventureReservationItemDraft): RewardPresentation? =
        _uiState.value.rewardPreview.appliedRewards
            .firstOrNull { reward -> reward.affectedActivityIds.contains(item.activity.rawValue) }
            ?.let(RewardPresentation::fromAppliedReward)

    fun appliedRewardPresentation(item: ReservationFoodItemDraft): RewardPresentation? =
        _uiState.value.rewardPreview.appliedRewards
            .firstOrNull { reward -> reward.affectedMenuItemIds.contains(item.menuItemId) }
            ?.let(RewardPresentation::fromAppliedReward)

    fun catalogRewardPresentation(activity: AdventureActivityCatalogItem): RewardPresentation? =
        RewardPresentationFactory.activityPresentation(
            activity = activity,
            wallet = _uiState.value.rewardPreview.walletSnapshot,
        )

    fun packageRewardPresentation(
        packageModel: AdventureFeaturedPackage,
        menuSections: List<MenuSection>,
    ): RewardPresentation? = RewardPresentationFactory.packagePresentation(
        packageModel = packageModel,
        catalog = _uiState.value.catalog,
        menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id },
        wallet = _uiState.value.rewardPreview.walletSnapshot,
    )

    fun foodPickerRewardPresentation(item: MenuItem, quantity: Int): RewardPresentation? {
        val projected = projectedRewardResult(item, quantity)
        projected.appliedRewards.firstOrNull { reward -> reward.affectedMenuItemIds.contains(item.id) }
            ?.let { return RewardPresentation.fromAppliedReward(it) }
        return RewardPresentationFactory.adventureMenuPresentation(item, projected.walletSnapshot)
    }

    fun foodPickerDisplayedPrice(item: MenuItem, quantity: Int): Double {
        val subtotal = item.finalPrice * quantity.coerceAtLeast(1)
        return (subtotal - foodPickerIncrementalDiscount(item, quantity)).coerceAtLeast(0.0)
            .adventureRoundMoney()
    }

    fun foodPickerIncrementalDiscount(item: MenuItem, quantity: Int): Double =
        projectedRewardResult(item, quantity).totalDiscount.adventureRoundMoney()

    private fun updateItems(items: List<AdventureReservationItemDraft>) {
        val correctedDiscount = bestMatchingPackageDiscount(items)
        _uiState.update {
            it.copy(
                items = items,
                packageDiscountAmount = correctedDiscount,
                selectedSlot = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    private fun updateFoodItems(foodItems: List<ReservationFoodItemDraft>) {
        _uiState.update {
            it.copy(
                foodItems = foodItems,
                selectedSlot = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    private fun bestMatchingPackageDiscount(items: List<AdventureReservationItemDraft>): Double {
        if (items.size <= 1) return 0.0
        val state = _uiState.value
        val activityKey = items
            .map { keyForActivityPackageMatch(it) }
            .sorted()
        return state.catalog.activePackagesSorted
            .filter { it.items.size > 1 }
            .firstOrNull { packageModel ->
                packageModel.items.map { keyForActivityPackageMatch(it) }.sorted() == activityKey
            }
            ?.packageDiscountAmount
            ?.coerceAtLeast(0.0)
            ?: 0.0
    }

    private fun keyForActivityPackageMatch(item: AdventureReservationItemDraft): String =
        listOf(
            item.activity.rawValue,
            item.durationMinutes,
            item.peopleCount,
            item.vehicleCount,
            item.offRoadRiderCount,
            item.nights,
        ).joinToString("|")

    private fun refreshAvailability() {
        val state = _uiState.value
        val hasFood = state.foodItems.isNotEmpty()
        if (state.items.isEmpty() && !hasFood) {
            availabilityJob?.cancel()
            _uiState.update {
                it.copy(
                    availableSlots = emptyList(),
                    selectedSlot = null,
                    isLoadingAvailability = false
                )
            }
            return
        }

        availabilityJob?.cancel()
        availabilityJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAvailability = true, errorMessage = null) }
            delay(120)
            runCatching {
                getAvailabilityUseCase.execute(
                    date = _uiState.value.selectedDate,
                    items = _uiState.value.items,
                    foodReservation = buildFoodDraft(_uiState.value),
                    packageDiscountAmount = _uiState.value.packageDiscountAmount,
                )
            }.onSuccess { slots ->
                _uiState.update { current ->
                    val selected = current.selectedSlot?.let { previous ->
                        slots.firstOrNull { it.startAt == previous.startAt && it.endAt == previous.endAt }
                    }
                    current.copy(
                        availableSlots = slots,
                        selectedSlot = selected,
                        isLoadingAvailability = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingAvailability = false,
                        availableSlots = emptyList(),
                        selectedSlot = null,
                        errorMessage = error.message ?: "No se pudo verificar disponibilidad.",
                    )
                }
            }
        }
    }

    private fun startCatalogObservationIfNeeded() {
        if (catalogJob?.isActive == true) return
        catalogJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCatalog = true, errorMessage = null) }
            observeAdventureCatalogUseCase.execute()
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoadingCatalog = false,
                            errorMessage = error.message ?: "No se pudo cargar aventura.",
                        )
                    }
                }
                .collectLatest { catalog ->
                    _uiState.update { state ->
                        val validItems =
                            state.items.filter { catalog.activity(it.activity)?.isActive == true }
                        state.copy(
                            catalog = catalog,
                            items = validItems,
                            packageDiscountAmount = if (validItems.size > 1) state.packageDiscountAmount else 0.0,
                            isLoadingCatalog = false,
                            errorMessage = null,
                        )
                    }
                    requestRewardPreview()
                    refreshAvailability()
                }
        }
    }

    private fun startRewardPreviewLoop() {
        rewardPreviewJob?.cancel()
        rewardPreviewJob = viewModelScope.launch {
            rewardPreviewRequests
                .filter { it != null }
                .map { requireNotNull(it) }
                .distinctUntilChanged()
                .debounce(180)
                .collectLatest { input ->
                    if (input.nationalId.isBlank()) {
                        _uiState.update {
                            it.copy(
                                rewardPreview = RewardComputationResult.empty(
                                    RewardWalletSnapshot.empty("")
                                )
                            )
                        }
                        return@collectLatest
                    }

                    _uiState.update { it.copy(isLoadingRewards = true) }
                    runCatching {
                        loyaltyRewardsRepository.previewAdventureRewards(
                            nationalId = input.nationalId,
                            activityItems = input.activityItems,
                            foodItems = input.foodItems,
                            catalog = input.catalog,
                        )
                    }.onSuccess { result ->
                        _uiState.update {
                            it.copy(
                                rewardPreview = result,
                                isLoadingRewards = false,
                            )
                        }
                    }.onFailure { error ->
                        if (error is CancellationException) throw error
                        _uiState.update {
                            it.copy(
                                rewardPreview = RewardComputationResult.empty(
                                    RewardWalletSnapshot.empty(
                                        input.nationalId
                                    )
                                ),
                                isLoadingRewards = false,
                            )
                        }
                    }
                }
        }
    }

    private fun requestRewardPreview() {
        val state = _uiState.value
        rewardPreviewRequests.value = RewardPreviewInput(
            nationalId = state.nationalId.filter(Char::isDigit),
            activityItems = state.items,
            foodItems = state.foodItems,
            catalog = state.catalog,
        )
    }

    private fun projectedRewardResult(item: MenuItem, quantity: Int): RewardComputationResult {
        val state = _uiState.value
        val wallet = state.rewardPreview.walletSnapshot
        val projectedItems =
            state.foodItems + ReservationFoodItemDraft(menuItem = item, quantity = quantity)

        val activityLines = state.items.mapNotNull { draft ->
            val config = state.catalog.activity(draft.activity) ?: return@mapNotNull null
            RewardActivityLine(
                activityId = draft.activity.rawValue,
                title = config.title,
                linePrice = AdventurePricingEngine.subtotal(draft, state.catalog),
            )
        }

        val foodLines = projectedItems.map { food ->
            RewardMenuLine(
                menuItemId = food.menuItemId,
                name = food.name,
                unitPrice = food.unitPrice,
                quantity = food.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateAdventure(
            templates = wallet.availableTemplates,
            wallet = wallet,
            activityLines = activityLines,
            foodLines = foodLines,
        )
    }

    private fun rewardAmountForActivity(activity: AdventureActivityType): Double =
        _uiState.value.rewardPreview.appliedRewards
            .filter { reward -> reward.affectedActivityIds.contains(activity.rawValue) }
            .sumOf { it.amount }
            .adventureRoundMoney()

    private fun buildFoodDraft(state: AdventureComboBuilderUiState): ReservationFoodDraft? {
        if (state.foodItems.isEmpty()) return null
        return ReservationFoodDraft(
            items = state.foodItems,
            servingMoment = state.foodServingMoment,
            servingTime = if (state.foodServingMoment == ReservationServingMoment.SPECIFIC_TIME) state.foodServingTime else null,
            notes = state.foodNotes.trim().takeIf { it.isNotEmpty() },
        )
    }

    private fun validateBeforeSubmit(state: AdventureComboBuilderUiState): String? {
        if (state.items.isEmpty() && state.foodItems.isEmpty()) return "Agrega al menos una actividad o comida."
        if (state.clientName.trim().isEmpty()) return "Tu perfil no tiene nombre registrado."
        if (state.whatsappNumber.trim().isEmpty()) return "Tu perfil no tiene WhatsApp registrado."
        if (state.nationalId.filter(Char::isDigit)
                .isEmpty()
        ) return "Tu perfil no tiene cédula registrada."
        if (state.eventType == ReservationEventType.CUSTOM && state.customEventTitle.trim()
                .isEmpty()
        ) {
            return "Indica el nombre del evento personalizado."
        }
        return null
    }

    private inline fun updateState(transform: AdventureComboBuilderUiState.() -> AdventureComboBuilderUiState) {
        _uiState.update(transform)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/viewmodel/RewardPreviewInput.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel


data class RewardPreviewInput(
    val nationalId: String,
    val activityItems: List<AdventureReservationItemDraft>,
    val foodItems: List<ReservationFoodItemDraft>,
    val catalog: AdventureCatalogSnapshot,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/DeveloperBypassSessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class DeveloperBypassSessionRepository @Inject constructor() : SessionRepositoriable {
    override fun sessionState(): Flow<SessionState> {
        return if (BuildConfig.DEBUG) {
            flowOf(
                SessionState.Authenticated(
                    profile = ClientProfile(
                        id = "developer-preview",
                        email = "developer@preview.local",
                        appleUserIdentifier = "",
                        fullName = "Developer Preview",
                        nationalId = "0000000000",
                        phoneNumber = "0000000000",
                        birthday = Date(),
                        address = "Preview",
                        emergencyContactName = "Preview",
                        emergencyContactPhone = "0000000000",
                        isProfileComplete = true,
                        createdAt = Date(),
                        updatedAt = Date(),
                        profileCompletedAt = Date(),
                        profileImageURL = null,
                        profileImagePath = null,
                    ),
                    developerBypass = true,
                ),
            )
        } else {
            flowOf(SessionState.Unauthenticated)
        }
    }

    override suspend fun refresh() = Unit
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/FirebaseAuthenticationRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class FirebaseAuthenticationRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthenticationRepositoriable {

    override fun currentUser(): AuthenticatedUser? {
        val user = auth.currentUser ?: return null
        val googleProviderUid =
            user.providerData.firstOrNull { it.providerId == GoogleAuthProvider.PROVIDER_ID }?.uid.orEmpty()

        return AuthenticatedUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty(),
            appleUserIdentifier = googleProviderUid,
        )
    }

    override suspend fun signInWithGoogle(
        googleIdToken: String,
    ): AuthenticatedUser {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val authResult = auth.signInWithCredential(credential).awaitResult()
        val firebaseUser =
            requireNotNull(authResult.user) { "Firebase auth returned a null user after Google sign in." }
        val googleProviderUid =
            firebaseUser.providerData.firstOrNull { it.providerId == GoogleAuthProvider.PROVIDER_ID }?.uid.orEmpty()

        return AuthenticatedUser(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.displayName.orEmpty(),
            appleUserIdentifier = googleProviderUid,
        )
    }

    override suspend fun reauthenticateCurrentUser(
        googleIdToken: String,
    ) {
        val user = requireNotNull(auth.currentUser) { "No authenticated user found." }
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        user.reauthenticate(credential).awaitResult()
    }

    override suspend fun deleteCurrentUser() {
        val user = requireNotNull(auth.currentUser) { "No authenticated user to delete." }
        user.delete().awaitResult()
    }

    override fun signOut() {
        auth.signOut()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/FirestoreClientProfileRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class FirestoreClientProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ClientProfileRepositoriable {

    private val collection = firestore.collection(FirestoreCollections.CLIENTS)

    companion object {
        private const val TAG = "AltosProfileRepo"
    }

    override suspend fun fetchProfile(uid: String): ClientProfile? {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) {
            Log.d(TAG, "fetchProfile -> empty uid")
            return null
        }

        Log.d(TAG, "fetchProfile -> requesting clients/$cleanUid")

        val snapshot = collection.document(cleanUid).get().awaitResult()

        Log.d(
            TAG,
            "fetchProfile -> snapshot exists=${snapshot.exists()}, id=${snapshot.id}, keys=${snapshot.data?.keys?.sorted()}"
        )

        if (!snapshot.exists()) {
            Log.d(TAG, "fetchProfile -> document does not exist for uid=$cleanUid")
            return null
        }

        val profile = snapshot.toClientProfileOrNull()

        Log.d(
            TAG,
            "fetchProfile -> mapped profile null=${profile == null}, " +
                    "id=${profile?.id}, " +
                    "email=${profile?.email}, " +
                    "fullName='${profile?.fullName}', " +
                    "nationalId='${profile?.nationalId}', " +
                    "phone='${profile?.phoneNumber}', " +
                    "address='${profile?.address}', " +
                    "emergencyName='${profile?.emergencyContactName}', " +
                    "emergencyPhone='${profile?.emergencyContactPhone}', " +
                    "isProfileComplete=${profile?.isProfileComplete}, " +
                    "isComplete=${profile?.isComplete}"
        )

        return profile
    }

    override suspend fun saveProfile(profile: ClientProfile) {
        Log.d(
            TAG,
            "saveProfile -> writing clients/${profile.id.trim()} " +
                    "email=${profile.email}, " +
                    "fullName='${profile.fullName}', " +
                    "nationalId='${profile.nationalId}', " +
                    "phone='${profile.phoneNumber}', " +
                    "address='${profile.address}', " +
                    "emergencyName='${profile.emergencyContactName}', " +
                    "emergencyPhone='${profile.emergencyContactPhone}', " +
                    "isProfileComplete=${profile.isProfileComplete}, " +
                    "isComplete=${profile.isComplete}"
        )

        collection
            .document(profile.id.trim())
            .set(ClientProfileDocument(profile), SetOptions.merge())
            .awaitResult()

        Log.d(TAG, "saveProfile -> write success clients/${profile.id.trim()}")
    }

    override suspend fun deleteProfile(uid: String) {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return
        collection.document(cleanUid).delete().awaitResult()
    }

    private fun DocumentSnapshot.toClientProfileOrNull(): ClientProfile? {
        return runCatching {
            ClientProfile(
                id = id.trim(),
                email = getString("email").orEmpty().trim(),
                appleUserIdentifier = getString("appleUserIdentifier").orEmpty().trim(),
                fullName = getString("fullName").orEmpty().trim(),
                nationalId = getString("nationalId").orEmpty().trim(),
                phoneNumber = getString("phoneNumber").orEmpty().trim(),
                birthday = getDateValue("birthday") ?: Date(0),
                address = getString("address").orEmpty().trim(),
                emergencyContactName = getString("emergencyContactName").orEmpty().trim(),
                emergencyContactPhone = getString("emergencyContactPhone").orEmpty().trim(),
                isProfileComplete = getBoolean("profileComplete") == true,
                createdAt = getDateValue("createdAt") ?: Date(),
                updatedAt = getDateValue("updatedAt") ?: Date(),
                profileCompletedAt = getDateValue("profileCompletedAt"),
                profileImageURL = getString("profileImageURL")?.trim()?.takeIf { it.isNotEmpty() },
                profileImagePath = getString("profileImagePath")?.trim()?.takeIf { it.isNotEmpty() },
            )
        }.onFailure { error ->
            Log.e(TAG, "toClientProfileOrNull -> mapping failed for docId=$id", error)
        }.getOrNull()
    }

    private fun DocumentSnapshot.getDateValue(field: String): Date? {
        return when (val value = get(field)) {
            is Timestamp -> value.toDate()
            is Date -> value
            else -> null
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/SessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class SessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authenticationRepository: AuthenticationRepositoriable,
    private val resolveSessionUseCase: ResolveSessionUseCase,
) : SessionRepositoriable {

    private val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    companion object {
        private const val TAG = "AltosSession"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun sessionState(): Flow<SessionState> {
        val authChanges = authUserFlow().map { Unit }
        val manualRefreshes = refreshRequests.onStart { emit(Unit) }

        return merge(authChanges, manualRefreshes)
            .mapLatest {
                Log.d(
                    TAG,
                    "sessionState() tick -> auth.currentUser.uid=${auth.currentUser?.uid}, email=${auth.currentUser?.email}"
                )
                resolveLatestSessionState()
            }
            .distinctUntilChanged()
    }

    override suspend fun refresh() {
        Log.d(
            TAG,
            "refresh() requested -> current uid before reload=${auth.currentUser?.uid}"
        )

        runCatching {
            auth.currentUser?.reload()?.awaitResult()
        }.onSuccess {
            Log.d(
                TAG,
                "refresh() reload success -> current uid after reload=${auth.currentUser?.uid}"
            )
        }.onFailure { error ->
            Log.e(TAG, "refresh() reload failure", error)
        }

        refreshRequests.emit(Unit)
    }

    private fun authUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Log.d(
                TAG,
                "AuthStateListener -> uid=${firebaseAuth.currentUser?.uid}, email=${firebaseAuth.currentUser?.email}"
            )
            trySend(firebaseAuth.currentUser).isSuccess
        }

        auth.addAuthStateListener(listener)

        Log.d(
            TAG,
            "authUserFlow initial emit -> uid=${auth.currentUser?.uid}, email=${auth.currentUser?.email}"
        )
        trySend(auth.currentUser).isSuccess

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }.distinctUntilChanged()

    private suspend fun resolveLatestSessionState(): SessionState {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Log.d(TAG, "resolveLatestSessionState -> Unauthenticated (firebaseUser=null)")
            return SessionState.Unauthenticated
        }

        Log.d(
            TAG,
            "resolveLatestSessionState -> firebaseUser uid=${firebaseUser.uid}, email=${firebaseUser.email}, displayName=${firebaseUser.displayName}"
        )

        val currentUser = authenticationRepository.currentUser()
            ?: AuthenticatedUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email.orEmpty(),
                displayName = firebaseUser.displayName.orEmpty(),
                appleUserIdentifier = "",
            )

        Log.d(
            TAG,
            "resolveLatestSessionState -> currentUser uid=${currentUser.uid}, email=${currentUser.email}, displayName=${currentUser.displayName}, appleUserIdentifier=${currentUser.appleUserIdentifier}"
        )

        val destination = resolveSessionUseCase.execute(currentUser)

        when (destination) {
            SessionDestination.SignedOut -> {
                Log.d(TAG, "resolveLatestSessionState -> destination=SignedOut")
                return SessionState.Unauthenticated
            }

            is SessionDestination.NeedsProfile -> {
                val profile = destination.profile
                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=NeedsProfile, profileExists=${profile != null}, profileIsComplete=${profile?.isComplete}, profileId=${profile?.id}, profileEmail=${profile?.email}"
                )
                return SessionState.NeedsProfileCompletion(
                    user = destination.user,
                    existingProfile = destination.profile,
                )
            }

            is SessionDestination.Authenticated -> {
                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=Authenticated, profileId=${destination.profile.id}, profileEmail=${destination.profile.email}, profileIsComplete=${destination.profile.isComplete}"
                )
                return SessionState.Authenticated(
                    profile = destination.profile,
                )
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/AuthenticatedUser.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

/**
 * Kept intentionally compatible with the current migrated model layer.
 * The field name remains appleUserIdentifier for now so the Firestore/profile
 * contract does not have to change during migration.
 *
 * Under Google sign-in we store the Google provider UID here when available,
 * otherwise an empty string.
 */
data class AuthenticatedUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val appleUserIdentifier: String,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/AuthenticationRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

interface AuthenticationRepositoriable {
    fun currentUser(): AuthenticatedUser?

    suspend fun signInWithGoogle(
        googleIdToken: String,
    ): AuthenticatedUser

    suspend fun reauthenticateCurrentUser(
        googleIdToken: String,
    )

    suspend fun deleteCurrentUser()

    fun signOut()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/ClientProfileRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


interface ClientProfileRepositoriable {
    suspend fun fetchProfile(uid: String): ClientProfile?
    suspend fun saveProfile(profile: ClientProfile)
    suspend fun deleteProfile(uid: String)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/CompleteClientProfileUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


class CompleteClientProfileUseCase(
    private val repository: ClientProfileRepositoriable,
) {
    suspend fun execute(profile: ClientProfile) {
        repository.saveProfile(profile)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/DeleteCurrentAccountUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class DeleteCurrentAccountUseCase(
    private val authRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) {
    suspend fun execute(
        currentUserId: String,
        googleIdToken: String,
    ) {
        authRepository.reauthenticateCurrentUser(googleIdToken)
        clientProfileRepository.deleteProfile(currentUserId)
        authRepository.deleteCurrentUser()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/ResolveSessionUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class ResolveSessionUseCase(
    private val authRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) {
    suspend fun execute(): SessionDestination {
        val user = authRepository.currentUser() ?: return SessionDestination.SignedOut
        return execute(user)
    }

    suspend fun execute(user: AuthenticatedUser): SessionDestination {
        val profile = clientProfileRepository.fetchProfile(user.uid)

        return when {
            profile == null -> SessionDestination.NeedsProfile(user = user, profile = null)
            profile.isComplete -> SessionDestination.Authenticated(profile)
            else -> SessionDestination.NeedsProfile(user = user, profile = profile)
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SessionDestination.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


sealed interface SessionDestination {
    data object SignedOut : SessionDestination
    data class NeedsProfile(
        val user: AuthenticatedUser,
        val profile: ClientProfile?,
    ) : SessionDestination
    data class Authenticated(
        val profile: ClientProfile,
    ) : SessionDestination
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SessionRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


interface SessionRepositoriable {
    fun sessionState(): Flow<SessionState>
    suspend fun refresh()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SessionState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState

    data class Authenticated(
        val profile: ClientProfile,
        val developerBypass: Boolean = false,
    ) : SessionState {
        val displayName: String get() = profile.fullName
        val userId: String get() = profile.id
    }

    data class NeedsProfileCompletion(
        val user: AuthenticatedUser,
        val existingProfile: ClientProfile?,
    ) : SessionState {
        val userId: String get() = user.uid
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SignInWithGoogleUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class SignInWithGoogleUseCase(
    private val repository: AuthenticationRepositoriable,
) {
    suspend fun execute(
        googleIdToken: String,
    ): AuthenticatedUser = repository.signInWithGoogle(googleIdToken)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SignOutUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class SignOutUseCase(
    private val repository: AuthenticationRepositoriable,
) {
    fun execute() = repository.signOut()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/view/AuthenticationScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view


@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current.findActivityOrNull()

    LaunchedEffect(activity) {
        val currentActivity = activity ?: return@LaunchedEffect
        viewModel.beginAuthorizedAccountsAttempt()
        runGoogleSignIn(
            activity = currentActivity,
            filterByAuthorizedAccounts = true,
            autoSelect = true,
            onToken = viewModel::onGoogleIdTokenReceived,
            onNoCredential = viewModel::finishAuthorizedAccountsAttempt,
            onError = viewModel::onSignInError,
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                ),
                            ),
                        )
                        .padding(24.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.18f), CircleShape)
                                .padding(14.dp),
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Agriculture,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }

                        Text(
                            text = "Bienvenido a Altos del Murco",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                        )

                        Text(
                            text = "Entra con Google para continuar con pedidos, reservas y tu perfil.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.92f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Acceso rápido",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Text(
                        text = if (uiState.isTryingAuthorizedAccounts && !uiState.isSubmitting) {
                            "Buscando una cuenta ya autorizada..."
                        } else {
                            "Usa tu cuenta de Google para mantener sincronizados tus beneficios y reservas."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Button(
                        onClick = {
                            val currentActivity = activity ?: return@Button
                            scope.launch {
                                runGoogleSignIn(
                                    activity = currentActivity,
                                    filterByAuthorizedAccounts = false,
                                    autoSelect = false,
                                    onToken = viewModel::onGoogleIdTokenReceived,
                                    onNoCredential = viewModel::finishAuthorizedAccountsAttempt,
                                    onError = viewModel::onSignInError,
                                )
                            }
                        },
                        enabled = activity != null && !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp),
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Login,
                                contentDescription = null,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (uiState.isSubmitting) "Iniciando sesión..." else "Continuar con Google",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

private suspend fun runGoogleSignIn(
    activity: Activity,
    filterByAuthorizedAccounts: Boolean,
    autoSelect: Boolean,
    onToken: (String) -> Unit,
    onNoCredential: () -> Unit,
    onError: (String) -> Unit,
) {
    val credentialManager = CredentialManager.create(activity)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
        .setAutoSelectEnabled(autoSelect)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCredential.idToken)
            return
        }

        onError("Credential Manager devolvió una credencial no compatible.")
    } catch (_: GetCredentialCancellationException) {
        onNoCredential()
    } catch (_: NoCredentialException) {
        onNoCredential()
    } catch (error: GetCredentialException) {
        if (filterByAuthorizedAccounts) {
            onNoCredential()
        } else {
            onError(error.message ?: "No se pudo iniciar sesión con Google.")
        }
    } catch (error: Exception) {
        onError(error.message ?: "No se pudo iniciar sesión con Google.")
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/view/CompleteProfilePlaceholderScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view


@Composable
fun CompleteProfilePlaceholderScreen(
    userId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Completar perfil",
            style = MaterialTheme.typography.headlineMedium,
        )
        AltosPlaceholderCard(
            title = "Perfil pendiente",
            body = "El usuario $userId necesita completar el perfil. " +
                    "La persistencia real del perfil se implementará en el Módulo 2.",
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/view/CompleteProfileScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view


@Composable
fun CompleteProfileScreen(
    state: SessionState.NeedsProfileCompletion,
    onProfileCompleted: (ClientProfile) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompleteProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "EC"))

    LaunchedEffect(
        state.user.uid,
        state.existingProfile?.updatedAt,
        state.existingProfile?.isProfileComplete,
    ) {
        viewModel.initialize(
            user = state.user,
            existingProfile = state.existingProfile,
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.profileCompleted.collect { profile ->
            onProfileCompleted(profile)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp),
                ) {
                    Button(
                        onClick = viewModel::saveProfile,
                        enabled = uiState.canSubmit && !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = "Guardar y entrar",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderCard(
                title = "Completa tu perfil",
                subtitle = "Cuando guardes correctamente, la app debe entrar al shell principal sin quedarse atrapada aquí.",
            )

            if (uiState.isSaving) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }

            InfoCard(
                icon = Icons.Rounded.CheckCircle,
                title = "Cuenta vinculada",
                subtitle = "Tu cuenta de Google ya está autenticada. Solo faltan tus datos para pedidos, reservas y beneficios.",
            )

            FormCard(title = "Datos principales") {
                LabeledField(icon = Icons.Rounded.Person, title = "Nombre completo") {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Nombre") },
                    )
                }

                LabeledField(icon = Icons.Rounded.Email, title = "Correo") {
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Correo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                }

                LabeledField(icon = Icons.Rounded.Badge, title = "Cédula") {
                    OutlinedTextField(
                        value = uiState.nationalId,
                        onValueChange = viewModel::onNationalIdChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Cédula") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }

                LabeledField(icon = Icons.Rounded.Phone, title = "WhatsApp") {
                    OutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("WhatsApp") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                }

                LabeledField(icon = Icons.Rounded.Cake, title = "Fecha de nacimiento") {
                    TextButton(
                        onClick = {
                            val current = Calendar.getInstance().apply { time = uiState.birthday }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val picked = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    viewModel.onBirthdayChanged(picked.time)
                                },
                                current.get(Calendar.YEAR),
                                current.get(Calendar.MONTH),
                                current.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        },
                    ) {
                        Text("Seleccionar fecha: ${dateFormatter.format(uiState.birthday)}")
                    }
                }

                LabeledField(icon = Icons.Rounded.Home, title = "Dirección") {
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = viewModel::onAddressChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Dirección") },
                        minLines = 2,
                    )
                }
            }

            FormCard(title = "Seguridad y contacto") {
                LabeledField(icon = Icons.Rounded.Shield, title = "Contacto de emergencia") {
                    OutlinedTextField(
                        value = uiState.emergencyContactName,
                        onValueChange = viewModel::onEmergencyContactNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Nombre del contacto") },
                    )
                }

                LabeledField(icon = Icons.Rounded.Phone, title = "Teléfono de emergencia") {
                    OutlinedTextField(
                        value = uiState.emergencyContactPhone,
                        onValueChange = viewModel::onEmergencyContactPhoneChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Teléfono del contacto") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "No se pudo continuar",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HeaderCard(title: String, subtitle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FormCard(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun LabeledField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                content()
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Divider()
        Spacer(modifier = Modifier.height(14.dp))
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/view/SignInPlaceholderScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view


@Composable
fun SignInPlaceholderScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Acceso",
            style = MaterialTheme.typography.headlineMedium,
        )
        AltosPlaceholderCard(
            title = "Módulo 2 pendiente",
            body = "Aquí conectaremos Firebase Auth y Sign in with Apple para Android. " +
                    "En release, el shell se detiene aquí hasta que el módulo real reemplace este placeholder.",
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AccountActionsViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


data class AccountActionsUiState(
    val isBusy: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AccountActionsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val deleteCurrentAccountUseCase: DeleteCurrentAccountUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountActionsUiState())
    val uiState: StateFlow<AccountActionsUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            runCatching {
                signOutUseCase.execute()
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Could not sign out.") }
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun deleteAccount(
        currentUserId: String,
        freshGoogleIdToken: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            runCatching {
                deleteCurrentAccountUseCase.execute(
                    currentUserId = currentUserId,
                    googleIdToken = freshGoogleIdToken,
                )
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Could not delete account."
                    )
                }
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AuthGateRoute.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


@Composable
fun AuthGateRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthGateViewModel = hiltViewModel(),
    authenticatedContent: @Composable (SessionState.Authenticated) -> Unit,
) {
    val sessionState = viewModel.sessionState.collectAsStateWithLifecycle()

    LaunchedEffect(sessionState) {
        Log.d("AltosAuthGate", "AuthGateRoute -> sessionState=$sessionState")
    }

    when (val state = sessionState.value) {
        SessionState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        SessionState.Unauthenticated -> {
            AuthenticationScreen(modifier = modifier)
        }

        is SessionState.Authenticated -> {
            authenticatedContent(state)
        }

        is SessionState.NeedsProfileCompletion -> {
            val existingProfile = state.existingProfile

            if (existingProfile?.isComplete == true) {
                LaunchedEffect(existingProfile.id, existingProfile.updatedAt.time) {
                    viewModel.refreshSession()
                }

                authenticatedContent(
                    SessionState.Authenticated(profile = existingProfile)
                )
            } else {
                CompleteProfileScreen(
                    state = state,
                    modifier = modifier,
                    onProfileCompleted = {
                        viewModel.refreshSession()
                    },
                )
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AuthGateViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


@HiltViewModel
class AuthGateViewModel @Inject constructor(
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionRepositoriable
        .sessionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )

    fun refreshSession() {
        viewModelScope.launch {
            sessionRepositoriable.refresh()
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AuthenticationViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


data class AuthenticationUiState(
    val isSubmitting: Boolean = false,
    val isTryingAuthorizedAccounts: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    fun beginAuthorizedAccountsAttempt() {
        _uiState.update {
            it.copy(
                isTryingAuthorizedAccounts = true,
                errorMessage = null,
            )
        }
    }

    fun finishAuthorizedAccountsAttempt() {
        _uiState.update {
            it.copy(isTryingAuthorizedAccounts = false)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                )
            }

            runCatching {
                signInWithGoogleUseCase.execute(idToken)
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Google sign-in failed.")
                }
            }

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isTryingAuthorizedAccounts = false,
                )
            }
        }
    }

    fun onSignInError(message: String) {
        _uiState.update {
            it.copy(
                isSubmitting = false,
                isTryingAuthorizedAccounts = false,
                errorMessage = message,
            )
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/CompleteProfileViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


data class CompleteProfileUiState(
    val user: AuthenticatedUser? = null,
    val existingProfile: ClientProfile? = null,
    val fullName: String = "",
    val email: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Calendar.getInstance()
        .apply {
            set(2000, 0, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        .time,
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = fullName.trim().isNotEmpty() &&
            email.trim().isNotEmpty() &&
            email.trim().contains("@") &&
            nationalId.onlyDigits().length >= 8 &&
            phoneNumber.onlyDigits().length >= 8 &&
            address.trim().isNotEmpty() &&
            emergencyContactName.trim().isNotEmpty() &&
            emergencyContactPhone.onlyDigits().length >= 8 &&
            birthday <= Date()
}

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val completeClientProfileUseCase: CompleteClientProfileUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    private val _profileCompleted = MutableSharedFlow<ClientProfile>(extraBufferCapacity = 1)
    val profileCompleted: SharedFlow<ClientProfile> = _profileCompleted.asSharedFlow()

    fun initialize(
        user: AuthenticatedUser,
        existingProfile: ClientProfile?,
    ) {
        val current = _uiState.value
        if (current.user?.uid == user.uid && current.existingProfile == existingProfile) return

        val now = Date()
        val defaultBirthday = Calendar.getInstance()
            .apply {
                time = now
                add(Calendar.YEAR, -18)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time

        _uiState.value = CompleteProfileUiState(
            user = user,
            existingProfile = existingProfile,
            fullName = existingProfile?.fullName ?: user.displayName,
            email = existingProfile?.email ?: user.email,
            nationalId = existingProfile?.nationalId.orEmpty(),
            phoneNumber = existingProfile?.phoneNumber.orEmpty(),
            birthday = existingProfile?.birthday ?: defaultBirthday,
            address = existingProfile?.address.orEmpty(),
            emergencyContactName = existingProfile?.emergencyContactName.orEmpty(),
            emergencyContactPhone = existingProfile?.emergencyContactPhone.orEmpty(),
            isSaving = false,
            errorMessage = null,
        )
    }

    fun onFullNameChanged(value: String) = update { copy(fullName = value) }
    fun onEmailChanged(value: String) = update { copy(email = value) }
    fun onNationalIdChanged(value: String) = update { copy(nationalId = value) }
    fun onPhoneNumberChanged(value: String) = update { copy(phoneNumber = value) }
    fun onBirthdayChanged(value: Date) = update { copy(birthday = value) }
    fun onAddressChanged(value: String) = update { copy(address = value) }
    fun onEmergencyContactNameChanged(value: String) = update { copy(emergencyContactName = value) }
    fun onEmergencyContactPhoneChanged(value: String) = update { copy(emergencyContactPhone = value) }
    fun clearError() = update { copy(errorMessage = null) }

    fun saveProfile() {
        val snapshot = _uiState.value
        val user = snapshot.user ?: return

        if (!snapshot.canSubmit) {
            update { copy(errorMessage = "Completa correctamente todos los campos obligatorios.") }
            return
        }

        viewModelScope.launch {
            update { copy(isSaving = true, errorMessage = null) }

            val now = Date()
            val existingProfile = snapshot.existingProfile
            val profile = ClientProfile(
                id = user.uid,
                email = snapshot.email.trim(),
                appleUserIdentifier = user.appleUserIdentifier,
                fullName = snapshot.fullName.trim(),
                nationalId = snapshot.nationalId.onlyDigits(),
                phoneNumber = snapshot.phoneNumber.onlyDigits(),
                birthday = snapshot.birthday,
                address = snapshot.address.trim(),
                emergencyContactName = snapshot.emergencyContactName.trim(),
                emergencyContactPhone = snapshot.emergencyContactPhone.onlyDigits(),
                isProfileComplete = true,
                createdAt = existingProfile?.createdAt ?: now,
                updatedAt = now,
                profileCompletedAt = existingProfile?.profileCompletedAt ?: now,
                profileImageURL = existingProfile?.profileImageURL,
                profileImagePath = existingProfile?.profileImagePath,
            )

            runCatching {
                completeClientProfileUseCase.execute(profile)
            }.onFailure { error ->
                update {
                    copy(
                        isSaving = false,
                        errorMessage = error.message ?: "No se pudo guardar el perfil.",
                    )
                }
                return@launch
            }

            update { copy(isSaving = false, existingProfile = profile) }
            _profileCompleted.tryEmit(profile)

            runCatching {
                repeat(3) { attempt ->
                    sessionRepositoriable.refresh()
                    if (attempt < 2) delay(120)
                }
            }
        }
    }

    private inline fun update(
        transform: CompleteProfileUiState.() -> CompleteProfileUiState,
    ) {
        _uiState.update(transform)
    }
}

private fun String.onlyDigits(): String = filter(Char::isDigit)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/data/AdventureBookingsRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.data


@Singleton
class AdventureBookingsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val catalogRepository: AdventureCatalogRepositoriable,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : AdventureBookingsRepositoriable {

    override fun observeBookings(
        nationalId: String,
    ): Flow<List<AdventureBooking>> = callbackFlow {
        val cleanNationalId = nationalId.filter(Char::isDigit)

        if (cleanNationalId.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", cleanNationalId)
            .orderBy("startAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bookings = snapshot
                    ?.documents
                    .orEmpty()
                    .mapNotNull { document ->
                        document.toObject(AdventureBookingDto::class.java)
                            ?.toDomain(document.id)
                    }
                    .sortedBy { it.startAt.time }

                trySend(bookings).isSuccess
            }

        awaitClose { registration.remove() }
    }

    override suspend fun fetchAvailability(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot> {
        val catalog = catalogRepository.fetchCatalog()

        return AdventurePlanner.buildAvailability(
            day = date,
            items = items,
            foodReservation = foodReservation,
            packageDiscountAmount = packageDiscountAmount,
            catalog = catalog,
        )
    }

    override suspend fun createBooking(request: AdventureBookingRequest): AdventureBooking {
        val catalog = catalogRepository.fetchCatalog()

        val basePlan = AdventurePlanner.buildPlan(
            day = request.date,
            startAt = request.selectedStartAt,
            items = request.items,
            foodReservation = request.foodReservation,
            packageDiscountAmount = request.packageDiscountAmount,
            catalog = catalog,
        ) ?: error("Invalid reservation configuration.")

        val rewardPreview = loyaltyRewardsRepository.previewAdventureRewards(
            nationalId = request.nationalId,
            activityItems = request.items,
            foodItems = request.foodReservation?.items.orEmpty(),
            catalog = catalog,
        )

        val finalPlan = AdventureBuildPlan(
            startAt = basePlan.startAt,
            endAt = basePlan.endAt,
            blocks = basePlan.blocks,
            adventureSubtotal = basePlan.adventureSubtotal,
            foodSubtotal = basePlan.foodSubtotal,
            subtotal = basePlan.subtotal,
            discountAmount = basePlan.discountAmount,
            loyaltyDiscountAmount = rewardPreview.totalDiscount,
            appliedRewards = rewardPreview.appliedRewards,
            nightPremium = basePlan.nightPremium,
            totalAmount = max(0.0, basePlan.totalAmount - rewardPreview.totalDiscount),
            hasNightPremium = basePlan.hasNightPremium,
        )

        val normalizedRequest = request.copy(
            nationalId = request.nationalId.filter(Char::isDigit),
            packageDiscountAmount = request.packageDiscountAmount.coerceAtLeast(0.0),
            loyaltyDiscountAmount = rewardPreview.totalDiscount.coerceAtLeast(0.0),
            appliedRewards = rewardPreview.appliedRewards,
        )

        val createdAt = Date()
        val bookingRef = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .document()

        val dto = AdventureBookingDto.Companion.from(
            request = normalizedRequest,
            plan = finalPlan,
            createdAt = createdAt,
            status = AdventureBookingStatus.PENDING,
        )

        bookingRef.set(dto).awaitResult()

        loyaltyRewardsRepository.reserveRewards(
            nationalId = normalizedRequest.nationalId,
            referenceType = LoyaltyRewardReferenceType.BOOKING,
            referenceId = bookingRef.id,
            appliedRewards = normalizedRequest.appliedRewards,
        )

        return dto.toDomain(bookingRef.id)
    }

    override suspend fun cancelBooking(id: String, nationalId: String) {
        val cleanNationalId = nationalId.filter(Char::isDigit)
        val cleanId = id.trim()

        require(cleanId.isNotEmpty()) { "Booking id is required." }
        require(cleanNationalId.isNotEmpty()) { "No se encontró una cédula asociada a esta cuenta." }

        val bookingRef = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .document(cleanId)

        val snapshot = bookingRef.get().awaitResult()

        if (!snapshot.exists()) {
            error("Booking not found.")
        }

        val dto = snapshot.toObject(AdventureBookingDto::class.java)
            ?: error("Could not read booking data.")

        if (dto.nationalId.filter(Char::isDigit) != cleanNationalId) {
            error("You are not allowed to cancel this booking.")
        }

        bookingRef
            .update("status", AdventureBookingStatus.CANCELED.rawValue)
            .awaitResult()

        loyaltyRewardsRepository.releaseRewards(
            nationalId = dto.nationalId,
            referenceId = cleanId,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/domain/AdventureBookingRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain


interface AdventureBookingsRepositoriable {
    fun observeBookings(nationalId: String): Flow<List<AdventureBooking>>

    suspend fun fetchAvailability(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot>

    suspend fun createBooking(request: AdventureBookingRequest): AdventureBooking

    suspend fun cancelBooking(id: String, nationalId: String)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/presentation/AdventureBookingsUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation


enum class AdventureReservationTimelineFilter(
    val title: String,
) {
    ALL("Todas"),
    CURRENT("Actuales"),
    FUTURE("Futuras"),
    PAST("Pasadas"),
}

enum class AdventureReservationStatusFilter(
    val title: String,
    val bookingStatus: AdventureBookingStatus?,
) {
    ALL("Todo", null),
    PENDING("Pendiente", AdventureBookingStatus.PENDING),
    CONFIRMED("Confirmada", AdventureBookingStatus.CONFIRMED),
    COMPLETED("Completada", AdventureBookingStatus.COMPLETED),
    CANCELED("Cancelada", AdventureBookingStatus.CANCELED),
}

enum class AdventureReservationSortOrder(
    val title: String,
) {
    NEAREST_FIRST("Próximas primero"),
    NEWEST_FIRST("Más recientes"),
    OLDEST_FIRST("Más antiguas"),
}

data class AdventureBookingsDateGroup(
    val id: String,
    val date: Date,
    val bookings: List<AdventureBooking>,
) {
    val title: String
        get() {
            val calendar = Calendar.getInstance()
            val today = AdventureDateHelper.startOfDay(Date())
            val tomorrow = calendar.apply {
                time = today
                add(Calendar.DAY_OF_YEAR, 1)
            }.time
            val yesterday = calendar.apply {
                time = today
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            return when {
                AdventureDateHelper.sameDay(date, today) -> "Hoy"
                AdventureDateHelper.sameDay(date, tomorrow) -> "Mañana"
                AdventureDateHelper.sameDay(date, yesterday) -> "Ayer"
                else -> longDateFormatter.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale("es", "EC")) else it.toString()
                }
            }
        }

    companion object {
        private val longDateFormatter = SimpleDateFormat(
            "EEEE d 'de' MMMM yyyy",
            Locale("es", "EC"),
        )
    }
}

data class AdventureBookingsUiState(
    val nationalId: String = "",
    val allBookings: List<AdventureBooking> = emptyList(),
    val selectedTimelineFilter: AdventureReservationTimelineFilter = AdventureReservationTimelineFilter.ALL,
    val selectedStatusFilter: AdventureReservationStatusFilter = AdventureReservationStatusFilter.ALL,
    val sortOrder: AdventureReservationSortOrder = AdventureReservationSortOrder.NEAREST_FIRST,
    val now: Date = Date(),
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val cancellingBookingId: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val displayedBookings: List<AdventureBooking>
        get() {
            val filtered = allBookings.filter { booking ->
                matchesTimelineFilter(booking) && matchesStatusFilter(booking)
            }

            return sorted(filtered)
        }

    val groupedBookings: List<AdventureBookingsDateGroup>
        get() {
            val groups = linkedMapOf<String, MutableList<AdventureBooking>>()

            displayedBookings.forEach { booking ->
                val day = AdventureDateHelper.startOfDay(booking.startAt)
                val key = AdventureDateHelper.dayKey(day)
                groups.getOrPut(key) { mutableListOf() }.add(booking)
            }

            return groups.map { (key, bookings) ->
                AdventureBookingsDateGroup(
                    id = key,
                    date = AdventureDateHelper.startOfDay(bookings.first().startAt),
                    bookings = bookings,
                )
            }
        }

    val totalCount: Int
        get() = allBookings.size

    val displayedCount: Int
        get() = displayedBookings.size

    val currentCount: Int
        get() = allBookings.count { isCurrent(it) }

    val futureCount: Int
        get() = allBookings.count { isFuture(it) }

    val pastCount: Int
        get() = allBookings.count { isPast(it) }

    private fun matchesTimelineFilter(booking: AdventureBooking): Boolean {
        return when (selectedTimelineFilter) {
            AdventureReservationTimelineFilter.ALL -> true
            AdventureReservationTimelineFilter.CURRENT -> isCurrent(booking)
            AdventureReservationTimelineFilter.FUTURE -> isFuture(booking)
            AdventureReservationTimelineFilter.PAST -> isPast(booking)
        }
    }

    private fun matchesStatusFilter(booking: AdventureBooking): Boolean {
        val selectedStatus = selectedStatusFilter.bookingStatus ?: return true
        return booking.status == selectedStatus
    }

    private fun sorted(bookings: List<AdventureBooking>): List<AdventureBooking> {
        return when (sortOrder) {
            AdventureReservationSortOrder.NEAREST_FIRST -> bookings.sortedWith { lhs, rhs ->
                val lhsRank = timelineRank(lhs)
                val rhsRank = timelineRank(rhs)

                when {
                    lhsRank != rhsRank -> lhsRank.compareTo(rhsRank)
                    lhsRank == 2 -> rhs.startAt.time.compareTo(lhs.startAt.time)
                    lhs.startAt.time != rhs.startAt.time -> lhs.startAt.time.compareTo(rhs.startAt.time)
                    else -> lhs.createdAt.time.compareTo(rhs.createdAt.time)
                }
            }

            AdventureReservationSortOrder.NEWEST_FIRST -> bookings.sortedWith(
                compareByDescending<AdventureBooking> { it.startAt.time }
                    .thenByDescending { it.createdAt.time },
            )

            AdventureReservationSortOrder.OLDEST_FIRST -> bookings.sortedWith(
                compareBy<AdventureBooking> { it.startAt.time }
                    .thenBy { it.createdAt.time },
            )
        }
    }

    private fun timelineRank(booking: AdventureBooking): Int {
        return when {
            isCurrent(booking) -> 0
            isFuture(booking) -> 1
            else -> 2
        }
    }

    private fun isCurrent(booking: AdventureBooking): Boolean {
        val isActiveStatus = booking.status == AdventureBookingStatus.PENDING ||
                booking.status == AdventureBookingStatus.CONFIRMED

        return isActiveStatus &&
                !booking.startAt.after(now) &&
                !booking.endAt.before(now)
    }

    private fun isFuture(booking: AdventureBooking): Boolean {
        val isActiveStatus = booking.status == AdventureBookingStatus.PENDING ||
                booking.status == AdventureBookingStatus.CONFIRMED

        return isActiveStatus && booking.startAt.after(now)
    }

    private fun isPast(booking: AdventureBooking): Boolean {
        return booking.endAt.before(now) ||
                booking.status == AdventureBookingStatus.COMPLETED ||
                booking.status == AdventureBookingStatus.CANCELED
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/presentation/AdventureBookingsViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation


@HiltViewModel
class AdventureBookingsViewModel @Inject constructor(
    private val observeAdventureBookingsUseCase: ObserveAdventureBookingsUseCase,
    private val cancelAdventureBookingUseCase: CancelAdventureBookingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureBookingsUiState())
    val uiState: StateFlow<AdventureBookingsUiState> = _uiState.asStateFlow()

    private var bookingsJob: Job? = null

    fun onAppear(profile: ClientProfile) {
        val cleanNationalId = profile.nationalId.filter(Char::isDigit)
        val current = _uiState.value

        _uiState.update {
            it.copy(
                nationalId = cleanNationalId,
                now = Date(),
            )
        }

        if (current.nationalId == cleanNationalId && bookingsJob?.isActive == true) {
            return
        }

        observeBookings()
    }

    fun onDisappear() {
        bookingsJob?.cancel()
        bookingsJob = null
    }

    fun refresh() {
        _uiState.update { it.copy(now = Date()) }
        observeBookings()
    }

    fun setTimelineFilter(filter: AdventureReservationTimelineFilter) {
        _uiState.update {
            it.copy(
                selectedTimelineFilter = filter,
                now = Date(),
            )
        }
    }

    fun setStatusFilter(filter: AdventureReservationStatusFilter) {
        _uiState.update {
            it.copy(selectedStatusFilter = filter)
        }
    }

    fun setSortOrder(sortOrder: AdventureReservationSortOrder) {
        _uiState.update {
            it.copy(sortOrder = sortOrder)
        }
    }

    fun cancelBooking(booking: AdventureBooking) {
        val nationalId = _uiState.value.nationalId

        if (nationalId.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "No se encontró una cédula asociada a esta cuenta.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCancelling = true,
                    cancellingBookingId = booking.id,
                    errorMessage = null,
                    successMessage = null,
                )
            }

            runCatching {
                cancelAdventureBookingUseCase.execute(
                    id = booking.id,
                    nationalId = nationalId,
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isCancelling = false,
                        cancellingBookingId = null,
                        successMessage = "Reserva cancelada correctamente.",
                        now = Date(),
                    )
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error

                _uiState.update {
                    it.copy(
                        isCancelling = false,
                        cancellingBookingId = null,
                        errorMessage = error.message ?: "No se pudo cancelar la reserva.",
                    )
                }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    private fun observeBookings() {
        val nationalId = _uiState.value.nationalId

        bookingsJob?.cancel()

        if (nationalId.isBlank()) {
            bookingsJob = null
            _uiState.update {
                it.copy(
                    allBookings = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                )
            }
            return
        }

        bookingsJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    now = Date(),
                )
            }

            observeAdventureBookingsUseCase.execute(nationalId)
                .catch { error ->
                    if (error is CancellationException) throw error

                    _uiState.update {
                        it.copy(
                            allBookings = emptyList(),
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar tus reservas.",
                            now = Date(),
                        )
                    }
                }
                .collectLatest { bookings ->
                    _uiState.update {
                        it.copy(
                            allBookings = bookings,
                            isLoading = false,
                            errorMessage = null,
                            now = Date(),
                        )
                    }
                }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/presentation/view/AdventureBookingsScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureBookingsScreen(
    sessionState: SessionState.Authenticated,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdventureBookingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    var bookingToCancel by remember { mutableStateOf<AdventureBooking?>(null) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        viewModel.onAppear(sessionState.profile)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.onDisappear() }
    }

    state.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = {
                TextButton(onClick = viewModel::dismissMessage) {
                    Text("OK")
                }
            },
            title = { Text("Mensaje") },
            text = { Text(message) },
        )
    }

    state.successMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = {
                TextButton(onClick = viewModel::dismissMessage) {
                    Text("OK")
                }
            },
            title = { Text("Listo") },
            text = { Text(message) },
        )
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelBooking(booking)
                        bookingToCancel = null
                    },
                ) {
                    Text("Cancelar reserva")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("Volver")
                }
            },
            title = { Text("¿Cancelar reserva?") },
            text = {
                Text(
                    "La reserva quedará cancelada y se liberarán los premios Murco Loyalty reservados para esta reserva.",
                )
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reservas de aventura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                AdventureBookingsHero(state = state)
            }

            item {
                AdventureBookingsControlsCard(
                    state = state,
                    onTimelineSelected = viewModel::setTimelineFilter,
                    onStatusSelected = viewModel::setStatusFilter,
                    onSortSelected = viewModel::setSortOrder,
                )
            }

            if (!state.errorMessage.isNullOrBlank()) {
                item {
                    InlineMessageCard(
                        title = "No se pudieron cargar tus reservas",
                        body = state.errorMessage.orEmpty(),
                        icon = Icons.Rounded.Warning,
                    )
                }
            }

            if (!state.isLoading && state.displayedBookings.isEmpty()) {
                item {
                    EmptyAdventureBookingsCard(state = state)
                }
            }

            state.groupedBookings.forEach { group ->
                item(key = "header-${group.id}") {
                    DateGroupHeader(group = group)
                }

                items(
                    count = group.bookings.size,
                    key = { index -> group.bookings[index].id },
                ) { index ->
                    val booking = group.bookings[index]

                    AdventureBookingCard(
                        booking = booking,
                        isCancelling = state.cancellingBookingId == booking.id,
                        onCancel = { bookingToCancel = booking },
                    )
                }
            }
        }
    }
}

@Composable
private fun AdventureBookingsHero(
    state: AdventureBookingsUiState,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    IconBubble(
                        icon = Icons.Rounded.CalendarMonth,
                        strong = true,
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Tu historial completo",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )

                        Text(
                            text = "Actuales, futuras y pasadas; filtradas por estado y ordenadas por fecha.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("Total", state.totalCount.toString())
                    MetricPill("Actuales", state.currentCount.toString())
                    MetricPill("Futuras", state.futureCount.toString())
                    MetricPill("Pasadas", state.pastCount.toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdventureBookingsControlsCard(
    state: AdventureBookingsUiState,
    onTimelineSelected: (AdventureReservationTimelineFilter) -> Unit,
    onStatusSelected: (AdventureReservationStatusFilter) -> Unit,
    onSortSelected: (AdventureReservationSortOrder) -> Unit,
) {
    var sortExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionTitle(
                icon = Icons.Rounded.FilterList,
                title = "Herramientas",
                subtitle = "Filtra por tiempo, estado y orden de fecha.",
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdventureReservationTimelineFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedTimelineFilter == filter,
                        onClick = { onTimelineSelected(filter) },
                        label = { Text(filter.title) },
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdventureReservationStatusFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedStatusFilter == filter,
                        onClick = { onStatusSelected(filter) },
                        label = { Text(filter.title) },
                    )
                }
            }

            Box {
                OutlinedButton(
                    onClick = { sortExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Sort, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.sortOrder.title,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false },
                ) {
                    AdventureReservationSortOrder.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.title) },
                            onClick = {
                                sortExpanded = false
                                onSortSelected(option)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateGroupHeader(
    group: AdventureBookingsDateGroup,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "${group.bookings.size} reserva(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SuggestionChip(
            onClick = {},
            label = { Text(AdventureDateHelper.shortDateText(group.date)) },
            icon = {
                Icon(
                    Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
private fun AdventureBookingCard(
    booking: AdventureBooking,
    isCancelling: Boolean,
    onCancel: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                IconBubble(
                    icon = bookingIcon(booking),
                    strong = false,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = booking.visitTypeTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )

                    Text(
                        text = booking.eventDisplayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${AdventureDateHelper.timeText(booking.startAt)} - ${
                            AdventureDateHelper.timeText(
                                booking.endAt
                            )
                        }",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                StatusBadge(status = booking.status)
            }

            Divider()

            if (booking.items.isNotEmpty()) {
                BookingSubsection(
                    title = "Actividades",
                    icon = Icons.Rounded.Explore,
                ) {
                    booking.items.forEach { item ->
                        Text(
                            text = "• ${item.title}: ${item.summaryText}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            booking.foodReservation?.takeIf { !it.isEmpty }?.let { food ->
                BookingFoodSection(food = food)
            }

            if (booking.appliedRewards.isNotEmpty()) {
                BookingSubsection(
                    title = "Premios aplicados",
                    icon = Icons.Rounded.CheckCircle,
                ) {
                    booking.appliedRewards.forEach { reward ->
                        Text(
                            text = "• ${reward.title}: -${reward.amount.priceText()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            booking.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                InlineNote(text = notes)
            }

            PriceSummary(booking = booking)

            AnimatedVisibility(visible = booking.canBeCancelled) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !isCancelling,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Cancel, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isCancelling) "Cancelando..." else "Cancelar reserva")
                }
            }

            if (booking.status == AdventureBookingStatus.COMPLETED) {
                CompletionInfo(text = "Reserva completada")
            }

            if (booking.status == AdventureBookingStatus.CANCELED) {
                CompletionInfo(text = "Reserva cancelada")
            }
        }
    }
}

@Composable
private fun BookingFoodSection(
    food: ReservationFoodDraft,
) {
    BookingSubsection(
        title = "Comida",
        icon = Icons.Rounded.LocalDining,
    ) {
        food.items.forEach { item ->
            Text(
                text = "• ${item.quantity}x ${item.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "Servicio: ${food.servingMoment.title}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )

        food.notes?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PriceSummary(
    booking: AdventureBooking,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryRow("Aventura", booking.adventureSubtotal.priceText())
        SummaryRow("Comida", booking.foodSubtotal.priceText())

        if (booking.discountAmount > 0.0) {
            SummaryRow("Descuento aventura", "-${booking.discountAmount.priceText()}")
        }

        if (booking.loyaltyDiscountAmount > 0.0) {
            SummaryRow("Murco Loyalty", "-${booking.loyaltyDiscountAmount.priceText()}")
        }

        Divider()

        SummaryRow(
            title = "Total",
            value = booking.totalAmount.priceText(),
            bold = true,
        )
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    bold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BookingSubsection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

@Composable
private fun EmptyAdventureBookingsCard(
    state: AdventureBookingsUiState,
) {
    InlineMessageCard(
        title = "No hay reservas para mostrar",
        body = when {
            state.totalCount == 0 -> "Cuando crees una reserva de aventura, comida o evento aparecerá aquí."
            else -> "Cambia los filtros para ver otras reservas."
        },
        icon = Icons.Rounded.Event,
    )
}

@Composable
private fun InlineMessageCard(
    title: String,
    body: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IconBubble(icon = icon)

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: AdventureBookingStatus,
) {
    val container = when (status) {
        AdventureBookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        AdventureBookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        AdventureBookingStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
        AdventureBookingStatus.CANCELED -> MaterialTheme.colorScheme.errorContainer
    }

    val content = when (status) {
        AdventureBookingStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        AdventureBookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        AdventureBookingStatus.COMPLETED -> MaterialTheme.colorScheme.onSecondaryContainer
        AdventureBookingStatus.CANCELED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Text(
        text = status.title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = content,
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBubble(icon = icon)

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MetricPill(
    title: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.13f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
        )
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    strong: Boolean = false,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (strong) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                },
            )
            .padding(11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (strong) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            },
        )
    }
}

@Composable
private fun InlineNote(
    text: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Rounded.ForkRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompletionInfo(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private val AdventureBooking.canBeCancelled: Boolean
    get() = status == AdventureBookingStatus.PENDING ||
            status == AdventureBookingStatus.CONFIRMED

private fun bookingIcon(booking: AdventureBooking): ImageVector {
    return when {
        booking.hasActivities -> booking.items.firstOrNull()?.activity?.bookingIcon()
        booking.hasFoodReservation -> Icons.Rounded.LocalDining
        else -> Icons.Rounded.Event
    } ?: Icons.Rounded.Event
}

private fun AdventureActivityType.bookingIcon(): ImageVector {
    return when (this) {
        AdventureActivityType.OFF_ROAD -> Icons.Rounded.Explore
        AdventureActivityType.PAINTBALL -> Icons.Rounded.Timeline
        AdventureActivityType.GO_KARTS -> Icons.Rounded.Event
        AdventureActivityType.SHOOTING_RANGE -> Icons.Rounded.AccessTime
        AdventureActivityType.CAMPING -> Icons.Rounded.CalendarMonth
        AdventureActivityType.EXTREME_SLIDE -> Icons.Rounded.Explore
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/presentation/view/BookingsScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view


private enum class BookingsMode {
    HOME,
    RESTAURANT_ORDERS,
    ADVENTURE_BOOKINGS,
}

@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
) {
    var mode by rememberSaveable { mutableStateOf(BookingsMode.HOME) }
    val ordersState by ordersViewModel.uiState.collectAsState()

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        ordersViewModel.syncProfile(sessionState.profile)
    }

    when (mode) {
        BookingsMode.HOME -> BookingsHomeContent(
            modifier = modifier,
            restaurantCount = ordersState.orders.size,
            onRestaurantOrders = { mode = BookingsMode.RESTAURANT_ORDERS },
            onAdventureBookings = { mode = BookingsMode.ADVENTURE_BOOKINGS },
        )

        BookingsMode.RESTAURANT_ORDERS -> OrdersScreen(
            state = ordersState,
            onBack = { mode = BookingsMode.HOME },
            onGroupingSelected = ordersViewModel::setGrouping,
            onSortSelected = ordersViewModel::setSortOption,
            onStatusSelected = ordersViewModel::setStatusFilter,
            onDismissError = ordersViewModel::clearError,
            modifier = modifier,
        )

        BookingsMode.ADVENTURE_BOOKINGS -> AdventureBookingsScreen(
            sessionState = sessionState,
            onBack = { mode = BookingsMode.HOME },
            modifier = modifier,
        )
    }
}

@Composable
private fun BookingsHomeContent(
    restaurantCount: Int,
    onRestaurantOrders: () -> Unit,
    onAdventureBookings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            BookingsHeroCard()
        }

        item {
            BookingEntryCard(
                badge = "Restaurante",
                title = "Pedidos del restaurante",
                subtitle = "Revisa tus pedidos actuales, anteriores, estados, totales y productos.",
                icon = Icons.Rounded.LocalDining,
                metric = "$restaurantCount pedido(s)",
                onClick = onRestaurantOrders,
            )
        }

        item {
            BookingEntryCard(
                badge = "Aventura",
                title = "Reservas de aventura",
                subtitle = "Mira combos, actividades, comida, eventos, premios aplicados y reservas nocturnas.",
                icon = Icons.Rounded.CalendarMonth,
                metric = "Actuales y futuras",
                onClick = onAdventureBookings,
            )
        }
    }
}

@Composable
private fun BookingsHeroCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .padding(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    HeroIconBubble(Icons.Rounded.ReceiptLong)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Gestiona tus reservas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )

                        Text(
                            text = "Pedidos del restaurante y reservas de aventura en un solo lugar, separado por experiencia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                        )
                    }
                }

                SuggestionChip(
                    onClick = {},
                    label = { Text("Altos del Murco") },
                    icon = {
                        Icon(
                            Icons.Rounded.Explore,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun BookingEntryCard(
    badge: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    metric: String,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionIconBubble(icon = icon)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = metric,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionIconBubble(
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun HeroIconBubble(
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f))
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/home/presentation/view/HomeScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Inicio",
            body = "Este es el shell de Compose para Altos del Murco. Desde aquí el proyecto ya tiene navegación, Hilt, DataStore y Room listos para los siguientes módulos.",
        )

    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/data/LoyaltyRewardsRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data


@Singleton
class LoyaltyRewardsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : LoyaltyRewardsRepositoriable {

    override suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty()) return RewardWalletSnapshot.empty("")

        val templates = fetchTemplates()
        val totals = computeTotals(cleanNationalId)
        val walletEvents = fetchWalletEvents(cleanNationalId)
        val currentLevel = LoyaltyLevel.fromTotalSpent(totals.totalSpent)

        val eligibleTemplates = templates
            .filter { template ->
                template.isActive &&
                        !template.isExpired &&
                        template.triggerMode == LoyaltyRewardTriggerMode.AUTOMATIC &&
                        template.isEligible(currentLevel) &&
                        usageCount(
                            template.id,
                            walletEvents
                        ) < template.maxUsesPerClient.coerceAtLeast(1)
            }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        return RewardWalletSnapshot(
            nationalId = cleanNationalId,
            currentLevel = currentLevel,
            totalSpent = totals.totalSpent.roundMoney(),
            points = totals.totalSpent.toInt(),
            availableTemplates = eligibleTemplates,
            reservedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.RESERVED },
            consumedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.CONSUMED },
            releasedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.RELEASED },
        )
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun observeWalletSnapshot(nationalId: String): Flow<RewardWalletSnapshot> =
        callbackFlow {
            val cleanNationalId = nationalId.cleanNationalId()
            if (cleanNationalId.isEmpty()) {
                trySend(RewardWalletSnapshot.empty(""))
                close()
                return@callbackFlow
            }

            val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 32)
            val registrations = mutableListOf<ListenerRegistration>()

            val loaderJob: Job = launch {
                refreshRequests
                    .onStart { emit(Unit) }
                    .debounce(160)
                    .mapLatest { loadWalletSnapshot(cleanNationalId) }
                    .catch { error ->
                        if (error is CancellationException) throw error
                        close(error)
                    }
                    .collect { wallet ->
                        trySend(wallet).isSuccess
                    }
            }

            fun requestRefresh() {
                refreshRequests.tryEmit(Unit)
            }

            registrations += firestore
                .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
                .document(cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.RESTAURANT_ORDERS)
                .whereEqualTo("nationalId", cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
                .whereEqualTo("nationalId", cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            requestRefresh()

            awaitClose {
                registrations.forEach { it.remove() }
                registrations.clear()
                loaderJob.cancel()
            }
        }

    override suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult {
        val wallet = loadWalletSnapshot(nationalId)
        val lines = items.map {
            RewardMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateRestaurant(
            templates = wallet.availableTemplates,
            wallet = wallet,
            menuLines = lines,
        )
    }

    override suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult {
        val wallet = loadWalletSnapshot(nationalId)

        val activityLines = activityItems.mapNotNull { item ->
            val activity = catalog.activity(item.activity) ?: return@mapNotNull null
            RewardActivityLine(
                activityId = activity.id,
                title = activity.title,
                linePrice = adventureSubtotalFor(item, activity),
            )
        }

        val foodLines = foodItems.map {
            RewardMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateAdventure(
            templates = wallet.availableTemplates,
            wallet = wallet,
            activityLines = activityLines,
            foodLines = foodLines,
        )
    }

    override suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    ) {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty() || referenceId.isBlank() || appliedRewards.isEmpty()) return

        val walletRef = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(cleanNationalId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walletRef)
            val events = snapshot.walletEvents().toMutableList()
            val now = Date()

            appliedRewards.forEach { reward ->
                val templateRef = firestore
                    .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
                    .document(reward.templateId)
                val templateSnapshot = transaction.get(templateRef)
                val template = templateSnapshot.toLoyaltyRewardTemplateOrNull()
                    ?: throw IllegalStateException("El premio ${reward.title} ya no existe.")

                if (!template.isActive || template.isExpired) {
                    throw IllegalStateException("El premio ${template.title} ya no está disponible.")
                }

                if (usageCount(template.id, events) >= template.maxUsesPerClient.coerceAtLeast(1)) {
                    throw IllegalStateException("El premio ${template.title} ya fue usado.")
                }

                events += LoyaltyWalletEvent(
                    id = reward.id,
                    templateId = reward.templateId,
                    templateTitle = reward.title,
                    referenceType = referenceType,
                    referenceId = referenceId,
                    status = LoyaltyWalletEventStatus.RESERVED,
                    amount = reward.amount,
                    createdAt = now,
                    updatedAt = now,
                )
            }

            transaction.set(
                walletRef,
                mapOf(
                    "nationalId" to cleanNationalId,
                    "updatedAt" to Timestamp(now),
                    "events" to events.map { it.toFirestoreMap() },
                ),
                SetOptions.merge(),
            )
            null
        }.awaitResult()
    }

    override suspend fun consumeRewards(nationalId: String, referenceId: String) {
        mutateReferenceStatus(nationalId, referenceId, LoyaltyWalletEventStatus.CONSUMED)
    }

    override suspend fun releaseRewards(nationalId: String, referenceId: String) {
        mutateReferenceStatus(nationalId, referenceId, LoyaltyWalletEventStatus.RELEASED)
    }

    private suspend fun mutateReferenceStatus(
        nationalId: String,
        referenceId: String,
        targetStatus: LoyaltyWalletEventStatus,
    ) {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty() || referenceId.isBlank()) return

        val walletRef = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(cleanNationalId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walletRef)
            val events = snapshot.walletEvents().map { event ->
                if (event.referenceId == referenceId && event.status == LoyaltyWalletEventStatus.RESERVED) {
                    event.copy(status = targetStatus, updatedAt = Date())
                } else {
                    event
                }
            }

            transaction.set(
                walletRef,
                mapOf(
                    "nationalId" to cleanNationalId,
                    "updatedAt" to Timestamp(Date()),
                    "events" to events.map { it.toFirestoreMap() },
                ),
                SetOptions.merge(),
            )
            null
        }.awaitResult()
    }

    private suspend fun fetchTemplates(): List<LoyaltyRewardTemplate> {
        val snapshot = firestore
            .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toLoyaltyRewardTemplateOrNull() }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })
    }

    private suspend fun fetchWalletEvents(nationalId: String): List<LoyaltyWalletEvent> {
        val snapshot = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(nationalId)
            .get()
            .awaitResult()

        return snapshot.walletEvents()
    }

    private suspend fun computeTotals(nationalId: String): LoyaltyTotals {
        val ordersSnapshot = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", nationalId)
            .get()
            .awaitResult()

        val bookingsSnapshot = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", nationalId)
            .get()
            .awaitResult()

        val restaurantSpent = ordersSnapshot.documents
            .filter { it.getString("status")?.equals("completed", ignoreCase = true) == true }
            .sumOf { it.doubleValue("totalAmount") }

        val adventureSpent = bookingsSnapshot.documents
            .filter { it.getString("status")?.equals("completed", ignoreCase = true) == true }
            .sumOf { it.doubleValue("totalAmount") }

        return LoyaltyTotals(
            restaurantSpent = restaurantSpent,
            adventureSpent = adventureSpent,
        )
    }

    private fun adventureSubtotalFor(
        item: AdventureReservationItemDraft,
        activity: AdventureActivityCatalogItem,
    ): Double {
        val finalUnitPrice = (activity.basePrice - activity.discountAmount).coerceAtLeast(0.0)
        return when (item.activity) {
            AdventureActivityType.OFF_ROAD -> {
                val hours = item.durationMinutes.toDouble() / 60.0
                finalUnitPrice * hours * item.vehicleCount.toDouble()
            }

            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE,
                -> {
                val blocks = item.durationMinutes.toDouble() / 30.0
                finalUnitPrice * blocks * item.peopleCount.toDouble()
            }

            AdventureActivityType.CAMPING -> {
                finalUnitPrice * item.peopleCount.toDouble() * item.nights.toDouble()
            }

            AdventureActivityType.EXTREME_SLIDE -> {
                finalUnitPrice * item.peopleCount.toDouble()
            }
        }.roundMoney()
    }

    private data class LoyaltyTotals(
        val restaurantSpent: Double,
        val adventureSpent: Double,
    ) {
        val totalSpent: Double = restaurantSpent + adventureSpent
    }

    private fun DocumentSnapshot.toLoyaltyRewardTemplateOrNull(): LoyaltyRewardTemplate? {
        val rawRule = get("rule") as? Map<*, *> ?: return null
        val rule = LoyaltyRewardRule(
            type = parseRuleType(rawRule.stringValue("type")),
            percentage = rawRule.doubleValueOrNull("percentage"),
            menuItemId = rawRule.stringValueOrNull("menuItemId")
                ?: rawRule.stringValueOrNull("menu_item_id"),
            activityId = rawRule.stringValueOrNull("activityId")
                ?: rawRule.stringValueOrNull("activity_id"),
            quantity = rawRule.intValueOrNull("quantity"),
            buyQuantity = rawRule.intValueOrNull("buyQuantity")
                ?: rawRule.intValueOrNull("buy_quantity"),
            freeQuantity = rawRule.intValueOrNull("freeQuantity")
                ?: rawRule.intValueOrNull("free_quantity"),
            repeatable = rawRule.boolValueOrNull("repeatable"),
        )

        val resolvedTitle = stringValueOrNull("title")
            ?.takeIf { it.isNotBlank() }
            ?: defaultTitleFor(rule)

        return LoyaltyRewardTemplate(
            id = stringValueOrNull("id")?.takeIf { it.isNotBlank() } ?: id,
            title = resolvedTitle,
            subtitle = stringValueOrNull("subtitle").orEmpty(),
            scope = parseScope(stringValueOrNull("scope")),
            minimumLevel = parseLevel(
                stringValueOrNull("minimumLevel") ?: stringValueOrNull("minimum_level")
            ),
            triggerMode = parseTriggerMode(
                stringValueOrNull("triggerMode") ?: stringValueOrNull("trigger_mode")
            ),
            isActive = boolValue("isActive", default = boolValue("active", default = true)),
            canStack = boolValue("canStack", default = boolValue("can_stack", default = true)),
            priority = intValue("priority", default = 0),
            maxUsesPerClient = intValue(
                "maxUsesPerClient",
                default = intValue("max_uses_per_client", default = 1)
            ).coerceAtLeast(1),
            expiresInDays = intValueOrNull("expiresInDays") ?: intValueOrNull("expires_in_days"),
            rule = rule,
            createdAt = dateValue("createdAt") ?: dateValue("created_at") ?: Date(),
            updatedAt = dateValue("updatedAt") ?: dateValue("updated_at") ?: dateValue("createdAt")
            ?: Date(),
        )
    }

    private fun defaultTitleFor(rule: LoyaltyRewardRule): String {
        return when (rule.type) {
            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE -> "Descuento en plato específico"
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> "Descuento en tu plato"
            LoyaltyRewardRuleType.FREE_MENU_ITEM -> "Plato gratis"
            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Promoción especial"
            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "Descuento en aventura"
        }
    }

    private fun DocumentSnapshot.walletEvents(): List<LoyaltyWalletEvent> {
        val rawEvents = get("events") as? List<*> ?: return emptyList()
        return rawEvents.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            LoyaltyWalletEvent(
                id = map.stringValueOrNull("id") ?: return@mapNotNull null,
                templateId = map.stringValueOrNull("templateId")
                    ?: map.stringValueOrNull("template_id")
                    ?: return@mapNotNull null,
                templateTitle = map.stringValueOrNull("templateTitle")
                    ?: map.stringValueOrNull("template_title")
                    ?: "Premio Murco Loyalty",
                referenceType = parseReferenceType(
                    map.stringValueOrNull("referenceType")
                        ?: map.stringValueOrNull("reference_type")
                ),
                referenceId = map.stringValueOrNull("referenceId")
                    ?: map.stringValueOrNull("reference_id").orEmpty(),
                status = parseEventStatus(map.stringValueOrNull("status")),
                amount = map.doubleValue("amount"),
                createdAt = map.dateValue("createdAt") ?: map.dateValue("created_at") ?: Date(),
                updatedAt = map.dateValue("updatedAt") ?: map.dateValue("updated_at") ?: Date(),
            )
        }
    }

    private fun LoyaltyWalletEvent.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "templateId" to templateId,
        "templateTitle" to templateTitle,
        "referenceType" to referenceType.name.lowercase(),
        "referenceId" to referenceId,
        "status" to status.name.lowercase(),
        "amount" to amount,
        "createdAt" to Timestamp(createdAt),
        "updatedAt" to Timestamp(updatedAt),
    )

    private fun usageCount(templateId: String, events: List<LoyaltyWalletEvent>): Int =
        events.count {
            it.templateId == templateId &&
                    (it.status == LoyaltyWalletEventStatus.RESERVED || it.status == LoyaltyWalletEventStatus.CONSUMED)
        }

    private fun parseScope(raw: String?): LoyaltyRewardScope = when (raw.normalizeKey()) {
        "restaurant" -> LoyaltyRewardScope.RESTAURANT
        "adventure" -> LoyaltyRewardScope.ADVENTURE
        "both" -> LoyaltyRewardScope.BOTH
        else -> LoyaltyRewardScope.BOTH
    }

    private fun parseTriggerMode(raw: String?): LoyaltyRewardTriggerMode =
        when (raw.normalizeKey()) {
            "manual" -> LoyaltyRewardTriggerMode.MANUAL
            else -> LoyaltyRewardTriggerMode.AUTOMATIC
        }

    private fun parseRuleType(raw: String?): LoyaltyRewardRuleType = when (raw.normalizeKey()) {
        "mostexpensivemenuitempercentage" -> LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE
        "specificmenuitempercentage" -> LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE
        "activitypercentage" -> LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE
        "freemenuitem" -> LoyaltyRewardRuleType.FREE_MENU_ITEM
        "buyxgetyfree" -> LoyaltyRewardRuleType.BUY_X_GET_Y_FREE
        else -> LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE
    }

    private fun parseReferenceType(raw: String?): LoyaltyRewardReferenceType =
        when (raw.normalizeKey()) {
            "booking" -> LoyaltyRewardReferenceType.BOOKING
            else -> LoyaltyRewardReferenceType.ORDER
        }

    private fun parseEventStatus(raw: String?): LoyaltyWalletEventStatus =
        when (raw.normalizeKey()) {
            "consumed" -> LoyaltyWalletEventStatus.CONSUMED
            "released" -> LoyaltyWalletEventStatus.RELEASED
            "expired" -> LoyaltyWalletEventStatus.EXPIRED
            else -> LoyaltyWalletEventStatus.RESERVED
        }

    private fun parseLevel(raw: String?): LoyaltyLevel = when (raw.normalizeKey()) {
        "silver" -> LoyaltyLevel.SILVER
        "gold" -> LoyaltyLevel.GOLD
        "platinum" -> LoyaltyLevel.PLATINUM
        "diamond" -> LoyaltyLevel.DIAMOND
        else -> LoyaltyLevel.BRONZE
    }

    private fun String?.normalizeKey(): String = orEmpty()
        .replace("_", "")
        .replace("-", "")
        .trim()
        .lowercase()

    private fun String.cleanNationalId(): String = filter { it.isDigit() }

    private fun DocumentSnapshot.stringValueOrNull(field: String): String? =
        getString(field)?.trim()

    private fun DocumentSnapshot.boolValue(field: String, default: Boolean): Boolean =
        getBoolean(field) ?: default

    private fun DocumentSnapshot.intValue(field: String, default: Int): Int =
        intValueOrNull(field) ?: default

    private fun DocumentSnapshot.intValueOrNull(field: String): Int? =
        when (val value = get(field)) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            is Number -> value.toInt()
            else -> null
        }

    private fun DocumentSnapshot.doubleValue(field: String): Double =
        when (val value = get(field)) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Number -> value.toDouble()
            else -> 0.0
        }

    private fun DocumentSnapshot.dateValue(field: String): Date? = when (val value = get(field)) {
        is Timestamp -> value.toDate()
        is Date -> value
        else -> null
    }

    private fun Map<*, *>.stringValue(field: String): String = stringValueOrNull(field).orEmpty()

    private fun Map<*, *>.stringValueOrNull(field: String): String? =
        (this[field] as? String)?.trim()

    private fun Map<*, *>.doubleValue(field: String): Double = doubleValueOrNull(field) ?: 0.0

    private fun Map<*, *>.doubleValueOrNull(field: String): Double? =
        when (val value = this[field]) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }

    private fun Map<*, *>.intValueOrNull(field: String): Int? = when (val value = this[field]) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        else -> null
    }

    private fun Map<*, *>.boolValueOrNull(field: String): Boolean? = this[field] as? Boolean

    private fun Map<*, *>.dateValue(field: String): Date? = when (val value = this[field]) {
        is Timestamp -> value.toDate()
        is Date -> value
        else -> null
    }

    private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/data/ProfileImageRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data


@Singleton
class ProfileImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage,
) : ProfileImageRepositoriable {

    private val cacheDirectory: File by lazy {
        File(context.cacheDir, "ProfileImages").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun cachedImageBytes(userId: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val file = fileFor(userId)
            if (file.exists()) runCatching { file.readBytes() }.getOrNull() else null
        }

    override suspend fun downloadAndCacheImage(userId: String, url: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val cleanUrl = url.trim()
            if (userId.isBlank() || cleanUrl.isBlank()) return@withContext null

            runCatching {
                val connection = URL(cleanUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 12_000
                connection.readTimeout = 12_000
                connection.instanceFollowRedirects = true
                connection.inputStream.use { input ->
                    val bytes = input.readBytes()
                    saveImageBytes(userId, bytes)
                }
            }.getOrNull()
        }

    override suspend fun saveImageBytes(userId: String, bytes: ByteArray): ByteArray =
        withContext(Dispatchers.IO) {
            if (userId.isNotBlank() && bytes.isNotEmpty()) {
                fileFor(userId).writeBytes(bytes)
            }
            bytes
        }

    override suspend fun removeCachedImage(userId: String) = withContext(Dispatchers.IO) {
        if (userId.isNotBlank()) fileFor(userId).delete()
    }

    override suspend fun uploadProfileImage(
        profile: ClientProfile,
        bytes: ByteArray,
    ): UploadedProfileImage {
        require(profile.id.isNotBlank()) { "User id is required to upload a profile image." }
        require(bytes.isNotEmpty()) { "Profile image data is empty." }

        val cleanUserId = profile.id.trim()
        val path = "profile_images/$cleanUserId/avatar_${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(path)

        ref.putBytes(bytes).awaitResult()
        val downloadUrl = ref.downloadUrl.awaitResult().toString()

        if (!profile.profileImagePath.isNullOrBlank() && profile.profileImagePath != path) {
            runCatching { deleteProfileImage(profile.profileImagePath) }
        }

        saveImageBytes(cleanUserId, bytes)

        return UploadedProfileImage(
            downloadURL = downloadUrl,
            storagePath = path,
        )
    }

    override suspend fun deleteProfileImage(storagePath: String?) {
        val cleanPath = storagePath?.trim().orEmpty()
        if (cleanPath.isEmpty()) return

        val ref = storage.reference.child(cleanPath)
        runCatching {
            ref.delete().awaitResult()
        }.onFailure { error ->
            val storageError = error as? StorageException
            if (storageError?.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw error
            }
        }
    }

    private fun fileFor(userId: String): File =
        File(cacheDirectory, "profile_${userId.trim()}.jpg")
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/data/ProfileStatsRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data


@Singleton
class ProfileStatsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ProfileStatsRepositoriable {

    override suspend fun loadStats(nationalId: String): ProfileStats {
        val cleanNationalId = nationalId.onlyDigits()
        if (cleanNationalId.isEmpty()) return ProfileStats.EMPTY

        val orderSnapshot = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", cleanNationalId)
            .get()
            .awaitResult()

        val bookingSnapshot = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", cleanNationalId)
            .get()
            .awaitResult()

        val wallet = loyaltyRewardsRepository.loadWalletSnapshot(cleanNationalId)

        val completedOrders = orderSnapshot.documents
            .mapNotNull { document -> document.toObject(OrderDto::class.java)?.toDomain() }
            .filter { order -> order.status == OrderStatus.COMPLETED }

        val completedBookings = bookingSnapshot.documents
            .mapNotNull { document ->
                document.toObject(AdventureBookingDto::class.java)?.toDomain(document.id)
            }
            .filter { booking -> booking.status == AdventureBookingStatus.COMPLETED }

        val restaurantSpent = completedOrders.sumOf { it.totalAmount }.roundMoney()
        val adventureSpent = completedBookings.sumOf { it.totalAmount }.roundMoney()
        val totalSpent = (restaurantSpent + adventureSpent).roundMoney()
        val computedLevel = LoyaltyLevel.fromTotalSpent(totalSpent)

        return ProfileStats(
            points = wallet.points.coerceAtLeast(totalSpent.toInt()),
            completedOrders = completedOrders.size,
            completedBookings = completedBookings.size,
            restaurantSpent = restaurantSpent,
            adventureSpent = adventureSpent,
            totalSpent = totalSpent,
            level = computedLevel,
            wallet = wallet.copy(
                currentLevel = computedLevel,
                totalSpent = totalSpent,
                points = wallet.points.coerceAtLeast(totalSpent.toInt()),
            ),
        )
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun observeStats(nationalId: String): Flow<ProfileStats> = callbackFlow {
        val cleanNationalId = nationalId.onlyDigits()
        if (cleanNationalId.isEmpty()) {
            trySend(ProfileStats.EMPTY).isSuccess
            close()
            return@callbackFlow
        }

        val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 32)
        val registrations = mutableListOf<ListenerRegistration>()

        fun requestRefresh() {
            refreshRequests.tryEmit(Unit)
        }

        val loaderJob: Job = launch {
            refreshRequests
                .onStart { emit(Unit) }
                .debounce(160)
                .mapLatest { loadStats(cleanNationalId) }
                .catch { error ->
                    if (error is CancellationException) throw error
                    close(error)
                }
                .collect { stats ->
                    trySend(stats).isSuccess
                }
        }

        registrations += firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", cleanNationalId)
            .addSnapshotListener { _, error ->
                if (error != null) close(error) else requestRefresh()
            }

        registrations += firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", cleanNationalId)
            .addSnapshotListener { _, error ->
                if (error != null) close(error) else requestRefresh()
            }

        val walletJob = loyaltyRewardsRepository
            .observeWalletSnapshot(cleanNationalId)
            .onEach { requestRefresh() }
            .catch { error ->
                if (error is CancellationException) throw error
                close(error)
            }
            .launchIn(this)

        awaitClose {
            registrations.forEach { it.remove() }
            walletJob.cancel()
            loaderJob.cancel()
        }
    }
}

private fun String.onlyDigits(): String = filter(Char::isDigit)

private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/AppliedRewardDto.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

data class AppliedRewardDto(
    val id: String = "",
    val templateId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val affectedMenuItemIds: List<String> = emptyList(),
    val affectedActivityIds: List<String> = emptyList(),
) {
    constructor(domain: AppliedReward) : this(
        id = domain.id,
        templateId = domain.templateId,
        title = domain.title,
        amount = domain.amount,
        note = domain.note,
        affectedMenuItemIds = domain.affectedMenuItemIds,
        affectedActivityIds = domain.affectedActivityIds,
    )

    fun toDomain(): AppliedReward = AppliedReward(
        id = id,
        templateId = templateId,
        title = title,
        amount = amount,
        note = note,
        affectedMenuItemIds = affectedMenuItemIds,
        affectedActivityIds = affectedActivityIds,
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ClientProfile.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


data class ClientProfile(
    val id: String,
    val email: String,
    val appleUserIdentifier: String,
    val fullName: String,
    val nationalId: String,
    val phoneNumber: String,
    val birthday: Date,
    val address: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val isProfileComplete: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    val profileCompletedAt: Date?,
    val profileImageURL: String?,
    val profileImagePath: String?,
) {
    val isComplete: Boolean
        get() = isProfileComplete &&
                fullName.isNotBlank() &&
                nationalId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank() &&
                emergencyContactName.isNotBlank() &&
                emergencyContactPhone.isNotBlank()

    val hasProfileImage: Boolean
        get() = !profileImageURL.isNullOrBlank()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ClientProfileDocument.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


data class ClientProfileDocument(
    val id: String = "",
    val email: String = "",
    val appleUserIdentifier: String = "",
    val fullName: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Date(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isProfileComplete: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val profileCompletedAt: Date? = null,
    val profileImageURL: String? = null,
    val profileImagePath: String? = null,
) {
    constructor(profile: ClientProfile) : this(
        id = profile.id,
        email = profile.email,
        appleUserIdentifier = profile.appleUserIdentifier,
        fullName = profile.fullName,
        nationalId = profile.nationalId,
        phoneNumber = profile.phoneNumber,
        birthday = profile.birthday,
        address = profile.address,
        emergencyContactName = profile.emergencyContactName,
        emergencyContactPhone = profile.emergencyContactPhone,
        isProfileComplete = profile.isProfileComplete,
        createdAt = profile.createdAt,
        updatedAt = profile.updatedAt,
        profileCompletedAt = profile.profileCompletedAt,
        profileImageURL = profile.profileImageURL,
        profileImagePath = profile.profileImagePath,
    )

    fun toDomain(
        documentIdFallback: String? = null,
    ): ClientProfile {
        val resolvedId = id.trim().ifEmpty { documentIdFallback?.trim().orEmpty() }

        return ClientProfile(
            id = resolvedId,
            email = email.trim(),
            appleUserIdentifier = appleUserIdentifier.trim(),
            fullName = fullName.trim(),
            nationalId = nationalId.trim(),
            phoneNumber = phoneNumber.trim(),
            birthday = birthday,
            address = address.trim(),
            emergencyContactName = emergencyContactName.trim(),
            emergencyContactPhone = emergencyContactPhone.trim(),
            isProfileComplete = isProfileComplete,
            createdAt = createdAt,
            updatedAt = updatedAt,
            profileCompletedAt = profileCompletedAt,
            profileImageURL = profileImageURL?.trim()?.takeIf { it.isNotEmpty() },
            profileImagePath = profileImagePath?.trim()?.takeIf { it.isNotEmpty() },
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/LoyaltyLevel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

enum class LoyaltyLevel(
    val title: String,
    val systemImage: String,
    val badgeSubtitle: String,
    val minimumSpent: Double,
    val spendRangeText: String,
    val benefits: List<String>,
) {
    BRONZE(
        title = "Bronce",
        systemImage = "sparkles",
        badgeSubtitle = "Tus primeras visitas ya empiezan a premiarte",
        minimumSpent = 0.0,
        spendRangeText = "De $0 a $99",
        benefits = listOf(
            "Acceso al programa Murco Loyalty",
            "Primeras promociones automáticas",
        ),
    ),
    SILVER(
        title = "Plata",
        systemImage = "seal.fill",
        badgeSubtitle = "Más beneficios cada vez que vuelves",
        minimumSpent = 100.0,
        spendRangeText = "De $100 a $299",
        benefits = listOf(
            "Más promociones activas por nivel",
            "Descuentos más frecuentes en restaurante y aventura",
        ),
    ),
    GOLD(
        title = "Oro",
        systemImage = "star.circle.fill",
        badgeSubtitle = "Descuentos más fuertes y regalos más frecuentes",
        minimumSpent = 300.0,
        spendRangeText = "De $300 a $799",
        benefits = listOf(
            "Premios de mayor valor",
            "Más opciones de items gratis o porcentaje off",
        ),
    ),
    PLATINUM(
        title = "Platino",
        systemImage = "crown.fill",
        badgeSubtitle = "Nivel preferente con premios premium",
        minimumSpent = 800.0,
        spendRangeText = "De $800 a $1499",
        benefits = listOf(
            "Beneficios premium",
            "Prioridad para recompensas más fuertes",
        ),
    ),
    DIAMOND(
        title = "Diamante",
        systemImage = "diamond.fill",
        badgeSubtitle = "Nuestro máximo nivel para clientes top",
        minimumSpent = 1500.0,
        spendRangeText = "Desde $1500",
        benefits = listOf(
            "Máximo nivel de beneficios",
            "Acceso continuo a recompensas top",
        ),
    );

    val nextLevel: LoyaltyLevel?
        get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromTotalSpent(totalSpent: Double): LoyaltyLevel = entries.lastOrNull {
            totalSpent >= it.minimumSpent
        } ?: BRONZE

        fun progress(totalSpent: Double): Double {
            val current = fromTotalSpent(totalSpent)
            val next = current.nextLevel ?: return 1.0
            val span = (next.minimumSpent - current.minimumSpent).coerceAtLeast(1.0)
            return ((totalSpent - current.minimumSpent) / span).coerceIn(0.0, 1.0)
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/LoyaltyRewardEngine.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


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
                LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> applyActivityTemplate(template, workingActivities)
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

                val index = lines.indices.firstOrNull { lines[it].menuItemId == targetId } ?: return null
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
                    remainingRewardableUnits = (line.remainingRewardableUnits - freeUnits).coerceAtLeast(0),
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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/LoyaltyRewardModels.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


enum class LoyaltyRewardScope(val title: String) {
    RESTAURANT("Restaurante"),
    ADVENTURE("Aventura"),
    BOTH("Ambos");

    fun matchesRestaurant(): Boolean = this == RESTAURANT || this == BOTH

    fun matchesAdventure(): Boolean = this == ADVENTURE || this == BOTH
}

enum class LoyaltyRewardTriggerMode {
    AUTOMATIC,
    MANUAL,
}

enum class LoyaltyRewardRuleType {
    MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
    SPECIFIC_MENU_ITEM_PERCENTAGE,
    ACTIVITY_PERCENTAGE,
    FREE_MENU_ITEM,
    BUY_X_GET_Y_FREE,
}

data class LoyaltyRewardRule(
    val type: LoyaltyRewardRuleType,
    val percentage: Double? = null,
    val menuItemId: String? = null,
    val activityId: String? = null,
    val quantity: Int? = null,
    val buyQuantity: Int? = null,
    val freeQuantity: Int? = null,
    val repeatable: Boolean? = null,
) {
    companion object {
        fun mostExpensiveMenuItemDiscount(percentage: Double): LoyaltyRewardRule =
            LoyaltyRewardRule(
                type = LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
                percentage = percentage,
                quantity = 1,
            )

        fun specificMenuItemDiscount(
            menuItemId: String,
            percentage: Double,
            quantity: Int = 1,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
            percentage = percentage,
            menuItemId = menuItemId,
            quantity = quantity.coerceAtLeast(1),
        )

        fun activityDiscount(
            activityId: String,
            percentage: Double,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE,
            percentage = percentage,
            activityId = activityId,
            quantity = 1,
        )

        fun freeMenuItem(
            menuItemId: String,
            quantity: Int = 1,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.FREE_MENU_ITEM,
            menuItemId = menuItemId,
            quantity = quantity.coerceAtLeast(1),
        )

        fun buyXGetYFree(
            menuItemId: String,
            buyQuantity: Int,
            freeQuantity: Int = 1,
            repeatable: Boolean = true,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
            menuItemId = menuItemId,
            buyQuantity = buyQuantity.coerceAtLeast(1),
            freeQuantity = freeQuantity.coerceAtLeast(1),
            repeatable = repeatable,
        )
    }
}

data class LoyaltyRewardTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val scope: LoyaltyRewardScope,
    val minimumLevel: LoyaltyLevel,
    val triggerMode: LoyaltyRewardTriggerMode,
    val isActive: Boolean,
    val canStack: Boolean,
    val priority: Int,
    val maxUsesPerClient: Int,
    val expiresInDays: Int?,
    val rule: LoyaltyRewardRule,
    val createdAt: Date,
    val updatedAt: Date,
) {
    val displaySummary: String
        get() = when (rule.type) {
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE ->
                "${(rule.percentage ?: 0.0).toInt()}% en el plato elegible más caro"

            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE ->
                "${(rule.percentage ?: 0.0).toInt()}% en item específico"

            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE ->
                "${(rule.percentage ?: 0.0).toInt()}% en actividad específica"

            LoyaltyRewardRuleType.FREE_MENU_ITEM ->
                "${(rule.quantity ?: 1).coerceAtLeast(1)} item(s) gratis"

            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE ->
                "Compra ${(rule.buyQuantity ?: 1).coerceAtLeast(1)} y recibe ${
                    (rule.freeQuantity ?: 1).coerceAtLeast(
                        1
                    )
                } gratis"
        }

    fun isEligible(level: LoyaltyLevel): Boolean = level.minimumSpent >= minimumLevel.minimumSpent

    val expirationDate: Date?
        get() {
            val days = expiresInDays ?: return null
            if (days <= 0) return null
            return Calendar.getInstance().apply {
                time = updatedAt
                add(Calendar.DAY_OF_YEAR, days)
            }.time
        }

    val isExpired: Boolean
        get() = expirationDate?.let { Date().after(it) } ?: false

    val expirationText: String?
        get() = expirationDate?.let { "Vence ${it.abbreviatedDateText()}" }

    val targetMenuItemId: String?
        get() = rule.menuItemId?.trim()?.takeIf { it.isNotEmpty() }

    val targetActivityId: String?
        get() = rule.activityId?.trim()?.takeIf { it.isNotEmpty() }
}

enum class LoyaltyRewardReferenceType {
    ORDER,
    BOOKING,
}

enum class LoyaltyWalletEventStatus {
    RESERVED,
    CONSUMED,
    RELEASED,
    EXPIRED,
}

data class LoyaltyWalletEvent(
    val id: String,
    val templateId: String,
    val templateTitle: String,
    val referenceType: LoyaltyRewardReferenceType,
    val referenceId: String,
    val status: LoyaltyWalletEventStatus,
    val amount: Double,
    val createdAt: Date,
    val updatedAt: Date,
)

data class AppliedReward(
    val id: String,
    val templateId: String,
    val title: String,
    val amount: Double,
    val note: String,
    val affectedMenuItemIds: List<String>,
    val affectedActivityIds: List<String>,
)

data class RewardWalletSnapshot(
    val nationalId: String,
    val currentLevel: LoyaltyLevel,
    val totalSpent: Double,
    val points: Int,
    val availableTemplates: List<LoyaltyRewardTemplate>,
    val reservedEvents: List<LoyaltyWalletEvent>,
    val consumedEvents: List<LoyaltyWalletEvent>,
    val releasedEvents: List<LoyaltyWalletEvent>,
) {
    companion object {
        fun empty(nationalId: String): RewardWalletSnapshot = RewardWalletSnapshot(
            nationalId = nationalId,
            currentLevel = LoyaltyLevel.BRONZE,
            totalSpent = 0.0,
            points = 0,
            availableTemplates = emptyList(),
            reservedEvents = emptyList(),
            consumedEvents = emptyList(),
            releasedEvents = emptyList(),
        )
    }
}

data class RewardComputationResult(
    val appliedRewards: List<AppliedReward>,
    val totalDiscount: Double,
    val walletSnapshot: RewardWalletSnapshot,
) {
    companion object {
        fun empty(wallet: RewardWalletSnapshot): RewardComputationResult = RewardComputationResult(
            appliedRewards = emptyList(),
            totalDiscount = 0.0,
            walletSnapshot = wallet,
        )
    }
}

data class RewardMenuLine(
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)

data class RewardActivityLine(
    val activityId: String,
    val title: String,
    val linePrice: Double,
)

data class LoyaltyRewardTemplateDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val scope: String,
    val minimumLevel: String,
    val triggerMode: String,
    val isActive: Boolean,
    val canStack: Boolean,
    val priority: Int,
    val maxUsesPerClient: Int,
    val expiresInDays: Int?,
    val rule: LoyaltyRewardRule,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {
    constructor(domain: LoyaltyRewardTemplate) : this(
        id = domain.id,
        title = domain.title,
        subtitle = domain.subtitle,
        scope = domain.scope.name.lowercase(),
        minimumLevel = domain.minimumLevel.name.lowercase(),
        triggerMode = domain.triggerMode.name.lowercase(),
        isActive = domain.isActive,
        canStack = domain.canStack,
        priority = domain.priority,
        maxUsesPerClient = domain.maxUsesPerClient.coerceAtLeast(1),
        expiresInDays = domain.expiresInDays,
        rule = domain.rule,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
    )

    fun toDomain(): LoyaltyRewardTemplate = LoyaltyRewardTemplate(
        id = id,
        title = title,
        subtitle = subtitle,
        scope = LoyaltyRewardScope.entries.firstOrNull { it.name.equals(scope, ignoreCase = true) }
            ?: LoyaltyRewardScope.BOTH,
        minimumLevel = LoyaltyLevel.entries.firstOrNull {
            it.name.equals(
                minimumLevel,
                ignoreCase = true
            )
        }
            ?: LoyaltyLevel.BRONZE,
        triggerMode = LoyaltyRewardTriggerMode.entries.firstOrNull {
            it.name.equals(
                triggerMode,
                ignoreCase = true
            )
        }
            ?: LoyaltyRewardTriggerMode.AUTOMATIC,
        isActive = isActive,
        canStack = canStack,
        priority = priority,
        maxUsesPerClient = maxUsesPerClient.coerceAtLeast(1),
        expiresInDays = expiresInDays,
        rule = rule,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt.toDate(),
    )
}

data class LoyaltyWalletEventDto(
    val id: String,
    val templateId: String,
    val templateTitle: String,
    val referenceType: String,
    val referenceId: String,
    val status: String,
    val amount: Double,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {
    constructor(domain: LoyaltyWalletEvent) : this(
        id = domain.id,
        templateId = domain.templateId,
        templateTitle = domain.templateTitle,
        referenceType = domain.referenceType.name.lowercase(),
        referenceId = domain.referenceId,
        status = domain.status.name.lowercase(),
        amount = domain.amount,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
    )

    fun toDomain(): LoyaltyWalletEvent = LoyaltyWalletEvent(
        id = id,
        templateId = templateId,
        templateTitle = templateTitle,
        referenceType = LoyaltyRewardReferenceType.entries.firstOrNull {
            it.name.equals(referenceType, ignoreCase = true)
        } ?: LoyaltyRewardReferenceType.ORDER,
        referenceId = referenceId,
        status = LoyaltyWalletEventStatus.entries.firstOrNull {
            it.name.equals(status, ignoreCase = true)
        } ?: LoyaltyWalletEventStatus.RESERVED,
        amount = amount,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt.toDate(),
    )
}

data class LoyaltyWalletDocument(
    val nationalId: String,
    val updatedAt: Date,
    val events: List<LoyaltyWalletEvent>,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/LoyaltyRewardsRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


interface LoyaltyRewardsRepositoriable {
    suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot

    fun observeWalletSnapshot(nationalId: String): Flow<RewardWalletSnapshot> = flow {
        emit(loadWalletSnapshot(nationalId))
    }

    suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult

    suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult

    suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    )

    suspend fun consumeRewards(
        nationalId: String,
        referenceId: String,
    )

    suspend fun releaseRewards(
        nationalId: String,
        referenceId: String,
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ProfileImageRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

data class UploadedProfileImage(
    val downloadURL: String,
    val storagePath: String,
)

interface ProfileImageRepositoriable {
    suspend fun cachedImageBytes(userId: String): ByteArray?
    suspend fun downloadAndCacheImage(userId: String, url: String): ByteArray?
    suspend fun saveImageBytes(userId: String, bytes: ByteArray): ByteArray
    suspend fun removeCachedImage(userId: String)
    suspend fun uploadProfileImage(profile: ClientProfile, bytes: ByteArray): UploadedProfileImage
    suspend fun deleteProfileImage(storagePath: String?)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ProfileStats.kt

```kotlin
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
            wallet = RewardWalletSnapshot.empty(nationalId = ""),
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ProfileStatsRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


interface ProfileStatsRepositoriable {
    suspend fun loadStats(nationalId: String): ProfileStats
    fun observeStats(nationalId: String): Flow<ProfileStats>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/ProfileUseCases.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


class LoadProfileStatsUseCase @Inject constructor(
    private val repository: ProfileStatsRepositoriable,
) {
    suspend fun execute(nationalId: String): ProfileStats = repository.loadStats(nationalId)
}

class ObserveProfileStatsUseCase @Inject constructor(
    private val repository: ProfileStatsRepositoriable,
) {
    fun execute(nationalId: String): Flow<ProfileStats> = repository.observeStats(nationalId)
}

class LoadProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile): ByteArray? {
        val cached = repository.cachedImageBytes(profile.id)
        if (cached != null) return cached

        val url = profile.profileImageURL?.trim().orEmpty()
        if (url.isEmpty()) return null

        return repository.downloadAndCacheImage(
            userId = profile.id,
            url = url,
        )
    }
}

class UploadProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile, bytes: ByteArray): UploadedProfileImage =
        repository.uploadProfileImage(profile = profile, bytes = bytes)
}

class DeleteProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile) {
        repository.deleteProfileImage(profile.profileImagePath)
        repository.removeCachedImage(profile.id)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/RewardPresentation.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/RewardPresentationFactory.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain



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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/presentation/view/ProfileScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.view


private enum class ProfileRoute {
    ROOT,
    EDIT,
    LOYALTY,
    PREFERENCES,
    SUPPORT,
    ACCOUNT,
}

private object ProfileLinks {
    const val instagram = "https://instagram.com/altosdelmurco"
    const val tiktok = "https://www.tiktok.com/@altosdelmurco"
    const val facebook = "https://www.facebook.com/altosdelmurco"
    const val whatsapp = "https://wa.me/593000000000"
    const val maps = "https://maps.google.com/?q=Altos+del+Murco"
    const val supportEmail = "mailto:soporte@altosdelmurco.com"
    const val privacyPolicy = "https://altosdelmurco.com/privacy"
    const val terms = "https://altosdelmurco.com/terms"
}

@Composable
fun ProfileScreen(
    sessionState: SessionState.Authenticated,
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var route by rememberSaveable { mutableStateOf(ProfileRoute.ROOT) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSignOutConfirmation by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        viewModel.onAppear(sessionState.profile)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        val selectedUri = uri ?: return@rememberLauncherForActivityResult
        val bytes = context.readBytes(selectedUri) ?: return@rememberLauncherForActivityResult
        viewModel.uploadProfileImage(bytes)
    }

    BackHandler(enabled = route != ProfileRoute.ROOT) {
        route = ProfileRoute.ROOT
    }

    state.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            confirmButton = {
                TextButton(onClick = viewModel::clearMessage) { Text("Aceptar") }
            },
            title = {
                Text(
                    when (message) {
                        is ProfileMessage.Error -> "Algo salió mal"
                        is ProfileMessage.Success -> "Listo"
                    },
                )
            },
            text = {
                Text(
                    when (message) {
                        is ProfileMessage.Error -> message.message
                        is ProfileMessage.Success -> message.message
                    },
                )
            },
        )
    }

    if (showSignOutConfirmation) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmation = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirmation = false
                        viewModel.signOut()
                    },
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmation = false }) { Text("Volver") }
            },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tu cuenta seguirá existiendo. Solo se cerrará la sesión en este dispositivo.") },
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            confirmButton = {
                TextButton(
                    enabled = !state.isDeletingAccount,
                    onClick = {
                        showDeleteConfirmation = false
                        val activity = context.findActivityOrNull() ?: return@TextButton
                        scope.launch {
                            runGoogleReauthentication(
                                activity = activity,
                                onToken = viewModel::deleteAccount,
                                onError = viewModel::presentError,
                            )
                        }
                    },
                ) { Text("Eliminar cuenta") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancelar") }
            },
            title = { Text("Eliminar cuenta definitivamente") },
            text = {
                Text("Se eliminará tu perfil de cliente y Firebase pedirá una credencial reciente de Google antes de borrar la cuenta.")
            },
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (route) {
            ProfileRoute.ROOT -> ProfileHomeScreen(
                state = state,
                onRefresh = viewModel::refresh,
                onPickImage = { imagePicker.launch("image/*") },
                onRemoveImage = viewModel::removeProfileImage,
                onOpenEdit = {
                    viewModel.beginEditProfile()
                    route = ProfileRoute.EDIT
                },
                onOpenLoyalty = { route = ProfileRoute.LOYALTY },
                onOpenPreferences = { route = ProfileRoute.PREFERENCES },
                onOpenSupport = { route = ProfileRoute.SUPPORT },
                onOpenAccount = { route = ProfileRoute.ACCOUNT },
            )

            ProfileRoute.EDIT -> EditProfileScreen(
                state = state,
                onBack = {
                    viewModel.cancelEditProfile()
                    route = ProfileRoute.ROOT
                },
                onFullNameChanged = viewModel::onEditFullNameChanged,
                onNationalIdChanged = viewModel::onEditNationalIdChanged,
                onPhoneChanged = viewModel::onEditPhoneChanged,
                onBirthdayChanged = viewModel::onEditBirthdayChanged,
                onAddressChanged = viewModel::onEditAddressChanged,
                onEmergencyNameChanged = viewModel::onEditEmergencyNameChanged,
                onEmergencyPhoneChanged = viewModel::onEditEmergencyPhoneChanged,
                onSave = viewModel::saveEditedProfile,
            )

            ProfileRoute.LOYALTY -> LoyaltyProgramScreen(
                stats = state.stats,
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.PREFERENCES -> PreferencesScreen(
                currentThemeMode = currentThemeMode,
                onThemeModeSelected = onThemeModeSelected,
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.SUPPORT -> SupportScreen(
                onBack = { route = ProfileRoute.ROOT },
            )

            ProfileRoute.ACCOUNT -> AccountActionsScreen(
                state = state,
                onBack = { route = ProfileRoute.ROOT },
                onSignOut = { showSignOutConfirmation = true },
                onDeleteAccount = { showDeleteConfirmation = true },
            )
        }
    }
}

@Composable
private fun ProfileHomeScreen(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onOpenEdit: () -> Unit,
    onOpenLoyalty: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAccount: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refrescar")
            }
        }

        ProfileHeaderCard(
            state = state,
            onPickImage = onPickImage,
            onRemoveImage = onRemoveImage,
        )

        ProfileStatsSection(
            state = state,
            onOpenLoyalty = onOpenLoyalty,
        )

        ProfileCard {
            SectionTitle(
                title = "Tu cuenta",
                subtitle = "Datos personales, preferencias y seguridad.",
            )
            ProfileMenuRow(
                title = "Editar perfil",
                subtitle = "Nombre, cédula, teléfono, dirección y contacto de emergencia",
                icon = Icons.Rounded.Edit,
                onClick = onOpenEdit,
            )
            ProfileMenuRow(
                title = "Murco Loyalty",
                subtitle = "Niveles, puntos y premios disponibles",
                icon = Icons.Rounded.EmojiEvents,
                onClick = onOpenLoyalty,
            )
            ProfileMenuRow(
                title = "Preferencias",
                subtitle = "Tema visual y permisos de la app",
                icon = Icons.Rounded.Settings,
                onClick = onOpenPreferences,
            )
            ProfileMenuRow(
                title = "Soporte",
                subtitle = "WhatsApp, redes, ubicación y políticas",
                icon = Icons.Rounded.SupportAgent,
                onClick = onOpenSupport,
            )
            ProfileMenuRow(
                title = "Acciones de la cuenta",
                subtitle = "Cerrar sesión o eliminar cuenta",
                icon = Icons.Rounded.Security,
                destructive = true,
                onClick = onOpenAccount,
            )
        }

        ProfileCard {
            SectionTitle(
                title = "Altos del Murco",
                subtitle = "Síguenos o encuentra la ubicación del restaurante.",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { uriHandler.openUri(ProfileLinks.instagram) },
                    label = { Text("Instagram") },
                    leadingIcon = { Icon(Icons.Rounded.OpenInNew, contentDescription = null) },
                )
                AssistChip(
                    onClick = { uriHandler.openUri(ProfileLinks.tiktok) },
                    label = { Text("TikTok") },
                    leadingIcon = { Icon(Icons.Rounded.OpenInNew, contentDescription = null) },
                )
            }
            OutlinedButton(
                onClick = { uriHandler.openUri(ProfileLinks.maps) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.Map, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Abrir ubicación")
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ProfileHeaderCard(
    state: ProfileUiState,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
) {
    ProfileCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileAvatar(
                    avatarBytes = state.avatarBytes,
                    initials = state.initials,
                    isLoading = state.isLoadingAvatar || state.isUploadingProfileImage,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.hasProfileImage) {
                        IconButton(
                            onClick = onRemoveImage,
                            enabled = !state.isUploadingProfileImage,
                            modifier = Modifier
                                .size(42.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape),
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Eliminar foto",
                                tint = MaterialTheme.colorScheme.onError,
                            )
                        }
                    }

                    IconButton(
                        onClick = onPickImage,
                        enabled = !state.isUploadingProfileImage,
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    ) {
                        Icon(
                            Icons.Rounded.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.emailText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Miembro desde ${state.memberSinceText}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Teléfono",
                    value = state.phoneText,
                    icon = Icons.Rounded.Phone,
                )
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Cumpleaños",
                    value = state.birthdayText,
                    icon = Icons.Rounded.Cake,
                )
            }

            InfoRow(
                title = "Dirección",
                value = state.addressText,
                icon = Icons.Rounded.Home,
            )
            InfoRow(
                title = "Contacto de emergencia",
                value = state.emergencyContactText,
                icon = Icons.Rounded.AccountCircle,
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    avatarBytes: ByteArray?,
    initials: String,
    isLoading: Boolean,
) {
    val imageBitmap = remember(avatarBytes) {
        avatarBytes?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), CircleShape),
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.34f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    state: ProfileUiState,
    onOpenLoyalty: () -> Unit,
) {
    ProfileCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(
                title = "Resumen",
                subtitle = "Solo cuentan pedidos y reservas completadas.",
                modifier = Modifier.weight(1f),
            )
            if (state.isLoadingStats) CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        }

        LevelSummaryCard(stats = state.stats, onClick = onOpenLoyalty)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Puntos",
                value = state.stats.points.toString(),
                icon = Icons.Rounded.Star,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Pedidos",
                value = state.stats.completedOrders.toString(),
                icon = Icons.Rounded.Restaurant,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Reservas",
                value = state.stats.completedBookings.toString(),
                icon = Icons.Rounded.Explore,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Restaurante",
                value = state.stats.restaurantSpent.priceText(),
                icon = Icons.Rounded.ShoppingBag,
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Aventura",
                value = state.stats.adventureSpent.priceText(),
                icon = Icons.Rounded.EventAvailable,
            )
        }
    }
}

@Composable
private fun LevelSummaryCard(
    stats: ProfileStats,
    onClick: () -> Unit,
) {
    val level = stats.level
    val next = level.nextLevel
    val progress = LoyaltyLevel.progress(stats.totalSpent).toFloat()
    val remaining = next?.let { (it.minimumSpent - stats.totalSpent).coerceAtLeast(0.0) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nivel ${level.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = level.badgeSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    )
                }
                Icon(Icons.Rounded.OpenInNew, contentDescription = null)
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = if (next == null) {
                    "Ya estás en el máximo nivel. Total acumulado: ${stats.totalSpent.priceText()}"
                } else {
                    "Te faltan ${remaining?.priceText()} para llegar a ${next.title}."
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun EditProfileScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onNationalIdChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onBirthdayChanged: (java.util.Date) -> Unit,
    onAddressChanged: (String) -> Unit,
    onEmergencyNameChanged: (String) -> Unit,
    onEmergencyPhoneChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val edit = state.editState ?: return
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) { Text("Cancelar") }
                    Button(
                        onClick = onSave,
                        enabled = edit.canSave && !state.isSavingProfile,
                        modifier = Modifier.weight(1.4f),
                    ) {
                        if (state.isSavingProfile) CircularProgressIndicator(
                            modifier = Modifier.size(
                                18.dp
                            ), strokeWidth = 2.dp
                        )
                        else Text("Guardar")
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScreenHeader(
                title = "Editar perfil",
                subtitle = "Esta información se usa para pedidos, reservas, beneficios y contacto.",
                onBack = onBack,
            )

            ProfileCard {
                SectionTitle("Datos personales", "Los campos obligatorios deben estar completos.")
                OutlinedTextField(
                    value = edit.fullName,
                    onValueChange = onFullNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                )
                OutlinedTextField(
                    value = state.emailText,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true,
                    label = { Text("Correo") },
                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.nationalId,
                    onValueChange = onNationalIdChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Cédula") },
                    leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.phoneNumber,
                    onValueChange = onPhoneChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("WhatsApp") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                )
                OutlinedButton(
                    onClick = { showBirthdayPicker(context, edit, onBirthdayChanged) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(edit.birthday.formatDateLong())
                }
            }

            ProfileCard {
                SectionTitle(
                    "Ubicación y emergencia",
                    "Ayuda al equipo a coordinar mejor cualquier visita."
                )
                OutlinedTextField(
                    value = edit.address,
                    onValueChange = onAddressChanged,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    label = { Text("Dirección") },
                    leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.emergencyContactName,
                    onValueChange = onEmergencyNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Contacto de emergencia") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                )
                OutlinedTextField(
                    value = edit.emergencyContactPhone,
                    onValueChange = onEmergencyPhoneChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Teléfono de emergencia") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                )
            }

            Spacer(Modifier.height(86.dp))
        }
    }
}

@Composable
private fun LoyaltyProgramScreen(
    stats: ProfileStats,
    onBack: () -> Unit,
) {
    val wallet = stats.wallet
    val level = stats.level
    val next = level.nextLevel
    val progress = LoyaltyLevel.progress(stats.totalSpent).toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Murco Loyalty",
            subtitle = "Niveles, puntos y premios automáticos del restaurante y aventura.",
            onBack = onBack,
        )

        ProfileCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Nivel ${level.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(level.badgeSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    Modifier.weight(1f),
                    "Total",
                    stats.totalSpent.priceText(),
                    Icons.Rounded.Star
                )
                StatTile(
                    Modifier.weight(1f),
                    "Puntos",
                    stats.points.toString(),
                    Icons.Rounded.EmojiEvents
                )
            }

            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            Text(
                text = if (next == null) {
                    "Ya estás en la cima del programa."
                } else {
                    "Próximo nivel: ${next.title}. Te faltan ${
                        (next.minimumSpent - stats.totalSpent).coerceAtLeast(
                            0.0
                        ).priceText()
                    }."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ProfileCard {
            SectionTitle(
                "Beneficios actuales",
                "Esto es lo que representa tu nivel ${level.title}."
            )
            level.benefits.forEach { benefit ->
                InfoRow(
                    title = benefit,
                    value = level.spendRangeText,
                    icon = Icons.Rounded.CheckCircle
                )
            }
        }

        RewardTemplateSection(
            title = "Premios disponibles",
            subtitle = "Se aplican automáticamente cuando tu pedido o reserva cumple la regla.",
            emptyText = "Todavía no tienes premios automáticos disponibles para tu nivel.",
            rows = wallet.availableTemplates.map { template ->
                RewardRowData(
                    title = template.title,
                    subtitle = template.subtitle,
                    value = template.displaySummary,
                )
            },
        )

        RewardEventSection(
            title = "Premios reservados",
            subtitle = "Ya están apartados en pedidos o reservas pendientes.",
            emptyText = "No tienes premios reservados ahora mismo.",
            events = wallet.reservedEvents,
            status = LoyaltyWalletEventStatus.RESERVED,
        )

        RewardEventSection(
            title = "Historial de premios usados",
            subtitle = "Beneficios ya consumidos.",
            emptyText = "Todavía no has usado premios.",
            events = wallet.consumedEvents,
            status = LoyaltyWalletEventStatus.CONSUMED,
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun PreferencesScreen(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Preferencias",
            subtitle = "Apariencia y ajustes del dispositivo.",
            onBack = onBack,
        )

        ProfileCard {
            SectionTitle("Apariencia", "Elige cómo quieres ver Altos del Murco.")
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentThemeMode == mode,
                    onClick = { onThemeModeSelected(mode) },
                    label = { Text(mode.displayTitle()) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (mode) {
                                ThemeMode.SYSTEM -> Icons.Rounded.AutoMode
                                ThemeMode.LIGHT -> Icons.Rounded.LightMode
                                ThemeMode.DARK -> Icons.Rounded.DarkMode
                            },
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        ProfileCard {
            ProfileMenuRow(
                title = "Permisos de la app",
                subtitle = "Notificaciones, cámara, imágenes y ajustes del dispositivo",
                icon = Icons.Rounded.Palette,
                onClick = {
                    val intent = Intent(
                        AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )

                    context.startActivity(intent)
                },
            )
            Text(
                text = "Para cambiar permisos, abre Ajustes > Apps > Altos del Murco en tu dispositivo Android.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SupportScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Soporte",
            subtitle = "Contactos, redes sociales y documentos de la app.",
            onBack = onBack,
        )
        ProfileCard {
            ProfileMenuRow(
                "WhatsApp",
                "Escríbenos para reservas o ayuda",
                Icons.Rounded.Phone
            ) { uriHandler.openUri(ProfileLinks.whatsapp) }
            ProfileMenuRow(
                "Instagram",
                "@altosdelmurco",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.instagram) }
            ProfileMenuRow(
                "TikTok",
                "Videos, promociones y experiencias",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.tiktok) }
            ProfileMenuRow(
                "Facebook",
                "Comunidad y novedades",
                Icons.Rounded.OpenInNew
            ) { uriHandler.openUri(ProfileLinks.facebook) }
            ProfileMenuRow(
                "Ubicación",
                "Abrir en Google Maps",
                Icons.Rounded.Map
            ) { uriHandler.openUri(ProfileLinks.maps) }
        }
        ProfileCard {
            ProfileMenuRow(
                "Correo de soporte",
                "soporte@altosdelmurco.com",
                Icons.Rounded.Email
            ) { uriHandler.openUri(ProfileLinks.supportEmail) }
            ProfileMenuRow(
                "Privacidad",
                "Política de privacidad",
                Icons.Rounded.Security
            ) { uriHandler.openUri(ProfileLinks.privacyPolicy) }
            ProfileMenuRow(
                "Términos",
                "Términos y condiciones",
                Icons.Rounded.Info
            ) { uriHandler.openUri(ProfileLinks.terms) }
        }
    }
}

@Composable
private fun AccountActionsScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            title = "Acciones de la cuenta",
            subtitle = "Estas acciones afectan tu sesión y tu perfil.",
            onBack = onBack,
        )

        ProfileCard {
            DangerRow(
                title = "Cerrar sesión",
                subtitle = "Cierra tu sesión actual en este dispositivo",
                icon = Icons.Rounded.Logout,
                enabled = !state.isSigningOut && !state.isDeletingAccount,
                onClick = onSignOut,
            )
            DangerRow(
                title = "Eliminar cuenta",
                subtitle = "Elimina permanentemente tu cuenta y perfil",
                icon = Icons.Rounded.Delete,
                enabled = !state.isSigningOut && !state.isDeletingAccount,
                onClick = onDeleteAccount,
            )

            AnimatedVisibility(state.isSigningOut || state.isDeletingAccount) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun RewardTemplateSection(
    title: String,
    subtitle: String,
    emptyText: String,
    rows: List<RewardRowData>,
) {
    ProfileCard {
        SectionTitle(title, subtitle)
        if (rows.isEmpty()) {
            Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            rows.forEach { row ->
                InfoRow(
                    title = row.title,
                    value = "${row.subtitle}\n${row.value}",
                    icon = Icons.Rounded.EmojiEvents
                )
            }
        }
    }
}

@Composable
private fun RewardEventSection(
    title: String,
    subtitle: String,
    emptyText: String,
    events: List<LoyaltyWalletEvent>,
    status: LoyaltyWalletEventStatus,
) {
    ProfileCard {
        SectionTitle(title, subtitle)
        if (events.isEmpty()) {
            Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            events.forEach { event ->
                val referenceLabel = when (event.referenceType) {
                    LoyaltyRewardReferenceType.ORDER -> "Pedido"
                    LoyaltyRewardReferenceType.BOOKING -> "Reserva"
                }
                InfoRow(
                    title = event.templateTitle,
                    value = "$referenceLabel ${event.referenceId.take(8)} • ${event.amount.priceText()}",
                    icon = if (status == LoyaltyWalletEventStatus.RESERVED) Icons.Rounded.EventAvailable else Icons.Rounded.CheckCircle,
                )
            }
        }
    }
}

private data class RewardRowData(
    val title: String,
    val subtitle: String,
    val value: String,
)

@Composable
private fun ScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompactInfoCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String,
    icon: ImageVector,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconBubble(icon)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = modifier.aspectRatio(1.15f),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBubble(icon, destructive)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Rounded.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DangerRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    destructive: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (destructive) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
    }
}

private fun showBirthdayPicker(
    context: Context,
    edit: EditProfileUiState,
    onPicked: (java.util.Date) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { time = edit.birthday }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val picked = Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPicked(picked.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun Context.readBytes(uri: Uri): ByteArray? = runCatching {
    contentResolver.openInputStream(uri)?.use { it.readBytes() }
}.getOrNull()

private suspend fun runGoogleReauthentication(
    activity: Activity,
    onToken: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val credentialManager = CredentialManager.create(activity)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result: GetCredentialResponse = credentialManager.getCredential(activity, request)
        val credential = result.credential
        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCredential.idToken)
        } else {
            onError("Credential Manager devolvió una credencial no compatible.")
        }
    } catch (_: GetCredentialCancellationException) {
        onError("Reautenticación cancelada.")
    } catch (_: NoCredentialException) {
        onError("No se encontró una cuenta de Google disponible.")
    } catch (error: GetCredentialException) {
        onError(error.message ?: "No se pudo reautenticar con Google.")
    } catch (error: Exception) {
        onError(error.message ?: "No se pudo reautenticar con Google.")
    }
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/presentation/viewmodel/ProfileUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel


sealed interface ProfileMessage {
    data class Error(val message: String) : ProfileMessage
    data class Success(val message: String) : ProfileMessage
}

data class ProfileUiState(
    val profile: ClientProfile? = null,
    val stats: ProfileStats = ProfileStats.EMPTY,
    val avatarBytes: ByteArray? = null,
    val isLoadingAvatar: Boolean = false,
    val isLoadingStats: Boolean = false,
    val isUploadingProfileImage: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isSigningOut: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val editState: EditProfileUiState? = null,
    val message: ProfileMessage? = null,
) {
    val displayName: String
        get() = profile?.fullName?.takeIf { it.isNotBlank() } ?: "Invitado"

    val emailText: String
        get() = profile?.email?.takeIf { it.isNotBlank() } ?: "Correo oculto"

    val phoneText: String
        get() = profile?.phoneNumber?.takeIf { it.isNotBlank() } ?: "No registrado"

    val nationalIdText: String
        get() = profile?.nationalId?.takeIf { it.isNotBlank() } ?: "No registrado"

    val birthdayText: String
        get() = profile?.birthday?.formatDateLong() ?: "No registrado"

    val addressText: String
        get() = profile?.address?.takeIf { it.isNotBlank() } ?: "No registrado"

    val emergencyContactText: String
        get() {
            val name = profile?.emergencyContactName?.takeIf { it.isNotBlank() } ?: "No registrado"
            val phone = profile?.emergencyContactPhone?.takeIf { it.isNotBlank() }
            return if (phone == null) name else "$name • $phone"
        }

    val memberSinceText: String
        get() = profile?.createdAt?.formatDateShort() ?: "Ahora"

    val initials: String
        get() = displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "AM" }

    val hasProfileImage: Boolean
        get() = avatarBytes != null || profile?.hasProfileImage == true
}

data class EditProfileUiState(
    val fullName: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = defaultBirthday(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
) {
    val canSave: Boolean
        get() = fullName.trim().isNotEmpty() &&
                nationalId.onlyDigits().length >= 8 &&
                phoneNumber.onlyDigits().length >= 8 &&
                address.trim().isNotEmpty() &&
                emergencyContactName.trim().isNotEmpty() &&
                emergencyContactPhone.onlyDigits().length >= 8

    companion object {
        fun fromProfile(profile: ClientProfile): EditProfileUiState = EditProfileUiState(
            fullName = profile.fullName,
            nationalId = profile.nationalId,
            phoneNumber = profile.phoneNumber,
            birthday = profile.birthday,
            address = profile.address,
            emergencyContactName = profile.emergencyContactName,
            emergencyContactPhone = profile.emergencyContactPhone,
        )
    }
}

data class AccountActionUiState(
    val isBusy: Boolean = false,
    val needsFreshGoogleToken: Boolean = false,
)

fun ClientProfile.updatedFromEdit(edit: EditProfileUiState): ClientProfile = copy(
    fullName = edit.fullName.trim(),
    nationalId = edit.nationalId.onlyDigits(),
    phoneNumber = edit.phoneNumber.onlyDigits(),
    birthday = edit.birthday,
    address = edit.address.trim(),
    emergencyContactName = edit.emergencyContactName.trim(),
    emergencyContactPhone = edit.emergencyContactPhone.onlyDigits(),
    isProfileComplete = true,
    updatedAt = Date(),
    profileCompletedAt = profileCompletedAt ?: Date(),
)

fun ThemeMode.displayTitle(): String = when (this) {
    ThemeMode.SYSTEM -> "Sistema"
    ThemeMode.LIGHT -> "Claro"
    ThemeMode.DARK -> "Oscuro"
}

fun Date.formatDateShort(): String = SimpleDateFormat("d MMM yyyy", Locale("es", "EC")).format(this)
fun Date.formatDateLong(): String = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "EC")).format(this)
fun String.onlyDigits(): String = filter(Char::isDigit)

private fun defaultBirthday(): Date = Calendar.getInstance().apply {
    add(Calendar.YEAR, -18)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/presentation/viewmodel/ProfileViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeProfileStatsUseCase: ObserveProfileStatsUseCase,
    private val loadProfileImageUseCase: LoadProfileImageUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val deleteProfileImageUseCase: DeleteProfileImageUseCase,
    private val completeClientProfileUseCase: CompleteClientProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteCurrentAccountUseCase: DeleteCurrentAccountUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var statsJob: Job? = null
    private var avatarJob: Job? = null

    fun onAppear(profile: ClientProfile) {
        val currentProfile = _uiState.value.profile
        if (currentProfile?.id != profile.id || currentProfile.updatedAt != profile.updatedAt) {
            _uiState.update {
                it.copy(
                    profile = profile,
                    editState = null,
                    message = null,
                )
            }
            loadAvatar(profile)
        }

        observeStats(profile)
    }

    fun refresh() {
        val profile = _uiState.value.profile ?: return
        observeStats(profile, forceRestart = true)
        loadAvatar(profile, forceReload = true)
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun presentError(message: String) {
        _uiState.update { it.copy(message = ProfileMessage.Error(message)) }
    }

    fun beginEditProfile() {
        val profile = _uiState.value.profile ?: return
        _uiState.update { it.copy(editState = EditProfileUiState.fromProfile(profile)) }
    }

    fun cancelEditProfile() {
        _uiState.update { it.copy(editState = null) }
    }

    fun onEditFullNameChanged(value: String) = updateEdit { copy(fullName = value) }
    fun onEditNationalIdChanged(value: String) = updateEdit { copy(nationalId = value) }
    fun onEditPhoneChanged(value: String) = updateEdit { copy(phoneNumber = value) }
    fun onEditBirthdayChanged(value: Date) = updateEdit { copy(birthday = value) }
    fun onEditAddressChanged(value: String) = updateEdit { copy(address = value) }
    fun onEditEmergencyNameChanged(value: String) = updateEdit { copy(emergencyContactName = value) }
    fun onEditEmergencyPhoneChanged(value: String) = updateEdit { copy(emergencyContactPhone = value) }

    fun saveEditedProfile() {
        val profile = _uiState.value.profile ?: return
        val edit = _uiState.value.editState ?: return

        if (!edit.canSave) {
            _uiState.update {
                it.copy(message = ProfileMessage.Error("Completa correctamente todos los campos obligatorios."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingProfile = true, message = null) }

            runCatching {
                val updated = profile.updatedFromEdit(edit)
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        editState = null,
                        isSavingProfile = false,
                        message = ProfileMessage.Success("Perfil actualizado correctamente."),
                    )
                }
                observeStats(updated, forceRestart = true)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        message = ProfileMessage.Error(error.message ?: "No se pudo actualizar el perfil."),
                    )
                }
            }
        }
    }

    fun uploadProfileImage(bytes: ByteArray) {
        val profile = _uiState.value.profile ?: return
        if (bytes.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingProfileImage = true, message = null) }

            runCatching {
                val uploaded = uploadProfileImageUseCase.execute(profile, bytes)
                val updated = profile.copy(
                    profileImageURL = uploaded.downloadURL,
                    profileImagePath = uploaded.storagePath,
                    updatedAt = Date(),
                )
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        avatarBytes = bytes,
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Success("Foto de perfil actualizada."),
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Error(error.message ?: "No se pudo subir la foto."),
                    )
                }
            }
        }
    }

    fun removeProfileImage() {
        val profile = _uiState.value.profile ?: return
        if (!profile.hasProfileImage && _uiState.value.avatarBytes == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingProfileImage = true, message = null) }

            runCatching {
                deleteProfileImageUseCase.execute(profile)
                val updated = profile.copy(
                    profileImageURL = null,
                    profileImagePath = null,
                    updatedAt = Date(),
                )
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        avatarBytes = null,
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Success("Foto de perfil eliminada."),
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Error(error.message ?: "No se pudo eliminar la foto."),
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true, message = null) }

            runCatching {
                signOutUseCase.execute()
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(message = ProfileMessage.Error(error.message ?: "No se pudo cerrar sesión."))
                }
            }

            _uiState.update { it.copy(isSigningOut = false) }
        }
    }

    fun deleteAccount(freshGoogleIdToken: String) {
        val profile = _uiState.value.profile ?: return
        if (freshGoogleIdToken.isBlank()) {
            _uiState.update {
                it.copy(message = ProfileMessage.Error("Google no devolvió una credencial válida."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, message = null) }

            runCatching {
                deleteProfileImageUseCase.execute(profile)
                deleteCurrentAccountUseCase.execute(
                    currentUserId = profile.id,
                    googleIdToken = freshGoogleIdToken,
                )
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        message = ProfileMessage.Error(error.message ?: "No se pudo eliminar la cuenta."),
                    )
                }
            }
        }
    }

    private fun loadAvatar(profile: ClientProfile, forceReload: Boolean = false) {
        avatarJob?.cancel()
        avatarJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAvatar = true) }

            runCatching {
                if (forceReload && !profile.profileImageURL.isNullOrBlank()) {
                    loadProfileImageUseCase.execute(profile.copy(profileImageURL = profile.profileImageURL))
                } else {
                    loadProfileImageUseCase.execute(profile)
                }
            }.onSuccess { bytes ->
                _uiState.update { it.copy(avatarBytes = bytes, isLoadingAvatar = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingAvatar = false) }
            }
        }
    }

    private fun observeStats(profile: ClientProfile, forceRestart: Boolean = false) {
        val nationalId = profile.nationalId.onlyDigits()
        if (nationalId.isEmpty()) {
            statsJob?.cancel()
            _uiState.update { it.copy(stats = ProfileStats.Companion.EMPTY, isLoadingStats = false) }
            return
        }

        if (!forceRestart && statsJob?.isActive == true) return

        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStats = true) }

            observeProfileStatsUseCase.execute(nationalId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingStats = false,
                            message = ProfileMessage.Error(error.message ?: "No se pudieron cargar tus estadísticas."),
                        )
                    }
                }
                .collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                            isLoadingStats = false,
                        )
                    }
                }
        }
    }

    private inline fun updateEdit(
        transform: EditProfileUiState.() -> EditProfileUiState,
    ) {
        _uiState.update { state ->
            val edit = state.editState ?: return@update state
            state.copy(editState = edit.transform())
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartDao.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local


@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_drafts WHERE id = :draftId")
    fun observeCart(draftId: String = CartDraftEntity.DEFAULT_ID): Flow<CartDraftWithItems?>

    @Upsert
    suspend fun upsertDraft(draft: CartDraftEntity)

    @Upsert
    suspend fun upsertItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_items WHERE draftId = :draftId")
    suspend fun deleteItemsForDraft(draftId: String = CartDraftEntity.DEFAULT_ID)

    @Query("DELETE FROM cart_drafts WHERE id = :draftId")
    suspend fun deleteDraft(draftId: String = CartDraftEntity.DEFAULT_ID)

    @Transaction
    suspend fun replaceDraft(
        draft: CartDraftEntity,
        items: List<CartItemEntity>,
    ) {
        deleteItemsForDraft(draft.id)
        upsertDraft(draft)
        if (items.isNotEmpty()) upsertItems(items)
    }

    @Transaction
    suspend fun clearAll(draftId: String = CartDraftEntity.DEFAULT_ID) {
        deleteItemsForDraft(draftId)
        deleteDraft(draftId)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartDraftEntity.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local


@Entity(tableName = "cart_drafts")
data class CartDraftEntity(
    @PrimaryKey val id: String = DEFAULT_ID,
    val nationalId: String?,
    val clientName: String,
    val tableNumber: String,
    val revision: Int?,
    val lastConfirmedRevision: Int?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
) {
    companion object {
        const val DEFAULT_ID: String = "active_cart"
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartDraftRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local


internal fun OrderDraft.toEntity(): CartDraftEntity = CartDraftEntity(
    id = id,
    nationalId = nationalId,
    clientName = clientName,
    tableNumber = tableNumber,
    revision = revision,
    lastConfirmedRevision = lastConfirmedRevision,
    createdAtMillis = createdAt.time,
    updatedAtMillis = updatedAt.time,
)

internal fun CartItem.toEntity(draftId: String): CartItemEntity = CartItemEntity(
    id = id,
    draftId = draftId,
    menuItemId = menuItem.id,
    categoryId = menuItem.categoryId,
    categoryTitle = menuItem.categoryTitle,
    name = menuItem.name,
    description = menuItem.description,
    notes = menuItem.notes,
    ingredients = menuItem.ingredients,
    quantity = quantity,
    unitPrice = unitPrice,
    offerPrice = menuItem.offerPrice,
    imageURL = menuItem.imageURL,
    isAvailable = menuItem.isAvailable,
    remainingQuantity = menuItem.remainingQuantity,
    isFeatured = menuItem.isFeatured,
    sortOrder = menuItem.sortOrder,
    itemNotes = notes,
)

internal fun CartDraftWithItems.toDomain(): OrderDraft = OrderDraft(
    id = draft.id,
    nationalId = draft.nationalId,
    clientName = draft.clientName,
    tableNumber = draft.tableNumber,
    createdAt = Date(draft.createdAtMillis),
    updatedAt = Date(draft.updatedAtMillis),
    items = items.map { item ->
        CartItem(
            id = item.id,
            menuItem = MenuItem(
                id = item.menuItemId,
                categoryId = item.categoryId,
                categoryTitle = item.categoryTitle,
                name = item.name,
                description = item.description,
                notes = item.notes,
                ingredients = item.ingredients,
                price = item.unitPrice,
                offerPrice = item.offerPrice,
                imageURL = item.imageURL,
                isAvailable = item.isAvailable,
                remainingQuantity = item.remainingQuantity,
                isFeatured = item.isFeatured,
                sortOrder = item.sortOrder,
            ),
            quantity = item.quantity.coerceAtLeast(1),
            notes = item.itemNotes,
        )
    },
    revision = draft.revision,
    lastConfirmedRevision = draft.lastConfirmedRevision,
)

@Singleton
class CartDraftRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartDraftRepositoriable {

    override fun observeDraft(): Flow<OrderDraft> = cartDao.observeCart().map { stored ->
        stored?.toDomain() ?: OrderDraft()
    }

    override suspend fun saveDraft(draft: OrderDraft) {
        cartDao.replaceDraft(
            draft = draft.copy(updatedAt = Date()).toEntity(),
            items = draft.items.map { it.toEntity(draft.id) },
        )
    }

    override suspend fun clear() {
        cartDao.clearAll()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartDraftWithItems.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local



data class CartDraftWithItems(
    @Embedded val draft: CartDraftEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "draftId",
    )
    val items: List<CartItemEntity>,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartItemEntity.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local



@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartDraftEntity::class,
            parentColumns = ["id"],
            childColumns = ["draftId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("draftId")],
)
data class CartItemEntity(
    @PrimaryKey val id: String,
    val draftId: String = CartDraftEntity.DEFAULT_ID,
    val menuItemId: String,
    val categoryId: String,
    val categoryTitle: String,
    val name: String,
    val description: String,
    val notes: String?,
    val ingredients: List<String>,
    val quantity: Int,
    val unitPrice: Double,
    val offerPrice: Double?,
    val imageURL: String?,
    val isAvailable: Boolean,
    val remainingQuantity: Int,
    val isFeatured: Boolean,
    val sortOrder: Int,
    val itemNotes: String?,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/MenuItemDto.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote



data class MenuItemDto(
    val id: String = "",
    val categoryId: String = "",
    val categoryTitle: String = "",
    val name: String = "",
    val description: String = "",
    val notes: String? = null,
    val ingredients: List<String> = emptyList(),
    val price: Double = 0.0,
    val offerPrice: Double? = null,
    val imageURL: String? = null,
    val isAvailable: Boolean = true,
    val remainingQuantity: Int = 0,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
) {
    fun toDomain(documentId: String? = null): MenuItem {
        val resolvedId = id.trim().ifBlank { documentId.orEmpty().trim() }
        val resolvedCategoryTitle =
            categoryTitle.trim().ifBlank { categoryId.toReadableCategoryTitle() }
        val resolvedCategoryId =
            categoryId.trim().ifBlank { resolvedCategoryTitle.toCategorySlug() }

        return MenuItem(
            id = resolvedId,
            categoryId = resolvedCategoryId,
            categoryTitle = resolvedCategoryTitle,
            name = name.trim(),
            description = description.trim(),
            notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            ingredients = ingredients.map { it.trim() }.filter { it.isNotEmpty() },
            price = price.coerceAtLeast(0.0),
            offerPrice = offerPrice?.takeIf { it in 0.0..price },
            imageURL = imageURL?.trim()?.takeIf { it.isNotEmpty() },
            isAvailable = isAvailable,
            remainingQuantity = remainingQuantity.coerceAtLeast(0),
            isFeatured = isFeatured,
            sortOrder = sortOrder,
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): MenuItemDto? {
            val id = document.stringValue("id").ifBlank { document.id }
            val name = document.stringValue("name")
            if (id.isBlank() || name.isBlank()) return null

            val price = document.doubleValue("price")
            val offerPrice = document.doubleValueOrNull("offerPrice") ?: document.doubleValueOrNull(
                "offer_price"
            )

            return MenuItemDto(
                id = id,
                categoryId = document.stringValue("categoryId").ifBlank {
                    document.stringValue("category_id")
                },
                categoryTitle = document.stringValue("categoryTitle").ifBlank {
                    document.stringValue("category_title")
                },
                name = name,
                description = document.stringValue("description"),
                notes = document.stringValueOrNull("notes"),
                ingredients = document.stringList("ingredients"),
                price = price,
                offerPrice = offerPrice,
                imageURL = document.stringValueOrNull("imageURL")
                    ?: document.stringValueOrNull("imageUrl")
                    ?: document.stringValueOrNull("image_url"),
                isAvailable = document.boolValue(
                    "isAvailable", default = document.boolValue("available", default = true)
                ),
                remainingQuantity = document.intValue(
                    "remainingQuantity",
                    default = document.intValue("remaining_quantity", default = 0)
                ),
                isFeatured = document.boolValue(
                    "isFeatured", default = document.boolValue("featured", default = false)
                ),
                sortOrder = document.intValue(
                    "sortOrder", default = document.intValue("sort_order", default = 0)
                ),
                createdAt = document.get("createdAt") as? Timestamp,
                updatedAt = document.get("updatedAt") as? Timestamp,
            )
        }
    }
}

private fun DocumentSnapshot.stringValue(field: String): String = stringValueOrNull(field).orEmpty()

private fun DocumentSnapshot.stringValueOrNull(field: String): String? = getString(field)?.trim()

private fun DocumentSnapshot.boolValue(field: String, default: Boolean): Boolean =
    getBoolean(field) ?: default

private fun DocumentSnapshot.intValue(field: String, default: Int): Int =
    when (val value = get(field)) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        else -> default
    }

private fun DocumentSnapshot.doubleValue(field: String): Double = doubleValueOrNull(field) ?: 0.0

private fun DocumentSnapshot.doubleValueOrNull(field: String): Double? =
    when (val value = get(field)) {
        is Double -> value
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        is Number -> value.toDouble()
        else -> null
    }

private fun DocumentSnapshot.stringList(field: String): List<String> {
    val value = get(field) as? List<*> ?: return emptyList()
    return value.mapNotNull { it as? String }.map { it.trim() }.filter { it.isNotEmpty() }
}

private fun String.toCategorySlug(): String =
    trim().lowercase().replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o")
        .replace("ú", "u").replace("ñ", "n").replace(Regex("[^a-z0-9]+"), "-").trim('-')
        .ifBlank { "otros" }

private fun String.toReadableCategoryTitle(): String =
    trim().replace("-", " ").replace("_", " ").split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.titlecase() } }
        .ifBlank { "Otros" }

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/MenuRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote


@Singleton
class MenuRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MenuRepositoriable {

    override fun observeMenu(): Flow<List<MenuSection>> = callbackFlow {
        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val items = snapshot.documents.mapNotNull { document ->
                            MenuItemDto.fromDocument(document)
                                ?.toDomain(documentId = document.id)
                                ?.takeIf { it.id.isNotBlank() && it.name.isNotBlank() }
                        }
                        trySend(groupIntoSections(items)).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }

    private fun groupIntoSections(items: List<MenuItem>): List<MenuSection> {
        return items
            .distinctBy { it.id }
            .groupBy { it.categoryId.ifBlank { "otros" } }
            .mapNotNull { (categoryId, categoryItems) ->
                val first = categoryItems.firstOrNull() ?: return@mapNotNull null
                MenuSection(
                    id = categoryId,
                    category = MenuCategory(
                        id = categoryId,
                        title = first.categoryTitle.ifBlank { "Otros" },
                    ),
                    items = categoryItems.sortedWith(compareBy<MenuItem> { it.sortOrder }.thenBy { it.name }),
                )
            }
            .sortedWith(
                compareBy<MenuSection> { categoryRank(it.category.title) }
                    .thenBy { it.category.title },
            )
    }

    private fun categoryRank(title: String): Int = when (title.trim()) {
        "Entradas" -> 0
        "Sopas" -> 1
        "Platos Fuertes" -> 2
        "Extras" -> 3
        "Postres" -> 4
        "Bebidas" -> 5
        "Bebidas Alcohólicas" -> 6
        else -> Int.MAX_VALUE
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/OrderDto.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote



data class OrderDto(
    val id: String = "",
    val nationalId: String? = null,
    val clientName: String = "",
    val tableNumber: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val items: List<OrderItemDto> = emptyList(),
    val subtotal: Double = 0.0,
    val loyaltyDiscountAmount: Double? = null,
    val appliedRewards: List<AppliedRewardDto>? = null,
    val totalAmount: Double = 0.0,
    val status: String? = null,
    val revision: Int? = null,
    val lastConfirmedRevision: Int? = null,
) {
    constructor(domain: Order) : this(
        id = domain.id,
        nationalId = domain.nationalId,
        clientName = domain.clientName,
        tableNumber = domain.tableNumber,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
        items = domain.items.map(::OrderItemDto),
        subtotal = domain.subtotal,
        loyaltyDiscountAmount = domain.loyaltyDiscountAmount,
        appliedRewards = domain.appliedRewards.map(::AppliedRewardDto),
        totalAmount = domain.totalAmount,
        status = domain.status.name.lowercase(),
        revision = domain.revision,
        lastConfirmedRevision = domain.lastConfirmedRevision,
    )

    fun toDomain(): Order = Order(
        id = id,
        nationalId = nationalId,
        clientName = clientName,
        tableNumber = tableNumber,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt?.toDate() ?: createdAt.toDate(),
        items = items.map { it.toDomain() },
        subtotal = subtotal,
        loyaltyDiscountAmount = (loyaltyDiscountAmount ?: 0.0).coerceAtLeast(0.0),
        appliedRewards = appliedRewards?.map { it.toDomain() } ?: emptyList(),
        totalAmount = totalAmount,
        status = OrderStatus.fromRaw(status),
        revision = revision ?: 1,
        lastConfirmedRevision = lastConfirmedRevision,
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/OrderItemDto.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote


data class OrderItemDto(
    val id: String = "",
    val menuItemId: String = "",
    val name: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
    val preparedQuantity: Int? = 0,
    val totalPrice: Double? = null,
    val notes: String? = null,
) {
    constructor(domain: OrderItem) : this(
        id = domain.id,
        menuItemId = domain.menuItemId,
        name = domain.name,
        unitPrice = domain.unitPrice,
        quantity = domain.quantity,
        preparedQuantity = domain.preparedQuantity,
        totalPrice = domain.totalPrice,
        notes = domain.notes,
    )

    fun toDomain(): OrderItem = OrderItem(
        id = id,
        menuItemId = menuItemId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        preparedQuantity = preparedQuantity ?: 0,
        notes = notes,
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/OrdersRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote


@Singleton
class OrdersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : OrdersRepositoriable {

    override suspend fun submit(order: Order) {
        val quantitiesByMenuItemId = order.items
            .groupBy { it.menuItemId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }
            .filterValues { it > 0 }

        val menuItemsToProcess: List<Pair<DocumentReference, Int>> =
            quantitiesByMenuItemId.map { (menuItemId, totalQuantity) ->
                firestore.collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
                    .document(menuItemId) to totalQuantity
            }

        firestore.runTransaction { transaction ->
            val loadedItems = menuItemsToProcess.map { (ref, totalQuantity) ->
                val snapshot = transaction.get(ref)
                val dto =
                    requireNotNull(snapshot.toObject(MenuItemDto::class.java)) { "Missing menu item ${ref.id}." }
                Triple(ref, dto, totalQuantity)
            }

            loadedItems.forEach { (ref, dto, totalQuantity) ->
                require(dto.isAvailable) { "${dto.name} no está disponible." }
                require(dto.remainingQuantity >= totalQuantity) { "Ya no hay suficiente stock de ${dto.name}." }

                val newRemainingQuantity = dto.remainingQuantity - totalQuantity
                transaction.update(
                    ref,
                    mapOf(
                        "remainingQuantity" to newRemainingQuantity,
                        "isAvailable" to (newRemainingQuantity > 0),
                        "updatedAt" to Timestamp.now(),
                    ),
                )
            }

            val orderRef =
                firestore.collection(FirestoreCollections.RESTAURANT_ORDERS).document(order.id)
            transaction.set(orderRef, OrderDto(order))
            null
        }.awaitResult()

        val nationalId = order.nationalId?.trim().orEmpty()
        if (nationalId.isNotEmpty() && order.appliedRewards.isNotEmpty()) {
            loyaltyRewardsRepository.reserveRewards(
                nationalId = nationalId,
                referenceType = LoyaltyRewardReferenceType.ORDER,
                referenceId = order.id,
                appliedRewards = order.appliedRewards,
            )
        }
    }

    override fun observeOrders(nationalId: String): Flow<List<Order>> = callbackFlow {
        val cleanNationalId = nationalId.trim()
        if (cleanNationalId.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", cleanNationalId)
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val orders = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(OrderDto::class.java)?.toDomain()
                        }.sortedByDescending { it.createdAt.time }
                        trySend(orders).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/CartDraftRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface CartDraftRepositoriable {
    fun observeDraft(): Flow<OrderDraft>
    suspend fun saveDraft(draft: OrderDraft)
    suspend fun clear()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/CartItem.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    val quantity: Int,
    val notes: String? = null,
) {
    val safeQuantity: Int = quantity.coerceAtLeast(1)
    val unitPrice: Double = menuItem.finalPrice
    val totalPrice: Double = safeQuantity * unitPrice

    fun withQuantity(newQuantity: Int): CartItem = copy(
        quantity = newQuantity.coerceAtLeast(1),
    )

    fun withNotes(newNotes: String?): CartItem = copy(
        notes = newNotes?.trim()?.takeIf { it.isNotEmpty() },
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/MenuCategory.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

data class MenuCategory(
    val id: String,
    val title: String,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/MenuItem.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

data class MenuItem(
    val id: String,
    val categoryId: String,
    val categoryTitle: String = "",
    val name: String,
    val description: String,
    val notes: String? = null,
    val ingredients: List<String>,
    val price: Double,
    val offerPrice: Double? = null,
    val imageURL: String? = null,
    val isAvailable: Boolean = true,
    val remainingQuantity: Int = 20,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0,
) {
    val hasOffer: Boolean = offerPrice != null && offerPrice < price

    val finalPrice: Double = offerPrice ?: price

    val isSoldOut: Boolean = remainingQuantity <= 0

    val canBeOrdered: Boolean = isAvailable && remainingQuantity > 0

    val stockLabel: String
        get() = when {
            !isAvailable -> "No disponible"
            remainingQuantity <= 0 -> "Agotado"
            remainingQuantity == 1 -> "Último plato"
            remainingQuantity <= 5 -> "Quedan $remainingQuantity"
            else -> "$remainingQuantity disponibles"
        }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/MenuRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface MenuRepositoriable {
    fun observeMenu(): Flow<List<MenuSection>>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/MenuSection.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

data class MenuSection(
    val id: String,
    val category: MenuCategory,
    val items: List<MenuItem>,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/ObserveMenuUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


class ObserveMenuUseCase(
    private val repository: MenuRepositoriable,
) {
    fun execute(): Flow<List<MenuSection>> = repository.observeMenu()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/Order.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


data class Order(
    val id: String,
    val nationalId: String?,
    val clientName: String,
    val tableNumber: String,
    val createdAt: Date,
    val updatedAt: Date,
    val items: List<OrderItem>,
    val subtotal: Double,
    val loyaltyDiscountAmount: Double = 0.0,
    val appliedRewards: List<AppliedReward> = emptyList(),
    val totalAmount: Double,
    val status: OrderStatus,
    val revision: Int,
    val lastConfirmedRevision: Int?,
) {
    val totalItems: Int = items.sumOf { it.quantity }
    val preparedItemsCount: Int = items.sumOf { it.safePreparedQuantity }
    val allItemsCompleted: Boolean = items.isNotEmpty() && items.all { it.isCompleted }
    val hasStartedPreparing: Boolean = items.any { it.isStarted }
    val requiresReconfirmation: Boolean = lastConfirmedRevision != revision
    val wasEditedAfterConfirmation: Boolean = lastConfirmedRevision?.let { revision > it } ?: false

    fun withLoyalty(
        appliedRewards: List<AppliedReward>,
        discount: Double,
    ): Order {
        val safeDiscount = discount.coerceIn(0.0, subtotal)
        return copy(
            loyaltyDiscountAmount = safeDiscount,
            appliedRewards = appliedRewards,
            totalAmount = (subtotal - safeDiscount).coerceAtLeast(0.0),
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/OrderDraft.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


private const val DEFAULT_DRAFT_ID = "active_cart"

data class OrderDraft(
    val id: String = DEFAULT_DRAFT_ID,
    val nationalId: String? = null,
    val clientName: String = "",
    val tableNumber: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val items: List<CartItem> = emptyList(),
    val revision: Int? = null,
    val lastConfirmedRevision: Int? = null,
) {
    val totalItems: Int = items.sumOf { it.safeQuantity }
    val subtotal: Double = items.sumOf { it.totalPrice }
    val totalAmount: Double = subtotal
    val isEmpty: Boolean = items.isEmpty()
    val hasValidClientName: Boolean = clientName.trim().isNotEmpty()
    val hasValidTableNumber: Boolean = tableNumber.trim().isNotEmpty()
    val canSubmit: Boolean = !isEmpty && hasValidClientName && hasValidTableNumber

    fun toOrder(
        orderId: String = UUID.randomUUID().toString(),
        status: OrderStatus = OrderStatus.PENDING,
    ): Order {
        val orderItems = items.map { item ->
            OrderItem(
                menuItemId = item.menuItem.id,
                name = item.menuItem.name,
                unitPrice = item.unitPrice,
                quantity = item.safeQuantity,
                notes = item.notes,
            )
        }

        return Order(
            id = orderId,
            nationalId = nationalId?.trim()?.takeIf { it.isNotEmpty() },
            clientName = clientName.trim(),
            tableNumber = tableNumber.trim(),
            createdAt = Date(),
            updatedAt = Date(),
            items = orderItems,
            subtotal = subtotal,
            loyaltyDiscountAmount = 0.0,
            appliedRewards = emptyList(),
            totalAmount = totalAmount,
            status = status,
            revision = revision ?: 0,
            lastConfirmedRevision = lastConfirmedRevision,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/OrderItem.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val preparedQuantity: Int = 0,
    val notes: String? = null,
) {
    init {
        require(quantity >= 0) { "quantity must be >= 0" }
    }

    val safePreparedQuantity: Int = preparedQuantity.coerceIn(0, quantity)

    val totalPrice: Double = quantity * unitPrice

    val remainingQuantity: Int = quantity - safePreparedQuantity

    val isStarted: Boolean = safePreparedQuantity > 0

    val isCompleted: Boolean = safePreparedQuantity == quantity

    fun updatingPreparedQuantity(newValue: Int): OrderItem = copy(
        preparedQuantity = newValue.coerceIn(0, quantity),
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/OrderStatus.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

enum class OrderStatus(val title: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    COMPLETED("Completed"),
    CANCELED("Canceled");

    companion object {
        fun fromRaw(rawValue: String?): OrderStatus = entries.firstOrNull {
            it.name.equals(rawValue, ignoreCase = true)
        } ?: PENDING
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/OrdersRepositoriable.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface OrdersRepositoriable {
    suspend fun submit(order: Order)
    fun observeOrders(nationalId: String): Flow<List<Order>>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/RestaurantUseCases.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

class ObserveOrdersUseCase(
    private val repository: OrdersRepositoriable,
) {
    fun execute(nationalId: String) = repository.observeOrders(nationalId)
}

class SubmitOrderUseCase(
    private val repository: OrdersRepositoriable,
) {
    suspend fun execute(order: Order) = repository.submit(order)
}

class ObserveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    fun execute() = repository.observeDraft()
}

class SaveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute(draft: OrderDraft) = repository.saveDraft(draft)
}

class ClearCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute() = repository.clear()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/MenuItemDetailScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MenuItemDetailScreen(
    item: MenuItem,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onAddToCart: (MenuItem, Int, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var quantity by rememberSaveable(item.id) { mutableIntStateOf(1) }
    var notes by rememberSaveable(item.id) { mutableStateOf("") }

    val safeQuantity = quantity.coerceIn(1, item.remainingQuantity.coerceAtLeast(1))
    val baseSubtotal = item.finalPrice * safeQuantity
    val displayedTotal = displayedPriceProvider(item, safeQuantity)
    val incrementalDiscount = incrementalDiscountProvider(item, safeQuantity)
    val rewardPresentation = rewardPresentationProvider(item, safeQuantity)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del plato") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
        bottomBar = {
            DetailBottomBar(
                canAdd = item.canBeOrdered,
                total = displayedTotal,
                quantity = safeQuantity,
                onAdd = {
                    onAddToCart(
                        item,
                        safeQuantity,
                        notes.trim().takeIf { it.isNotEmpty() },
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 142.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                DetailHero(
                    item = item,
                    displayedTotal = displayedTotal,
                    baseSubtotal = baseSubtotal,
                    hasRewardDiscount = incrementalDiscount > 0.0,
                )
            }

            item {
                DetailCard(title = "Descripción") {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            rewardPresentation?.let { reward ->
                item {
                    RewardDetailCard(reward = reward)
                }
            }

            item {
                DetailCard(title = "Cantidad") {
                    QuantityStepper(
                        quantity = safeQuantity,
                        maxQuantity = item.remainingQuantity.coerceAtLeast(1),
                        enabled = item.canBeOrdered,
                        onQuantityChanged = { quantity = it },
                    )
                }
            }

            item {
                DetailCard(title = "Notas para cocina") {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it.take(220) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Ej. sin cebolla, más cocido, sin ají") },
                    )
                }
            }

            item {
                DetailCard(title = "Total") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryRow("Cantidad", "x$safeQuantity")
                        SummaryRow("Unitario", item.finalPrice.priceLabel())

                        if (incrementalDiscount > 0.0) {
                            SummaryRow("Subtotal", baseSubtotal.priceLabel())
                            SummaryRow("Beneficio", "-${incrementalDiscount.priceLabel()}")
                            HorizontalDivider()
                        }

                        SummaryRow(
                            title = "Total",
                            value = displayedTotal.priceLabel(),
                            emphasized = true,
                        )
                    }
                }
            }

            if (!item.notes.isNullOrBlank()) {
                item {
                    DetailCard(title = "Notas del plato") {
                        Text(
                            text = item.notes.orEmpty(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (item.ingredients.isNotEmpty()) {
                item {
                    DetailCard(title = "Ingredientes") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item.ingredients.forEach { ingredient ->
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHero(
    item: MenuItem,
    displayedTotal: Double,
    baseSubtotal: Double,
    hasRewardDiscount: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            )
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.ExtraBold,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (hasRewardDiscount) {
                        Text(
                            text = baseSubtotal.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = displayedTotal.priceLabel(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Text(
                    text = item.stockLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.90f),
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(title = title)
            content()
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    maxQuantity: Int,
    enabled: Boolean,
    onQuantityChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = enabled && quantity > 1,
            onClick = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Menos")
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
        )

        IconButton(
            enabled = enabled && quantity < maxQuantity,
            onClick = { onQuantityChanged((quantity + 1).coerceAtMost(maxQuantity)) },
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Más")
        }
    }
}

@Composable
private fun RewardDetailCard(reward: RewardPresentation) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = reward.badge,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun DetailBottomBar(
    canAdd: Boolean,
    total: Double,
    quantity: Int,
    onAdd: () -> Unit,
) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Button(
                enabled = canAdd,
                onClick = onAdd,
                modifier = Modifier.weight(1.45f),
            ) {
                Icon(Icons.Rounded.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Añadir x$quantity")
            }
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/MenuListScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListScreen(
    state: MenuUiState,
    clientName: String,
    levelTitle: String,
    cartItemsCount: Int,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    eligibleItemsProvider: (LoyaltyRewardTemplate) -> List<MenuItem>,
    onCategorySelected: (String?) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Sabor de Los Altos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Menú, promos y platos destacados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenOrders) {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = "Pedidos",
                        )
                    }

                    IconButton(onClick = onOpenCart) {
                        CartIcon(cartItemsCount = cartItemsCount)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenCart) {
                CartIcon(cartItemsCount = cartItemsCount)
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading && state.sections.isEmpty() -> {
                LoadingRestaurantState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            state.sections.isEmpty() -> {
                EmptyRestaurantState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 104.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    state.errorMessage?.let { message ->
                        item {
                            ErrorCard(
                                message = message,
                                onDismiss = onDismissError,
                            )
                        }
                    }

                    item {
                        RewardsSection(
                            isLoading = state.isLoadingRewards,
                            templates = state.restaurantRewardTemplates,
                            eligibleItemsProvider = eligibleItemsProvider,
                        )
                    }

                    if (state.featuredItems.isNotEmpty()) {
                        item {
                            FeaturedCarousel(
                                featuredItems = state.featuredItems,
                                rewardProvider = rewardProvider,
                                onOpen = onOpenItem,
                            )
                        }
                    }

                    item {
                        CategorySelectorBlock(
                            selectedCategoryId = state.selectedCategoryId,
                            categories = state.categories,
                            onCategorySelected = onCategorySelected,
                        )
                    }

                    items(state.visibleSections, key = { it.id }) { section ->
                        MenuSectionCard(
                            section = section,
                            rewardProvider = rewardProvider,
                            onOpen = onOpenItem,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartIcon(cartItemsCount: Int) {
    BadgedBox(
        badge = {
            if (cartItemsCount > 0) {
                Badge { Text(cartItemsCount.toString()) }
            }
        },
    ) {
        Icon(
            imageVector = Icons.Rounded.ShoppingCart,
            contentDescription = "Carrito",
        )
    }
}

@Composable
private fun LoadingRestaurantState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Cargando menú...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyRestaurantState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp),
                )
                Text(
                    text = "No hay platos publicados todavía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando Firestore tenga documentos en restaurant_menu_items aparecerán aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HeroStatPill(title: String) {
    Surface(
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "No se pudo actualizar",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
private fun RewardsSection(
    isLoading: Boolean,
    templates: List<LoyaltyRewardTemplate>,
    eligibleItemsProvider: (LoyaltyRewardTemplate) -> List<MenuItem>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Tus cupones y premios",
            subtitle = "Se aplican automáticamente en platos elegibles y al confirmar el pedido.",
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (templates.isEmpty() && !isLoading) {
            ElevatedCard(shape = RoundedCornerShape(22.dp)) {
                Text(
                    text = "Todavía no tienes premios activos para restaurante.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (templates.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(templates, key = { it.id }) { template ->
                    RewardCouponCard(
                        template = template,
                        eligibleItems = eligibleItemsProvider(template),
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardCouponCard(
    template: LoyaltyRewardTemplate,
    eligibleItems: List<MenuItem>,
) {
    ElevatedCard(
        modifier = Modifier.width(300.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(999.dp),
                ) {
                    Text(
                        text = badgeText(template),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                template.expirationText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    )
                }
            }

            Text(
                text = template.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = template.subtitle.ifBlank { template.displaySummary },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            val appliesToText = when {
                eligibleItems.isNotEmpty() -> "Aplica a: " + eligibleItems.take(3).joinToString { it.name } +
                        if (eligibleItems.size > 3) " +${eligibleItems.size - 3}" else ""
                template.rule.type == LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> "Aplica al plato elegible más caro del pedido."
                else -> "Aún no encontré el producto objetivo en el menú. Revisa el menuItemId del cupón."
            }

            Text(
                text = appliesToText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun badgeText(template: LoyaltyRewardTemplate): String = when (template.rule.type) {
    LoyaltyRewardRuleType.FREE_MENU_ITEM -> "Gratis"
    LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Promo"
    LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
    LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
        -> "${(template.rule.percentage ?: 0.0).toInt()}% OFF"
    LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "Aventura"
}

@Composable
private fun FeaturedCarousel(
    featuredItems: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Popular",
            subtitle = "Favoritos de los clientes y platos destacados",
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(featuredItems, key = { it.id }) { item ->
                FeaturedMenuCard(
                    item = item,
                    reward = rewardProvider(item),
                    onClick = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun FeaturedMenuCard(
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            )
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(999.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Whatshot,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Destacado",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = item.finalPrice.priceLabel(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                reward?.let {
                    CompactRewardRibbon(reward = it, onDark = true)
                }
            }
        }
    }
}

@Composable
private fun CategorySelectorBlock(
    selectedCategoryId: String?,
    categories: List<MenuCategory>,
    onCategorySelected: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "Explorar por categoría",
            subtitle = "Muévete rápido entre platos, bebidas y extras.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todo") },
            )

            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.title) },
                )
            }
        }
    }
}

@Composable
private fun MenuSectionCard(
    section: MenuSection,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(title = section.category.title, subtitle = "${section.items.size} producto(s)")

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            section.items.forEach { item ->
                MenuItemRowCard(
                    item = item,
                    reward = rewardProvider(item),
                    onClick = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun MenuItemRowCard(
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.canBeOrdered, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (item.isFeatured) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.hasOffer) {
                            Text(
                                text = item.price.priceLabel(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }

                        Text(
                            text = item.finalPrice.priceLabel(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        )

                        Text(
                            text = "• ${item.stockLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (item.canBeOrdered) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    }
                }
            }

            reward?.let {
                CompactRewardRibbon(reward = it, onDark = false)
            }

            if (item.ingredients.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item.ingredients.take(4).forEach { ingredient ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = ingredient,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactRewardRibbon(
    reward: RewardPresentation,
    onDark: Boolean,
) {
    val background = if (onDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    }
    val titleColor = if (onDark) Color.White else MaterialTheme.colorScheme.primary
    val bodyColor = if (onDark) {
        Color.White.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reward.badge,
                style = MaterialTheme.typography.labelMedium,
                color = titleColor,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodySmall,
                color = bodyColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            reward.amountText?.let {
                Text(
                    text = "Ahorro estimado: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    subtitle: String = "",
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )

        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

internal fun Double.priceLabel(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/RestaurantScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


private sealed interface RestaurantDestination {
    data object Menu : RestaurantDestination
    data class Detail(val itemId: String) : RestaurantDestination
    data object Cart : RestaurantDestination
    data object Checkout : RestaurantDestination
    data object Orders : RestaurantDestination
    data class Success(val order: Order) : RestaurantDestination
}

@Composable
fun RestaurantScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    ordersViewModel: OrdersViewModel = hiltViewModel(),
) {
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val checkoutState by checkoutViewModel.uiState.collectAsStateWithLifecycle()
    val ordersState by ordersViewModel.uiState.collectAsStateWithLifecycle()

    var destination: RestaurantDestination by remember { mutableStateOf(RestaurantDestination.Menu) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        menuViewModel.onAppear(sessionState.profile.nationalId)
        cartViewModel.syncProfile(sessionState.profile)
        checkoutViewModel.syncProfile(sessionState.profile)
        ordersViewModel.syncProfile(sessionState.profile)
    }

    LaunchedEffect(Unit) {
        checkoutViewModel.createdOrder.collect { order ->
            destination = RestaurantDestination.Success(order)
        }
    }

    BackHandler(enabled = destination !is RestaurantDestination.Menu) {
        destination = RestaurantDestination.Menu
    }

    when (val current = destination) {
        RestaurantDestination.Menu -> {
            MenuListScreen(
                state = menuState,
                clientName = sessionState.profile.fullName,
                levelTitle = menuViewModel.currentLevelTitle(),
                cartItemsCount = cartState.totalItems,
                rewardProvider = { item -> menuViewModel.rewardPresentation(item) },
                eligibleItemsProvider = menuViewModel::eligibleMenuItems,
                onCategorySelected = menuViewModel::onCategorySelected,
                onOpenItem = { item -> destination = RestaurantDestination.Detail(item.id) },
                onOpenCart = { destination = RestaurantDestination.Cart },
                onOpenOrders = { destination = RestaurantDestination.Orders },
                onDismissError = menuViewModel::clearError,
                modifier = modifier,
            )
        }

        is RestaurantDestination.Detail -> {
            val item = menuState.itemById(current.itemId)
            if (item == null) {
                LaunchedEffect(current.itemId) {
                    destination = RestaurantDestination.Menu
                }
            } else {
                MenuItemDetailScreen(
                    item = item,
                    rewardPresentationProvider = { menuItem, quantity ->
                        menuViewModel.rewardPresentation(menuItem, quantity)
                    },
                    displayedPriceProvider = { menuItem, quantity ->
                        menuViewModel.displayedPrice(menuItem, quantity)
                    },
                    incrementalDiscountProvider = { menuItem, quantity ->
                        menuViewModel.incrementalDiscount(menuItem, quantity)
                    },
                    onAddToCart = { menuItem, quantity, notes ->
                        cartViewModel.addItem(menuItem, quantity, notes)
                        destination = RestaurantDestination.Cart
                    },
                    onBack = { destination = RestaurantDestination.Menu },
                    modifier = modifier,
                )
            }
        }

        RestaurantDestination.Cart -> {
            CartScreen(
                state = cartState,
                onBack = { destination = RestaurantDestination.Menu },
                onCheckout = { destination = RestaurantDestination.Checkout },
                onIncrease = cartViewModel::increaseItem,
                onDecrease = cartViewModel::decreaseItem,
                onRemove = cartViewModel::removeItem,
                onClearCart = cartViewModel::clearCart,
                onDismissError = cartViewModel::clearError,
                modifier = modifier,
            )
        }

        RestaurantDestination.Checkout -> {
            CheckoutScreen(
                state = checkoutState,
                profile = sessionState.profile,
                onBack = { destination = RestaurantDestination.Cart },
                onTableNumberChanged = checkoutViewModel::updateTableNumber,
                onSubmit = checkoutViewModel::submit,
                onDismissError = checkoutViewModel::clearError,
                modifier = modifier,
            )
        }

        RestaurantDestination.Orders -> {
            OrdersScreen(
                state = ordersState,
                onBack = { destination = RestaurantDestination.Menu },
                onGroupingSelected = ordersViewModel::setGrouping,
                onSortSelected = ordersViewModel::setSortOption,
                onStatusSelected = ordersViewModel::setStatusFilter,
                onDismissError = ordersViewModel::clearError,
                modifier = modifier,
            )
        }

        is RestaurantDestination.Success -> {
            OrderSuccessScreen(
                order = current.order,
                onBackToRestaurant = { destination = RestaurantDestination.Menu },
                onOpenOrders = { destination = RestaurantDestination.Orders },
                modifier = modifier,
            )
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/cart/CartScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearCart: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineDiscounts = state.allocatedDiscountByCartItemId()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!state.isEmpty) {
                        TextButton(onClick = onClearCart) {
                            Text("Limpiar")
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (!state.isEmpty) {
                CartBottomBar(
                    subtotal = state.subtotal,
                    discount = state.discount,
                    total = state.total,
                    isLoadingRewards = state.isLoadingRewards,
                    canCheckout = state.canCheckout,
                    onCheckout = onCheckout,
                )
            }
        },
    ) { innerPadding ->
        when {
            state.isEmpty -> {
                EmptyCart(
                    onBack = onBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 150.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (state.isLoadingRewards) {
                        item {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    state.errorMessage?.let { message ->
                        item {
                            ErrorCardInline(
                                message = message,
                                onDismiss = onDismissError,
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Tu pedido",
                            subtitle = if (state.isLoadingRewards) {
                                "Calculando premios Murco Loyalty para ${state.totalItems} producto(s)."
                            } else {
                                "${state.totalItems} producto(s) listos para enviar."
                            },
                        )
                    }

                    items(state.items, key = { it.id }) { item ->
                        CartItemCard(
                            item = item,
                            allocatedDiscount = lineDiscounts[item.id] ?: 0.0,
                            rewards = state.appliedRewardPresentations(item.menuItem.id),
                            onIncrease = { onIncrease(item.id) },
                            onDecrease = { onDecrease(item.id) },
                            onRemove = { onRemove(item.id) },
                        )
                    }

                    if (state.appliedRewards.isNotEmpty()) {
                        item {
                            AppliedRewardsCard(
                                rewards = state.appliedRewards.map(RewardPresentation::fromAppliedReward),
                            )
                        }
                    }

                    item {
                        OrderSummaryCard(
                            subtotal = state.subtotal,
                            discount = state.discount,
                            total = state.total,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCart(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCartCheckout,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Agrega platos desde el menú para crear tu pedido.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onBack) {
                    Text("Volver al menú")
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    allocatedDiscount: Double,
    rewards: List<RewardPresentation>,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    val discountedLineTotal = (item.totalPrice - allocatedDiscount).coerceAtLeast(0.0)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp),
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.menuItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Unitario ${item.unitPrice.priceLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    if (allocatedDiscount > 0.0) {
                        Text(
                            text = item.totalPrice.priceLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                        Text(
                            text = discountedLineTotal.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "-${allocatedDiscount.priceLabel()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    } else {
                        Text(
                            text = item.totalPrice.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            rewards.forEach { reward ->
                CompactCartRewardRibbon(reward = reward)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = onRemove) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Quitar")
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onDecrease) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Menos")
                }

                Text(
                    text = item.safeQuantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Rounded.Add, contentDescription = "Más")
                }
            }
        }
    }
}

@Composable
private fun CompactCartRewardRibbon(reward: RewardPresentation) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalOffer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = reward.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            reward.amountText?.let { amount ->
                Text(
                    text = "-$amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun AppliedRewardsCard(
    rewards: List<RewardPresentation>,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Premios aplicados",
                subtitle = "Estos beneficios ya se reflejan en el total del carrito.",
            )

            rewards.forEach { reward ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = reward.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                        )
                    }

                    reward.amountText?.let { amount ->
                        Text(
                            text = "-$amount",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    subtotal: Double,
    discount: Double,
    total: Double,
    isLoadingRewards: Boolean,
    canCheckout: Boolean,
    onCheckout: () -> Unit,
) {
    Surface(shadowElevation = 10.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (isLoadingRewards) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isLoadingRewards -> "Calculando beneficios"
                            discount > 0.0 -> "Total con Murco Loyalty"
                            else -> "Subtotal"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (discount > 0.0) {
                        Text(
                            text = subtotal.priceLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = total.priceLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (discount > 0.0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }

                Button(
                    enabled = canCheckout,
                    onClick = onCheckout,
                    modifier = Modifier.weight(1.25f),
                ) {
                    Icon(Icons.Rounded.ShoppingCartCheckout, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Checkout")
                }
            }
        }
    }
}

@Composable
internal fun ErrorCardInline(
    message: String,
    onDismiss: () -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
internal fun OrderSummaryCard(
    subtotal: Double,
    discount: Double,
    total: Double,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionHeader(title = "Resumen")
            SummaryLine("Subtotal", subtotal.priceLabel())
            if (discount > 0.0) {
                SummaryLine("Murco Loyalty", "-${discount.priceLabel()}")
            }
            HorizontalDivider()
            SummaryLine("Total", total.priceLabel(), emphasized = true)
        }
    }
}

@Composable
internal fun SummaryLine(
    title: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            color = if (title == "Murco Loyalty") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/cart/CheckoutScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    profile: ClientProfile,
    onBack: () -> Unit,
    onTableNumberChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Confirmar pedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                total = state.total,
                canSubmit = state.canSubmit,
                isSubmitting = state.isSubmitting,
                onSubmit = onSubmit,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoadingRewards || state.isSubmitting) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            state.errorMessage?.let { message ->
                item {
                    ErrorCardInline(
                        message = message,
                        onDismiss = onDismissError,
                    )
                }
            }

            item {
                CheckoutClientCard(profile = profile)
            }

            item {
                TableCard(
                    tableNumber = state.draft.tableNumber,
                    onTableNumberChanged = onTableNumberChanged,
                )
            }

            item {
                CheckoutItemsCard(state = state)
            }

            if (state.rewardPreview.appliedRewards.isNotEmpty()) {
                item {
                    RewardsAppliedCard(
                        rewards = state.rewardPreview.appliedRewards.map {
                            RewardPresentation.fromAppliedReward(it)
                        },
                    )
                }
            }

            item {
                OrderSummaryCard(
                    subtotal = state.subtotal,
                    discount = state.discount,
                    total = state.total,
                )
            }
        }
    }
}

@Composable
private fun CheckoutClientCard(profile: ClientProfile) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Cliente",
                subtitle = "Estos datos vienen de tu perfil y no se editan aquí.",
            )

            InfoRow(
                icon = Icons.Rounded.Person,
                title = "Nombre",
                value = profile.fullName,
            )
            InfoRow(
                icon = Icons.Rounded.Badge,
                title = "Cédula",
                value = profile.nationalId,
            )
        }
    }
}

@Composable
private fun TableCard(
    tableNumber: String,
    onTableNumberChanged: (String) -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Mesa",
                subtitle = "Indica dónde debe llegar el pedido.",
            )

            OutlinedTextField(
                value = tableNumber,
                onValueChange = onTableNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Número o nombre de mesa") },
                leadingIcon = {
                    Icon(Icons.Rounded.TableRestaurant, contentDescription = null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                ),
            )
        }
    }
}

@Composable
private fun CheckoutItemsCard(state: CheckoutUiState) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Productos",
                subtitle = "${state.draft.totalItems} producto(s) seleccionados.",
            )

            state.draft.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestaurantMenu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.menuItem.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "x${item.safeQuantity} • ${item.unitPrice.priceLabel()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!item.notes.isNullOrBlank()) {
                            Text(
                                text = item.notes.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    Text(
                        text = item.totalPrice.priceLabel(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RewardsAppliedCard(rewards: List<RewardPresentation>) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Beneficios aplicados",
                subtitle = "Se reservarán al enviar el pedido.",
            )

            rewards.forEach { reward ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = reward.message,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    reward.amountText?.let {
                        Text(
                            text = "-$it",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value.ifBlank { "Sin registrar" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    total: Double,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Button(
                enabled = canSubmit,
                onClick = onSubmit,
                modifier = Modifier.weight(1.35f),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(if (isSubmitting) "Enviando..." else "Enviar pedido")
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/order/OrderSuccessScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order


@Composable
fun OrderSuccessScreen(
    order: Order,
    onBackToRestaurant: () -> Unit,
    onOpenOrders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = onOpenOrders,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.ReceiptLong, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Ver mis pedidos")
                }

                OutlinedButton(
                    onClick = onBackToRestaurant,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Restaurant, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Volver al restaurante")
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                    ),
                                ),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(52.dp),
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Pedido enviado",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Tu pedido fue registrado y aparecerá en tiempo real para el equipo de Altos del Murco.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SummaryLine("Código", "#${order.id.takeLast(6).uppercase()}")
                            SummaryLine("Mesa", order.tableNumber)
                            SummaryLine("Productos", order.totalItems.toString())
                            SummaryLine("Total", order.totalAmount.priceLabel(), emphasized = true)
                        }
                    }

                    if (order.appliedRewards.isNotEmpty()) {
                        Text(
                            text = "Beneficios reservados: ${order.appliedRewards.size}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/order/OrdersScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrdersUiState,
    onBack: () -> Unit,
    onGroupingSelected: (OrdersGroupingOption) -> Unit,
    onSortSelected: (OrdersSortOption) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis pedidos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }

            state.errorMessage?.let { message ->
                item {
                    ErrorCardInline(
                        message = message,
                        onDismiss = onDismissError,
                    )
                }
            }

            item {
                OrdersControlsCard(
                    state = state,
                    onGroupingSelected = onGroupingSelected,
                    onSortSelected = onSortSelected,
                    onStatusSelected = onStatusSelected,
                )
            }

            if (state.visibleOrders.isEmpty()) {
                item {
                    EmptyOrdersCard()
                }
            } else {
                val groups = groupOrders(state)
                groups.forEach { (title, orders) ->
                    item {
                        SectionHeader(
                            title = title,
                            subtitle = "${orders.size} pedido(s)",
                        )
                    }

                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersControlsCard(
    state: OrdersUiState,
    onGroupingSelected: (OrdersGroupingOption) -> Unit,
    onSortSelected: (OrdersSortOption) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Herramientas",
                subtitle = "Agrupa, filtra y ordena tus pedidos.",
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                OrdersGroupingOption.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = state.grouping == option,
                        onClick = { onGroupingSelected(option) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = OrdersGroupingOption.entries.size,
                        ),
                    ) {
                        Text(option.title)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.statusFilter == null,
                    onClick = { onStatusSelected(null) },
                    label = { Text("Todos") },
                )
                OrderStatus.entries.forEach { status ->
                    FilterChip(
                        selected = state.statusFilter == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(status.title) },
                    )
                }
            }

            SortDropdown(
                selected = state.sortOption,
                onSelected = onSortSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(
    selected: OrdersSortOption,
    onSelected: (OrdersSortOption) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.title,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Ordenar") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            OrdersSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.title) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyOrdersCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "No hay pedidos para mostrar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando envíes pedidos aparecerán aquí en tiempo real.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #${order.id.takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "${order.totalItems} producto(s) • Mesa ${order.tableNumber}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                StatusPill(order.status)
            }

            HorizontalDivider()

            order.items.take(3).forEach { item ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.RestaurantMenu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.quantity}x ${item.name}",
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (!item.notes.isNullOrBlank()) {
                            Text(
                                text = item.notes.orEmpty(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    Text(
                        text = item.totalPrice.priceLabel(),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (order.items.size > 3) {
                Text(
                    text = "+${order.items.size - 3} producto(s) más",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider()

            Row {
                Text(
                    text = order.createdAt.shortDateTime(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = order.totalAmount.priceLabel(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun StatusPill(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.secondary
        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.outline
        OrderStatus.CANCELED -> MaterialTheme.colorScheme.error
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = status.title,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

private fun groupOrders(state: OrdersUiState): List<Pair<String, List<Order>>> {
    return when (state.grouping) {
        OrdersGroupingOption.DATE -> state.visibleOrders
            .groupBy { OrdersViewModel.dateGroupTitle(it.createdAt) }
            .map { it.key to it.value }

        OrdersGroupingOption.STATUS -> state.visibleOrders
            .groupBy { it.status.title }
            .map { it.key to it.value }
    }
}

private fun Date.shortDateTime(): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "EC")).format(this)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/CartUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


data class CartUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoading: Boolean = true,
    val isLoadingRewards: Boolean = false,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(
        RewardWalletSnapshot.empty("")
    ),
    val errorMessage: String? = null,
    val lastAddedItemName: String? = null,
) {
    val items: List<CartItem> get() = draft.items
    val totalItems: Int get() = draft.totalItems
    val subtotal: Double get() = draft.subtotal.roundMoney()
    val discount: Double get() = rewardPreview.totalDiscount.coerceIn(0.0, subtotal).roundMoney()
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0).roundMoney()
    val appliedRewards: List<AppliedReward> get() = rewardPreview.appliedRewards
    val isEmpty: Boolean get() = draft.isEmpty
    val canCheckout: Boolean get() = !draft.isEmpty

    fun allocatedDiscountByCartItemId(): Map<String, Double> {
        if (items.isEmpty() || appliedRewards.isEmpty()) return emptyMap()

        val allocations = items.associate { it.id to 0.0 }.toMutableMap()

        appliedRewards.forEach { reward ->
            val affectedMenuIds = reward.affectedMenuItemIds.toSet()
            if (affectedMenuIds.isEmpty() || reward.amount <= 0.0) return@forEach

            val candidates = items.filter { item ->
                item.menuItem.id in affectedMenuIds && item.totalPrice > 0.0
            }
            if (candidates.isEmpty()) return@forEach

            val availableTotal = candidates.sumOf { item ->
                (item.totalPrice - (allocations[item.id] ?: 0.0)).coerceAtLeast(0.0)
            }
            if (availableTotal <= 0.0) return@forEach

            var remainingRewardAmount = reward.amount.coerceAtLeast(0.0).roundMoney()

            candidates.forEachIndexed { index, item ->
                val alreadyAllocated = allocations[item.id] ?: 0.0
                val lineCapacity = (item.totalPrice - alreadyAllocated).coerceAtLeast(0.0)

                if (lineCapacity <= 0.0 || remainingRewardAmount <= 0.0) return@forEachIndexed

                val rawShare = if (index == candidates.lastIndex) {
                    remainingRewardAmount
                } else {
                    reward.amount * (lineCapacity / availableTotal)
                }

                val allocation = min(
                    lineCapacity,
                    rawShare.coerceAtMost(remainingRewardAmount),
                ).coerceAtLeast(0.0).roundMoney()

                allocations[item.id] = (alreadyAllocated + allocation).roundMoney()
                remainingRewardAmount =
                    (remainingRewardAmount - allocation).coerceAtLeast(0.0).roundMoney()
            }
        }

        return allocations
            .filterValues { it > 0.0 }
            .mapValues { (_, value) -> value.roundMoney() }
    }

    fun appliedRewardPresentations(menuItemId: String): List<RewardPresentation> = appliedRewards
        .filter { reward -> reward.affectedMenuItemIds.contains(menuItemId) }
        .map(RewardPresentation::fromAppliedReward)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/CartViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


@HiltViewModel
class CartViewModel @Inject constructor(
    observeCartDraftUseCase: ObserveCartDraftUseCase,
    private val saveCartDraftUseCase: SaveCartDraftUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var currentNationalId: String = ""
    private var rewardPreviewJob: Job? = null

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                val draftNationalId = draft.nationalId?.filter(Char::isDigit).orEmpty()
                if (draftNationalId.isNotEmpty()) currentNationalId = draftNationalId

                _uiState.update {
                    it.copy(
                        draft = draft,
                        isLoading = false,
                    )
                }

                refreshRewardPreview(draft)
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        val current = _uiState.value.draft
        val cleanNationalId = profile.nationalId.filter { it.isDigit() }
        currentNationalId = cleanNationalId

        val updated = current.copy(
            nationalId = cleanNationalId,
            clientName = profile.fullName,
            updatedAt = Date(),
        )

        save(updated)
        refreshRewardPreview(updated)
    }

    fun addItem(
        menuItem: MenuItem,
        quantity: Int,
        notes: String?,
    ) {
        val safeQuantity = quantity.coerceAtLeast(1)

        if (!menuItem.canBeOrdered) {
            _uiState.update {
                it.copy(errorMessage = "${menuItem.name} está agotado o no disponible.")
            }
            return
        }

        val trimmedNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
        val current = _uiState.value.draft
        val existingIndex = current.items.indexOfFirst {
            it.menuItem.id == menuItem.id && it.notes.orEmpty() == trimmedNotes.orEmpty()
        }

        val updatedItems = if (existingIndex >= 0) {
            current.items.mapIndexed { index, item ->
                if (index == existingIndex) {
                    val desired = item.safeQuantity + safeQuantity
                    val maxAllowed = menuItem.remainingQuantity.coerceAtLeast(1)
                    item.copy(
                        menuItem = menuItem,
                        quantity = desired.coerceAtMost(maxAllowed),
                        notes = trimmedNotes,
                    )
                } else {
                    item
                }
            }
        } else {
            current.items + CartItem(
                menuItem = menuItem,
                quantity = safeQuantity.coerceAtMost(menuItem.remainingQuantity.coerceAtLeast(1)),
                notes = trimmedNotes,
            )
        }

        save(
            current.copy(
                items = updatedItems,
                updatedAt = Date(),
            ),
        )

        _uiState.update {
            it.copy(
                errorMessage = null,
                lastAddedItemName = menuItem.name,
            )
        }
    }

    fun increaseItem(cartItemId: String) {
        mutateItems { items ->
            items.map { item ->
                if (item.id == cartItemId) {
                    item.copy(
                        quantity = (item.safeQuantity + 1)
                            .coerceAtMost(item.menuItem.remainingQuantity.coerceAtLeast(1)),
                    )
                } else {
                    item
                }
            }
        }
    }

    fun decreaseItem(cartItemId: String) {
        mutateItems { items ->
            items.mapNotNull { item ->
                if (item.id == cartItemId) {
                    val newQuantity = item.safeQuantity - 1
                    if (newQuantity <= 0) null else item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }
    }

    fun removeItem(cartItemId: String) {
        mutateItems { items -> items.filterNot { it.id == cartItemId } }
    }

    fun updateItemNotes(cartItemId: String, notes: String?) {
        mutateItems { items ->
            items.map { item ->
                if (item.id == cartItemId) item.withNotes(notes) else item
            }
        }
    }

    fun updateTableNumber(value: String) {
        val cleaned = value.take(20)
        save(_uiState.value.draft.copy(tableNumber = cleaned, updatedAt = Date()))
    }

    fun dismissAddedMessage() {
        _uiState.update { it.copy(lastAddedItemName = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                clearCartDraftUseCase.execute()
                rewardPreviewJob?.cancel()
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(currentNationalId),
                        ),
                        isLoadingRewards = false,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo limpiar el carrito.")
                }
            }
        }
    }

    private fun mutateItems(transform: (List<CartItem>) -> List<CartItem>) {
        val current = _uiState.value.draft
        val updated = current.copy(
            items = transform(current.items),
            updatedAt = Date(),
        )
        save(updated)
    }

    private fun save(draft: OrderDraft) {
        viewModelScope.launch {
            try {
                saveCartDraftUseCase.execute(draft)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo guardar el carrito.")
                }
            }
        }
    }

    private fun refreshRewardPreview(draft: OrderDraft) {
        rewardPreviewJob?.cancel()

        val cleanNationalId = draft.nationalId
            ?.filter(Char::isDigit)
            ?.takeIf { it.isNotEmpty() }
            ?: currentNationalId

        if (cleanNationalId.isEmpty() || draft.items.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoadingRewards = false,
                    rewardPreview = RewardComputationResult.empty(
                        RewardWalletSnapshot.empty(cleanNationalId),
                    ),
                )
            }
            return
        }

        rewardPreviewJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingRewards = true,
                    errorMessage = null,
                )
            }

            try {
                val preview = buildRewardPreview(
                    nationalId = cleanNationalId,
                    draft = draft,
                )

                _uiState.update {
                    it.copy(
                        rewardPreview = preview,
                        isLoadingRewards = false,
                        errorMessage = null,
                    )
                }
            } catch (_: CancellationException) {
                // Expected when the cart changes quickly. Do not show this as a UI error.
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(cleanNationalId),
                        ),
                        isLoadingRewards = false,
                        errorMessage = error.message ?: "No se pudieron calcular beneficios.",
                    )
                }
            }
        }
    }

    private suspend fun buildRewardPreview(
        nationalId: String,
        draft: OrderDraft,
    ): RewardComputationResult {
        val previewItems = draft.items.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                name = it.menuItem.name,
                unitPrice = it.unitPrice,
                quantity = it.safeQuantity,
                notes = it.notes,
            )
        }

        return loyaltyRewardsRepository.previewRestaurantRewards(
            nationalId = nationalId,
            items = previewItems,
        )
    }
}

fun Double.roundMoney(): Double = round(this * 100.0) / 100.0


```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/CheckoutUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


data class CheckoutUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoadingCart: Boolean = true,
    val isSubmitting: Boolean = false,
    val isLoadingRewards: Boolean = false,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(
        RewardWalletSnapshot.empty(
            ""
        )
    ),
    val errorMessage: String? = null,
) {
    val subtotal: Double get() = draft.subtotal
    val discount: Double get() = rewardPreview.totalDiscount.coerceAtLeast(0.0)
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0)
    val canSubmit: Boolean get() = draft.canSubmit && !isSubmitting
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/CheckoutViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


@HiltViewModel
class CheckoutViewModel @Inject constructor(
    observeCartDraftUseCase: ObserveCartDraftUseCase,
    private val saveCartDraftUseCase: SaveCartDraftUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _createdOrder = MutableSharedFlow<Order>()
    val createdOrder: SharedFlow<Order> = _createdOrder.asSharedFlow()

    private var currentNationalId: String = ""
    private var rewardPreviewJob: Job? = null

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                _uiState.update {
                    it.copy(
                        draft = draft,
                        isLoadingCart = false,
                    )
                }
                refreshRewardPreview(draft)
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        currentNationalId = profile.nationalId.filter { it.isDigit() }
        val current = _uiState.value.draft
        val updated = current.copy(
            nationalId = currentNationalId,
            clientName = profile.fullName,
        )
        saveDraft(updated)
        refreshRewardPreview(updated)
    }

    fun updateTableNumber(value: String) {
        saveDraft(_uiState.value.draft.copy(tableNumber = value.take(20)))
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun submit() {
        val draft = _uiState.value.draft

        if (!draft.canSubmit) {
            _uiState.update {
                it.copy(errorMessage = "Completa la mesa y asegúrate de tener productos en el carrito.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            try {
                val preview = buildRewardPreview(draft)
                val finalOrder = draft
                    .toOrder(orderId = UUID.randomUUID().toString())
                    .withLoyalty(
                        appliedRewards = preview.appliedRewards,
                        discount = preview.totalDiscount,
                    )

                submitOrderUseCase.execute(finalOrder)
                clearCartDraftUseCase.execute()
                _createdOrder.emit(finalOrder)

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(currentNationalId),
                        ),
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "No se pudo enviar el pedido.",
                    )
                }
            }
        }
    }

    private fun saveDraft(draft: OrderDraft) {
        viewModelScope.launch {
            try {
                saveCartDraftUseCase.execute(draft)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo actualizar el checkout.")
                }
            }
        }
    }

    private fun refreshRewardPreview(draft: OrderDraft) {
        rewardPreviewJob?.cancel()
        rewardPreviewJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRewards = true) }
            try {
                val preview = buildRewardPreview(draft)
                _uiState.update {
                    it.copy(
                        rewardPreview = preview,
                        isLoadingRewards = false,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(draft.nationalId.orEmpty()),
                        ),
                        isLoadingRewards = false,
                        errorMessage = error.message ?: "No se pudieron calcular beneficios.",
                    )
                }
            }
        }
    }

    private suspend fun buildRewardPreview(draft: OrderDraft): RewardComputationResult {
        val cleanNationalId = draft.nationalId
            ?.filter(Char::isDigit)
            ?.takeIf { it.isNotEmpty() }
            ?: currentNationalId

        if (cleanNationalId.isEmpty() || draft.items.isEmpty()) {
            return RewardComputationResult.empty(RewardWalletSnapshot.empty(cleanNationalId))
        }

        val previewItems = draft.items.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                name = it.menuItem.name,
                unitPrice = it.unitPrice,
                quantity = it.safeQuantity,
                notes = it.notes,
            )
        }

        return loyaltyRewardsRepository.previewRestaurantRewards(
            nationalId = cleanNationalId,
            items = previewItems,
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/MenuUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


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

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/MenuViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var menuJob: Job? = null
    private var rewardsJob: Job? = null
    private var currentRewardsNationalId: String? = null

    fun onAppear(nationalId: String?) {
        startMenuObservationIfNeeded()
        setNationalId(nationalId)
    }

    fun setNationalId(nationalId: String?) {
        val cleanNationalId = nationalId?.filter(Char::isDigit).orEmpty()

        if (cleanNationalId.isEmpty()) {
            rewardsJob?.cancel()
            rewardsJob = null
            currentRewardsNationalId = null
            _uiState.update {
                it.copy(
                    isLoadingRewards = false,
                    walletSnapshot = RewardWalletSnapshot.empty(""),
                )
            }
            return
        }

        if (currentRewardsNationalId == cleanNationalId && rewardsJob?.isActive == true) return

        currentRewardsNationalId = cleanNationalId
        rewardsJob?.cancel()
        rewardsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRewards = true, errorMessage = null) }
            loyaltyRewardsRepository
                .observeWalletSnapshot(cleanNationalId)
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoadingRewards = false,
                            walletSnapshot = RewardWalletSnapshot.empty(cleanNationalId),
                            errorMessage = error.message ?: "No se pudieron cargar tus beneficios.",
                        )
                    }
                }
                .collectLatest { wallet ->
                    _uiState.update {
                        it.copy(
                            walletSnapshot = wallet,
                            isLoadingRewards = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun currentLevelTitle(): String = _uiState.value.walletSnapshot.currentLevel.title

    fun rewardPresentation(item: MenuItem): RewardPresentation? =
        rewardPresentation(item, quantity = 1)

    fun rewardPresentation(
        item: MenuItem,
        quantity: Int = 1,
    ): RewardPresentation? {
        val projected = projectedRewardResult(item, quantity)

        projected.appliedRewards
            .firstOrNull { reward -> reward.affectedMenuItemIds.contains(item.id) }
            ?.let { return RewardPresentation.fromAppliedReward(it) }

        return RewardPresentationFactory.menuPresentation(
            item = item,
            wallet = _uiState.value.walletSnapshot,
        )
    }

    fun displayedPrice(
        item: MenuItem,
        quantity: Int = 1,
    ): Double {
        val subtotal = (item.finalPrice * quantity.coerceAtLeast(1)).roundMoney()
        return (subtotal - incrementalDiscount(item, quantity)).coerceAtLeast(0.0).roundMoney()
    }

    fun incrementalDiscount(
        item: MenuItem,
        quantity: Int = 1,
    ): Double = projectedRewardResult(item, quantity).totalDiscount.roundMoney()

    fun eligibleMenuItems(template: LoyaltyRewardTemplate): List<MenuItem> {
        val allItems = _uiState.value.allItems
        return when (template.rule.type) {
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> allItems.filter { it.canBeOrdered }

            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
            LoyaltyRewardRuleType.FREE_MENU_ITEM,
            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
                -> {
                val targetId = template.targetMenuItemId ?: return emptyList()
                allItems.filter { it.id == targetId }
            }

            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> emptyList()
        }
    }

    private fun startMenuObservationIfNeeded() {
        if (menuJob?.isActive == true) return

        menuJob = viewModelScope.launch {
            observeMenuUseCase.execute()
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo cargar el menú.",
                        )
                    }
                }
                .collectLatest { sections ->
                    val sorted = sortSections(sections)
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            sections = sorted,
                            selectedCategoryId = current.selectedCategoryId
                                ?.takeIf { selected -> sorted.any { it.category.id == selected } },
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private fun projectedRewardResult(
        item: MenuItem,
        quantity: Int,
    ): RewardComputationResult {
        val wallet = _uiState.value.walletSnapshot
        if (wallet.availableTemplates.isEmpty()) return RewardComputationResult.empty(wallet)

        return LoyaltyRewardEngine.evaluateRestaurant(
            templates = wallet.availableTemplates,
            wallet = wallet,
            menuLines = listOf(
                RewardMenuLine(
                    menuItemId = item.id,
                    name = item.name,
                    unitPrice = item.finalPrice,
                    quantity = quantity.coerceAtLeast(1),
                ),
            ),
        )
    }

    private fun sortSections(sections: List<MenuSection>): List<MenuSection> {
        val preferredOrder = listOf(
            "Entradas",
            "Sopas",
            "Platos Fuertes",
            "Extras",
            "Postres",
            "Bebidas",
            "Bebidas Alcohólicas",
        )

        return sections
            .map { section ->
                section.copy(
                    items = section.items.sortedWith(
                        compareBy<MenuItem> { it.sortOrder }.thenBy { it.name },
                    ),
                )
            }
            .sortedWith(
                compareBy<MenuSection> {
                    val index = preferredOrder.indexOf(it.category.title)
                    if (index == -1) Int.MAX_VALUE else index
                }.thenBy { it.category.title },
            )
    }

    private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/OrdersUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


data class OrdersUiState(
    val nationalId: String = "",
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val grouping: OrdersGroupingOption = OrdersGroupingOption.DATE,
    val sortOption: OrdersSortOption = OrdersSortOption.NEWEST,
    val statusFilter: OrderStatus? = null,
    val errorMessage: String? = null,
) {
    val visibleOrders: List<Order>
        get() {
            val filtered = statusFilter?.let { status -> orders.filter { it.status == status } } ?: orders
            return when (sortOption) {
                OrdersSortOption.NEWEST -> filtered.sortedByDescending { it.createdAt.time }
                OrdersSortOption.OLDEST -> filtered.sortedBy { it.createdAt.time }
                OrdersSortOption.HIGHEST_TOTAL -> filtered.sortedByDescending { it.totalAmount }
            }
        }
}

enum class OrdersGroupingOption(val title: String) {
    DATE("Fecha"),
    STATUS("Estado"),
}

enum class OrdersSortOption(val title: String) {
    NEWEST("Recientes"),
    OLDEST("Antiguos"),
    HIGHEST_TOTAL("Mayor total"),
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/OrdersViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val observeOrdersUseCase: ObserveOrdersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun syncProfile(profile: ClientProfile) {
        val cleanNationalId = profile.nationalId.filter { it.isDigit() }
        if (cleanNationalId == _uiState.value.nationalId && observeJob != null) return

        _uiState.update {
            it.copy(
                nationalId = cleanNationalId,
                isLoading = cleanNationalId.isNotEmpty(),
                errorMessage = null,
            )
        }

        observeOrders(cleanNationalId)
    }

    fun setGrouping(value: OrdersGroupingOption) {
        _uiState.update { it.copy(grouping = value) }
    }

    fun setSortOption(value: OrdersSortOption) {
        _uiState.update { it.copy(sortOption = value) }
    }

    fun setStatusFilter(value: OrderStatus?) {
        _uiState.update { it.copy(statusFilter = value) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun observeOrders(nationalId: String) {
        observeJob?.cancel()

        if (nationalId.isBlank()) {
            _uiState.update {
                it.copy(
                    orders = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                )
            }
            return
        }

        observeJob = viewModelScope.launch {
            observeOrdersUseCase.execute(nationalId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar pedidos.",
                        )
                    }
                }
                .collectLatest { orders ->
                    _uiState.update {
                        it.copy(
                            orders = orders,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    companion object {
        private val dayFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "EC"))

        fun dateGroupTitle(date: Date): String = dayFormatter.format(date)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/AdventureRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class AdventureRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdventureCatalogRepository(
        repository: AdventureCatalogRepository,
    ): AdventureCatalogRepositoriable

    @Binds
    @Singleton
    abstract fun bindAdventureBookingsRepository(
        repository: AdventureBookingsRepository,
    ): AdventureBookingsRepositoriable
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/AuthModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindSessionRepository(
        repository: SessionRepository,
    ): SessionRepositoriable
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/AuthRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    abstract fun bindAuthenticationRepository(
        repository: FirebaseAuthenticationRepository,
    ): AuthenticationRepositoriable

    @Binds
    abstract fun bindClientProfileRepository(
        repository: FirestoreClientProfileRepository,
    ): ClientProfileRepositoriable
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/AuthUseCaseModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {

    @Provides
    fun provideResolveSessionUseCase(
        authRepository: AuthenticationRepositoriable,
        clientProfileRepository: ClientProfileRepositoriable,
    ): ResolveSessionUseCase = ResolveSessionUseCase(
        authRepository = authRepository,
        clientProfileRepository = clientProfileRepository,
    )

    @Provides
    fun provideSignInWithGoogleUseCase(
        repository: AuthenticationRepositoriable,
    ): SignInWithGoogleUseCase = SignInWithGoogleUseCase(repository)

    @Provides
    fun provideCompleteClientProfileUseCase(
        repository: ClientProfileRepositoriable,
    ): CompleteClientProfileUseCase = CompleteClientProfileUseCase(repository)

    @Provides
    fun provideDeleteCurrentAccountUseCase(
        authRepository: AuthenticationRepositoriable,
        clientProfileRepository: ClientProfileRepositoriable,
    ): DeleteCurrentAccountUseCase = DeleteCurrentAccountUseCase(
        authRepository = authRepository,
        clientProfileRepository = clientProfileRepository,
    )

    @Provides
    fun provideSignOutUseCase(
        repository: AuthenticationRepositoriable,
    ): SignOutUseCase = SignOutUseCase(repository)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/DatabaseModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAltosDatabase(
        @ApplicationContext context: Context,
    ): AltosDatabase = Room.databaseBuilder(
        context,
        AltosDatabase::class.java,
        "altos_database",
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideCartDao(database: AltosDatabase): CartDao = database.cartDao()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/DispatcherQualifiers.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/DispatchersModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/FirebaseModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(
        @ApplicationContext context: Context,
    ): FirebaseApp {
        FirebaseApp.getApps(context).firstOrNull()?.let { return it }

        return checkNotNull(FirebaseApp.initializeApp(context)) {
            "FirebaseApp.initializeApp returned null. Verify google-services.json and Gradle setup."
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(
        firebaseApp: FirebaseApp,
    ): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    @Provides
    @Singleton
    fun provideFirebaseFirestore(
        firebaseApp: FirebaseApp,
    ): FirebaseFirestore = FirebaseFirestore.getInstance(firebaseApp)

    @Provides
    @Singleton
    fun provideFirebaseStorage(
        firebaseApp: FirebaseApp,
    ): FirebaseStorage = FirebaseStorage.getInstance(firebaseApp)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/ProfileRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {

    @Binds
    abstract fun bindLoyaltyRewardsRepository(
        repository: LoyaltyRewardsRepository,
    ): LoyaltyRewardsRepositoriable

    @Binds
    abstract fun bindProfileStatsRepository(
        repository: ProfileStatsRepository,
    ): ProfileStatsRepositoriable

    @Binds
    abstract fun bindProfileImageRepository(
        repository: ProfileImageRepository,
    ): ProfileImageRepositoriable
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/RestaurantRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class RestaurantRepositoryModule {

    @Binds
    abstract fun bindMenuRepository(repository: MenuRepository): MenuRepositoriable

    @Binds
    abstract fun bindOrdersRepository(repository: OrdersRepository): OrdersRepositoriable

    @Binds
    abstract fun bindCartDraftRepository(repository: CartDraftRepository): CartDraftRepositoriable
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/RestaurantUseCaseModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
object RestaurantUseCaseModule {

    @Provides
    fun provideObserveMenuUseCase(
        repository: MenuRepository,
    ): ObserveMenuUseCase = ObserveMenuUseCase(repository)

    @Provides
    fun provideObserveOrdersUseCase(
        repository: OrdersRepository,
    ): ObserveOrdersUseCase = ObserveOrdersUseCase(repository)

    @Provides
    fun provideSubmitOrderUseCase(
        repository: OrdersRepository,
    ): SubmitOrderUseCase = SubmitOrderUseCase(repository)

    @Provides
    fun provideObserveCartDraftUseCase(
        repository: CartDraftRepository,
    ): ObserveCartDraftUseCase = ObserveCartDraftUseCase(repository)

    @Provides
    fun provideSaveCartDraftUseCase(
        repository: CartDraftRepository,
    ): SaveCartDraftUseCase = SaveCartDraftUseCase(repository)

    @Provides
    fun provideClearCartDraftUseCase(
        repository: CartDraftRepository,
    ): ClearCartDraftUseCase = ClearCartDraftUseCase(repository)
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/constant/ClientId.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.constant

const val clientId: String =
    "423232967849-2sb8p9a6brp99sfv0uqd8uq1jg1l117v.apps.googleusercontent.com"

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/constant/FirestoreCollections.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.constant

object FirestoreCollections {
    const val CLIENTS = "clients"
    const val ADVENTURE_ACTIVITIES = "adventure_activities"
    const val ADVENTURE_FEATURED_PACKAGES = "adventure_featured_packages"
    const val ADVENTURE_BOOKINGS = "adventure_bookings"
    const val RESTAURANT_MENU_ITEMS = "restaurant_menu_items"
    const val RESTAURANT_ORDERS = "restaurant_orders"
    const val CLIENT_LOYALTY_WALLETS = "client_loyalty_wallets"
    const val LOYALTY_REWARD_TEMPLATES = "loyalty_reward_templates"
    const val POSTS = "posts"
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/database/AltosDatabase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.database


@Database(
    entities = [
        CartDraftEntity::class,
        CartItemEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(RoomConverters::class)
abstract class AltosDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/database/AppPreferencesDataSource.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.database


private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")

@Singleton
class AppPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    val themeMode: Flow<ThemeMode> = context.appPreferencesDataStore.data.map { preferences ->
        ThemeMode.valueOf(
            preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name,
        )
    }

    suspend fun setThemeMode(themeMode: ThemeMode) = withContext(ioDispatcher) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/database/RoomConverters.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.database


class RoomConverters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value?.joinToString("||") ?: ""

    @TypeConverter
    fun toStringList(value: String?): List<String> = value
        ?.takeIf { it.isNotBlank() }
        ?.split("||")
        ?: emptyList()
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/database/TaskAwait.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.database


suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        if (continuation.isActive) continuation.resume(result)
    }
    addOnFailureListener { error ->
        if (continuation.isActive) continuation.resumeWithException(error)
    }
    addOnCanceledListener {
        if (continuation.isActive) continuation.cancel()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/extrension/MoneyAndDateExtensions.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.extrension


private val usdFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
private val abbreviatedDateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

fun Double.priceText(): String = usdFormatter.format(this)

fun Date.abbreviatedDateText(): String = abbreviatedDateFormatter.format(this)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/navigation/AltosApp.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.navigation


@Composable
fun AltosApp(
    themeViewModel: AppThemeViewModel = hiltViewModel(),
) {
    val themeState by themeViewModel.uiState.collectAsStateWithLifecycle()

    AltosTheme(themeMode = themeState.themeMode) {
        AuthGateRoute { authenticatedState: SessionState.Authenticated ->
            AltosMainShell(
                sessionState = authenticatedState,
                currentThemeMode = themeState.themeMode,
                onThemeModeSelected = themeViewModel::setThemeMode,
            )
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/navigation/AltosMainShell.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.navigation


private enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
) {
    HOME(
        route = "home",
        label = "Home",
        icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
    ),
    RESTAURANT(
        route = "restaurant",
        label = "Restaurant",
        icon = { Icon(Icons.Rounded.Restaurant, contentDescription = null) },
    ),
    ADVENTURE(
        route = "adventure",
        label = "Adventure",
        icon = { Icon(Icons.Rounded.Explore, contentDescription = null) },
    ),
    BOOKINGS(
        route = "bookings",
        label = "Bookings",
        icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
    ),
    PROFILE(
        route = "profile",
        label = "Profile",
        icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
    ),
}

@Composable
fun AltosMainShell(
    sessionState: SessionState.Authenticated,
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        },
                        icon = destination.icon,
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen()
            }
            composable(TopLevelDestination.RESTAURANT.route) {
                RestaurantScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.ADVENTURE.route) {
                AdventureScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.BOOKINGS.route) {
                BookingsScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    sessionState = sessionState,
                    currentThemeMode = currentThemeMode,
                    onThemeModeSelected = onThemeModeSelected,
                )
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/AltosPlaceholderCard.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme


@Composable
fun AltosPlaceholderCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/AppThemeViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme


data class AppThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<AppThemeUiState> = appPreferencesDataSource.themeMode
        .map(::AppThemeUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppThemeUiState(),
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            appPreferencesDataSource.setThemeMode(themeMode)
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/Color.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme


val MurcoGreen = Color(0xFF2F7A46)
val MurcoGreenDark = Color(0xFF1C5D32)
val MurcoStone = Color(0xFFF5F2ED)
val MurcoCharcoal = Color(0xFF161616)
val MurcoCard = Color(0xFFFFFFFF)
val MurcoCardDark = Color(0xFF222222)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/SectionTheme.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

enum class SectionTheme {
    Neutral,
    Restaurant,
    Adventure,
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/Theme.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme


private val LightColors = lightColorScheme(
    primary = MurcoGreen,
    secondary = MurcoGreenDark,
    background = MurcoStone,
    surface = MurcoCard,
    onPrimary = Color.White,
    onBackground = MurcoCharcoal,
    onSurface = MurcoCharcoal,
)

private val DarkColors = darkColorScheme(
    primary = MurcoGreen,
    secondary = MurcoGreenDark,
    background = MurcoCharcoal,
    surface = MurcoCardDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun AltosTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MurcoTypography,
        content = content,
    )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/ThemeMode.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/theme/Type.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.theme


val MurcoTypography = Typography()

```

---

# app/src/test/java/com/premierdarkcoffee/tourism/altosdelmurco/ExampleUnitTest.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

```

---

