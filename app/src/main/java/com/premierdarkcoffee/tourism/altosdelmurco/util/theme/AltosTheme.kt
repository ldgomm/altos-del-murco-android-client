package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class AppSectionTheme {
    Neutral,
    Adventure,
    Restaurant;

    val watermarkAssetName: String?
        get() = when (this) {
            Neutral -> null
            Adventure -> "theme_adventure_mark"
            Restaurant -> "theme_restaurant_mark"
        }
}

data class BrandPalette(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val onPrimary: Color,

    val background: Color,
    val surface: Color,
    val card: Color,
    val elevatedCard: Color,
    val stroke: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    val success: Color,
    val warning: Color,
    val destructive: Color,

    val shadow: Color,
    val glow: Color,

    val heroGradient: Brush,
    val softGradient: Brush,
    val cardGradient: Brush,
    val chipGradient: Brush,
)

val LocalAppSectionTheme = staticCompositionLocalOf { AppSectionTheme.Neutral }
val LocalBrandPalette = staticCompositionLocalOf {
    AppTheme.palette(
        theme = AppSectionTheme.Neutral,
        darkTheme = false,
    )
}
val LocalBrandDarkTheme = staticCompositionLocalOf { false }

object AppTheme {

    object Radius {
        val small = 14.dp
        val medium = 18.dp
        val large = 22.dp
        val xLarge = 28.dp
    }

    object Metrics {
        val fieldHeight = 54.dp
        val buttonHeight = 54.dp
        val cardPadding = 16.dp
        val sectionSpacing = 20.dp
        val shadowRadius = 18.dp
        val shadowY = 10.dp
    }

    fun palette(
        theme: AppSectionTheme,
        darkTheme: Boolean,
    ): BrandPalette {
        return when (theme) {
            AppSectionTheme.Neutral -> neutralPalette(darkTheme)
            AppSectionTheme.Adventure -> adventurePalette(darkTheme)
            AppSectionTheme.Restaurant -> restaurantPalette(darkTheme)
        }
    }

    private fun neutralPalette(dark: Boolean): BrandPalette {
        val primary = adaptive(dark, 0x2F3E4F, 0xB5C2D0)
        val secondary = adaptive(dark, 0x5F738A, 0x8FA7BF)
        val accent = adaptive(dark, 0x6F8FB0, 0x9DB6D4)
        val background = adaptive(dark, 0xF4F7FA, 0x0C1014)
        val surface = adaptive(dark, 0xFFFFFF, 0x12171D)
        val card = adaptive(dark, 0xFBFCFD, 0x151B22)
        val elevatedCard = adaptive(dark, 0xFFFFFF, 0x19212A)
        val stroke = adaptive(dark, 0xDDE5EC, 0x2A3542)
        val textPrimary = adaptive(dark, 0x15202B, 0xF1F5F9)
        val textSecondary = adaptive(dark, 0x5C6B7A, 0xA7B4C2)
        val textTertiary = adaptive(dark, 0x8A97A5, 0x728191)
        val success = adaptive(dark, 0x2F855A, 0x68D391)
        val warning = adaptive(dark, 0xB7791F, 0xF6AD55)
        val destructive = adaptive(dark, 0xC53030, 0xFC8181)
        val glow = adaptive(dark, 0x9DB6D4, 0x5D7996)

        return BrandPalette(
            primary = primary,
            secondary = secondary,
            accent = accent,
            onPrimary = Color.White,
            background = background,
            surface = surface,
            card = card,
            elevatedCard = elevatedCard,
            stroke = stroke,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            success = success,
            warning = warning,
            destructive = destructive,
            shadow = Color.Black,
            glow = glow,
            heroGradient = Brush.linearGradient(listOf(primary, accent)),
            softGradient = Brush.linearGradient(
                listOf(
                    background,
                    accent.copy(alpha = if (dark) 0.10f else 0.08f),
                )
            ),
            cardGradient = Brush.linearGradient(listOf(elevatedCard, card)),
            chipGradient = Brush.linearGradient(
                listOf(
                    primary.copy(alpha = if (dark) 0.24f else 0.14f),
                    accent.copy(alpha = if (dark) 0.16f else 0.08f),
                )
            ),
        )
    }

