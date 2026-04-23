package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()
    var locallyCompletedState by remember { mutableStateOf<SessionState.Authenticated?>(null) }

    LaunchedEffect(sessionState) {
        when (sessionState) {
            SessionState.Unauthenticated,
            SessionState.Loading,
                -> locallyCompletedState = null
            is SessionState.Authenticated -> locallyCompletedState = null
            is SessionState.NeedsProfileCompletion -> Unit
        }
    }

    locallyCompletedState?.let { authenticated ->
        authenticatedContent(authenticated)
        return
    }

    when (val state = sessionState) {
        SessionState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        SessionState.Unauthenticated -> AuthenticationScreen(modifier = modifier)

        is SessionState.NeedsProfileCompletion -> CompleteProfileScreen(
            state = state,
            modifier = modifier,
            onProfileCompleted = { profile ->
                locallyCompletedState = SessionState.Authenticated(profile = profile)
                viewModel.refreshSession()
            },
        )

        is SessionState.Authenticated -> authenticatedContent(state)
    }
}
