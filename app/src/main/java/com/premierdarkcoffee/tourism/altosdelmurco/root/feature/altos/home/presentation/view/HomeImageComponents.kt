package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material.icons.rounded.NavigateBefore
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPostMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
internal fun RemoteBitmapImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderIcon: ImageVector = Icons.Rounded.PhotoLibrary,
) {
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(url) { mutableStateOf(false) }
    var didFail by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        bitmap = null
        didFail = false
        val cleanUrl = url?.trim().orEmpty()
        if (cleanUrl.isEmpty()) {
            didFail = true
            return@LaunchedEffect
        }

        isLoading = true
        val loaded = runCatching {
            withContext(Dispatchers.IO) { HomeImageMemoryCache.load(cleanUrl) }
        }.getOrNull()

        bitmap = loaded
        didFail = loaded == null
        isLoading = false
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = requireNotNull(bitmap).asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                )
            }

            isLoading -> CircularProgressIndicator(modifier = Modifier.size(28.dp))

            didFail -> Icon(
                imageVector = Icons.Rounded.ImageNotSupported,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            else -> Icon(
                imageVector = placeholderIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun FeaturedMediaViewer(
    media: List<FeaturedPostMedia>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
) {
    if (media.isEmpty()) return

    var currentIndex by remember(media, selectedIndex) {
        mutableIntStateOf(selectedIndex.coerceIn(0, media.lastIndex))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            ZoomableRemoteBitmapImage(
                url = media[currentIndex].downloadURL,
                modifier = Modifier.fillMaxSize(),
            )

            ViewerTopControls(
                currentIndex = currentIndex,
                total = media.size,
                onDismiss = onDismiss,
            )

            AnimatedVisibility(
                visible = currentIndex > 0,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                ViewerArrowButton(
                    icon = Icons.Rounded.NavigateBefore,
                    contentDescription = "Anterior",
                    onClick = { currentIndex = (currentIndex - 1).coerceAtLeast(0) },
                )
            }

            AnimatedVisibility(
                visible = currentIndex < media.lastIndex,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                ViewerArrowButton(
                    icon = Icons.Rounded.NavigateNext,
                    contentDescription = "Siguiente",
                    onClick = { currentIndex = (currentIndex + 1).coerceAtMost(media.lastIndex) },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ViewerTopControls(
    currentIndex: Int,
    total: Int,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.48f),
            shape = RoundedCornerShape(50),
        ) {
            Text(
                text = "${currentIndex + 1} / $total",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.48f)),
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cerrar",
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun ViewerArrowButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.42f))
            .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(34.dp),
        )
    }
}

@Composable
private fun ZoomableRemoteBitmapImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    var scale by remember(url) { mutableFloatStateOf(1f) }
    var offset by remember(url) { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 4f)
        scale = newScale
        offset = if (newScale <= 1.01f) Offset.Zero else offset + panChange
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .pointerInput(url) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1.01f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2f
                        }
                    },
                )
            }
            .transformable(transformableState),
        contentAlignment = Alignment.Center,
    ) {
        RemoteBitmapImage(
            url = url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
        )
    }
}

private object HomeImageMemoryCache {
    private val cache = object : LruCache<String, Bitmap>(80 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    fun load(url: String): Bitmap? {
        cache.get(url)?.let { return it }

        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 20_000
            readTimeout = 30_000
            instanceFollowRedirects = true
        }

        return try {
            connection.inputStream.use { input ->
                BitmapFactory.decodeStream(input)?.also { bitmap ->
                    cache.put(url, bitmap)
                }
            }
        } finally {
            connection.disconnect()
        }
    }
}
