package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
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
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSecondaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalAppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import java.net.URLEncoder
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

private sealed interface AdventureMode {
    data object Catalog : AdventureMode
    data object Builder : AdventureMode
}

private val AdventureTheme = AppSectionTheme.Adventure

@Composable
fun AdventureComboBuilderScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    catalogViewModel: AdventureCatalogViewModel = hiltViewModel(),
    builderViewModel: AdventureComboBuilderViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
    homePrefillPackageId: String? = null,
    onHomePrefillPackageConsumed: () -> Unit = {},
) {
    val darkTheme = LocalBrandDarkTheme.current
    val adventurePalette = AppTheme.palette(AdventureTheme, darkTheme)

    CompositionLocalProvider(
        LocalAppSectionTheme provides AdventureTheme,
        LocalBrandPalette provides adventurePalette,
    ) {
        AdventureScreenContent(
            sessionState = sessionState,
            modifier = modifier,
            catalogViewModel = catalogViewModel,
            builderViewModel = builderViewModel,
            menuViewModel = menuViewModel,
            homePrefillPackageId = homePrefillPackageId,
            onHomePrefillPackageConsumed = onHomePrefillPackageConsumed,
        )
    }
}

@Composable
private fun AdventureScreenContent(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    catalogViewModel: AdventureCatalogViewModel,
    builderViewModel: AdventureComboBuilderViewModel,
    menuViewModel: MenuViewModel,
    homePrefillPackageId: String?,
    onHomePrefillPackageConsumed: () -> Unit,
) {
    val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()
    val builderState by builderViewModel.uiState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()

    val palette = LocalBrandPalette.current

    var mode by remember { mutableStateOf<AdventureMode>(AdventureMode.Catalog) }
    var showFoodPicker by remember { mutableStateOf(false) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        catalogViewModel.onAppear()
        builderViewModel.onAppear(sessionState.profile)
        menuViewModel.onAppear(sessionState.profile.userId)
    }

    LaunchedEffect(
        homePrefillPackageId,
        catalogState.catalog.activePackagesSorted,
        menuState.sections,
    ) {
        val packageId = homePrefillPackageId?.trim().orEmpty()
        if (packageId.isEmpty()) return@LaunchedEffect

        val packageModel = catalogState.catalog.activePackagesSorted
            .firstOrNull { it.id == packageId }
            ?: return@LaunchedEffect

        val hasAllFoodData = packageModel.foodItems.isEmpty() || menuState.sections.isNotEmpty()
        if (!hasAllFoodData) return@LaunchedEffect

        builderViewModel.replacePackage(packageModel, menuState.sections)
        mode = AdventureMode.Builder
        onHomePrefillPackageConsumed()
    }

    DisposableEffect(Unit) {
        onDispose {
            catalogViewModel.onDisappear()
            builderViewModel.onDisappear()
        }
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

    val message = builderState.errorMessage
        ?: builderState.successMessage
        ?: catalogState.errorMessage

    if (message != null) {
        AlertDialog(
            onDismissRequest = {
                builderViewModel.dismissMessage()
                catalogViewModel.clearError()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        builderViewModel.dismissMessage()
                        catalogViewModel.clearError()
                    },
                ) {
                    Text("OK", color = palette.primary)
                }
            },
            title = {
                Text(
                    text = "Mensaje",
                    color = palette.textPrimary,
                )
            },
            text = {
                Text(
                    text = message,
                    color = palette.textSecondary,
                )
            },
            containerColor = palette.elevatedCard,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
        )
    }

    BrandScreen(
        theme = AdventureTheme,
        modifier = modifier,
    ) {
        when (mode) {
            AdventureMode.Catalog -> AdventureCatalogContent(
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
                viewModel = builderViewModel,
                menuSections = menuState.sections,
                onBack = { mode = AdventureMode.Catalog },
                onAddFood = { showFoodPicker = true },
                userId = sessionState.profile.userId,
            )
        }
    }
}

