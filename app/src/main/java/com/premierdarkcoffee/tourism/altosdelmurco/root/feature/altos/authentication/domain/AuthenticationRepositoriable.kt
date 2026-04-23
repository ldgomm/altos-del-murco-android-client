package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

interface AuthenticationRepositoriable {
    fun currentUser(): AuthenticatedUser?

    suspend fun signInWithApple(
        idToken: String,
        rawNonce: String,
        fullName: String?,
        email: String?,
        appleUserIdentifier: String,
    ): AuthenticatedUser

    suspend fun reauthenticateCurrentUser(
        idToken: String,
        rawNonce: String,
    )

    suspend fun deleteCurrentUser()

    fun signOut()
}
