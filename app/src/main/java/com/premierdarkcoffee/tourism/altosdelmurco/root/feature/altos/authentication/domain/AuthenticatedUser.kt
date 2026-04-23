package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

/**
 * Kept intentionally compatible with the current migrated model layer.
 * The field name remains appleUserIdentifier for now so the Firestore/profile
 * contract does not have to change during migration.
 *
 * Under Google sign-in we store the Google provider UID here when available,
 * otherwise an empty string.
 */
data class AuthenticatedUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val appleUserIdentifier: String,
)
