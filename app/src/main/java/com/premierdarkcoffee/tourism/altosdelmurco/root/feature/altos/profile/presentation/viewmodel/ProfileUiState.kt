package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStats
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.ThemeMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed interface ProfileMessage {
    data class Error(val message: String) : ProfileMessage
    data class Success(val message: String) : ProfileMessage
}

data class ProfileUiState(
    val profile: ClientProfile? = null,
    val stats: ProfileStats = ProfileStats.EMPTY,
    val avatarBytes: ByteArray? = null,
    val isLoadingAvatar: Boolean = false,
    val isLoadingStats: Boolean = false,
    val isUploadingProfileImage: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isSigningOut: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val editState: EditProfileUiState? = null,
    val message: ProfileMessage? = null,
) {
    val displayName: String
        get() = profile?.fullName?.takeIf { it.isNotBlank() } ?: "Invitado"

    val emailText: String
        get() = profile?.email?.takeIf { it.isNotBlank() } ?: "Correo oculto"

    val phoneText: String
        get() = profile?.phoneNumber?.takeIf { it.isNotBlank() } ?: "No registrado"

    val nationalIdText: String
        get() = profile?.nationalId?.takeIf { it.isNotBlank() } ?: "No registrado"

    val birthdayText: String
        get() = profile?.birthday?.formatDateLong() ?: "No registrado"

    val addressText: String
        get() = profile?.address?.takeIf { it.isNotBlank() } ?: "No registrado"

    val emergencyContactText: String
        get() {
            val name = profile?.emergencyContactName?.takeIf { it.isNotBlank() } ?: "No registrado"
            val phone = profile?.emergencyContactPhone?.takeIf { it.isNotBlank() }
            return if (phone == null) name else "$name • $phone"
        }

    val memberSinceText: String
        get() = profile?.createdAt?.formatDateShort() ?: "Ahora"

    val initials: String
        get() = displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "AM" }

    val hasProfileImage: Boolean
        get() = avatarBytes != null || profile?.hasProfileImage == true
}

data class EditProfileUiState(
    val fullName: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = defaultBirthday(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
) {
    val canSave: Boolean
        get() = fullName.trim().isNotEmpty() &&
                nationalId.onlyDigits().length >= 8 &&
                phoneNumber.onlyDigits().length >= 8 &&
                address.trim().isNotEmpty() &&
                emergencyContactName.trim().isNotEmpty() &&
                emergencyContactPhone.onlyDigits().length >= 8

    companion object {
        fun fromProfile(profile: ClientProfile): EditProfileUiState = EditProfileUiState(
            fullName = profile.fullName,
            nationalId = profile.nationalId,
            phoneNumber = profile.phoneNumber,
            birthday = profile.birthday,
            address = profile.address,
            emergencyContactName = profile.emergencyContactName,
            emergencyContactPhone = profile.emergencyContactPhone,
        )
    }
}

data class AccountActionUiState(
    val isBusy: Boolean = false,
    val needsFreshGoogleToken: Boolean = false,
)

fun ClientProfile.updatedFromEdit(edit: EditProfileUiState): ClientProfile = copy(
    fullName = edit.fullName.trim(),
    nationalId = edit.nationalId.onlyDigits(),
    phoneNumber = edit.phoneNumber.onlyDigits(),
    birthday = edit.birthday,
    address = edit.address.trim(),
    emergencyContactName = edit.emergencyContactName.trim(),
    emergencyContactPhone = edit.emergencyContactPhone.onlyDigits(),
    isProfileComplete = true,
    updatedAt = Date(),
    profileCompletedAt = profileCompletedAt ?: Date(),
)

fun ThemeMode.displayTitle(): String = when (this) {
    ThemeMode.SYSTEM -> "Sistema"
    ThemeMode.LIGHT -> "Claro"
    ThemeMode.DARK -> "Oscuro"
}

fun Date.formatDateShort(): String = SimpleDateFormat("d MMM yyyy", Locale("es", "EC")).format(this)
fun Date.formatDateLong(): String = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "EC")).format(this)
fun String.onlyDigits(): String = filter(Char::isDigit)

private fun defaultBirthday(): Date = Calendar.getInstance().apply {
    add(Calendar.YEAR, -18)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time
