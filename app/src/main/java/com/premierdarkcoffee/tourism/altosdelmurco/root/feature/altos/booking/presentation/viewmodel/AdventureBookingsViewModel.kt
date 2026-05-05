package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.CancelAdventureBookingUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ObserveAdventureBookingsUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain.AdventureBookingCancellationPolicy
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AdventureBookingsViewModel @Inject constructor(
    private val observeAdventureBookingsUseCase: ObserveAdventureBookingsUseCase,
    private val cancelAdventureBookingUseCase: CancelAdventureBookingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureBookingsUiState())
    val uiState: StateFlow<AdventureBookingsUiState> = _uiState.asStateFlow()

    private var bookingsJob: Job? = null

    fun onAppear(profile: ClientProfile) {
        val cleanUserId = profile.userId
        val current = _uiState.value

        _uiState.update {
            it.copy(
                userId = cleanUserId,
                now = Date(),
            )
        }

        if (current.userId == cleanUserId && bookingsJob?.isActive == true) {
            return
        }

        observeBookings()
    }

    fun onDisappear() {
        bookingsJob?.cancel()
        bookingsJob = null
    }

    fun refresh() {
        _uiState.update { it.copy(now = Date()) }
        observeBookings()
    }

    fun setTimelineFilter(filter: AdventureReservationTimelineFilter) {
        _uiState.update {
            it.copy(
                selectedTimelineFilter = filter,
                now = Date(),
            )
        }
    }

    fun setStatusFilter(filter: AdventureReservationStatusFilter) {
        _uiState.update { it.copy(selectedStatusFilter = filter) }
    }

    fun setSortOrder(sortOrder: AdventureReservationSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun canCancel(booking: AdventureBooking): Boolean =
        AdventureBookingCancellationPolicy.canClientCancel(booking, _uiState.value.now)

    fun cancellationBlockedReason(booking: AdventureBooking): String? =
        AdventureBookingCancellationPolicy.reasonClientCannotCancel(booking, _uiState.value.now)

    fun cancelBooking(booking: AdventureBooking) {
        val userId = _uiState.value.userId

        if (userId.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "No se encontró una sesión activa para esta cuenta.")
            }
            return
        }

        AdventureBookingCancellationPolicy.reasonClientCannotCancel(booking, _uiState.value.now)
            ?.let { reason ->
                _uiState.update { it.copy(errorMessage = reason) }
                return
            }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCancelling = true,
                    cancellingBookingId = booking.id,
                    errorMessage = null,
                    successMessage = null,
                )
            }

            runCatching {
                cancelAdventureBookingUseCase.execute(id = booking.id)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isCancelling = false,
                        cancellingBookingId = null,
                        successMessage = "Reserva cancelada correctamente.",
                        now = Date(),
                    )
                }
            }.onFailure { error ->
                if (error is CancellationException) throw error

                _uiState.update {
                    it.copy(
                        isCancelling = false,
                        cancellingBookingId = null,
                        errorMessage = error.message ?: "No se pudo cancelar la reserva.",
                    )
                }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    private fun observeBookings() {
        val userId = _uiState.value.userId
        bookingsJob?.cancel()

        if (userId.isBlank()) {
            _uiState.update {
                it.copy(
                    allBookings = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                )
            }
            return
        }

        bookingsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            observeAdventureBookingsUseCase.execute(userId)
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar tus reservas.",
                        )
                    }
                }
                .collectLatest { bookings ->
                    _uiState.update {
                        it.copy(
                            allBookings = bookings,
                            isLoading = false,
                            errorMessage = null,
                            now = Date(),
                        )
                    }
                }
        }
    }
}
