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
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreen(
    sessionState: SessionState.Authenticated,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedItemId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(sessionState.profile.nationalId) {
        viewModel.onAppear(sessionState.profile.nationalId)
    }

    val selectedItem = uiState.sections
        .flatMap { it.items }
        .firstOrNull { it.id == selectedItemId }

    if (selectedItem != null) {
        MenuItemDetailScreen(
            item = selectedItem,
            rewardPresentation = viewModel.rewardPresentation(selectedItem),
            onBack = { selectedItemId = null },
            modifier = modifier,
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Sabor de Los Altos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Menú, promos y platos destacados",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text(viewModel.currentLevelTitle()) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.LocalOffer,
                                contentDescription = null,
                            )
                        },
                    )
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.sections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.sections.isEmpty() -> {
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
                        top = 14.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    item {
                        RestaurantHeroCard(
                            clientName = sessionState.profile.fullName,
                            levelTitle = viewModel.currentLevelTitle(),
                            featuredCount = uiState.featuredItems.size,
                            sectionCount = uiState.sections.size,
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        item {
                            ErrorCard(
                                message = message,
                                onDismiss = viewModel::clearError,
                            )
                        }
                    }

                    if (uiState.featuredItems.isNotEmpty()) {
                        item {
                            FeaturedCarousel(
                                featuredItems = uiState.featuredItems,
                                rewardProvider = viewModel::rewardPresentation,
                                onOpen = { selectedItemId = it.id },
                            )
                        }
                    }

                    item {
                        CategorySelectorBlock(
                            selectedCategoryId = uiState.selectedCategoryId,
                            sections = uiState.sections,
                            onCategorySelected = viewModel::onCategorySelected,
                        )
                    }

                    items(uiState.visibleSections, key = { it.id }) { section ->
                        MenuSectionCard(
                            section = section,
                            rewardProvider = viewModel::rewardPresentation,
                            onOpen = { selectedItemId = it.id },
                        )
                    }
                }
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
    val accent = MaterialTheme.colorScheme.secondary
    val friendlyName = clientName.substringBefore(" ").ifBlank { "amigo" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        accent,
                    ),
                ),
            )
            .padding(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
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
                            tint = Color.White,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White.copy(alpha = 0.16f),
                        labelColor = Color.White,
                        leadingIconContentColor = Color.White,
                    ),
                    border = null,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Hola, $friendlyName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Explora el menú con una experiencia más cercana a Altos iOS: hero visible, destacados arriba y categorías claras.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.94f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStatPill(title = "$featuredCount destacados")
                HeroStatPill(title = "$sectionCount categorías")
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
                text = "No se pudo actualizar el menú",
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
private fun EmptyRestaurantState(
    modifier: Modifier = Modifier,
) {
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
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text = "No hay platos disponibles todavía.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Cuando el menú esté publicado en Firestore, aparecerá aquí con sus categorías y destacados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FeaturedCarousel(
    featuredItems: List<MenuItem>,
    rewardProvider: (MenuItem) -> RewardPresentation?,
    onOpen: (MenuItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader(
            title = "Destacados",
            subtitle = "El equivalente Compose del featuredCarousel de SwiftUI.",
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 2.dp),
        ) {
            items(featuredItems, key = { it.id }) { item ->
                FeaturedMenuCard(
                    item = item,
                    rewardPresentation = rewardProvider(item),
                    onOpen = { onOpen(item) },
                )
            }
        }
    }
}

@Composable
private fun CategorySelectorBlock(
    selectedCategoryId: String?,
    sections: List<MenuSection>,
    onCategorySelected: (String?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionHeader(
            title = "Explorar por categoría",
            subtitle = "Muévete rápido entre entradas, sopas, platos fuertes y más.",
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todo") },
            )

            sections.forEach { section ->
                FilterChip(
                    selected = selectedCategoryId == section.category.id,
                    onClick = { onCategorySelected(section.category.id) },
                    label = { Text(section.category.title) },
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
private fun FeaturedMenuCard(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onOpen: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(292.dp)
            .height(208.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                    ),
                ),
            )
            .clickable(onClick = onOpen)
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.Top) {
                AssistChip(
                    onClick = onOpen,
                    label = { Text("Destacado") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        labelColor = Color.White,
                        leadingIconContentColor = Color.White,
                    ),
                    border = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                MenuStockBadge(item = item, onColor = Color.White)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (item.hasOffer) {
                        Text(
                            text = item.price.priceLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.72f),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        text = item.finalPrice.priceLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                rewardPresentation?.let {
                    CompactRewardRibbon(reward = it, onDark = true)
                }
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
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = section.category.title,
            subtitle = "${section.items.size} plato(s)",
        )

        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                section.items.forEachIndexed { index, item ->
                    MenuItemRow(
                        item = item,
                        rewardPresentation = rewardProvider(item),
                        onOpen = { onOpen(item) },
                    )

                    if (index < section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: MenuItem,
    rewardPresentation: RewardPresentation?,
    onOpen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (item.isFeatured) Icons.Rounded.LocalFireDepartment else Icons.Rounded.Restaurant,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (item.isFeatured) {
                    Spacer(modifier = Modifier.width(8.dp))
                    MiniStatusPill(
                        text = "Popular",
                        container = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        content = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PriceCluster(item = item)
                Spacer(modifier = Modifier.weight(1f))
                MenuStockBadge(item = item, onColor = MaterialTheme.colorScheme.onSurface)
            }

            rewardPresentation?.let {
                CompactRewardRibbon(reward = it, onDark = false)
            }

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun PriceCluster(item: MenuItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (item.hasOffer) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun MenuStockBadge(
    item: MenuItem,
    onColor: Color,
) {
    val container = when {
        item.canBeOrdered -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    }
    val content = when {
        item.canBeOrdered -> if (onColor == Color.White) Color.White else MaterialTheme.colorScheme.secondary
        else -> if (onColor == Color.White) Color.White else MaterialTheme.colorScheme.error
    }

    MiniStatusPill(
        text = item.stockLabel,
        container = container,
        content = content,
    )
}

@Composable
private fun MiniStatusPill(
    text: String,
    container: Color,
    content: Color,
) {
    Surface(
        color = container,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = content,
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MiniStatusPill(
                text = reward.badge,
                container = if (onDark) {
                    Color.White.copy(alpha = 0.18f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                },
                content = titleColor,
            )
            Text(
                text = reward.title,
                style = MaterialTheme.typography.labelLarge,
                color = titleColor,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodySmall,
                color = bodyColor,
            )
        }
    }
}

private fun Double.priceLabel(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)
