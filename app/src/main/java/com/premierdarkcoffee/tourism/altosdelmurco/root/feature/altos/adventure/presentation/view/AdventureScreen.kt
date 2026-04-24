package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationEventType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationServingMoment
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel.AdventureCatalogViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel.AdventureComboBuilderViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import java.util.Calendar
import java.util.Date

private sealed interface AdventureMode {
    data object Catalog : AdventureMode
    data object Builder : AdventureMode
}

@Composable
fun AdventureScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    catalogViewModel: AdventureCatalogViewModel = hiltViewModel(),
    builderViewModel: AdventureComboBuilderViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
) {
    val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()
    val builderState by builderViewModel.uiState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    var mode by remember { mutableStateOf<AdventureMode>(AdventureMode.Catalog) }
    var showFoodPicker by remember { mutableStateOf(false) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        catalogViewModel.onAppear()
        builderViewModel.onAppear(sessionState.profile)
        menuViewModel.onAppear(sessionState.profile.nationalId)
    }

    if (showFoodPicker) {
        AdventureFoodPickerSheet(
            menuSections = menuState.sections,
            selectedDate = builderState.selectedDate,
            rewardPresentationProvider = builderViewModel::foodPickerRewardPresentation,
            displayedPriceProvider = builderViewModel::foodPickerDisplayedPrice,
            incrementalDiscountProvider = builderViewModel::foodPickerIncrementalDiscount,
            onDismiss = { showFoodPicker = false },
            onAdd = { item, quantity, notes ->
                builderViewModel.addFoodItem(item, quantity, notes)
            },
        )
    }

    val message =
        builderState.errorMessage ?: builderState.successMessage ?: catalogState.errorMessage
    if (message != null) {
        AlertDialog(
            onDismissRequest = {
                builderViewModel.dismissMessage()
                catalogViewModel.clearError()
            },
            confirmButton = {
                TextButton(onClick = {
                    builderViewModel.dismissMessage()
                    catalogViewModel.clearError()
                }) { Text("OK") }
            },
            title = { Text("Mensaje") },
            text = { Text(message) },
        )
    }

    when (mode) {
        AdventureMode.Catalog -> AdventureCatalogContent(
            modifier = modifier,
            isLoading = catalogState.isLoading,
            catalog = catalogState.catalog,
            menuSections = menuState.sections,
            builderViewModel = builderViewModel,
            onCustomCombo = {
                builderViewModel.prepareCustomDraftIfNeeded()
                mode = AdventureMode.Builder
            },
            onOpenSingle = { activity ->
                builderViewModel.replaceItems(listOf(activity.defaultDraft), 0.0)
                mode = AdventureMode.Builder
            },
            onOpenPackage = { packageModel ->
                builderViewModel.replacePackage(packageModel, menuState.sections)
                mode = AdventureMode.Builder
            },
        )

        AdventureMode.Builder -> AdventureBuilderContent(
            modifier = modifier,
            viewModel = builderViewModel,
            menuSections = menuState.sections,
            onBack = { mode = AdventureMode.Catalog },
            onAddFood = { showFoodPicker = true },
            clientId = sessionState.profile.id,
        )
    }
}

