package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import java.util.Date

data class AdventureBookingsUiState(
    val selectedDate: Date = Date(),
    val nationalId: String = "",
    val bookings: List<AdventureBooking> = emptyList(),
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
