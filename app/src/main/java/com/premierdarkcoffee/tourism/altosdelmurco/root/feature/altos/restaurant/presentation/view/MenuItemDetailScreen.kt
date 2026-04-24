package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem

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
    var quantity by rememberSaveable(item.id) { mutableIntStateOf(1) }
    var notes by rememberSaveable(item.id) { mutableStateOf("") }

    val safeQuantity = quantity.coerceIn(1, item.remainingQuantity.coerceAtLeast(1))
    val baseSubtotal = item.finalPrice * safeQuantity
    val displayedTotal = displayedPriceProvider(item, safeQuantity)
    val incrementalDiscount = incrementalDiscountProvider(item, safeQuantity)
    val rewardPresentation = rewardPresentationProvider(item, safeQuantity)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del plato") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
        bottomBar = {
            DetailBottomBar(
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 142.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                DetailHero(
                    item = item,
                    displayedTotal = displayedTotal,
                    baseSubtotal = baseSubtotal,
                    hasRewardDiscount = incrementalDiscount > 0.0,
                )
            }

            item {
                DetailCard(title = "Descripción") {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            rewardPresentation?.let { reward ->
                item {
                    RewardDetailCard(reward = reward)
                }
            }

            item {
                DetailCard(title = "Cantidad") {
                    QuantityStepper(
                        quantity = safeQuantity,
                        maxQuantity = item.remainingQuantity.coerceAtLeast(1),
                        enabled = item.canBeOrdered,
                        onQuantityChanged = { quantity = it },
                    )
                }
            }

            item {
                DetailCard(title = "Notas para cocina") {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it.take(220) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Ej. sin cebolla, más cocido, sin ají") },
                    )
                }
            }

            item {
                DetailCard(title = "Total") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryRow("Cantidad", "x$safeQuantity")
                        SummaryRow("Unitario", item.finalPrice.priceLabel())

                        if (incrementalDiscount > 0.0) {
                            SummaryRow("Subtotal", baseSubtotal.priceLabel())
                            SummaryRow("Beneficio", "-${incrementalDiscount.priceLabel()}")
                            HorizontalDivider()
                        }

                        SummaryRow(
                            title = "Total",
                            value = displayedTotal.priceLabel(),
                            emphasized = true,
                        )
                    }
                }
            }

            if (!item.notes.isNullOrBlank()) {
                item {
                    DetailCard(title = "Notas del plato") {
                        Text(
                            text = item.notes.orEmpty(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (item.ingredients.isNotEmpty()) {
                item {
                    DetailCard(title = "Ingredientes") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item.ingredients.forEach { ingredient ->
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
    item: MenuItem,
    displayedTotal: Double,
    baseSubtotal: Double,
    hasRewardDiscount: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            )
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.ExtraBold,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (hasRewardDiscount) {
                        Text(
                            text = baseSubtotal.priceLabel(),
                            style = MaterialTheme.typography.titleMedium,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = displayedTotal.priceLabel(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Text(
                    text = item.stockLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.90f),
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(title = title)
            content()
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    maxQuantity: Int,
    enabled: Boolean,
    onQuantityChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = enabled && quantity > 1,
            onClick = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Menos")
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
        )

        IconButton(
            enabled = enabled && quantity < maxQuantity,
            onClick = { onQuantityChanged((quantity + 1).coerceAtMost(maxQuantity)) },
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Más")
        }
    }
}

@Composable
private fun RewardDetailCard(reward: RewardPresentation) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = reward.badge,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = reward.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun DetailBottomBar(
    canAdd: Boolean,
    total: Double,
    quantity: Int,
    onAdd: () -> Unit,
) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Button(
                enabled = canAdd,
                onClick = onAdd,
                modifier = Modifier.weight(1.45f),
            ) {
                Icon(Icons.Rounded.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Añadir x$quantity")
            }
        }
    }
}

@Composable
private fun SummaryRow(
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
        )
    }
}
