package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.TableRestaurant
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.priceLabel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CheckoutUiState
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    profile: ClientProfile,
    onBack: () -> Unit,
    onTableNumberChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = AppSectionTheme.Restaurant
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

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
                        Text(
                            text = "Confirmar pedido",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Volver",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = palette.surface.copy(alpha = 0.92f),
                        titleContentColor = palette.textPrimary,
                        navigationIconContentColor = palette.textPrimary,
                        actionIconContentColor = palette.textPrimary,
                    ),
                )
            },
            bottomBar = {
                CheckoutBottomBar(
                    theme = theme,
                    total = state.total,
                    canSubmit = state.canSubmit,
                    isSubmitting = state.isSubmitting,
                    onSubmit = onSubmit,
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
                    bottom = 132.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.isLoadingRewards || state.isSubmitting) {
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
                            theme = theme,
                            message = message,
                            onDismiss = onDismissError,
                        )
                    }
                }

                item {
                    CheckoutClientCard(
                        theme = theme,
                        profile = profile,
                    )
                }

                item {
                    TableCard(
                        theme = theme,
                        tableNumber = state.draft.tableNumber,
                        onTableNumberChanged = onTableNumberChanged,
                    )
                }

                item {
                    CheckoutItemsCard(
                        theme = theme,
                        state = state,
                    )
                }

                if (state.rewardPreview.appliedRewards.isNotEmpty()) {
                    item {
                        RewardsAppliedCard(
                            theme = theme,
                            rewards = state.rewardPreview.appliedRewards.map {
                                RewardPresentation.fromAppliedReward(it)
                            },
                        )
                    }
                }

                item {
                    OrderSummaryCard(
                        theme = theme,
                        subtotal = state.subtotal,
                        discount = state.discount,
                        total = state.total,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutClientCard(
    theme: AppSectionTheme,
    profile: ClientProfile,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Cliente",
            subtitle = "Estos datos vienen de tu perfil y no se editan aquí.",
        )

        InfoRow(
            theme = theme,
            icon = Icons.Rounded.Person,
            title = "Nombre",
            value = profile.fullName,
        )

        InfoRow(
            theme = theme,
            icon = Icons.Rounded.Badge,
            title = "Cédula",
            value = profile.nationalId,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableCard(
    theme: AppSectionTheme,
    tableNumber: String,
    onTableNumberChanged: (String) -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Mesa",
            subtitle = "Indica dónde debe llegar el pedido.",
        )

        OutlinedTextField(
            value = tableNumber,
            onValueChange = onTableNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Número o nombre de mesa") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.TableRestaurant,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.Radius.large),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary,
                focusedContainerColor = palette.elevatedCard,
                unfocusedContainerColor = palette.elevatedCard,
                disabledContainerColor = palette.card,
                focusedBorderColor = palette.primary,
                unfocusedBorderColor = palette.stroke,
                focusedLabelColor = palette.primary,
                unfocusedLabelColor = palette.textSecondary,
                cursorColor = palette.primary,
                focusedLeadingIconColor = palette.primary,
                unfocusedLeadingIconColor = palette.textSecondary,
            ),
        )
    }
}

@Composable
private fun CheckoutItemsCard(
    theme: AppSectionTheme,
    state: CheckoutUiState,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Productos",
            subtitle = "${state.draft.totalItems} producto(s) seleccionados.",
        )

        state.draft.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BrandIconBubble(
                    theme = theme,
                    icon = Icons.Rounded.RestaurantMenu,
                    size = 42.dp,
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.menuItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )

                    Text(
                        text = "x${item.safeQuantity} • ${item.unitPrice.priceLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                    )

                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.accent,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                Text(
                    text = item.totalPrice.priceLabel(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
            }

            if (index != state.draft.items.lastIndex) {
                HorizontalDivider(
                    color = palette.stroke.copy(alpha = 0.72f),
                )
            }
        }
    }
}

@Composable
private fun RewardsAppliedCard(
    theme: AppSectionTheme,
    rewards: List<RewardPresentation>,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.xLarge)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.18f else 0.08f),
                spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.18f else 0.08f),
            )
            .clip(shape)
            .background(palette.chipGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = shape,
            )
            .padding(AppTheme.Metrics.cardPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Beneficios aplicados",
            subtitle = "Se reservarán al enviar el pedido.",
        )

        rewards.forEach { reward ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BrandIconBubble(
                    theme = theme,
                    icon = Icons.Rounded.LocalOffer,
                    size = 42.dp,
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.title,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = reward.message,
                        color = palette.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                reward.amountText?.let { amount ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.heroGradient)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "-$amount",
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.onPrimary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    theme: AppSectionTheme,
    subtotal: Double,
    discount: Double,
    total: Double,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(
                theme = theme,
                emphasized = false,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BrandSectionHeader(
            theme = theme,
            title = "Resumen",
            subtitle = "Revisa el total antes de enviar tu pedido.",
        )

        SummaryLine(
            label = "Subtotal",
            value = subtotal.priceLabel(),
            labelColor = palette.textSecondary,
            valueColor = palette.textPrimary,
        )

        if (discount > 0.0) {
            SummaryLine(
                label = "Beneficios",
                value = "-${discount.priceLabel()}",
                labelColor = palette.textSecondary,
                valueColor = palette.success,
            )
        }

        HorizontalDivider(
            color = palette.stroke.copy(alpha = 0.72f),
        )

        SummaryLine(
            label = "Total",
            value = total.priceLabel(),
            labelColor = palette.textPrimary,
            valueColor = palette.textPrimary,
            emphasized = true,
        )
    }
}

@Composable
fun SummaryLine(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = labelColor,
            style = if (emphasized) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium,
        )

        Text(
            text = value,
            color = valueColor,
            style = if (emphasized) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
        )
    }
}

@Composable
private fun InfoRow(
    theme: AppSectionTheme,
    icon: ImageVector,
    title: String,
    value: String,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandIconBubble(
            theme = theme,
            icon = icon,
            size = 42.dp,
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
            )

            Text(
                text = value.ifBlank { "Sin registrar" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
        }
    }
}

@Composable
private fun ErrorCardInline(
    theme: AppSectionTheme,
    message: String,
    onDismiss: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.destructive.copy(alpha = if (darkTheme) 0.18f else 0.10f))
            .border(
                width = 1.dp,
                color = palette.destructive.copy(alpha = 0.35f),
                shape = shape,
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.WarningAmber,
            contentDescription = null,
            tint = palette.destructive,
        )

        Text(
            text = message,
            modifier = Modifier.weight(1f),
            color = palette.textPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cerrar",
                tint = palette.textSecondary,
            )
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    theme: AppSectionTheme,
    total: Double,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(
        topStart = AppTheme.Radius.xLarge,
        topEnd = AppTheme.Radius.xLarge,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = shape,
                ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.30f else 0.12f),
                spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.30f else 0.12f),
            )
            .clip(shape)
            .background(palette.cardGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = shape,
            ),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary,
                )

                Text(
                    text = total.priceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                )
            }

            BrandPrimaryButton(
                theme = theme,
                enabled = canSubmit && !isSubmitting,
                onClick = onSubmit,
                modifier = Modifier.weight(1.35f),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = palette.onPrimary,
                        trackColor = Color.Transparent,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = palette.onPrimary,
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = if (isSubmitting) "Enviando..." else "Enviar pedido",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}