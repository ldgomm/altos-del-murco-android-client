package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingBlock
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureResourceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.viewmodel.AdventureBookingsViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderServiceMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalAppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class BookingsTimelineScope(
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val icon: ImageVector,
) {
    TODAY(
        title = "Hoy",
        shortTitle = "Hoy",
        subtitle = "Pedidos y reservas que deben atenderse hoy.",
        icon = Icons.Rounded.Schedule,
    ),
    UPCOMING(
        title = "Próximas",
        shortTitle = "Próximas",
        subtitle = "Reservas futuras de restaurante, aventura o eventos.",
        icon = Icons.Rounded.CalendarMonth,
    ),
    HISTORY(
        title = "Historial",
        shortTitle = "Historial",
        subtitle = "Reservas pasadas, completadas o canceladas.",
        icon = Icons.Rounded.History,
    ),
    ALL(
        title = "Todas",
        shortTitle = "Todas",
        subtitle = "Agenda completa de pedidos y experiencias.",
        icon = Icons.Rounded.ReceiptLong,
    ),
}

private enum class BookingsGroupingOption(val title: String) {
    BY_DATE("Fecha"),
    BY_STATUS("Estado"),
    BY_TYPE("Tipo"),
}

private enum class BookingsSortOption(val title: String) {
    SERVICE_TIME_ASCENDING("Más cercana"),
    SERVICE_TIME_DESCENDING("Más lejana"),
    NEWEST_CREATED("Más reciente"),
    HIGHEST_TOTAL("Mayor total"),
}

private enum class UnifiedReservationStatusFilter(val title: String) {
    ALL("Todo"),
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    PREPARING("Preparando"),
    COMPLETED("Completada"),
    CANCELED("Cancelada");

    fun matches(reservation: UnifiedReservation): Boolean {
        return when (this) {
            ALL -> true
            PENDING -> reservation.normalizedStatus == UnifiedReservationStatus.PENDING
            CONFIRMED -> reservation.normalizedStatus == UnifiedReservationStatus.CONFIRMED
            PREPARING -> reservation.normalizedStatus == UnifiedReservationStatus.PREPARING
            COMPLETED -> reservation.normalizedStatus == UnifiedReservationStatus.COMPLETED
            CANCELED -> reservation.normalizedStatus == UnifiedReservationStatus.CANCELED
        }
    }
}

private enum class UnifiedReservationStatus(
    val title: String,
    val icon: ImageVector,
) {
    PENDING("Pendiente", Icons.Rounded.HourglassTop),
    CONFIRMED("Confirmada", Icons.Rounded.CheckCircle),
    PREPARING("Preparando", Icons.Rounded.Fastfood),
    COMPLETED("Completada", Icons.Rounded.DoneAll),
    CANCELED("Cancelada", Icons.Rounded.Cancel),
}

private enum class UnifiedReservationKind(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val theme: AppSectionTheme,
) {
    RESTAURANT(
        title = "Restaurante",
        subtitle = "Pedidos y reservas de comida",
        icon = Icons.Rounded.Restaurant,
        theme = AppSectionTheme.Restaurant,
    ),
    ADVENTURE(
        title = "Aventura",
        subtitle = "Experiencias, combos y eventos",
        icon = Icons.Rounded.Terrain,
        theme = AppSectionTheme.Adventure,
    ),
}

private sealed interface UnifiedReservation {
    val id: String
    val kind: UnifiedReservationKind
    val title: String
    val subtitle: String
    val clientName: String
    val serviceDate: Date
    val endDate: Date
    val createdAt: Date
    val total: Double
    val normalizedStatus: UnifiedReservationStatus
    val searchableText: String

    val isTerminal: Boolean
        get() = normalizedStatus == UnifiedReservationStatus.COMPLETED ||
                normalizedStatus == UnifiedReservationStatus.CANCELED

    val isCanceled: Boolean
        get() = normalizedStatus == UnifiedReservationStatus.CANCELED

    fun occursOn(day: Date): Boolean {
        val startOfDay = AdventureDateHelper.startOfDay(day)
        val nextDay = Calendar.getInstance().apply {
            time = startOfDay
            add(Calendar.DAY_OF_YEAR, 1)
        }.time

        return serviceDate.before(nextDay) && !endDate.before(startOfDay)
    }

    data class RestaurantOrder(
        val order: Order,
    ) : UnifiedReservation {
        override val id: String = "restaurant-${order.id}"
        override val kind: UnifiedReservationKind = UnifiedReservationKind.RESTAURANT
        override val title: String =
            if (order.isScheduledForLater) "Reserva de comida" else "Pedido restaurante"
        override val subtitle: String =
            "${order.totalItems} item(s) • Mesa ${order.tableNumber}"
        override val clientName: String =
            order.clientName.ifBlank { "Cliente" }
        override val serviceDate: Date = order.scheduledAt
        override val endDate: Date = order.scheduledAt.addMinutes(90)
        override val createdAt: Date = order.createdAt
        override val total: Double = order.totalAmount
        override val normalizedStatus: UnifiedReservationStatus =
            order.recalculatedAgendaStatus().toUnifiedStatus()

        override val searchableText: String =
            listOf(
                order.id,
                order.clientName,
                order.tableNumber,
                order.nationalId.orEmpty(),
                order.serviceMode.title,
                order.items.joinToString(" ") { "${it.name} ${it.notes.orEmpty()}" },
            ).joinToString(" ").lowercase(Locale.US)
    }

    data class ExperienceBooking(
        val booking: AdventureBooking,
    ) : UnifiedReservation {
        override val id: String = "adventure-${booking.id}"
        override val kind: UnifiedReservationKind = UnifiedReservationKind.ADVENTURE
        override val title: String = booking.visitTypeTitle
        override val subtitle: String =
            "${booking.eventDisplayTitle} • ${booking.guestCount} invitado(s)"
        override val clientName: String =
            booking.clientName.ifBlank { "Cliente" }
        override val serviceDate: Date = booking.startAt
        override val endDate: Date = booking.endAt
        override val createdAt: Date = booking.createdAt
        override val total: Double = booking.totalAmount
        override val normalizedStatus: UnifiedReservationStatus =
            booking.status.toUnifiedStatus()

        override val searchableText: String =
            listOf(
                booking.id,
                booking.clientName,
                booking.whatsappNumber,
                booking.nationalId,
                booking.eventDisplayTitle,
                booking.visitTypeTitle,
                booking.items.joinToString(" ") { it.title },
                booking.foodReservation?.items?.joinToString(" ") { it.name }.orEmpty(),
            ).joinToString(" ").lowercase(Locale.US)
    }
}

