package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingBlock
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingRequest
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBuildPlan
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureResourceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationEventType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationServingMoment
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import java.util.Date
import java.util.UUID

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
