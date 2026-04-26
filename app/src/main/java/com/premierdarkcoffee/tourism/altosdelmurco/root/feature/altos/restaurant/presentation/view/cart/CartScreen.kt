package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCartCheckout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonDefaults
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CartUiState
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSecondaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle

private val CartTheme = AppSectionTheme.Restaurant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearCart: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)
    val lineDiscounts = state.allocatedDiscountByCartItemId()

    BrandScreen(
        theme = CartTheme,
        modifier = modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = palette.textPrimary,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = palette.surface.copy(alpha = 0.92f),
                        titleContentColor = palette.textPrimary,
                        navigationIconContentColor = palette.textPrimary,
                        actionIconContentColor = palette.primary,
                    ),
                    title = {
                        Text(
                            text = "Carrito",
                            fontWeight = FontWeight.ExtraBold,
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
                    actions = {
                        if (!state.isEmpty) {
                            TextButton(
                                onClick = onClearCart,
                                colors = TextButtonDefaults.textButtonColors(
                                    contentColor = palette.primary,
                                ),
                            ) {
                                Text(
                                    text = "Limpiar",
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                if (!state.isEmpty) {
                    CartBottomBar(
                        subtotal = state.subtotal,
                        discount = state.discount,
                        total = state.total,
                        isLoadingRewards = state.isLoadingRewards,
                        canCheckout = state.canCheckout,
                        onCheckout = onCheckout,
                    )
                }
            },
        ) { innerPadding ->
            when {
                state.isEmpty -> {
                    EmptyCart(
                        onBack = onBack,
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
                            bottom = 150.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        if (state.isLoadingRewards) {
                            item {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = palette.primary,
                                    trackColor = palette.stroke.copy(alpha = 0.45f),
                                )
                            }
                        }

                        state.errorMessage?.let { message ->
                            item {
                                ErrorCardInline(
                                    message = message,
                                    onDismiss = onDismissError,
                                )
                            }
                        }

                        item {
                            BrandSectionHeader(
                                theme = CartTheme,
                                title = "Tu pedido",
                                subtitle = if (state.isLoadingRewards) {
                                    "Calculando premios Murco Loyalty para ${state.totalItems} producto(s)."
                                } else {
                                    "${state.totalItems} producto(s) listos para enviar."
                                },
                            )
                        }

                        items(
                            items = state.items,
                            key = { it.id },
                        ) { item ->
                            CartItemCard(
                                item = item,
                                allocatedDiscount = lineDiscounts[item.id] ?: 0.0,
                                rewards = state.appliedRewardPresentations(item.menuItem.id),
                                onIncrease = { onIncrease(item.id) },
                                onDecrease = { onDecrease(item.id) },
                                onRemove = { onRemove(item.id) },
                            )
                        }

                        if (state.appliedRewards.isNotEmpty()) {
                            item {
                                AppliedRewardsCard(
                                    rewards = state.appliedRewards.map(RewardPresentation::fromAppliedReward),
                                )
                            }
                        }

                        item {
                            OrderSummaryCard(
                                subtotal = state.subtotal,
                                discount = state.discount,
                                total = state.total,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCart(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appCardStyle(
                    theme = CartTheme
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandIconBubble(
                theme = CartTheme,
                icon = Icons.Rounded.ShoppingCartCheckout,
                size = 58.dp,
            )

            Text(
                text = "Tu carrito está vacío",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )

            Text(
                text = "Agrega platos desde el menú para crear tu pedido.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
            )

            BrandPrimaryButton(
                theme = CartTheme,
                onClick = onBack,
            ) {
                Text(
                    text = "Volver al menú",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    allocatedDiscount: Double,
    rewards: List<RewardPresentation>,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)
    val discountedLineTotal = (item.totalPrice - allocatedDiscount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(CartTheme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrandIconBubble(
                theme = CartTheme,
                icon = Icons.Rounded.Restaurant,
                size = 44.dp,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "Unitario ${item.unitPrice.priceLabel()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                )

                if (!item.notes.isNullOrBlank()) {
                    Text(
                        text = item.notes.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                if (allocatedDiscount > 0.0) {
                    Text(
                        text = item.totalPrice.priceLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textTertiary,
                        textDecoration = TextDecoration.LineThrough,
                    )

                    Text(
                        text = discountedLineTotal.priceLabel(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.primary,
                    )

                    Text(
                        text = "-${allocatedDiscount.priceLabel()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                } else {
                    Text(
                        text = item.totalPrice.priceLabel(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.textPrimary,
                    )
                }
            }
        }

        rewards.forEach { reward ->
            CompactCartRewardRibbon(reward = reward)
        }

        HorizontalDivider(color = palette.stroke)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandSecondaryButton(
                theme = CartTheme,
                onClick = onRemove,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = palette.textPrimary,
                    modifier = Modifier.size(18.dp),
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "Quitar",
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.weight(0.35f))

            IconButton(onClick = onDecrease) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Menos",
                    tint = palette.primary,
                )
            }

            Text(
                text = item.safeQuantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )

            IconButton(onClick = onIncrease) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Más",
                    tint = palette.primary,
                )
            }
        }
    }
}

@Composable
private fun CompactCartRewardRibbon(
    reward: RewardPresentation,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = palette.chipGradient,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalOffer,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(18.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = reward.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            reward.amountText?.let { amount ->
                Text(
                    text = "-$amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.primary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun AppliedRewardsCard(
    rewards: List<RewardPresentation>,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(
                theme = CartTheme,
                emphasized = true,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = CartTheme,
            title = "Premios aplicados",
            subtitle = "Estos beneficios ya se reflejan en el total del carrito.",
        )

        rewards.forEach { reward ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalOffer,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(20.dp),
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = reward.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                    )
                }

                reward.amountText?.let { amount ->
                    Text(
                        text = "-$amount",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.primary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    subtotal: Double,
    discount: Double,
    total: Double,
    isLoadingRewards: Boolean,
    canCheckout: Boolean,
    onCheckout: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Surface(
        color = palette.surface.copy(alpha = if (darkTheme) 0.96f else 0.94f),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (isLoadingRewards) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = palette.primary,
                    trackColor = palette.stroke.copy(alpha = 0.45f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isLoadingRewards -> "Calculando beneficios"
                            discount > 0.0 -> "Total con Murco Loyalty"
                            else -> "Subtotal"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary,
                    )

                    if (discount > 0.0) {
                        Text(
                            text = subtotal.priceLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textTertiary,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = total.priceLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (discount > 0.0) {
                            palette.primary
                        } else {
                            palette.textPrimary
                        },
                    )
                }

                BrandPrimaryButton(
                    theme = CartTheme,
                    enabled = canCheckout,
                    onClick = onCheckout,
                    modifier = Modifier.weight(1.25f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ShoppingCartCheckout,
                        contentDescription = null,
                        tint = palette.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Checkout",
                        color = palette.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ErrorCardInline(
    message: String,
    onDismiss: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(CartTheme),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = message,
            color = palette.destructive,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )

        TextButton(
            onClick = onDismiss,
            colors = TextButtonDefaults.textButtonColors(
                contentColor = palette.primary,
            ),
        ) {
            Text("Cerrar")
        }
    }
}

@Composable
internal fun OrderSummaryCard(
    subtotal: Double,
    discount: Double,
    total: Double,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(CartTheme),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BrandSectionHeader(
            theme = CartTheme,
            title = "Resumen",
        )

        SummaryLine("Subtotal", subtotal.priceLabel())

        if (discount > 0.0) {
            SummaryLine("Murco Loyalty", "-${discount.priceLabel()}")
        }

        HorizontalDivider(color = palette.stroke)

        SummaryLine(
            title = "Total",
            value = total.priceLabel(),
            emphasized = true,
        )
    }
}

@Composable
internal fun SummaryLine(
    title: String,
    value: String,
    emphasized: Boolean = false,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(CartTheme, darkTheme)
    val isRewardLine = title == "Murco Loyalty"

    Row(modifier = Modifier.fillMaxWidth()) {
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
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
            style = if (emphasized) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = when {
                emphasized -> palette.textPrimary
                isRewardLine -> palette.primary
                else -> palette.textPrimary
            },
        )
    }
}