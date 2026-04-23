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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/DeveloperBypassSessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class DeveloperBypassSessionRepository @Inject constructor() : SessionRepository {
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/data/FirebaseSessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data


@Singleton
class FirebaseSessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authenticationRepository: AuthenticationRepositoriable,
    private val resolveSessionUseCase: ResolveSessionUseCase,
) : SessionRepository {

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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/authentication/domain/SessionRepository.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain


interface SessionRepository {
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
    private val sessionRepository: SessionRepository,
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
                sessionRepository.refresh()
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
                sessionRepository.refresh()
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
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionRepository
        .sessionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )

    fun refreshSession() {
        viewModelScope.launch {
            sessionRepository.refresh()
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
    private val sessionRepository: SessionRepository,
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
                sessionRepository.refresh()
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
    private val sessionRepository: SessionRepository,
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
                    sessionRepository.refresh()
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/domain/ObserveMenuUseCase.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain


class ObserveMenuUseCase(
    private val repository: MenuRepository,
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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/MenuItemDetailScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDetailScreen(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Plato",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
            )
        },
        bottomBar = {
            DetailBottomBar(
                item = item,
                rewardPresentation = rewardPresentation,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                DetailHero(item = item)
            }

            item {
                OverviewCard(item = item)
            }

            rewardPresentation?.let { reward ->
                item {
                    RewardSpotlightCard(reward = reward)
                }
            }

            item {
                AvailabilityCard(item = item)
            }

            if (!item.notes.isNullOrBlank()) {
                item {
                    NotesCard(notes = item.notes.orEmpty())
                }
            }

            if (item.ingredients.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = "Ingredientes",
                        subtitle = "Componentes principales del plato.",
                    )
                }

                item {
                    IngredientsCard(ingredients = item.ingredients)
                }
            }
        }
    }
}

