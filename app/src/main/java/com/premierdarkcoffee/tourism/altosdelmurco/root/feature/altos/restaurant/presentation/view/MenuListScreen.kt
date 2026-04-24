package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListScreen(
    state: MenuUiState,
    clientName: String,
    levelTitle: String,
    cartItemsCount: Int,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onCategorySelected: (String?) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Sabor de Los Altos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Menú, promos y platos destacados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenOrders) {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = "Pedidos",
                        )
                    }

                    IconButton(onClick = onOpenCart) {
                        CartIcon(cartItemsCount = cartItemsCount)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenCart) {
                CartIcon(cartItemsCount = cartItemsCount)
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading && state.sections.isEmpty() -> {
                LoadingRestaurantState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            state.sections.isEmpty() -> {
                EmptyRestaurantState(
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
                        bottom = 104.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    item {
                        RestaurantHeroCard(
                            clientName = clientName,
                            levelTitle = levelTitle,
                            featuredCount = state.featuredItems.size,
                            sectionCount = state.sections.size,
                        )
                    }

                    state.errorMessage?.let { message ->
                        item {
                            ErrorCard(
                                message = message,
                                onDismiss = onDismissError,
                            )
                        }
                    }

                    if (state.isLoadingRewards || state.restaurantRewardTemplates.isNotEmpty()) {
                        item {
                            RewardsSection(
                                isLoading = state.isLoadingRewards,
                                templates = state.restaurantRewardTemplates,
                                allItems = state.allItems,
                            )
                        }
                    }

                    if (state.featuredItems.isNotEmpty()) {
                        item {
                            FeaturedCarousel(
                                featuredItems = state.featuredItems,
                                rewardProvider = rewardProvider,
                                onOpen = onOpenItem,
                            )
                        }
                    }

                    item {
                        CategorySelectorBlock(
                            selectedCategoryId = state.selectedCategoryId,
                            categories = state.categories,
                            onCategorySelected = onCategorySelected,
                        )
                    }

                    items(state.visibleSections, key = { it.id }) { section ->
                        MenuSectionCard(
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

@Composable
private fun CartIcon(cartItemsCount: Int) {
    BadgedBox(
        badge = {
            if (cartItemsCount > 0) {
                Badge { Text(cartItemsCount.toString()) }
            }
        },
    ) {
        Icon(
            imageVector = Icons.Rounded.ShoppingCart,
            contentDescription = "Carrito",
        )
    }
}

@Composable
private fun LoadingRestaurantState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Cargando menú...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyRestaurantState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp),
                )
                Text(
                    text = "No hay platos publicados todavía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando Firestore tenga documentos en restaurant_menu_items aparecerán aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RestaurantHeroCard(
    clientName: String,
    levelTitle: String,
    featuredCount: Int,
    sectionCount: Int,
) {
    val friendlyName = clientName.substringBefore(" ").ifBlank { "amigo" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary,
                    ),
                ),
            )
            .padding(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                AssistChip(
                    onClick = {},
                    label = { Text("Nivel $levelTitle") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.LocalOffer,
                            contentDescription = null,
                        )
                    },
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Hola, $friendlyName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    text = "Elige tus favoritos. Si tienes premios Murco Loyalty, se muestran y se aplican automáticamente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStatPill("$featuredCount destacados")
                HeroStatPill("$sectionCount categorías")
            }
        }
    }
}

@Composable
private fun HeroStatPill(title: String) {
    Surface(
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "No se pudo actualizar",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
private fun RewardsSection(
    isLoading: Boolean,
    templates: List<LoyaltyRewardTemplate>,
    allItems: List<MenuItem>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Tus cupones y premios",
            subtitle = "Se aplican automáticamente al abrir un plato elegible o al confirmar el pedido.",
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (templates.isEmpty() && !isLoading) {
            ElevatedCard(shape = RoundedCornerShape(22.dp)) {
                Text(
                    text = "Todavía no tienes premios activos para restaurante.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(templates, key = { it.id }) { template ->
                    RewardCouponCard(
                        template = template,
                        eligibleItems = eligibleItemsFor(template, allItems),
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
    ElevatedCard(
        modifier = Modifier.width(300.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(999.dp),
                ) {
                    Text(
                        text = badgeText(template),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                template.expirationText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    )
                }
            }

            Text(
                text = template.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = template.subtitle.ifBlank { template.displaySummary },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            if (eligibleItems.isNotEmpty()) {
                Text(
                    text = "Aplica a: " + eligibleItems.take(3).joinToString { it.name } +
                        if (eligibleItems.size > 3) " +${eligibleItems.size - 3}" else "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun eligibleItemsFor(
    template: LoyaltyRewardTemplate,
    allItems: List<MenuItem>,
): List<MenuItem> = when (template.rule.type) {
    LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> allItems.filter { it.canBeOrdered }
    LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
    LoyaltyRewardRuleType.FREE_MENU_ITEM,
    LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
        -> {
        val targetId = template.targetMenuItemId ?: return emptyList()
        allItems.filter { it.id == targetId }
    }
    LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> emptyList()
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
    featuredItems: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Popular",
            subtitle = "Favoritos de los clientes y platos destacados",
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(featuredItems, key = { it.id }) { item ->
                FeaturedMenuCard(
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
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
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
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                reward?.let {
                    CompactRewardRibbon(reward = it, onDark = true)
                }
            }
        }
    }
}

@Composable
private fun CategorySelectorBlock(
    selectedCategoryId: String?,
    categories: List<MenuCategory>,
    onCategorySelected: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "Explorar por categoría",
            subtitle = "Muévete rápido entre platos, bebidas y extras.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.title) },
                )
            }
        }
    }
}

@Composable
private fun MenuSectionCard(
    section: MenuSection,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(title = section.category.title, subtitle = "${section.items.size} producto(s)")

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            section.items.forEach { item ->
                MenuItemRowCard(
                    item = item,
                    reward = rewardProvider(item),
                    onClick = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun MenuItemRowCard(
    item: MenuItem,
    reward: RewardPresentation?,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item.canBeOrdered, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
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
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (item.isFeatured) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }

                        Text(
                            text = item.finalPrice.priceLabel(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        )

                        Text(
                            text = "• ${item.stockLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (item.canBeOrdered) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    }
                }
            }

            reward?.let {
                CompactRewardRibbon(reward = it, onDark = false)
            }

            if (item.ingredients.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item.ingredients.take(4).forEach { ingredient ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = ingredient,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
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
    val background = if (onDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    }
    val titleColor = if (onDark) Color.White else MaterialTheme.colorScheme.primary
    val bodyColor = if (onDark) {
        Color.White.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp),
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
                fontWeight = FontWeight.Bold,
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
internal fun SectionHeader(
    title: String,
    subtitle: String = "",
) {
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

internal fun Double.priceLabel(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)
