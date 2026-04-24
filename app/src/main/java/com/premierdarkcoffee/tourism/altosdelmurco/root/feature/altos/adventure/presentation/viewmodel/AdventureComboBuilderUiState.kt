package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationEventType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationServingMoment
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import java.util.Date

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
