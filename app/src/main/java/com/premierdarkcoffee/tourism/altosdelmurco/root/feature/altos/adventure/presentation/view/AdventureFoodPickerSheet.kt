package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view.RemoteBitmapImage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.text.Normalizer
import java.util.Date
import java.util.Locale

private val adventureFoodCategoryDisplayOrder = listOf(
    "Entradas",
    "Sopas",
    "Platos Fuertes",
    "Extras",
    "Postres",
    "Bebidas",
    "Bebidas Alcohólicas",
)

private data class AdventureFoodStep(
    val number: Int,
    val category: MenuCategory,
    val itemCount: Int,
) {
    val subtitle: String
        get() = when (category.title) {
            "Entradas" -> "Para abrir"
            "Sopas" -> "Calientitas"
            "Platos Fuertes" -> "La estrella"
            "Extras" -> "Complementos"
            "Postres" -> "Final dulce"
            "Bebidas" -> "Refrescos"
            "Bebidas Alcohólicas" -> "Acompañantes"
            else -> "Explorar"
        }
}

private data class AdventureFoodGroup(
    val id: String,
    val title: String,
    val subtitle: String,
    val items: List<MenuItem>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureFoodPickerSheet(
    menuSections: List<MenuSection>,
    selectedDate: Date,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onDismiss: () -> Unit,
    onAdd: (MenuItem, Int, String?) -> Unit,
) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val isTodayReservation = remember(selectedDate.time) {
        AdventureDateHelper.isDateInToday(selectedDate)
    }

    val orderedSections = remember(menuSections, isTodayReservation) {
        menuSections.map { section ->
            section.copy(
                items = section.items.filter { item -> if (isTodayReservation) item.canBeOrdered else item.isAvailable }
                    .sortedWith(
                        compareByDescending<MenuItem> { it.isFeatured }.thenBy { it.sortOrder }
                            .thenBy { it.name.lowercase(Locale.getDefault()) },
                    ),
            )
        }.sortedWith(
            compareBy<MenuSection> { section ->
                adventureFoodCategoryDisplayOrder.indexOf(section.category.title)
                    .takeIf { it >= 0 } ?: Int.MAX_VALUE
            }.thenBy { it.category.title },
        )
    }

    val categories = remember(orderedSections) {
        orderedSections.map { it.category }.distinctBy { it.id }
    }

    LaunchedEffect(categories) {
        val current = selectedCategoryId
        if (current == null || categories.none { it.id == current }) {
            selectedCategoryId = categories.firstOrNull()?.id
        }
    }

    val selectedCategory = remember(categories, selectedCategoryId) {
        categories.firstOrNull { it.id == selectedCategoryId } ?: categories.firstOrNull()
    }

    val allItems = remember(orderedSections) {
        orderedSections.flatMap { it.items }
    }

    val cleanSearch = searchText.trim()
    val isSearching = cleanSearch.isNotEmpty()

    val searchResults = remember(allItems, cleanSearch) {
        if (cleanSearch.isBlank()) {
            emptyList()
        } else {
            val query = cleanSearch.normalizedSearchKey()
            allItems.filter { item -> item.normalizedSearchText().contains(query) }.sortedWith(
                compareByDescending<MenuItem> { it.canBeOrdered }.thenByDescending { it.isFeatured }
                    .thenBy { it.sortOrder }.thenBy { it.name.lowercase(Locale.getDefault()) },
            )
        }
    }

    val featuredItems = remember(allItems) {
        allItems.filter { it.isFeatured && it.canBeOrdered }
            .sortedWith(compareBy<MenuItem> { it.sortOrder }.thenBy { it.name }).take(8)
    }

    val steps = remember(orderedSections) {
        orderedSections.mapIndexed { index, section ->
            AdventureFoodStep(
                number = index + 1,
                category = section.category,
                itemCount = section.items.size,
            )
        }
    }

    val selectedSection = remember(orderedSections, selectedCategory) {
        selectedCategory?.let { category ->
            orderedSections.firstOrNull { it.category.id == category.id }
        } ?: orderedSections.firstOrNull()
    }

    val selectedGroups = remember(selectedSection) {
        selectedSection?.let(::adventureFoodGroups).orEmpty()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.surface,
        contentColor = palette.textPrimary,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.94f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 8.dp,
                bottom = 30.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                AdventureFoodPickerHeader(onDismiss = onDismiss)
            }

            item {
                AdventureFoodSearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                )
            }

            if (isSearching) {
                item {
                    BrandSectionHeader(
                        theme = theme,
                        title = if (searchResults.isEmpty()) "Sin resultados" else "Resultados",
                        subtitle = if (searchResults.isEmpty()) {
                            "No encontré \"$cleanSearch\". Prueba con cuy, sopa, jugo o parrillada."
                        } else {
                            "Encontré ${searchResults.size} coincidencia(s) para \"$cleanSearch\"."
                        },
                    )
                }

                if (searchResults.isEmpty()) {
                    item { AdventureFoodEmptyState() }
                } else {
                    items(searchResults, key = { it.id }) { item ->
                        PremiumAdventureFoodCard(
                            item = item,
                            selectedDate = selectedDate,
                            rewardPresentationProvider = rewardPresentationProvider,
                            displayedPriceProvider = displayedPriceProvider,
                            incrementalDiscountProvider = incrementalDiscountProvider,
                            onAdd = { quantity, notes ->
                                onAdd(item, quantity, notes)
                                onDismiss()
                            },
                        )
                    }
                }
            } else {
//                if (featuredItems.isNotEmpty()) {
//                    item {
//                        AdventureFoodFeaturedRail(
//                            featuredItems = featuredItems,
//                            selectedDate = selectedDate,
//                            rewardPresentationProvider = rewardPresentationProvider,
//                            displayedPriceProvider = displayedPriceProvider,
//                            incrementalDiscountProvider = incrementalDiscountProvider,
//                            onAdd = { item, quantity, notes ->
//                                onAdd(item, quantity, notes)
//                                onDismiss()
//                            },
//                        )
//                    }
//                }

                item {
                    AdventureFoodStepSelector(
                        steps = steps,
                        selectedCategoryId = selectedCategory?.id,
                        onSelected = { selectedCategoryId = it },
                    )
                }

                item {
                    BrandSectionHeader(
                        theme = theme,
                        title = selectedCategory?.title ?: "Menú",
                        subtitle = selectedCategorySubtitle(selectedCategory?.title.orEmpty()),
                    )
                }

                if (selectedGroups.isEmpty()) {
                    item { AdventureFoodEmptyState() }
                } else {
                    selectedGroups.forEach { group ->
                        item(key = group.id) {
                            AdventureFoodGroupBlock(
                                group = group,
                                selectedDate = selectedDate,
                                rewardPresentationProvider = rewardPresentationProvider,
                                displayedPriceProvider = displayedPriceProvider,
                                incrementalDiscountProvider = incrementalDiscountProvider,
                                onAdd = { item, quantity, notes ->
                                    onAdd(item, quantity, notes)
                                    onDismiss()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdventureFoodPickerHeader(onDismiss: () -> Unit) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(palette.heroGradient)
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(30.dp))
            .padding(20.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(126.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f)),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Reserva también la comida",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )

                    Text(
                        text = "Elige platos antes de salir, al volver de la ruta o para una hora específica.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.92f),
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdventureFoodMiniBadge("Paso a paso")
                AdventureFoodMiniBadge("Buscar")
                AdventureFoodMiniBadge("Fotos")
            }
        }
    }
}

@Composable
private fun AdventureFoodMiniBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 7.dp),
    )
}

