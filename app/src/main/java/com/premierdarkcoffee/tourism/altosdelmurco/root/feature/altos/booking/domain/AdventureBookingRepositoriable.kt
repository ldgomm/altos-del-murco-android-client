package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingRequest
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AdventureBookingsRepositoriable {
    fun observeBookings(userId: String): Flow<List<AdventureBooking>>

    suspend fun fetchAvailability(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot>

    suspend fun createBooking(request: AdventureBookingRequest): AdventureBooking

    suspend fun cancelBooking(id: String)
}
