package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view.RemoteBitmapImage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardRuleType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardTemplate
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuUiState
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandBadge
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.SeasonalHeroSurface
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.SeasonalImageCardBox
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.badgeImageVector
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.rememberCurrentAltosSeasonalTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.restaurantHeroSubtitle
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.restaurantHeroTitle
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.shortPromise
import java.text.NumberFormat
import java.util.Locale

private val restaurantCategoryDisplayOrder = listOf(
    "Entradas",
    "Sopas",
    "Platos Fuertes",
    "Extras",
    "Postres",
    "Bebidas",
    "Bebidas Alcohólicas",
)

private data class DishGroup(
    val id: String,
    val title: String,
    val subtitle: String,
    val items: List<MenuItem>,
)

private data class MenuStep(
    val index: Int,
    val category: MenuCategory,
    val itemCount: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListScreen(
    state: MenuUiState,
    clientName: String,
    levelTitle: String,
    cartItemsCount: Int,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    eligibleItemsProvider: (LoyaltyRewardTemplate) -> List<MenuItem>,
    onCategorySelected: (String?) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    var searchText by rememberSaveable { mutableStateOf("") }

    val orderedSections = remember(state.sections) {
        state.sections
            .sortedWith(
                compareBy<MenuSection> { section ->
                    restaurantCategoryDisplayOrder
                        .indexOf(section.category.title)
                        .takeIf { it >= 0 }
                        ?: Int.MAX_VALUE
                }.thenBy { it.category.title },
            )
    }

    val categories = remember(orderedSections) { orderedSections.map { it.category } }
    val selectedCategoryId = state.selectedCategoryId
    val selectedSection = remember(orderedSections, selectedCategoryId) {
        orderedSections.firstOrNull { it.category.id == selectedCategoryId }
            ?: orderedSections.firstOrNull()
    }

    val menuSteps = remember(orderedSections) {
        orderedSections.mapIndexed { index, section ->
            MenuStep(
                index = index + 1,
                category = section.category,
                itemCount = section.items.count { it.isAvailable },
            )
        }
    }

    val allAvailableItems = remember(orderedSections) {
        orderedSections
            .flatMap { it.items }
            .distinctBy { it.id }
            .filter { it.isAvailable }
    }

    val featuredItems = remember(allAvailableItems) {
        allAvailableItems
            .filter { it.isFeatured && it.canBeOrdered }
            .sortedWith(compareBy<MenuItem> { it.sortOrder }.thenBy { it.name })
    }

    val searchQuery = searchText.trim()
    val searchResults = remember(searchQuery, allAvailableItems) {
        val query = searchQuery.lowercase(Locale.ROOT)
        if (query.isBlank()) {
            emptyList()
        } else {
            allAvailableItems
                .filter { item -> item.matchesMenuQuery(query) }
                .sortedWith(compareByDescending<MenuItem> { it.isFeatured }.thenBy { it.sortOrder }
                    .thenBy { it.name })
        }
    }

    val selectedDishGroups = remember(selectedSection) {
        selectedSection?.dishGroups().orEmpty()
    }

    LaunchedEffect(categories, selectedCategoryId) {
        if (categories.isEmpty()) return@LaunchedEffect
        val selectedStillExists = categories.any { it.id == selectedCategoryId }
        if (selectedCategoryId.isNullOrBlank() || !selectedStillExists) {
            onCategorySelected(categories.first().id)
        }
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
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Sabor de Los Altos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.textPrimary,
                            )

                            Text(
                                text = "Elige por antojo, paso a paso o búsqueda rápida",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenOrders) {
                            Icon(
                                imageVector = Icons.Rounded.ReceiptLong,
                                contentDescription = "Pedidos",
                                tint = palette.textPrimary,
                            )
                        }

                        IconButton(onClick = onOpenCart) {
                            CartIcon(cartItemsCount = cartItemsCount)
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onOpenCart,
                    containerColor = palette.primary,
                    contentColor = palette.onPrimary,
                ) {
                    CartIcon(cartItemsCount = cartItemsCount)
                }
            },
        ) { innerPadding ->
            when {
                state.isLoading && state.sections.isEmpty() -> {
                    LoadingRestaurantState(
                        theme = theme,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }

                state.sections.isEmpty() -> {
                    EmptyRestaurantState(
                        theme = theme,
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
                            bottom = 112.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(22.dp),
                    ) {
                        state.errorMessage?.let { message ->
                            item {
                                ErrorCard(
                                    message = message,
                                    onDismiss = onDismissError,
                                )
                            }
                        }

                        item {
                            RestaurantDiscoveryHero(
                                clientName = clientName,
                                levelTitle = levelTitle,
                                cartItemsCount = cartItemsCount,
                                availableDishesCount = allAvailableItems.count { it.canBeOrdered },
                                onOpenCart = onOpenCart,
                                onOpenOrders = onOpenOrders,
                            )
                        }

                        item {
                            RestaurantSearchField(
                                value = searchText,
                                onValueChange = { searchText = it },
                            )
                        }

                        if (searchQuery.isNotBlank()) {
                            item {
                                SearchResultsSection(
                                    query = searchQuery,
                                    results = searchResults,
                                    rewardProvider = rewardProvider,
                                    onOpen = onOpenItem,
                                )
                            }
                        } else {
                            item {
                                StepByStepCategoryJourney(
                                    steps = menuSteps,
                                    selectedCategoryId = selectedSection?.category?.id,
                                    onCategorySelected = onCategorySelected,
                                )
                            }

                            item {
                                SelectedCategoryMenu(
                                    section = selectedSection,
                                    groups = selectedDishGroups,
                                    rewardProvider = rewardProvider,
                                    onOpen = onOpenItem,
                                )
                            }

                            item {
                                RewardsSection(
                                    isLoading = state.isLoadingRewards,
                                    templates = state.restaurantRewardTemplates,
                                    eligibleItemsProvider = eligibleItemsProvider,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartIcon(cartItemsCount: Int) {
    val palette = LocalBrandPalette.current

    BadgedBox(
        badge = {
            if (cartItemsCount > 0) {
                Badge(
                    containerColor = palette.accent,
                    contentColor = Color.White,
                ) {
                    Text(cartItemsCount.toString())
                }
            }
        },
    ) {
        Icon(
            imageVector = Icons.Rounded.ShoppingCart,
            contentDescription = "Carrito",
            tint = palette.textPrimary,
        )
    }
}

@Composable
private fun RestaurantDiscoveryHero(
    clientName: String,
    levelTitle: String,
    cartItemsCount: Int,
    availableDishesCount: Int,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
) {
    val greeting = clientName
        .trim()
        .takeIf { it.isNotEmpty() }?.substringBefore(' ')
        ?: "Bienvenido"

    val seasonalTheme = rememberCurrentAltosSeasonalTheme()
    val title = seasonalTheme?.restaurantHeroTitle(greeting)
    val subtitle = seasonalTheme?.restaurantHeroSubtitle(levelTitle)
        ?: levelTitle.ifBlank { "Fotos, búsqueda y pedido paso a paso" }

    SeasonalHeroSurface(
        sectionTheme = AppSectionTheme.Restaurant,
        seasonalTheme = seasonalTheme,
    ) {
        Box(
            modifier = Modifier
                .size(146.dp)
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.10f), CircleShape),
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape,
                    border = BorderStroke(width = 1.dp, color = Color.White.copy(alpha = 0.18f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = seasonalTheme?.badgeImageVector()
                                ?: Icons.Rounded.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title ?: "",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.90f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Text(
                text = "Mira y pide lo irresistible, disfruta de una experiencia real",
                color = Color.White.copy(alpha = 0.92f),
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                HeroMicroBadge(title = "$availableDishesCount platos")
                HeroMicroBadge(title = seasonalTheme?.shortPromise() ?: "Buscar + fotos")
                HeroMicroBadge(title = "$cartItemsCount en carrito")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroButton(
                    title = "Ver carrito",
                    emphasized = true,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCart,
                )

                HeroButton(
                    title = "Mis pedidos",
                    emphasized = false,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenOrders,
                )
            }
        }
    }
}

@Composable
private fun HeroMicroBadge(title: String) {
    Surface(
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.12f),
        ),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HeroButton(
    title: String,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = if (emphasized) 0.18f else 0.11f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.16f),
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RestaurantSearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val palette = LocalBrandPalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Limpiar búsqueda",
                    )
                }
            }
        },
        label = { Text("Buscar cuy, parrillada, sopa, bebida...") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            disabledTextColor = palette.textTertiary,
            focusedContainerColor = palette.card,
            unfocusedContainerColor = palette.card,
            disabledContainerColor = palette.card,
            cursorColor = palette.primary,
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.stroke,
            disabledBorderColor = palette.stroke.copy(alpha = 0.55f),
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,
            focusedLeadingIconColor = palette.primary,
            unfocusedLeadingIconColor = palette.textSecondary,
            focusedTrailingIconColor = palette.primary,
            unfocusedTrailingIconColor = palette.textSecondary,
        ),
    )
}

@Composable
private fun SearchResultsSection(
    query: String,
    results: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = if (results.isEmpty()) "Sin resultados" else "Resultados",
            subtitle = if (results.isEmpty()) {
                "No encontré \"$query\". Prueba con cuy, parrillada, sopa o bebida."
            } else {
                "Encontré ${results.size} coincidencia(s) para \"$query\"."
            },
        )

        if (results.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appCardStyle(theme, emphasized = false),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BrandIconBubble(theme = theme, icon = Icons.Rounded.Search, size = 52.dp)
                Text(
                    text = "No está en el menú visible",
                    color = palette.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "También puedes navegar por pasos para descubrir opciones parecidas.",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                results.forEach { item ->
                    CompactDishRow(
                        item = item,
                        reward = rewardProvider(item),
                        onClick = { onOpen(item) },
                    )
                }
            }
        }
    }
}

