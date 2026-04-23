package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import java.util.Date

data class ClientProfileDocument(
    val id: String = "",
    val email: String = "",
    val appleUserIdentifier: String = "",
    val fullName: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Date(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isProfileComplete: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val profileCompletedAt: Date? = null,
    val profileImageURL: String? = null,
    val profileImagePath: String? = null,
) {
    constructor(profile: ClientProfile) : this(
        id = profile.id,
        email = profile.email,
        appleUserIdentifier = profile.appleUserIdentifier,
        fullName = profile.fullName,
        nationalId = profile.nationalId,
        phoneNumber = profile.phoneNumber,
        birthday = profile.birthday,
        address = profile.address,
        emergencyContactName = profile.emergencyContactName,
        emergencyContactPhone = profile.emergencyContactPhone,
        isProfileComplete = profile.isProfileComplete,
        createdAt = profile.createdAt,
        updatedAt = profile.updatedAt,
        profileCompletedAt = profile.profileCompletedAt,
        profileImageURL = profile.profileImageURL,
        profileImagePath = profile.profileImagePath,
    )

    fun toDomain(): ClientProfile = ClientProfile(
        id = id,
        email = email,
        appleUserIdentifier = appleUserIdentifier,
        fullName = fullName,
        nationalId = nationalId,
        phoneNumber = phoneNumber,
        birthday = birthday,
        address = address,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        isProfileComplete = isProfileComplete,
        createdAt = createdAt,
        updatedAt = updatedAt,
        profileCompletedAt = profileCompletedAt,
        profileImageURL = profileImageURL,
        profileImagePath = profileImagePath,
    )
}