private data class UnifiedReservationsGroup(
    val id: String,
    val title: String,
    val subtitle: String?,
    val reservations: List<UnifiedReservation>,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    adventureBookingsViewModel: AdventureBookingsViewModel = hiltViewModel(),
) {
    val theme = AppSectionTheme.Neutral
    val darkTheme = LocalBrandDarkTheme.current
    val neutralPalette = AppTheme.palette(theme, darkTheme)

    CompositionLocalProvider(
        LocalAppSectionTheme provides theme,
        LocalBrandPalette provides neutralPalette,
    ) {
        BookingsScreenContent(
            modifier = modifier,
            sessionState = sessionState,
            ordersViewModel = ordersViewModel,
            adventureBookingsViewModel = adventureBookingsViewModel,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BookingsScreenContent(
    modifier: Modifier,
    sessionState: SessionState.Authenticated,
    ordersViewModel: OrdersViewModel,
    adventureBookingsViewModel: AdventureBookingsViewModel,
) {
    val theme = AppSectionTheme.Neutral
    val palette = LocalBrandPalette.current

    val ordersState by ordersViewModel.uiState.collectAsStateWithLifecycle()
    val adventureState by adventureBookingsViewModel.uiState.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    var selectedScope by remember { mutableStateOf(BookingsTimelineScope.TODAY) }
    var grouping by remember { mutableStateOf(BookingsGroupingOption.BY_DATE) }
    var sortOption by remember { mutableStateOf(BookingsSortOption.SERVICE_TIME_ASCENDING) }
    var statusFilter by remember { mutableStateOf(UnifiedReservationStatusFilter.ALL) }
    var selectedReservation by remember { mutableStateOf<UnifiedReservation?>(null) }
    var bookingToCancel by remember { mutableStateOf<AdventureBooking?>(null) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        ordersViewModel.syncProfile(sessionState.profile)
        adventureBookingsViewModel.onAppear(sessionState.profile)
    }

    DisposableEffect(Unit) {
        onDispose {
            adventureBookingsViewModel.onDisappear()
        }
    }

    val now = Date()
    val today = AdventureDateHelper.startOfDay(now)

    val allReservations by remember(
        ordersState.orders,
        adventureState.allBookings,
    ) {
        derivedStateOf {
            ordersState.orders.map(UnifiedReservation::RestaurantOrder) +
                    adventureState.allBookings.map(UnifiedReservation::ExperienceBooking)
        }
    }

    val scopedReservations = remember(
        allReservations,
        selectedScope,
        now.time,
    ) {
        allReservations.filter { reservation ->
            val serviceDay = AdventureDateHelper.startOfDay(reservation.serviceDate)
            val isFutureDay = serviceDay.after(today)
            val isPastServiceDay = reservation.endDate.before(today)

            when (selectedScope) {
                BookingsTimelineScope.TODAY ->
                    !reservation.isTerminal && reservation.occursOn(today)

                BookingsTimelineScope.UPCOMING ->
                    !reservation.isTerminal && isFutureDay

                BookingsTimelineScope.HISTORY ->
                    reservation.isTerminal || isPastServiceDay

                BookingsTimelineScope.ALL ->
                    true
            }
        }
    }

    val visibleReservations = remember(
        scopedReservations,
        searchText,
        statusFilter,
        sortOption,
    ) {
        val query = searchText.trim().lowercase(Locale.US)

        scopedReservations
            .asSequence()
            .filter { statusFilter.matches(it) }
            .filter { query.isEmpty() || it.searchableText.contains(query) }
            .toList()
            .sortedByOption(sortOption, selectedScope)
    }

    val groupedReservations = remember(
        visibleReservations,
        grouping,
        sortOption,
        selectedScope,
    ) {
        visibleReservations.groupedByOption(
            grouping = grouping,
            sortOption = sortOption,
            selectedScope = selectedScope,
        )
    }

    val isLoading = ordersState.isLoading || adventureState.isLoading

    selectedReservation?.let { reservation ->
        ReservationDetailSheet(
            reservation = reservation,
            onDismiss = { selectedReservation = null },
            onCancelAdventure = { booking ->
                selectedReservation = null
                bookingToCancel = booking
            },
        )
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        adventureBookingsViewModel.cancelBooking(booking)
                        bookingToCancel = null
                    },
                ) {
                    Text(
                        text = "Sí, cancelar",
                        color = palette.destructive,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("No", color = palette.textSecondary)
                }
            },
            title = {
                Text(
                    text = "Cancelar reserva",
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Esta acción marcará la reserva de aventura como cancelada.",
                    color = palette.textSecondary,
                )
            },
            containerColor = palette.elevatedCard,
        )
    }

    val adventureMessage = adventureState.errorMessage ?: adventureState.successMessage
    adventureMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { adventureBookingsViewModel.dismissMessage() },
            confirmButton = {
                TextButton(onClick = { adventureBookingsViewModel.dismissMessage() }) {
                    Text("OK", color = palette.primary)
                }
            },
            title = {
                Text(
                    text = "Mensaje",
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = message,
                    color = palette.textSecondary,
                )
            },
            containerColor = palette.elevatedCard,
        )
    }

    BrandScreen(
        theme = theme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Reservas",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.textPrimary,
                            )

                            Text(
                                text = "Restaurante, aventura y eventos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                ordersViewModel.syncProfile(sessionState.profile)
                                adventureBookingsViewModel.onAppear(sessionState.profile)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Actualizar",
                                tint = palette.textPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = palette.background.copy(alpha = 0.94f),
                        titleContentColor = palette.textPrimary,
                        actionIconContentColor = palette.textPrimary,
                    ),
                )
            },
        ) { innerPadding ->
            when {
                isLoading && allReservations.isEmpty() -> {
                    LoadingReservationsState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 32.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        item {
                            ReservationsHeroCard(
                                selectedScope = selectedScope,
                                visibleCount = visibleReservations.size,
                                pendingCount = visibleReservations.count {
                                    it.normalizedStatus == UnifiedReservationStatus.PENDING
                                },
                                visibleTotal = visibleReservations.visibleTotal(),
                            )
                        }

                        item {
                            TimelineScopeSelector(
                                selectedScope = selectedScope,
                                onScopeSelected = { scope ->
                                    selectedScope = scope

                                    if (scope == BookingsTimelineScope.UPCOMING) {
                                        grouping = BookingsGroupingOption.BY_DATE
                                        sortOption = BookingsSortOption.SERVICE_TIME_ASCENDING
                                    }

                                    if (scope == BookingsTimelineScope.HISTORY) {
                                        sortOption = BookingsSortOption.SERVICE_TIME_DESCENDING
                                    }
                                },
                            )
                        }

                        item {
                            MetricsSection(
                                todayCount = allReservations.count {
                                    !it.isTerminal && it.occursOn(today)
                                },
                                upcomingCount = allReservations.count {
                                    !it.isTerminal &&
                                            AdventureDateHelper.startOfDay(it.serviceDate)
                                                .after(today)
                                },
                                historyCount = allReservations.count {
                                    it.isTerminal || it.endDate.before(today)
                                },
                                canceledCount = allReservations.count { it.isCanceled },
                            )
                        }

                        item {
                            ControlsSection(
                                searchText = searchText,
                                onSearchTextChange = { searchText = it },
                                grouping = grouping,
                                onGroupingChange = { grouping = it },
                                sortOption = sortOption,
                                onSortOptionChange = { sortOption = it },
                                statusFilter = statusFilter,
                                onStatusFilterChange = { statusFilter = it },
                                visibleCount = visibleReservations.size,
                                totalCount = allReservations.size,
                            )
                        }

                        ordersState.errorMessage?.let { message ->
                            item {
                                AgendaErrorBanner(message = message)
                            }
                        }

                        adventureState.errorMessage?.let { message ->
                            item {
                                AgendaErrorBanner(message = message)
                            }
                        }

                        if (visibleReservations.isEmpty()) {
                            item {
                                EmptyReservationsState(
                                    selectedScope = selectedScope,
                                    hasSearch = searchText.isNotBlank(),
                                )
                            }
                        } else {
                            groupedReservations.forEach { group ->
                                item(key = group.id) {
                                    ReservationsGroupHeader(group = group)
                                }

                                items(
                                    items = group.reservations,
                                    key = { it.id },
                                ) { reservation ->
                                    UnifiedReservationAgendaCard(
                                        reservation = reservation,
                                        onOpen = { selectedReservation = reservation },
                                        onCancelAdventure = { bookingToCancel = it },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationsHeroCard(
    selectedScope: BookingsTimelineScope,
    visibleCount: Int,
    pendingCount: Int,
    visibleTotal: Double,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(AppSectionTheme.Neutral, emphasized = false),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = AppSectionTheme.Neutral,
                icon = selectedScope.icon,
                size = 56.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = selectedScope.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    text = selectedScope.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeroInfoPill(
                title = "Visibles",
                value = visibleCount.toString(),
                modifier = Modifier.weight(1f),
            )

            HeroInfoPill(
                title = "Pendientes",
                value = pendingCount.toString(),
                modifier = Modifier.weight(1f),
            )

            HeroInfoPill(
                title = "Total",
                value = visibleTotal.agendaPriceText(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HeroInfoPill(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = modifier
            .clip(CircleShape)
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TimelineScopeSelector(
    selectedScope: BookingsTimelineScope,
    onScopeSelected: (BookingsTimelineScope) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(AppSectionTheme.Neutral),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = AppSectionTheme.Neutral,
            title = "Agenda",
            subtitle = "Elige qué parte de tus reservas quieres revisar.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BookingsTimelineScope.entries.forEach { scope ->
                AgendaFilterChip(
                    title = scope.shortTitle,
                    icon = scope.icon,
                    selected = selectedScope == scope,
                    onClick = { onScopeSelected(scope) },
                )
            }
        }
    }
}

@Composable
private fun MetricsSection(
    todayCount: Int,
    upcomingCount: Int,
    historyCount: Int,
    canceledCount: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Hoy",
                value = todayCount.toString(),
                subtitle = "Por atender",
                icon = Icons.Rounded.Schedule,
                tint = LocalBrandPalette.current.warning,
                modifier = Modifier.weight(1f),
            )

            MetricCard(
                title = "Próximas",
                value = upcomingCount.toString(),
                subtitle = "Futuras",
                icon = Icons.Rounded.CalendarMonth,
                tint = LocalBrandPalette.current.success,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Historial",
                value = historyCount.toString(),
                subtitle = "Pasadas o cerradas",
                icon = Icons.Rounded.History,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f),
            )

            MetricCard(
                title = "Canceladas",
                value = canceledCount.toString(),
                subtitle = "No activas",
                icon = Icons.Rounded.Cancel,
                tint = LocalBrandPalette.current.destructive,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = modifier.appCardStyle(AppSectionTheme.Neutral, emphasized = false),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = palette.textPrimary,
            fontWeight = FontWeight.ExtraBold,
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ControlsSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    grouping: BookingsGroupingOption,
    onGroupingChange: (BookingsGroupingOption) -> Unit,
    sortOption: BookingsSortOption,
    onSortOptionChange: (BookingsSortOption) -> Unit,
    statusFilter: UnifiedReservationStatusFilter,
    onStatusFilterChange: (UnifiedReservationStatusFilter) -> Unit,
    visibleCount: Int,
    totalCount: Int,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(AppSectionTheme.Neutral),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = AppSectionTheme.Neutral,
            title = "Organizar",
            subtitle = "$visibleCount de $totalCount reserva(s) visibles.",
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.Radius.large),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = palette.textSecondary,
                )
            },
            label = {
                Text("Buscar cliente, cédula, plato o actividad")
            },
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BookingsGroupingOption.entries.forEach { option ->
                AgendaFilterChip(
                    title = option.title,
                    icon = Icons.Rounded.FilterList,
                    selected = grouping == option,
                    onClick = { onGroupingChange(option) },
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AgendaDropDownControl(
                title = "Orden",
                value = sortOption.title,
                icon = Icons.Rounded.Sort,
                modifier = Modifier.weight(1f),
            ) { dismiss ->
                BookingsSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.title) },
                        onClick = {
                            onSortOptionChange(option)
                            dismiss()
                        },
                    )
                }
            }

            AgendaDropDownControl(
                title = "Estado",
                value = statusFilter.title,
                icon = Icons.Rounded.FilterList,
                modifier = Modifier.weight(1f),
            ) { dismiss ->
                UnifiedReservationStatusFilter.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.title) },
                        onClick = {
                            onStatusFilterChange(option)
                            dismiss()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AgendaDropDownControl(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    menuContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    val palette = LocalBrandPalette.current
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.Radius.medium))
                .background(palette.elevatedCard)
                .border(
                    width = 1.dp,
                    color = palette.stroke,
                    shape = RoundedCornerShape(AppTheme.Radius.medium),
                )
                .clickable { expanded = true }
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(20.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                tint = palette.textTertiary,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = palette.elevatedCard,
        ) {
            menuContent { expanded = false }
        }
    }
}