@Composable
private fun AdventureFoodSearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val palette = LocalBrandPalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        },
        label = { Text("Buscar plato, bebida o ingrediente") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            disabledTextColor = palette.textTertiary,
            focusedContainerColor = palette.elevatedCard,
            unfocusedContainerColor = palette.elevatedCard,
            disabledContainerColor = palette.card,
            cursorColor = palette.primary,
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.stroke,
            disabledBorderColor = palette.stroke.copy(alpha = 0.55f),
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,
            focusedLeadingIconColor = palette.primary,
            unfocusedLeadingIconColor = palette.textSecondary,
        ),
    )
}

//@Composable
//private fun AdventureFoodFeaturedRail(
//    featuredItems: List<MenuItem>,
//    selectedDate: Date,
//    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
//    displayedPriceProvider: (MenuItem, Int) -> Double,
//    incrementalDiscountProvider: (MenuItem, Int) -> Double,
//    onAdd: (MenuItem, Int, String?) -> Unit,
//) {
//    val theme = AppSectionTheme.Adventure
//
//    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
//        BrandSectionHeader(
//            theme = theme,
//            title = "Favoritos para acompañar",
//            subtitle = "Platos destacados para completar una visita premium sin pensar demasiado.",
//        )
//
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(14.dp),
//        ) {
//            items(featuredItems, key = { it.id }) { item ->
//                Box(modifier = Modifier.width(260.dp)) {
//                    PremiumAdventureFoodCard(
//                        item = item,
//                        selectedDate = selectedDate,
//                        rewardPresentationProvider = rewardPresentationProvider,
//                        displayedPriceProvider = displayedPriceProvider,
//                        incrementalDiscountProvider = incrementalDiscountProvider,
//                        onAdd = { quantity, notes -> onAdd(item, quantity, notes) },
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
private fun AdventureFoodStepSelector(
    steps: List<AdventureFoodStep>,
    selectedCategoryId: String?,
    onSelected: (String) -> Unit,
) {
    val theme = AppSectionTheme.Adventure

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Arma el servicio por momentos",
            subtitle = "Primero entrada, luego sopa, plato fuerte, extras, postre y bebida. Sin una lista eterna.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            steps.forEach { step ->
                AdventureFoodStepCard(
                    step = step,
                    selected = selectedCategoryId == step.category.id,
                    onClick = { onSelected(step.category.id) },
                )
            }
        }
    }
}

