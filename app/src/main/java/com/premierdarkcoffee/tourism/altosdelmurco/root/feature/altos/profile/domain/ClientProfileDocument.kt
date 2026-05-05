package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import java.util.Date

data class ClientProfileDocument(
    val id: String = "",
    val userId: String = "",
    val email: String = "",
    val appleUserIdentifier: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val birthday: Date = Date(),
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isProfileComplete: Boolean = false,
    val profileComplete: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val profileCompletedAt: Date? = null,
    val profileImageURL: String? = null,
    val profileImagePath: String? = null,
) {
    constructor(profile: ClientProfile) : this(
        id = profile.id,
        userId = profile.userId,
        email = profile.email,
        appleUserIdentifier = profile.appleUserIdentifier,
        fullName = profile.fullName,
        phoneNumber = profile.phoneNumber,
        birthday = profile.birthday,
        address = profile.address,
        emergencyContactName = profile.emergencyContactName,
        emergencyContactPhone = profile.emergencyContactPhone,
        isProfileComplete = profile.isProfileComplete,
        profileComplete = profile.isProfileComplete,
        createdAt = profile.createdAt,
        updatedAt = profile.updatedAt,
        profileCompletedAt = profile.profileCompletedAt,
        profileImageURL = profile.profileImageURL,
        profileImagePath = profile.profileImagePath,
    )

    fun toDomain(
        documentIdFallback: String? = null,
    ): ClientProfile {
        val resolvedId = id.trim()
            .ifEmpty { userId.trim() }
            .ifEmpty { documentIdFallback?.trim().orEmpty() }

        return ClientProfile(
            id = resolvedId,
            email = email.trim(),
            appleUserIdentifier = appleUserIdentifier.trim(),
            fullName = fullName.trim(),
            phoneNumber = phoneNumber.trim(),
            birthday = birthday,
            address = address.trim(),
            emergencyContactName = emergencyContactName.trim(),
            emergencyContactPhone = emergencyContactPhone.trim(),
            isProfileComplete = isProfileComplete || profileComplete,
            createdAt = createdAt,
            updatedAt = updatedAt,
            profileCompletedAt = profileCompletedAt,
            profileImageURL = profileImageURL?.trim()?.takeIf { it.isNotEmpty() },
            profileImagePath = profileImagePath?.trim()?.takeIf { it.isNotEmpty() },
        )
    }
}