//@Composable
//private fun FeaturedDishShelf(
//    featuredItems: List<MenuItem>,
//    rewardProvider: (MenuItem) -> RewardPresentation?,
//    onOpen: (MenuItem) -> Unit,
//) {
//    val theme = AppSectionTheme.Restaurant
//
//    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
//        BrandSectionHeader(
//            theme = theme,
//            title = "Primer antojo",
//            subtitle = "Platos destacados para decidir en segundos.",
//        )
//
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
//            items(
//                items = featuredItems.take(8),
//                key = { it.id },
//            ) { item ->
//                PremiumDishTile(
//                    item = item,
//                    reward = rewardProvider(item),
//                    modifier = Modifier.width(220.dp),
//                    onClick = { onOpen(item) },
//                )
//            }
//        }
//    }
//}

@Composable
private fun StepByStepCategoryJourney(
    steps: List<MenuStep>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
) {
    val theme = AppSectionTheme.Restaurant

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Ordena paso a paso",
            subtitle = "No más una lista eterna: toca un paso y mira solo lo que toca.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            steps.forEach { step ->
                CategoryStepCard(
                    step = step,
                    selected = step.category.id == selectedCategoryId,
                    onClick = { onCategorySelected(step.category.id) },
                )
            }
        }
    }
}

@Composable
private fun CategoryStepCard(
    step: MenuStep,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current
    val shape = RoundedCornerShape(22.dp)

    Surface(
        modifier = Modifier
            .width(172.dp)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (selected) palette.primary else palette.card,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color.Transparent else palette.stroke,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (selected) Color.White.copy(alpha = 0.18f) else palette.primary.copy(
                        alpha = 0.10f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) Color.White.copy(alpha = 0.18f) else palette.primary.copy(
                            alpha = 0.14f
                        ),
                    ),
                ) {
                    Text(
                        text = step.index.toString().padStart(2, '0'),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        color = if (selected) Color.White else palette.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = if (selected) Color.White else palette.textTertiary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = step.category.title,
                color = if (selected) Color.White else palette.textPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${step.itemCount} opciones",
                color = if (selected) Color.White.copy(alpha = 0.86f) else palette.textSecondary,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun SelectedCategoryMenu(
    section: MenuSection?,
    groups: List<DishGroup>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        if (section == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appCardStyle(theme),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Selecciona una categoría",
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando el menú cargue, verás los pasos disponibles aquí.",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            return@Column
        }

        BrandSectionHeader(
            theme = theme,
            title = section.category.title,
            subtitle = selectedCategorySubtitle(section.category.title),
        )

        if (groups.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appCardStyle(theme),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "No hay opciones disponibles por ahora.",
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Puede que estén agotadas o desactivadas temporalmente.",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            groups.forEach { group ->
                DishGroupShelf(
                    group = group,
                    rewardProvider = rewardProvider,
                    onOpen = onOpen,
                )
            }
        }
    }
}

