package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ResolveSessionUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionDestination
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authenticationRepository: AuthenticationRepositoriable,
    private val resolveSessionUseCase: ResolveSessionUseCase,
) : SessionRepositoriable {

    private val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    companion object {
        private const val TAG = "AltosSession"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun sessionState(): Flow<SessionState> {
        val authChanges = authUserFlow().map { Unit }
        val manualRefreshes = refreshRequests.onStart { emit(Unit) }

        return merge(authChanges, manualRefreshes)
            .mapLatest {
                Log.d(
                    TAG,
                    "sessionState() tick -> auth.currentUser.uid=${auth.currentUser?.uid}, email=${auth.currentUser?.email}"
                )

                resolveLatestSessionState()
            }
            .distinctUntilChanged()
    }

    override suspend fun refresh() {
        Log.d(
            TAG,
            "refresh() requested -> current uid before verify=${auth.currentUser?.uid}"
        )

        val result = runCatching {
            authenticationRepository.verifyCurrentUserIsStillValid()
        }

        result.onSuccess {
            Log.d(
                TAG,
                "refresh() verify success -> current uid after verify=${auth.currentUser?.uid}"
            )
        }

        result.onFailure { error ->
            if (error.isFirebaseSessionInvalidOrDisabled()) {
                forceSignOut(
                    reason = "refresh() detected disabled/deleted/expired Firebase user. Closing session.",
                    error = error,
                )
            } else {
                Log.w(
                    TAG,
                    "refresh() verify failed but it is not a terminal auth error. Keeping local session.",
                    error,
                )
            }
        }

        refreshRequests.emit(Unit)
    }

    private fun authUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Log.d(
                TAG,
                "AuthStateListener -> uid=${firebaseAuth.currentUser?.uid}, email=${firebaseAuth.currentUser?.email}"
            )

            trySend(firebaseAuth.currentUser).isSuccess
        }

        auth.addAuthStateListener(listener)

        Log.d(
            TAG,
            "authUserFlow initial emit -> uid=${auth.currentUser?.uid}, email=${auth.currentUser?.email}"
        )

        trySend(auth.currentUser).isSuccess

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }.distinctUntilChanged()

    private suspend fun resolveLatestSessionState(): SessionState {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            Log.d(TAG, "resolveLatestSessionState -> Unauthenticated because firebaseUser=null")
            return SessionState.Unauthenticated
        }

        val verifiedFirebaseUser = verifyOrCloseSession(firebaseUser)
            ?: return SessionState.Unauthenticated

        Log.d(
            TAG,
            "resolveLatestSessionState -> verified uid=${verifiedFirebaseUser.uid}, email=${verifiedFirebaseUser.email}"
        )

        val currentUser = authenticationRepository.currentUser()
            ?: AuthenticatedUser(
                uid = verifiedFirebaseUser.uid,
                email = verifiedFirebaseUser.email.orEmpty(),
                displayName = verifiedFirebaseUser.displayName.orEmpty(),
                appleUserIdentifier = "",
            )

        val destination = runCatching {
            resolveSessionUseCase.execute(currentUser)
        }.getOrElse { error ->
            if (error.isFirebaseSessionInvalidOrDisabled()) {
                forceSignOut(
                    reason = "resolveLatestSessionState detected invalid Firebase session while resolving profile.",
                    error = error,
                )

                return SessionState.Unauthenticated
            }

            throw error
        }

        return when (destination) {
            SessionDestination.SignedOut -> {
                Log.d(TAG, "resolveLatestSessionState -> destination=SignedOut")
                SessionState.Unauthenticated
            }

            is SessionDestination.NeedsProfile -> {
                val profile = destination.profile

                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=NeedsProfile, profileExists=${profile != null}, profileIsComplete=${profile?.isComplete}"
                )

                SessionState.NeedsProfileCompletion(
                    user = destination.user,
                    existingProfile = destination.profile,
                )
            }

            is SessionDestination.Authenticated -> {
                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=Authenticated, profileId=${destination.profile.id}, profileIsComplete=${destination.profile.isComplete}"
                )

                SessionState.Authenticated(
                    profile = destination.profile,
                )
            }
        }
    }

    private suspend fun verifyOrCloseSession(
        firebaseUser: FirebaseUser,
    ): FirebaseUser? {
        val result = runCatching {
            authenticationRepository.verifyCurrentUserIsStillValid()
        }

        if (result.isSuccess) {
            return auth.currentUser
        }

        val error = result.exceptionOrNull()

        if (error != null && error.isFirebaseSessionInvalidOrDisabled()) {
            forceSignOut(
                reason = "verifyOrCloseSession detected disabled/deleted/expired Firebase user uid=${firebaseUser.uid}.",
                error = error,
            )

            return null
        }

        Log.w(
            TAG,
            "verifyOrCloseSession failed with non-terminal error. Keeping session to avoid logging out due to network/transient issue.",
            error,
        )

        return auth.currentUser
    }

    private fun forceSignOut(
        reason: String,
        error: Throwable? = null,
    ) {
        Log.w(TAG, reason, error)

        runCatching {
            authenticationRepository.signOut()
        }.onFailure { signOutError ->
            Log.e(
                TAG,
                "forceSignOut -> signOut failed, but session will still resolve as unauthenticated soon.",
                signOutError
            )
        }
    }
}