@Composable
private fun AgendaFilterChip(
    title: String,
    icon: ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current
    val shape = CircleShape

    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected) palette.primary.copy(alpha = 0.16f)
                else palette.elevatedCard
            )
            .border(
                width = 1.dp,
                color = if (selected) palette.primary.copy(alpha = 0.55f) else palette.stroke,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) palette.primary else palette.textSecondary,
                modifier = Modifier.size(16.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (selected) palette.primary else palette.textSecondary,
        )
    }
}

/* -------------------------------------------------------------------------- */
/* Reservation cards                                                           */
/* -------------------------------------------------------------------------- */

@Composable
private fun ReservationsGroupHeader(
    group: UnifiedReservationsGroup,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.ExtraBold,
            )

            group.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )
            }
        }

        Text(
            text = group.reservations.size.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = palette.textSecondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun UnifiedReservationAgendaCard(
    reservation: UnifiedReservation,
    onOpen: () -> Unit,
    onCancelAdventure: (AdventureBooking) -> Unit,
) {
    val neutral = LocalBrandPalette.current
    val darkTheme = LocalBrandDarkTheme.current
    val kindPalette = AppTheme.palette(reservation.kind.theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .appCardStyle(AppSectionTheme.Neutral),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = reservation.kind.theme,
                icon = reservation.kind.icon,
                size = 50.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = reservation.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = neutral.textPrimary,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = reservation.clientName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = neutral.textSecondary,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = reservation.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = neutral.textTertiary,
                )
            }

            StatusPill(status = reservation.normalizedStatus)
        }

        ScheduleBlock(reservation = reservation)

        when (reservation) {
            is UnifiedReservation.RestaurantOrder -> {
                RestaurantOrderPreview(order = reservation.order)
            }

            is UnifiedReservation.ExperienceBooking -> {
                AdventureBookingPreview(booking = reservation.booking)
            }
        }

        HorizontalDivider(color = neutral.stroke.copy(alpha = 0.75f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = reservation.total.agendaPriceText(),
                style = MaterialTheme.typography.titleLarge,
                color = kindPalette.primary,
                fontWeight = FontWeight.ExtraBold,
            )

            Spacer(modifier = Modifier.weight(1f))

            if (
                reservation is UnifiedReservation.ExperienceBooking &&
                reservation.booking.status != AdventureBookingStatus.CANCELED &&
                reservation.booking.status != AdventureBookingStatus.COMPLETED
            ) {
                TextButton(onClick = { onCancelAdventure(reservation.booking) }) {
                    Text(
                        text = "Cancelar",
                        color = neutral.destructive,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            TextButton(onClick = onOpen) {
                Text(
                    text = "Ver detalle",
                    color = kindPalette.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ScheduleBlock(
    reservation: UnifiedReservation,
) {
    val neutral = LocalBrandPalette.current
    val darkTheme = LocalBrandDarkTheme.current
    val kindPalette = AppTheme.palette(reservation.kind.theme, darkTheme)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(neutral.elevatedCard)
            .border(
                width = 1.dp,
                color = neutral.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.Schedule,
            contentDescription = null,
            tint = kindPalette.primary,
            modifier = Modifier.size(22.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = when (reservation) {
                    is UnifiedReservation.RestaurantOrder ->
                        if (reservation.order.isScheduledForLater) "Reserva para" else "Pedido para"

                    is UnifiedReservation.ExperienceBooking ->
                        "Visita para"
                },
                style = MaterialTheme.typography.labelMedium,
                color = neutral.textSecondary,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = when (reservation) {
                    is UnifiedReservation.RestaurantOrder ->
                        reservation.order.scheduledDateText

                    is UnifiedReservation.ExperienceBooking ->
                        "${reservation.booking.startAt.shortDateTime()} - ${reservation.booking.endAt.shortTime()}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = neutral.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = "Creada ${reservation.createdAt.shortDateTime()}",
                style = MaterialTheme.typography.labelSmall,
                color = neutral.textTertiary,
            )
        }

        KindBadge(kind = reservation.kind)
    }
}

@Composable
private fun RestaurantOrderPreview(
    order: Order,
) {
    val palette = LocalBrandPalette.current
    val effectiveStatus = order.recalculatedAgendaStatus()
    val progress = if (order.totalItems > 0) {
        order.preparedItemsCount.toFloat() / order.totalItems.toFloat()
    } else {
        0f
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AgendaTinyBadge(
                text = order.serviceMode.title,
                icon = if (order.serviceMode == OrderServiceMode.SCHEDULED) {
                    Icons.Rounded.CalendarMonth
                } else {
                    Icons.Rounded.Restaurant
                },
                theme = AppSectionTheme.Restaurant,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Mesa ${order.tableNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (order.requiresReconfirmation || order.wasEditedAfterConfirmation) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.requiresReconfirmation) {
                    WarningBadge(text = "Requiere reconfirmación")
                }

                if (order.wasEditedAfterConfirmation) {
                    WarningBadge(text = "Editado")
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            order.items.take(3).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.quantity}x",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(34.dp),
                    )

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    Text(
                        text = item.totalPrice.agendaPriceText(),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (order.items.size > 3) {
                Text(
                    text = "+${order.items.size - 3} producto(s) más",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when {
                        order.allItemsCompleted -> "Preparación completa"
                        order.hasStartedPreparing -> "Preparación iniciada"
                        effectiveStatus == OrderStatus.CONFIRMED -> "Confirmado"
                        else -> "Preparación"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${order.preparedItemsCount}/${order.totalItems}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = effectiveStatus.statusColor(),
                trackColor = palette.stroke.copy(alpha = 0.65f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}

@Composable
private fun AdventureBookingPreview(
    booking: AdventureBooking,
) {
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AgendaTinyBadge(
                text = "${booking.guestCount} invitado(s)",
                icon = Icons.Rounded.Person,
                theme = AppSectionTheme.Adventure,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = booking.eventDisplayTitle,
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (booking.hasActivities) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                booking.items.take(3).forEach { item ->
                    ReservationActivityLine(item = item)
                }

                if (booking.items.size > 3) {
                    Text(
                        text = "+${booking.items.size - 3} actividad(es) más",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textTertiary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        booking.foodReservation?.takeIf { !it.isEmpty }?.let { food ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalDining,
                    contentDescription = null,
                    tint = AppTheme.palette(
                        AppSectionTheme.Adventure,
                        LocalBrandDarkTheme.current
                    ).primary,
                    modifier = Modifier.size(18.dp),
                )

                Text(
                    text = food.items.take(3).joinToString(" • ") {
                        "${it.quantity}x ${it.name}"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* Detail sheet                                                                */
/* -------------------------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationDetailSheet(
    reservation: UnifiedReservation,
    onDismiss: () -> Unit,
    onCancelAdventure: (AdventureBooking) -> Unit,
) {
    val palette = LocalBrandPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandSectionHeader(
                    theme = reservation.kind.theme,
                    title = "Detalle de reserva",
                    subtitle = reservation.title,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        tint = palette.textPrimary,
                    )
                }
            }

            when (reservation) {
                is UnifiedReservation.RestaurantOrder -> {
                    RestaurantOrderDetailContent(order = reservation.order)
                }

                is UnifiedReservation.ExperienceBooking -> {
                    AdventureBookingDetailContent(
                        booking = reservation.booking,
                        onCancel = if (
                            reservation.booking.status != AdventureBookingStatus.CANCELED &&
                            reservation.booking.status != AdventureBookingStatus.COMPLETED
                        ) {
                            { onCancelAdventure(reservation.booking) }
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantOrderDetailContent(
    order: Order,
) {
    val palette = LocalBrandPalette.current
    val effectiveStatus = order.recalculatedAgendaStatus()
    val progress = if (order.totalItems > 0) {
        order.preparedItemsCount.toFloat() / order.totalItems.toFloat()
    } else {
        0f
    }

    DetailHeroCard(
        theme = AppSectionTheme.Restaurant,
        icon = Icons.Rounded.Restaurant,
        title = if (order.isScheduledForLater) "Reserva de comida" else "Pedido restaurante",
        subtitle = order.scheduledDateText,
        status = effectiveStatus.toUnifiedStatus(),
        total = order.totalAmount,
    )

    DetailSection(
        theme = AppSectionTheme.Restaurant,
        title = "Horario",
        subtitle = "Resumen de servicio del pedido.",
    ) {
        DetailRow("Cliente", order.clientName)
        DetailRow("Mesa", order.tableNumber)
        DetailRow("Servicio", order.serviceMode.title)
        DetailRow("Programado", order.scheduledDateText)
        DetailRow("Creado", order.createdAt.shortDateTime())

        if (order.requiresReconfirmation) {
            WarningBadge(text = "Este pedido requiere reconfirmación")
        }

        if (order.wasEditedAfterConfirmation) {
            WarningBadge(text = "Fue editado después de confirmar")
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = effectiveStatus.statusColor(),
            trackColor = palette.stroke,
        )
    }

    DetailSection(
        theme = AppSectionTheme.Restaurant,
        title = "Productos",
        subtitle = "Todo lo incluido en este pedido.",
    ) {
        order.items.forEach { item ->
            OrderDetailItemCard(item = item)
        }
    }

    if (order.appliedRewards.isNotEmpty()) {
        DetailSection(
            theme = AppSectionTheme.Restaurant,
            title = "Premios aplicados",
            subtitle = "Beneficios usados automáticamente.",
        ) {
            order.appliedRewards.forEach { reward ->
                DetailAmountRow(
                    title = reward.title,
                    amount = -reward.amount,
                    forceGreen = true,
                )
                Text(
                    text = reward.note,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )
            }
        }
    }

    DetailSection(
        theme = AppSectionTheme.Restaurant,
        title = "Montos",
        subtitle = "Resumen económico.",
    ) {
        DetailAmountRow("Subtotal", order.subtotal)

        if (order.loyaltyDiscountAmount > 0) {
            DetailAmountRow(
                title = "Murco Loyalty",
                amount = -order.loyaltyDiscountAmount,
                forceGreen = true,
            )
        }

        HorizontalDivider(color = palette.stroke)

        DetailAmountRow(
            title = "Total",
            amount = order.totalAmount,
            bold = true,
        )
    }
}

@Composable
private fun OrderDetailItemCard(
    item: OrderItem,
) {
    val palette = LocalBrandPalette.current
    val progress = if (item.quantity > 0) {
        item.safePreparedQuantity.toFloat() / item.quantity.toFloat()
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = AppSectionTheme.Restaurant,
                icon = Icons.Rounded.LocalDining,
                size = 42.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "${item.quantity} × ${item.unitPrice.agendaPriceText()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )

                Text(
                    text = "Pendiente: ${item.remainingQuantity}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = item.totalPrice.agendaPriceText(),
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Preparado: ${item.safePreparedQuantity}/${item.quantity}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
            )

            Spacer(modifier = Modifier.weight(1f))

            StatusMiniText(
                text = when {
                    item.isCompleted -> "Completo"
                    item.isStarted -> "En proceso"
                    else -> "Pendiente"
                },
                color = when {
                    item.isCompleted -> palette.success
                    item.isStarted -> palette.warning
                    else -> palette.textTertiary
                },
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape),
            color = when {
                item.isCompleted -> palette.success
                item.isStarted -> palette.warning
                else -> palette.textTertiary
            },
            trackColor = palette.stroke,
        )

        item.notes?.takeIf { it.isNotBlank() }?.let { notes ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.Radius.small))
                    .background(palette.card)
                    .padding(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp),
                )

                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun AdventureBookingDetailContent(
    booking: AdventureBooking,
    onCancel: (() -> Unit)?,
) {
    DetailHeroCard(
        theme = AppSectionTheme.Adventure,
        icon = if (booking.hasActivities) Icons.Rounded.Terrain else Icons.Rounded.LocalDining,
        title = booking.eventDisplayTitle,
        subtitle = "${booking.visitTypeTitle} • ${booking.startAt.shortDateTime()}",
        status = booking.status.toUnifiedStatus(),
        total = booking.totalAmount,
    )

    DetailSection(
        theme = AppSectionTheme.Adventure,
        title = "Horario",
        subtitle = "Resumen de fecha y duración.",
    ) {
        DetailRow("Inicio", booking.startAt.longDateTime())
        DetailRow("Fin", booking.endAt.longDateTime())
        DetailRow("Creada", booking.createdAt.shortDateTime())
        DetailRow("Tipo", booking.visitTypeTitle)
        DetailRow("Evento", booking.eventDisplayTitle)
    }

    DetailSection(
        theme = AppSectionTheme.Adventure,
        title = "Cliente",
        subtitle = "Datos asociados a la reserva.",
    ) {
        DetailRow("Nombre", booking.clientName)
        DetailRow("WhatsApp", booking.whatsappNumber)
        DetailRow("Cédula", booking.nationalId)
        DetailRow("Invitados", "${booking.guestCount}")
    }

    if (booking.hasActivities) {
        DetailSection(
            theme = AppSectionTheme.Adventure,
            title = "Actividades",
            subtitle = "Configuración principal del combo.",
        ) {
            booking.items.forEach { item ->
                AdventureActivityDetailCard(item = item)
            }
        }

        DetailSection(
            theme = AppSectionTheme.Adventure,
            title = "Itinerario",
            subtitle = "Bloques reales programados.",
        ) {
            booking.blocks.forEach { block ->
                AdventureBlockCard(block = block)
            }
        }
    }

    booking.foodReservation?.takeIf { !it.isEmpty }?.let { food ->
        DetailSection(
            theme = AppSectionTheme.Adventure,
            title = "Comida reservada",
            subtitle = "Platos agregados a esta reserva.",
        ) {
            food.items.forEach { item ->
                FoodDetailCard(item = item)
            }

            HorizontalDivider(color = LocalBrandPalette.current.stroke)

            DetailRow("Servicio", food.servingMoment.title)
            food.servingTime?.let {
                DetailRow("Hora", it.shortTime())
            }
            food.notes?.takeIf { it.isNotBlank() }?.let {
                DetailRow("Notas cocina", it)
            }
        }
    }

    if (!booking.eventNotes.isNullOrBlank() || !booking.notes.isNullOrBlank()) {
        DetailSection(
            theme = AppSectionTheme.Adventure,
            title = "Notas",
            subtitle = "Indicaciones adicionales.",
        ) {
            booking.eventNotes?.takeIf { it.isNotBlank() }?.let {
                DetailNoteCard(title = "Notas del evento", text = it)
            }

            booking.notes?.takeIf { it.isNotBlank() }?.let {
                DetailNoteCard(title = "Notas generales", text = it)
            }
        }
    }

    DetailSection(
        theme = AppSectionTheme.Adventure,
        title = "Totales",
        subtitle = "Resumen económico.",
    ) {
        DetailAmountRow("Aventura", booking.adventureSubtotal)
        DetailAmountRow("Comida", booking.foodSubtotal)
        DetailAmountRow("Subtotal", booking.subtotal)

        if (booking.discountAmount > 0) {
            DetailAmountRow(
                title = "Descuento",
                amount = -booking.discountAmount,
                forceGreen = true,
            )
        }

        if (booking.loyaltyDiscountAmount > 0) {
            DetailAmountRow(
                title = "Murco Loyalty",
                amount = -booking.loyaltyDiscountAmount,
                forceGreen = true,
            )
        }

        HorizontalDivider(color = LocalBrandPalette.current.stroke)

        DetailAmountRow(
            title = "Total",
            amount = booking.totalAmount,
            bold = true,
        )

        if (booking.appliedRewards.isNotEmpty()) {
            HorizontalDivider(color = LocalBrandPalette.current.stroke)

            booking.appliedRewards.forEach { reward ->
                DetailAmountRow(
                    title = reward.title,
                    amount = -reward.amount,
                    forceGreen = true,
                )

                Text(
                    text = reward.note,
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalBrandPalette.current.textSecondary,
                )
            }
        }
    }

    if (onCancel != null) {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                tint = LocalBrandPalette.current.destructive,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Cancelar reserva",
                color = LocalBrandPalette.current.destructive,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* Detail components                                                           */
/* -------------------------------------------------------------------------- */

@Composable
private fun DetailHeroCard(
    theme: AppSectionTheme,
    icon: ImageVector,
    title: String,
    subtitle: String,
    status: UnifiedReservationStatus,
    total: Double,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.xLarge))
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(AppTheme.Radius.xLarge),
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            BrandIconBubble(
                theme = theme,
                icon = icon,
                size = 56.dp,
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = Color.White.copy(alpha = 0.18f),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
            ) {
                Text(
                    text = status.title,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = palette.onPrimary,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.onPrimary.copy(alpha = 0.88f),
            )
        }

        Text(
            text = total.agendaPriceText(),
            style = MaterialTheme.typography.titleLarge,
            color = palette.onPrimary,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun DetailSection(
    theme: AppSectionTheme,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = title,
            subtitle = subtitle,
        )

        content()
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String,
) {
    val palette = LocalBrandPalette.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(92.dp),
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailAmountRow(
    title: String,
    amount: Double,
    bold: Boolean = false,
    forceGreen: Boolean = false,
) {
    val palette = LocalBrandPalette.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (bold) palette.textPrimary else palette.textSecondary,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = amount.agendaPriceText(),
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = when {
                forceGreen -> palette.success
                bold -> palette.primary
                else -> palette.textPrimary
            },
            fontWeight = if (bold || forceGreen) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AdventureActivityDetailCard(
    item: AdventureReservationItemDraft,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BrandIconBubble(
            theme = AppSectionTheme.Adventure,
            icon = adventureIconFor(item.activity),
            size = 42.dp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = item.summaryText,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )
        }
    }
}

@Composable
private fun AdventureBlockCard(
    block: AdventureBookingBlock,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        AppTheme.palette(
                            AppSectionTheme.Adventure,
                            LocalBrandDarkTheme.current
                        ).primary
                    ),
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(42.dp)
                    .background(palette.stroke),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = block.title,
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "${block.startAt.shortTime()} - ${block.endAt.shortTime()}",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )

            Text(
                text = block.unitsText(),
                style = MaterialTheme.typography.labelMedium,
                color = palette.textTertiary,
            )

            if (block.subtotal > 0) {
                Text(
                    text = block.subtotal.agendaPriceText(),
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.palette(
                        AppSectionTheme.Adventure,
                        LocalBrandDarkTheme.current
                    ).primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun FoodDetailCard(
    item: ReservationFoodItemDraft,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BrandIconBubble(
            theme = AppSectionTheme.Adventure,
            icon = Icons.Rounded.LocalDining,
            size = 40.dp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${item.quantity}x ${item.name}",
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Unitario: ${item.unitPrice.agendaPriceText()}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
            )

            Text(
                text = "Subtotal: ${item.subtotal.agendaPriceText()}",
                style = MaterialTheme.typography.labelMedium,
                color = AppTheme.palette(
                    AppSectionTheme.Adventure,
                    LocalBrandDarkTheme.current
                ).primary,
                fontWeight = FontWeight.Bold,
            )

            item.notes?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textTertiary,
                )
            }
        }
    }
}

@Composable
private fun DetailNoteCard(
    title: String,
    text: String,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.elevatedCard)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = palette.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
        )
    }
}

/* -------------------------------------------------------------------------- */
/* Small shared UI                                                             */
/* -------------------------------------------------------------------------- */

@Composable
private fun StatusPill(
    status: UnifiedReservationStatus,
) {
    val color = status.statusColor()

    Surface(
        color = color.copy(alpha = if (LocalBrandDarkTheme.current) 0.22f else 0.13f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.42f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = status.icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp),
            )

            Text(
                text = status.title,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun KindBadge(
    kind: UnifiedReservationKind,
) {
    val palette = AppTheme.palette(kind.theme, LocalBrandDarkTheme.current)

    Surface(
        color = palette.primary.copy(alpha = 0.14f),
        shape = CircleShape,
        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f)),
    ) {
        Text(
            text = kind.title,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = palette.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AgendaTinyBadge(
    text: String,
    icon: ImageVector,
    theme: AppSectionTheme,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Surface(
        color = palette.primary.copy(alpha = 0.12f),
        shape = CircleShape,
        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.25f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(14.dp),
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = palette.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WarningBadge(
    text: String,
) {
    val palette = LocalBrandPalette.current

    Surface(
        color = palette.warning.copy(alpha = if (LocalBrandDarkTheme.current) 0.22f else 0.13f),
        shape = CircleShape,
        border = BorderStroke(1.dp, palette.warning.copy(alpha = 0.42f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = palette.warning,
                modifier = Modifier.size(14.dp),
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = palette.warning,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StatusMiniText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ReservationActivityLine(
    item: AdventureReservationItemDraft,
) {
    val palette = LocalBrandPalette.current
    val adventurePalette = AppTheme.palette(AppSectionTheme.Adventure, LocalBrandDarkTheme.current)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = adventureIconFor(item.activity),
            contentDescription = null,
            tint = adventurePalette.primary,
            modifier = Modifier.size(18.dp),
        )

        Text(
            text = "${item.title} • ${item.summaryText}",
            style = MaterialTheme.typography.labelMedium,
            color = palette.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AgendaErrorBanner(
    message: String,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.medium))
            .background(palette.warning.copy(alpha = if (LocalBrandDarkTheme.current) 0.18f else 0.10f))
            .border(
                width = 1.dp,
                color = palette.warning.copy(alpha = 0.35f),
                shape = RoundedCornerShape(AppTheme.Radius.medium),
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            tint = palette.warning,
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LoadingReservationsState(
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CircularProgressIndicator(color = palette.primary)

            Text(
                text = "Cargando tus reservas...",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textSecondary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptyReservationsState(
    selectedScope: BookingsTimelineScope,
    hasSearch: Boolean,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(AppSectionTheme.Neutral),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandIconBubble(
            theme = AppSectionTheme.Neutral,
            icon = Icons.Rounded.Event,
            size = 58.dp,
        )

        Text(
            text = "No hay reservas aquí",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = when {
                hasSearch ->
                    "No encontramos reservas que coincidan con tu búsqueda en esta sección."

                selectedScope == BookingsTimelineScope.TODAY ->
                    "Tus pedidos y reservas de hoy aparecerán aquí."

                selectedScope == BookingsTimelineScope.UPCOMING ->
                    "Las próximas reservas de comida, aventura o eventos aparecerán aquí."

                selectedScope == BookingsTimelineScope.HISTORY ->
                    "Tus reservas completadas, pasadas o canceladas aparecerán aquí."

                else ->
                    "Cuando hagas pedidos o reserves experiencias, aparecerán aquí."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
        )
    }
}

/* -------------------------------------------------------------------------- */
/* Sorting, grouping and helpers                                               */
/* -------------------------------------------------------------------------- */

private fun List<UnifiedReservation>.sortedByOption(
    sortOption: BookingsSortOption,
    selectedScope: BookingsTimelineScope,
): List<UnifiedReservation> {
    return when (sortOption) {
        BookingsSortOption.SERVICE_TIME_ASCENDING ->
            sortedWith(
                compareBy<UnifiedReservation> { it.serviceDate.time }
                    .thenBy { it.createdAt.time },
            )

        BookingsSortOption.SERVICE_TIME_DESCENDING ->
            sortedWith(
                compareByDescending<UnifiedReservation> { it.serviceDate.time }
                    .thenByDescending { it.createdAt.time },
            )

        BookingsSortOption.NEWEST_CREATED ->
            sortedWith(
                compareByDescending<UnifiedReservation> { it.createdAt.time }
                    .thenByDescending { it.serviceDate.time },
            )

        BookingsSortOption.HIGHEST_TOTAL ->
            sortedWith(
                compareByDescending<UnifiedReservation> { it.total }
                    .thenBy { it.serviceDate.time },
            )
    }.let { sorted ->
        if (
            selectedScope == BookingsTimelineScope.HISTORY &&
            sortOption == BookingsSortOption.SERVICE_TIME_ASCENDING
        ) {
            sorted.reversed()
        } else {
            sorted
        }
    }
}

private fun List<UnifiedReservation>.groupedByOption(
    grouping: BookingsGroupingOption,
    sortOption: BookingsSortOption,
    selectedScope: BookingsTimelineScope,
): List<UnifiedReservationsGroup> {
    return when (grouping) {
        BookingsGroupingOption.BY_DATE -> {
            val groups = groupBy {
                AdventureDateHelper.dayKey(
                    AdventureDateHelper.startOfDay(it.serviceDate)
                )
            }

            groups.map { (key, reservations) ->
                val day = AdventureDateHelper.startOfDay(reservations.first().serviceDate)
                UnifiedReservationsGroup(
                    id = "date-$key",
                    title = sectionTitle(day),
                    subtitle = "Fecha de servicio o visita",
                    reservations = reservations.sortedByOption(sortOption, selectedScope),
                )
            }.sortedWith(
                if (
                    selectedScope == BookingsTimelineScope.HISTORY ||
                    sortOption == BookingsSortOption.SERVICE_TIME_DESCENDING ||
                    sortOption == BookingsSortOption.NEWEST_CREATED
                ) {
                    compareByDescending { it.reservations.first().serviceDate.time }
                } else {
                    compareBy { it.reservations.first().serviceDate.time }
                },
            )
        }

        BookingsGroupingOption.BY_STATUS -> {
            val order = listOf(
                UnifiedReservationStatus.PENDING,
                UnifiedReservationStatus.CONFIRMED,
                UnifiedReservationStatus.PREPARING,
                UnifiedReservationStatus.COMPLETED,
                UnifiedReservationStatus.CANCELED,
            )

            val groups = groupBy { it.normalizedStatus }

            order.mapNotNull { status ->
                val reservations = groups[status].orEmpty()
                if (reservations.isEmpty()) return@mapNotNull null

                UnifiedReservationsGroup(
                    id = "status-${status.name}",
                    title = status.title,
                    subtitle = statusSubtitle(status),
                    reservations = reservations.sortedByOption(sortOption, selectedScope),
                )
            }
        }

        BookingsGroupingOption.BY_TYPE -> {
            UnifiedReservationKind.entries.mapNotNull { kind ->
                val reservations = filter { it.kind == kind }
                if (reservations.isEmpty()) return@mapNotNull null

                UnifiedReservationsGroup(
                    id = "kind-${kind.name}",
                    title = kind.title,
                    subtitle = kind.subtitle,
                    reservations = reservations.sortedByOption(sortOption, selectedScope),
                )
            }
        }
    }
}

private fun statusSubtitle(status: UnifiedReservationStatus): String {
    return when (status) {
        UnifiedReservationStatus.PENDING -> "Esperando confirmación"
        UnifiedReservationStatus.CONFIRMED -> "Reserva aceptada"
        UnifiedReservationStatus.PREPARING -> "Pedido en preparación"
        UnifiedReservationStatus.COMPLETED -> "Reserva finalizada"
        UnifiedReservationStatus.CANCELED -> "Reserva cancelada"
    }
}

private fun List<UnifiedReservation>.visibleTotal(): Double {
    return filterNot { it.isCanceled }.sumOf { it.total }
}

fun Order.recalculatedAgendaStatus(): OrderStatus {
    return when {
        status == OrderStatus.CANCELED -> OrderStatus.CANCELED
        requiresReconfirmation -> OrderStatus.PENDING
        allItemsCompleted -> OrderStatus.COMPLETED
        hasStartedPreparing -> OrderStatus.PREPARING
        status == OrderStatus.CONFIRMED -> OrderStatus.CONFIRMED
        else -> OrderStatus.PENDING
    }
}

private fun OrderStatus.toUnifiedStatus(): UnifiedReservationStatus {
    return when (this) {
        OrderStatus.PENDING -> UnifiedReservationStatus.PENDING
        OrderStatus.CONFIRMED -> UnifiedReservationStatus.CONFIRMED
        OrderStatus.PREPARING -> UnifiedReservationStatus.PREPARING
        OrderStatus.COMPLETED -> UnifiedReservationStatus.COMPLETED
        OrderStatus.CANCELED -> UnifiedReservationStatus.CANCELED
    }
}

private fun AdventureBookingStatus.toUnifiedStatus(): UnifiedReservationStatus {
    return when (this) {
        AdventureBookingStatus.PENDING -> UnifiedReservationStatus.PENDING
        AdventureBookingStatus.CONFIRMED -> UnifiedReservationStatus.CONFIRMED
        AdventureBookingStatus.COMPLETED -> UnifiedReservationStatus.COMPLETED
        AdventureBookingStatus.CANCELED -> UnifiedReservationStatus.CANCELED
    }
}

@Composable
private fun UnifiedReservationStatus.statusColor(): Color {
    val palette = LocalBrandPalette.current

    return when (this) {
        UnifiedReservationStatus.PENDING -> palette.warning
        UnifiedReservationStatus.CONFIRMED -> palette.success
        UnifiedReservationStatus.PREPARING -> Color(0xFF8B5CF6)
        UnifiedReservationStatus.COMPLETED -> Color(0xFF3B82F6)
        UnifiedReservationStatus.CANCELED -> palette.destructive
    }
}

@Composable
private fun OrderStatus.statusColor(): Color {
    return toUnifiedStatus().statusColor()
}

private fun Date.addMinutes(minutes: Int): Date {
    return Calendar.getInstance().apply {
        time = this@addMinutes
        add(Calendar.MINUTE, minutes)
    }.time
}

private fun sectionTitle(date: Date): String {
    val today = AdventureDateHelper.startOfDay(Date())
    val tomorrow = Calendar.getInstance().apply {
        time = today
        add(Calendar.DAY_OF_YEAR, 1)
    }.time
    val yesterday = Calendar.getInstance().apply {
        time = today
        add(Calendar.DAY_OF_YEAR, -1)
    }.time

    return when {
        AdventureDateHelper.sameDay(date, today) -> "Hoy"
        AdventureDateHelper.sameDay(date, tomorrow) -> "Mañana"
        AdventureDateHelper.sameDay(date, yesterday) -> "Ayer"
        else -> SimpleDateFormat(
            "EEEE d 'de' MMMM yyyy",
            Locale("es", "EC"),
        ).format(date).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("es", "EC")) else it.toString()
        }
    }
}

private fun Date.shortDateTime(): String {
    return SimpleDateFormat(
        "dd MMM yyyy, h:mm a",
        Locale("es", "EC"),
    ).format(this)
}

private fun Date.longDateTime(): String {
    return SimpleDateFormat(
        "EEEE d 'de' MMMM yyyy, h:mm a",
        Locale("es", "EC"),
    ).format(this).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale("es", "EC")) else it.toString()
    }
}

private fun Date.shortTime(): String {
    return SimpleDateFormat(
        "h:mm a",
        Locale("es", "EC"),
    ).format(this)
}

private fun Double.agendaPriceText(): String {
    return "$${String.format(Locale.US, "%.2f", this)}"
}

private val ReservationFoodDraft.isEmpty: Boolean
    get() = items.isEmpty()

private fun AdventureBookingBlock.unitsText(): String {
    return when (resourceType) {
        AdventureResourceType.OFF_ROAD_VEHICLES -> "$reservedUnits vehículo(s)"
        AdventureResourceType.PAINTBALL_PEOPLE,
        AdventureResourceType.GO_KART_PEOPLE,
        AdventureResourceType.SHOOTING_PEOPLE,
        AdventureResourceType.CAMPING_PEOPLE,
        AdventureResourceType.EXTREME_SLIDE_PEOPLE -> "$reservedUnits persona(s)"
    }
}

private fun adventureIconFor(activity: AdventureActivityType): ImageVector {
    return when (activity) {
        AdventureActivityType.OFF_ROAD -> Icons.Rounded.DirectionsCar
        AdventureActivityType.PAINTBALL -> Icons.Rounded.PersonSearch
        AdventureActivityType.GO_KARTS -> Icons.Rounded.DirectionsCar
        AdventureActivityType.SHOOTING_RANGE -> Icons.Rounded.PersonSearch
        AdventureActivityType.CAMPING -> Icons.Rounded.Terrain
        AdventureActivityType.EXTREME_SLIDE -> Icons.Rounded.Terrain
    }
}