@Composable
private fun AdventureCatalogContent(
    isLoading: Boolean,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    builderViewModel: AdventureComboBuilderViewModel,
    onCustomCombo: () -> Unit,
    onOpenSingle: (AdventureActivityCatalogItem) -> Unit,
    onOpenPackage: (AdventureFeaturedPackage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

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
                BrandPrimaryButton(
                    theme = AdventureTheme,
                    onClick = onCustomCombo,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = palette.onPrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Iniciar combo personalizado",
                        color = palette.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
        )

        if (isLoading && catalog.activities.isEmpty()) {
            AdventureLoadingCard()
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
                            menuSections,
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

                BrandSecondaryButton(
                    theme = AdventureTheme,
                    onClick = onCustomCombo,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = null,
                        tint = palette.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Abrir creador de aventuras",
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedPackageCard(
    packageModel: AdventureFeaturedPackage,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    val menuItemsById = menuSections
        .flatMap { it.items }
        .associateBy { it.id }

    val activitySubtotal = AdventurePricingEngine.estimatedSubtotal(packageModel.items, catalog)
    val foodSubtotal = packageModel.foodItems.sumOf { food ->
        (menuItemsById[food.menuItemId]?.finalPrice ?: 0.0) * food.quantity
    }

    val total = (activitySubtotal + foodSubtotal - packageModel.packageDiscountAmount)
        .coerceAtLeast(0.0)

    val foodSummary = packageModel.foodItems.joinToString(" • ") { food ->
        "${food.quantity}x ${menuItemsById[food.menuItemId]?.name ?: food.menuItemId}"
    }

    AdventureCard(emphasized = true) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AdventureIconBubble(icon = Icons.Rounded.Explore)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = packageModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f),
                    )

                    packageModel.badge
                        ?.takeIf { it.isNotBlank() }
                        ?.let { AdventureBadge(text = it) }
                }

                Text(
                    text = packageModel.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )

                if (foodSummary.isNotBlank()) {
                    Text(
                        text = foodSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                    )
                }

                Text(
                    text = "Aventura ${activitySubtotal.priceText()}${
                        if (foodSubtotal > 0) " • Comida ${foodSubtotal.priceText()}" else ""
                    }",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textTertiary,
                )

                if (packageModel.packageDiscountAmount > 0) {
                    Text(
                        text = "Descuento del paquete: ${packageModel.packageDiscountAmount.priceText()}",
                        color = palette.success,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                reward?.let {
                    Text(
                        text = "${it.badge}: ${it.message}",
                        color = palette.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        BrandPrimaryButton(
            theme = AdventureTheme,
            onClick = onClick,
        ) {
            Text(
                text = "Desde ${total.priceText()} • Ver combo",
                color = palette.onPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SingleActivityCard(
    activity: AdventureActivityCatalogItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AdventureIconBubble(icon = adventureIconFor(activity.activityType))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                Text(
                    text = activity.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Desde ${activity.finalUnitPrice.priceText()}",
                        color = palette.primary,
                        fontWeight = FontWeight.Bold,
                    )

                    if (activity.hasDiscount) {
                        Text(
                            text = "Antes ${activity.basePrice.priceText()}",
                            color = palette.textTertiary,
                            textDecoration = TextDecoration.LineThrough,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                reward?.let {
                    Text(
                        text = "${it.badge}: ${it.message}",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        BrandSecondaryButton(
            theme = AdventureTheme,
            onClick = onClick,
        ) {
            Text(
                text = "Reservar",
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AdventureBuilderContent(
    viewModel: AdventureComboBuilderViewModel,
    menuSections: List<MenuSection>,
    onBack: () -> Unit,
    onAddFood: () -> Unit,
    userId: String?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = LocalBrandPalette.current
    val context = LocalContext.current

    var editingItem by remember { mutableStateOf<AdventureReservationItemDraft?>(null) }
    var showMissingWhatsAppDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.openWhatsAppAfterSubmit.collect {
            context.openAltosWhatsAppForAdventureConfirmation()
        }
    }

    fun handleSubmitTapped() {
        when {
            state.clientName.trim().isEmpty() -> {
                viewModel.presentError("Ingresa tu nombre para enviar la reserva.")
            }

            state.whatsappNumber.filter(Char::isDigit).isEmpty() -> {
                showMissingWhatsAppDialog = true
            }

            else -> {
                viewModel.submit(userId)
            }
        }
    }

    if (showMissingWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showMissingWhatsAppDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMissingWhatsAppDialog = false
                        viewModel.submit(userId, openWhatsAppAfterSubmit = true)
                    },
                ) {
                    Text("Enviar y escribir por WhatsApp", color = palette.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMissingWhatsAppDialog = false }) {
                    Text("Agregar WhatsApp aquí", color = palette.textSecondary)
                }
            },
            title = {
                Text("Confirmar por WhatsApp", color = palette.textPrimary)
            },
            text = {
                Text(
                    text = "Puedes enviar la reserva sin número. Al finalizar abriremos WhatsApp para que nos escribas.",
                    color = palette.textSecondary,
                )
            },
            containerColor = palette.elevatedCard,
        )
    }

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
        containerColor = Color.Transparent,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface.copy(alpha = 0.92f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                BrandPrimaryButton(
                    theme = AdventureTheme,
                    onClick = { handleSubmitTapped() },
                    enabled = !state.isSubmitting && state.selectedSlot != null,
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = palette.onPrimary,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = palette.onPrimary,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = if (state.isSubmitting) "Confirmando..." else "Confirmar reserva",
                        color = palette.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Crear reserva",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Configura actividades, comida, horario y descuentos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary,
                    )
                }

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        tint = palette.textPrimary,
                    )
                }
            }

            if (state.isLoadingCatalog || state.isLoadingAvailability || state.isLoadingRewards) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = palette.primary,
                    trackColor = palette.stroke,
                )
            }

            AdventureDateAndSlotsSection(viewModel = viewModel)
            AdventureEventSection(viewModel = viewModel)
            AdventureActivitiesSection(
                viewModel = viewModel,
                onEditItem = { editingItem = it },
            )
            AdventureFoodSection(
                viewModel = viewModel,
                onAddFood = onAddFood,
                onEditFood = { editingFood = it },
            )
            AdventureContactSection(viewModel = viewModel)
            AdventureSummarySection(viewModel = viewModel)

            Spacer(Modifier.height(84.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdventureDateAndSlotsSection(
    viewModel: AdventureComboBuilderViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = LocalBrandPalette.current

    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    AdventureCard {
        AdventureSectionTitle(
            title = "Fecha",
            subtitle = "Elige el día de visita y luego un horario disponible.",
        )

        BrandSecondaryButton(
            theme = AdventureTheme,
            onClick = { showDatePicker = true },
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = null,
                tint = palette.primary,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = AdventureDateHelper.shortDateText(state.selectedDate),
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

    AdventureCard {
        AdventureSectionTitle(
            title = "Horarios sugeridos",
            subtitle = "Elige una hora preferida. Confirmaremos disponibilidad final por WhatsApp si hay cambios.",
        )

        when {
            state.isLoadingAvailability -> {
                CircularProgressIndicator(color = palette.primary)
            }

            state.availableSlots.isEmpty() -> {
                Text(
                    text = "Agrega una actividad o comida para ver horarios sugeridos.",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            else -> {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.availableSlots.forEach { slot ->
                        SlotChip(
                            slot = slot,
                            selected = state.selectedSlot?.startAt == slot.startAt,
                            total = viewModel.effectiveTotal(slot),
                            hasValidCombo = viewModel.hasValidCombo,
                            totalSavings = viewModel.totalSavings,
                            matchedPackageTitle = viewModel.matchedPackageTitle,
                            onClick = { viewModel.selectSlot(slot) },
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        AdventureComposeDatePickerDialog(
            selectedDate = state.selectedDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { pickedDate ->
                viewModel.setDate(pickedDate)
                showDatePicker = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdventureComposeDatePickerDialog(
    selectedDate: Date,
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit,
) {
    val initialSelectedDateMillis = remember(selectedDate.time) {
        selectedDate.toDatePickerUtcMillis()
    }

    val todayUtcMillis = remember {
        Date().toDatePickerUtcMillis()
    }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = currentYear..currentYear + 2,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayUtcMillis
            }
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis

                    if (selectedMillis != null) {
                        onDateSelected(selectedMillis.toLocalDateFromDatePickerMillis())
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
                    text = "Fecha de visita",
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

private fun Long.toLocalDateFromDatePickerMillis(): Date {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = this@toLocalDateFromDatePickerMillis
    }

    return Calendar.getInstance().apply {
        clear()
        set(
            utcCalendar.get(Calendar.YEAR),
            utcCalendar.get(Calendar.MONTH),
            utcCalendar.get(Calendar.DAY_OF_MONTH),
            0,
            0,
            0,
        )
        set(Calendar.MILLISECOND, 0)
    }.time
}

@Composable
private fun SlotChip(
    slot: AdventureAvailabilitySlot,
    selected: Boolean,
    total: Double,
    hasValidCombo: Boolean,
    totalSavings: Double,
    matchedPackageTitle: String?,
    onClick: () -> Unit,
) {
    AdventureSelectableChip(
        selected = selected,
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(vertical = 6.dp),
        ) {
            Text(
                text = AdventureDateHelper.timeText(slot.startAt),
                fontWeight = FontWeight.Bold,
            )
            Text("Termina ${AdventureDateHelper.timeText(slot.endAt)}")
            Text(
                text = total.priceText(),
                fontWeight = FontWeight.Bold,
            )

            if (hasValidCombo) {
                Text(
                    text = "Combo ${matchedPackageTitle ?: "activo"} • Ahorras ${totalSavings.priceText()}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun AdventureEventSection(
    viewModel: AdventureComboBuilderViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AdventureCard {
        AdventureSectionTitle(
            title = "Evento",
            subtitle = "Invitados, tipo de evento y notas especiales.",
        )

        CounterRow(
            title = "Invitados",
            value = state.guestCount,
            onDecrease = { viewModel.setGuestCount(state.guestCount - 1) },
            onIncrease = { viewModel.setGuestCount(state.guestCount + 1) },
        )

        EnumDropdown(
            title = "Tipo de evento",
            current = state.eventType,
            values = ReservationEventType.entries,
            label = { it.title },
            onSelected = viewModel::setEventType,
        )

        if (state.eventType == ReservationEventType.CUSTOM) {
            AdventureOutlinedTextField(
                value = state.customEventTitle,
                onValueChange = viewModel::setCustomEventTitle,
                label = "Nombre del evento",
            )
        }

        AdventureOutlinedTextField(
            value = state.eventNotes,
            onValueChange = viewModel::setEventNotes,
            label = "Notas del evento",
            minLines = 2,
        )
    }
}

@Composable
private fun AdventureActivitiesSection(
    viewModel: AdventureComboBuilderViewModel,
    onEditItem: (AdventureReservationItemDraft) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = LocalBrandPalette.current

    AdventureCard {
        AdventureSectionTitle(
            title = "Actividades",
            subtitle = "Opcionales. Puedes reservar aventura, comida o ambas.",
        )

        if (state.items.isEmpty()) {
            Text(
                text = "No hay actividades agregadas.",
                color = palette.textSecondary,
            )
        } else {
            state.items.forEach { item ->
                ActivityDraftRow(
                    item = item,
                    viewModel = viewModel,
                    onEdit = { onEditItem(item) },
                    onDelete = { viewModel.removeItem(item.id) },
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            viewModel.availableActivitiesToAdd.forEach { activity ->
                AdventureAssistChip(
                    text = "+ ${activity.title}",
                    onClick = { viewModel.addItem(activity.activityType) },
                )
            }
        }
    }
}

@Composable
private fun ActivityDraftRow(
    item: AdventureReservationItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        AdventureIconBubble(icon = adventureIconFor(item.activity))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )

            Text(
                text = item.summaryText,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )

            val base = viewModel.baseAdventureSubtotal(item)
            val shown = viewModel.displayedAdventureSubtotal(item)

            Text(
                text = if (shown < base) {
                    "${base.priceText()} → ${shown.priceText()}"
                } else {
                    base.priceText()
                },
                color = palette.primary,
                fontWeight = FontWeight.SemiBold,
            )

            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    text = it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.primary,
                )
            }
        }

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Editar",
                tint = palette.textSecondary,
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Quitar",
                tint = palette.destructive,
            )
        }
    }
}

@Composable
private fun AdventureFoodSection(
    viewModel: AdventureComboBuilderViewModel,
    onAddFood: () -> Unit,
    onEditFood: (ReservationFoodItemDraft) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = LocalBrandPalette.current

    AdventureCard {
        AdventureSectionTitle(
            title = "Comida",
            subtitle = "Agrega platos del restaurante a la reserva.",
        )

        if (state.foodItems.isEmpty()) {
            Text(
                text = "No hay platos agregados todavía.",
                color = palette.textSecondary,
            )
        } else {
            state.foodItems.forEach { item ->
                FoodDraftRow(
                    item = item,
                    viewModel = viewModel,
                    onEdit = { onEditFood(item) },
                )
            }

            HorizontalDivider(color = palette.stroke)

            EnumDropdown(
                title = "Momento de servicio",
                current = state.foodServingMoment,
                values = ReservationServingMoment.entries,
                label = { it.title },
                onSelected = viewModel::setFoodServingMoment,
            )

            if (state.foodServingMoment == ReservationServingMoment.SPECIFIC_TIME) {
                val context = LocalContext.current

                BrandSecondaryButton(
                    theme = AdventureTheme,
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            time = state.foodServingTime
                        }

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
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = palette.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Hora: ${AdventureDateHelper.timeText(state.foodServingTime)}",
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            AdventureOutlinedTextField(
                value = state.foodNotes,
                onValueChange = viewModel::setFoodNotes,
                label = "Notas de comida",
                minLines = 2,
            )
        }

        BrandSecondaryButton(
            theme = AdventureTheme,
            onClick = onAddFood,
        ) {
            Icon(
                imageVector = Icons.Rounded.Restaurant,
                contentDescription = null,
                tint = palette.primary,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Agregar comida",
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun FoodDraftRow(
    item: ReservationFoodItemDraft,
    viewModel: AdventureComboBuilderViewModel,
    onEdit: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        AdventureIconBubble(icon = Icons.Rounded.LocalDining)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )

            Text(
                text = "${item.quantity} x ${item.unitPrice.priceText()}",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
            )

            Text(
                text = viewModel.displayedFoodSubtotal(item).priceText(),
                color = palette.primary,
                fontWeight = FontWeight.SemiBold,
            )

            viewModel.appliedRewardPresentation(item)?.let {
                Text(
                    text = it.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.primary,
                )
            }
        }

        IconButton(onClick = { viewModel.decreaseFoodQuantity(item.id) }) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "Menos",
                tint = palette.textSecondary,
            )
        }

        IconButton(onClick = { viewModel.increaseFoodQuantity(item.id) }) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Más",
                tint = palette.primary,
            )
        }

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Editar",
                tint = palette.textSecondary,
            )
        }

        IconButton(onClick = { viewModel.removeFoodItem(item.id) }) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Quitar",
                tint = palette.destructive,
            )
        }
    }
}

@Composable
private fun AdventureContactSection(
    viewModel: AdventureComboBuilderViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AdventureCard {
        AdventureSectionTitle(
            title = "Contacto",
            subtitle = "Solo el nombre es obligatorio. WhatsApp es opcional.",
        )

        AdventureOutlinedTextField(
            value = state.clientName,
            onValueChange = viewModel::setClientName,
            label = "Nombre para la reserva",
        )

        AdventureOutlinedTextField(
            value = state.whatsappNumber,
            onValueChange = viewModel::setWhatsapp,
            label = "WhatsApp opcional",
        )

        Text(
            text = if (state.clientName.trim().isEmpty()) {
                "Necesitamos un nombre para identificar tu reserva."
            } else {
                "Puedes dejar el número vacío y escribirnos por WhatsApp después de enviar la reserva."
            },
            color = LocalBrandPalette.current.textSecondary,
            style = MaterialTheme.typography.bodySmall,
        )

        AdventureOutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::setNotes,
            label = "Notas generales",
            minLines = 2,
        )
    }
}

@Composable
private fun ContactLine(
    icon: ImageVector,
    title: String,
    value: String,
) {
    val palette = LocalBrandPalette.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.primary,
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
            )

            Text(
                text = value.ifBlank { "No registrado" },
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
        }
    }
}

@Composable
private fun AdventureSummarySection(
    viewModel: AdventureComboBuilderViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val breakdown = viewModel.currentPricingBreakdown
    val palette = LocalBrandPalette.current

    AdventureCard(emphasized = true) {
        AdventureSectionTitle(
            title = "Resumen",
            subtitle = "Revisa el total antes de confirmar.",
        )

        val slot = state.selectedSlot

        if (slot != null) {
            AdventurePriceRow("Aventura", slot.adventureSubtotal)
            AdventurePriceRow("Comida", slot.foodSubtotal)
            AdventurePriceRow(
                "Subtotal sin combo ni loyalty",
                viewModel.subtotalBeforeComboAndLoyalty
            )

            if (breakdown.activityDiscountAmount > 0) {
                AdventurePriceRow(
                    label = "Descuento de actividades",
                    amount = breakdown.activityDiscountAmount,
                    negative = true,
                )
            }

            if (viewModel.hasValidCombo && viewModel.comboDiscountAmount > 0) {
                AdventurePriceRow(
                    label = "Descuento combo${
                        viewModel.matchedPackageTitle?.let { " • $it" }.orEmpty()
                    }",
                    amount = viewModel.comboDiscountAmount,
                    negative = true,
                )
            } else if (state.items.size == 1) {
                Text(
                    text = "Una sola actividad no cuenta como combo. Se mantienen descuentos individuales y premios disponibles.",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )
            }

            if (state.rewardPreview.totalDiscount > 0) {
                AdventurePriceRow(
                    label = "Murco Loyalty",
                    amount = state.rewardPreview.totalDiscount,
                    negative = true,
                )
            }

            if (viewModel.totalSavings > 0) {
                Text(
                    text = "Ahorro total: ${viewModel.totalSavings.priceText()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.success,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (slot.nightPremium > 0.0) {
                Text(
                    text = "Recargo nocturno +${slot.nightPremium.priceText()}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            HorizontalDivider(color = palette.stroke)

            AdventurePriceRow(
                label = "Total",
                amount = viewModel.effectiveTotal(slot),
                bold = true,
            )
        } else {
            AdventurePriceRow(
                "Aventura estimada",
                breakdown.activitySubtotalAfterIndividualDiscounts
            )
            AdventurePriceRow("Comida estimada", breakdown.foodSubtotal)
            AdventurePriceRow(
                "Subtotal sin combo ni loyalty",
                viewModel.subtotalBeforeComboAndLoyalty
            )

            if (breakdown.activityDiscountAmount > 0) {
                AdventurePriceRow(
                    label = "Descuento de actividades",
                    amount = breakdown.activityDiscountAmount,
                    negative = true,
                )
            }

            if (viewModel.hasValidCombo && viewModel.comboDiscountAmount > 0) {
                AdventurePriceRow(
                    label = "Descuento combo${
                        viewModel.matchedPackageTitle?.let { " • $it" }.orEmpty()
                    }",
                    amount = viewModel.comboDiscountAmount,
                    negative = true,
                )
            }

            if (state.rewardPreview.totalDiscount > 0) {
                AdventurePriceRow(
                    label = "Murco Loyalty",
                    amount = state.rewardPreview.totalDiscount,
                    negative = true,
                )
            }

            if (viewModel.totalSavings > 0) {
                Text(
                    text = "Ahorro estimado: ${viewModel.totalSavings.priceText()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.success,
                    fontWeight = FontWeight.Bold,
                )
            }

            HorizontalDivider(color = palette.stroke)

            AdventurePriceRow(
                label = "Total estimado",
                amount = viewModel.estimatedTotal,
                bold = true,
            )
        }

        if (breakdown.extraItems.isNotEmpty() && breakdown.hasValidCombo) {
            Text(
                text = "Actividades extra calculadas fuera del combo: ${breakdown.extraItems.size}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
            )
        }

        viewModel.activeRewardPresentations.forEach { reward ->
            Text(
                text = "${reward.badge}: ${reward.message}",
                color = palette.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AdventurePriceRow(
    label: String,
    amount: Double,
    negative: Boolean = false,
    bold: Boolean = false,
) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = if (bold) palette.textPrimary else palette.textSecondary,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = if (negative) "-${amount.priceText()}" else amount.priceText(),
            color = when {
                negative -> palette.success
                bold -> palette.primary
                else -> palette.textPrimary
            },
            fontWeight = if (bold || negative) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CounterRow(
    title: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$title: $value",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )

        IconButton(onClick = onDecrease) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "Menos",
                tint = palette.textSecondary,
            )
        }

        IconButton(onClick = onIncrease) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Más",
                tint = palette.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    title: String,
    current: T,
    values: List<T>,
    label: (T) -> String,
    onSelected: (T) -> Unit,
) {
    val palette = LocalBrandPalette.current
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = label(current),
            onValueChange = {},
            readOnly = true,
            label = { Text(title) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = adventureTextFieldColors(),
            shape = RoundedCornerShape(AppTheme.Radius.large),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = palette.elevatedCard,
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label(value),
                            color = palette.textPrimary,
                        )
                    },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AdventureItemEditorDialog(
    item: AdventureReservationItemDraft,
    config: AdventureActivityCatalogItem?,
    onDismiss: () -> Unit,
    onSave: (AdventureReservationItemDraft) -> Unit,
) {
    val palette = LocalBrandPalette.current
    var draft by remember(item.id) { mutableStateOf(item) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(draft) }) {
                Text("Guardar", color = palette.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = palette.textSecondary)
            }
        },
        title = {
            Text(
                text = config?.title ?: item.title,
                color = palette.textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (item.activity != AdventureActivityType.CAMPING) {
                    DurationSelector(
                        draft = draft,
                        options = config?.durationOptions ?: item.activity.legacyDurationOptions,
                        onChanged = { draft = draft.copy(durationMinutes = it) },
                    )
                }

                when (item.activity) {
                    AdventureActivityType.OFF_ROAD -> {
                        CounterRow(
                            title = "Vehículos",
                            value = draft.vehicleCount,
                            onDecrease = {
                                val newVehicleCount = (draft.vehicleCount - 1).coerceAtLeast(1)
                                draft = draft.copy(
                                    vehicleCount = newVehicleCount,
                                    offRoadRiderCount = draft.offRoadRiderCount.coerceAtMost(
                                        newVehicleCount * 2,
                                    ),
                                )
                            },
                            onIncrease = {
                                draft = draft.copy(vehicleCount = draft.vehicleCount + 1)
                            },
                        )

                        CounterRow(
                            title = "Personas",
                            value = draft.offRoadRiderCount,
                            onDecrease = {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount - 1)
                                        .coerceAtLeast(1),
                                )
                            },
                            onIncrease = {
                                draft = draft.copy(
                                    offRoadRiderCount = (draft.offRoadRiderCount + 1)
                                        .coerceAtMost(draft.vehicleCount * 2),
                                )
                            },
                        )
                    }

                    AdventureActivityType.CAMPING -> {
                        CounterRow(
                            title = "Noches",
                            value = draft.nights,
                            onDecrease = {
                                draft = draft.copy(
                                    nights = (draft.nights - 1).coerceAtLeast(1),
                                )
                            },
                            onIncrease = {
                                draft = draft.copy(nights = draft.nights + 1)
                            },
                        )

                        CounterRow(
                            title = "Personas",
                            value = draft.peopleCount,
                            onDecrease = {
                                draft = draft.copy(
                                    peopleCount = (draft.peopleCount - 1).coerceAtLeast(1),
                                )
                            },
                            onIncrease = {
                                draft = draft.copy(peopleCount = draft.peopleCount + 1)
                            },
                        )
                    }

                    else -> {
                        CounterRow(
                            title = "Personas",
                            value = draft.peopleCount,
                            onDecrease = {
                                draft = draft.copy(
                                    peopleCount = (draft.peopleCount - 1).coerceAtLeast(1),
                                )
                            },
                            onIncrease = {
                                draft = draft.copy(peopleCount = draft.peopleCount + 1)
                            },
                        )
                    }
                }
            }
        },
        containerColor = palette.elevatedCard,
        titleContentColor = palette.textPrimary,
        textContentColor = palette.textPrimary,
    )
}

@Composable
private fun DurationSelector(
    draft: AdventureReservationItemDraft,
    options: List<Int>,
    onChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.distinct().sorted().forEach { minutes ->
            AdventureSelectableChip(
                selected = draft.durationMinutes == minutes,
                onClick = { onChanged(minutes) },
            ) {
                Text(
                    text = if (minutes >= 60) "${minutes / 60}h" else "$minutes min",
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun FoodItemEditorDialog(
    item: ReservationFoodItemDraft,
    onDismiss: () -> Unit,
    onSave: (ReservationFoodItemDraft) -> Unit,
) {
    val palette = LocalBrandPalette.current
    var draft by remember(item.id) { mutableStateOf(item) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(draft) }) {
                Text("Guardar", color = palette.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = palette.textSecondary)
            }
        },
        title = {
            Text(
                text = "Editar comida",
                color = palette.textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = draft.name,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                CounterRow(
                    title = "Cantidad",
                    value = draft.quantity,
                    onDecrease = {
                        draft = draft.copy(
                            quantity = (draft.quantity - 1).coerceAtLeast(1),
                        )
                    },
                    onIncrease = {
                        draft = draft.copy(quantity = draft.quantity + 1)
                    },
                )

                AdventureOutlinedTextField(
                    value = draft.notes.orEmpty(),
                    onValueChange = {
                        draft = draft.copy(
                            notes = it.trim().takeIf { value -> value.isNotEmpty() },
                        )
                    },
                    label = "Notas",
                    minLines = 2,
                )
            }
        },
        containerColor = palette.elevatedCard,
        titleContentColor = palette.textPrimary,
        textContentColor = palette.textPrimary,
    )
}

/* -------------------------------------------------------------------------- */
/* Theme adapters for this screen                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun AdventureGradientHero(
    title: String,
    subtitle: String,
    action: @Composable () -> Unit,
) {
    val palette = LocalBrandPalette.current
    val shape = RoundedCornerShape(AppTheme.Radius.xLarge)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = shape,
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = palette.onPrimary,
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onPrimary.copy(alpha = 0.88f),
        )

        action()
    }
}

@Composable
private fun AdventureSectionTitle(
    title: String,
    subtitle: String? = null,
) {
    BrandSectionHeader(
        theme = AdventureTheme,
        title = title,
        subtitle = subtitle,
    )
}

@Composable
private fun AdventureIconBubble(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    BrandIconBubble(
        theme = AdventureTheme,
        icon = icon,
        modifier = modifier,
    )
}

@Composable
private fun AdventureLoadingCard() {
    val palette = LocalBrandPalette.current

    AdventureCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = palette.primary)
        }
    }
}

@Composable
private fun AdventureSelectableChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val palette = LocalBrandPalette.current
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                brush = if (selected) palette.heroGradient else palette.chipGradient,
            )
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else palette.stroke,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contentColor = if (selected) palette.onPrimary else palette.primary

        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides contentColor,
        ) {
            content()
        }
    }
}

@Composable
private fun AdventureAssistChip(
    text: String,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                color = palette.primary,
                fontWeight = FontWeight.SemiBold,
            )
        },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = palette.primary.copy(alpha = 0.10f),
            labelColor = palette.primary,
        ),
        border = androidx.compose.material3.AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = palette.stroke,
        ),
    )
}

@Composable
private fun AdventureOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        label = { Text(label) },
        shape = RoundedCornerShape(AppTheme.Radius.large),
        colors = adventureTextFieldColors(),
    )
}

@Composable
private fun adventureTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = LocalBrandPalette.current.textPrimary,
    unfocusedTextColor = LocalBrandPalette.current.textPrimary,
    disabledTextColor = LocalBrandPalette.current.textTertiary,

    focusedContainerColor = LocalBrandPalette.current.elevatedCard,
    unfocusedContainerColor = LocalBrandPalette.current.elevatedCard,
    disabledContainerColor = LocalBrandPalette.current.card,

    cursorColor = LocalBrandPalette.current.primary,

    focusedBorderColor = LocalBrandPalette.current.primary,
    unfocusedBorderColor = LocalBrandPalette.current.stroke,
    disabledBorderColor = LocalBrandPalette.current.stroke.copy(alpha = 0.55f),

    focusedLabelColor = LocalBrandPalette.current.primary,
    unfocusedLabelColor = LocalBrandPalette.current.textSecondary,

    focusedTrailingIconColor = LocalBrandPalette.current.primary,
    unfocusedTrailingIconColor = LocalBrandPalette.current.textSecondary,
)

private fun Context.openAltosWhatsAppForAdventureConfirmation() {
    val message =
        "Hola Altos del Murco, acabo de enviar una reserva desde la app y quiero confirmar disponibilidad lo antes posible."
    val encoded = URLEncoder.encode(message, "UTF-8")
    val uri = "https://wa.me/593967188093?text=$encoded".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { startActivity(intent) }
}