package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
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
        val appleProviderUid = user.providerData.firstOrNull { it.providerId == "apple.com" }?.uid.orEmpty()
        return AuthenticatedUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty(),
            appleUserIdentifier = appleProviderUid,
        )
    }

    override suspend fun signInWithApple(
        idToken: String,
        rawNonce: String,
        fullName: String?,
        email: String?,
        appleUserIdentifier: String,
    ): AuthenticatedUser {
        val credential = OAuthProvider
            .newCredentialBuilder("apple.com")
            .setIdTokenWithRawNonce(idToken, rawNonce)
            .build()

        val authResult = auth.signInWithCredential(credential).awaitResult()
        val firebaseUser = requireNotNull(authResult.user) { "Firebase auth returned a null user after Apple sign in." }

        val finalDisplayName = fullName?.trim().takeUnless { it.isNullOrEmpty() }
            ?: firebaseUser.displayName.orEmpty()
        val finalEmail = email?.trim().takeUnless { it.isNullOrEmpty() }
            ?: firebaseUser.email.orEmpty()
        val providerUid = firebaseUser.providerData.firstOrNull { it.providerId == "apple.com" }?.uid
        val finalAppleIdentifier = providerUid?.takeIf { it.isNotBlank() } ?: appleUserIdentifier

        return AuthenticatedUser(
            uid = firebaseUser.uid,
            email = finalEmail,
            displayName = finalDisplayName,
            appleUserIdentifier = finalAppleIdentifier,
        )
    }

    override suspend fun reauthenticateCurrentUser(
        idToken: String,
        rawNonce: String,
    ) {
        val user = requireNotNull(auth.currentUser) { "No authenticated user found." }
        val credential = OAuthProvider
            .newCredentialBuilder("apple.com")
            .setIdTokenWithRawNonce(idToken, rawNonce)
            .build()
        user.reauthenticate(credential).awaitResult()
    }

    override suspend fun deleteCurrentUser() {
        val user = requireNotNull(auth.currentUser) { "No authenticated user to delete." }
        user.delete().awaitResult()
    }

    override fun signOut() {
        auth.signOut()
    }
}
