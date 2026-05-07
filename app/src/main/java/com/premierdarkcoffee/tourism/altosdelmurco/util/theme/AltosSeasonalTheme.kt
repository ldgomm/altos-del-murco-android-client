package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TheaterComedy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

enum class AltosSeasonalTheme(
    val title: String,
    val motionStyle: SeasonalMotionStyle,
    val particleCount: Int,
) {
    NewYear("Año Nuevo", SeasonalMotionStyle.Burst, 18),
    Diablada("Diablada", SeasonalMotionStyle.Fall, 14),
    Carnival("Carnaval", SeasonalMotionStyle.Burst, 22),
    PawkarRaymi("Pawkar Raymi", SeasonalMotionStyle.Orbit, 18),
    HolyWeek("Semana Santa", SeasonalMotionStyle.Fall, 14),
    MothersDay("Día de Mamá", SeasonalMotionStyle.Fall, 16),
    Pichincha("24 de Mayo", SeasonalMotionStyle.Wind, 14),
    FathersDay("Día de Papá", SeasonalMotionStyle.Fall, 14),
    IntiRaymi("Inti Raymi", SeasonalMotionStyle.Orbit, 20),
    GuayaquilJuly("Fiestas Julianas", SeasonalMotionStyle.Wind, 16),
    AugustIndependence("10 de Agosto", SeasonalMotionStyle.Wind, 14),
    Yamor("Yamor", SeasonalMotionStyle.Orbit, 18),
    GuayaquilOctober("Guayaquil", SeasonalMotionStyle.Wind, 16),
    RodeoMontuvio("Rodeo Montuvio", SeasonalMotionStyle.Wind, 16),
    Halloween("Halloween", SeasonalMotionStyle.Fall, 18),
    Difuntos("Difuntos", SeasonalMotionStyle.Fall, 14),
    MamaNegra("Mama Negra", SeasonalMotionStyle.Burst, 18),
    Quito("Fiestas de Quito", SeasonalMotionStyle.Wind, 16),
    Christmas("Navidad", SeasonalMotionStyle.Fall, 22),
    NewYearsEve("Año Viejo", SeasonalMotionStyle.Burst, 22),
}

enum class SeasonalMotionStyle {
    Fall,
    Wind,
    Orbit,
    Burst,
}

object EcuadorSeasonalThemeResolver {
    val zoneId: ZoneId = ZoneId.of("America/Guayaquil")

    fun resolve(date: LocalDate = LocalDate.now(zoneId)): AltosSeasonalTheme? {
        val year = date.year

        fun range(startMonth: Month, startDay: Int, endMonth: Month, endDay: Int): Boolean {
            val start = LocalDate.of(year, startMonth, startDay)
            val end = LocalDate.of(year, endMonth, endDay)
            return !date.isBefore(start) && !date.isAfter(end)
        }

        fun around(center: LocalDate, before: Long, after: Long): Boolean {
            val start = center.minusDays(before)
            val end = center.plusDays(after)
            return !date.isBefore(start) && !date.isAfter(end)
        }

        val easter = easterSunday(year)

        // Highest-priority campaigns first. This avoids Navidad fighting Año Viejo, etc.
        if (range(Month.DECEMBER, 27, Month.DECEMBER, 31)) return AltosSeasonalTheme.NewYearsEve
        if (range(Month.DECEMBER, 7, Month.DECEMBER, 25)) return AltosSeasonalTheme.Christmas
        if (range(Month.DECEMBER, 1, Month.DECEMBER, 6)) return AltosSeasonalTheme.Quito
        if (range(Month.NOVEMBER, 4, Month.NOVEMBER, 12)) return AltosSeasonalTheme.MamaNegra
        if (range(Month.NOVEMBER, 1, Month.NOVEMBER, 3)) return AltosSeasonalTheme.Difuntos
        if (range(Month.OCTOBER, 27, Month.OCTOBER, 31)) return AltosSeasonalTheme.Halloween
        if (range(Month.OCTOBER, 10, Month.OCTOBER, 13)) return AltosSeasonalTheme.RodeoMontuvio
        if (range(Month.OCTOBER, 6, Month.OCTOBER, 9)) return AltosSeasonalTheme.GuayaquilOctober
        if (range(Month.SEPTEMBER, 1, Month.SEPTEMBER, 15)) return AltosSeasonalTheme.Yamor
        if (range(Month.AUGUST, 8, Month.AUGUST, 10)) return AltosSeasonalTheme.AugustIndependence
        if (range(Month.JULY, 20, Month.JULY, 25)) return AltosSeasonalTheme.GuayaquilJuly
        if (range(Month.JUNE, 18, Month.JUNE, 26)) return AltosSeasonalTheme.IntiRaymi

        val fathersDay = nthWeekday(year, Month.JUNE, DayOfWeek.SUNDAY, 3)
        if (around(fathersDay, before = 2, after = 1)) return AltosSeasonalTheme.FathersDay

        if (range(Month.MAY, 22, Month.MAY, 24)) return AltosSeasonalTheme.Pichincha

        val mothersDay = nthWeekday(year, Month.MAY, DayOfWeek.SUNDAY, 2)
        if (around(mothersDay, before = 8, after = 1)) return AltosSeasonalTheme.MothersDay

        if (around(easter, before = 7, after = 0)) return AltosSeasonalTheme.HolyWeek
        if (!date.isBefore(easter.minusDays(50)) && !date.isAfter(easter.minusDays(46))) return AltosSeasonalTheme.Carnival
        if (range(Month.MARCH, 18, Month.MARCH, 23)) return AltosSeasonalTheme.PawkarRaymi
        if (date.month == Month.JANUARY && date.dayOfMonth in 1..2) return AltosSeasonalTheme.NewYear
        if (date.month == Month.JANUARY && date.dayOfMonth in 3..6) return AltosSeasonalTheme.Diablada

        return null
    }