@Composable
private fun AdventureFoodStepCard(
    step: AdventureFoodStep,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .width(152.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .background(if (selected) palette.heroGradient else palette.cardGradient)
            .border(
                1.dp, if (selected) Color.Transparent else palette.stroke, RoundedCornerShape(22.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) Color.White.copy(alpha = 0.18f) else palette.primary.copy(
                            alpha = 0.12f
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = step.number.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) palette.onPrimary else palette.primary,
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = step.itemCount.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (selected) palette.onPrimary else palette.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) Color.White.copy(alpha = 0.14f) else palette.elevatedCard)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
            )
        }

        Text(
            text = step.category.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
            color = if (selected) palette.onPrimary else palette.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = step.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) palette.onPrimary.copy(alpha = 0.84f) else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AdventureFoodGroupBlock(
    group: AdventureFoodGroup,
    selectedDate: Date,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onAdd: (MenuItem, Int, String?) -> Unit,
) {
    val palette = LocalBrandPalette.current
    val theme = AppSectionTheme.Adventure

    Column(
        modifier = Modifier.appCardStyle(theme = theme, emphasized = false),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = palette.textPrimary,
                )

                Text(
                    text = group.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                )
            }

            Text(
                text = group.items.size.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }

        group.items.forEach { item ->
            PremiumAdventureFoodCard(
                item = item,
                selectedDate = selectedDate,
                rewardPresentationProvider = rewardPresentationProvider,
                displayedPriceProvider = displayedPriceProvider,
                incrementalDiscountProvider = incrementalDiscountProvider,
                onAdd = { quantity, notes -> onAdd(item, quantity, notes) },
            )
        }
    }
}

