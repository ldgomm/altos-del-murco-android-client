package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = MurcoGreen,
    secondary = MurcoGreenDark,
    background = MurcoStone,
    surface = MurcoCard,
    onPrimary = Color.White,
    onBackground = MurcoCharcoal,
    onSurface = MurcoCharcoal,
)

private val DarkColors = darkColorScheme(
    primary = MurcoGreen,
    secondary = MurcoGreenDark,
    background = MurcoCharcoal,
    surface = MurcoCardDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

//@Composable
//fun AltosTheme(
//    themeMode: ThemeMode,
//    content: @Composable () -> Unit,
//) {
//    val darkTheme = when (themeMode) {
//        ThemeMode.SYSTEM -> isSystemInDarkTheme()
//        ThemeMode.LIGHT -> false
//        ThemeMode.DARK -> true
//    }
//
//    MaterialTheme(
//        colorScheme = if (darkTheme) DarkColors else LightColors,
//        typography = MurcoTypography,
//        content = content,
//    )
//}

@Composable
fun AltosTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    sectionTheme: AppSectionTheme = AppSectionTheme.Neutral,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val palette = AppTheme.palette(
        theme = sectionTheme,
        darkTheme = darkTheme,
    )

    val colorScheme = palette.toMaterialColorScheme(darkTheme)

    ConfigureSystemBars(
        darkTheme = darkTheme,
        palette = palette,
    )

    CompositionLocalProvider(
        LocalAppSectionTheme provides sectionTheme,
        LocalBrandPalette provides palette,
        LocalBrandDarkTheme provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MurcoTypography,
            content = content,
        )
    }
}