@Composable
private fun AdventureCatalogContent(
    isLoading: Boolean,
    catalog: com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    builderViewModel: AdventureComboBuilderViewModel,
    onCustomCombo: () -> Unit,
    onOpenSingle: (AdventureActivityCatalogItem) -> Unit,
    onOpenPackage: (AdventureFeaturedPackage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        AdventureGradientHero(
            title = "Construye tu combo perfecto",
            subtitle = "Actividades, paquetes destacados, comida del restaurante, horarios y premios Murco Loyalty en una sola reserva.",
            action = {
                Button(
                    onClick = onCustomCombo,
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar combo personalizado")
                }
            },
        )

        if (isLoading && catalog.activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AdventureSectionTitle(
                title = "Paquetes destacados",
                subtitle = "Combos sugeridos cargados desde Firestore.",
            )
            if (catalog.activePackagesSorted.isEmpty()) {
                AdventureEmptyState(
                    title = "No hay paquetes destacados",
                    body = "Cuando actives paquetes en Firestore aparecerán aquí.",
                    icon = Icons.Rounded.Explore,
                )
            } else {
                catalog.activePackagesSorted.forEach { packageModel ->
                    FeaturedPackageCard(
                        packageModel = packageModel,
                        catalog = catalog,
                        menuSections = menuSections,
                        reward = builderViewModel.packageRewardPresentation(
                            packageModel,
                            menuSections
                        ),
                        onClick = { onOpenPackage(packageModel) },
                    )
                }
            }

            AdventureSectionTitle(
                title = "Actividades individuales",
                subtitle = "Reserva una actividad o úsala como base para tu combo.",
            )
            catalog.activeActivitiesSorted.forEach { activity ->
                SingleActivityCard(
                    activity = activity,
                    reward = builderViewModel.catalogRewardPresentation(activity),
                    onClick = { onOpenSingle(activity) },
                )
            }

            AdventureCard {
                AdventureSectionTitle(
                    title = "¿Necesitas algo diferente?",
                    subtitle = "Crea una combinación a medida con tiempos, personas, comida y notas del evento.",
                )
                OutlinedButton(onClick = onCustomCombo, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.Explore, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir creador de aventuras")
                }
            }
        }
    }
}

@Composable
private fun FeaturedPackageCard(
    packageModel: AdventureFeaturedPackage,
    catalog: com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    reward: com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation?,
    onClick: () -> Unit,
) {
    val menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id }
    val activitySubtotal = AdventurePricingEngine.estimatedSubtotal(packageModel.items, catalog)
    val foodSubtotal = packageModel.foodItems.sumOf { food ->
        (menuItemsById[food.menuItemId]?.finalPrice ?: 0.0) * food.quantity
    }
    val total =
        (activitySubtotal + foodSubtotal - packageModel.packageDiscountAmount).coerceAtLeast(0.0)
    val foodSummary = packageModel.foodItems.joinToString(" • ") { food ->
        "${food.quantity}x ${menuItemsById[food.menuItemId]?.name ?: food.menuItemId}"
    }

    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = Icons.Rounded.Explore)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        packageModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    packageModel.badge?.takeIf { it.isNotBlank() }
                        ?.let { AdventureBadge(text = it) }
                }
                Text(
                    packageModel.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (foodSummary.isNotBlank()) Text(
                    foodSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Aventura ${activitySubtotal.priceText()}${if (foodSubtotal > 0) " • Comida ${foodSubtotal.priceText()}" else ""}",
                    style = MaterialTheme.typography.labelMedium
                )
                if (packageModel.packageDiscountAmount > 0) Text(
                    "Descuento del paquete: ${packageModel.packageDiscountAmount.priceText()}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                reward?.let {
                    Text(
                        "${it.badge}: ${it.message}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Desde ${total.priceText()} • Ver combo")
        }
    }
}

@Composable
private fun SingleActivityCard(
    activity: AdventureActivityCatalogItem,
    reward: com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation?,
    onClick: () -> Unit,
) {
    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = adventureIconFor(activity.activityType))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    activity.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Desde ${activity.finalUnitPrice.priceText()}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (activity.hasDiscount) {
                        Text(
                            "Antes ${activity.basePrice.priceText()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                reward?.let {
                    Text(
                        "${it.badge}: ${it.message}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Reservar")
        }
    }
}

@Composable
private fun AdventureBuilderContent(
    viewModel: AdventureComboBuilderViewModel,
    menuSections: List<MenuSection>,
    onBack: () -> Unit,
    onAddFood: () -> Unit,
    clientId: String?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingItem by remember { mutableStateOf<AdventureReservationItemDraft?>(null) }
    var editingFood by remember { mutableStateOf<ReservationFoodItemDraft?>(null) }

    editingItem?.let { item ->
        AdventureItemEditorDialog(
            item = item,
            config = viewModel.config(item.activity),
            onDismiss = { editingItem = null },
            onSave = {
                viewModel.updateItem(it)
                editingItem = null
            },
        )
    }

    editingFood?.let { item ->
        FoodItemEditorDialog(
            item = item,
            onDismiss = { editingFood = null },
            onSave = {
                viewModel.updateFoodItem(it)
                editingFood = null
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.submit(clientId) },
                    enabled = !state.isSubmitting && state.selectedSlot != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isSubmitting) CircularProgressIndicator() else Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.isSubmitting) "Confirmando..." else "Confirmar reserva")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Crear reserva",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }

            if (state.isLoadingCatalog || state.isLoadingAvailability || state.isLoadingRewards) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            AdventureDateAndSlotsSection(viewModel = viewModel)
            AdventureEventSection(viewModel = viewModel)
            AdventureActivitiesSection(viewModel = viewModel, onEditItem = { editingItem = it })
            AdventureFoodSection(
                viewModel = viewModel,
                onAddFood = onAddFood,
                onEditFood = { editingFood = it })
            AdventureContactSection(viewModel = viewModel)
            AdventureSummarySection(viewModel = viewModel)
            Spacer(Modifier.height(84.dp))
        }
    }
}

