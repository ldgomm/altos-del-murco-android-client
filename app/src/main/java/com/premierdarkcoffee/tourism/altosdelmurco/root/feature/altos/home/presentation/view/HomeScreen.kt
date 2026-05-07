package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.ForkRight
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel.AdventureCatalogViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPost
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel.FeaturedFeedUiState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel.FeaturedFeedViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentationFactory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.MenuItemDetailScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CartViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.SeasonalCardContainer
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumAltosCopy
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumEmptyState
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumGradientHero
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumPriceRow
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumRewardCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumScreenHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.premiumMoney

private enum class HomeRoute {
    MAIN, FEATURED_DISHES, EXPERIENCE_PACKAGES, REWARDS_CENTER,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated? = null,
    viewModel: FeaturedFeedViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    adventureCatalogViewModel: AdventureCatalogViewModel = hiltViewModel(),
    onOpenRestaurant: () -> Unit = {},
    onOpenExperiences: () -> Unit = {},
    onOpenExperiencePackage: (String) -> Unit = {},
    onOpenBookings: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
) {
    val feedState by viewModel.uiState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val catalogState by adventureCatalogViewModel.uiState.collectAsStateWithLifecycle()
    val profile = sessionState?.profile

    var route by rememberSaveable { mutableStateOf(HomeRoute.MAIN) }
    var selectedMenuItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedPackageId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(profile?.id, profile?.updatedAt) {
        viewModel.start()
        menuViewModel.onAppear(profile?.userId)
        adventureCatalogViewModel.onAppear()
        profile?.let(cartViewModel::syncProfile)
    }

    DisposableEffect(Unit) {
        onDispose {
            adventureCatalogViewModel.onDisappear()
        }
    }

    BackHandler(enabled = route != HomeRoute.MAIN || selectedMenuItemId != null || selectedPackageId != null) {
        when {
            selectedMenuItemId != null -> selectedMenuItemId = null
            selectedPackageId != null -> selectedPackageId = null
            else -> route = HomeRoute.MAIN
        }
    }

    val errorMessage = feedState.errorMessage ?: menuState.errorMessage ?: catalogState.errorMessage
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissError()
                menuViewModel.clearError()
                adventureCatalogViewModel.clearError()
            },
            title = { Text("No se pudo actualizar Inicio") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissError()
                    menuViewModel.clearError()
                    adventureCatalogViewModel.clearError()
                }) { Text("Aceptar") }
            },
        )
    }

    when {
        selectedMenuItemId != null -> {
            val selectedItem = menuState.itemById(selectedMenuItemId.orEmpty())
            if (selectedItem == null) {
                LaunchedEffect(selectedMenuItemId, menuState.allItems.size) {
                    if (!menuState.isLoading) selectedMenuItemId = null
                }
                HomeLoadingShell(text = "Cargando plato...")
            } else {
                MenuItemDetailScreen(
                    item = selectedItem,
                    rewardPresentationProvider = { item, quantity ->
                        menuViewModel.rewardPresentation(item, quantity)
                    },
                    displayedPriceProvider = { item, quantity ->
                        menuViewModel.displayedPrice(item, quantity)
                    },
                    incrementalDiscountProvider = { item, quantity ->
                        menuViewModel.incrementalDiscount(item, quantity)
                    },
                    onAddToCart = { item, quantity, notes ->
                        cartViewModel.addItem(item, quantity, notes) { added ->
                            if (added) {
                                selectedMenuItemId = null
                                onOpenRestaurant()
                            }
                        }
                    },
                    onBack = { selectedMenuItemId = null },
                    modifier = modifier,
                )
            }
        }

        selectedPackageId != null -> {
            val selectedPackage =
                catalogState.catalog.activePackagesSorted.firstOrNull { it.id == selectedPackageId }

            if (selectedPackage == null) {
                LaunchedEffect(selectedPackageId, catalogState.catalog.activePackagesSorted.size) {
                    if (!catalogState.isLoading) selectedPackageId = null
                }
                HomeLoadingShell(text = "Cargando combo...")
            } else {
                HomeExperiencePackageDetailScreen(
                    packageModel = selectedPackage,
                    catalog = catalogState.catalog,
                    menuSections = menuState.sections,
                    walletSnapshot = menuState.walletSnapshot,
                    onBack = { selectedPackageId = null },
                    onReservePackage = {
                        onOpenExperiencePackage(selectedPackage.id)
                    },
                    onOpenExperiences = onOpenExperiences,
                    modifier = modifier,
                )
            }
        }

        else -> {
            HomeScaffold(
                modifier = modifier,
                title = when (route) {
                    HomeRoute.MAIN -> PremiumAltosCopy.brand
                    HomeRoute.FEATURED_DISHES -> "Platos destacados"
                    HomeRoute.EXPERIENCE_PACKAGES -> "Combos activos"
                    HomeRoute.REWARDS_CENTER -> "Murco Loyalty"
                },
                subtitle = when (route) {
                    HomeRoute.MAIN -> PremiumAltosCopy.promise
                    HomeRoute.FEATURED_DISHES -> "Recomendados con detalle real y compra directa."
                    HomeRoute.EXPERIENCE_PACKAGES -> "Compara, revisa y reserva un combo completo."
                    HomeRoute.REWARDS_CENTER -> "Beneficios visibles antes de pagar."
                },
                showBack = route != HomeRoute.MAIN,
                onBack = { route = HomeRoute.MAIN },
                onRefresh = {
                    viewModel.refresh()
                    menuViewModel.onAppear(profile?.userId)
                    adventureCatalogViewModel.refresh()
                },
            ) { padding ->
                when (route) {
                    HomeRoute.MAIN -> HomeMainContent(
                        padding = padding,
                        profileName = profile?.fullName.orEmpty(),
                        feedState = feedState,
                        featuredItems = menuState.featuredItems,
                        catalog = catalogState.catalog,
                        menuSections = menuState.sections,
                        walletSnapshot = menuState.walletSnapshot,
                        onOpenRestaurant = onOpenRestaurant,
                        onOpenExperiences = onOpenExperiences,
                        onOpenBookings = onOpenBookings,
                        onOpenProfile = onOpenProfile,
                        onOpenFeaturedDishes = { route = HomeRoute.FEATURED_DISHES },
                        onOpenPackages = { route = HomeRoute.EXPERIENCE_PACKAGES },
                        onOpenRewards = { route = HomeRoute.REWARDS_CENTER },
                        onOpenDishDetail = { selectedMenuItemId = it.id },
                        onOpenPackageDetail = { selectedPackageId = it.id },
                        onLoadMorePost = viewModel::loadMoreIfNeeded,
                    )

                    HomeRoute.FEATURED_DISHES -> HomeFeaturedDishesScreen(
                        padding = padding,
                        featuredItems = menuState.featuredItems,
                        onOpenItem = { selectedMenuItemId = it.id },
                    )

                    HomeRoute.EXPERIENCE_PACKAGES -> HomePackagesScreen(
                        padding = padding,
                        catalog = catalogState.catalog,
                        menuSections = menuState.sections,
                        walletSnapshot = menuState.walletSnapshot,
                        onOpenPackage = { selectedPackageId = it.id },
                    )

                    HomeRoute.REWARDS_CENTER -> HomeRewardsCenterScreen(
                        padding = padding,
                        walletSnapshot = menuState.walletSnapshot,
                        onOpenProfile = onOpenProfile,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    modifier: Modifier,
    title: String,
    subtitle: String,
    showBack: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                title = {
                    PremiumScreenHeader(
                        title = title,
                        subtitle = subtitle,
                    )
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        content = content,
    )
}

@Composable
private fun HomeMainContent(
    padding: PaddingValues,
    profileName: String,
    feedState: FeaturedFeedUiState,
    featuredItems: List<MenuItem>,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    walletSnapshot: RewardWalletSnapshot,
    onOpenRestaurant: () -> Unit,
    onOpenExperiences: () -> Unit,
    onOpenBookings: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenFeaturedDishes: () -> Unit,
    onOpenPackages: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenDishDetail: (MenuItem) -> Unit,
    onOpenPackageDetail: (AdventureFeaturedPackage) -> Unit,
    onLoadMorePost: (FeaturedPost) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            HomeHero(
                clientName = profileName,
                onOpenRestaurant = onOpenRestaurant,
                onOpenExperiences = onOpenExperiences,
            )
        }

        item {
            HomeQuickActions(
                featuredCount = featuredItems.size,
                packageCount = catalog.activePackagesSorted.size,
                rewardsCount = walletSnapshot.availableTemplates.count { !it.isExpired },
                onOpenFeaturedDishes = onOpenFeaturedDishes,
                onOpenPackages = onOpenPackages,
                onOpenRewards = onOpenRewards,
                onOpenBookings = onOpenBookings,
                onOpenProfile = onOpenProfile,
            )
        }

        if (featuredItems.isNotEmpty()) {
            item {
                PremiumSectionHeader(
                    title = "Recomendados del restaurante",
                    subtitle = "Ahora cada plato abre su detalle, precio, premio y compra directa.",
                    icon = Icons.Rounded.LocalDining,
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(featuredItems.take(8), key = { it.id }) { item ->
                        FeaturedMenuHomeCard(
                            item = item,
                            reward = RewardPresentationFactory.menuPresentation(
                                item, walletSnapshot
                            ),
                            onClick = { onOpenDishDetail(item) },
                        )
                    }
                }
            }
        }

        if (catalog.activePackagesSorted.isNotEmpty()) {
            item {
                PremiumSectionHeader(
                    title = "Combos de experiencias",
                    subtitle = "Toca un combo para ver actividades, comida incluida, ahorro y reserva prellenada.",
                    icon = Icons.Rounded.Terrain,
                )
            }
            items(catalog.activePackagesSorted.take(4), key = { it.id }) { packageModel ->
                ExperiencePackageHomeCard(
                    packageModel = packageModel,
                    catalog = catalog,
                    menuSections = menuSections,
                    walletSnapshot = walletSnapshot,
                    onClick = { onOpenPackageDetail(packageModel) },
                )
            }
        }

        item {
            PremiumSectionHeader(
                title = "Momentos destacados",
                subtitle = "Fotos recientes publicadas desde ADM.",
                icon = Icons.Rounded.PhotoLibrary,
            )
        }

        when {
            feedState.shouldShowInitialPlaceholders -> {
                items(2) { HomePostSkeleton() }
            }

            feedState.shouldShowEmptyState -> {
                item {
                    PremiumEmptyState(
                        title = "Aún no hay publicaciones activas",
                        body = "Cuando ADM publique nuevas fotos aparecerán aquí automáticamente.",
                        icon = Icons.Rounded.PhotoLibrary,
                    )
                }
            }

            else -> {
                items(feedState.posts, key = { it.id }) { post ->
                    PremiumFeaturedPostCard(post = post)
                    LaunchedEffect(post.id) {
                        onLoadMorePost(post)
                    }
                }
                if (feedState.isLoadingMore) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHero(
    clientName: String,
    onOpenRestaurant: () -> Unit,
    onOpenExperiences: () -> Unit,
) {
    val greeting =
        clientName.trim().takeIf { it.isNotEmpty() }?.let { "Hola, ${it.substringBefore(' ')}" }
            ?: "Bienvenido"
    PremiumGradientHero(
        title = "$greeting. Vive Altos del Murco.",
        subtitle = "Pide comida, reserva experiencias, arma combos con comida incluida y aprovecha premios Murco Loyalty en un solo lugar.",
        badge = "Restaurante + Experiencias",
        primaryAction = {
            Button(onClick = onOpenRestaurant, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Restaurant, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(PremiumAltosCopy.restaurantCta)
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onOpenExperiences, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Explore, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(PremiumAltosCopy.experiencesCta)
            }
        },
    )
}

@Composable
private fun HomeQuickActions(
    featuredCount: Int,
    packageCount: Int,
    rewardsCount: Int,
    onOpenFeaturedDishes: () -> Unit,
    onOpenPackages: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenBookings: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeMetricActionCard(
                title = "Platos destacados",
                value = featuredCount.toString(),
                subtitle = "Ver y pedir",
                icon = Icons.Rounded.ForkRight,
                modifier = Modifier.weight(1f),
                onClick = onOpenFeaturedDishes,
            )
            HomeMetricActionCard(
                title = "Combos activos",
                value = packageCount.toString(),
                subtitle = "Comparar combos",
                icon = Icons.Rounded.Terrain,
                modifier = Modifier.weight(1f),
                onClick = onOpenPackages,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeMetricActionCard(
                title = "Premios",
                value = rewardsCount.toString(),
                subtitle = "Ver beneficios",
                icon = Icons.Rounded.Sell,
                modifier = Modifier.weight(1f),
                onClick = onOpenRewards,
            )
            HomeMetricActionCard(
                title = "Reservas",
                value = "Agenda",
                subtitle = "Ver reservas",
                icon = Icons.Rounded.CalendarMonth,
                modifier = Modifier.weight(1f),
                onClick = onOpenBookings,
            )
        }
    }
}

@Composable
private fun HomeMetricActionCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PremiumIconBubble(icon = icon, modifier = Modifier.size(38.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun FeaturedMenuHomeCard(
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    SeasonalCardContainer(
        sectionTheme = AppSectionTheme.Restaurant,
        modifier = Modifier.width(250.dp),
        onClick = onClick,
        minHeightDp = 0,
    ) {
        PremiumIconBubble(Icons.Rounded.LocalDining)

        Text(
            item.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            item.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (item.hasOffer) {
                Text(
                    item.price.premiumMoney(),
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                item.finalPrice.premiumMoney(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        reward?.let {
            Text(
                text = "${it.badge}: ${it.message}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = if (item.canBeOrdered) "Tocar para elegir cantidad" else "Agotado hoy / disponible para reservas futuras",
            style = MaterialTheme.typography.labelSmall,
            color = if (item.canBeOrdered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ExperiencePackageHomeCard(
    packageModel: AdventureFeaturedPackage,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    walletSnapshot: RewardWalletSnapshot,
    onClick: () -> Unit,
) {
    val menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id }
    val activitySubtotal = AdventurePricingEngine.estimatedSubtotal(packageModel.items, catalog)
    val foodSubtotal = packageFoodSubtotal(packageModel, menuItemsById)
    val comboDiscount = packageModel.packageDiscountAmount.coerceAtLeast(0.0)
    val finalTotal = (activitySubtotal + foodSubtotal - comboDiscount).coerceAtLeast(0.0)
    val reward = RewardPresentationFactory.packagePresentation(
        packageModel = packageModel,
        catalog = catalog,
        menuItemsById = menuItemsById,
        wallet = walletSnapshot,
    )

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                PremiumIconBubble(Icons.Rounded.Terrain, selected = true)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        packageModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(packageModel.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!packageModel.badge.isNullOrBlank()) {
                        Text(
                            packageModel.badge,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Aventura ${activitySubtotal.premiumMoney()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (foodSubtotal > 0) {
                    Text(
                        "Comida ${foodSubtotal.premiumMoney()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (comboDiscount > 0) {
                Text(
                    "Ahorro combo ${comboDiscount.premiumMoney()}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            reward?.let {
                Text(
                    text = "${it.badge}: ${it.message}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("Desde ${finalTotal.premiumMoney()} • Ver detalle")
            }
        }
    }
}

@Composable
private fun HomeFeaturedDishesScreen(
    padding: PaddingValues,
    featuredItems: List<MenuItem>,
    onOpenItem: (MenuItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (featuredItems.isEmpty()) {
            item {
                PremiumEmptyState(
                    title = "No hay platos destacados",
                    body = "Marca platos como destacados en ADM para que aparezcan aquí.",
                    icon = Icons.Rounded.LocalDining,
                )
            }
        } else {
            items(featuredItems, key = { it.id }) { item ->
                HomeFeaturedDishWideCard(item = item, onClick = { onOpenItem(item) })
            }
        }
    }
}

@Composable
private fun HomeFeaturedDishWideCard(
    item: MenuItem,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            PremiumIconBubble(Icons.Rounded.LocalDining)
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    item.name,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    item.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.finalPrice.premiumMoney(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    item.stockLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.canBeOrdered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HomePackagesScreen(
    padding: PaddingValues,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    walletSnapshot: RewardWalletSnapshot,
    onOpenPackage: (AdventureFeaturedPackage) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (catalog.activePackagesSorted.isEmpty()) {
            item {
                PremiumEmptyState(
                    title = "No hay combos activos",
                    body = "Activa paquetes en Firestore para vender planes completos desde Inicio.",
                    icon = Icons.Rounded.Terrain,
                )
            }
        } else {
            items(catalog.activePackagesSorted, key = { it.id }) { packageModel ->
                ExperiencePackageHomeCard(
                    packageModel = packageModel,
                    catalog = catalog,
                    menuSections = menuSections,
                    walletSnapshot = walletSnapshot,
                    onClick = { onOpenPackage(packageModel) },
                )
            }
        }
    }
}

@Composable
private fun HomeRewardsCenterScreen(
    padding: PaddingValues,
    walletSnapshot: RewardWalletSnapshot,
    onOpenProfile: () -> Unit,
) {
    val activeRewards = walletSnapshot.availableTemplates.filterNot { it.isExpired }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            PremiumCard(emphasized = true) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumIconBubble(Icons.Rounded.Star, selected = true)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            walletSnapshot.currentLevel.title,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${walletSnapshot.points} punto(s) • ${walletSnapshot.totalSpent.premiumMoney()} acumulados",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (activeRewards.isEmpty()) {
            item {
                PremiumEmptyState(
                    title = "Sin premios activos",
                    body = "Cuando tengas premios disponibles aparecerán aquí con su uso y vencimiento.",
                    icon = Icons.Rounded.Sell,
                    action = {
                        Button(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver perfil")
                        }
                    },
                )
            }
        } else {
            items(activeRewards, key = { it.id }) { template ->
                PremiumCard {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        PremiumIconBubble(Icons.Rounded.Sell, selected = true)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                template.title,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                template.subtitle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                template.displaySummary,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            template.expirationText?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeExperiencePackageDetailScreen(
    packageModel: AdventureFeaturedPackage,
    catalog: AdventureCatalogSnapshot,
    menuSections: List<MenuSection>,
    walletSnapshot: RewardWalletSnapshot,
    onBack: () -> Unit,
    onReservePackage: () -> Unit,
    onOpenExperiences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id }
    val activitySubtotal = AdventurePricingEngine.estimatedSubtotal(packageModel.items, catalog)
    val foodSubtotal = packageFoodSubtotal(packageModel, menuItemsById)
    val comboDiscount = packageModel.packageDiscountAmount.coerceAtLeast(0.0)
    val finalTotal = (activitySubtotal + foodSubtotal - comboDiscount).coerceAtLeast(0.0)
    val reward = RewardPresentationFactory.packagePresentation(
        packageModel = packageModel,
        catalog = catalog,
        menuItemsById = menuItemsById,
        wallet = walletSnapshot,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    PremiumScreenHeader(
                        title = packageModel.title,
                        subtitle = "Detalle del combo antes de reservar.",
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PremiumCard(emphasized = true) {
                    PremiumIconBubble(Icons.Rounded.Terrain, selected = true)
                    Text(
                        packageModel.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(packageModel.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!packageModel.badge.isNullOrBlank()) {
                        Text(
                            packageModel.badge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(onClick = onReservePackage, modifier = Modifier.fillMaxWidth()) {
                        Text("Reservar este combo • ${finalTotal.premiumMoney()}")
                    }
                    OutlinedButton(
                        onClick = onOpenExperiences, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear otro combo")
                    }
                }
            }

            reward?.let {
                item { PremiumRewardCard(reward = it) }
            }

            item {
                PremiumCard {
                    PremiumSectionHeader(
                        title = "Actividades incluidas", icon = Icons.Rounded.Explore
                    )
                    packageModel.items.forEach { draft ->
                        val config = catalog.activity(draft.activity)
                        DetailLine(
                            icon = Icons.Rounded.Terrain,
                            title = config?.title ?: draft.title,
                            subtitle = draft.summaryText,
                            trailing = AdventurePricingEngine.subtotal(draft, catalog)
                                .premiumMoney(),
                        )
                    }
                }
            }

            if (packageModel.foodItems.isNotEmpty()) {
                item {
                    PremiumCard {
                        PremiumSectionHeader(
                            title = "Comida incluida", icon = Icons.Rounded.LocalDining
                        )
                        packageModel.foodItems.forEach { food ->
                            val item = menuItemsById[food.menuItemId]
                            DetailLine(
                                icon = Icons.Rounded.LocalDining,
                                title = item?.name ?: food.menuItemId,
                                subtitle = "${food.quantity} unidad(es)",
                                trailing = ((item?.finalPrice
                                    ?: 0.0) * food.quantity).premiumMoney(),
                            )
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    PremiumSectionHeader(title = "Precio", icon = Icons.Rounded.Sell)
                    PremiumPriceRow("Aventura", activitySubtotal)
                    PremiumPriceRow("Comida", foodSubtotal)
                    if (comboDiscount > 0) {
                        PremiumPriceRow("Ahorro combo", comboDiscount, negative = true)
                    }
                    HorizontalDivider()
                    PremiumPriceRow("Total estimado", finalTotal, bold = true)
                }
            }
        }
    }
}

@Composable
private fun DetailLine(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        PremiumIconBubble(icon, modifier = Modifier.size(38.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(trailing, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

private fun packageFoodSubtotal(
    packageModel: AdventureFeaturedPackage,
    menuItemsById: Map<String, MenuItem>,
): Double = packageModel.foodItems.sumOf { food ->
    (menuItemsById[food.menuItemId]?.finalPrice ?: 0.0) * food.quantity
}

@Composable
private fun HomeLoadingShell(text: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PremiumFeaturedPostCard(post: FeaturedPost) {
    PremiumCard {
        Text(
            "Altos del Murco",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        post.description?.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            "Publicado desde ADM",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun HomePostSkeleton() {
    PremiumCard {
        Text("Cargando destacados...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(90.dp))
    }
}
