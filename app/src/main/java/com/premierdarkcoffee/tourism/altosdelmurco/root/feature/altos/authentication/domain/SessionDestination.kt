package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile

sealed interface SessionDestination {
    data object SignedOut : SessionDestination
    data class NeedsProfile(
        val user: AuthenticatedUser,
        val profile: ClientProfile?,
    ) : SessionDestination
    data class Authenticated(
        val profile: ClientProfile,
    ) : SessionDestination
}