@Composable
private fun DetailHero(item: MenuItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
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
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (item.isFeatured) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Popular") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.16f),
                            labelColor = Color.White,
                            leadingIconContentColor = Color.White,
                        ),
                        border = null,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = item.categoryTitle.ifBlank { "Menú" },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.90f),
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.92f),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailPill(
                        text = item.stockLabel,
                        containerColor = if (item.canBeOrdered) {
                            Color.White.copy(alpha = 0.18f)
                        } else {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
                        },
                    )
                    if (item.hasOffer) {
                        DetailPill(
                            text = "Oferta",
                            containerColor = Color.White.copy(alpha = 0.18f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPill(
    text: String,
    containerColor: Color,
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun OverviewCard(item: MenuItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionTitle(
                title = "Resumen del plato",
                subtitle = "Precio, estado y detalles principales.",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Precio actual",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.hasOffer) {
                            Text(
                                text = item.price.priceLabel(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }

                        Text(
                            text = item.finalPrice.priceLabel(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (item.hasOffer) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Surface(
                    color = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.stockLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardSpotlightCard(reward: RewardPresentation) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = {},
                    label = { Text(reward.badge) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.LocalOffer,
                            contentDescription = null,
                        )
                    },
                )
            }

            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            reward.amountText?.takeIf { it.isNotBlank() }?.let { amountText ->
                HorizontalDivider()
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AvailabilityCard(item: MenuItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle(
                title = "Disponibilidad",
                subtitle = if (item.canBeOrdered) {
                    "Este plato está disponible y listo para los siguientes módulos de carrito y checkout."
                } else {
                    "Por ahora el plato aparece agotado o no disponible."
                },
            )

            Text(
                text = item.stockLabel,
                style = MaterialTheme.typography.titleMedium,
                color = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NotesCard(notes: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle(
                title = "Notas del plato",
                subtitle = "Información adicional importante para el cliente.",
            )
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IngredientsCard(ingredients: List<String>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ingredients.forEach { ingredient ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                ) {
                    Text(
                        text = ingredient,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DetailBottomBar(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onBack: () -> Unit,
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (item.hasOffer) "Precio promocional" else "Precio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.hasOffer) {
                            Text(
                                text = item.price.priceLabel(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }
                        Text(
                            text = item.finalPrice.priceLabel(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = item.stockLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (item.canBeOrdered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                    )
                }
            }

            rewardPresentation?.let {
                Text(
                    text = "${it.badge} · ${it.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Volver al menú",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun Double.priceLabel(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/view/RestaurantScreen.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedItemId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(sessionState.profile.nationalId) {
        viewModel.onAppear(sessionState.profile.nationalId)
    }

    val selectedItem = uiState.sections
        .flatMap { it.items }
        .firstOrNull { it.id == selectedItemId }

    if (selectedItem != null) {
        MenuItemDetailScreen(
            item = selectedItem,
            rewardPresentation = viewModel.rewardPresentation(selectedItem),
            onBack = { selectedItemId = null },
            modifier = modifier,
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Sabor de Los Altos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Menú, promos y platos destacados",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text(viewModel.currentLevelTitle()) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.LocalOffer,
                                contentDescription = null,
                            )
                        },
                    )
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.sections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.sections.isEmpty() -> {
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
                        top = 14.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    item {
                        RestaurantHeroCard(
                            clientName = sessionState.profile.fullName,
                            levelTitle = viewModel.currentLevelTitle(),
                            featuredCount = uiState.featuredItems.size,
                            sectionCount = uiState.sections.size,
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        item {
                            ErrorCard(
                                message = message,
                                onDismiss = viewModel::clearError,
                            )
                        }
                    }

                    if (uiState.featuredItems.isNotEmpty()) {
                        item {
                            FeaturedCarousel(
                                featuredItems = uiState.featuredItems,
                                rewardProvider = viewModel::rewardPresentation,
                                onOpen = { selectedItemId = it.id },
                            )
                        }
                    }

                    item {
                        CategorySelectorBlock(
                            selectedCategoryId = uiState.selectedCategoryId,
                            sections = uiState.sections,
                            onCategorySelected = viewModel::onCategorySelected,
                        )
                    }

                    items(uiState.visibleSections, key = { it.id }) { section ->
                        MenuSectionCard(
                            section = section,
                            rewardProvider = viewModel::rewardPresentation,
                            onOpen = { selectedItemId = it.id },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestaurantHeroCard(
    clientName: String,
    levelTitle: String,
    featuredCount: Int,
    sectionCount: Int,
) {
    val accent = MaterialTheme.colorScheme.secondary
    val friendlyName = clientName.substringBefore(" ").ifBlank { "amigo" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        accent,
                    ),
                ),
            )
            .padding(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                AssistChip(
                    onClick = {},
                    label = { Text("Nivel $levelTitle") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.LocalOffer,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White.copy(alpha = 0.16f),
                        labelColor = Color.White,
                        leadingIconContentColor = Color.White,
                    ),
                    border = null,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Hola, $friendlyName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Explora el menú con una experiencia más cercana a Altos iOS: hero visible, destacados arriba y categorías claras.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.94f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStatPill(title = "$featuredCount destacados")
                HeroStatPill(title = "$sectionCount categorías")
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
                text = "No se pudo actualizar el menú",
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
private fun EmptyRestaurantState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text = "No hay platos disponibles todavía.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando el menú esté publicado en Firestore, aparecerá aquí con sus categorías y destacados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FeaturedCarousel(
    featuredItems: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Destacados",
            subtitle = "El equivalente Compose del featuredCarousel de SwiftUI.",
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 2.dp),
        ) {
            items(featuredItems, key = { it.id }) { item ->
                FeaturedMenuCard(
                    item = item,
                    rewardPresentation = rewardProvider(item),
                    onOpen = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun CategorySelectorBlock(
    selectedCategoryId: String?,
    sections: List<MenuSection>,
    onCategorySelected: (String?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionHeader(
            title = "Explorar por categoría",
            subtitle = "Muévete rápido entre entradas, sopas, platos fuertes y más.",
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

            sections.forEach { section ->
                FilterChip(
                    selected = selectedCategoryId == section.category.id,
                    onClick = { onCategorySelected(section.category.id) },
                    label = { Text(section.category.title) },
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
private fun FeaturedMenuCard(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onOpen: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(292.dp)
            .height(208.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                    ),
                ),
            )
            .clickable(onClick = onOpen)
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.Top) {
                AssistChip(
                    onClick = onOpen,
                    label = { Text("Destacado") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        labelColor = Color.White,
                        leadingIconContentColor = Color.White,
                    ),
                    border = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                MenuStockBadge(item = item, onColor = Color.White)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (item.hasOffer) {
                        Text(
                            text = item.price.priceLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.72f),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        text = item.finalPrice.priceLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                rewardPresentation?.let {
                    CompactRewardRibbon(reward = it, onDark = true)
                }
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
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = section.category.title,
            subtitle = "${section.items.size} plato(s)",
        )

        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                section.items.forEachIndexed { index, item ->
                    MenuItemRow(
                        item = item,
                        rewardPresentation = rewardProvider(item),
                        onOpen = { onOpen(item) },
                    )

                    if (index < section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onOpen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (item.isFeatured) Icons.Rounded.LocalFireDepartment else Icons.Rounded.Restaurant,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (item.isFeatured) {
                    Spacer(modifier = Modifier.width(8.dp))
                    MiniStatusPill(
                        text = "Popular",
                        container = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        content = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PriceCluster(item = item)
                Spacer(modifier = Modifier.weight(1f))
                MenuStockBadge(item = item, onColor = MaterialTheme.colorScheme.onSurface)
            }

            rewardPresentation?.let {
                CompactRewardRibbon(reward = it, onDark = false)
            }

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun PriceCluster(item: MenuItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (item.hasOffer) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun MenuStockBadge(
    item: MenuItem,
    onColor: Color,
) {
    val container = when {
        item.canBeOrdered -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    }
    val content = when {
        item.canBeOrdered -> if (onColor == Color.White) Color.White else MaterialTheme.colorScheme.secondary
        else -> if (onColor == Color.White) Color.White else MaterialTheme.colorScheme.error
    }

    MiniStatusPill(
        text = item.stockLabel,
        container = container,
        content = content,
    )
}

@Composable
private fun MiniStatusPill(
    text: String,
    container: Color,
    content: Color,
) {
    Surface(
        color = container,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = content,
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MiniStatusPill(
                text = reward.badge,
                container = if (onDark) {
                    Color.White.copy(alpha = 0.18f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                },
                content = titleColor,
            )
            Text(
                text = reward.title,
                style = MaterialTheme.typography.labelLarge,
                color = titleColor,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodySmall,
                color = bodyColor,
            )
        }
    }
}

private fun Double.priceLabel(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/MenuUiState.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


data class MenuUiState(
    val isLoading: Boolean = true,
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
}

```

---

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/altos/restaurant/presentation/viewmodel/MenuViewModel.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel


@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var lastRewardsNationalId: String? = null

    fun onAppear(
        nationalId: String?,
    ) {
        if (observeJob == null) {
            observeJob = viewModelScope.launch {
                observeMenuUseCase.execute().collectLatest { sections ->
                    val sorted = sortSections(sections)
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            sections = sorted,
                            selectedCategoryId = current.selectedCategoryId
                                ?.takeIf { selected -> sorted.any { it.category.id == selected } }
                                ?: sorted.firstOrNull()?.category?.id,
                            errorMessage = null,
                        )
                    }
                }
            }
        }

        refreshRewards(nationalId)
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun rewardPresentation(
        item: MenuItem,
    ): RewardPresentation? = RewardPresentationFactory.menuPresentation(
        item = item,
        wallet = _uiState.value.walletSnapshot,
    )

    fun currentLevelTitle(): String = _uiState.value.walletSnapshot.currentLevel.title

    private fun refreshRewards(
        nationalId: String?,
    ) {
        val cleanNationalId = nationalId?.trim().orEmpty()
        if (cleanNationalId.isEmpty()) {
            _uiState.update {
                it.copy(walletSnapshot = RewardWalletSnapshot.empty(""))
            }
            lastRewardsNationalId = null
            return
        }

        if (lastRewardsNationalId == cleanNationalId &&
            _uiState.value.walletSnapshot.nationalId == cleanNationalId
        ) {
            return
        }

        lastRewardsNationalId = cleanNationalId

        viewModelScope.launch {
            runCatching {
                loyaltyRewardsRepository.loadWalletSnapshot(cleanNationalId)
            }.onSuccess { wallet ->
                _uiState.update { it.copy(walletSnapshot = wallet) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        walletSnapshot = RewardWalletSnapshot.empty(cleanNationalId),
                        errorMessage = error.message ?: "Could not load loyalty wallet.",
                    )
                }
            }
        }
    }

    private fun sortSections(
        sections: List<MenuSection>,
    ): List<MenuSection> = sections
        .map { section ->
            section.copy(items = section.items.sortedBy { it.sortOrder })
        }
        .sortedWith(
            compareBy<MenuSection> { categoryRank(it.category.title) }
                .thenBy { it.category.title }
        )

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

# app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/root/feature/di/AuthModule.kt

```kotlin
package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindSessionRepository(
        repository: FirebaseSessionRepository,
    ): SessionRepository
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
                RestaurantScreen(sessionState = sessionState)
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