@Composable
private fun AdventureDateAndSlotsSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    AdventureCard {
        AdventureSectionTitle("Fecha", "Elige el día de visita y luego un horario disponible.")
        Button(
            onClick = {
                val calendar = Calendar.getInstance().apply { time = state.selectedDate }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = Calendar.getInstance().apply {
                            set(year, month, day, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        viewModel.setDate(picked.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            },
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(AdventureDateHelper.shortDateText(state.selectedDate))
        }
    }

    AdventureCard {
        AdventureSectionTitle("Horarios disponibles", "Selecciona inicio o llegada preferida.")
        if (state.isLoadingAvailability) {
            CircularProgressIndicator()
        } else if (state.availableSlots.isEmpty()) {
            Text(
                "Agrega una actividad o comida, o prueba otra fecha.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.availableSlots.forEach { slot ->
                    SlotChip(
                        slot = slot,
                        selected = state.selectedSlot?.startAt == slot.startAt,
                        total = viewModel.effectiveTotal(slot),
                        onClick = { viewModel.selectSlot(slot) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotChip(
    slot: AdventureAvailabilitySlot,
    selected: Boolean,
    total: Double,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Text(AdventureDateHelper.timeText(slot.startAt), fontWeight = FontWeight.Bold)
                Text("Termina ${AdventureDateHelper.timeText(slot.endAt)}")
                Text(
                    total.priceText(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
    )
}

@Composable
private fun AdventureEventSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Evento", "Invitados, tipo de evento y notas especiales.")
        CounterRow(
            title = "Invitados",
            value = state.guestCount,
            onDecrease = { viewModel.setGuestCount(state.guestCount - 1) },
            onIncrease = { viewModel.setGuestCount(state.guestCount + 1) })
        EnumDropdown(
            title = "Tipo de evento",
            current = state.eventType,
            values = ReservationEventType.entries,
            label = { it.title },
            onSelected = viewModel::setEventType,
        )
        if (state.eventType == ReservationEventType.CUSTOM) {
            OutlinedTextField(
                value = state.customEventTitle,
                onValueChange = viewModel::setCustomEventTitle,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del evento") })
        }
        OutlinedTextField(
            value = state.eventNotes,
            onValueChange = viewModel::setEventNotes,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Notas del evento") })
    }
}

@Composable
private fun AdventureActivitiesSection(
    viewModel: AdventureComboBuilderViewModel,
    onEditItem: (AdventureReservationItemDraft) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle(
            "Actividades",
            "Opcionales. Puedes reservar aventura, comida o ambas."
        )
        if (state.items.isEmpty()) {
            Text(
                "No hay actividades agregadas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.items.forEach { item ->
                ActivityDraftRow(
                    item = item,
                    viewModel = viewModel,
                    onEdit = { onEditItem(item) },
                    onDelete = { viewModel.removeItem(item.id) })
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.availableActivitiesToAdd.forEach { activity ->
                AssistChip(
                    onClick = { viewModel.addItem(activity.activityType) },
                    label = { Text("+ ${activity.title}") })
            }
        }
    }
}

@Composable
private fun ActivityDraftRow(
    item: AdventureReservationItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AdventureIconBubble(icon = adventureIconFor(item.activity))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold)
            Text(
                item.summaryText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val base = viewModel.baseAdventureSubtotal(item)
            val shown = viewModel.displayedAdventureSubtotal(item)
            Text(
                if (shown < base) "${base.priceText()} → ${shown.priceText()}" else base.priceText(),
                color = MaterialTheme.colorScheme.primary
            )
            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, contentDescription = "Editar") }
        IconButton(onClick = onDelete) { Icon(Icons.Rounded.Delete, contentDescription = "Quitar") }
    }
}

@Composable
private fun AdventureFoodSection(
    viewModel: AdventureComboBuilderViewModel,
    onAddFood: () -> Unit,
    onEditFood: (ReservationFoodItemDraft) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Comida", "Agrega platos del restaurante a la reserva.")
        if (state.foodItems.isEmpty()) {
            Text(
                "No hay platos agregados todavía.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.foodItems.forEach { item ->
                FoodDraftRow(item = item, viewModel = viewModel, onEdit = { onEditFood(item) })
            }
            Divider()
            EnumDropdown(
                title = "Momento de servicio",
                current = state.foodServingMoment,
                values = ReservationServingMoment.entries,
                label = { it.title },
                onSelected = viewModel::setFoodServingMoment,
            )
            if (state.foodServingMoment == ReservationServingMoment.SPECIFIC_TIME) {
                val context = LocalContext.current
                Button(onClick = {
                    val calendar = Calendar.getInstance().apply { time = state.foodServingTime }
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val picked = Calendar.getInstance().apply {
                                time = state.foodServingTime
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            viewModel.setFoodServingTime(picked.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false,
                    ).show()
                }) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Hora: ${AdventureDateHelper.timeText(state.foodServingTime)}")
                }
            }
            OutlinedTextField(
                value = state.foodNotes,
                onValueChange = viewModel::setFoodNotes,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                label = { Text("Notas de comida") })
        }
        OutlinedButton(onClick = onAddFood, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Restaurant, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar comida")
        }
    }
}

@Composable
private fun FoodDraftRow(
    item: ReservationFoodItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AdventureIconBubble(icon = Icons.Rounded.LocalDining)
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text(
                "${item.quantity} x ${item.unitPrice.priceText()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                viewModel.displayedFoodSubtotal(item).priceText(),
                color = MaterialTheme.colorScheme.primary
            )
            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = { viewModel.decreaseFoodQuantity(item.id) }) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = "Menos"
            )
        }
        IconButton(onClick = { viewModel.increaseFoodQuantity(item.id) }) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Más"
            )
        }
        IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, contentDescription = "Editar") }
        IconButton(onClick = { viewModel.removeFoodItem(item.id) }) {
            Icon(
                Icons.Rounded.Delete,
                contentDescription = "Quitar"
            )
        }
    }
}

