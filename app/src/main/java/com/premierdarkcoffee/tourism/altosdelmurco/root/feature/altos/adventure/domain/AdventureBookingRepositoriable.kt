package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AdventureBookingsRepositoriable {
    fun observeBookings(day: Date, nationalId: String): Flow<List<AdventureBooking>>

    suspend fun fetchAvailability(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot>

    suspend fun createBooking(request: AdventureBookingRequest): AdventureBooking

    suspend fun cancelBooking(id: String, nationalId: String)
}