package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.ForkRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureBookingsDateGroup
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureBookingsUiState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureBookingsViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureReservationSortOrder
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureReservationStatusFilter
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.AdventureReservationTimelineFilter
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureBookingsScreen(
    sessionState: SessionState.Authenticated,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdventureBookingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    var bookingToCancel by remember { mutableStateOf<AdventureBooking?>(null) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        viewModel.onAppear(sessionState.profile)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.onDisappear() }
    }

    state.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = {
                TextButton(onClick = viewModel::dismissMessage) {
                    Text("OK")
                }
            },
            title = { Text("Mensaje") },
            text = { Text(message) },
        )
    }

    state.successMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = {
                TextButton(onClick = viewModel::dismissMessage) {
                    Text("OK")
                }
            },
            title = { Text("Listo") },
            text = { Text(message) },
        )
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelBooking(booking)
                        bookingToCancel = null
                    },
                ) {
                    Text("Cancelar reserva")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("Volver")
                }
            },
            title = { Text("¿Cancelar reserva?") },
            text = {
                Text(
                    "La reserva quedará cancelada y se liberarán los premios Murco Loyalty reservados para esta reserva.",
                )
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reservas de aventura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                AdventureBookingsHero(state = state)
            }

            item {
                AdventureBookingsControlsCard(
                    state = state,
                    onTimelineSelected = viewModel::setTimelineFilter,
                    onStatusSelected = viewModel::setStatusFilter,
                    onSortSelected = viewModel::setSortOrder,
                )
            }

            if (!state.errorMessage.isNullOrBlank()) {
                item {
                    InlineMessageCard(
                        title = "No se pudieron cargar tus reservas",
                        body = state.errorMessage.orEmpty(),
                        icon = Icons.Rounded.Warning,
                    )
                }
            }

            if (!state.isLoading && state.displayedBookings.isEmpty()) {
                item {
                    EmptyAdventureBookingsCard(state = state)
                }
            }

            state.groupedBookings.forEach { group ->
                item(key = "header-${group.id}") {
                    DateGroupHeader(group = group)
                }

                items(
                    count = group.bookings.size,
                    key = { index -> group.bookings[index].id },
                ) { index ->
                    val booking = group.bookings[index]

                    AdventureBookingCard(
                        booking = booking,
                        isCancelling = state.cancellingBookingId == booking.id,
                        onCancel = { bookingToCancel = booking },
                    )
                }
            }
        }
    }
}

