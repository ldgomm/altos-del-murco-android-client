package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

data class AuthenticatedUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val appleUserIdentifier: String,
)
