package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandBadge
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MenuItemDetailScreen(
    item: MenuItem,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onAddToCart: (MenuItem, Int, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Restaurant
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    var quantity by rememberSaveable(item.id) { mutableIntStateOf(1) }
    var notes by rememberSaveable(item.id) { mutableStateOf("") }

    val maxQuantity = item.remainingQuantity.coerceAtLeast(1)
    val safeQuantity = quantity.coerceIn(1, maxQuantity)

    LaunchedEffect(safeQuantity, maxQuantity) {
        if (quantity != safeQuantity) {
            quantity = safeQuantity
        }
    }

    val baseSubtotal = item.finalPrice * safeQuantity
    val displayedTotal = displayedPriceProvider(item, safeQuantity)
    val incrementalDiscount = incrementalDiscountProvider(item, safeQuantity)
    val rewardPresentation = rewardPresentationProvider(item, safeQuantity)

    BrandScreen(
        theme = theme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = palette.textPrimary,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Detalle del plato",
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Volver",
                                tint = palette.textPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = palette.surface.copy(alpha = 0.92f),
                        navigationIconContentColor = palette.textPrimary,
                        titleContentColor = palette.textPrimary,
                        actionIconContentColor = palette.textPrimary,
                    ),
                )
            },
            bottomBar = {
                DetailBottomBar(
                    theme = theme,
                    canAdd = item.canBeOrdered,
                    total = displayedTotal,
                    quantity = safeQuantity,
                    onAdd = {
                        onAddToCart(
                            item,
                            safeQuantity,
                            notes.trim().takeIf { it.isNotEmpty() },
                        )
                    },
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
                    bottom = 142.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    DetailHero(
                        theme = theme,
                        item = item,
                        displayedTotal = displayedTotal,
                        baseSubtotal = baseSubtotal,
                        hasRewardDiscount = incrementalDiscount > 0.0,
                    )
                }

                item {
                    DetailCard(
                        theme = theme,
                        title = "Descripción",
                    ) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = palette.textSecondary,
                            lineHeight = 22.sp,
                        )
                    }
                }

                rewardPresentation?.let { reward ->
                    item {
                        RewardDetailCard(
                            theme = theme,
                            reward = reward,
                        )
                    }
                }

                item {
                    DetailCard(
                        theme = theme,
                        title = "Cantidad",
                    ) {
                        QuantityStepper(
                            theme = theme,
                            quantity = safeQuantity,
                            maxQuantity = maxQuantity,
                            enabled = item.canBeOrdered,
                            onQuantityChanged = { quantity = it },
                        )
                    }
                }

                item {
                    DetailCard(
                        theme = theme,
                        title = "Notas para cocina",
                    ) {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it.take(220) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            label = {
                                Text("Ej. sin cebolla, más cocido, sin ají")
                            },
                            shape = RoundedCornerShape(AppTheme.Radius.large),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = palette.textPrimary,
                                unfocusedTextColor = palette.textPrimary,
                                focusedLabelColor = palette.primary,
                                unfocusedLabelColor = palette.textSecondary,
                                cursorColor = palette.primary,
                                focusedBorderColor = palette.primary,
                                unfocusedBorderColor = palette.stroke,
                                focusedContainerColor = palette.elevatedCard,
                                unfocusedContainerColor = palette.elevatedCard,
                            ),
                        )

                        Text(
                            text = "${notes.length}/220",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textTertiary,
                            modifier = Modifier.align(Alignment.End),
                        )
                    }
                }

                item {
                    DetailCard(
                        theme = theme,
                        title = "Total",
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryRow(
                                theme = theme,
                                title = "Cantidad",
                                value = "x$safeQuantity",
                            )

                            SummaryRow(
                                theme = theme,
                                title = "Unitario",
                                value = item.finalPrice.priceLabel(),
                            )

                            if (incrementalDiscount > 0.0) {
                                SummaryRow(
                                    theme = theme,
                                    title = "Subtotal",
                                    value = baseSubtotal.priceLabel(),
                                )

                                SummaryRow(
                                    theme = theme,
                                    title = "Beneficio",
                                    value = "-${incrementalDiscount.priceLabel()}",
                                    valueColor = palette.success,
                                )

                                HorizontalDivider(
                                    color = palette.stroke,
                                    thickness = 1.dp,
                                )
                            }

                            SummaryRow(
                                theme = theme,
                                title = "Total",
                                value = displayedTotal.priceLabel(),
                                emphasized = true,
                            )
                        }
                    }
                }

                if (!item.notes.isNullOrBlank()) {
                    item {
                        DetailCard(
                            theme = theme,
                            title = "Notas del plato",
                        ) {
                            Text(
                                text = item.notes.orEmpty(),
                                color = palette.textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                if (item.ingredients.isNotEmpty()) {
                    item {
                        DetailCard(
                            theme = theme,
                            title = "Ingredientes",
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                item.ingredients.forEach { ingredient ->
                                    IngredientChip(
                                        theme = theme,
                                        title = ingredient,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHero(
    theme: AppSectionTheme,
    item: MenuItem,
    displayedTotal: Double,
    baseSubtotal: Double,
    hasRewardDiscount: Boolean,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.16f),
                shape = RoundedCornerShape(32.dp),
            )
            .padding(22.dp),
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.TopEnd)
                .offset(x = 62.dp, y = (-56).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-58).dp, y = 52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f)),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BrandIconBubble(
                    theme = theme,
                    icon = Icons.Rounded.Restaurant,
                    size = 66.dp,
                    contentDescription = null,
                )

                BrandBadge(
                    theme = theme,
                    title = if (item.canBeOrdered) "Disponible" else "No disponible",
                    selected = item.canBeOrdered,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.onPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (hasRewardDiscount) {
                        Text(
                            text = baseSubtotal.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.onPrimary.copy(alpha = 0.72f),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = displayedTotal.priceLabel(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Text(
                    text = item.stockLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onPrimary.copy(alpha = 0.88f),
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    theme: AppSectionTheme,
    title: String,
    emphasized: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(
                theme = theme,
                emphasized = emphasized,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RestaurantSectionHeader(
            theme = theme,
            title = title,
        )

        content()
    }
}

@Composable
private fun QuantityStepper(
    theme: AppSectionTheme,
    quantity: Int,
    maxQuantity: Int,
    enabled: Boolean,
    onQuantityChanged: (Int) -> Unit,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantityButton(
            theme = theme,
            enabled = enabled && quantity > 1,
            icon = Icons.Rounded.Remove,
            contentDescription = "Menos",
            onClick = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = "Máx. $maxQuantity",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textTertiary,
            )
        }

        QuantityButton(
            theme = theme,
            enabled = enabled && quantity < maxQuantity,
            icon = Icons.Rounded.Add,
            contentDescription = "Más",
            onClick = { onQuantityChanged((quantity + 1).coerceAtMost(maxQuantity)) },
        )
    }
}

@Composable
private fun QuantityButton(
    theme: AppSectionTheme,
    enabled: Boolean,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (enabled) {
                    palette.chipGradient
                } else {
                    Brush.linearGradient(
                        listOf(
                            palette.stroke.copy(alpha = 0.25f),
                            palette.stroke.copy(alpha = 0.12f),
                        )
                    )
                },
            )
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = CircleShape,
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) palette.primary else palette.textTertiary,
        )
    }
}

@Composable
private fun RewardDetailCard(
    theme: AppSectionTheme,
    reward: RewardPresentation,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.Radius.xLarge))
            .background(palette.chipGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = RoundedCornerShape(AppTheme.Radius.xLarge),
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BrandIconBubble(
                theme = theme,
                icon = Icons.Rounded.Redeem,
                size = 42.dp,
                contentDescription = null,
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = reward.badge,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.primary,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Text(
            text = reward.message,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun DetailBottomBar(
    theme: AppSectionTheme,
    canAdd: Boolean,
    total: Double,
    quantity: Int,
    onAdd: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Surface(
        color = palette.surface.copy(alpha = if (darkTheme) 0.96f else 0.94f),
        tonalElevation = 0.dp,
        shadowElevation = 14.dp,
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )

                Text(
                    text = total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            BrandPrimaryButton(
                theme = theme,
                enabled = canAdd,
                onClick = onAdd,
                modifier = Modifier.weight(1.45f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    tint = palette.onPrimary,
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Añadir x$quantity",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    theme: AppSectionTheme,
    title: String,
    value: String,
    emphasized: Boolean = false,
    valueColor: Color? = null,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = if (emphasized) palette.textPrimary else palette.textSecondary,
            style = if (emphasized) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            color = valueColor ?: if (emphasized) palette.primary else palette.textPrimary,
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
            style = if (emphasized) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
        )
    }
}

@Composable
private fun IngredientChip(
    theme: AppSectionTheme,
    title: String,
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = palette.stroke,
        ),
    ) {
        Text(
            text = title,
            modifier = Modifier
                .background(palette.chipGradient)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = palette.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RestaurantSectionHeader(
    theme: AppSectionTheme,
    title: String,
    subtitle: String = "",
) {
    val palette = AppTheme.palette(theme, LocalBrandDarkTheme.current)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(palette.heroGradient),
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
            )
        }
    }
}