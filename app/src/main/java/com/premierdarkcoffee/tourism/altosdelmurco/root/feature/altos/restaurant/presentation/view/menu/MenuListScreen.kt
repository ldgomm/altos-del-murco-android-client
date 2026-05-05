package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appListRowStyle
import java.text.NumberFormat
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
)
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

    BrandScreen(
        theme = theme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Sabor de Los Altos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.textPrimary,
                            )

                            Text(
                                text = "Menú, promos y platos destacados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary,
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
                        scrolledContainerColor = palette.background.copy(alpha = 0.92f),
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
                        item {
                            RestaurantHeroCard(
                                clientName = clientName,
                                levelTitle = levelTitle,
                                cartItemsCount = cartItemsCount,
                                onOpenCart = onOpenCart,
                                onOpenOrders = onOpenOrders,
                            )
                        }

                        state.errorMessage?.let { message ->
                            item {
                                ErrorCard(
                                    theme = theme,
                                    message = message,
                                    onDismiss = onDismissError,
                                )
                            }
                        }

                        item {
                            RewardsSection(
                                theme = theme,
                                isLoading = state.isLoadingRewards,
                                templates = state.restaurantRewardTemplates,
                                eligibleItemsProvider = eligibleItemsProvider,
                            )
                        }

                        if (state.featuredItems.isNotEmpty()) {
                            item {
                                FeaturedCarousel(
                                    theme = theme,
                                    featuredItems = state.featuredItems,
                                    rewardProvider = rewardProvider,
                                    onOpen = onOpenItem,
                                )
                            }
                        }

                        item {
                            CategorySelectorBlock(
                                theme = theme,
                                selectedCategoryId = state.selectedCategoryId,
                                categories = state.categories,
                                onCategorySelected = onCategorySelected,
                            )
                        }

                        items(
                            items = state.visibleSections,
                            key = { it.id },
                        ) { section ->
                            MenuSectionCard(
                                theme = theme,
                                section = section,
                                rewardProvider = rewardProvider,
                                onOpen = onOpenItem,
                            )
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
private fun RestaurantHeroCard(
    clientName: String,
    levelTitle: String,
    cartItemsCount: Int,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.xLarge))
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.16f),
                shape = RoundedCornerShape(AppTheme.Radius.xLarge),
            )
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape,
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.18f),
                    ),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = if (clientName.isBlank()) {
                            "Bienvenido a Altos"
                        } else {
                            "Hola, $clientName"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = levelTitle.ifBlank { "Explora platos, cupones y pedidos" },
                        color = Color.White.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStatPill(title = "$cartItemsCount en carrito")

                HeroStatPill(title = "Promos activas")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clickable(onClick = onOpenCart),
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.18f),
                    ),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ver carrito",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clickable(onClick = onOpenOrders),
                    color = Color.White.copy(alpha = 0.11f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.14f),
                    ),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Mis pedidos",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
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
                icon = Icons.Rounded.Restaurant,
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

@Composable
private fun HeroStatPill(title: String) {
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ErrorCard(
    theme: AppSectionTheme,
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
private fun RewardsSection(
    theme: AppSectionTheme,
    isLoading: Boolean,
    templates: List<LoyaltyRewardTemplate>,
    eligibleItemsProvider: (LoyaltyRewardTemplate) -> List<MenuItem>,
) {
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Tus cupones y premios",
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
                        theme = theme,
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
    theme: AppSectionTheme,
    template: LoyaltyRewardTemplate,
    eligibleItems: List<MenuItem>,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .width(310.dp)
            .appCardStyle(theme, emphasized = true),
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
                "Aplica a: " + eligibleItems.take(3)
                    .joinToString { it.name } + if (eligibleItems.size > 3) " +${eligibleItems.size - 3}" else ""
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

private fun badgeText(template: LoyaltyRewardTemplate): String = when (template.rule.type) {
    LoyaltyRewardRuleType.FREE_MENU_ITEM -> "Gratis"
    LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Promo"

    LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
    LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
        -> "${(template.rule.percentage ?: 0.0).toInt()}% OFF"

    LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "Aventura"
}

@Composable
private fun FeaturedCarousel(
    theme: AppSectionTheme,
    featuredItems: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Popular",
            subtitle = "Favoritos de los clientes y platos destacados.",
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(
                items = featuredItems,
                key = { it.id },
            ) { item ->
                FeaturedMenuCard(
                    theme = theme,
                    item = item,
                    reward = rewardProvider(item),
                    onClick = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun FeaturedMenuCard(
    theme: AppSectionTheme,
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = Modifier
            .width(286.dp)
            .height(226.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(30.dp),
            )
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.14f),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Whatshot,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )

                        Text(
                            text = "Destacado",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = item.finalPrice.priceLabel(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                reward?.let {
                    CompactRewardRibbon(
                        reward = it,
                        onDark = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelectorBlock(
    theme: AppSectionTheme,
    selectedCategoryId: String?,
    categories: List<MenuCategory>,
    onCategorySelected: (String?) -> Unit,
) {
    val palette = LocalBrandPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = "Explorar por categoría",
            subtitle = "Muévete rápido entre platos, bebidas y extras.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            categories.forEach { category ->
                RestaurantFilterChip(
                    title = category.title,
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                )
            }
        }
    }
}

@Composable
private fun RestaurantFilterChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) palette.primary else palette.card,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color.Transparent else palette.stroke,
        ),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            color = if (selected) palette.onPrimary else palette.textPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MenuSectionCard(
    theme: AppSectionTheme,
    section: MenuSection,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BrandSectionHeader(
            theme = theme,
            title = section.category.title,
            subtitle = "${section.items.size} producto(s)",
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            section.items.forEach { item ->
                if (item.isAvailable) {
                    MenuItemRowCard(
                        theme = theme,
                        item = item,
                        reward = rewardProvider(item),
                        onClick = { onOpen(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItemRowCard(
    theme: AppSectionTheme,
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                enabled = item.canBeOrdered,
                onClick = onClick,
            )
            .appListRowStyle(theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.chipGradient)
                    .border(
                        width = 1.dp,
                        color = palette.stroke,
                        shape = RoundedCornerShape(18.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = palette.primary,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.textPrimary,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

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
                        color = if (item.canBeOrdered) {
                            palette.textSecondary
                        } else {
                            palette.destructive
                        },
                    )
                }
            }
        }

        reward?.let {
            CompactRewardRibbon(
                reward = it,
                onDark = false,
            )
        }

        if (item.ingredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item.ingredients.take(4).forEach { ingredient ->
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.chipGradientColorFallback(),
                        border = BorderStroke(
                            width = 1.dp,
                            color = palette.stroke,
                        ),
                    ) {
                        Text(
                            text = ingredient,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                        )
                    }
                }
            }
        }
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

    val titleColor = if (onDark) {
        Color.White
    } else {
        palette.primary
    }

    val bodyColor = if (onDark) {
        Color.White.copy(alpha = 0.90f)
    } else {
        palette.textSecondary
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = border,
        ),
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

private fun BrandPalette.chipGradientColorFallback(): Color {
    return primary.copy(alpha = 0.08f)
}

internal fun Double.priceLabel(): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}

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