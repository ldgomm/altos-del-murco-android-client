package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState

    data class Authenticated(
        val profile: ClientProfile,
        val developerBypass: Boolean = false,
    ) : SessionState {
        val displayName: String get() = profile.fullName
        val userId: String get() = profile.id
    }

    data class NeedsProfileCompletion(
        val user: AuthenticatedUser,
        val existingProfile: ClientProfile?,
    ) : SessionState {
        val userId: String get() = user.uid
    }
}