@Composable
private fun AdventureBookingsHero(
    state: AdventureBookingsUiState,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
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
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    IconBubble(
                        icon = Icons.Rounded.CalendarMonth,
                        strong = true,
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Tu historial completo",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )

                        Text(
                            text = "Actuales, futuras y pasadas; filtradas por estado y ordenadas por fecha.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("Total", state.totalCount.toString())
                    MetricPill("Actuales", state.currentCount.toString())
                    MetricPill("Futuras", state.futureCount.toString())
                    MetricPill("Pasadas", state.pastCount.toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdventureBookingsControlsCard(
    state: AdventureBookingsUiState,
    onTimelineSelected: (AdventureReservationTimelineFilter) -> Unit,
    onStatusSelected: (AdventureReservationStatusFilter) -> Unit,
    onSortSelected: (AdventureReservationSortOrder) -> Unit,
) {
    var sortExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionTitle(
                icon = Icons.Rounded.FilterList,
                title = "Herramientas",
                subtitle = "Filtra por tiempo, estado y orden de fecha.",
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdventureReservationTimelineFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedTimelineFilter == filter,
                        onClick = { onTimelineSelected(filter) },
                        label = { Text(filter.title) },
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdventureReservationStatusFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedStatusFilter == filter,
                        onClick = { onStatusSelected(filter) },
                        label = { Text(filter.title) },
                    )
                }
            }

            Box {
                OutlinedButton(
                    onClick = { sortExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Sort, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.sortOrder.title,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false },
                ) {
                    AdventureReservationSortOrder.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.title) },
                            onClick = {
                                sortExpanded = false
                                onSortSelected(option)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateGroupHeader(
    group: AdventureBookingsDateGroup,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "${group.bookings.size} reserva(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SuggestionChip(
            onClick = {},
            label = { Text(AdventureDateHelper.shortDateText(group.date)) },
            icon = {
                Icon(
                    Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
private fun AdventureBookingCard(
    booking: AdventureBooking,
    isCancelling: Boolean,
    onCancel: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                IconBubble(
                    icon = bookingIcon(booking),
                    strong = false,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = booking.visitTypeTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )

                    Text(
                        text = booking.eventDisplayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${AdventureDateHelper.timeText(booking.startAt)} - ${
                            AdventureDateHelper.timeText(
                                booking.endAt
                            )
                        }",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                StatusBadge(status = booking.status)
            }

            Divider()

            if (booking.items.isNotEmpty()) {
                BookingSubsection(
                    title = "Actividades",
                    icon = Icons.Rounded.Explore,
                ) {
                    booking.items.forEach { item ->
                        Text(
                            text = "• ${item.title}: ${item.summaryText}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            booking.foodReservation?.takeIf { !it.isEmpty }?.let { food ->
                BookingFoodSection(food = food)
            }

            if (booking.appliedRewards.isNotEmpty()) {
                BookingSubsection(
                    title = "Premios aplicados",
                    icon = Icons.Rounded.CheckCircle,
                ) {
                    booking.appliedRewards.forEach { reward ->
                        Text(
                            text = "• ${reward.title}: -${reward.amount.priceText()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            booking.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                InlineNote(text = notes)
            }

            PriceSummary(booking = booking)

            AnimatedVisibility(visible = booking.canBeCancelled) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !isCancelling,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Cancel, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isCancelling) "Cancelando..." else "Cancelar reserva")
                }
            }

            if (booking.status == AdventureBookingStatus.COMPLETED) {
                CompletionInfo(text = "Reserva completada")
            }

            if (booking.status == AdventureBookingStatus.CANCELED) {
                CompletionInfo(text = "Reserva cancelada")
            }
        }
    }
}

@Composable
private fun BookingFoodSection(
    food: ReservationFoodDraft,
) {
    BookingSubsection(
        title = "Comida",
        icon = Icons.Rounded.LocalDining,
    ) {
        food.items.forEach { item ->
            Text(
                text = "• ${item.quantity}x ${item.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "Servicio: ${food.servingMoment.title}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )

        food.notes?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PriceSummary(
    booking: AdventureBooking,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryRow("Aventura", booking.adventureSubtotal.priceText())
        SummaryRow("Comida", booking.foodSubtotal.priceText())

        if (booking.discountAmount > 0.0) {
            SummaryRow("Descuento aventura", "-${booking.discountAmount.priceText()}")
        }

        if (booking.loyaltyDiscountAmount > 0.0) {
            SummaryRow("Murco Loyalty", "-${booking.loyaltyDiscountAmount.priceText()}")
        }

        Divider()

        SummaryRow(
            title = "Total",
            value = booking.totalAmount.priceText(),
            bold = true,
        )
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    bold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BookingSubsection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

@Composable
private fun EmptyAdventureBookingsCard(
    state: AdventureBookingsUiState,
) {
    InlineMessageCard(
        title = "No hay reservas para mostrar",
        body = when {
            state.totalCount == 0 -> "Cuando crees una reserva de aventura, comida o evento aparecerá aquí."
            else -> "Cambia los filtros para ver otras reservas."
        },
        icon = Icons.Rounded.Event,
    )
}

@Composable
private fun InlineMessageCard(
    title: String,
    body: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IconBubble(icon = icon)

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: AdventureBookingStatus,
) {
    val container = when (status) {
        AdventureBookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        AdventureBookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        AdventureBookingStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
        AdventureBookingStatus.CANCELED -> MaterialTheme.colorScheme.errorContainer
    }

    val content = when (status) {
        AdventureBookingStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        AdventureBookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        AdventureBookingStatus.COMPLETED -> MaterialTheme.colorScheme.onSecondaryContainer
        AdventureBookingStatus.CANCELED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Text(
        text = status.title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = content,
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBubble(icon = icon)

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MetricPill(
    title: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.13f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
        )
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    strong: Boolean = false,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (strong) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                },
            )
            .padding(11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (strong) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            },
        )
    }
}

@Composable
private fun InlineNote(
    text: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Rounded.ForkRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompletionInfo(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private val AdventureBooking.canBeCancelled: Boolean
    get() = status == AdventureBookingStatus.PENDING ||
            status == AdventureBookingStatus.CONFIRMED

private fun bookingIcon(booking: AdventureBooking): ImageVector {
    return when {
        booking.hasActivities -> booking.items.firstOrNull()?.activity?.bookingIcon()
        booking.hasFoodReservation -> Icons.Rounded.LocalDining
        else -> Icons.Rounded.Event
    } ?: Icons.Rounded.Event
}

private fun AdventureActivityType.bookingIcon(): ImageVector {
    return when (this) {
        AdventureActivityType.OFF_ROAD -> Icons.Rounded.Explore
        AdventureActivityType.PAINTBALL -> Icons.Rounded.Timeline
        AdventureActivityType.GO_KARTS -> Icons.Rounded.Event
        AdventureActivityType.SHOOTING_RANGE -> Icons.Rounded.AccessTime
        AdventureActivityType.CAMPING -> Icons.Rounded.CalendarMonth
        AdventureActivityType.EXTREME_SLIDE -> Icons.Rounded.Explore
    }
}