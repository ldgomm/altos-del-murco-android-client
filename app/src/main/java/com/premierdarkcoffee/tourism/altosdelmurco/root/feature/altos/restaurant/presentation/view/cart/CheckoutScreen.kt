package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.TableRestaurant
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderScheduleFormatter
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CheckoutUiState
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    profile: ClientProfile,
    onBack: () -> Unit,
    onTableNumberChanged: (String) -> Unit,
    onScheduledAtChanged: (Date) -> Unit,
    onScheduleNow: () -> Unit,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Restaurant
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    BrandScreen(
        theme = theme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Confirmar pedido", fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = palette.surface.copy(alpha = 0.92f),
                        titleContentColor = palette.textPrimary,
                        navigationIconContentColor = palette.textPrimary,
                    ),
                )
            },
            bottomBar = {
                CheckoutBottomBar(
                    theme = theme,
                    total = state.total,
                    canSubmit = state.canSubmit,
                    isSubmitting = state.isSubmitting,
                    isScheduledForLater = state.isScheduledForLater,
                    onSubmit = onSubmit,
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
                    bottom = 132.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.isLoadingRewards || state.isSubmitting) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = palette.primary,
                            trackColor = palette.stroke.copy(alpha = 0.45f),
                        )
                    }
                }

                state.errorMessage?.let { message ->
                    item { ErrorCardInline(theme, message, onDismissError) }
                }

                item { CheckoutClientCard(theme, profile) }

                item {
                    TableCard(
                        theme = theme,
                        tableNumber = state.draft.tableNumber,
                        isScheduledForLater = state.isScheduledForLater,
                        onTableNumberChanged = onTableNumberChanged,
                    )
                }

                item {
                    ScheduleCard(
                        theme = theme,
                        scheduledAt = state.draft.scheduledAt,
                        isScheduledForLater = state.isScheduledForLater,
                        onScheduledAtChanged = onScheduledAtChanged,
                        onScheduleNow = onScheduleNow,
                    )
                }

                item { CheckoutItemsCard(theme, state) }

                item {
                    OrderSummaryCard(
                        theme = theme,
                        subtotal = state.subtotal,
                        discount = state.discount,
                        total = state.total,
                        scheduledAt = state.draft.scheduledAt,
                        isScheduledForLater = state.isScheduledForLater,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutClientCard(theme: AppSectionTheme, profile: ClientProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Cliente",
            subtitle = "Estos datos vienen de tu perfil."
        )
        InfoRow(theme, "Nombre", profile.fullName)
        InfoRow(theme, "Cédula", profile.nationalId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableCard(
    theme: AppSectionTheme,
    tableNumber: String,
    isScheduledForLater: Boolean,
    onTableNumberChanged: (String) -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = if (isScheduledForLater) "Mesa o referencia" else "Mesa",
            subtitle = if (isScheduledForLater) {
                "Para reservas posteriores puede quedar vacía; ADM la verá como Por asignar."
            } else {
                "Indica dónde debe llegar el pedido."
            },
        )

        OutlinedTextField(
            value = tableNumber,
            onValueChange = onTableNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (isScheduledForLater) "Mesa, nombre de reserva o referencia" else "Número o nombre de mesa") },
            leadingIcon = { Icon(Icons.Rounded.TableRestaurant, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.Radius.large),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary,
                focusedContainerColor = palette.elevatedCard,
                unfocusedContainerColor = palette.elevatedCard,
                focusedBorderColor = palette.primary,
                unfocusedBorderColor = palette.stroke,
                focusedLabelColor = palette.primary,
                unfocusedLabelColor = palette.textSecondary,
                cursorColor = palette.primary,
            ),
        )
    }
}

