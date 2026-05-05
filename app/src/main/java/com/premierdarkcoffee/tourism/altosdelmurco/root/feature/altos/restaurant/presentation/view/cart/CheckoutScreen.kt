package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.TableRestaurant
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    profile: ClientProfile,
    onBack: () -> Unit,
    onTableNumberChanged: (String) -> Unit,
    onWhatsappChanged: (String) -> Unit,
    onScheduledAtChanged: (Date) -> Unit,
    onScheduleNow: () -> Unit,
    onSubmit: (openWhatsAppAfterSubmit: Boolean) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Restaurant
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    var showMissingWhatsAppDialog by rememberSaveable { mutableStateOf(false) }

    val isScheduledWhatsAppMissing =
        state.isScheduledForLater && state.draft.whatsappNumber.filter(Char::isDigit).isEmpty()

    fun handleSubmitTapped() {
        if (isScheduledWhatsAppMissing) {
            showMissingWhatsAppDialog = true
        } else {
            onSubmit(false)
        }
    }

    if (showMissingWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showMissingWhatsAppDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMissingWhatsAppDialog = false
                        onSubmit(true)
                    },
                ) {
                    Text("Enviar y escribir por WhatsApp", color = palette.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMissingWhatsAppDialog = false }) {
                    Text("Agregar WhatsApp aquí")
                }
            },
            title = { Text("Confirmar por WhatsApp") },
            text = {
                Text("Puedes enviar la reserva sin número. Al finalizar abriremos WhatsApp para que nos escribas.")
            },
            containerColor = palette.elevatedCard,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
        )
    }

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
                    onSubmit = { handleSubmitTapped() },
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

                if (state.isScheduledForLater) {
                    item {
                        CheckoutContactCard(
                            theme = theme,
                            whatsappNumber = state.draft.whatsappNumber,
                            onWhatsappChanged = onWhatsappChanged,
                        )
                    }
                }

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
                
                item {
                    CheckoutItemsCard(theme, state)
                }

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
            subtitle = "Solo el nombre es obligatorio. Los datos de contacto son opcionales."
        )
        InfoRow(theme, "Nombre", profile.fullName)
    }
}

@Composable
private fun CheckoutContactCard(
    theme: AppSectionTheme,
    whatsappNumber: String,
    onWhatsappChanged: (String) -> Unit,
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
            title = "Contacto para confirmar",
            subtitle = "Solo se usa para reservas programadas. Puedes dejarlo vacío y escribirnos por WhatsApp después de enviar.",
        )

        OutlinedTextField(
            value = whatsappNumber,
            onValueChange = onWhatsappChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("WhatsApp opcional") },
            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.Radius.large),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(palette.chipGradient)
                .border(1.dp, palette.stroke, RoundedCornerShape(18.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = palette.primary,
            )

            Text(
                text = if (whatsappNumber.filter(Char::isDigit).isEmpty()) {
                    "Si no ingresas número, enviaremos la reserva y abriremos WhatsApp para que nos escribas."
                } else {
                    "Usaremos este número únicamente para confirmar la reserva programada."
                },
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCard(
    theme: AppSectionTheme,
    scheduledAt: Date,
    isScheduledForLater: Boolean,
    onScheduledAtChanged: (Date) -> Unit,
    onScheduleNow: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "¿Cuándo quieres tu comida?",
            subtitle = "Elige si debemos prepararla ahora o reservarla para más tarde.",
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ScheduleModeOption(
                theme = theme,
                title = "Preparar ahora",
                subtitle = "El restaurante recibe el pedido como inmediato. La mesa es obligatoria y WhatsApp no se guarda.",
                icon = Icons.Rounded.Schedule,
                selected = !isScheduledForLater,
                onClick = onScheduleNow,
            )

            ScheduleModeOption(
                theme = theme,
                title = "Programar para después",
                subtitle = "Reserva solo comida para una fecha y hora específica. La mesa puede quedar por asignar.",
                icon = Icons.Rounded.CalendarMonth,
                selected = isScheduledForLater,
                onClick = {
                    onScheduledAtChanged(
                        scheduledAt.coerceToFutureRestaurantSchedule()
                    )
                },
            )
        }

        if (isScheduledForLater) {
            ScheduledOrderPanel(
                theme = theme,
                scheduledAt = scheduledAt,
                onScheduleNow = onScheduleNow,
                onPickDate = { showDatePicker = true },
                onPickTime = { showTimePicker = true },
            )
        } else {
            ImmediateOrderPanel(
                theme = theme,
                scheduledAt = scheduledAt,
            )
        }
    }

    if (showDatePicker) {
        ScheduleDatePickerDialog(
            scheduledAt = scheduledAt.coerceToFutureRestaurantSchedule(),
            onDismiss = { showDatePicker = false },
            onDateSelected = { next ->
                onScheduledAtChanged(next.coerceToFutureRestaurantSchedule())
                showDatePicker = false
            },
        )
    }

    if (showTimePicker) {
        ScheduleTimePickerDialog(
            scheduledAt = scheduledAt.coerceToFutureRestaurantSchedule(),
            onDismiss = { showTimePicker = false },
            onTimeSelected = { next ->
                onScheduledAtChanged(next.coerceToFutureRestaurantSchedule())
                showTimePicker = false
            },
        )
    }
}

