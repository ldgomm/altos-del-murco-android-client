package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.CancelAdventureBookingUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ObserveAdventureBookingsUseCase
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
        val cleanNationalId = profile.nationalId.filter(Char::isDigit)
        val current = _uiState.value

        _uiState.update {
            it.copy(
                nationalId = cleanNationalId,
                now = Date(),
            )
        }

        if (current.nationalId == cleanNationalId && bookingsJob?.isActive == true) {
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
        _uiState.update {
            it.copy(selectedStatusFilter = filter)
        }
    }

    fun setSortOrder(sortOrder: AdventureReservationSortOrder) {
        _uiState.update {
            it.copy(sortOrder = sortOrder)
        }
    }

    fun cancelBooking(booking: AdventureBooking) {
        val nationalId = _uiState.value.nationalId

        if (nationalId.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "No se encontró una cédula asociada a esta cuenta.")
            }
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
                cancelAdventureBookingUseCase.execute(
                    id = booking.id,
                    nationalId = nationalId,
                )
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
        val nationalId = _uiState.value.nationalId

        bookingsJob?.cancel()

        if (nationalId.isBlank()) {
            bookingsJob = null
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
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    now = Date(),
                )
            }

            observeAdventureBookingsUseCase.execute(nationalId)
                .catch { error ->
                    if (error is CancellationException) throw error

                    _uiState.update {
                        it.copy(
                            allBookings = emptyList(),
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar tus reservas.",
                            now = Date(),
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