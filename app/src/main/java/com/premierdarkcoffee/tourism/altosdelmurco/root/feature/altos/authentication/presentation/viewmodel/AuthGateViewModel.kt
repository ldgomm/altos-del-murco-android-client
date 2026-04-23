package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthGateViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionRepository
        .sessionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )

    fun refreshSession() {
        viewModelScope.launch {
            sessionRepository.refresh()
        }
    }
}
