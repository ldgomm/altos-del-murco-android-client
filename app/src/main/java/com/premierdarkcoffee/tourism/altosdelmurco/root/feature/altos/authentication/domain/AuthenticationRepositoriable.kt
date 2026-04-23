package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

interface AuthenticationRepositoriable {
    fun currentUser(): AuthenticatedUser?

    suspend fun signInWithGoogle(
        googleIdToken: String,
    ): AuthenticatedUser

    suspend fun reauthenticateCurrentUser(
        googleIdToken: String,
    )

    suspend fun deleteCurrentUser()

    fun signOut()
}
