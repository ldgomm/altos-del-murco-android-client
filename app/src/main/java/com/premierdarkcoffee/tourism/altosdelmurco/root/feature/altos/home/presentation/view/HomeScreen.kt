package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.ForkRight
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel.AdventureCatalogViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPost
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel.FeaturedFeedViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumAltosCopy
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumCard
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumEmptyState
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumGradientHero
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumIconBubble
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumMetricTile
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumScreenHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.PremiumSectionHeader
import com.premierdarkcoffee.tourism.altosdelmurco.util.ui.premiumMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated? = null,
    viewModel: FeaturedFeedViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
    adventureCatalogViewModel: AdventureCatalogViewModel = hiltViewModel(),
    onOpenRestaurant: () -> Unit = {},
    onOpenExperiences: () -> Unit = {},
    onOpenBookings: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
) {
    val feedState by viewModel.uiState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val catalogState by adventureCatalogViewModel.uiState.collectAsStateWithLifecycle()
    val profile = sessionState?.profile

    LaunchedEffect(profile?.id, profile?.updatedAt) {
        viewModel.start()
        menuViewModel.onAppear(profile?.nationalId)
        adventureCatalogViewModel.onAppear()
    }

    DisposableEffect(Unit) {
        onDispose {
            adventureCatalogViewModel.onDisappear()
        }
    }

    val errorMessage = feedState.errorMessage ?: menuState.errorMessage ?: catalogState.errorMessage
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissError()
                menuViewModel.clearError()
                adventureCatalogViewModel.clearError()
            },
            title = { Text("No se pudo actualizar Inicio") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissError()
                    menuViewModel.clearError()
                    adventureCatalogViewModel.clearError()
                }) { Text("Aceptar") }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    PremiumScreenHeader(
                        title = PremiumAltosCopy.brand,
                        subtitle = PremiumAltosCopy.promise,
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.refresh()
                        menuViewModel.onAppear(profile?.nationalId)
                        adventureCatalogViewModel.refresh()
                    }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                HomeHero(
                    clientName = profile?.fullName.orEmpty(),
                    onOpenRestaurant = onOpenRestaurant,
                    onOpenExperiences = onOpenExperiences,
                )
            }

            item {
                HomeQuickActions(
                    featuredCount = menuState.featuredItems.size,
                    packageCount = catalogState.catalog.activePackagesSorted.size,
                    rewardsCount = menuState.walletSnapshot.availableTemplates.count { !it.isExpired },
                    onOpenBookings = onOpenBookings,
                    onOpenProfile = onOpenProfile,
                )
            }

            if (menuState.featuredItems.isNotEmpty()) {
                item {
                    PremiumSectionHeader(
                        title = "Recomendados del restaurante",
                        subtitle = "Platos destacados para pedir más rápido.",
                        icon = Icons.Rounded.LocalDining,
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(menuState.featuredItems.take(8), key = { it.id }) { item ->
                            FeaturedMenuHomeCard(
                                item = item,
                                onClick = onOpenRestaurant,
                            )
                        }
                    }
                }
            }

            if (catalogState.catalog.activePackagesSorted.isNotEmpty()) {
                item {
                    PremiumSectionHeader(
                        title = "Combos de experiencias",
                        subtitle = "Primero vendemos el plan completo; luego actividades sueltas.",
                        icon = Icons.Rounded.Terrain,
                    )
                }
                items(
                    catalogState.catalog.activePackagesSorted.take(4),
                    key = { it.id }) { packageModel ->
                    ExperiencePackageHomeCard(
                        packageModel = packageModel,
                        subtotal = AdventurePricingEngine.estimatedSubtotal(
                            packageModel.items,
                            catalogState.catalog
                        ),
                        onClick = onOpenExperiences,
                    )
                }
            }

            item {
                PremiumSectionHeader(
                    title = "Momentos destacados",
                    subtitle = "Fotos recientes publicadas desde ADM.",
                    icon = Icons.Rounded.PhotoLibrary,
                )
            }

            when {
                feedState.shouldShowInitialPlaceholders -> {
                    items(2) { HomePostSkeleton() }
                }

                feedState.shouldShowEmptyState -> {
                    item {
                        PremiumEmptyState(
                            title = "Aún no hay publicaciones activas",
                            body = "Cuando ADM publique nuevas fotos aparecerán aquí automáticamente.",
                            icon = Icons.Rounded.PhotoLibrary,
                        )
                    }
                }

                else -> {
                    items(feedState.posts, key = { it.id }) { post ->
                        PremiumFeaturedPostCard(post = post)
                        LaunchedEffect(post.id) {
                            viewModel.loadMoreIfNeeded(post)
                        }
                    }
                    if (feedState.isLoadingMore) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHero(
    clientName: String,
    onOpenRestaurant: () -> Unit,
    onOpenExperiences: () -> Unit,
) {
    val greeting =
        clientName.trim().takeIf { it.isNotEmpty() }?.let { "Hola, ${it.substringBefore(' ')}" }
            ?: "Bienvenido"
    PremiumGradientHero(
        title = "$greeting. Vive Altos del Murco.",
        subtitle = "Pide comida, reserva experiencias, arma combos con comida incluida y aprovecha premios Murco Loyalty en un solo lugar.",
        badge = "Restaurante + Experiencias",
        primaryAction = {
            Button(onClick = onOpenRestaurant, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Restaurant, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text(PremiumAltosCopy.restaurantCta)
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onOpenExperiences, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Explore, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text(PremiumAltosCopy.experiencesCta)
            }
        },
    )
}

@Composable
private fun HomeQuickActions(
    featuredCount: Int,
    packageCount: Int,
    rewardsCount: Int,
    onOpenBookings: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PremiumMetricTile(
                "Platos destacados",
                featuredCount.toString(),
                Icons.Rounded.ForkRight,
                Modifier.weight(1f)
            )
            PremiumMetricTile(
                "Combos activos",
                packageCount.toString(),
                Icons.Rounded.Terrain,
                Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PremiumMetricTile(
                "Premios",
                rewardsCount.toString(),
                Icons.Rounded.Sell,
                Modifier.weight(1f)
            )
            Card(
                onClick = onOpenBookings,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.60f
                    )
                ),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremiumIconBubble(
                        Icons.Rounded.CalendarMonth,
                        modifier = Modifier.height(38.dp)
                    )
                    Text(
                        "Reservas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Ver agenda",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        PremiumCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumIconBubble(Icons.Rounded.Star, selected = true)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Murco Loyalty visible antes de pagar", fontWeight = FontWeight.Bold)
                    Text(
                        "Cada descuento debe aparecer como dinero real: -$3.00, item gratis o porcentaje aplicado.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onOpenProfile) { Text("Ver") }
            }
        }
    }
}

@Composable
private fun FeaturedMenuHomeCard(
    item: MenuItem,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PremiumIconBubble(Icons.Rounded.LocalDining)
            Text(
                item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Text(
                item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.hasOffer) {
                    Text(
                        item.price.premiumMoney(),
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    item.finalPrice.premiumMoney(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = if (item.canBeOrdered) "Disponible hoy" else "No disponible hoy / reservas futuras",
                style = MaterialTheme.typography.labelSmall,
                color = if (item.canBeOrdered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun ExperiencePackageHomeCard(
    packageModel: AdventureFeaturedPackage,
    subtotal: Double,
    onClick: () -> Unit,
) {
    val comboDiscount = packageModel.packageDiscountAmount.coerceAtLeast(0.0)
    val finalTotal = (subtotal - comboDiscount).coerceAtLeast(0.0)
    PremiumCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            PremiumIconBubble(Icons.Rounded.Terrain, selected = true)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    packageModel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(packageModel.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!packageModel.badge.isNullOrBlank()) {
                    Text(
                        packageModel.badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Subtotal ${subtotal.premiumMoney()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (comboDiscount > 0) Text(
                "Ahorra ${comboDiscount.premiumMoney()}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Desde ${finalTotal.premiumMoney()} • Reservar")
        }
    }
}

@Composable
private fun PremiumFeaturedPostCard(post: FeaturedPost) {
    PremiumCard {
        Text(
            "Altos del Murco",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        post.description?.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            "Publicado desde ADM",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun HomePostSkeleton() {
    PremiumCard {
        Text("Cargando destacados...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(90.dp))
    }
}
