package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class SignInWithGoogleUseCase(
    private val repository: AuthenticationRepositoriable,
) {
    suspend fun execute(
        googleIdToken: String,
    ): AuthenticatedUser = repository.signInWithGoogle(googleIdToken)
}
