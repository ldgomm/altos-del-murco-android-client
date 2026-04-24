package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order.OrdersScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel

private enum class BookingsMode {
    HOME,
    RESTAURANT_ORDERS,
    ADVENTURE_BOOKINGS,
}

@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
) {
    var mode by rememberSaveable { mutableStateOf(BookingsMode.HOME) }
    val ordersState by ordersViewModel.uiState.collectAsState()

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        ordersViewModel.syncProfile(sessionState.profile)
    }

    when (mode) {
        BookingsMode.HOME -> BookingsHomeContent(
            modifier = modifier,
            restaurantCount = ordersState.orders.size,
            onRestaurantOrders = { mode = BookingsMode.RESTAURANT_ORDERS },
            onAdventureBookings = { mode = BookingsMode.ADVENTURE_BOOKINGS },
        )

        BookingsMode.RESTAURANT_ORDERS -> OrdersScreen(
            state = ordersState,
            onBack = { mode = BookingsMode.HOME },
            onGroupingSelected = ordersViewModel::setGrouping,
            onSortSelected = ordersViewModel::setSortOption,
            onStatusSelected = ordersViewModel::setStatusFilter,
            onDismissError = ordersViewModel::clearError,
            modifier = modifier,
        )

        BookingsMode.ADVENTURE_BOOKINGS -> AdventureBookingsScreen(
            sessionState = sessionState,
            onBack = { mode = BookingsMode.HOME },
            modifier = modifier,
        )
    }
}

@Composable
private fun BookingsHomeContent(
    restaurantCount: Int,
    onRestaurantOrders: () -> Unit,
    onAdventureBookings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            BookingsHeroCard()
        }

        item {
            BookingEntryCard(
                badge = "Restaurante",
                title = "Pedidos del restaurante",
                subtitle = "Revisa tus pedidos actuales, anteriores, estados, totales y productos.",
                icon = Icons.Rounded.LocalDining,
                metric = "$restaurantCount pedido(s)",
                onClick = onRestaurantOrders,
            )
        }

        item {
            BookingEntryCard(
                badge = "Aventura",
                title = "Reservas de aventura",
                subtitle = "Mira combos, actividades, comida, eventos, premios aplicados y reservas nocturnas.",
                icon = Icons.Rounded.CalendarMonth,
                metric = "Actuales y futuras",
                onClick = onAdventureBookings,
            )
        }
    }
}

@Composable
private fun BookingsHeroCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .padding(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    HeroIconBubble(Icons.Rounded.ReceiptLong)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Gestiona tus reservas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )

                        Text(
                            text = "Pedidos del restaurante y reservas de aventura en un solo lugar, separado por experiencia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                        )
                    }
                }

                SuggestionChip(
                    onClick = {},
                    label = { Text("Altos del Murco") },
                    icon = {
                        Icon(
                            Icons.Rounded.Explore,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun BookingEntryCard(
    badge: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    metric: String,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionIconBubble(icon = icon)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = metric,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionIconBubble(
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun HeroIconBubble(
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f))
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}