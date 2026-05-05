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

    val userIdText: String
        get() = profile?.userId?.takeIf { it.isNotBlank() } ?: "No registrado"

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
        get() = displayName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }.ifBlank { "AM" }

    val hasProfileImage: Boolean
        get() = avatarBytes != null || profile?.hasProfileImage == true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileUiState

        if (isLoadingAvatar != other.isLoadingAvatar) return false
        if (isLoadingStats != other.isLoadingStats) return false
        if (isUploadingProfileImage != other.isUploadingProfileImage) return false
        if (isSavingProfile != other.isSavingProfile) return false
        if (isSigningOut != other.isSigningOut) return false
        if (isDeletingAccount != other.isDeletingAccount) return false
        if (profile != other.profile) return false
        if (stats != other.stats) return false
        if (!avatarBytes.contentEquals(other.avatarBytes)) return false
        if (editState != other.editState) return false
        if (message != other.message) return false
        if (hasProfileImage != other.hasProfileImage) return false
        if (displayName != other.displayName) return false
        if (emailText != other.emailText) return false
        if (phoneText != other.phoneText) return false
        if (userIdText != other.userIdText) return false
        if (birthdayText != other.birthdayText) return false
        if (addressText != other.addressText) return false
        if (emergencyContactText != other.emergencyContactText) return false
        if (memberSinceText != other.memberSinceText) return false
        if (initials != other.initials) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoadingAvatar.hashCode()
        result = 31 * result + isLoadingStats.hashCode()
        result = 31 * result + isUploadingProfileImage.hashCode()
        result = 31 * result + isSavingProfile.hashCode()
        result = 31 * result + isSigningOut.hashCode()
        result = 31 * result + isDeletingAccount.hashCode()
        result = 31 * result + (profile?.hashCode() ?: 0)
        result = 31 * result + stats.hashCode()
        result = 31 * result + (avatarBytes?.contentHashCode() ?: 0)
        result = 31 * result + (editState?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + hasProfileImage.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + emailText.hashCode()
        result = 31 * result + phoneText.hashCode()
        result = 31 * result + userIdText.hashCode()
        result = 31 * result + birthdayText.hashCode()
        result = 31 * result + addressText.hashCode()
        result = 31 * result + emergencyContactText.hashCode()
        result = 31 * result + memberSinceText.hashCode()
        result = 31 * result + initials.hashCode()
        return result
    }
}

data class EditProfileUiState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val birthday: Date = defaultBirthday(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
) {
    val canSave: Boolean
        get() = fullName.trim().isNotEmpty()

    companion object {
        fun fromProfile(profile: ClientProfile): EditProfileUiState = EditProfileUiState(
            fullName = profile.fullName,
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
    phoneNumber = edit.phoneNumber.trim(),
    birthday = edit.birthday,
    address = edit.address.trim(),
    emergencyContactName = edit.emergencyContactName.trim(),
    emergencyContactPhone = edit.emergencyContactPhone.trim(),
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
fun Date.formatDateLong(): String =
    SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "EC")).format(this)

private fun defaultBirthday(): Date = Calendar.getInstance().apply {
    add(Calendar.YEAR, -18)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time
