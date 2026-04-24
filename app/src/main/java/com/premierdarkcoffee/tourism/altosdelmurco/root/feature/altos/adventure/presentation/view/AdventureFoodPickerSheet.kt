package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppSectionTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.AppTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandPrimaryButton
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.BrandSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandDarkTheme
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.LocalBrandPalette
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.appCardStyle
import java.util.Date

private val adventureFoodCategoryDisplayOrder = listOf(
    "Entradas",
    "Sopas",
    "Platos Fuertes",
    "Extras",
    "Postres",
    "Bebidas",
    "Bebidas Alcohólicas",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureFoodPickerSheet(
    menuSections: List<MenuSection>,
    selectedDate: Date,
    rewardPresentationProvider: (MenuItem, Int) -> RewardPresentation?,
    displayedPriceProvider: (MenuItem, Int) -> Double,
    incrementalDiscountProvider: (MenuItem, Int) -> Double,
    onDismiss: () -> Unit,
    onAdd: (MenuItem, Int, String?) -> Unit,
) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandDarkTheme.current

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val orderedSections = remember(menuSections) {
        menuSections.sortedWith(
            compareBy<MenuSection> { section ->
                adventureFoodCategoryDisplayOrder
                    .indexOf(section.category.title)
                    .takeIf { it >= 0 }
                    ?: Int.MAX_VALUE
            }.thenBy { it.category.title },
        )
    }

    val visibleSections = remember(
        orderedSections,
        selectedCategoryId,
        searchText,
    ) {
        val query = searchText.trim().lowercase()

        orderedSections
            .filter { section ->
                selectedCategoryId == null || section.category.id == selectedCategoryId
            }
            .mapNotNull { section ->
                val items = if (query.isEmpty()) {
                    section.items
                } else {
                    section.items.filter { item ->
                        item.name.lowercase().contains(query) ||
                                item.description.lowercase().contains(query) ||
                                item.ingredients.any { ingredient ->
                                    ingredient.lowercase().contains(query)
                                }
                    }
                }

                if (items.isEmpty()) null else section.copy(items = items)
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.Transparent) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.92f)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BrandSectionHeader(
                        theme = theme,
                        title = "Menú del restaurante",
                        subtitle = "Agrega platos a tu reserva de aventura.",
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cerrar",
                        )
                    }
                }

                AdventureFoodSearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                )

                AdventureFoodCategoryChips(
                    orderedSections = orderedSections,
                    selectedCategoryId = selectedCategoryId,
                    onSelectedCategoryChange = { selectedCategoryId = it },
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (visibleSections.isEmpty()) {
                        AdventureFoodEmptyState()
                    } else {
                        visibleSections.forEach { section ->
                            Text(
                                text = section.category.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                            )

                            section.items.forEach { item ->
                                AdventureFoodPickerRow(
                                    item = item,
                                    selectedDate = selectedDate,
                                    rewardPresentation = rewardPresentationProvider(item, 1),
                                    displayedPrice = displayedPriceProvider(item, 1),
                                    incrementalDiscount = incrementalDiscountProvider(item, 1),
                                    onAdd = { quantity, notes ->
                                        onAdd(item, quantity, notes)
                                        onDismiss()
                                    },
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AdventureFoodSearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val palette = LocalBrandPalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppTheme.Radius.large),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        },
        label = { Text("Buscar plato, bebida o ingrediente") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            disabledTextColor = palette.textTertiary,

            focusedContainerColor = palette.elevatedCard,
            unfocusedContainerColor = palette.elevatedCard,
            disabledContainerColor = palette.card,

            cursorColor = palette.primary,

            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.stroke,
            disabledBorderColor = palette.stroke.copy(alpha = 0.55f),

            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,

            focusedLeadingIconColor = palette.primary,
            unfocusedLeadingIconColor = palette.textSecondary,
        ),
    )
}

@Composable
private fun AdventureFoodCategoryChips(
    orderedSections: List<MenuSection>,
    selectedCategoryId: String?,
    onSelectedCategoryChange: (String?) -> Unit,
) {
    val theme = AppSectionTheme.Adventure

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AdventureSelectableBadge(
            title = "Todo",
            selected = selectedCategoryId == null,
            onClick = { onSelectedCategoryChange(null) },
            leadingIcon = Icons.Rounded.LocalDining,
        )

        orderedSections
            .map { it.category }
            .distinctBy { it.id }
            .forEach { category ->
                AdventureSelectableBadge(
                    title = category.title,
                    selected = selectedCategoryId == category.id,
                    onClick = { onSelectedCategoryChange(category.id) },
                )
            }
    }
}