    private fun adventurePalette(dark: Boolean): BrandPalette {
        val primary = adaptive(dark, 0x2F6B3C, 0x7BCB69)
        val secondary = adaptive(dark, 0x4D8A47, 0x9BE07C)
        val accent = adaptive(dark, 0xA6C95A, 0xD5F08D)
        val background = adaptive(dark, 0xF2F7F0, 0x0B140D)
        val surface = adaptive(dark, 0xFFFFFF, 0x111B13)
        val card = adaptive(dark, 0xF8FCF6, 0x152017)
        val elevatedCard = adaptive(dark, 0xFFFFFF, 0x19261B)
        val stroke = adaptive(dark, 0xD8E7D4, 0x2A3C2D)
        val textPrimary = adaptive(dark, 0x142117, 0xEEF8EE)
        val textSecondary = adaptive(dark, 0x5D7260, 0xA8BDAA)
        val textTertiary = adaptive(dark, 0x839485, 0x708172)
        val success = adaptive(dark, 0x2F855A, 0x68D391)
        val warning = adaptive(dark, 0xB7791F, 0xF6C15A)
        val destructive = adaptive(dark, 0xC53030, 0xFC8181)
        val glow = adaptive(dark, 0x9FD96A, 0x59B84B)

        return BrandPalette(
            primary = primary,
            secondary = secondary,
            accent = accent,
            onPrimary = Color.White,
            background = background,
            surface = surface,
            card = card,
            elevatedCard = elevatedCard,
            stroke = stroke,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            success = success,
            warning = warning,
            destructive = destructive,
            shadow = Color.Black,
            glow = glow,
            heroGradient = Brush.linearGradient(listOf(primary, secondary, accent)),
            softGradient = Brush.linearGradient(
                listOf(
                    background,
                    primary.copy(alpha = if (dark) 0.18f else 0.07f),
                    accent.copy(alpha = if (dark) 0.10f else 0.05f),
                )
            ),
            cardGradient = Brush.linearGradient(
                listOf(
                    elevatedCard,
                    card,
                    accent.copy(alpha = if (dark) 0.04f else 0.03f),
                )
            ),
            chipGradient = Brush.linearGradient(
                listOf(
                    primary.copy(alpha = if (dark) 0.30f else 0.14f),
                    accent.copy(alpha = if (dark) 0.18f else 0.10f),
                )
            ),
        )
    }

    private fun restaurantPalette(dark: Boolean): BrandPalette {
        val primary = adaptive(dark, 0x3E4347, 0xC2C8CE)
        val secondary = adaptive(dark, 0x5A6066, 0x9BA3AB)
        val accent = adaptive(dark, 0x8B7D67, 0xC5B79E)
        val background = adaptive(dark, 0xF3F2F0, 0x0D0F11)
        val surface = adaptive(dark, 0xFCFBFA, 0x14171A)
        val card = adaptive(dark, 0xF7F5F3, 0x1A1E22)
        val elevatedCard = adaptive(dark, 0xFFFFFF, 0x20252A)
        val stroke = adaptive(dark, 0xD8D4CE, 0x333940)
        val textPrimary = adaptive(dark, 0x1C1F22, 0xF3F5F7)
        val textSecondary = adaptive(dark, 0x666D74, 0xB1B8BF)
        val textTertiary = adaptive(dark, 0x8A9096, 0x7A838C)
        val success = adaptive(dark, 0x2F855A, 0x68D391)
        val warning = adaptive(dark, 0x9C7B3D, 0xD6B56E)
        val destructive = adaptive(dark, 0xC94C4C, 0xFC8181)
        val glow = adaptive(dark, 0xA79A84, 0x7B7468)

        return BrandPalette(
            primary = primary,
            secondary = secondary,
            accent = accent,
            onPrimary = Color.White,
            background = background,
            surface = surface,
            card = card,
            elevatedCard = elevatedCard,
            stroke = stroke,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            success = success,
            warning = warning,
            destructive = destructive,
            shadow = Color.Black,
            glow = glow,
            heroGradient = Brush.linearGradient(listOf(primary, secondary, accent)),
            softGradient = Brush.linearGradient(
                listOf(
                    background,
                    primary.copy(alpha = if (dark) 0.16f else 0.05f),
                    accent.copy(alpha = if (dark) 0.10f else 0.04f),
                )
            ),
            cardGradient = Brush.linearGradient(
                listOf(
                    elevatedCard,
                    card,
                    accent.copy(alpha = if (dark) 0.035f else 0.02f),
                )
            ),
            chipGradient = Brush.linearGradient(
                listOf(
                    primary.copy(alpha = if (dark) 0.28f else 0.12f),
                    accent.copy(alpha = if (dark) 0.14f else 0.08f),
                )
            ),
        )
    }

