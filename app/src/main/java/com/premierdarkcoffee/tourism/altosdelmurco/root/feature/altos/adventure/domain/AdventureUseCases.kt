package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain.AdventureBookingsRepositoriable
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

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