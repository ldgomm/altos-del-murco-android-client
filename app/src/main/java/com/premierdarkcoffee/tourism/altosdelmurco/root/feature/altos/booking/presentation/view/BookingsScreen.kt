package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureBookingsViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumEmptyState
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumMetricTile
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumScreenHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumSectionHeader
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
        override val subtitle: String = "${booking.eventDisplayTitle} • ${booking.guestCount} invitado(s)"
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

    val allReservations = remember(ordersState.orders, adventureState.allBookings) {
        ordersState.orders.map(UnifiedReservation::RestaurantOrder) +
            adventureState.allBookings.map(UnifiedReservation::ExperienceBooking)
    }
    val now = Date()
    val filtered = allReservations
        .filter { item ->
            when (selectedFilter) {
                ReservationFilter.UPCOMING -> !item.isCancelled && !item.isCompleted && !item.date.before(now)
                ReservationFilter.PAST -> !item.isCancelled && (item.isCompleted || item.date.before(now))
                ReservationFilter.CANCELLED -> item.isCancelled
            }
        }
        .sortedWith(
            if (selectedFilter == ReservationFilter.PAST || selectedFilter == ReservationFilter.CANCELLED) {
                compareByDescending<UnifiedReservation> { it.date.time }
            } else {
                compareBy<UnifiedReservation> { it.date.time }
            }
        )

    val grouped = filtered.groupBy { AdventureDateHelper.dayKey(AdventureDateHelper.startOfDay(it.date)) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            PremiumScreenHeader(
                title = "Reservas",
                subtitle = "Tu agenda completa: pedidos del restaurante y experiencias en un solo lugar.",
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PremiumMetricTile("Próximas", allReservations.count { !it.isCancelled && !it.isCompleted && !it.date.before(now) }.toString(), Icons.Rounded.Schedule, Modifier.weight(1f))
                PremiumMetricTile("Pasadas", allReservations.count { !it.isCancelled && (it.isCompleted || it.date.before(now)) }.toString(), Icons.Rounded.CheckCircle, Modifier.weight(1f))
                PremiumMetricTile("Canceladas", allReservations.count { it.isCancelled }.toString(), Icons.Rounded.Close, Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReservationFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.title) },
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                PremiumEmptyState(
                    title = "No hay reservas en ${selectedFilter.title.lowercase()}",
                    body = "Cuando hagas pedidos o reserves experiencias, aparecerán aquí agrupadas por fecha.",
                    icon = Icons.Rounded.CalendarMonth,
                )
            }
        } else {
            grouped.forEach { (dayKey, reservations) ->
                item(key = dayKey) {
                    PremiumSectionHeader(
                        title = reservations.firstOrNull()?.date?.formatBookingsDate().orEmpty(),
                        subtitle = "${reservations.size} movimiento(s)",
                        icon = Icons.Rounded.CalendarMonth,
                    )
                }
                items(reservations, key = { it.id }) { reservation ->
                    UnifiedReservationCard(reservation)
                }
            }
        }
    }
}

@Composable
private fun UnifiedReservationCard(reservation: UnifiedReservation) {
    PremiumCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            PremiumIconBubble(
                icon = when (reservation) {
                    is UnifiedReservation.RestaurantOrder -> Icons.Rounded.Restaurant
                    is UnifiedReservation.ExperienceBooking -> Icons.Rounded.Explore
                },
                selected = reservation !is UnifiedReservation.RestaurantOrder,
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(reservation.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text(reservation.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(onClick = {}, label = { Text(reservation.statusText) })
                    Text(AdventureDateHelper.timeText(reservation.date), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(reservation.total.premiumMoney(), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun Date.formatBookingsDate(): String {
    val formatter = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "EC"))
    return formatter.format(this).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "EC")) else it.toString() }
}
