package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

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
        if (current.nationalId == cleanNationalId && bookingsJob?.isActive == true) return
        _uiState.update { it.copy(nationalId = cleanNationalId) }
        observeBookings()
    }

    fun onDateSelected(date: Date) {
        _uiState.update { it.copy(selectedDate = date) }
        observeBookings()
    }

    fun refresh() {
        observeBookings()
    }

    fun cancelBooking(booking: AdventureBooking) {
        val nationalId = _uiState.value.nationalId
        if (nationalId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tu perfil no tiene cédula registrada.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCancelling = true,
                    errorMessage = null,
                    successMessage = null
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
                        successMessage = "Reserva cancelada correctamente.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isCancelling = false,
                        errorMessage = error.message ?: "No se pudo cancelar la reserva.",
                    )
                }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun observeBookings() {
        val snapshot = _uiState.value
        val nationalId = snapshot.nationalId
        if (nationalId.isBlank()) {
            bookingsJob?.cancel()
            bookingsJob = null
            _uiState.update { it.copy(bookings = emptyList(), isLoading = false) }
            return
        }

        bookingsJob?.cancel()
        bookingsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            observeAdventureBookingsUseCase.execute(
                day = snapshot.selectedDate,
                nationalId = nationalId,
            ).catch { error ->
                if (error is CancellationException) throw error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudieron cargar tus reservas.",
                    )
                }
            }.collectLatest { bookings ->
                _uiState.update {
                    it.copy(
                        bookings = bookings,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }
    }
}
