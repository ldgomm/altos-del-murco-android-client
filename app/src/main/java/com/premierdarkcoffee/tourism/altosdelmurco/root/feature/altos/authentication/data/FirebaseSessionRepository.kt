package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ResolveSessionUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionDestination
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Singleton
class FirebaseSessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authenticationRepository: AuthenticationRepositoriable,
    private val resolveSessionUseCase: ResolveSessionUseCase,
) : SessionRepository {

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
            "refresh() requested -> current uid before reload=${auth.currentUser?.uid}"
        )

        runCatching {
            auth.currentUser?.reload()?.awaitResult()
        }.onSuccess {
            Log.d(
                TAG,
                "refresh() reload success -> current uid after reload=${auth.currentUser?.uid}"
            )
        }.onFailure { error ->
            Log.e(TAG, "refresh() reload failure", error)
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
            Log.d(TAG, "resolveLatestSessionState -> Unauthenticated (firebaseUser=null)")
            return SessionState.Unauthenticated
        }

        Log.d(
            TAG,
            "resolveLatestSessionState -> firebaseUser uid=${firebaseUser.uid}, email=${firebaseUser.email}, displayName=${firebaseUser.displayName}"
        )

        val currentUser = authenticationRepository.currentUser()
            ?: AuthenticatedUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email.orEmpty(),
                displayName = firebaseUser.displayName.orEmpty(),
                appleUserIdentifier = "",
            )

        Log.d(
            TAG,
            "resolveLatestSessionState -> currentUser uid=${currentUser.uid}, email=${currentUser.email}, displayName=${currentUser.displayName}, appleUserIdentifier=${currentUser.appleUserIdentifier}"
        )

        val destination = resolveSessionUseCase.execute(currentUser)

        when (destination) {
            SessionDestination.SignedOut -> {
                Log.d(TAG, "resolveLatestSessionState -> destination=SignedOut")
                return SessionState.Unauthenticated
            }

            is SessionDestination.NeedsProfile -> {
                val profile = destination.profile
                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=NeedsProfile, profileExists=${profile != null}, profileIsComplete=${profile?.isComplete}, profileId=${profile?.id}, profileEmail=${profile?.email}"
                )
                return SessionState.NeedsProfileCompletion(
                    user = destination.user,
                    existingProfile = destination.profile,
                )
            }

            is SessionDestination.Authenticated -> {
                Log.d(
                    TAG,
                    "resolveLatestSessionState -> destination=Authenticated, profileId=${destination.profile.id}, profileEmail=${destination.profile.email}, profileIsComplete=${destination.profile.isComplete}"
                )
                return SessionState.Authenticated(
                    profile = destination.profile,
                )
            }
        }
    }
}