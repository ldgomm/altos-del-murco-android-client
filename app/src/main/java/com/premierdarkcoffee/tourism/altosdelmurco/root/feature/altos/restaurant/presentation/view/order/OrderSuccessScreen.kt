package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart.SummaryLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreenBackground
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSecondaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle

@Composable
fun OrderSuccessScreen(
    order: Order,
    onBackToRestaurant: () -> Unit,
    onOpenOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = AppSectionTheme.Restaurant
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        BrandScreenBackground(
            theme = theme,
            modifier = Modifier.matchParentSize(),
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.background.copy(alpha = 0.96f))
                        .navigationBarsPadding()
                        .padding(
                            start = 16.dp,
                            top = 12.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    BrandPrimaryButton(
                        theme = theme,
                        onClick = onOpenOrders,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RestaurantButtonContent(
                            icon = Icons.Rounded.ReceiptLong,
                            text = "Ver mis pedidos",
                            tint = palette.onPrimary,
                        )
                    }

                    BrandSecondaryButton(
                        theme = theme,
                        onClick = onBackToRestaurant,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RestaurantButtonContent(
                            icon = Icons.Rounded.Restaurant,
                            text = "Volver al restaurante",
                            tint = palette.textPrimary,
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .appCardStyle(
                            theme = theme,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape)
                            .background(palette.heroGradient)
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.18f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = palette.onPrimary,
                            modifier = Modifier.size(54.dp),
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Pedido enviado",
                            style = MaterialTheme.typography.headlineSmall,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            text = "Tu pedido fue registrado y aparecerá en tiempo real para el equipo de Altos del Murco.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = palette.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppTheme.Radius.large))
                            .background(palette.cardGradient)
                            .border(
                                width = 1.dp,
                                color = palette.stroke,
                                shape = RoundedCornerShape(AppTheme.Radius.large),
                            )
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SummaryLine("Código", "#${order.id.takeLast(6).uppercase()}")
                        SummaryLine("Mesa", order.tableNumber)
                        SummaryLine("Productos", order.totalItems.toString())
                        SummaryLine(
                            label = "Total",
                            value = order.totalAmount.priceLabel(),
                            labelColor = palette.textSecondary,
                            valueColor = palette.textPrimary,
                        )
                    }

                    if (order.appliedRewards.isNotEmpty()) {
                        Text(
                            text = "Beneficios reservados: ${order.appliedRewards.size}",
                            color = palette.accent,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(palette.chipGradient)
                                .border(
                                    width = 1.dp,
                                    color = palette.stroke,
                                    shape = CircleShape,
                                )
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestaurantButtonContent(
    icon: ImageVector,
    text: String,
    tint: Color,
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp),
    )

    Spacer(modifier = Modifier.size(8.dp))

    Text(
        text = text,
        color = tint,
        fontWeight = FontWeight.SemiBold,
    )
}