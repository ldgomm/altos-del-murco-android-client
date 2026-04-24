package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

fun Modifier.appScreenStyle(
    theme: AppSectionTheme,
): Modifier = composed {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    this
        .background(palette.background)
        .background(palette.softGradient)
}

fun Modifier.appCardStyle(
    theme: AppSectionTheme,
    emphasized: Boolean = false,
): Modifier = composed {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.xLarge)

    this
        .shadow(
            elevation = AppTheme.Metrics.shadowRadius,
            shape = shape,
            ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.24f else 0.10f),
            spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.24f else 0.10f),
        )
        .clip(shape)
        .background(palette.cardGradient)
        .border(
            width = 1.dp,
            color = palette.stroke,
            shape = shape,
        )
        .drawWithContent {
            drawContent()

            if (emphasized) {
                drawRoundRect(
                    brush = palette.heroGradient,
                    topLeft = Offset(16.dp.toPx(), 16.dp.toPx()),
                    size = Size(62.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
        }
        .padding(AppTheme.Metrics.cardPadding)
}

fun Modifier.appTextFieldStyle(
    theme: AppSectionTheme,
): Modifier = composed {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    this
        .shadow(
            elevation = 8.dp,
            shape = shape,
            ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.12f else 0.04f),
            spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.12f else 0.04f),
        )
        .clip(shape)
        .background(palette.elevatedCard)
        .border(
            width = 1.dp,
            color = palette.stroke,
            shape = shape,
        )
        .padding(horizontal = 16.dp)
}

fun Modifier.appListRowStyle(
    theme: AppSectionTheme,
): Modifier = composed {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    this
        .shadow(
            elevation = 10.dp,
            shape = shape,
            ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.16f else 0.06f),
            spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.16f else 0.06f),
        )
        .clip(shape)
        .background(palette.cardGradient)
        .border(
            width = 1.dp,
            color = palette.stroke,
            shape = shape,
        )
        .padding(horizontal = 12.dp, vertical = 10.dp)
}