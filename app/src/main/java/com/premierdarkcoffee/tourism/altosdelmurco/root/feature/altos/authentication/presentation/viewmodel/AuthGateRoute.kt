package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        )

        is SessionState.Authenticated -> authenticatedContent(state)
    }
}
