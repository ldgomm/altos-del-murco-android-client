package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPost
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPostCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPostMedia
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel.FeaturedFeedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sessionState: SessionState.Authenticated? = null,
    viewModel: FeaturedFeedViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    state.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("No se pudo cargar destacados") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) { Text("Aceptar") }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Altos del Murco",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Restaurante, aventura y momentos destacados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar destacados")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                HomeSectionHeader(
                    title = "Destacados",
                    subtitle = "Fotos recientes del restaurante, aventura y momentos de nuestros clientes.",
                    icon = Icons.Rounded.Star,
                )
            }

            when {
                state.shouldShowInitialPlaceholders -> {
                    items(2) {
                        FeaturedPostPlaceholder()
                    }
                }

                state.shouldShowEmptyState -> {
                    item {
                        HomeEmptyState(
                            title = "Aún no hay publicaciones activas.",
                            body = "Cuando ADM publique nuevas fotos aparecerán aquí automáticamente.",
                            icon = Icons.Rounded.PhotoLibrary,
                        )
                    }
                }

                else -> {
                    itemsIndexed(
                        items = state.posts,
                        key = { _, post -> post.id },
                    ) { _, post ->
                        LaunchedEffect(post.id, state.posts.lastOrNull()?.id) {
                            viewModel.loadMoreIfNeeded(post)
                        }

                        FeaturedPostCard(post = post)
                    }

                    if (state.isLoadingMore) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Cargando más",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard(
    displayName: String,
    onOpenRestaurant: () -> Unit,
    onOpenAdventure: () -> Unit,
) {
    val title = displayName
        .trim()
        .takeIf { it.isNotEmpty() }
        ?.let { "Bienvenido, ${it.firstName()}" }
        ?: "Bienvenido"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    HomeIconBubble(
                        icon = Icons.Rounded.Home,
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.weight(1f))
                    HomeBadge(text = "Los Altos", inverted = true)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        text = "Restaurante y aventura en un solo lugar. Explora experiencias, revisa tus reservas y accede rápido a cada sección.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onOpenRestaurant) {
                        Icon(Icons.Rounded.Restaurant, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Restaurante")
                    }
                    Button(onClick = onOpenAdventure) {
                        Icon(Icons.Rounded.Explore, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Aventura")
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedPostCard(post: FeaturedPost) {
    var selectedMediaIndex by remember(post.id) { mutableIntStateOf(0) }
    var isViewerPresented by remember(post.id) { mutableStateOf(false) }
    val media = post.orderedMedia

    if (isViewerPresented) {
        FeaturedMediaViewer(
            media = media,
            selectedIndex = selectedMediaIndex,
            onDismiss = { isViewerPresented = false },
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HomeIconBubble(icon = iconFor(post.category))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = post.createdAt.homePostDateText(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                HomeBadge(text = "Nuevo")
            }

            FeaturedMediaCollage(
                media = media,
                onTap = { index ->
                    selectedMediaIndex = index
                    isViewerPresented = true
                },
            )

            post.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun FeaturedMediaCollage(
    media: List<FeaturedPostMedia>,
    onTap: (Int) -> Unit,
) {
    when (media.size) {
        0 -> Unit

        1 -> CollageImage(
            item = media[0],
            index = 0,
            onTap = onTap,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        )

        2 -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CollageImage(media[0], 0, onTap, Modifier
                .weight(1f)
                .fillMaxSize())
            CollageImage(media[1], 1, onTap, Modifier
                .weight(1f)
                .fillMaxSize())
        }

        3 -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CollageImage(media[0], 0, onTap, Modifier
                .weight(1f)
                .fillMaxSize())
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CollageImage(media[1], 1, onTap, Modifier
                    .weight(1f)
                    .fillMaxWidth())
                CollageImage(media[2], 2, onTap, Modifier
                    .weight(1f)
                    .fillMaxWidth())
            }
        }

        else -> Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val displayed = media.take(4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CollageImage(displayed[0], 0, onTap, Modifier
                    .weight(1f)
                    .fillMaxSize())
                CollageImage(displayed[1], 1, onTap, Modifier
                    .weight(1f)
                    .fillMaxSize())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CollageImage(displayed[2], 2, onTap, Modifier
                    .weight(1f)
                    .fillMaxSize())
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()) {
                    CollageImage(displayed[3], 3, onTap, Modifier.fillMaxSize())
                    if (media.size > 4) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.Black.copy(alpha = 0.40f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+${media.size - 4}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollageImage(
    item: FeaturedPostMedia,
    index: Int,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    RemoteBitmapImage(
        url = item.downloadURL,
        contentDescription = "Foto destacada ${index + 1}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onTap(index) },
    )
}

@Composable
private fun FeaturedPostPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
        ),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HomeIconBubble(icon = icon)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeEmptyState(
    title: String,
    body: String,
    icon: ImageVector,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HomeIconBubble(icon = icon)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HomeIconBubble(
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
        )
    }
}

@Composable
private fun HomeBadge(
    text: String,
    inverted: Boolean = false,
) {
    val container = if (inverted) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }
    val content =
        if (inverted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = content,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

private fun iconFor(category: FeaturedPostCategory): ImageVector = when (category) {
    FeaturedPostCategory.RESTAURANT -> Icons.Rounded.LocalDining
    FeaturedPostCategory.ADVENTURE -> Icons.Rounded.Explore
    FeaturedPostCategory.CLIENTS -> Icons.Rounded.Groups
}

private fun String.firstName(): String = trim().split(Regex("\\s+")).firstOrNull().orEmpty()

private fun Date.homePostDateText(): String = postDateFormatter.format(this)

private val postDateFormatter = SimpleDateFormat("d MMM yyyy • h:mm a", Locale("es", "EC"))
