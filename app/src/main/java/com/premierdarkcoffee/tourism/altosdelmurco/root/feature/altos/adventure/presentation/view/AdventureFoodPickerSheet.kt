package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.priceText
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
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    val orderedSections = menuSections.sortedWith(
        compareBy<MenuSection> { section ->
            adventureFoodCategoryDisplayOrder.indexOf(section.category.title).takeIf { it >= 0 }
                ?: Int.MAX_VALUE
        }.thenBy { it.category.title },
    )

    val visibleSections = orderedSections
        .filter { section -> selectedCategoryId == null || section.category.id == selectedCategoryId }
        .mapNotNull { section ->
            val query = searchText.trim().lowercase()
            val items = if (query.isEmpty()) {
                section.items
            } else {
                section.items.filter { item ->
                    item.name.lowercase().contains(query) ||
                            item.description.lowercase().contains(query) ||
                            item.ingredients.any { it.lowercase().contains(query) }
                }
            }
            if (items.isEmpty()) null else section.copy(items = items)
        }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AdventureSectionTitle(
                    title = "Menú del restaurante",
                    subtitle = "Agrega platos a tu reserva de aventura.",
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cerrar")
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                label = { Text("Buscar plato, bebida o ingrediente") },
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = { selectedCategoryId = null },
                    label = { Text("Todo") },
                    leadingIcon = if (selectedCategoryId == null) {
                        { Icon(Icons.Rounded.LocalDining, contentDescription = null) }
                    } else null,
                )
                orderedSections.map { it.category }.distinctBy { it.id }.forEach { category ->
                    AssistChip(
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.title) },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (visibleSections.isEmpty()) {
                    AdventureEmptyState(
                        title = "No se encontraron platos",
                        body = "Prueba otra búsqueda o cambia de categoría.",
                        icon = Icons.Rounded.Search,
                    )
                } else {
                    visibleSections.forEach { section ->
                        Text(
                            text = section.category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
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

@Composable
private fun AdventureFoodPickerRow(
    item: MenuItem,
    selectedDate: Date,
    rewardPresentation: RewardPresentation?,
    displayedPrice: Double,
    incrementalDiscount: Double,
    onAdd: (Int, String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    val blockedToday = AdventureDateHelper.isDateInToday(selectedDate) && !item.canBeOrdered

    AdventureCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AdventureIconBubble(icon = Icons.Rounded.LocalDining)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (incrementalDiscount > 0) {
                        Text(
                            text = item.finalPrice.priceText(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        text = (if (incrementalDiscount > 0) displayedPrice else item.finalPrice).priceText(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                rewardPresentation?.let { reward ->
                    Text(
                        text = "${reward.badge}: ${reward.message}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                if (blockedToday) {
                    Text(
                        text = "Por hoy está agotado y no se puede pedir.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        if (expanded) {
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { quantity = (quantity - 1).coerceAtLeast(1) }) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Menos")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { quantity += 1 }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Más")
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                label = { Text("Notas para cocina") },
            )
            Button(
                onClick = { onAdd(quantity, notes.trim().takeIf { it.isNotEmpty() }) },
                enabled = !blockedToday,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Agregar a la reserva")
            }
        } else {
            TextButton(onClick = { expanded = true }, enabled = !blockedToday) {
                Text("Elegir cantidad y notas")
            }
        }
    }
}
