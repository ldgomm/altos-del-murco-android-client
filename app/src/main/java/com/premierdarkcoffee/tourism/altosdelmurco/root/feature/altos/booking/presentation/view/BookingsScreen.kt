package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.viewmodel.AdventureBookingsViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandBadge
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.premiumMoney
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ReservationFilter(val title: String) {
    UPCOMING("Próximas"),
    PAST("Pasadas"),
    CANCELLED("Canceladas"),
}

private sealed interface UnifiedReservation {
    val id: String
    val title: String
    val subtitle: String
    val date: Date
    val total: Double
    val statusText: String
    val isCancelled: Boolean
    val isCompleted: Boolean

    data class RestaurantOrder(val order: Order) : UnifiedReservation {
        override val id: String = "restaurant-${order.id}"
        override val title: String = "Pedido restaurante"
        override val subtitle: String = "${order.totalItems} item(s) • Mesa ${order.tableNumber}"
        override val date: Date = order.createdAt
        override val total: Double = order.totalAmount
        override val statusText: String = order.status.title
        override val isCancelled: Boolean = order.status == OrderStatus.CANCELED
        override val isCompleted: Boolean = order.status == OrderStatus.COMPLETED
    }

    data class ExperienceBooking(val booking: AdventureBooking) : UnifiedReservation {
        override val id: String = "experience-${booking.id}"
        override val title: String = booking.visitTypeTitle
        override val subtitle: String =
            "${booking.eventDisplayTitle} • ${booking.guestCount} invitado(s)"
        override val date: Date = booking.startAt
        override val total: Double = booking.totalAmount
        override val statusText: String = booking.status.title
        override val isCancelled: Boolean = booking.status == AdventureBookingStatus.CANCELED
        override val isCompleted: Boolean = booking.status == AdventureBookingStatus.COMPLETED
    }
}

@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    adventureBookingsViewModel: AdventureBookingsViewModel = hiltViewModel(),
) {
    val theme = AppSectionTheme.Neutral
    val palette = LocalBrandPalette.current

    val ordersState by ordersViewModel.uiState.collectAsStateWithLifecycle()
    val adventureState by adventureBookingsViewModel.uiState.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(ReservationFilter.UPCOMING) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        ordersViewModel.syncProfile(sessionState.profile)
        adventureBookingsViewModel.onAppear(sessionState.profile)
    }

    DisposableEffect(Unit) {
        onDispose { adventureBookingsViewModel.onDisappear() }
    }

    val allReservations = remember(
        ordersState.orders,
        adventureState.allBookings,
    ) {
        ordersState.orders.map(UnifiedReservation::RestaurantOrder) +
                adventureState.allBookings.map(UnifiedReservation::ExperienceBooking)
    }

    val now = Date()

    val upcomingCount = allReservations.count {
        !it.isCancelled && !it.isCompleted && !it.date.before(now)
    }

    val pastCount = allReservations.count {
        !it.isCancelled && (it.isCompleted || it.date.before(now))
    }

    val cancelledCount = allReservations.count {
        it.isCancelled
    }

    val filtered = allReservations
        .filter { item ->
            when (selectedFilter) {
                ReservationFilter.UPCOMING ->
                    !item.isCancelled && !item.isCompleted && !item.date.before(now)

                ReservationFilter.PAST ->
                    !item.isCancelled && (item.isCompleted || item.date.before(now))

                ReservationFilter.CANCELLED ->
                    item.isCancelled
            }
        }
        .sortedWith(
            if (
                selectedFilter == ReservationFilter.PAST ||
                selectedFilter == ReservationFilter.CANCELLED
            ) {
                compareByDescending<UnifiedReservation> { it.date.time }
            } else {
                compareBy<UnifiedReservation> { it.date.time }
            }
        )

    val grouped = filtered.groupBy {
        AdventureDateHelper.dayKey(
            AdventureDateHelper.startOfDay(it.date)
        )
    }

    BrandScreen(
        theme = theme,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 30.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                BrandSectionHeader(
                    theme = theme,
                    title = "Reservas",
                    subtitle = "Tu agenda completa: pedidos del restaurante y experiencias en un solo lugar.",
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NeutralMetricTile(
                        title = "Próximas",
                        value = upcomingCount.toString(),
                        icon = Icons.Rounded.Schedule,
                        modifier = Modifier.weight(1f),
                    )

                    NeutralMetricTile(
                        title = "Pasadas",
                        value = pastCount.toString(),
                        icon = Icons.Rounded.CheckCircle,
                        modifier = Modifier.weight(1f),
                    )

                    NeutralMetricTile(
                        title = "Canceladas",
                        value = cancelledCount.toString(),
                        icon = Icons.Rounded.Close,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ReservationFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter.title,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = palette.card,
                                labelColor = palette.textSecondary,
                                selectedContainerColor = palette.primary,
                                selectedLabelColor = palette.onPrimary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == filter,
                                borderColor = palette.stroke,
                                selectedBorderColor = palette.primary,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp,
                            ),
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    NeutralEmptyState(
                        title = "No hay reservas en ${selectedFilter.title.lowercase()}",
                        body = "Cuando hagas pedidos o reserves experiencias, aparecerán aquí agrupadas por fecha.",
                        icon = Icons.Rounded.CalendarMonth,
                    )
                }
            } else {
                grouped.forEach { (dayKey, reservations) ->
                    item(key = dayKey) {
                        BrandSectionHeader(
                            theme = theme,
                            title = reservations.firstOrNull()
                                ?.date
                                ?.formatBookingsDate()
                                .orEmpty(),
                            subtitle = "${reservations.size} movimiento(s)",
                        )
                    }

                    items(
                        items = reservations,
                        key = { it.id },
                    ) { reservation ->
                        UnifiedReservationCard(
                            reservation = reservation,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UnifiedReservationCard(
    reservation: UnifiedReservation,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Neutral
    val palette = LocalBrandPalette.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = when (reservation) {
                    is UnifiedReservation.RestaurantOrder -> Icons.Rounded.Restaurant
                    is UnifiedReservation.ExperienceBooking -> Icons.Rounded.Explore
                },
                size = 48.dp,
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = reservation.title,
                    color = palette.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = reservation.subtitle,
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrandBadge(
                        theme = theme,
                        title = reservation.statusText,
                        selected = reservation.isCompleted || reservation.isCancelled,
                    )

                    Text(
                        text = AdventureDateHelper.timeText(reservation.date),
                        color = palette.textTertiary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = reservation.total.premiumMoney(),
                    color = palette.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun NeutralMetricTile(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Neutral
    val palette = LocalBrandPalette.current

    Column(
        modifier = modifier.appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BrandIconBubble(
            theme = theme,
            icon = icon,
            size = 38.dp,
            contentDescription = null,
        )

        Text(
            text = value,
            color = palette.textPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )

        Text(
            text = title,
            color = palette.textSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NeutralEmptyState(
    title: String,
    body: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Neutral
    val palette = LocalBrandPalette.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .appCardStyle(
                theme = theme,
                emphasized = true,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandIconBubble(
            theme = theme,
            icon = icon,
            size = 58.dp,
            contentDescription = null,
        )

        Text(
            text = title,
            color = palette.textPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        Text(
            text = body,
            color = palette.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun Date.formatBookingsDate(): String {
    val formatter = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "EC"))

    return formatter.format(this).replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale("es", "EC"))
        } else {
            it.toString()
        }
    }
}