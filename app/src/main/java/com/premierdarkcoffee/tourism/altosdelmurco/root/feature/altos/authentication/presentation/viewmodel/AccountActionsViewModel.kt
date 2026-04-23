package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.DeleteCurrentAccountUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SignOutUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountActionsUiState(
    val isBusy: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AccountActionsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val deleteCurrentAccountUseCase: DeleteCurrentAccountUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountActionsUiState())
    val uiState: StateFlow<AccountActionsUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            runCatching {
                signOutUseCase.execute()
                sessionRepository.refresh()
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Could not sign out.") }
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun deleteAccount(
        currentUserId: String,
        freshGoogleIdToken: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            runCatching {
                deleteCurrentAccountUseCase.execute(
                    currentUserId = currentUserId,
                    googleIdToken = freshGoogleIdToken,
                )
                sessionRepository.refresh()
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Could not delete account.") }
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }
}
