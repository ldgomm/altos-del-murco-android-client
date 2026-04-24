package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class AuthGateViewModel @Inject constructor(
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionRepositoriable
        .sessionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )

    init {
        startSessionGuard()
    }

    fun refreshSession() {
        viewModelScope.launch {
            sessionRepositoriable.refresh()
        }
    }

    fun verifySessionNow() {
        refreshSession()
    }

    private fun startSessionGuard() {
        viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                sessionRepositoriable.refresh()
            }
        }
    }
}