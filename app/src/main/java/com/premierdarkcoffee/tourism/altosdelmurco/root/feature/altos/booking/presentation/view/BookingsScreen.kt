package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AltosPlaceholderCard

@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Reservas",
            body = "Los pedidos del restaurante y las reservas de aventura terminarán viviendo aquí, con navegación separada pero una experiencia unificada.",
        )

    }
}
