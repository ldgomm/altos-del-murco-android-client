package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.auth.FirebaseAuthException

private val terminalSessionErrorCodes = setOf(
    "ERROR_USER_DISABLED",
    "ERROR_USER_NOT_FOUND",
    "ERROR_USER_TOKEN_EXPIRED",
    "ERROR_INVALID_USER_TOKEN",
)

fun Throwable.isFirebaseSessionInvalidOrDisabled(): Boolean {
    val authException = generateSequence(this as Throwable?) { it.cause }
        .filterIsInstance<FirebaseAuthException>()
        .firstOrNull()
        ?: return false

    return authException.errorCode in terminalSessionErrorCodes
}