package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AltosPlaceholderCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.ThemeMode

@Composable
fun ProfileScreen(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Perfil",
            body = "En este primer módulo dejamos lista la persistencia del tema con DataStore para reemplazar AppPreferences del proyecto Swift.",
        )

        Text(
            text = "Tema",
            style = MaterialTheme.typography.titleMedium,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentThemeMode == mode,
                    onClick = { onThemeModeSelected(mode) },
                    label = { Text(mode.name) },
                )
            }
        }
    }
}
