package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BrandSectionHeader(
    theme: AppSectionTheme,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(palette.heroGradient),
            )

            Text(
                text = title,
                color = palette.textPrimary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun BrandIconBubble(
    theme: AppSectionTheme,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String? = null,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(palette.chipGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = palette.primary,
            modifier = Modifier.size(size * 0.38f),
        )
    }
}

@Composable
fun BrandIconBubble(
    theme: AppSectionTheme,
    systemImage: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String? = null,
) {
    BrandIconBubble(
        theme = theme,
        icon = systemImage.toMaterialIcon(),
        modifier = modifier,
        size = size,
        contentDescription = contentDescription,
    )
}

@Composable
fun BrandBadge(
    theme: AppSectionTheme,
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)

    Text(
        text = title,
        color = if (selected) palette.onPrimary else palette.primary,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = if (selected) palette.heroGradient else palette.chipGradient,
            )
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else palette.stroke,
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
fun BrandPrimaryButton(
    theme: AppSectionTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "BrandPrimaryButtonScale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.Metrics.buttonHeight)
            .scale(scale)
            .shadow(
                elevation = if (pressed) 10.dp else AppTheme.Metrics.shadowRadius,
                shape = shape,
                ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.32f else 0.14f),
                spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.32f else 0.14f),
            )
            .clip(shape)
            .background(
                brush = if (enabled) palette.heroGradient else palette.cardGradient,
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (darkTheme) 0.06f else 0.18f),
                shape = shape,
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

@Composable
fun BrandSecondaryButton(
    theme: AppSectionTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(theme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.large)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.988f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "BrandSecondaryButtonScale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.Metrics.buttonHeight)
            .scale(scale)
            .shadow(
                elevation = if (pressed) 8.dp else 14.dp,
                shape = shape,
                ambientColor = palette.shadow.copy(alpha = if (darkTheme) 0.18f else 0.08f),
                spotColor = palette.shadow.copy(alpha = if (darkTheme) 0.18f else 0.08f),
            )
            .clip(shape)
            .background(palette.cardGradient)
            .border(
                width = 1.dp,
                color = palette.stroke,
                shape = shape,
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}

fun String.toMaterialIcon(): ImageVector {
    return when (trim().lowercase()) {
        "figure.hiking", "hiking" -> Icons.Filled.Hiking
        "mountain.2", "mountain", "terrain" -> Icons.Filled.Terrain
        "fork.knife", "restaurant", "menucard" -> Icons.Filled.Restaurant
        "cart", "cart.fill", "bag", "basket" -> Icons.Filled.ShoppingCart
        "person", "person.fill" -> Icons.Filled.Person
        "calendar", "calendar.badge.clock" -> Icons.Filled.CalendarMonth
        "photo", "photo.fill", "camera" -> Icons.Filled.Photo
        "gift", "gift.fill", "coupon", "tag" -> Icons.Filled.Redeem
        "ticket", "ticket.fill" -> Icons.Filled.ConfirmationNumber
        "car", "car.fill" -> Icons.Filled.DirectionsCar
        "atv", "quad", "offroad", "motorcycle" -> Icons.Filled.SportsMotorsports
        "flame", "flame.fill" -> Icons.Filled.LocalFireDepartment
        "eye", "eye.fill" -> Icons.Filled.Visibility
        "safari", "compass", "location" -> Icons.Filled.Explore
        else -> Icons.Filled.Star
    }
}