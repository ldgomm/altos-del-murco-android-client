package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel.AdventureBookingsViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import java.util.Calendar

@Composable
fun AdventureBookingsScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    viewModel: AdventureBookingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var bookingToCancel by remember { mutableStateOf<AdventureBooking?>(null) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        viewModel.onAppear(sessionState.profile)
    }

    val message = state.errorMessage ?: state.successMessage
    if (message != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } },
            title = { Text("Mensaje") },
            text = { Text(message) },
        )
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelBooking(booking)
                    bookingToCancel = null
                }) { Text("Cancelar reserva") }
            },
            dismissButton = { TextButton(onClick = { bookingToCancel = null }) { Text("Volver") } },
            title = { Text("¿Cancelar reserva?") },
            text = { Text("Se liberarán los premios reservados y la reserva quedará como cancelada.") },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Mis reservas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Aventura, comida y eventos por día.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = viewModel::refresh) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refrescar")
            }
        }

        AdventureCard {
            AdventureSectionTitle("Fecha", "Mira las reservas de un día específico.")
            OutlinedButton(
                onClick = {
                    val calendar = Calendar.getInstance().apply { time = state.selectedDate }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val picked = Calendar.getInstance().apply {
                                set(year, month, day, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            viewModel.onDateSelected(picked.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text(AdventureDateHelper.shortDateText(state.selectedDate))
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.bookings.isEmpty()) {
            AdventureEmptyState(
                title = "Sin reservas para este día",
                body = "Cuando crees una reserva de aventura o comida aparecerá aquí.",
                icon = Icons.Rounded.Event,
            )
        } else {
            state.bookings.forEach { booking ->
                AdventureBookingCard(
                    booking = booking,
                    onCancel = { bookingToCancel = booking },
                )
            }
        }
    }
}

@Composable
private fun AdventureBookingCard(
    booking: AdventureBooking,
    onCancel: () -> Unit,
) {
    AdventureCard {
        Row(
            verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdventureIconBubble(icon = Icons.Rounded.Event)
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    booking.visitTypeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${AdventureDateHelper.timeText(booking.startAt)} - ${
                        AdventureDateHelper.timeText(
                            booking.endAt
                        )
                    }",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(booking.eventDisplayTitle, style = MaterialTheme.typography.bodyMedium)
            }
            AssistChip(onClick = {}, label = { Text(booking.status.title) })
        }

        Divider()

        if (booking.items.isNotEmpty()) {
            Text("Actividades", fontWeight = FontWeight.Bold)
            booking.items.forEach { item ->
                Text(
                    "• ${item.title}: ${item.summaryText}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        booking.foodReservation?.takeIf { !it.isEmpty }?.let { food ->
            Text("Comida", fontWeight = FontWeight.Bold)
            food.items.forEach { item ->
                Text(
                    "• ${item.quantity}x ${item.name}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Servicio: ${food.servingMoment.title}", style = MaterialTheme.typography.bodySmall
            )
        }

        if (booking.appliedRewards.isNotEmpty()) {
            Text("Premios aplicados", fontWeight = FontWeight.Bold)
            booking.appliedRewards.forEach { reward ->
                Text(
                    "• ${reward.title}: -${reward.amount.priceText()}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        AdventurePriceRow("Total", booking.totalAmount, bold = true)

        if (booking.status == AdventureBookingStatus.PENDING || booking.status == AdventureBookingStatus.CONFIRMED) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                enabled = booking.status != AdventureBookingStatus.CANCELED
            ) {
                Icon(Icons.Rounded.Cancel, contentDescription = null)
                Text("Cancelar reserva")
            }
        } else if (booking.status == AdventureBookingStatus.COMPLETED) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Reserva completada", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
