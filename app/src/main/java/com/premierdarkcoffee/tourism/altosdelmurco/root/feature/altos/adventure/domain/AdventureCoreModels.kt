package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round

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