    private fun nthWeekday(year: Int, month: Month, weekday: DayOfWeek, nth: Int): LocalDate =
        LocalDate.of(year, month, 1).with(TemporalAdjusters.dayOfWeekInMonth(nth, weekday))

    private fun easterSunday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }

    fun millisUntilNextEcuadorMidnight(): Long {
        val now = ZonedDateTime.now(zoneId)
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(zoneId).plusSeconds(1)
        return Duration.between(now, nextMidnight).toMillis().coerceAtLeast(60_000L)
    }
}

@Composable
fun rememberCurrentAltosSeasonalTheme(): AltosSeasonalTheme? {
    var today by remember {
        mutableStateOf(LocalDate.now(EcuadorSeasonalThemeResolver.zoneId))
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(EcuadorSeasonalThemeResolver.millisUntilNextEcuadorMidnight())
            today = LocalDate.now(EcuadorSeasonalThemeResolver.zoneId)
        }
    }

    return remember(today) { EcuadorSeasonalThemeResolver.resolve(today) }
}

@Composable
fun SeasonalCardContainer(
    sectionTheme: AppSectionTheme,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    seasonalTheme: AltosSeasonalTheme? = rememberCurrentAltosSeasonalTheme(),
    onClick: (() -> Unit)? = null,
    minHeightDp: Int = 0,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(sectionTheme, darkTheme)
    val shape = RoundedCornerShape(AppTheme.Radius.xLarge)

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (emphasized) 18.dp else 10.dp,
                shape = shape,
                clip = false,
            )
            .clip(shape)
            .background(palette.cardGradient)
            .then(clickableModifier)
            .border(
                width = 1.dp,
                color = if (seasonalTheme == null) palette.stroke else palette.stroke.copy(alpha = 0.72f),
                shape = shape,
            )
            .defaultMinSize(minHeight = minHeightDp.dp),
    ) {
        SeasonalAnimatedCardBackground(
            seasonalTheme = seasonalTheme,
            darkTheme = darkTheme,
            modifier = Modifier.matchParentSize(),
            intensity = if (sectionTheme == AppSectionTheme.Neutral) 0.72f else 1f,
        )

        Column(
            modifier = Modifier.padding(AppTheme.Metrics.cardPadding),
            verticalArrangement = verticalArrangement,
            content = content,
        )

        if (emphasized) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .widthIn(min = 62.dp)
                    .size(width = 62.dp, height = 6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.heroGradient),
            )
        }

//        seasonalTheme?.let { theme ->
//            SeasonalTinyBadge(
//                theme = theme,
//                sectionTheme = sectionTheme,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(12.dp),
//            )
//        }
    }
}