@Composable
private fun PremiumAdventureFoodCard(
    item: MenuItem,
    selectedDate: Date,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onAdd: (Int, String?) -> Unit,
) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    var quantity by rememberSaveable(item.id) { mutableIntStateOf(1) }
    var notes by rememberSaveable(item.id) { mutableStateOf("") }

    val blockedToday = AdventureDateHelper.isDateInToday(selectedDate) && !item.canBeOrdered
    val baseSubtotal = item.finalPrice * quantity
    val displayedTotal = displayedPriceProvider(item, quantity)
    val incrementalDiscount = incrementalDiscountProvider(item, quantity)
    val rewardPresentation = rewardPresentationProvider(item, quantity)
    val totalToShow = if (incrementalDiscount > 0) displayedTotal else baseSubtotal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardGradient)
            .border(1.dp, palette.stroke, RoundedCornerShape(24.dp))
            .alpha(if (blockedToday) 0.58f else 1f)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(154.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.chipGradient),
        ) {
            RemoteBitmapImage(
                url = item.imageURL,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholderIcon = Icons.Rounded.Restaurant,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.58f),
                            ),
                        ),
                    ),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                if (item.isFeatured) {
                    AdventureFoodImageBadge(text = "Popular")
                }
                if (item.hasOffer) {
                    AdventureFoodImageBadge(text = "Oferta")
                }
            }

            Text(
                text = item.categoryTitle.ifBlank { "Menú" },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.34f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(verticalAlignment = Alignment.Top) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = palette.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                        maxLines = if (expanded) 4 else 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (item.hasOffer) {
                    Text(
                        text = item.price.priceText(),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textTertiary,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }

                Text(
                    text = item.finalPrice.priceText(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = palette.primary,
                )
            }
        }

        rewardPresentation?.let { reward ->
            AdventureFoodRewardRibbon(reward = reward)
        }

        when {
            blockedToday -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = palette.destructive,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Agotado para hoy",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.destructive,
                    )
                }
            }

            AdventureDateHelper.isDateInToday(selectedDate) -> {
                Text(
                    text = item.stockLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            item.isAvailable && !item.canBeOrdered -> {
                Text(
                    text = "Reservable para fecha futura",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalDivider(color = palette.stroke)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IconButton(
                        onClick = { quantity = (quantity - 1).coerceAtLeast(1) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(palette.chipGradient)
                            .border(1.dp, palette.stroke, CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RemoveCircle,
                            contentDescription = "Menos",
                            tint = palette.primary,
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )

                    IconButton(
                        onClick = { quantity += 1 },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(palette.chipGradient)
                            .border(1.dp, palette.stroke, CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = "Más",
                            tint = palette.primary,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.End) {
                        if (incrementalDiscount > 0) {
                            Text(
                                text = baseSubtotal.priceText(),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textTertiary,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }

                        Text(
                            text = totalToShow.priceText(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = palette.primary,
                        )
                    }
                }

                AdventureFoodNotesField(
                    value = notes,
                    onValueChange = { notes = it },
                )
            }
        }

        if (expanded) {
            BrandPrimaryButton(
                theme = theme,
                onClick = { onAdd(quantity, notes.trim().takeIf { it.isNotEmpty() }) },
                enabled = !blockedToday,
            ) {
                Text(
                    text = "Agregar a la reserva",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            TextButton(
                onClick = { expanded = true },
                enabled = !blockedToday,
            ) {
                Text(
                    text = "Elegir cantidad y notas",
                    color = if (blockedToday) palette.textTertiary else palette.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AdventureFoodImageBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.34f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
    )
}

@Composable
private fun AdventureFoodRewardRibbon(reward: RewardPresentation) {
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.chipGradient)
            .border(1.dp, palette.stroke, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        BrandIconBubble(
            theme = AppSectionTheme.Adventure,
            icon = Icons.Rounded.LocalDining,
            size = 34.dp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = reward.badge,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = palette.primary,
            )

            Text(
                text = reward.message,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AdventureFoodNotesField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val palette = LocalBrandPalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 3,
        label = { Text("Notas opcionales") },
        shape = RoundedCornerShape(AppTheme.Radius.large),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            focusedContainerColor = palette.elevatedCard,
            unfocusedContainerColor = palette.elevatedCard,
            cursorColor = palette.primary,
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.stroke,
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,
        ),
    )
}

@Composable
private fun AdventureFoodEmptyState() {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BrandIconBubble(
            theme = theme,
            icon = Icons.Rounded.Search,
            size = 54.dp,
        )

        Text(
            text = "No se encontraron platos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )

        Text(
            text = "Prueba otra búsqueda o cambia de categoría.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
        )
    }
}

private fun selectedCategorySubtitle(title: String): String = when (title) {
    "Entradas" -> "Algo pequeño para recibir a tus invitados antes de la experiencia."
    "Sopas" -> "Caliente, serrano y perfecto si llegan con frío o hambre."
    "Platos Fuertes" -> "Agrupado para decidir rápido sin bajar una pared infinita."
    "Extras" -> "Complementos para que la mesa quede completa."
    "Postres" -> "Un cierre dulce después de comer o después de la ruta."
    "Bebidas" -> "Bebidas listas para recibir al grupo sin improvisar."
    "Bebidas Alcohólicas" -> "Opciones para acompañar con responsabilidad."
    else -> "Selecciona lo que quieres agregar a la reserva."
}

private fun adventureFoodGroups(section: MenuSection): List<AdventureFoodGroup> {
    val items = section.items
    if (items.isEmpty()) return emptyList()

    if (section.category.title == "Platos Fuertes") {
        val share = items.filter {
            it.matchesAny(
                "parrillada", "familiar", "para dos", "compartir", "altos"
            )
        }
        val house = items.filter {
            it.matchesAny(
                "cuy",
                "borrego",
                "costilla",
                "jack",
                "andina"
            ) && share.none { shareItem -> shareItem.id == it.id }
        }
        val classic = items.filter { item ->
            share.none { it.id == item.id } && house.none { it.id == item.id }
        }

        return listOf(
            AdventureFoodGroup(
                id = "share",
                title = "Para compartir",
                subtitle = "Parrilladas y platos grandes para pareja, familia o amigos.",
                items = share,
            ),
            AdventureFoodGroup(
                id = "house",
                title = "Especialidades de la casa",
                subtitle = "Los platos que hacen que la visita se sienta Altos del Murco.",
                items = house,
            ),
            AdventureFoodGroup(
                id = "classic",
                title = "Más platos fuertes",
                subtitle = "Otras opciones contundentes para completar la reserva.",
                items = classic,
            ),
        ).filter { it.items.isNotEmpty() }
    }

    val featured = items.filter { it.isFeatured || it.hasOffer }
    val rest = items.filter { item -> featured.none { it.id == item.id } }

    if (featured.isEmpty()) {
        return listOf(
            AdventureFoodGroup(
                id = section.id,
                title = "Opciones disponibles",
                subtitle = "${items.size} opción(es) para este momento.",
                items = items,
            ),
        )
    }

    return listOf(
        AdventureFoodGroup(
            id = "featured-${section.id}",
            title = "Recomendados",
            subtitle = "Los más atractivos de esta categoría.",
            items = featured,
        ),
        AdventureFoodGroup(
            id = "all-${section.id}",
            title = "También puedes pedir",
            subtitle = "Más opciones para completar la mesa.",
            items = rest,
        ),
    ).filter { it.items.isNotEmpty() }
}

private fun MenuItem.matchesAny(vararg keys: String): Boolean {
    val text = (listOf(name, description) + ingredients).joinToString(" ").normalizedSearchKey()
    return keys.any { key -> text.contains(key.normalizedSearchKey()) }
}

private fun MenuItem.normalizedSearchText(): String =
    (listOf(name, description, categoryTitle) + ingredients).joinToString(" ").normalizedSearchKey()

private fun String.normalizedSearchKey(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.getDefault())
