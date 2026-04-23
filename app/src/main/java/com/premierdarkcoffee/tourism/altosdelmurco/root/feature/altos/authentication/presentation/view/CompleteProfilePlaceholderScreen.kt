package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AltosPlaceholderCard

@Composable
fun CompleteProfilePlaceholderScreen(
    userId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Completar perfil",
            style = MaterialTheme.typography.headlineMedium,
        )
        AltosPlaceholderCard(
            title = "Perfil pendiente",
            body = "El usuario $userId necesita completar el perfil. " +
                    "La persistencia real del perfil se implementará en el Módulo 2.",
        )
    }
}