@Composable
fun SeasonalAnimatedCardBackground(
    seasonalTheme: AltosSeasonalTheme?,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    intensity: Float = 1f,
) {
    if (seasonalTheme == null) return

    val transition = rememberInfiniteTransition(label = "altos-seasonal-card")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (seasonalTheme.motionStyle) {
                    SeasonalMotionStyle.Burst -> 7_500
                    SeasonalMotionStyle.Wind -> 10_500
                    SeasonalMotionStyle.Orbit -> 12_000
                    SeasonalMotionStyle.Fall -> 13_000
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "altos-seasonal-card-progress",
    )

    val colors = seasonalColors(seasonalTheme, darkTheme)

    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val heightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colors[0].copy(alpha = 0.14f * intensity),
                            colors[1 % colors.size].copy(alpha = 0.10f * intensity),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Icon(
            imageVector = seasonalIcon(seasonalTheme),
            contentDescription = null,
            tint = colors.first().copy(alpha = if (darkTheme) 0.10f else 0.075f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 4.dp)
                .size((if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.72f)
                .graphicsLayer(rotationZ = -10f),
        )

        repeat(seasonalTheme.particleCount) { index ->
            val particle = remember(seasonalTheme, index) {
                ComposeSeasonalParticle(index, seasonalTheme.motionStyle)
            }
            val localProgress = ((progress + particle.offset) % 1f).coerceIn(0f, 1f)
            val position = particle.position(
                progress = localProgress,
                width = widthPx,
                height = heightPx,
            )
            val scale = particle.scale(localProgress)
            val icon = seasonalParticleIcon(seasonalTheme, index)
            val tint = colors[index % colors.size]

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint.copy(alpha = particle.opacity),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = position.x.roundToInt(),
                            y = position.y.roundToInt(),
                        )
                    }
                    .size(particle.size.dp)
                    .graphicsLayer {
                        rotationZ = particle.rotation(localProgress)
                        scaleX = scale
                        scaleY = scale
                    },
            )
        }
    }
}