@Composable
private fun ScheduleCard(
    theme: AppSectionTheme,
    scheduledAt: Date,
    isScheduledForLater: Boolean,
    onScheduledAtChanged: (Date) -> Unit,
    onScheduleNow: () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val calendar = remember(scheduledAt) { Calendar.getInstance().apply { time = scheduledAt } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Cuándo preparar",
            subtitle = "Reserva solo comida para más tarde sin usar actividades de aventura.",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = if (isScheduledForLater) Icons.Rounded.CalendarMonth else Icons.Rounded.Schedule,
                size = 44.dp,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isScheduledForLater) "Reserva de comida" else "Pedido inmediato",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = OrderScheduleFormatter.displayText(scheduledAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )
            }

            OutlinedButton(
                enabled = isScheduledForLater,
                onClick = onScheduleNow,
            ) { Text("Ahora") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val next = Calendar.getInstance().apply {
                                time = scheduledAt
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time
                            onScheduledAtChanged(next)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                    ).show()
                },
            ) { Text("Fecha") }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val next = Calendar.getInstance().apply {
                                time = scheduledAt
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time
                            onScheduledAtChanged(next)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false,
                    ).show()
                },
            ) { Text("Hora") }
        }

        Text(
            text = "Por defecto es ahora. Si eliges otro día, el pedido se guardará con scheduledAt en restaurant_orders.",
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun CheckoutItemsCard(theme: AppSectionTheme, state: CheckoutUiState) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Productos",
            subtitle = "${state.draft.totalItems} producto(s) seleccionados."
        )

        state.draft.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BrandIconBubble(theme = theme, icon = Icons.Rounded.RestaurantMenu, size = 42.dp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.menuItem.name,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    Text(
                        "x${item.safeQuantity} • ${item.unitPrice.priceLabel()}",
                        color = palette.textSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            item.notes.orEmpty(),
                            color = palette.accent,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    item.totalPrice.priceLabel(),
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            }

            if (index != state.draft.items.lastIndex) {
                HorizontalDivider(color = palette.stroke.copy(alpha = 0.72f))
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    theme: AppSectionTheme,
    subtotal: Double,
    discount: Double,
    total: Double,
    scheduledAt: Date,
    isScheduledForLater: Boolean,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme, emphasized = false),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Resumen",
            subtitle = "Revisa el total antes de enviar."
        )
        SummaryLine("Subtotal", subtotal.priceLabel(), palette.textSecondary, palette.textPrimary)
        if (discount > 0.0) SummaryLine(
            "Beneficios",
            "-${discount.priceLabel()}",
            palette.textSecondary,
            palette.success
        )
        SummaryLine(
            if (isScheduledForLater) "Reserva" else "Hora",
            OrderScheduleFormatter.displayText(scheduledAt),
            palette.textSecondary,
            palette.textPrimary
        )
        HorizontalDivider(color = palette.stroke.copy(alpha = 0.72f))
        SummaryLine(
            "Total",
            total.priceLabel(),
            palette.textPrimary,
            palette.textPrimary,
            emphasized = true
        )
    }
}

@Composable
fun SummaryLine(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    emphasized: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            color = labelColor,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            value,
            color = valueColor,
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoRow(theme: AppSectionTheme, title: String, value: String) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = palette.textSecondary)
        Text(
            value.ifBlank { "Sin registrar" },
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary
        )
    }
}

@Composable
private fun ErrorCardInline(theme: AppSectionTheme, message: String, onDismiss: () -> Unit) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.destructive.copy(alpha = if (darkTheme) 0.18f else 0.10f))
            .border(1.dp, palette.destructive.copy(alpha = 0.35f), shape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = palette.destructive)
        Text(
            message,
            modifier = Modifier.weight(1f),
            color = palette.textPrimary,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Rounded.Close, contentDescription = "Cerrar", tint = palette.textSecondary)
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    theme: AppSectionTheme,
    total: Double,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    isScheduledForLater: Boolean,
    onSubmit: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape =
        RoundedCornerShape(topStart = AppTheme.Radius.xLarge, topEnd = AppTheme.Radius.xLarge)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.cardGradient)
            .border(1.dp, palette.stroke, shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary
                )
                Text(
                    total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary
                )
            }

            BrandPrimaryButton(
                theme = theme,
                enabled = canSubmit && !isSubmitting,
                onClick = onSubmit,
                modifier = Modifier.weight(1.45f),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = palette.onPrimary,
                        trackColor = Color.Transparent
                    )
                } else {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = palette.onPrimary
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = when {
                        isSubmitting -> "Enviando..."
                        isScheduledForLater -> "Reservar comida"
                        else -> "Enviar pedido"
                    },
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun Double.priceLabel(): String = NumberFormat.getCurrencyInstance(Locale.US).format(this)
