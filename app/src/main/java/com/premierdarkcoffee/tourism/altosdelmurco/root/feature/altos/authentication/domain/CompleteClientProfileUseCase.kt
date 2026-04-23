package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile

class CompleteClientProfileUseCase(
    private val repository: ClientProfileRepositoriable,
) {
    suspend fun execute(profile: ClientProfile) {
        repository.saveProfile(profile)
    }
}
