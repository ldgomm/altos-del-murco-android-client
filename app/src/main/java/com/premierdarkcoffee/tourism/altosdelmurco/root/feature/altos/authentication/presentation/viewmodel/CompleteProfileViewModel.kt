package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.CompleteClientProfileUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompleteProfileUiState(
    val user: AuthenticatedUser? = null,
    val fullName: String = "",
    val email: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Calendar.getInstance()
        .apply { set(2000, 0, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.time,
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean =
        fullName.isNotBlank() &&
                nationalId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank() &&
                emergencyContactName.isNotBlank() &&
                emergencyContactPhone.isNotBlank()
}

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val completeClientProfileUseCase: CompleteClientProfileUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    fun initialize(
        user: AuthenticatedUser,
        existingProfile: ClientProfile?,
    ) {
        if (_uiState.value.user?.uid == user.uid) return

        _uiState.value = CompleteProfileUiState(
            user = user,
            fullName = existingProfile?.fullName ?: user.displayName,
            email = existingProfile?.email ?: user.email,
            nationalId = existingProfile?.nationalId.orEmpty(),
            phoneNumber = existingProfile?.phoneNumber.orEmpty(),
            birthday = existingProfile?.birthday ?: _uiState.value.birthday,
            address = existingProfile?.address.orEmpty(),
            emergencyContactName = existingProfile?.emergencyContactName.orEmpty(),
            emergencyContactPhone = existingProfile?.emergencyContactPhone.orEmpty(),
            errorMessage = null,
        )
    }

    fun onFullNameChanged(value: String) = update { copy(fullName = value) }
    fun onNationalIdChanged(value: String) = update { copy(nationalId = value) }
    fun onPhoneNumberChanged(value: String) = update { copy(phoneNumber = value) }
    fun onBirthdayChanged(value: Date) = update { copy(birthday = value) }
    fun onAddressChanged(value: String) = update { copy(address = value) }
    fun onEmergencyContactNameChanged(value: String) = update { copy(emergencyContactName = value) }
    fun onEmergencyContactPhoneChanged(value: String) =
        update { copy(emergencyContactPhone = value) }

    fun clearError() = update { copy(errorMessage = null) }

    fun saveProfile() {
        val snapshot = _uiState.value
        val user = snapshot.user ?: return

        if (!snapshot.canSubmit) {
            update { copy(errorMessage = "Please complete all required profile fields.") }
            return
        }

        viewModelScope.launch {
            update { copy(isSaving = true, errorMessage = null) }

            val now = Date()
            val profile = ClientProfile(
                id = user.uid,
                email = snapshot.email,
                appleUserIdentifier = user.appleUserIdentifier,
                fullName = snapshot.fullName.trim(),
                nationalId = snapshot.nationalId.trim(),
                phoneNumber = snapshot.phoneNumber.trim(),
                birthday = snapshot.birthday,
                address = snapshot.address.trim(),
                emergencyContactName = snapshot.emergencyContactName.trim(),
                emergencyContactPhone = snapshot.emergencyContactPhone.trim(),
                isProfileComplete = true,
                createdAt = now,
                updatedAt = now,
                profileCompletedAt = now,
                profileImageURL = null,
                profileImagePath = null,
            )

            runCatching {
                completeClientProfileUseCase.execute(profile)
                sessionRepository.refresh()
            }.onFailure { error ->
                update { copy(errorMessage = error.message ?: "Could not save profile.") }
            }

            update { copy(isSaving = false) }
        }
    }

    private inline fun update(transform: CompleteProfileUiState.() -> CompleteProfileUiState) {
        _uiState.update(transform)
    }
}
