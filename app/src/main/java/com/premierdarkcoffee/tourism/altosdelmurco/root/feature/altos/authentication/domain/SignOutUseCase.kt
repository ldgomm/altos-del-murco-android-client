package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

class SignOutUseCase(
    private val repository: AuthenticationRepositoriable,
) {
    fun execute() = repository.signOut()
}