@Composable
private fun AdventureSelectableBadge(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(
                brush = if (selected) {
                    palette.heroGradient
                } else {
                    palette.chipGradient
                },
            )
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else palette.stroke,
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selected) palette.onPrimary else palette.primary,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) palette.onPrimary else palette.primary,
        )
    }
}

@Composable
private fun AdventureFoodEmptyState() {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BrandIconBubble(
            theme = theme,
            icon = Icons.Rounded.Search,
            size = 54.dp,
        )

        Text(
            text = "No se encontraron platos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )

        Text(
            text = "Prueba otra búsqueda o cambia de categoría.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun AdventureFoodPickerRow(
    item: MenuItem,
    selectedDate: Date,
    rewardPresentation: RewardPresentation?,
    displayedPrice: Double,
    incrementalDiscount: Double,
    onAdd: (Int, String?) -> Unit,
) {
    val theme = AppSectionTheme.Adventure
    val palette = LocalBrandPalette.current

    var expanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }

    val blockedToday = AdventureDateHelper.isDateInToday(selectedDate) && !item.canBeOrdered
    val originalPrice = item.finalPrice
    val effectivePrice = if (incrementalDiscount > 0) displayedPrice else item.finalPrice

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appCardStyle(theme = theme),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BrandIconBubble(
                theme = theme,
                icon = Icons.Rounded.LocalDining,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (incrementalDiscount > 0) {
                        Text(
                            text = originalPrice.priceText(),
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textTertiary,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }

                    Text(
                        text = effectivePrice.priceText(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                }

                rewardPresentation?.let { reward ->
                    AdventureRewardBadge(reward = reward)
                }

                if (blockedToday) {
                    Text(
                        text = "Por hoy está agotado y no se puede pedir.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.destructive,
                    )
                }
            }
        }

        if (expanded) {
            HorizontalDivider(color = palette.stroke)

            QuantitySelector(
                quantity = quantity,
                onDecrease = { quantity = (quantity - 1).coerceAtLeast(1) },
                onIncrease = { quantity += 1 },
            )

            AdventureFoodNotesField(
                value = notes,
                onValueChange = { notes = it },
            )

            BrandPrimaryButton(
                theme = theme,
                onClick = {
                    onAdd(
                        quantity,
                        notes.trim().takeIf { it.isNotEmpty() },
                    )
                },
                enabled = !blockedToday,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Agregar a la reserva",
                    color = palette.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            TextButton(
                onClick = { expanded = true },
                enabled = !blockedToday,
            ) {
                Text(
                    text = "Elegir cantidad y notas",
                    color = if (blockedToday) palette.textTertiary else palette.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun AdventureRewardBadge(
    reward: RewardPresentation,
) {
    val palette = LocalBrandPalette.current

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(palette.chipGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = CircleShape,
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Text(
            text = "${reward.badge}: ${reward.message}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = palette.primary,
        )
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    val palette = LocalBrandPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(palette.chipGradient)
                .border(
                    width = 1.dp,
                    color = palette.stroke,
                    shape = CircleShape,
                ),
        ) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "Menos",
                tint = palette.primary,
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )

        IconButton(
            onClick = onIncrease,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(palette.chipGradient)
                .border(
                    width = 1.dp,
                    color = palette.stroke,
                    shape = CircleShape,
                ),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Más",
                tint = palette.primary,
            )
        }
    }
}

@Composable
private fun AdventureFoodNotesField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val palette = LocalBrandPalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppTheme.Radius.large),
        label = { Text("Notas para cocina") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            disabledTextColor = palette.textTertiary,

            focusedContainerColor = palette.elevatedCard,
            unfocusedContainerColor = palette.elevatedCard,
            disabledContainerColor = palette.card,

            cursorColor = palette.primary,

            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.stroke,
            disabledBorderColor = palette.stroke.copy(alpha = 0.55f),

            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,
        ),
    )
}