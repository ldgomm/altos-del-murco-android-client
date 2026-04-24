package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BrandScreen(
    theme: AppSectionTheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalBrandPalette.current.background),
    ) {
        BrandScreenBackground(
            theme = theme,
            modifier = Modifier.matchParentSize(),
        )

        content()
    }
}

@Composable
fun BrandScreenBackground(
    theme: AppSectionTheme,
    modifier: Modifier = Modifier,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .background(palette.softGradient),
    ) {
        when (theme) {
            AppSectionTheme.Neutral -> {
                GlowCircle(
                    theme = theme,
                    useSecondary = false,
                )
            }

            AppSectionTheme.Adventure -> {
                GlowCircle(
                    theme = theme,
                    useSecondary = false,
                )

                GlowCircle(
                    theme = theme,
                    useSecondary = true,
                )
            }

            AppSectionTheme.Restaurant -> {
                GlowCircle(
                    theme = theme,
                    useSecondary = false,
                )

                GlowCircle(
                    theme = theme,
                    useSecondary = true,
                )
            }
        }

        BrandWatermark(
            theme = theme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 20.dp)
                .alpha(if (darkTheme) 0.05f else 0.08f),
        )
    }
}

@Composable
private fun GlowCircle(
    alpha: Float = 0.0f,
    size: Int = 0,
    blur: Int = 0,
    x: Int = 0,
    y: Int = 0,
    theme: AppSectionTheme,
    useSecondary: Boolean,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val color = if (useSecondary) palette.secondary else palette.glow

    Box(
        modifier = Modifier
            .size(size.dp)
            .offset(x = x.dp, y = y.dp)
            .background(
                color = color.copy(alpha = alpha),
                shape = CircleShape,
            )
            .blur(blur.dp),
    )
}

@Composable
fun BrandWatermark(
    theme: AppSectionTheme,
    modifier: Modifier = Modifier,
) {
    val assetName = theme.watermarkAssetName ?: return
    val context = LocalContext.current

    val resourceId = remember(assetName) {
        val drawableId = context.resources.getIdentifier(
            assetName,
            "drawable",
            context.packageName,
        )

        if (drawableId != 0) {
            drawableId
        } else {
            context.resources.getIdentifier(
                assetName,
                "mipmap",
                context.packageName,
            )
        }
    }

    if (resourceId == 0) return

    Image(
        painter = painterResource(resourceId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(140.dp),
    )
}