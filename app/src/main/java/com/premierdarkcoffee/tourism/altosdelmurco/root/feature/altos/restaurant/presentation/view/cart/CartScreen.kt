package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart

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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.SectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CartUiState

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
    val lineDiscounts = state.allocatedDiscountByCartItemId()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!state.isEmpty) {
                        TextButton(onClick = onClearCart) {
                            Text("Limpiar")
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
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                        SectionHeader(
                            title = "Tu pedido",
                            subtitle = if (state.isLoadingRewards) {
                                "Calculando premios Murco Loyalty para ${state.totalItems} producto(s)."
                            } else {
                                "${state.totalItems} producto(s) listos para enviar."
                            },
                        )
                    }

                    items(state.items, key = { it.id }) { item ->
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

@Composable
private fun EmptyCart(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCartCheckout,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Agrega platos desde el menú para crear tu pedido.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onBack) {
                    Text("Volver al menú")
                }
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
    val discountedLineTotal = (item.totalPrice - allocatedDiscount).coerceAtLeast(0.0)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp),
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.menuItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Unitario ${item.unitPrice.priceLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                        Text(
                            text = discountedLineTotal.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "-${allocatedDiscount.priceLabel()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    } else {
                        Text(
                            text = item.totalPrice.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            rewards.forEach { reward ->
                CompactCartRewardRibbon(reward = reward)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = onRemove) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Quitar")
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onDecrease) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Menos")
                }

                Text(
                    text = item.safeQuantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Rounded.Add, contentDescription = "Más")
                }
            }
        }
    }
}

@Composable
private fun CompactCartRewardRibbon(reward: RewardPresentation) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = reward.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            reward.amountText?.let { amount ->
                Text(
                    text = "-$amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
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
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = reward.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                        )
                    }

                    reward.amountText?.let { amount ->
                        Text(
                            text = "-$amount",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
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
    Surface(shadowElevation = 10.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (isLoadingRewards) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (discount > 0.0) {
                        Text(
                            text = subtotal.priceLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = total.priceLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (discount > 0.0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }

                Button(
                    enabled = canCheckout,
                    onClick = onCheckout,
                    modifier = Modifier.weight(1.25f),
                ) {
                    Icon(Icons.Rounded.ShoppingCartCheckout, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Checkout")
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
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
internal fun OrderSummaryCard(
    subtotal: Double,
    discount: Double,
    total: Double,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionHeader(title = "Resumen")
            SummaryLine("Subtotal", subtotal.priceLabel())
            if (discount > 0.0) {
                SummaryLine("Murco Loyalty", "-${discount.priceLabel()}")
            }
            HorizontalDivider()
            SummaryLine("Total", total.priceLabel(), emphasized = true)
        }
    }
}

@Composable
internal fun SummaryLine(
    title: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            color = if (title == "Murco Loyalty") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}
