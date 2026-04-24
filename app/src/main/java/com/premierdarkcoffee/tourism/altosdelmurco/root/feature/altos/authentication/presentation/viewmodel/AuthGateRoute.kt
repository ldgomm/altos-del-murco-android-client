package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.verifySessionNow()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(sessionState.value) {
        Log.d("AltosAuthGate", "AuthGateRoute -> sessionState=${sessionState.value}")
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