@Composable
fun SeasonalTinyBadge(
    theme: AltosSeasonalTheme,
    sectionTheme: AppSectionTheme,
    modifier: Modifier = Modifier,
) {
    val palette = AppTheme.palette(sectionTheme, LocalBrandDarkTheme.current)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .background(palette.heroGradient)
                .padding(horizontal = 9.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = seasonalIcon(theme),
                contentDescription = null,
                tint = palette.onPrimary,
                modifier = Modifier.size(13.dp),
            )
            Text(
                text = theme.title,
                color = palette.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private data class ComposeSeasonalParticle(
    val index: Int,
    val style: SeasonalMotionStyle,
) {
    val baseX: Float = random(index * 31 + 1, 0.04f, 0.96f)
    val baseY: Float = random(index * 37 + 3, 0.08f, 0.92f)
    val offset: Float = random(index * 41 + 5, 0f, 1f)
    val drift: Float = random(index * 47 + 11, 16f, 88f)
    val phase: Float = random(index * 53 + 13, 0f, (PI * 2).toFloat())
    val size: Float =
        random(index * 59 + 17, 10f, if (style == SeasonalMotionStyle.Burst) 30f else 24f)
    val opacity: Float = random(index * 61 + 19, 0.20f, 0.54f)

    fun position(progress: Float, width: Float, height: Float): ParticlePosition {
        return when (style) {
            SeasonalMotionStyle.Fall -> {
                ParticlePosition(
                    x = width * baseX + sin(progress * PI.toFloat() * 2f + phase) * drift,
                    y = -42f + (height + 84f) * progress,
                )
            }

            SeasonalMotionStyle.Wind -> {
                ParticlePosition(
                    x = -46f + (width + 92f) * progress,
                    y = height * baseY + sin(progress * PI.toFloat() * 2f + phase) * drift * 0.42f,
                )
            }

            SeasonalMotionStyle.Orbit -> {
                val centerX = width * baseX
                val centerY = height * baseY
                val radius = drift * 0.56f
                val angle = progress * PI.toFloat() * 2f + phase
                ParticlePosition(
                    x = centerX + cos(angle) * radius,
                    y = centerY + sin(angle) * radius,
                )
            }

            SeasonalMotionStyle.Burst -> {
                val originX = width * 0.50f
                val originY = height * 0.45f
                val angle = baseX * PI.toFloat() * 2f
                val distance = (0.16f + progress) * min(width, height) * 0.92f
                ParticlePosition(
                    x = originX + cos(angle + phase * 0.15f) * distance,
                    y = originY + sin(angle + phase * 0.15f) * distance,
                )
            }
        }
    }

    fun rotation(progress: Float): Float = when (style) {
        SeasonalMotionStyle.Orbit -> progress * 120f + phase * 12f
        SeasonalMotionStyle.Burst -> progress * 280f
        SeasonalMotionStyle.Wind -> sin(progress * PI.toFloat() * 2f + phase) * 24f
        SeasonalMotionStyle.Fall -> progress * 160f + phase * 8f
    }

    fun scale(progress: Float): Float = when (style) {
        SeasonalMotionStyle.Burst -> 0.72f + sin(progress * PI.toFloat()) * 0.42f
        else -> 0.86f + sin(progress * PI.toFloat() * 2f + phase) * 0.14f
    }

    companion object {
        fun random(seed: Int, min: Float, max: Float): Float {
            val raw = sin(seed.toFloat() * 12.9898f) * 43758.5453f
            val normalized = raw - floor(raw)
            return min + normalized * (max - min)
        }
    }
}

private data class ParticlePosition(
    val x: Float,
    val y: Float,
)

private fun seasonalColors(theme: AltosSeasonalTheme, darkTheme: Boolean): List<Color> {
    fun color(hex: Long): Color = Color(0xFF000000L or hex)
    val lightAlphaBoost = if (darkTheme) 1f else 0.86f

    return when (theme) {
        AltosSeasonalTheme.NewYear -> listOf(
            color(0xF7D774),
            Color.White,
            color(0x74C0FC),
            color(0xB197FC)
        )

        AltosSeasonalTheme.Diablada -> listOf(
            color(0xE03131),
            color(0xF59F00),
            color(0x212529),
            Color.White
        )

        AltosSeasonalTheme.Carnival -> listOf(
            color(0xF783AC),
            color(0x4DABF7),
            color(0x69DB7C),
            color(0xFFD43B),
            color(0xB197FC)
        )

        AltosSeasonalTheme.PawkarRaymi -> listOf(
            color(0x69DB7C),
            color(0xFFD43B),
            color(0xFF922B),
            color(0x38D9A9)
        )

        AltosSeasonalTheme.HolyWeek -> listOf(
            color(0x9775FA),
            color(0xFFD43B),
            color(0x8CE99A),
            Color.White
        )

        AltosSeasonalTheme.MothersDay -> listOf(
            color(0xF06595),
            color(0xFCC2D7),
            color(0xFF8787),
            color(0xB197FC)
        )

        AltosSeasonalTheme.Pichincha -> listOf(
            color(0xFFD43B),
            color(0x4DABF7),
            color(0xFF6B6B),
            Color.White
        )

        AltosSeasonalTheme.FathersDay -> listOf(
            color(0x4DABF7),
            color(0x74C0FC),
            color(0xFFD43B),
            color(0xADB5BD)
        )

        AltosSeasonalTheme.IntiRaymi -> listOf(
            color(0xFFD43B),
            color(0xFF922B),
            color(0xF76707),
            color(0x69DB7C)
        )

        AltosSeasonalTheme.GuayaquilJuly, AltosSeasonalTheme.GuayaquilOctober -> listOf(
            color(
                0x4DABF7
            ), Color.White, color(0xFFD43B), color(0x228BE6)
        )

        AltosSeasonalTheme.AugustIndependence -> listOf(
            color(0xFFD43B),
            color(0x228BE6),
            color(0xFA5252),
            Color.White
        )

        AltosSeasonalTheme.Yamor -> listOf(
            color(0xFFD43B),
            color(0x82C91E),
            color(0xFF922B),
            color(0x7950F2)
        )

        AltosSeasonalTheme.RodeoMontuvio -> listOf(
            color(0xD9480F),
            color(0xF59F00),
            color(0xA16207),
            Color.White
        )

        AltosSeasonalTheme.Halloween -> listOf(
            color(0xF76707),
            color(0x845EF7),
            color(0x212529),
            color(0xFFD43B)
        )

        AltosSeasonalTheme.Difuntos -> listOf(
            color(0x862E9C),
            color(0xF783AC),
            color(0xFF922B),
            color(0x8CE99A)
        )

        AltosSeasonalTheme.MamaNegra -> listOf(
            color(0xE03131),
            color(0xF59F00),
            color(0x7950F2),
            color(0x212529)
        )

        AltosSeasonalTheme.Quito -> listOf(
            color(0xC92A2A),
            color(0x228BE6),
            color(0xFFD43B),
            Color.White
        )

        AltosSeasonalTheme.Christmas -> listOf(
            color(0xE03131),
            color(0x2F9E44),
            Color.White,
            color(0x74C0FC),
            color(0xFFD43B)
        )

        AltosSeasonalTheme.NewYearsEve -> listOf(
            color(0xFFD43B),
            color(0xFF922B),
            color(0xE03131),
            Color.White
        )
    }.map { it.copy(alpha = it.alpha * lightAlphaBoost) }
}

private fun seasonalIcon(theme: AltosSeasonalTheme): ImageVector = when (theme) {
    AltosSeasonalTheme.NewYear -> Icons.Rounded.AutoAwesome
    AltosSeasonalTheme.Diablada -> Icons.Rounded.TheaterComedy
    AltosSeasonalTheme.Carnival -> Icons.Rounded.Celebration
    AltosSeasonalTheme.PawkarRaymi -> Icons.Rounded.LocalFlorist
    AltosSeasonalTheme.HolyWeek -> Icons.Rounded.LocalFireDepartment
    AltosSeasonalTheme.MothersDay -> Icons.Rounded.Favorite
    AltosSeasonalTheme.Pichincha -> Icons.Rounded.Flag
    AltosSeasonalTheme.FathersDay -> Icons.Rounded.Person
    AltosSeasonalTheme.IntiRaymi -> Icons.Rounded.WbSunny
    AltosSeasonalTheme.GuayaquilJuly -> Icons.Rounded.DirectionsBoat
    AltosSeasonalTheme.AugustIndependence -> Icons.Rounded.Flag
    AltosSeasonalTheme.Yamor -> Icons.Rounded.Park
    AltosSeasonalTheme.GuayaquilOctober -> Icons.Rounded.Flag
    AltosSeasonalTheme.RodeoMontuvio -> Icons.Rounded.EmojiEvents
    AltosSeasonalTheme.Halloween -> Icons.Rounded.DarkMode
    AltosSeasonalTheme.Difuntos -> Icons.Rounded.Coffee
    AltosSeasonalTheme.MamaNegra -> Icons.Rounded.TheaterComedy
    AltosSeasonalTheme.Quito -> Icons.Rounded.Flag
    AltosSeasonalTheme.Christmas -> Icons.Rounded.CardGiftcard
    AltosSeasonalTheme.NewYearsEve -> Icons.Rounded.Celebration
}

private fun seasonalParticleIcon(theme: AltosSeasonalTheme, index: Int): ImageVector {
    val icons = when (theme) {
        AltosSeasonalTheme.NewYear -> listOf(
            Icons.Rounded.AutoAwesome,
            Icons.Rounded.Star,
            Icons.Rounded.Celebration
        )

        AltosSeasonalTheme.Diablada -> listOf(
            Icons.Rounded.TheaterComedy,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.Carnival -> listOf(
            Icons.Rounded.Celebration,
            Icons.Rounded.AutoAwesome,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.PawkarRaymi -> listOf(
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.WbSunny,
            Icons.Rounded.Park
        )

        AltosSeasonalTheme.HolyWeek -> listOf(
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.AutoAwesome
        )

        AltosSeasonalTheme.MothersDay -> listOf(
            Icons.Rounded.Favorite,
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.LocalFlorist
        )

        AltosSeasonalTheme.Pichincha -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome
        )

        AltosSeasonalTheme.FathersDay -> listOf(
            Icons.Rounded.Person,
            Icons.Rounded.Favorite,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.IntiRaymi -> listOf(
            Icons.Rounded.WbSunny,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.LocalFlorist
        )

        AltosSeasonalTheme.GuayaquilJuly -> listOf(
            Icons.Rounded.DirectionsBoat,
            Icons.Rounded.Flag,
            Icons.Rounded.WbSunny
        )

        AltosSeasonalTheme.AugustIndependence -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome
        )

        AltosSeasonalTheme.Yamor -> listOf(
            Icons.Rounded.Park,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.Restaurant
        )

        AltosSeasonalTheme.GuayaquilOctober -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome
        )

        AltosSeasonalTheme.RodeoMontuvio -> listOf(
            Icons.Rounded.EmojiEvents,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.Halloween -> listOf(
            Icons.Rounded.DarkMode,
            Icons.Rounded.Nightlight,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.Difuntos -> listOf(
            Icons.Rounded.Coffee,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.LocalFireDepartment
        )

        AltosSeasonalTheme.MamaNegra -> listOf(
            Icons.Rounded.TheaterComedy,
            Icons.Rounded.Celebration,
            Icons.Rounded.LocalFireDepartment
        )

        AltosSeasonalTheme.Quito -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.Celebration
        )

        AltosSeasonalTheme.Christmas -> listOf(
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.AcUnit,
            Icons.Rounded.Star
        )

        AltosSeasonalTheme.NewYearsEve -> listOf(
            Icons.Rounded.Celebration,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.AutoAwesome
        )
    }
    return icons[index % icons.size]
}
