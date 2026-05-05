package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveOrdersUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val observeOrdersUseCase: ObserveOrdersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun syncProfile(profile: ClientProfile) {
        val cleanUserId = profile.userId
        if (cleanUserId == _uiState.value.userId && observeJob != null) return

        _uiState.update {
            it.copy(
                userId = cleanUserId,
                isLoading = cleanUserId.isNotEmpty(),
                errorMessage = null,
            )
        }

        observeOrders(cleanUserId)
    }

    fun setGrouping(value: OrdersGroupingOption) {
        _uiState.update { it.copy(grouping = value) }
    }

    fun setSortOption(value: OrdersSortOption) {
        _uiState.update { it.copy(sortOption = value) }
    }

    fun setStatusFilter(value: OrderStatus?) {
        _uiState.update { it.copy(statusFilter = value) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun observeOrders(userId: String) {
        observeJob?.cancel()

        if (userId.isBlank()) {
            _uiState.update {
                it.copy(
                    orders = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                )
            }
            return
        }

        observeJob = viewModelScope.launch {
            observeOrdersUseCase.execute(userId).catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar pedidos.",
                        )
                    }
                }.collectLatest { orders ->
                    _uiState.update {
                        it.copy(
                            orders = orders,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    companion object {
        private val dayFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "EC"))

        fun dateGroupTitle(date: Date): String = dayFormatter.format(date)
    }
}
