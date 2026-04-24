package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ObserveAdventureCatalogUseCase
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
import javax.inject.Inject

@HiltViewModel
class AdventureCatalogViewModel @Inject constructor(
    private val observeAdventureCatalogUseCase: ObserveAdventureCatalogUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureCatalogUiState())
    val uiState: StateFlow<AdventureCatalogUiState> = _uiState.asStateFlow()

    private var catalogJob: Job? = null

    fun onAppear() {
        if (catalogJob?.isActive == true) return

        catalogJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            observeAdventureCatalogUseCase.execute()
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo cargar el catálogo de aventura.",
                        )
                    }
                }
                .collectLatest { catalog ->
                    _uiState.update {
                        it.copy(
                            catalog = catalog,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun onDisappear() {
        catalogJob?.cancel()
        catalogJob = null
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
