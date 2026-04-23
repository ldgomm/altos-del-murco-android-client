package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view.AuthenticationScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view.CompleteProfileScreen

@Composable
fun AuthGateRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthGateViewModel = hiltViewModel(),
    authenticatedContent: @Composable (SessionState.Authenticated) -> Unit,
) {
    val sessionState = viewModel.sessionState.collectAsStateWithLifecycle()

    LaunchedEffect(sessionState) {
        Log.d("AltosAuthGate", "AuthGateRoute -> sessionState=$sessionState")
    }

    when (val state = sessionState.value) {
        SessionState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        SessionState.Unauthenticated -> {
            AuthenticationScreen(modifier = modifier)
        }

        is SessionState.Authenticated -> {
            authenticatedContent(state)
        }

        is SessionState.NeedsProfileCompletion -> {
            val existingProfile = state.existingProfile

            if (existingProfile?.isComplete == true) {
                LaunchedEffect(existingProfile.id, existingProfile.updatedAt.time) {
                    viewModel.refreshSession()
                }

                authenticatedContent(
                    SessionState.Authenticated(profile = existingProfile)
                )
            } else {
                CompleteProfileScreen(
                    state = state,
                    modifier = modifier,
                    onProfileCompleted = {
                        viewModel.refreshSession()
                    },
                )
            }
        }
    }
}