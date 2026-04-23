package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import java.util.Date

data class ClientProfile(
    val id: String,
    val email: String,
    val appleUserIdentifier: String,
    val fullName: String,
    val nationalId: String,
    val phoneNumber: String,
    val birthday: Date,
    val address: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val isProfileComplete: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    val profileCompletedAt: Date?,
    val profileImageURL: String?,
    val profileImagePath: String?,
) {
    val isComplete: Boolean
        get() = isProfileComplete &&
                fullName.isNotBlank() &&
                nationalId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank() &&
                emergencyContactName.isNotBlank() &&
                emergencyContactPhone.isNotBlank()

    val hasProfileImage: Boolean
        get() = !profileImageURL.isNullOrBlank()
}