@Composable
private fun DishGroupShelf(
    group: DishGroup,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = group.title,
                color = LocalBrandPalette.current.textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = group.subtitle,
                color = LocalBrandPalette.current.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                items = group.items,
                key = { it.id },
            ) { item ->
                PremiumDishTile(
                    item = item,
                    reward = rewardProvider(item),
                    modifier = Modifier.width(204.dp),
                    onClick = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun PremiumDishTile(
    item: MenuItem,
    reward: RewardPresentation?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    SeasonalImageCardBox(
        sectionTheme = AppSectionTheme.Restaurant,
        modifier = modifier.alpha(if (item.canBeOrdered) 1f else 0.58f),
        enabled = item.canBeOrdered,
        onClick = onClick,
        cornerRadiusDp = 26,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DishImageHero(
                item = item,
                height = 146.dpValue,
            )

            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.isFeatured) {
                        BrandBadge(
                            theme = AppSectionTheme.Restaurant,
                            title = "Popular",
                            selected = false
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = item.finalPrice.priceLabel(),
                        color = palette.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Text(
                    text = item.name,
                    color = palette.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = item.description,
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                DishPriceAndStock(item = item)

                reward?.let {
                    CompactRewardRibbon(reward = it, onDark = false)
                }
            }
        }
    }
}

@Composable
private fun DishImageHero(
    item: MenuItem,
    height: androidx.compose.ui.unit.Dp,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
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
                            Color.Black.copy(alpha = 0.22f),
                            Color.Black.copy(alpha = 0.68f),
                        ),
                    ),
                ),
        )

        Text(
            text = item.categoryTitle.ifBlank { "Menú" },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(Color.Black.copy(alpha = 0.28f), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (!item.canBeOrdered) {
            Text(
                text = item.stockLabel,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.42f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private val Int.dpValue: androidx.compose.ui.unit.Dp
    get() = this.dp

@Composable
private fun CompactDishRow(
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = item.canBeOrdered, onClick = onClick)
            .appCardStyle(theme, emphasized = false)
            .alpha(if (item.canBeOrdered) 1f else 0.58f),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.chipGradient)
                .border(1.dp, palette.stroke, RoundedCornerShape(20.dp)),
        ) {
            RemoteBitmapImage(
                url = item.imageURL,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholderIcon = Icons.Rounded.Restaurant,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name,
                    color = palette.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (item.isFeatured) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Text(
                text = item.description,
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            DishPriceAndStock(item = item)

            reward?.let { CompactRewardRibbon(reward = it, onDark = false) }
        }
    }
}

@Composable
private fun DishPriceAndStock(item: MenuItem) {
    val palette = LocalBrandPalette.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.hasOffer) {
            Text(
                text = item.price.priceLabel(),
                style = MaterialTheme.typography.bodySmall,
                color = palette.textTertiary,
                textDecoration = TextDecoration.LineThrough,
            )
        }

        Text(
            text = item.finalPrice.priceLabel(),
            style = MaterialTheme.typography.titleSmall,
            color = palette.primary,
            fontWeight = FontWeight.ExtraBold,
        )

        Text(
            text = "• ${item.stockLabel}",
            style = MaterialTheme.typography.labelMedium,
            color = if (item.canBeOrdered) palette.textSecondary else palette.destructive,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RewardsSection(
    isLoading: Boolean,
    templates: List<LoyaltyRewardTemplate>,
    eligibleItemsProvider: (LoyaltyRewardTemplate) -> List<MenuItem>,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Beneficios disponibles",
            subtitle = "Se aplican automáticamente en platos elegibles y al confirmar el pedido.",
        )

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = palette.primary,
                trackColor = palette.stroke,
            )
        }

        if (templates.isEmpty() && !isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appCardStyle(theme),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrandIconBubble(
                        theme = theme,
                        icon = Icons.Rounded.LocalOffer,
                        size = 42.dp,
                    )

                    Text(
                        text = "Todavía no tienes premios activos para restaurante.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary,
                    )
                }
            }
        } else if (templates.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(
                    items = templates,
                    key = { it.id },
                ) { template ->
                    RewardCouponCard(
                        template = template,
                        eligibleItems = eligibleItemsProvider(template),
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardCouponCard(
    template: LoyaltyRewardTemplate,
    eligibleItems: List<MenuItem>,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .width(310.dp)
            .appCardStyle(theme),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandBadge(
                theme = theme,
                title = badgeText(template),
                selected = true,
            )

            Spacer(modifier = Modifier.weight(1f))

            template.expirationText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Text(
            text = template.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = palette.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = template.subtitle.ifBlank { template.displaySummary },
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        val appliesToText = when {
            eligibleItems.isNotEmpty() -> {
                "Aplica a: " + eligibleItems.take(3).joinToString { it.name } +
                        if (eligibleItems.size > 3) " +${eligibleItems.size - 3}" else ""
            }

            template.rule.type == LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> {
                "Aplica al plato elegible más caro del pedido."
            }

            else -> {
                "Aún no encontré el producto objetivo en el menú. Revisa el menuItemId del cupón."
            }
        }

        Text(
            text = appliesToText,
            style = MaterialTheme.typography.labelMedium,
            color = palette.primary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CompactRewardRibbon(
    reward: RewardPresentation,
    onDark: Boolean,
) {
    val palette = LocalBrandPalette.current

    val background = if (onDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        palette.primary.copy(alpha = 0.08f)
    }

    val border = if (onDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        palette.primary.copy(alpha = 0.14f)
    }

    val titleColor = if (onDark) Color.White else palette.primary
    val bodyColor = if (onDark) Color.White.copy(alpha = 0.90f) else palette.textSecondary

    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 1.dp, color = border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reward.badge,
                style = MaterialTheme.typography.labelMedium,
                color = titleColor,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodySmall,
                color = bodyColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            reward.amountText?.let {
                Text(
                    text = "Ahorro estimado: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.destructive.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = palette.destructive.copy(alpha = 0.28f),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "No se pudo actualizar",
                color = palette.destructive,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar",
                    tint = palette.destructive,
                )
            }
        }

        Text(
            text = message,
            color = palette.textPrimary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun LoadingRestaurantState(
    theme: AppSectionTheme,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appCardStyle(theme),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = Icons.Rounded.Restaurant,
                size = 54.dp,
            )

            Text(
                text = "Cargando menú...",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = palette.primary,
                trackColor = palette.stroke,
            )
        }
    }
}

@Composable
private fun EmptyRestaurantState(
    theme: AppSectionTheme,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appCardStyle(theme, emphasized = true),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = Icons.Rounded.WarningAmber,
                size = 58.dp,
            )

            Text(
                text = "No hay platos publicados todavía",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )

            Text(
                text = "Cuando Firestore tenga documentos en restaurant_menu_items aparecerán aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
            )
        }
    }
}

private fun MenuSection.dishGroups(): List<DishGroup> {
    val sorted = items
        .filter { it.isAvailable }
        .sortedWith(
            compareByDescending<MenuItem> { it.isFeatured }
                .thenBy { it.sortOrder }
                .thenBy { it.name },
        )

    if (sorted.isEmpty()) return emptyList()

    if (category.title != "Platos Fuertes" || sorted.size <= 6) {
        val featured = sorted.filter { it.isFeatured && it.canBeOrdered }
        val regular = sorted.filterNot { featured.any { featuredItem -> featuredItem.id == it.id } }

        return listOfNotNull(
            featured.takeIf { it.isNotEmpty() }?.let {
                DishGroup(
                    id = "${category.id}-featured",
                    title = "Recomendados",
                    subtitle = "Los más llamativos de esta sección.",
                    items = it,
                )
            },
            regular.takeIf { it.isNotEmpty() }?.let {
                DishGroup(
                    id = "${category.id}-available",
                    title = "Disponibles",
                    subtitle = "${it.size} opción(es) para elegir.",
                    items = it,
                )
            },
        )
    }

    val grill = sorted.filter {
        it.containsAny("parrill", "bbq", "costilla", "andina", "altos", "familiar", "para dos")
    }
    val traditional = sorted.filter {
        it.containsAny(
            "cuy",
            "borrego",
            "gallina",
            "yahuarlocro",
            "locro",
            "caldo",
            "fritada",
            "seco"
        )
    }.filterNot { grill.any { grillItem -> grillItem.id == it.id } }
    val others = sorted.filterNot { item ->
        grill.any { it.id == item.id } || traditional.any { it.id == item.id }
    }

    return listOf(
        DishGroup(
            id = "platos-fuertes-grill",
            title = "Parrilladas y BBQ",
            subtitle = "Lo más visual, potente y fácil de compartir.",
            items = grill,
        ),
        DishGroup(
            id = "platos-fuertes-tradicional",
            title = "Tradición serrana",
            subtitle = "Cuy, borrego, caldos y sabores de la casa.",
            items = traditional,
        ),
        DishGroup(
            id = "platos-fuertes-otros",
            title = "Más platos fuertes",
            subtitle = "Otras opciones para completar la mesa.",
            items = others,
        ),
    ).filter { it.items.isNotEmpty() }
}

private fun MenuItem.matchesMenuQuery(query: String): Boolean {
    val normalizedQuery = query.lowercase(Locale.ROOT)
    return name.lowercase(Locale.ROOT).contains(normalizedQuery) ||
            description.lowercase(Locale.ROOT).contains(normalizedQuery) ||
            categoryTitle.lowercase(Locale.ROOT).contains(normalizedQuery) ||
            ingredients.any { it.lowercase(Locale.ROOT).contains(normalizedQuery) }
}

private fun MenuItem.containsAny(vararg terms: String): Boolean {
    val searchable = buildString {
        append(name)
        append(' ')
        append(description)
        append(' ')
        append(categoryTitle)
        append(' ')
        append(ingredients.joinToString(" "))
    }.lowercase(Locale.ROOT)

    return terms.any { searchable.contains(it.lowercase(Locale.ROOT)) }
}

private fun selectedCategorySubtitle(title: String): String = when (title) {
    "Entradas" -> "Algo pequeño para abrir el apetito antes de la ruta."
    "Sopas" -> "Caliente, serrano y perfecto para llegar con hambre."
    "Platos Fuertes" -> "Separado por antojos para no bajar una pared infinita."
    "Extras" -> "Complementos rápidos para completar la mesa."
    "Postres" -> "Cierra la experiencia con algo dulce."
    "Bebidas", "Bebidas Alcohólicas" -> "Elige bebidas para tener todo listo al llegar."
    else -> "Selecciona lo que quieres agregar a tu pedido."
}

private fun badgeText(template: LoyaltyRewardTemplate): String = when (template.rule.type) {
    LoyaltyRewardRuleType.FREE_MENU_ITEM -> "Gratis"
    LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Promo"
    LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
    LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
        -> "${(template.rule.percentage ?: 0.0).toInt()}% OFF"

    LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "Aventura"
}

internal fun Double.priceLabel(): String = NumberFormat.getCurrencyInstance(Locale.US).format(this)

@Composable
internal fun SectionHeader(title: String, subtitle: String = "") {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
