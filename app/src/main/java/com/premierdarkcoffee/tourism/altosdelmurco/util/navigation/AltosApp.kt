package com.premierdarkcoffee.tourism.altosdelmurco.util.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.viewmodel.AuthGateRoute
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AltosTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppThemeViewModel

@Composable
fun AltosApp(
    themeViewModel: AppThemeViewModel = hiltViewModel(),
) {
    val themeState by themeViewModel.uiState.collectAsStateWithLifecycle()

    AltosTheme(themeMode = themeState.themeMode) {
        AuthGateRoute { authenticatedState: SessionState.Authenticated ->
            AltosMainShell(
                sessionState = authenticatedState,
                currentThemeMode = themeState.themeMode,
                onThemeModeSelected = themeViewModel::setThemeMode,
            )
        }
    }
}
