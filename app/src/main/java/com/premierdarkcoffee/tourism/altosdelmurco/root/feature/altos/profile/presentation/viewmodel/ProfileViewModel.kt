package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.CompleteClientProfileUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.DeleteCurrentAccountUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SignOutUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.DeleteProfileImageUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoadProfileImageUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ObserveProfileStatsUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStats
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeProfileStatsUseCase: ObserveProfileStatsUseCase,
    private val loadProfileImageUseCase: LoadProfileImageUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val deleteProfileImageUseCase: DeleteProfileImageUseCase,
    private val completeClientProfileUseCase: CompleteClientProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteCurrentAccountUseCase: DeleteCurrentAccountUseCase,
    private val sessionRepositoriable: SessionRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var statsJob: Job? = null
    private var avatarJob: Job? = null

    fun onAppear(profile: ClientProfile) {
        val currentProfile = _uiState.value.profile
        if (currentProfile?.id != profile.id || currentProfile.updatedAt != profile.updatedAt) {
            _uiState.update {
                it.copy(
                    profile = profile,
                    editState = null,
                    message = null,
                )
            }
            loadAvatar(profile)
        }

        observeStats(profile)
    }

    fun refresh() {
        val profile = _uiState.value.profile ?: return
        observeStats(profile, forceRestart = true)
        loadAvatar(profile, forceReload = true)
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun presentError(message: String) {
        _uiState.update { it.copy(message = ProfileMessage.Error(message)) }
    }

    fun beginEditProfile() {
        val profile = _uiState.value.profile ?: return
        _uiState.update { it.copy(editState = EditProfileUiState.fromProfile(profile)) }
    }

    fun cancelEditProfile() {
        _uiState.update { it.copy(editState = null) }
    }

    fun onEditFullNameChanged(value: String) = updateEdit { copy(fullName = value) }
    fun onEditPhoneChanged(value: String) = updateEdit { copy(phoneNumber = value) }
    fun onEditBirthdayChanged(value: Date) = updateEdit { copy(birthday = value) }
    fun onEditAddressChanged(value: String) = updateEdit { copy(address = value) }
    fun onEditEmergencyNameChanged(value: String) =
        updateEdit { copy(emergencyContactName = value) }

    fun onEditEmergencyPhoneChanged(value: String) =
        updateEdit { copy(emergencyContactPhone = value) }

    fun saveEditedProfile() {
        val profile = _uiState.value.profile ?: return
        val edit = _uiState.value.editState ?: return

        if (!edit.canSave) {
            _uiState.update {
                it.copy(message = ProfileMessage.Error("Completa correctamente todos los campos obligatorios."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingProfile = true, message = null) }

            runCatching {
                val updated = profile.updatedFromEdit(edit)
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        editState = null,
                        isSavingProfile = false,
                        message = ProfileMessage.Success("Perfil actualizado correctamente."),
                    )
                }
                observeStats(updated, forceRestart = true)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        message = ProfileMessage.Error(
                            error.message ?: "No se pudo actualizar el perfil."
                        ),
                    )
                }
            }
        }
    }

    fun uploadProfileImage(bytes: ByteArray) {
        val profile = _uiState.value.profile ?: return
        if (bytes.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingProfileImage = true, message = null) }

            runCatching {
                val uploaded = uploadProfileImageUseCase.execute(profile, bytes)
                val updated = profile.copy(
                    profileImageURL = uploaded.downloadURL,
                    profileImagePath = uploaded.storagePath,
                    updatedAt = Date(),
                )
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        avatarBytes = bytes,
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Success("Foto de perfil actualizada."),
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Error(
                            error.message ?: "No se pudo subir la foto."
                        ),
                    )
                }
            }
        }
    }

    fun removeProfileImage() {
        val profile = _uiState.value.profile ?: return
        if (!profile.hasProfileImage && _uiState.value.avatarBytes == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingProfileImage = true, message = null) }

            runCatching {
                deleteProfileImageUseCase.execute(profile)
                val updated = profile.copy(
                    profileImageURL = null,
                    profileImagePath = null,
                    updatedAt = Date(),
                )
                completeClientProfileUseCase.execute(updated)
                sessionRepositoriable.refresh()
                updated
            }.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        profile = updated,
                        avatarBytes = null,
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Success("Foto de perfil eliminada."),
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUploadingProfileImage = false,
                        message = ProfileMessage.Error(
                            error.message ?: "No se pudo eliminar la foto."
                        ),
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true, message = null) }

            runCatching {
                signOutUseCase.execute()
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        message = ProfileMessage.Error(
                            error.message ?: "No se pudo cerrar sesión."
                        )
                    )
                }
            }

            _uiState.update { it.copy(isSigningOut = false) }
        }
    }

    fun deleteAccount(freshGoogleIdToken: String) {
        val profile = _uiState.value.profile ?: return
        if (freshGoogleIdToken.isBlank()) {
            _uiState.update {
                it.copy(message = ProfileMessage.Error("Google no devolvió una credencial válida."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, message = null) }

            runCatching {
                deleteProfileImageUseCase.execute(profile)
                deleteCurrentAccountUseCase.execute(
                    currentUserId = profile.id,
                    googleIdToken = freshGoogleIdToken,
                )
                sessionRepositoriable.refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        message = ProfileMessage.Error(
                            error.message ?: "No se pudo eliminar la cuenta."
                        ),
                    )
                }
            }
        }
    }

    private fun loadAvatar(profile: ClientProfile, forceReload: Boolean = false) {
        avatarJob?.cancel()
        avatarJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAvatar = true) }

            runCatching {
                if (forceReload && !profile.profileImageURL.isNullOrBlank()) {
                    loadProfileImageUseCase.execute(profile.copy(profileImageURL = profile.profileImageURL))
                } else {
                    loadProfileImageUseCase.execute(profile)
                }
            }.onSuccess { bytes ->
                _uiState.update { it.copy(avatarBytes = bytes, isLoadingAvatar = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingAvatar = false) }
            }
        }
    }

    private fun observeStats(profile: ClientProfile, forceRestart: Boolean = false) {
        val userId = profile.userId.trim()
        if (userId.isEmpty()) {
            statsJob?.cancel()
            _uiState.update {
                it.copy(
                    stats = ProfileStats.Companion.EMPTY,
                    isLoadingStats = false
                )
            }
            return
        }

        if (!forceRestart && statsJob?.isActive == true) return

        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStats = true) }

            observeProfileStatsUseCase.execute(userId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingStats = false,
                            message = ProfileMessage.Error(
                                error.message ?: "No se pudieron cargar tus estadísticas."
                            ),
                        )
                    }
                }
                .collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                            isLoadingStats = false,
                        )
                    }
                }
        }
    }

    private inline fun updateEdit(
        transform: EditProfileUiState.() -> EditProfileUiState,
    ) {
        _uiState.update { state ->
            val edit = state.editState ?: return@update state
            state.copy(editState = edit.transform())
        }
    }
}
