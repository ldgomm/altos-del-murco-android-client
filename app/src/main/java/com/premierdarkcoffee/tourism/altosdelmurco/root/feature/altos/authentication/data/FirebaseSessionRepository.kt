package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticatedUser
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ResolveSessionUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionDestination
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Singleton
class FirebaseSessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authenticationRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) : SessionRepository {

    private val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun sessionState(): Flow<SessionState> {
        return authUserFlow()
            .flatMapLatest { firebaseUser ->
                refreshRequests
                    .onStart { emit(Unit) }
                    .flatMapLatest {
                        flow {
                            emit(resolve(firebaseUser))
                        }
                    }
            }
            .distinctUntilChanged()
    }

    override suspend fun refresh() {
        refreshRequests.emit(Unit)
    }

    private fun authUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser).isSuccess
        }

        auth.addAuthStateListener(listener)
        trySend(auth.currentUser).isSuccess

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    private suspend fun resolve(firebaseUser: FirebaseUser?): SessionState {
        if (firebaseUser == null) return SessionState.Unauthenticated

        val currentUser = authenticationRepository.currentUser()
            ?: AuthenticatedUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email.orEmpty(),
                displayName = firebaseUser.displayName.orEmpty(),
                appleUserIdentifier = "",
            )

        return when (val destination = ResolveSessionUseCase(authenticationRepository, clientProfileRepository).execute(currentUser)) {
            SessionDestination.SignedOut -> SessionState.Unauthenticated
            is SessionDestination.NeedsProfile -> SessionState.NeedsProfileCompletion(
                user = destination.user,
                existingProfile = destination.profile,
            )
            is SessionDestination.Authenticated -> SessionState.Authenticated(
                profile = destination.profile,
            )
        }
    }
}
