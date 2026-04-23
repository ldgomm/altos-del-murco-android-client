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
    val id: String,
    val activity: String,
    val durationMinutes: Int,
    val peopleCount: Int,
    val vehicleCount: Int,
    val offRoadRiderCount: Int,
    val nights: Int,
) {
    constructor(item: AdventureReservationItemDraft) : this(
        id = item.id,
        activity = item.activity.name.lowercase(),
        durationMinutes = item.durationMinutes,
        peopleCount = item.peopleCount,
        vehicleCount = item.vehicleCount,
        offRoadRiderCount = item.offRoadRiderCount,
        nights = item.nights,
    )

    fun toDomain(): AdventureReservationItemDraft? {
        val activityType = AdventureActivityType.entries.firstOrNull {
            it.name.equals(activity, ignoreCase = true)
        } ?: return null

        return AdventureReservationItemDraft(
            id = id,
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
    val id: String,
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val notes: String?,
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
        id = id,
        menuItemId = menuItemId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        notes = notes,
    )
}

data class ReservationFoodDraftDto(
    val items: List<ReservationFoodItemDraftDto>,
    val servingMoment: String,
    val servingTime: Timestamp?,
    val notes: String?,
) {
    constructor(food: ReservationFoodDraft) : this(
        items = food.items.map(::ReservationFoodItemDraftDto),
        servingMoment = food.servingMoment.name.lowercase(),
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
    val id: String,
    val title: String,
    val activity: String,
    val resourceType: String,
    val startAt: Timestamp,
    val endAt: Timestamp,
    val reservedUnits: Int,
    val subtotal: Double,
) {
    constructor(block: AdventureBookingBlock) : this(
        id = block.id,
        title = block.title,
        activity = block.activity.name.lowercase(),
        resourceType = block.resourceType.name.lowercase(),
        startAt = Timestamp(block.startAt),
        endAt = Timestamp(block.endAt),
        reservedUnits = block.reservedUnits,
        subtotal = block.subtotal,
    )

    fun toDomain(): AdventureBookingBlock? {
        val activityType = AdventureActivityType.entries.firstOrNull {
            it.name.equals(activity, ignoreCase = true)
        } ?: return null
        val resource = AdventureResourceType.entries.firstOrNull {
            it.name.equals(resourceType, ignoreCase = true)
        } ?: return null

        return AdventureBookingBlock(
            id = id,
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
    val id: String,
    val templateId: String,
    val title: String,
    val amount: Double,
    val note: String,
    val affectedMenuItemIds: List<String>,
    val affectedActivityIds: List<String>,
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
    val clientId: String?,
    val clientName: String,
    val whatsappNumber: String,
    val nationalId: String,
    val startDayKey: String,
    val startAt: Timestamp,
    val endAt: Timestamp,
    val guestCount: Int?,
    val eventType: String?,
    val customEventTitle: String?,
    val eventNotes: String?,
    val items: List<AdventureReservationItemDraftDto>,
    val foodReservation: ReservationFoodDraftDto?,
    val blocks: List<AdventureBookingBlockDto>,
    val adventureSubtotal: Double?,
    val foodSubtotal: Double?,
    val subtotal: Double,
    val discountAmount: Double,
    val loyaltyDiscountAmount: Double?,
    val appliedRewards: List<AdventureAppliedRewardDto>?,
    val nightPremium: Double,
    val totalAmount: Double,
    val status: String,
    val createdAt: Timestamp,
    val notes: String?,
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
            eventType = request.eventType.name.lowercase(),
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
            status = status.name.lowercase(),
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
        val activityType = AdventureActivityType.entries.firstOrNull { it.name.equals(id, ignoreCase = true) }
            ?: return null
        val parsedPricingMode = AdventurePricingMode.entries.firstOrNull {
            it.name.equals(pricingMode, ignoreCase = true)
        } ?: return null

        return AdventureActivityCatalogItem(
            id = id,
            activityType = activityType,
            title = title,
            systemImage = systemImage,
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            includes = includes,
            durationOptions = durationOptions,
            pricingMode = parsedPricingMode,
            basePrice = basePrice,
            discountAmount = discountAmount,
            currency = currency,
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
    fun toDomain(): AdventureReservationItemDraft? {
        val activityType = AdventureActivityType.entries.firstOrNull { it.name.equals(activity, ignoreCase = true) }
            ?: return null
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
            packageDiscountAmount = packageDiscountAmount,
            items = mappedItems,
            foodItems = mappedFoodItems,
            updatedAt = updatedAt.toDate(),
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/data/AdventureRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data


@Module
@InstallIn(SingletonComponent::class)
abstract class AdventureRepositoryModule {

    @Binds
    abstract fun bindAdventureCatalogRepository(
        repository: FirebaseAdventureCatalogRepository,
    ): AdventureCatalogRepository
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/data/FirebaseAdventureCatalogRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data


@Singleton
class FirebaseAdventureCatalogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AdventureCatalogRepository {

    override suspend fun fetchCatalog(): AdventureCatalogSnapshot {
        val activitiesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_ACTIVITIES).get().awaitResult()
        val packagesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES).get()
                .awaitResult()
        return makeCatalogSnapshot(activitiesSnapshot, packagesSnapshot)
    }

    override fun observeCatalog(): Flow<AdventureCatalogSnapshot> = callbackFlow {
        var latestActivities: com.google.firebase.firestore.QuerySnapshot? = null
        var latestPackages: com.google.firebase.firestore.QuerySnapshot? = null

        fun emitIfReady() {
            val activities = latestActivities
            val packages = latestPackages
            if (activities != null && packages != null) {
                trySend(makeCatalogSnapshot(activities, packages)).isSuccess
            }
        }

        val activitiesRegistration: ListenerRegistration = firestore
            .collection(FirestoreCollections.ADVENTURE_ACTIVITIES)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot != null -> {
                        latestActivities = snapshot
                        emitIfReady()
                    }
                }
            }

        val packagesRegistration: ListenerRegistration = firestore
            .collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot != null -> {
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
        activitiesSnapshot: com.google.firebase.firestore.QuerySnapshot,
        packagesSnapshot: com.google.firebase.firestore.QuerySnapshot,
    ): AdventureCatalogSnapshot {
        val activities = activitiesSnapshot.documents.mapNotNull { doc ->
            doc.toObject(AdventureActivityCatalogDto::class.java)?.toDomain()
        }

        val activitiesByType = activities.associateBy { it.activityType }

        val packages: List<AdventureFeaturedPackage> =
            packagesSnapshot.documents.mapNotNull { doc ->
                val dto =
                    doc.toObject(AdventureFeaturedPackageDto::class.java) ?: return@mapNotNull null
                if (!dto.isActive) return@mapNotNull null
                val packageModel = dto.toDomain() ?: return@mapNotNull null
                val allItemsActive =
                    packageModel.items.all { item -> activitiesByType[item.activity]?.isActive == true }
                if (!allItemsActive) return@mapNotNull null
                packageModel
            }

        return AdventureCatalogSnapshot(
            activities = activities.sortedWith(compareBy({ it.sortOrder }, { it.title })),
            featuredPackages = packages.sortedWith(compareBy({ it.sortOrder }, { it.title })),
        )
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCatalogModels.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

enum class AdventurePricingMode {
    PER_HOUR_PER_VEHICLE,
    PER_30_MIN_PER_PERSON,
    PER_NIGHT_PER_PERSON,
    FIXED_PER_PERSON,
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
    val updatedAt: java.util.Date,
) {
    val finalUnitPrice: Double = (basePrice - discountAmount).coerceAtLeast(0.0)

    val hasDiscount: Boolean = discountAmount > 0.0

    val defaultDraft: AdventureReservationItemDraft = AdventureReservationItemDraft(
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
    val updatedAt: java.util.Date,
)

data class AdventureCatalogSnapshot(
    val activities: List<AdventureActivityCatalogItem>,
    val featuredPackages: List<AdventureFeaturedPackage>,
) {
    private val activitiesByType: Map<AdventureActivityType, AdventureActivityCatalogItem> =
        activities.associateBy { it.activityType }

    fun activity(activity: AdventureActivityType): AdventureActivityCatalogItem? =
        activitiesByType[activity]

    val activeActivitiesSorted: List<AdventureActivityCatalogItem> = activities
        .filter { it.isActive }
        .sortedWith(compareBy<AdventureActivityCatalogItem> { it.sortOrder }.thenBy { it.title })

    val activePackagesSorted: List<AdventureFeaturedPackage> = featuredPackages
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCatalogRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


interface AdventureCatalogRepository {
    suspend fun fetchCatalog(): AdventureCatalogSnapshot
    fun observeCatalog(): Flow<AdventureCatalogSnapshot>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/domain/AdventureCoreModels.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain


enum class AdventureActivityType(
    val legacyTitle: String,
    val legacySystemImage: String,
    val legacyDurationOptions: List<Int>
) {
    OFF_ROAD(
        legacyTitle = "Off-road 4x4",
        legacySystemImage = "car.fill",
        legacyDurationOptions = listOf(60, 120, 180),
    ),
    PAINTBALL(
        legacyTitle = "Paintball",
        legacySystemImage = "shield.lefthalf.filled",
        legacyDurationOptions = listOf(30, 60, 90, 120),
    ),
    GO_KARTS(
        legacyTitle = "Go karts",
        legacySystemImage = "flag.checkered",
        legacyDurationOptions = listOf(30, 60, 90, 120),
    ),
    SHOOTING_RANGE(
        legacyTitle = "Campo de tiro",
        legacySystemImage = "target",
        legacyDurationOptions = listOf(30, 60, 90, 120),
    ),
    CAMPING(
        legacyTitle = "Camping",
        legacySystemImage = "tent.fill",
        legacyDurationOptions = emptyList(),
    ),
    EXTREME_SLIDE(
        legacyTitle = "Resbaladera extrema",
        legacySystemImage = "figure.fall",
        legacyDurationOptions = listOf(30),
    );

    companion object {
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
            catalog: AdventureCatalogSnapshot?,
        ): AdventureReservationItemDraft {
            val config = catalog?.activity(activity) ?: return defaultDraft(activity)
            return config.defaultDraft
        }
    }
}

enum class AdventureResourceType {
    OFF_ROAD_VEHICLES,
    PAINTBALL_PEOPLE,
    GO_KART_PEOPLE,
    SHOOTING_PEOPLE,
    CAMPING_PEOPLE,
    EXTREME_SLIDE_PEOPLE,
}

enum class AdventureBookingStatus(val title: String) {
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    COMPLETED("Completada"),
    CANCELED("Cancelada");

    companion object {
        fun fromRaw(rawValue: String?): AdventureBookingStatus = entries.firstOrNull {
            it.name.equals(rawValue, ignoreCase = true)
        } ?: CONFIRMED
    }
}

enum class ReservationEventType(val title: String) {
    REGULAR_VISIT("Visita regular"),
    BIRTHDAY("Cumpleaños"),
    ANNIVERSARY("Aniversario"),
    CORPORATE("Evento corporativo"),
    FAMILY_GATHERING("Reunión familiar"),
    CUSTOM("Otro");

    companion object {
        fun fromRaw(rawValue: String?): ReservationEventType = entries.firstOrNull {
            it.name.equals(rawValue, ignoreCase = true)
        } ?: REGULAR_VISIT
    }
}

enum class ReservationServingMoment(val title: String) {
    ON_ARRIVAL("Al llegar"),
    AFTER_ACTIVITIES("Después de actividades"),
    SPECIFIC_TIME("Hora específica");

    companion object {
        fun fromRaw(rawValue: String?): ReservationServingMoment = entries.firstOrNull {
            it.name.equals(rawValue, ignoreCase = true)
        } ?: AFTER_ACTIVITIES
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
    val title: String = activity.legacyTitle

    val summaryText: String
        get() = when (activity) {
            AdventureActivityType.OFF_ROAD -> "${durationMinutes / 60}h • $vehicleCount vehículo(s) • $offRoadRiderCount persona(s)"
            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE,
                -> "$durationMinutes min • $peopleCount persona(s)"

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

    val subtotal: Double = quantity.coerceAtLeast(1) * unitPrice
}

data class ReservationFoodDraft(
    val items: List<ReservationFoodItemDraft>,
    val servingMoment: ReservationServingMoment,
    val servingTime: Date?,
    val notes: String?,
) {
    val subtotal: Double = items.sumOf { it.subtotal }

    val isEmpty: Boolean = items.isEmpty()
}

data class AdventureBookingBlock(
    val id: String,
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
    val id: String,
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
    val hasActivities: Boolean = items.isNotEmpty()

    val hasFoodReservation: Boolean = foodReservation?.isEmpty == false
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
    val hasActivities: Boolean = items.isNotEmpty()

    val hasFoodReservation: Boolean = foodReservation?.isEmpty == false

    val eventDisplayTitle: String
        get() = if (eventType == ReservationEventType.CUSTOM) {
            customEventTitle?.trim().takeUnless { it.isNullOrEmpty() } ?: "Evento personalizado"
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
    const val SLOT_MINUTES: Int = 30
    const val DAYTIME_START_HOUR: Int = 7
    const val DAYTIME_END_HOUR: Int = 20
    const val NIGHT_PREMIUM_START_HOUR: Int = 18
    const val OFF_ROAD_PEOPLE_PER_VEHICLE: Int = 2
    const val FOOD_ONLY_DEFAULT_DURATION_MINUTES: Int = 90

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
    private val calendar: Calendar = Calendar.getInstance()

    fun dayKey(date: Date): String {
        val local = Calendar.getInstance().apply { time = date }
        return "%04d-%02d-%02d".format(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH) + 1,
            local.get(Calendar.DAY_OF_MONTH),
        )
    }

    fun dateOn(day: Date, hour: Int, minute: Int): Date {
        val cal = Calendar.getInstance().apply {
            time = day
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }

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
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/adventure/presentation/view/AdventureScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view


@Composable
fun AdventureScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Aventura",
            body = "El catálogo de actividades, paquetes destacados, builder de combos, disponibilidad y reservas se portarán sobre esta base.",
        )

    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/AuthModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindSessionRepository(
        repository: DeveloperBypassSessionRepository,
    ): SessionRepository
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/AuthRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/DeveloperBypassSessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class DeveloperBypassSessionRepository @Inject constructor() : SessionRepository {
    override fun sessionState(): Flow<SessionState> {
        return if (BuildConfig.DEBUG) {
            flowOf(
                SessionState.Authenticated(
                    displayName = "Developer Preview",
                    developerBypass = true,
                ),
            )
        } else {
            flowOf(SessionState.Unauthenticated)
        }
    }
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
        val appleProviderUid = user.providerData.firstOrNull { it.providerId == "apple.com" }?.uid.orEmpty()
        return AuthenticatedUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty(),
            appleUserIdentifier = appleProviderUid,
        )
    }

    override suspend fun signInWithApple(
        idToken: String,
        rawNonce: String,
        fullName: String?,
        email: String?,
        appleUserIdentifier: String,
    ): AuthenticatedUser {
        val credential = OAuthProvider
            .newCredentialBuilder("apple.com")
            .setIdTokenWithRawNonce(idToken, rawNonce)
            .build()

        val authResult = auth.signInWithCredential(credential).awaitResult()
        val firebaseUser = requireNotNull(authResult.user) { "Firebase auth returned a null user after Apple sign in." }

        val finalDisplayName = fullName?.trim().takeUnless { it.isNullOrEmpty() }
            ?: firebaseUser.displayName.orEmpty()
        val finalEmail = email?.trim().takeUnless { it.isNullOrEmpty() }
            ?: firebaseUser.email.orEmpty()
        val providerUid = firebaseUser.providerData.firstOrNull { it.providerId == "apple.com" }?.uid
        val finalAppleIdentifier = providerUid?.takeIf { it.isNotBlank() } ?: appleUserIdentifier

        return AuthenticatedUser(
            uid = firebaseUser.uid,
            email = finalEmail,
            displayName = finalDisplayName,
            appleUserIdentifier = finalAppleIdentifier,
        )
    }

    override suspend fun reauthenticateCurrentUser(
        idToken: String,
        rawNonce: String,
    ) {
        val user = requireNotNull(auth.currentUser) { "No authenticated user found." }
        val credential = OAuthProvider
            .newCredentialBuilder("apple.com")
            .setIdTokenWithRawNonce(idToken, rawNonce)
            .build()
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

    override suspend fun fetchProfile(uid: String): ClientProfile? {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return null

        val snapshot = collection.document(cleanUid).get().awaitResult()
        if (!snapshot.exists()) return null

        val document = snapshot.toObject(ClientProfileDocument::class.java) ?: return null
        return document.toDomain()
    }

    override suspend fun saveProfile(profile: ClientProfile) {
        collection.document(profile.id).set(ClientProfileDocument(profile)).awaitResult()
    }

    override suspend fun deleteProfile(uid: String) {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return
        collection.document(cleanUid).delete().awaitResult()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/AuthenticatedUser.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

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

    suspend fun signInWithApple(
        idToken: String,
        rawNonce: String,
        fullName: String?,
        email: String?,
        appleUserIdentifier: String,
    ): AuthenticatedUser

    suspend fun reauthenticateCurrentUser(
        idToken: String,
        rawNonce: String,
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


interface SessionRepository {
    fun sessionState(): Flow<SessionState>
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
        val displayName: String,
        val developerBypass: Boolean,
    ) : SessionState
    data class NeedsProfileCompletion(
        val userId: String,
    ) : SessionState
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AuthGateRoute.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


@Composable
fun AuthGateRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthGateViewModel = hiltViewModel(),
    authenticatedContent: @Composable (SessionState.Authenticated) -> Unit,
) {
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()

    when (val state = sessionState) {
        SessionState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        SessionState.Unauthenticated -> SignInPlaceholderScreen(modifier = modifier)
        is SessionState.NeedsProfileCompletion -> CompleteProfilePlaceholderScreen(
            userId = state.userId,
            modifier = modifier,
        )
        is SessionState.Authenticated -> authenticatedContent(state)
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/presentation/viewmodel/AuthGateViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel


@HiltViewModel
class AuthGateViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionRepository
        .sessionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/booking/presentation/view/BookingsScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view


@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Reservas",
            body = "Los pedidos del restaurante y las reservas de aventura terminarán viviendo aquí, con navegación separada pero una experiencia unificada.",
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/data/NoOpLoyaltyRewardsRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data


@Singleton
class NoOpLoyaltyRewardsRepository @Inject constructor() : LoyaltyRewardsRepository {

    override suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot {
        return RewardWalletSnapshot.empty(nationalId.trim())
    }

    override suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult {
        return RewardComputationResult.empty(RewardWalletSnapshot.empty(nationalId.trim()))
    }

    override suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult {
        return RewardComputationResult.empty(RewardWalletSnapshot.empty(nationalId.trim()))
    }

    override suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    ) = Unit

    override suspend fun consumeRewards(nationalId: String, referenceId: String) = Unit

    override suspend fun releaseRewards(nationalId: String, referenceId: String) = Unit
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/data/ProfileRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data


@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {

    @Binds
    abstract fun bindLoyaltyRewardsRepository(
        repository: NoOpLoyaltyRewardsRepository,
    ): LoyaltyRewardsRepository
}

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
)

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

    fun toDomain(): ClientProfile = ClientProfile(
        id = id,
        email = email,
        appleUserIdentifier = appleUserIdentifier,
        fullName = fullName,
        nationalId = nationalId,
        phoneNumber = phoneNumber,
        birthday = birthday,
        address = address,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        isProfileComplete = isProfileComplete,
        createdAt = createdAt,
        updatedAt = updatedAt,
        profileCompletedAt = profileCompletedAt,
        profileImageURL = profileImageURL,
        profileImagePath = profileImagePath,
    )
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/profile/domain/LoyaltyRewardsRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain


interface LoyaltyRewardsRepository {
    suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot

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


@Composable
fun ProfileScreen(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Perfil",
            body = "En este primer módulo dejamos lista la persistencia del tema con DataStore para reemplazar AppPreferences del proyecto Swift.",
        )

        Text(
            text = "Tema",
            style = MaterialTheme.typography.titleMedium,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentThemeMode == mode,
                    onClick = { onThemeModeSelected(mode) },
                    label = { Text(mode.name) },
                )
            }
        }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/CartDao.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data


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
    suspend fun replaceDraft(draft: CartDraftEntity, items: List<CartItemEntity>) {
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/CartDraftEntity.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data


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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/CartDraftWithItems.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data


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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/CartItemEntity.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data


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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/CartDraftMappers.kt

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
            quantity = item.quantity,
            notes = item.itemNotes,
        )
    },
    revision = draft.revision,
    lastConfirmedRevision = draft.lastConfirmedRevision,
)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/local/RoomCartDraftRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local


@Singleton
class RoomCartDraftRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartDraftRepository {

    override fun observeDraft(): Flow<OrderDraft> = cartDao.observeCart().map { stored ->
        stored?.toDomain() ?: OrderDraft()
    }

    override suspend fun saveDraft(draft: OrderDraft) {
        cartDao.replaceDraft(
            draft = draft.toEntity(),
            items = draft.items.map { it.toEntity(draft.id) },
        )
    }

    override suspend fun clear() {
        cartDao.clearAll()
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/FirebaseMenuRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote


@Singleton
class FirebaseMenuRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MenuRepository {

    override fun observeMenu(): Flow<List<MenuSection>> = callbackFlow {
        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
            .orderBy("categoryTitle")
            .orderBy("sortOrder")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val items = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(MenuItemDto::class.java)?.toDomain()
                        }
                        trySend(groupIntoSections(items)).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }

    private fun groupIntoSections(items: List<MenuItem>): List<MenuSection> {
        return items
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, categoryItems) ->
                val first = categoryItems.firstOrNull() ?: return@mapNotNull null
                MenuSection(
                    id = categoryId,
                    category = MenuCategory(
                        id = categoryId,
                        title = first.categoryTitle,
                    ),
                    items = categoryItems.sortedBy { it.sortOrder },
                )
            }
            .sortedBy { it.category.title }
    }
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/data/remote/FirebaseOrdersRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote


@Singleton
class FirebaseOrdersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : OrdersRepository {

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
    fun toDomain(): MenuItem = MenuItem(
        id = id,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        name = name,
        description = description,
        notes = notes,
        ingredients = ingredients,
        price = price,
        offerPrice = offerPrice,
        imageURL = imageURL,
        isAvailable = isAvailable,
        remainingQuantity = remainingQuantity.coerceAtLeast(0),
        isFeatured = isFeatured,
        sortOrder = sortOrder,
    )
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/CartDraftRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface CartDraftRepository {
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
    val unitPrice: Double = menuItem.finalPrice

    val totalPrice: Double = quantity * unitPrice
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/MenuRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface MenuRepository {
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
    ): Order = copy(
        loyaltyDiscountAmount = discount.coerceAtLeast(0.0),
        appliedRewards = appliedRewards,
        totalAmount = (subtotal - discount.coerceAtLeast(0.0)).coerceAtLeast(0.0),
    )
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
    val totalItems: Int = items.sumOf { it.quantity }

    val subtotal: Double = items.sumOf { it.totalPrice }

    val totalAmount: Double = subtotal

    val isEmpty: Boolean = items.isEmpty()

    val hasValidClientName: Boolean = clientName.trim().isNotEmpty()

    val hasValidTableNumber: Boolean = tableNumber.trim().isNotEmpty()

    val canSubmit: Boolean = !isEmpty && hasValidClientName && hasValidTableNumber

    fun toOrder(
        orderId: String,
        status: OrderStatus = OrderStatus.PENDING,
    ): Order {
        val orderItems = items.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                name = it.menuItem.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
                notes = it.notes,
            )
        }

        return Order(
            id = orderId,
            nationalId = nationalId,
            clientName = clientName.trim(),
            tableNumber = tableNumber.trim(),
            createdAt = Date(),
            updatedAt = Date(),
            items = orderItems,
            subtotal = subtotal,
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/OrdersRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


interface OrdersRepository {
    suspend fun submit(order: Order)
    fun observeOrders(nationalId: String): Flow<List<Order>>
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/RestaurantScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@Composable
fun RestaurantScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Restaurante",
            body = "El catálogo, el detalle de platos, el carrito y el checkout se migrarán en los siguientes módulos. Este entry point ya está conectado al tab principal.",
        )

    }
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/RestaurantRepositoryModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class RestaurantRepositoryModule {

    @Binds
    abstract fun bindMenuRepository(repository: FirebaseMenuRepository): MenuRepository

    @Binds
    abstract fun bindOrdersRepository(repository: FirebaseOrdersRepository): OrdersRepository

    @Binds
    abstract fun bindCartDraftRepository(repository: RoomCartDraftRepository): CartDraftRepository
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/util/constant/FirestoreCollections.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.util.constant

object FirestoreCollections {
    const val CLIENTS = "clients"
    const val CLIENT_LOYALTY_WALLETS = "client_loyalty_wallets"
    const val LOYALTY_REWARD_TEMPLATES = "loyalty_reward_templates"
    const val RESTAURANT_MENU_ITEMS = "restaurant_menu_items"
    const val RESTAURANT_ORDERS = "restaurant_orders"
    const val ADVENTURE_ACTIVITIES = "adventure_activities"
    const val ADVENTURE_FEATURED_PACKAGES = "adventure_featured_packages"
    const val ADVENTURE_BOOKINGS = "adventure_bookings"
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
    version = 2,
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
                RestaurantScreen()
            }
            composable(TopLevelDestination.ADVENTURE.route) {
                AdventureScreen()
            }
            composable(TopLevelDestination.BOOKINGS.route) {
                BookingsScreen()
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
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

