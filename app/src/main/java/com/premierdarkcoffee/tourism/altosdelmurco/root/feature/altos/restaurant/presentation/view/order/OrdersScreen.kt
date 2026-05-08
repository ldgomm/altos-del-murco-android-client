package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view.recalculatedAgendaStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart.ErrorCardInline
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersGroupingOption
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersSortOption
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersUiState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val RestaurantTheme = AppSectionTheme.Restaurant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrdersUiState,
    onBack: () -> Unit,
    onGroupingSelected: (OrdersGroupingOption) -> Unit,
    onSortSelected: (OrdersSortOption) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    BrandScreen(
        theme = RestaurantTheme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Mis pedidos",
                            color = palette.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Volver",
                                tint = palette.textPrimary,
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            LazyOrdersContent(
                state = state,
                innerPadding = innerPadding,
                onGroupingSelected = onGroupingSelected,
                onSortSelected = onSortSelected,
                onStatusSelected = onStatusSelected,
                onDismissError = onDismissError,
            )
        }
    }
}

@Composable
private fun LazyOrdersContent(
    state: OrdersUiState,
    innerPadding: PaddingValues,
    onGroupingSelected: (OrdersGroupingOption) -> Unit,
    onSortSelected: (OrdersSortOption) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
    onDismissError: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    androidx.compose.foundation.lazy.LazyColumn(
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
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = palette.accent,
                    trackColor = palette.stroke.copy(alpha = 0.45f),
                )
            }
        }

        state.errorMessage?.let { message ->
            item {
                ErrorCardInline(
                    message = message,
                    onDismiss = onDismissError,
                )
            }
        }

        item {
            OrdersControlsCard(
                state = state,
                onGroupingSelected = onGroupingSelected,
                onSortSelected = onSortSelected,
                onStatusSelected = onStatusSelected,
            )
        }

        if (state.visibleOrders.isEmpty()) {
            item {
                EmptyOrdersCard()
            }
        } else {
            val groups = groupOrders(state)

            groups.forEach { (title, orders) ->
                item {
                    BrandSectionHeader(
                        theme = RestaurantTheme,
                        title = title,
                        subtitle = "${orders.size} pedido(s)",
                    )
                }

                items(orders, key = { it.id }) { order -> OrderCard(order = order) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersControlsCard(
    state: OrdersUiState,
    onGroupingSelected: (OrdersGroupingOption) -> Unit,
    onSortSelected: (OrdersSortOption) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(
                theme = RestaurantTheme,
                emphasized = true,
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = RestaurantTheme,
            title = "Herramientas",
            subtitle = "Agrupa, filtra y ordena tus pedidos.",
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            OrdersGroupingOption.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = state.grouping == option,
                    onClick = { onGroupingSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = OrdersGroupingOption.entries.size,
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = palette.primary,
                        activeContentColor = palette.onPrimary,
                        inactiveContainerColor = palette.elevatedCard,
                        inactiveContentColor = palette.textSecondary,
                        activeBorderColor = Color.Transparent,
                        inactiveBorderColor = palette.stroke,
                    ),
                ) {
                    Text(
                        text = option.title,
                        fontWeight = if (state.grouping == option) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RestaurantFilterChip(
                selected = state.statusFilter == null,
                label = "Todos",
                onClick = { onStatusSelected(null) },
            )

            OrderStatus.entries.forEach { status ->
                RestaurantFilterChip(
                    selected = state.statusFilter == status,
                    label = status.title,
                    onClick = { onStatusSelected(status) },
                )
            }
        }

        SortDropdown(
            selected = state.sortOption,
            onSelected = onSortSelected,
        )
    }
}

@Composable
private fun RestaurantFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
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
            selected = selected,
            borderColor = palette.stroke,
            selectedBorderColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(
    selected: OrdersSortOption,
    onSelected: (OrdersSortOption) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.title,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text("Ordenar")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                )
            },
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary,
                focusedLabelColor = palette.primary,
                unfocusedLabelColor = palette.textSecondary,
                focusedBorderColor = palette.primary,
                unfocusedBorderColor = palette.stroke,
                focusedContainerColor = palette.elevatedCard,
                unfocusedContainerColor = palette.elevatedCard,
                cursorColor = palette.primary,
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = palette.elevatedCard,
        ) {
            OrdersSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.title,
                            color = if (option == selected) {
                                palette.primary
                            } else {
                                palette.textPrimary
                            },
                            fontWeight = if (option == selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyOrdersCard() {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appCardStyle(
                    theme = RestaurantTheme,
                    emphasized = true,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrandIconBubble(
                theme = RestaurantTheme,
                icon = Icons.Rounded.ReceiptLong,
                size = 56.dp,
            )

            Text(
                text = "No hay pedidos para mostrar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )

            Text(
                text = "Cuando envíes pedidos aparecerán aquí en tiempo real.",
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = RestaurantTheme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = "Pedido #${order.id.takeLast(6).uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                )

                Text(
                    text = "${order.totalItems} producto(s) • Mesa ${order.tableNumber}",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (order.isScheduledForLater) {
                    Text(
                        text = "WhatsApp: ${order.displayWhatsApp}",
                        color = palette.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            StatusPill(order.status)
        }

        HorizontalDivider(
            color = palette.stroke.copy(alpha = 0.75f),
        )

        order.items.take(3).forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BrandIconBubble(
                    theme = RestaurantTheme,
                    icon = Icons.Rounded.RestaurantMenu,
                    size = 36.dp,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "${item.quantity}x ${item.name}",
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes.orEmpty(),
                            color = palette.textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Text(
                    text = item.totalPrice.priceLabel(),
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (order.items.size > 3) {
            Text(
                text = "+${order.items.size - 3} producto(s) más",
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalDivider(
            color = palette.stroke.copy(alpha = 0.75f),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = order.createdAt.shortDateTime(),
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = order.totalAmount.priceLabel(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = palette.primary,
            )
        }
    }
}

@Composable
private fun StatusPill(status: OrderStatus) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(RestaurantTheme, darkTheme)

    val color = when (status) {
        OrderStatus.PENDING -> palette.warning
        OrderStatus.CONFIRMED -> palette.primary
        OrderStatus.PREPARING -> palette.accent
        OrderStatus.COMPLETED -> palette.success
        OrderStatus.CANCELED -> palette.destructive
    }

    Surface(
        color = color.copy(alpha = if (darkTheme) 0.18f else 0.12f),
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Text(
            text = status.title,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

private fun groupOrders(state: OrdersUiState): List<Pair<String, List<Order>>> {
    return when (state.grouping) {
        OrdersGroupingOption.DATE -> state.visibleOrders.groupBy { OrdersViewModel.dateGroupTitle(it.scheduledAt) }
            .map {
                it.key to it.value.sortedWith(compareBy<Order> { order -> order.scheduledAt.time }.thenBy { order -> order.createdAt.time })
            }

        OrdersGroupingOption.STATUS -> state.visibleOrders.groupBy { it.recalculatedAgendaStatus().title }
            .map { it.key to it.value }
    }
}


private fun Date.shortDateTime(): String {
    return SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale("es", "EC"),
    ).format(this)
}
