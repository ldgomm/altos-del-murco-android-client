package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyLevel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStats
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumMetricTile
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumScreenHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.premiumMoney

@Composable
fun PremiumProfileDashboard(
    profile: ClientProfile,
    stats: ProfileStats,
    isLoading: Boolean,
    onEditProfile: () -> Unit,
    onOpenAccountActions: () -> Unit,
    onOpenPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PremiumScreenHeader(
            title = "Perfil",
            subtitle = "Tu identidad, nivel, beneficios y resumen de visitas.",
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        ProfileIdentityCard(
            profile = profile,
            stats = stats,
            onEditProfile = onEditProfile,
        )

        LoyaltyProgressCard(stats = stats)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PremiumMetricTile("Pedidos", stats.completedOrders.toString(), Icons.Rounded.ReceiptLong, Modifier.weight(1f))
            PremiumMetricTile("Reservas", stats.completedBookings.toString(), Icons.Rounded.CalendarMonth, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PremiumMetricTile("Restaurante", stats.restaurantSpent.premiumMoney(), Icons.Rounded.LocalDining, Modifier.weight(1f))
            PremiumMetricTile("Experiencias", stats.adventureSpent.premiumMoney(), Icons.Rounded.Star, Modifier.weight(1f))
        }

        PremiumSectionHeader(
            title = "Beneficios activos",
            subtitle = "Los premios disponibles deben verse antes del checkout.",
            icon = Icons.Rounded.EmojiEvents,
        )

        val rewards = stats.wallet.availableTemplates.filterNot { it.isExpired }.take(5)
        if (rewards.isEmpty()) {
            PremiumCard {
                Text("No tienes premios activos en este momento.", fontWeight = FontWeight.Bold)
                Text("Sigue acumulando visitas y consumo para desbloquear nuevos beneficios.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            rewards.forEach { template ->
                PremiumCard {
                    Text(template.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = template.subtitle.ifBlank { template.displaySummary },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text("Disponible para ${template.scope.title}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        PremiumSectionHeader(
            title = "Cuenta",
            subtitle = "Acciones secundarias separadas de tu tablero de beneficios.",
            icon = Icons.Rounded.Settings,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onOpenPreferences, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Settings, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Preferencias")
            }
            OutlinedButton(onClick = onOpenAccountActions, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Person, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Cuenta")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileIdentityCard(
    profile: ClientProfile,
    stats: ProfileStats,
    onEditProfile: () -> Unit,
) {
    PremiumCard(emphasized = true) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(40.dp),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(profile.fullName.ifBlank { "Cliente Altos" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Cédula ${profile.nationalId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Nivel ${stats.level.title}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onEditProfile) {
                Icon(Icons.Rounded.Edit, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Editar")
            }
        }
        Divider()
        Text(profile.phoneNumber.ifBlank { "Sin teléfono registrado" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(profile.email.ifBlank { "Sin email registrado" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LoyaltyProgressCard(stats: ProfileStats) {
    val nextLevel = stats.level.nextLevel
    val progress = LoyaltyLevel.progress(stats.totalSpent).toFloat().coerceIn(0f, 1f)

    PremiumCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PremiumIconBubble(Icons.Rounded.EmojiEvents, selected = true)
            Column(modifier = Modifier.weight(1f)) {
                Text("Murco Loyalty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text("Nivel ${stats.level.title} • ${stats.points} puntos", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(stats.totalSpent.premiumMoney(), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        Text(
            text = nextLevel?.let { "Siguiente meta: ${it.title} (${it.spendRangeText})" } ?: "Ya estás en el nivel máximo.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        stats.level.benefits.forEach { benefit ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(benefit, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