@Composable
private fun AdventureContactSection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Contacto", "Datos sincronizados desde tu perfil.")
        ContactLine(Icons.Rounded.Person, "Nombre", state.clientName)
        ContactLine(Icons.Rounded.Phone, "WhatsApp", state.whatsappNumber)
        ContactLine(Icons.Rounded.Event, "Cédula", state.nationalId)
        OutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::setNotes,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Notas generales") })
    }
}

@Composable
private fun ContactLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(value.ifBlank { "No registrado" }, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AdventureSummarySection(viewModel: AdventureComboBuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdventureCard {
        AdventureSectionTitle("Resumen", "Revisa el total antes de confirmar.")
        val slot = state.selectedSlot
        if (slot != null) {
            AdventurePriceRow("Aventura", slot.adventureSubtotal)
            AdventurePriceRow("Comida", slot.foodSubtotal)
            AdventurePriceRow("Subtotal", slot.subtotal)
            AdventurePriceRow("Descuento aventura", slot.discountAmount, negative = true)
            if (state.rewardPreview.totalDiscount > 0) AdventurePriceRow(
                "Murco Loyalty",
                state.rewardPreview.totalDiscount,
                negative = true
            )
            Divider()
            AdventurePriceRow("Total", viewModel.effectiveTotal(slot), bold = true)
        } else {
            AdventurePriceRow("Aventura estimada", viewModel.estimatedAdventureSubtotal)
            AdventurePriceRow("Comida estimada", viewModel.estimatedFoodSubtotal)
            AdventurePriceRow(
                "Descuento estimado",
                viewModel.estimatedDiscountAmount,
                negative = true
            )
            Divider()
            AdventurePriceRow("Total estimado", viewModel.estimatedTotal, bold = true)
        }
        viewModel.activeRewardPresentations.forEach { reward ->
            Text(
                "${reward.badge}: ${reward.message}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CounterRow(title: String, value: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$title: $value", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onDecrease) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = "Menos"
            )
        }
        IconButton(onClick = onIncrease) { Icon(Icons.Rounded.Add, contentDescription = "Más") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    title: String,
    current: T,
    values: List<T>,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = label(current),
            onValueChange = {},
            readOnly = true,
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEach { value ->
                DropdownMenuItem(text = { Text(label(value)) }, onClick = {
                    onSelected(value)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun AdventureItemEditorDialog(
    item: AdventureReservationItemDraft,
    config: AdventureActivityCatalogItem?,
    onDismiss: () -> Unit,
    onSave: (AdventureReservationItemDraft) -> Unit
) {
    var draft by remember(item.id) { mutableStateOf(item) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(draft) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(config?.title ?: item.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (item.activity != AdventureActivityType.CAMPING) {
                    DurationSelector(
                        draft = draft,
                        options = config?.durationOptions ?: item.activity.legacyDurationOptions,
                        onChanged = { draft = draft.copy(durationMinutes = it) })
                }
                when (item.activity) {
                    AdventureActivityType.OFF_ROAD -> {
                        CounterRow(
                            "Vehículos",
                            draft.vehicleCount,
                            {
                                draft = draft.copy(
                                    vehicleCount = (draft.vehicleCount - 1).coerceAtLeast(1),
                                    offRoadRiderCount = draft.offRoadRiderCount.coerceAtMost(
                                        ((draft.vehicleCount - 1).coerceAtLeast(1)) * 2
                                    )
                                )
                            },
                            { draft = draft.copy(vehicleCount = draft.vehicleCount + 1) })
                        CounterRow(
                            "Personas",
                            draft.offRoadRiderCount,
                            {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount - 1).coerceAtLeast(
                                        1
                                    )
                                )
                            },
                            {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount + 1).coerceAtMost(
                                        draft.vehicleCount * 2
                                    )
                                )
                            })
                    }

                    AdventureActivityType.CAMPING -> {
                        CounterRow(
                            "Noches",
                            draft.nights,
                            { draft = draft.copy(nights = (draft.nights - 1).coerceAtLeast(1)) },
                            { draft = draft.copy(nights = draft.nights + 1) })
                        CounterRow(
                            "Personas",
                            draft.peopleCount,
                            {
                                draft =
                                    draft.copy(peopleCount = (draft.peopleCount - 1).coerceAtLeast(1))
                            },
                            { draft = draft.copy(peopleCount = draft.peopleCount + 1) })
                    }

                    else -> CounterRow(
                        "Personas",
                        draft.peopleCount,
                        {
                            draft =
                                draft.copy(peopleCount = (draft.peopleCount - 1).coerceAtLeast(1))
                        },
                        { draft = draft.copy(peopleCount = draft.peopleCount + 1) })
                }
            }
        },
    )
}

@Composable
private fun DurationSelector(
    draft: AdventureReservationItemDraft,
    options: List<Int>,
    onChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.distinct().sorted().forEach { minutes ->
            FilterChip(
                selected = draft.durationMinutes == minutes,
                onClick = { onChanged(minutes) },
                label = { Text(if (minutes >= 60) "${minutes / 60}h" else "$minutes min") })
        }
    }
}

@Composable
private fun FoodItemEditorDialog(
    item: ReservationFoodItemDraft,
    onDismiss: () -> Unit,
    onSave: (ReservationFoodItemDraft) -> Unit
) {
    var draft by remember(item.id) { mutableStateOf(item) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(draft) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar comida") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(draft.name, fontWeight = FontWeight.Bold)
                CounterRow(
                    "Cantidad",
                    draft.quantity,
                    { draft = draft.copy(quantity = (draft.quantity - 1).coerceAtLeast(1)) },
                    { draft = draft.copy(quantity = draft.quantity + 1) })
                OutlinedTextField(
                    value = draft.notes.orEmpty(),
                    onValueChange = {
                        draft = draft.copy(notes = it.trim().takeIf { value -> value.isNotEmpty() })
                    },
                    minLines = 2,
                    label = { Text("Notas") })
            }
        },
    )
}