@Composable
private fun ScheduleModeOption(
    theme: AppSectionTheme,
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) {
                    palette.primary.copy(alpha = if (darkTheme) 0.22f else 0.10f)
                } else {
                    palette.elevatedCard
                }
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) palette.primary else palette.stroke,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BrandIconBubble(
            theme = theme,
            icon = icon,
            size = 44.dp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                if (selected) {
                    Text(
                        text = "Seleccionado",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                }
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )
        }

        Icon(
            imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) palette.primary else palette.textTertiary,
        )
    }
}

@Composable
private fun ImmediateOrderPanel(
    theme: AppSectionTheme,
    scheduledAt: Date,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.chipGradient)
            .border(1.dp, palette.stroke, RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BrandIconBubble(
            theme = theme,
            icon = Icons.Rounded.Schedule,
            size = 42.dp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = "Se preparará lo antes posible",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )

            Text(
                text = "Este pedido se enviará como inmediato. La mesa es obligatoria para que podamos llevar la comida correctamente.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )

            Text(
                text = "Hora estimada: ${OrderScheduleFormatter.displayText(scheduledAt)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
            )
        }
    }
}

@Composable
private fun ScheduledOrderPanel(
    theme: AppSectionTheme,
    scheduledAt: Date,
    onScheduleNow: () -> Unit,
    onPickDate: () -> Unit,
    onPickTime: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val warning = restaurantScheduleWarning(scheduledAt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.chipGradient)
            .border(1.dp, palette.stroke, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = Icons.Rounded.CalendarMonth,
                size = 42.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "Comida programada",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                Text(
                    text = "Prepararemos o confirmaremos tu comida para la fecha elegida. La mesa puede quedar por asignar y WhatsApp ayuda a confirmar disponibilidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                )

                Text(
                    text = OrderScheduleFormatter.displayText(scheduledAt),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }

            OutlinedButton(onClick = onScheduleNow) {
                Text("Ahora")
            }
        }

        HorizontalDivider(color = palette.stroke)

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onPickDate,
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar fecha")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onPickTime,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar hora")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = if (warning == null) Icons.Rounded.CheckCircle else Icons.Rounded.WarningAmber,
                contentDescription = null,
                tint = if (warning == null) palette.success else palette.destructive,
            )

            Text(
                text = warning
                    ?: "Esta reserva se guardará con scheduledAt en restaurant_orders para que ADM la vea como comida programada.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun Date.coerceToFutureRestaurantSchedule(now: Date = Date()): Date {
    val minimum = Calendar.getInstance().apply {
        time = now
        add(Calendar.MINUTE, 30)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.roundToNextQuarterHour()

    val candidate = if (before(minimum)) minimum else this
    return candidate.roundToNextQuarterHour()
}

private fun Date.roundToNextQuarterHour(): Date {
    return Calendar.getInstance().apply {
        time = this@roundToNextQuarterHour
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        val minute = get(Calendar.MINUTE)
        val remainder = minute % 15
        if (remainder != 0) {
            add(Calendar.MINUTE, 15 - remainder)
        }
    }.time
}

private fun restaurantScheduleWarning(
    scheduledAt: Date,
    now: Date = Date(),
): String? {
    val minimum = Calendar.getInstance().apply {
        time = now
        add(Calendar.MINUTE, 30)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.roundToNextQuarterHour()

    if (scheduledAt.before(minimum)) {
        return "Programa tu pedido con al menos 30 minutos de anticipación."
    }

    val calendar = Calendar.getInstance().apply { time = scheduledAt }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val minutesFromStartOfDay = hour * 60 + minute

    if (minutesFromStartOfDay !in (7 * 60)..(20 * 60)) {
        return "Elige una hora entre 7:00 y 20:00 para evitar confusiones con cocina."
    }

    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDatePickerDialog(
    scheduledAt: Date,
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit,
) {
    val initialSelectedDateMillis = remember(scheduledAt.time) {
        scheduledAt.toDatePickerUtcMillis()
    }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = currentYear..(currentYear + 2),
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis

                    if (selectedMillis != null) {
                        onDateSelected(
                            scheduledAt.withDateFromDatePickerMillis(selectedMillis)
                        )
                    } else {
                        onDismiss()
                    }
                },
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "Fecha de preparación",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 12.dp,
                        top = 16.dp,
                    ),
                )
            },
            headline = {
                Text(
                    text = "Selecciona el día",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 12.dp,
                        bottom = 12.dp,
                    ),
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    scheduledAt: Date,
    onDismiss: () -> Unit,
    onTimeSelected: (Date) -> Unit,
) {
    val calendar = remember(scheduledAt.time) {
        Calendar.getInstance().apply { time = scheduledAt }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Hora de preparación")
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(
                        scheduledAt.withTime(
                            hourOfDay = timePickerState.hour,
                            minute = timePickerState.minute,
                        )
                    )
                },
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

private fun Date.toDatePickerUtcMillis(): Long {
    val localCalendar = Calendar.getInstance().apply {
        time = this@toDatePickerUtcMillis
    }

    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            localCalendar.get(Calendar.YEAR),
            localCalendar.get(Calendar.MONTH),
            localCalendar.get(Calendar.DAY_OF_MONTH),
            0,
            0,
            0,
        )
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun Date.withDateFromDatePickerMillis(selectedMillis: Long): Date {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = selectedMillis
    }

    return Calendar.getInstance().apply {
        time = this@withDateFromDatePickerMillis
        set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
        set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

private fun Date.withTime(
    hourOfDay: Int,
    minute: Int,
): Date {
    return Calendar.getInstance().apply {
        time = this@withTime
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
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
            theme = theme, title = "Resumen", subtitle = "Revisa el total antes de enviar."
        )
        SummaryLine("Subtotal", subtotal.priceLabel(), palette.textSecondary, palette.textPrimary)
        if (discount > 0.0) SummaryLine(
            "Beneficios", "-${discount.priceLabel()}", palette.textSecondary, palette.success
        )
        SummaryLine(
            if (isScheduledForLater) "Reserva" else "Hora",
            OrderScheduleFormatter.displayText(scheduledAt),
            palette.textSecondary,
            palette.textPrimary
        )
        HorizontalDivider(color = palette.stroke.copy(alpha = 0.72f))
        SummaryLine(
            "Total", total.priceLabel(), palette.textPrimary, palette.textPrimary, emphasized = true
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

@Composable
fun CheckoutModeExplanationCard(
    isScheduledForLater: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current
    val title = if (isScheduledForLater) "Reserva de comida" else "Pedido inmediato"
    val body = if (isScheduledForLater) {
        "Elegiremos o confirmaremos la mesa antes de tu visita. También confirmamos disponibilidad de platos antes de preparar."
    } else {
        "La mesa es obligatoria y el stock se confirma ahora para preparar tu pedido."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.chipGradient)
            .border(1.dp, palette.stroke, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text(body, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
        Text(
            "Confirmamos precios, disponibilidad y premios actuales al enviar.",
            style = MaterialTheme.typography.labelSmall,
            color = palette.primary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun Double.priceLabel(): String = NumberFormat.getCurrencyInstance(Locale.US).format(this)
