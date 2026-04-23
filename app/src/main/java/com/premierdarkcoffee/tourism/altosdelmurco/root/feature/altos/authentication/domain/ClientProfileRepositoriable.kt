package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile

interface ClientProfileRepositoriable {
    suspend fun fetchProfile(uid: String): ClientProfile?
    suspend fun saveProfile(profile: ClientProfile)
    suspend fun deleteProfile(uid: String)
}
