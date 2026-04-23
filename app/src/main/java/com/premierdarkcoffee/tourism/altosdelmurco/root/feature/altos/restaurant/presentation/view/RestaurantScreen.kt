package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AltosPlaceholderCard

@Composable
fun RestaurantScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AltosPlaceholderCard(
            title = "Restaurante",
            body = "El catálogo, el detalle de platos, el carrito y el checkout se migrarán en los siguientes módulos. Este entry point ya está conectado al tab principal.",
        )

    }
}
