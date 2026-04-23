package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class DeleteCurrentAccountUseCase(
    private val authRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) {
    suspend fun execute(
        currentUserId: String,
        googleIdToken: String,
    ) {
        authRepository.reauthenticateCurrentUser(googleIdToken)
        clientProfileRepository.deleteProfile(currentUserId)
        authRepository.deleteCurrentUser()
    }
}
