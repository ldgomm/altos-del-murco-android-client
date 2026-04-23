package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.CompleteClientProfileUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompleteProfileUiState(
    val user: AuthenticatedUser? = null,
    val existingProfile: ClientProfile? = null,
    val fullName: String = "",
    val email: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Calendar.getInstance()
        .apply {
            set(2000, 0, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        .time,
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = fullName.trim().isNotEmpty() &&
            email.trim().isNotEmpty() &&
            email.trim().contains("@") &&
            nationalId.onlyDigits().length >= 8 &&
            phoneNumber.onlyDigits().length >= 8 &&
            address.trim().isNotEmpty() &&
            emergencyContactName.trim().isNotEmpty() &&
            emergencyContactPhone.onlyDigits().length >= 8 &&
            birthday <= Date()
}

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val completeClientProfileUseCase: CompleteClientProfileUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    private val _profileCompleted = MutableSharedFlow<ClientProfile>(extraBufferCapacity = 1)
    val profileCompleted: SharedFlow<ClientProfile> = _profileCompleted.asSharedFlow()

    fun initialize(
        user: AuthenticatedUser,
        existingProfile: ClientProfile?,
    ) {
        val current = _uiState.value
        if (current.user?.uid == user.uid && current.existingProfile == existingProfile) return

        val now = Date()
        val defaultBirthday = Calendar.getInstance()
            .apply {
                time = now
                add(Calendar.YEAR, -18)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time

        _uiState.value = CompleteProfileUiState(
            user = user,
            existingProfile = existingProfile,
            fullName = existingProfile?.fullName ?: user.displayName,
            email = existingProfile?.email ?: user.email,
            nationalId = existingProfile?.nationalId.orEmpty(),
            phoneNumber = existingProfile?.phoneNumber.orEmpty(),
            birthday = existingProfile?.birthday ?: defaultBirthday,
            address = existingProfile?.address.orEmpty(),
            emergencyContactName = existingProfile?.emergencyContactName.orEmpty(),
            emergencyContactPhone = existingProfile?.emergencyContactPhone.orEmpty(),
            isSaving = false,
            errorMessage = null,
        )
    }

    fun onFullNameChanged(value: String) = update { copy(fullName = value) }
    fun onEmailChanged(value: String) = update { copy(email = value) }
    fun onNationalIdChanged(value: String) = update { copy(nationalId = value) }
    fun onPhoneNumberChanged(value: String) = update { copy(phoneNumber = value) }
    fun onBirthdayChanged(value: Date) = update { copy(birthday = value) }
    fun onAddressChanged(value: String) = update { copy(address = value) }
    fun onEmergencyContactNameChanged(value: String) = update { copy(emergencyContactName = value) }
    fun onEmergencyContactPhoneChanged(value: String) = update { copy(emergencyContactPhone = value) }
    fun clearError() = update { copy(errorMessage = null) }

    fun saveProfile() {
        val snapshot = _uiState.value
        val user = snapshot.user ?: return

        if (!snapshot.canSubmit) {
            update { copy(errorMessage = "Completa correctamente todos los campos obligatorios.") }
            return
        }

        viewModelScope.launch {
            update { copy(isSaving = true, errorMessage = null) }

            val now = Date()
            val existingProfile = snapshot.existingProfile
            val profile = ClientProfile(
                id = user.uid,
                email = snapshot.email.trim(),
                appleUserIdentifier = user.appleUserIdentifier,
                fullName = snapshot.fullName.trim(),
                nationalId = snapshot.nationalId.onlyDigits(),
                phoneNumber = snapshot.phoneNumber.onlyDigits(),
                birthday = snapshot.birthday,
                address = snapshot.address.trim(),
                emergencyContactName = snapshot.emergencyContactName.trim(),
                emergencyContactPhone = snapshot.emergencyContactPhone.onlyDigits(),
                isProfileComplete = true,
                createdAt = existingProfile?.createdAt ?: now,
                updatedAt = now,
                profileCompletedAt = existingProfile?.profileCompletedAt ?: now,
                profileImageURL = existingProfile?.profileImageURL,
                profileImagePath = existingProfile?.profileImagePath,
            )

            runCatching {
                completeClientProfileUseCase.execute(profile)
            }.onFailure { error ->
                update {
                    copy(
                        isSaving = false,
                        errorMessage = error.message ?: "No se pudo guardar el perfil.",
                    )
                }
                return@launch
            }

            update { copy(isSaving = false, existingProfile = profile) }
            _profileCompleted.tryEmit(profile)

            runCatching {
                repeat(3) { attempt ->
                    sessionRepository.refresh()
                    if (attempt < 2) delay(120)
                }
            }
        }
    }

    private inline fun update(
        transform: CompleteProfileUiState.() -> CompleteProfileUiState,
    ) {
        _uiState.update(transform)
    }
}

private fun String.onlyDigits(): String = filter(Char::isDigit)
