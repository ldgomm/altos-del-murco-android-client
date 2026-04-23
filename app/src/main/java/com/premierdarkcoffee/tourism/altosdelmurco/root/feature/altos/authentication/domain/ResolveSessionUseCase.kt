package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class ResolveSessionUseCase(
    private val authRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) {
    suspend fun execute(): SessionDestination {
        val user = authRepository.currentUser() ?: return SessionDestination.SignedOut
        return execute(user)
    }

    suspend fun execute(user: AuthenticatedUser): SessionDestination {
        val profile = clientProfileRepository.fetchProfile(user.uid)

        return when {
            profile == null -> SessionDestination.NeedsProfile(user = user, profile = null)
            profile.isComplete -> SessionDestination.Authenticated(profile)
            else -> SessionDestination.NeedsProfile(user = user, profile = profile)
        }
    }
}
