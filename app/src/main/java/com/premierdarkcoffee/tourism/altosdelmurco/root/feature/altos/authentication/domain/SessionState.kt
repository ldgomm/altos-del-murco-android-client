package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data class Authenticated(
        val displayName: String,
        val developerBypass: Boolean,
    ) : SessionState
    data class NeedsProfileCompletion(
        val userId: String,
    ) : SessionState
}
