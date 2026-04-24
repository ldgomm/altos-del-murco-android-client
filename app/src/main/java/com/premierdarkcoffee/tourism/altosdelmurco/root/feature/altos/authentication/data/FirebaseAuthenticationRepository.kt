package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthenticationRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthenticationRepositoriable {

    override fun currentUser(): AuthenticatedUser? {
        val user = auth.currentUser ?: return null

        return AuthenticatedUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty(),
            appleUserIdentifier = user.googleProviderUid(),
        )
    }

    override suspend fun signInWithGoogle(
        googleIdToken: String,
    ): AuthenticatedUser {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val authResult = auth.signInWithCredential(credential).awaitResult()

        val firebaseUser = requireNotNull(authResult.user) {
            "Firebase auth returned a null user after Google sign in."
        }

        return AuthenticatedUser(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.displayName.orEmpty(),
            appleUserIdentifier = firebaseUser.googleProviderUid(),
        )
    }

    override suspend fun reauthenticateCurrentUser(
        googleIdToken: String,
    ) {
        val user = requireNotNull(auth.currentUser) {
            "No authenticated user found."
        }

        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        user.reauthenticate(credential).awaitResult()
    }

    override suspend fun verifyCurrentUserIsStillValid() {
        val user = auth.currentUser ?: return

        user.reload().awaitResult()

        val refreshedUser = auth.currentUser ?: return
        refreshedUser.getIdToken(true).awaitResult()
    }

    override suspend fun deleteCurrentUser() {
        val user = requireNotNull(auth.currentUser) {
            "No authenticated user to delete."
        }

        user.delete().awaitResult()
    }

    override fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.googleProviderUid(): String {
        return providerData
            .firstOrNull { it.providerId == GoogleAuthProvider.PROVIDER_ID }
            ?.uid
            .orEmpty()
    }
}