    private fun adaptive(
        dark: Boolean,
        lightRgb: Long,
        darkRgb: Long,
    ): Color {
        return rgb(if (dark) darkRgb else lightRgb)
    }

    private fun rgb(rgb: Long): Color {
        return Color(0xFF000000 or rgb)
    }
}

//@Composable
//fun AltosTheme(
//    themeMode: ThemeMode = ThemeMode.SYSTEM,
//    sectionTheme: AppSectionTheme = AppSectionTheme.Neutral,
//    content: @Composable () -> Unit,
//) {
//    val darkTheme = when (themeMode) {
//        ThemeMode.SYSTEM -> isSystemInDarkTheme()
//        ThemeMode.LIGHT -> false
//        ThemeMode.DARK -> true
//    }
//
//    val palette = AppTheme.palette(
//        theme = sectionTheme,
//        darkTheme = darkTheme,
//    )
//
//    val colorScheme = palette.toMaterialColorScheme(darkTheme)
//
//    ConfigureSystemBars(
//        darkTheme = darkTheme,
//        palette = palette,
//    )
//
//    CompositionLocalProvider(
//        LocalAppSectionTheme provides sectionTheme,
//        LocalBrandPalette provides palette,
//        LocalBrandDarkTheme provides darkTheme,
//    ) {
//        MaterialTheme(
//            colorScheme = colorScheme,
//            typography = MurcoTypography,
//            content = content,
//        )
//    }
//}

fun BrandPalette.toMaterialColorScheme(
    darkTheme: Boolean,
): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primary.copy(alpha = 0.24f),
            onPrimaryContainer = textPrimary,

            secondary = secondary,
            onSecondary = Color.Black,
            secondaryContainer = secondary.copy(alpha = 0.20f),
            onSecondaryContainer = textPrimary,

            tertiary = accent,
            onTertiary = Color.Black,
            tertiaryContainer = accent.copy(alpha = 0.18f),
            onTertiaryContainer = textPrimary,

            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = card,
            onSurfaceVariant = textSecondary,

            outline = stroke,
            outlineVariant = stroke.copy(alpha = 0.55f),

            error = destructive,
            onError = Color.White,
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primary.copy(alpha = 0.14f),
            onPrimaryContainer = textPrimary,

            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.12f),
            onSecondaryContainer = textPrimary,

            tertiary = accent,
            onTertiary = Color.White,
            tertiaryContainer = accent.copy(alpha = 0.12f),
            onTertiaryContainer = textPrimary,

            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = card,
            onSurfaceVariant = textSecondary,

            outline = stroke,
            outlineVariant = stroke.copy(alpha = 0.65f),

            error = destructive,
            onError = Color.White,
        )
    }
}

@Composable
fun ConfigureSystemBars(
    darkTheme: Boolean,
    palette: BrandPalette,
) {
    val view = LocalView.current

    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = palette.background.toArgbCompat()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = !darkTheme
        controller.isAppearanceLightNavigationBars = !darkTheme
    }
}

private fun Color.toArgbCompat(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
    )
}
