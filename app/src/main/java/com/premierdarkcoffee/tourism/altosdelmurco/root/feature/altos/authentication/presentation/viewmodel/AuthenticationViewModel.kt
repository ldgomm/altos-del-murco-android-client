package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SignInWithGoogleUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepositoriable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthenticationUiState(
    val isSubmitting: Boolean = false,
    val isTryingAuthorizedAccounts: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    fun beginAuthorizedAccountsAttempt() {
        _uiState.update {
            it.copy(
                isTryingAuthorizedAccounts = true,
                errorMessage = null,
            )
        }
    }

    fun finishAuthorizedAccountsAttempt() {
        _uiState.update {
            it.copy(isTryingAuthorizedAccounts = false)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                )
            }

            runCatching {
                signInWithGoogleUseCase.execute(idToken)
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Google sign-in failed.")
                }
            }

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isTryingAuthorizedAccounts = false,
                )
            }
        }
    }

    fun onSignInError(message: String) {
        _uiState.update {
            it.copy(
                isSubmitting = false,
                isTryingAuthorizedAccounts = false,
                errorMessage = message,
            )
        }
    }
}
