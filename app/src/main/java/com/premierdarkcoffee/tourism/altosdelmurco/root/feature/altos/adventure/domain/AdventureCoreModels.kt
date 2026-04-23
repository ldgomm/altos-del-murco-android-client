package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import java.util.Calendar
import java.util.Date
import java.util.UUID

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
