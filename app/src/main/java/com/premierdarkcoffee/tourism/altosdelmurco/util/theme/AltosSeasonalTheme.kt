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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalDensity
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

/**
 * Ecuador seasonal layer for Altos del Murco.
 *
 * Performance rules:
 * - Never animate more than 10 particles per card.
 * - Smaller cards automatically receive fewer particles.
 * - Particle anchors are grid-distributed, not fully random, so the UI feels calmer.
 */
enum class AltosSeasonalTheme(
    val title: String,
    val motionStyle: SeasonalMotionStyle,
    val particleCount: Int,
) {
    NewYear("Año Nuevo", SeasonalMotionStyle.Burst, 8),
    Diablada("Diablada de Píllaro", SeasonalMotionStyle.Fall, 7),
    Reyes("Día de Reyes", SeasonalMotionStyle.Orbit, 6),
    Carnival("Carnaval", SeasonalMotionStyle.Burst, 10),
    ValentinesDay("San Valentín", SeasonalMotionStyle.Bloom, 10),
    FlowersFruits("Flores y Frutas", SeasonalMotionStyle.Bloom, 10),
    PawkarRaymi("Pawkar Raymi", SeasonalMotionStyle.Orbit, 7),
    HolyWeek("Semana Santa", SeasonalMotionStyle.Fall, 6),
    Kasama("Kasama Tsáchila", SeasonalMotionStyle.Wind, 7),
    Chirimoya("Festival de la Chirimoya", SeasonalMotionStyle.Orbit, 6),
    MothersDay("Día de Mamá", SeasonalMotionStyle.Bloom, 9),
    Pichincha("24 de Mayo", SeasonalMotionStyle.Fall, 6),
    CorpusChristi("Corpus Christi", SeasonalMotionStyle.Orbit, 6),
    FathersDay("Día de Papá", SeasonalMotionStyle.Fall, 7),
    IntiRaymi("Inti Raymi", SeasonalMotionStyle.Orbit, 8),
    SanPedro("San Pedro", SeasonalMotionStyle.Wind, 7),
    ChagrasMachachi("Chagras de Machachi", SeasonalMotionStyle.Wind, 7),
    GuayaquilJuly("Fiestas Julianas", SeasonalMotionStyle.Wind, 7),
    AugustIndependence("10 de Agosto", SeasonalMotionStyle.Burst, 7),
    VirgenDelCisne("Virgen del Cisne", SeasonalMotionStyle.Fall, 6),
    Yamor("Fiesta del Yamor", SeasonalMotionStyle.Orbit, 7),
    GuayaquilOctober("Guayaquil", SeasonalMotionStyle.Wind, 7),
    RodeoMontuvio("Rodeo Montuvio", SeasonalMotionStyle.Wind, 7),
    Halloween("Halloween", SeasonalMotionStyle.Fall, 7),
    Difuntos("Difuntos", SeasonalMotionStyle.Fall, 6),
    CuencaIndependence("Independencia de Cuenca", SeasonalMotionStyle.Burst, 6),
    MamaNegra("Mama Negra", SeasonalMotionStyle.Burst, 8),
    Quito("Fiestas de Quito", SeasonalMotionStyle.Wind, 7),
    Christmas("Navidad", SeasonalMotionStyle.Fall, 9),
    NewYearsEve("Año Viejo", SeasonalMotionStyle.Burst, 10),
}

enum class SeasonalMotionStyle {
    Fall,
    Wind,
    Orbit,
    Burst,
    Bloom,
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

        fun fixed(month: Month, day: Int): LocalDate = LocalDate.of(year, month, day)

        val easter = easterSunday(year)
        val carnivalTuesday = easter.minusDays(47)
        val corpusChristi = easter.plusDays(60)

        // Highest-priority campaigns first. Close Ecuadorian dates intentionally use shorter windows.
        if (range(Month.DECEMBER, 27, Month.DECEMBER, 31)) return AltosSeasonalTheme.NewYearsEve
        if (around(
                fixed(Month.DECEMBER, 25),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.Christmas
        if (around(fixed(Month.DECEMBER, 6), before = 7, after = 1)) return AltosSeasonalTheme.Quito
        if (around(
                fixed(Month.NOVEMBER, 11),
                before = 6,
                after = 1
            )
        ) return AltosSeasonalTheme.MamaNegra
        if (range(
                Month.NOVEMBER,
                3,
                Month.NOVEMBER,
                4
            )
        ) return AltosSeasonalTheme.CuencaIndependence
        if (range(Month.NOVEMBER, 1, Month.NOVEMBER, 3)) return AltosSeasonalTheme.Difuntos
        if (range(Month.OCTOBER, 27, Month.OCTOBER, 31)) return AltosSeasonalTheme.Halloween
        if (around(
                fixed(Month.OCTOBER, 12),
                before = 1,
                after = 1
            )
        ) return AltosSeasonalTheme.RodeoMontuvio
        if (around(
                fixed(Month.OCTOBER, 9),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.GuayaquilOctober
        if (around(
                fixed(Month.SEPTEMBER, 8),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.Yamor
        if (around(
                fixed(Month.AUGUST, 15),
                before = 3,
                after = 1
            )
        ) return AltosSeasonalTheme.VirgenDelCisne
        if (around(
                fixed(Month.AUGUST, 10),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.AugustIndependence

        val chagras = nthWeekday(year, Month.JULY, DayOfWeek.SUNDAY, 3)
        if (around(chagras, before = 7, after = 1)) return AltosSeasonalTheme.ChagrasMachachi

        if (around(
                fixed(Month.JULY, 25),
                before = 4,
                after = 1
            )
        ) return AltosSeasonalTheme.GuayaquilJuly
        if (around(fixed(Month.JUNE, 29), before = 6, after = 1)) return AltosSeasonalTheme.SanPedro
        if (around(
                fixed(Month.JUNE, 21),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.IntiRaymi

        val fathersDay = nthWeekday(year, Month.JUNE, DayOfWeek.SUNDAY, 3)
        if (around(fathersDay, before = 3, after = 1)) return AltosSeasonalTheme.FathersDay

        if (around(corpusChristi, before = 7, after = 1)) return AltosSeasonalTheme.CorpusChristi
        if (around(fixed(Month.MAY, 24), before = 7, after = 1)) return AltosSeasonalTheme.Pichincha

        val mothersDay = nthWeekday(year, Month.MAY, DayOfWeek.SUNDAY, 2)
        if (around(mothersDay, before = 7, after = 1)) return AltosSeasonalTheme.MothersDay

        if (around(fixed(Month.MAY, 3), before = 7, after = 1)) return AltosSeasonalTheme.Chirimoya
        if (around(fixed(Month.APRIL, 14), before = 7, after = 1)) return AltosSeasonalTheme.Kasama
        if (around(easter, before = 7, after = 1)) return AltosSeasonalTheme.HolyWeek
        if (around(
                fixed(Month.MARCH, 21),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.PawkarRaymi
        if (around(carnivalTuesday, before = 4, after = 1)) return AltosSeasonalTheme.FlowersFruits
        if (around(carnivalTuesday, before = 7, after = -5)) return AltosSeasonalTheme.Carnival
        if (around(
                fixed(Month.FEBRUARY, 14),
                before = 7,
                after = 1
            )
        ) return AltosSeasonalTheme.ValentinesDay
        if (around(fixed(Month.JANUARY, 6), before = 0, after = 1)) return AltosSeasonalTheme.Reyes
        if (range(Month.JANUARY, 3, Month.JANUARY, 5)) return AltosSeasonalTheme.Diablada
        if (range(Month.JANUARY, 1, Month.JANUARY, 2)) return AltosSeasonalTheme.NewYear

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
fun SeasonalHeroSurface(
    sectionTheme: AppSectionTheme,
    modifier: Modifier = Modifier,
    seasonalTheme: AltosSeasonalTheme? = rememberCurrentAltosSeasonalTheme(),
    content: @Composable BoxScope.() -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(sectionTheme, darkTheme)
    val shape = RoundedCornerShape(34.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.heroGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (seasonalTheme == null) 0.16f else 0.26f),
                shape = shape,
            ),
    ) {
        SeasonalAnimatedCardBackground(
            seasonalTheme = seasonalTheme,
            darkTheme = darkTheme,
            modifier = Modifier.matchParentSize(),
            intensity = if (seasonalTheme == AltosSeasonalTheme.ValentinesDay) 1.22f else 1.0f,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = if (darkTheme) 0.18f else 0.06f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        content()
    }
}

@Composable
fun SeasonalImageCardBox(
    sectionTheme: AppSectionTheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    cornerRadiusDp: Int = 26,
    seasonalTheme: AltosSeasonalTheme? = rememberCurrentAltosSeasonalTheme(),
    content: @Composable BoxScope.() -> Unit,
) {
    val darkTheme = LocalBrandDarkTheme.current
    val palette = AppTheme.palette(sectionTheme, darkTheme)
    val shape = RoundedCornerShape(cornerRadiusDp.dp)
    val clickModifier =
        if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(palette.card)
            .then(clickModifier)
            .border(
                width = 1.dp,
                color = if (seasonalTheme == null) palette.stroke else palette.stroke.copy(alpha = 0.76f),
                shape = shape,
            ),
    ) {
        SeasonalAnimatedCardBackground(
            seasonalTheme = seasonalTheme,
            darkTheme = darkTheme,
            modifier = Modifier.matchParentSize(),
            intensity = if (seasonalTheme == AltosSeasonalTheme.ValentinesDay) 1.05f else 0.82f,
        )

        content()
    }
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
            intensity = if (sectionTheme == AppSectionTheme.Neutral) 0.68f else 0.88f,
        )

        Column(
            modifier = Modifier.padding(AppTheme.Metrics.cardPadding),
            verticalArrangement = verticalArrangement,
            content = content,
        )
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
    val washProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6_400,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "altos-seasonal-wash-progress",
    )
    val washPulse = 0.78f + sin(washProgress * PI.toFloat() * 2f) * 0.22f

    val colors = seasonalColors(seasonalTheme, darkTheme)

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val heightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)
        val particleTotal = seasonalParticleBudget(
            theme = seasonalTheme,
            intensity = intensity,
            compact = maxWidth < 180.dp || maxHeight < 120.dp,
            medium = maxWidth < 260.dp || maxHeight < 160.dp,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colors[0].copy(alpha = (0.15f + washPulse * 0.035f) * intensity),
                            colors[1 % colors.size].copy(alpha = (0.10f + washPulse * 0.025f) * intensity),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Icon(
            imageVector = seasonalIcon(seasonalTheme),
            contentDescription = null,
            tint = colors.first().copy(alpha = if (darkTheme) 0.115f else 0.082f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 6.dp, end = 2.dp)
                .size((if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.78f)
                .graphicsLayer {
                    rotationZ = -10f + sin(washProgress * PI.toFloat() * 2f) * 3.5f
                    val watermarkScale = 1f + washPulse * 0.035f
                    scaleX = watermarkScale
                    scaleY = watermarkScale
                },
        )

        repeat(particleTotal) { index ->
            val particle = remember(seasonalTheme, index, particleTotal) {
                ComposeSeasonalParticle(
                    index = index,
                    total = particleTotal,
                    style = seasonalTheme.motionStyle,
                )
            }
            val animatedProgress by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = particle.durationMillis,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "altos-seasonal-particle-${seasonalTheme.name}-$index",
            )
            val localProgress = ((animatedProgress + particle.offset) % 1f).coerceIn(0f, 1f)
            val position = particle.position(
                progress = localProgress,
                width = widthPx,
                height = heightPx,
            )
            val scale = particle.scale(localProgress, intensity)
            val icon = seasonalParticleIcon(seasonalTheme, index)
            val tint = colors[index % colors.size]

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint.copy(alpha = particle.animatedOpacity(localProgress, intensity)),
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

private fun seasonalParticleBudget(
    theme: AltosSeasonalTheme,
    intensity: Float,
    compact: Boolean,
    medium: Boolean,
): Int {
    val base = theme.particleCount.coerceIn(0, 10)
    if (base == 0) return 0

    val sizeLimit = when {
        compact -> 5
        medium -> 7
        else -> 10
    }
    val intensityLimit = (base * intensity.coerceIn(0.72f, 1.18f)).roundToInt().coerceAtLeast(4)
    return min(base, min(sizeLimit, intensityLimit)).coerceAtMost(10)
}

private data class ComposeSeasonalParticle(
    val index: Int,
    val total: Int,
    val style: SeasonalMotionStyle,
) {
    private val emphasized: Boolean = index == 0 || index % 4 == 0
    private val lane: Int = index % 4

    val baseX: Float = distributedX(index, total)
    val baseY: Float = distributedY(index, total)
    val offset: Float = random(index * 41 + 5, 0f, 1f)
    val drift: Float =
        random(index * 47 + 11, if (emphasized) 34f else 24f, if (emphasized) 122f else 96f)
    val phase: Float = random(index * 53 + 13, 0f, (PI * 2).toFloat())
    val durationMillis: Int = random(
        seed = index * 43 + 7,
        min = when (style) {
            SeasonalMotionStyle.Burst -> 3_900f
            SeasonalMotionStyle.Wind -> 4_800f
            SeasonalMotionStyle.Orbit -> 5_400f
            SeasonalMotionStyle.Fall -> 5_300f
            SeasonalMotionStyle.Bloom -> 5_800f
        },
        max = when (style) {
            SeasonalMotionStyle.Burst -> 6_600f
            SeasonalMotionStyle.Wind -> 8_400f
            SeasonalMotionStyle.Orbit -> 9_200f
            SeasonalMotionStyle.Fall -> 8_900f
            SeasonalMotionStyle.Bloom -> 9_600f
        },
    ).roundToInt()
    val size: Float = if (emphasized) {
        random(
            seed = index * 59 + 17,
            min = if (style == SeasonalMotionStyle.Burst || style == SeasonalMotionStyle.Bloom) 23f else 20f,
            max = if (style == SeasonalMotionStyle.Burst || style == SeasonalMotionStyle.Bloom) 38f else 33f,
        )
    } else {
        random(
            seed = index * 59 + 17,
            min = 12f,
            max = if (style == SeasonalMotionStyle.Burst || style == SeasonalMotionStyle.Bloom) 28f else 25f,
        )
    }
    private val opacity: Float =
        random(index * 61 + 19, if (emphasized) 0.30f else 0.22f, if (emphasized) 0.64f else 0.54f)

    fun position(progress: Float, width: Float, height: Float): ParticlePosition {
        val twoPi = PI.toFloat() * 2f
        val fastWave = sin(progress * twoPi * 2.15f + phase)
        val slowWave = cos(progress * twoPi + phase * 0.72f)

        return when (style) {
            SeasonalMotionStyle.Fall -> {
                val laneSway = if (lane % 2 == 0) 1f else -1f
                ParticlePosition(
                    x = width * baseX + sin(progress * twoPi + phase) * drift + fastWave * drift * 0.18f * laneSway,
                    y = -54f + (height + 108f) * progress + slowWave * 7f,
                )
            }

            SeasonalMotionStyle.Wind -> {
                ParticlePosition(
                    x = -58f + (width + 116f) * progress + slowWave * drift * 0.18f,
                    y = height * baseY + sin(progress * twoPi + phase) * drift * 0.58f + fastWave * 9f,
                )
            }

            SeasonalMotionStyle.Orbit -> {
                val centerX = width * baseX
                val centerY = height * baseY
                val radius = drift * if (emphasized) 0.70f else 0.56f
                val angle = progress * twoPi + phase
                val figureEight = sin(angle * 2f + phase) * radius * 0.24f
                ParticlePosition(
                    x = centerX + cos(angle) * radius + figureEight,
                    y = centerY + sin(angle) * radius * 0.74f + slowWave * 10f,
                )
            }

            SeasonalMotionStyle.Burst -> {
                val originX = width * 0.50f
                val originY = height * 0.45f
                val angle = baseX * twoPi + phase * 0.15f + progress * 0.62f
                val distance = (0.12f + progress * 1.04f) * min(
                    width,
                    height
                ) * if (emphasized) 0.98f else 0.86f
                ParticlePosition(
                    x = originX + cos(angle) * distance + fastWave * 8f,
                    y = originY + sin(angle) * distance + slowWave * 8f,
                )
            }

            SeasonalMotionStyle.Bloom -> {
                val centerX = width * (0.16f + baseX * 0.68f)
                val centerY = height * (0.16f + baseY * 0.68f)
                val radius = drift * (0.18f + progress * if (emphasized) 0.74f else 0.62f)
                val angle = progress * twoPi + phase
                val floatY = sin(progress * PI.toFloat() + phase) * 17f - progress * 24f
                ParticlePosition(
                    x = centerX + cos(angle) * radius + sin(progress * twoPi * 2f + phase) * 11f,
                    y = centerY + sin(angle) * radius + floatY + slowWave * 5f,
                )
            }
        }
    }

    fun rotation(progress: Float): Float = when (style) {
        SeasonalMotionStyle.Orbit -> progress * 165f + phase * 12f
        SeasonalMotionStyle.Burst -> progress * 380f + phase * 8f
        SeasonalMotionStyle.Wind -> sin(progress * PI.toFloat() * 2f + phase) * 34f + progress * 36f
        SeasonalMotionStyle.Fall -> progress * 230f + phase * 9f
        SeasonalMotionStyle.Bloom -> sin(progress * PI.toFloat() * 2f + phase) * 42f + progress * 130f
    }

    fun scale(progress: Float, intensity: Float): Float {
        val emphasisBoost = if (emphasized) 0.09f else 0f
        val intensityBoost = (intensity - 1f).coerceIn(0f, 0.18f)
        return when (style) {
            SeasonalMotionStyle.Burst -> 0.76f + sin(progress * PI.toFloat()).coerceAtLeast(0f) * 0.48f + emphasisBoost + intensityBoost
            SeasonalMotionStyle.Bloom -> 0.78f + sin(progress * PI.toFloat()).coerceAtLeast(0f) * 0.42f + emphasisBoost + intensityBoost
            else -> 0.86f + sin(progress * PI.toFloat() * 2f + phase) * 0.16f + emphasisBoost + intensityBoost
        }.coerceIn(0.72f, 1.42f)
    }

    fun animatedOpacity(progress: Float, intensity: Float): Float {
        val pulse = 0.80f + sin(progress * PI.toFloat() * 2f + phase) * 0.20f
        val edgeFade = when (style) {
            SeasonalMotionStyle.Burst, SeasonalMotionStyle.Bloom -> sin(progress * PI.toFloat()).coerceIn(
                0.30f,
                1f
            )

            else -> 1f
        }
        return (opacity * pulse * edgeFade * intensity.coerceIn(0.72f, 1.30f)).coerceIn(
            0.12f,
            0.74f
        )
    }

    companion object {
        private fun columns(total: Int): Int = when {
            total <= 1 -> 1
            total <= 4 -> 2
            total <= 6 -> 3
            else -> 4
        }

        private fun distributedX(index: Int, total: Int): Float {
            val columns = columns(total)
            val rows = ((total + columns - 1) / columns).coerceAtLeast(1)
            val row = index / columns
            val column = index % columns
            val itemsInLastRow = total % columns
            val isLastPartialRow = row == rows - 1 && itemsInLastRow != 0
            val rowOffset = if (isLastPartialRow) (columns - itemsInLastRow) / 2f else 0f
            val jitter = random(index * 67 + 23, -0.16f, 0.16f)
            return ((column + rowOffset + 0.5f + jitter) / columns).coerceIn(0.07f, 0.93f)
        }

        private fun distributedY(index: Int, total: Int): Float {
            val columns = columns(total)
            val rows = ((total + columns - 1) / columns).coerceAtLeast(1)
            val row = index / columns
            val jitter = random(index * 71 + 29, -0.15f, 0.15f)
            return ((row + 0.5f + jitter) / rows).coerceIn(0.09f, 0.91f)
        }

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
            color(0xB197FC),
        )

        AltosSeasonalTheme.Diablada -> listOf(
            color(0xE03131),
            color(0xF59F00),
            color(0x212529),
            Color.White,
        )

        AltosSeasonalTheme.Reyes -> listOf(
            color(0xFFD43B),
            Color.White,
            color(0xB197FC),
            color(0x4DABF7),
        )

        AltosSeasonalTheme.Carnival -> listOf(
            color(0xF783AC),
            color(0x4DABF7),
            color(0x69DB7C),
            color(0xFFD43B),
            color(0xB197FC),
        )

        AltosSeasonalTheme.ValentinesDay -> listOf(
            color(0xFF4D8D),
            color(0xFFB3C7),
            color(0xF783AC),
            color(0xE64980),
            color(0xFFF0F6),
            color(0xC2255C),
        )

        AltosSeasonalTheme.FlowersFruits -> listOf(
            color(0xF783AC),
            color(0xFFDEEB),
            color(0xFF922B),
            color(0x82C91E),
            color(0xFFD43B),
        )

        AltosSeasonalTheme.PawkarRaymi -> listOf(
            color(0x69DB7C),
            color(0xFFD43B),
            color(0xFF922B),
            color(0x38D9A9),
        )

        AltosSeasonalTheme.HolyWeek -> listOf(
            color(0x9775FA),
            color(0xFFD43B),
            color(0x8CE99A),
            Color.White,
            color(0xA16207),
        )

        AltosSeasonalTheme.Kasama -> listOf(
            color(0x2F9E44),
            color(0xFFD43B),
            color(0x7950F2),
            color(0xFF922B),
        )

        AltosSeasonalTheme.Chirimoya -> listOf(
            color(0x82C91E),
            Color.White,
            color(0xFFD43B),
            color(0x38D9A9),
        )

        AltosSeasonalTheme.MothersDay -> listOf(
            color(0xF06595),
            color(0xFCC2D7),
            color(0xFF8787),
            color(0xB197FC),
        )

        AltosSeasonalTheme.Pichincha -> listOf(
            color(0xFFD43B),
            color(0x4DABF7),
            color(0xFF6B6B),
            Color.White,
        )

        AltosSeasonalTheme.CorpusChristi -> listOf(
            color(0x7950F2),
            color(0xFFD43B),
            Color.White,
            color(0xFF922B),
        )

        AltosSeasonalTheme.FathersDay -> listOf(
            color(0x4DABF7),
            color(0x74C0FC),
            color(0xFFD43B),
            color(0xADB5BD),
        )

        AltosSeasonalTheme.IntiRaymi -> listOf(
            color(0xFFD43B),
            color(0xFF922B),
            color(0xF76707),
            color(0x69DB7C),
        )

        AltosSeasonalTheme.SanPedro -> listOf(
            color(0xFFD43B),
            color(0xE67700),
            color(0x7950F2),
            color(0x2F9E44),
        )

        AltosSeasonalTheme.ChagrasMachachi -> listOf(
            color(0xA16207),
            color(0xD9480F),
            color(0x2B8A3E),
            Color.White,
        )

        AltosSeasonalTheme.GuayaquilJuly -> listOf(
            color(0x4DABF7),
            Color.White,
            color(0xFFD43B),
            color(0x228BE6),
        )

        AltosSeasonalTheme.AugustIndependence -> listOf(
            color(0xFFD43B),
            color(0x228BE6),
            color(0xFA5252),
            Color.White,
        )

        AltosSeasonalTheme.VirgenDelCisne -> listOf(
            color(0x74C0FC),
            Color.White,
            color(0xFFD43B),
            color(0xB197FC),
        )

        AltosSeasonalTheme.Yamor -> listOf(
            color(0xFFD43B),
            color(0x82C91E),
            color(0xFF922B),
            color(0x7950F2),
        )

        AltosSeasonalTheme.GuayaquilOctober -> listOf(
            color(0x228BE6),
            Color.White,
            color(0xFFD43B),
            color(0x4DABF7),
        )

        AltosSeasonalTheme.RodeoMontuvio -> listOf(
            color(0xD9480F),
            color(0xF59F00),
            color(0xA16207),
            Color.White,
        )

        AltosSeasonalTheme.Halloween -> listOf(
            color(0xF76707),
            color(0x845EF7),
            color(0x212529),
            color(0xFFD43B),
        )

        AltosSeasonalTheme.Difuntos -> listOf(
            color(0x862E9C),
            color(0xF783AC),
            color(0xFF922B),
            color(0x8CE99A),
        )

        AltosSeasonalTheme.CuencaIndependence -> listOf(
            color(0xC92A2A),
            color(0xFFD43B),
            color(0x228BE6),
            Color.White,
        )

        AltosSeasonalTheme.MamaNegra -> listOf(
            color(0xE03131),
            color(0xF59F00),
            color(0x7950F2),
            color(0x212529),
        )

        AltosSeasonalTheme.Quito -> listOf(
            color(0xC92A2A),
            color(0x228BE6),
            color(0xFFD43B),
            Color.White,
        )

        AltosSeasonalTheme.Christmas -> listOf(
            color(0xE03131),
            color(0x2F9E44),
            Color.White,
            color(0x74C0FC),
            color(0xFFD43B),
        )

        AltosSeasonalTheme.NewYearsEve -> listOf(
            color(0xFFD43B),
            color(0xFF922B),
            color(0xE03131),
            Color.White,
        )
    }.map { it.copy(alpha = it.alpha * lightAlphaBoost) }
}

private fun seasonalIcon(theme: AltosSeasonalTheme): ImageVector = when (theme) {
    AltosSeasonalTheme.NewYear -> Icons.Rounded.AutoAwesome
    AltosSeasonalTheme.Diablada -> Icons.Rounded.TheaterComedy
    AltosSeasonalTheme.Reyes -> Icons.Rounded.CardGiftcard
    AltosSeasonalTheme.Carnival -> Icons.Rounded.Celebration
    AltosSeasonalTheme.ValentinesDay -> Icons.Rounded.Favorite
    AltosSeasonalTheme.FlowersFruits -> Icons.Rounded.LocalFlorist
    AltosSeasonalTheme.PawkarRaymi -> Icons.Rounded.LocalFlorist
    AltosSeasonalTheme.HolyWeek -> Icons.Rounded.Restaurant
    AltosSeasonalTheme.Kasama -> Icons.Rounded.Person
    AltosSeasonalTheme.Chirimoya -> Icons.Rounded.Park
    AltosSeasonalTheme.MothersDay -> Icons.Rounded.Favorite
    AltosSeasonalTheme.Pichincha -> Icons.Rounded.Flag
    AltosSeasonalTheme.CorpusChristi -> Icons.Rounded.AutoAwesome
    AltosSeasonalTheme.FathersDay -> Icons.Rounded.Person
    AltosSeasonalTheme.IntiRaymi -> Icons.Rounded.WbSunny
    AltosSeasonalTheme.SanPedro -> Icons.Rounded.Celebration
    AltosSeasonalTheme.ChagrasMachachi -> Icons.Rounded.EmojiEvents
    AltosSeasonalTheme.GuayaquilJuly -> Icons.Rounded.DirectionsBoat
    AltosSeasonalTheme.AugustIndependence -> Icons.Rounded.Flag
    AltosSeasonalTheme.VirgenDelCisne -> Icons.Rounded.AutoAwesome
    AltosSeasonalTheme.Yamor -> Icons.Rounded.Restaurant
    AltosSeasonalTheme.GuayaquilOctober -> Icons.Rounded.Flag
    AltosSeasonalTheme.RodeoMontuvio -> Icons.Rounded.EmojiEvents
    AltosSeasonalTheme.Halloween -> Icons.Rounded.DarkMode
    AltosSeasonalTheme.Difuntos -> Icons.Rounded.Coffee
    AltosSeasonalTheme.CuencaIndependence -> Icons.Rounded.Flag
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
            Icons.Rounded.Celebration,
        )

        AltosSeasonalTheme.Diablada -> listOf(
            Icons.Rounded.TheaterComedy,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.Reyes -> listOf(
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.Carnival -> listOf(
            Icons.Rounded.Celebration,
            Icons.Rounded.AutoAwesome,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.ValentinesDay -> listOf(
            Icons.Rounded.Favorite,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.FlowersFruits -> listOf(
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.Park,
            Icons.Rounded.WbSunny,
            Icons.Rounded.Restaurant,
        )

        AltosSeasonalTheme.PawkarRaymi -> listOf(
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.WbSunny,
            Icons.Rounded.Park,
        )

        AltosSeasonalTheme.HolyWeek -> listOf(
            Icons.Rounded.Restaurant,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.Kasama -> listOf(
            Icons.Rounded.Person,
            Icons.Rounded.Park,
            Icons.Rounded.LocalFireDepartment,
        )

        AltosSeasonalTheme.Chirimoya -> listOf(
            Icons.Rounded.Park,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.WbSunny,
        )

        AltosSeasonalTheme.MothersDay -> listOf(
            Icons.Rounded.Favorite,
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.LocalFlorist,
        )

        AltosSeasonalTheme.Pichincha -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.CorpusChristi -> listOf(
            Icons.Rounded.AutoAwesome,
            Icons.Rounded.Star,
            Icons.Rounded.WbSunny,
        )

        AltosSeasonalTheme.FathersDay -> listOf(
            Icons.Rounded.Person,
            Icons.Rounded.Favorite,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.IntiRaymi -> listOf(
            Icons.Rounded.WbSunny,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.LocalFlorist,
        )

        AltosSeasonalTheme.SanPedro -> listOf(
            Icons.Rounded.Celebration,
            Icons.Rounded.WbSunny,
            Icons.Rounded.Person,
        )

        AltosSeasonalTheme.ChagrasMachachi -> listOf(
            Icons.Rounded.EmojiEvents,
            Icons.Rounded.Park,
            Icons.Rounded.LocalFireDepartment,
        )

        AltosSeasonalTheme.GuayaquilJuly -> listOf(
            Icons.Rounded.DirectionsBoat,
            Icons.Rounded.Flag,
            Icons.Rounded.WbSunny,
        )

        AltosSeasonalTheme.AugustIndependence -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.VirgenDelCisne -> listOf(
            Icons.Rounded.AutoAwesome,
            Icons.Rounded.Star,
            Icons.Rounded.LocalFlorist,
        )

        AltosSeasonalTheme.Yamor -> listOf(
            Icons.Rounded.Restaurant,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.WbSunny,
        )

        AltosSeasonalTheme.GuayaquilOctober -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.AutoAwesome,
        )

        AltosSeasonalTheme.RodeoMontuvio -> listOf(
            Icons.Rounded.EmojiEvents,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.Halloween -> listOf(
            Icons.Rounded.DarkMode,
            Icons.Rounded.Nightlight,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.Difuntos -> listOf(
            Icons.Rounded.Coffee,
            Icons.Rounded.LocalFlorist,
            Icons.Rounded.LocalFireDepartment,
        )

        AltosSeasonalTheme.CuencaIndependence -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.Restaurant,
        )

        AltosSeasonalTheme.MamaNegra -> listOf(
            Icons.Rounded.TheaterComedy,
            Icons.Rounded.Celebration,
            Icons.Rounded.LocalFireDepartment,
        )

        AltosSeasonalTheme.Quito -> listOf(
            Icons.Rounded.Flag,
            Icons.Rounded.Star,
            Icons.Rounded.Celebration,
        )

        AltosSeasonalTheme.Christmas -> listOf(
            Icons.Rounded.CardGiftcard,
            Icons.Rounded.AcUnit,
            Icons.Rounded.Star,
        )

        AltosSeasonalTheme.NewYearsEve -> listOf(
            Icons.Rounded.Celebration,
            Icons.Rounded.LocalFireDepartment,
            Icons.Rounded.AutoAwesome,
        )
    }
    return icons[index % icons.size]
}

fun AltosSeasonalTheme.shortPromise(): String = when (this) {
    AltosSeasonalTheme.NewYear -> "Año nuevo, aire libre y nuevos planes"
    AltosSeasonalTheme.Diablada -> "Máscaras, fuego y tradición popular"
    AltosSeasonalTheme.Reyes -> "Rosca, familia y últimos brillos navideños"
    AltosSeasonalTheme.Carnival -> "Carnaval, color y antojos serranos"
    AltosSeasonalTheme.ValentinesDay -> "Flores, corazones y planes para dos"
    AltosSeasonalTheme.FlowersFruits -> "Ambato florece con frutas, flores y pan"
    AltosSeasonalTheme.PawkarRaymi -> "Florecimiento, agua y nuevos ciclos"
    AltosSeasonalTheme.HolyWeek -> "Fanesca, calma y tradición familiar"
    AltosSeasonalTheme.Kasama -> "Año nuevo Tsáchila, danza y raíces"
    AltosSeasonalTheme.Chirimoya -> "Sabores de fruta, campo y feria"
    AltosSeasonalTheme.MothersDay -> "Un detalle bonito para mamá"
    AltosSeasonalTheme.Pichincha -> "Historia, bandera y orgullo nacional"
    AltosSeasonalTheme.CorpusChristi -> "Dulces, fe y fiesta patrimonial"
    AltosSeasonalTheme.FathersDay -> "Un plan para celebrar a papá"
    AltosSeasonalTheme.IntiRaymi -> "Sol, cosecha y montaña"
    AltosSeasonalTheme.SanPedro -> "Zapateo, plaza y música andina"
    AltosSeasonalTheme.ChagrasMachachi -> "Caballos, ponchos y montaña chagra"
    AltosSeasonalTheme.GuayaquilJuly -> "Puerto, río y orgullo guayaquileño"
    AltosSeasonalTheme.AugustIndependence -> "Primer grito, patria y memoria"
    AltosSeasonalTheme.VirgenDelCisne -> "Camino, devoción y encuentro familiar"
    AltosSeasonalTheme.Yamor -> "Maíz, música y fiesta otavaleña"
    AltosSeasonalTheme.GuayaquilOctober -> "Independencia, Malecón y tradición"
    AltosSeasonalTheme.RodeoMontuvio -> "Campo, destreza y cultura montuvia"
    AltosSeasonalTheme.Halloween -> "Noche divertida, misterio y antojos"
    AltosSeasonalTheme.Difuntos -> "Tradición, colada morada y memoria"
    AltosSeasonalTheme.CuencaIndependence -> "Arte, historia y orgullo cuencano"
    AltosSeasonalTheme.MamaNegra -> "Comparsas, música y color latacungueño"
    AltosSeasonalTheme.Quito -> "Fiestas, canelazo y quiteñidad"
    AltosSeasonalTheme.Christmas -> "Navidad con sabor de casa"
    AltosSeasonalTheme.NewYearsEve -> "Despedimos el año en familia"
}

fun AltosSeasonalTheme.homeHeroTitle(greeting: String): String = when (this) {
    AltosSeasonalTheme.NewYear -> "$greeting. Empieza el año en Los Altos."
    AltosSeasonalTheme.Diablada -> "$greeting. Tradición y montaña con carácter."
    AltosSeasonalTheme.Reyes -> "$greeting. Un último plan de temporada."
    AltosSeasonalTheme.Carnival -> "$greeting. Carnaval con sabor y aventura."
    AltosSeasonalTheme.ValentinesDay -> "$greeting. Celebra con amor en Los Altos."
    AltosSeasonalTheme.FlowersFruits -> "$greeting. Flores, frutas y sabores de fiesta."
    AltosSeasonalTheme.PawkarRaymi -> "$greeting. Florece una nueva salida."
    AltosSeasonalTheme.HolyWeek -> "$greeting. Semana Santa con sabor a fanesca."
    AltosSeasonalTheme.Kasama -> "$greeting. Celebra raíces y nuevos comienzos."
    AltosSeasonalTheme.Chirimoya -> "$greeting. Un plan fresco y dulce."
    AltosSeasonalTheme.MothersDay -> "$greeting. Mamá merece Los Altos."
    AltosSeasonalTheme.Pichincha -> "$greeting. Celebra Ecuador desde la montaña."
    AltosSeasonalTheme.CorpusChristi -> "$greeting. Dulces, tradición y mesa familiar."
    AltosSeasonalTheme.FathersDay -> "$greeting. Papá merece una aventura."
    AltosSeasonalTheme.IntiRaymi -> "$greeting. Celebra el sol y la montaña."
    AltosSeasonalTheme.SanPedro -> "$greeting. San Pedro suena a fiesta andina."
    AltosSeasonalTheme.ChagrasMachachi -> "$greeting. Ruta chagra cerca de la montaña."
    AltosSeasonalTheme.GuayaquilJuly -> "$greeting. Guayaquil también se celebra aquí."
    AltosSeasonalTheme.AugustIndependence -> "$greeting. Un plan con orgullo patrio."
    AltosSeasonalTheme.VirgenDelCisne -> "$greeting. Devoción, camino y sabores de casa."
    AltosSeasonalTheme.Yamor -> "$greeting. Yamor, maíz y alegría andina."
    AltosSeasonalTheme.GuayaquilOctober -> "$greeting. Celebra independencia con sabor."
    AltosSeasonalTheme.RodeoMontuvio -> "$greeting. Campo, tradición y platos fuertes."
    AltosSeasonalTheme.Halloween -> "$greeting. Una escapada con misterio."
    AltosSeasonalTheme.Difuntos -> "$greeting. Tradición que abraza."
    AltosSeasonalTheme.CuencaIndependence -> "$greeting. Cuenca celebra y Los Altos acompaña."
    AltosSeasonalTheme.MamaNegra -> "$greeting. Color, comparsa y tradición."
    AltosSeasonalTheme.Quito -> "$greeting. Fiestas, montaña y experiencias."
    AltosSeasonalTheme.Christmas -> "$greeting. Navidad sabe mejor en familia."
    AltosSeasonalTheme.NewYearsEve -> "$greeting. Cierra el año en Los Altos."
}

fun AltosSeasonalTheme.adventureHeroTitle(greeting: String): String = when (this) {
    AltosSeasonalTheme.NewYear -> "$greeting. Empieza el año con una ruta."
    AltosSeasonalTheme.Diablada -> "$greeting. Aventura con espíritu de fiesta."
    AltosSeasonalTheme.Reyes -> "$greeting. Cierra la temporada con montaña."
    AltosSeasonalTheme.Carnival -> "$greeting. Carnaval con adrenalina y sabor."
    AltosSeasonalTheme.ValentinesDay -> "$greeting. Una aventura para compartir."
    AltosSeasonalTheme.FlowersFruits -> "$greeting. Florece una aventura distinta."
    AltosSeasonalTheme.PawkarRaymi -> "$greeting. Florece una nueva aventura."
    AltosSeasonalTheme.HolyWeek -> "$greeting. Escápate con calma a la montaña."
    AltosSeasonalTheme.Kasama -> "$greeting. Nuevo ciclo, aire libre y tradición."
    AltosSeasonalTheme.Chirimoya -> "$greeting. Un paseo dulce por la montaña."
    AltosSeasonalTheme.MothersDay -> "$greeting. Mamá también merece aventura."
    AltosSeasonalTheme.Pichincha -> "$greeting. Historia y paisaje en una salida."
    AltosSeasonalTheme.CorpusChristi -> "$greeting. Tradición, dulces y montaña."
    AltosSeasonalTheme.FathersDay -> "$greeting. Papá merece ruta y parrilla."
    AltosSeasonalTheme.IntiRaymi -> "$greeting. Celebra el sol en la montaña."
    AltosSeasonalTheme.SanPedro -> "$greeting. Fiesta andina con ruta y sabor."
    AltosSeasonalTheme.ChagrasMachachi -> "$greeting. Vive una salida con alma chagra."
    AltosSeasonalTheme.GuayaquilJuly -> "$greeting. Un plan brillante como el puerto."
    AltosSeasonalTheme.AugustIndependence -> "$greeting. Aventura con orgullo de Ecuador."
    AltosSeasonalTheme.VirgenDelCisne -> "$greeting. Camino, calma y experiencia familiar."
    AltosSeasonalTheme.Yamor -> "$greeting. Celebra el maíz y la montaña."
    AltosSeasonalTheme.GuayaquilOctober -> "$greeting. Independencia con aire libre."
    AltosSeasonalTheme.RodeoMontuvio -> "$greeting. Campo, fuerza y experiencia premium."
    AltosSeasonalTheme.Halloween -> "$greeting. Una aventura con misterio."
    AltosSeasonalTheme.Difuntos -> "$greeting. Tradición, paisaje y familia."
    AltosSeasonalTheme.CuencaIndependence -> "$greeting. Historia, arte y una escapada."
    AltosSeasonalTheme.MamaNegra -> "$greeting. Color, cultura y montaña."
    AltosSeasonalTheme.Quito -> "$greeting. Fiestas, montaña y experiencias."
    AltosSeasonalTheme.Christmas -> "$greeting. Navidad con aventura familiar."
    AltosSeasonalTheme.NewYearsEve -> "$greeting. Cierra el año con una gran ruta."
}

fun AltosSeasonalTheme.homeHeroSubtitle(): String = when (this) {
    AltosSeasonalTheme.NewYear -> "Arranca el año con comida rica, aire de montaña y una reserva fácil para volver con buena energía."
    AltosSeasonalTheme.Diablada -> "Una temporada de máscaras, música y tradición popular para inspirar planes con carácter."
    AltosSeasonalTheme.Reyes -> "Últimos días de ambiente navideño: comparte algo rico y reserva una escapada familiar."
    AltosSeasonalTheme.Carnival -> "Color, música, comida serrana y experiencias para venir con amigos o familia."
    AltosSeasonalTheme.ValentinesDay -> "Arma un plan con comida, flores visuales, corazones y experiencias para compartir sin complicarte."
    AltosSeasonalTheme.FlowersFruits -> "Inspirado en Ambato: flores, frutas, pan, desfile y una mesa lista para celebrar con sabor."
    AltosSeasonalTheme.PawkarRaymi -> "La época del florecimiento pide aire libre, comida de casa y una visita para renovar energía."
    AltosSeasonalTheme.HolyWeek -> "Fanesca, tradición y calma: reserva comida o una experiencia familiar para Semana Santa."
    AltosSeasonalTheme.Kasama -> "Un guiño al nuevo año Tsáchila: raíces, color, danza y planes para salir de la rutina."
    AltosSeasonalTheme.Chirimoya -> "Celebra sabores de fruta y campo con una visita fresca, tranquila y bien servida."
    AltosSeasonalTheme.MothersDay -> "Sorprende a mamá con comida rica, paisajes y un plan familiar completo."
    AltosSeasonalTheme.Pichincha -> "Historia, bandera y orgullo ecuatoriano con comida, paisaje y actividades para compartir."
    AltosSeasonalTheme.CorpusChristi -> "Dulces tradicionales, fe popular y un ambiente familiar para sentarse a compartir."
    AltosSeasonalTheme.FathersDay -> "Comida fuerte, aventura y un día distinto para celebrar a papá."
    AltosSeasonalTheme.IntiRaymi -> "Sol, cosecha, montaña y experiencias para reconectar con lo nuestro."
    AltosSeasonalTheme.SanPedro -> "Música, zapateo y tradición andina para inspirar una salida con comida y paisaje."
    AltosSeasonalTheme.ChagrasMachachi -> "Cerca de Machachi, la cultura chagra se siente mejor con montaña, ruta y parrilla."
    AltosSeasonalTheme.GuayaquilJuly -> "Fiestas del puerto con energía alegre: celebra con comida, familia y una experiencia distinta."
    AltosSeasonalTheme.AugustIndependence -> "Primer Grito de Independencia, orgullo nacional y planes para disfrutar Ecuador."
    AltosSeasonalTheme.VirgenDelCisne -> "Una temporada de camino y devoción que invita a compartir con calma y buena mesa."
    AltosSeasonalTheme.Yamor -> "Maíz, música y tradición otavaleña para un plan andino con sabor auténtico."
    AltosSeasonalTheme.GuayaquilOctober -> "Independencia, alegría y orgullo guayaquileño con una mesa lista para celebrar."
    AltosSeasonalTheme.RodeoMontuvio -> "Campo, destreza y cultura montuvia para una experiencia con platos bien servidos."
    AltosSeasonalTheme.Halloween -> "Una temporada divertida para venir por antojos, fotos y una escapada diferente."
    AltosSeasonalTheme.Difuntos -> "Sabores tradicionales, familia y un momento tranquilo para recordar y compartir."
    AltosSeasonalTheme.CuencaIndependence -> "Arte, historia y celebración cuencana con sabores de casa y planes familiares."
    AltosSeasonalTheme.MamaNegra -> "Comparsas, color y tradición popular para encender la temporada con alegría."
    AltosSeasonalTheme.Quito -> "Fiestas, canelazo simbólico, música y montaña para celebrar cerca de casa."
    AltosSeasonalTheme.Christmas -> "Reserva comida, experiencias y combos para compartir con esa sensación de casa que no se improvisa."
    AltosSeasonalTheme.NewYearsEve -> "Comida, aventura y últimos recuerdos del año con descuentos y reservas desde una sola cuenta."
}

fun AltosSeasonalTheme.adventureHeroSubtitle(): String = when (this) {
    AltosSeasonalTheme.NewYear -> "Empieza con una ruta, fotos, comida y una reserva clara para que el primer plan salga redondo."
    AltosSeasonalTheme.Diablada -> "Una experiencia con energía intensa: montaña, adrenalina y comida para cerrar la salida."
    AltosSeasonalTheme.Reyes -> "Aprovecha los últimos días de temporada para una salida familiar sin complicarte."
    AltosSeasonalTheme.Carnival -> "Combina cuadrones, paintball, go karts o camping con comida para venir con amigos y disfrutar sin improvisar."
    AltosSeasonalTheme.ValentinesDay -> "Elige una ruta, añade comida y arma un plan para dos con corazones, flores y montaña sin complicarte."
    AltosSeasonalTheme.FlowersFruits -> "Flores, frutas y pan inspiran una escapada colorida con fotos, comida y montaña."
    AltosSeasonalTheme.PawkarRaymi -> "Aprovecha la temporada del florecimiento: aire libre, comida serrana y experiencias para reconectar."
    AltosSeasonalTheme.HolyWeek -> "Reserva una escapada tranquila, con horarios claros, fanesca de temporada y experiencias familiares."
    AltosSeasonalTheme.Kasama -> "Dale la bienvenida a un ciclo nuevo con naturaleza, ruta y una comida compartida."
    AltosSeasonalTheme.Chirimoya -> "Un plan suave, fresco y familiar para disfrutar aire libre antes de sentarse a comer."
    AltosSeasonalTheme.MothersDay -> "Prepara un día completo para mamá: paisaje, comida rica, fotos y una experiencia que se recuerde."
    AltosSeasonalTheme.Pichincha -> "Celebra historia ecuatoriana con una ruta, paisaje de sierra y comida para compartir."
    AltosSeasonalTheme.CorpusChristi -> "Una salida familiar con tradición, dulces, calma y experiencias al aire libre."
    AltosSeasonalTheme.FathersDay -> "Arma una salida con ruta, adrenalina y una buena comida para celebrar a papá como se merece."
    AltosSeasonalTheme.IntiRaymi -> "Sol, cosecha y montaña: actividades al aire libre con el sabor de Los Altos al final del camino."
    AltosSeasonalTheme.SanPedro -> "Una temporada andina para salir, zapatear simbólicamente y cerrar con parrilla."
    AltosSeasonalTheme.ChagrasMachachi -> "Caballos, ponchos y montaña inspiran una experiencia de campo premium cerca de Los Altos."
    AltosSeasonalTheme.GuayaquilJuly -> "Trae el ánimo del puerto a la sierra: aventura, fotos y comida para celebrar."
    AltosSeasonalTheme.AugustIndependence -> "Un feriado con orgullo ecuatoriano: reserva experiencia, comida y momentos al aire libre."
    AltosSeasonalTheme.VirgenDelCisne -> "Una salida tranquila para compartir camino, paisaje y sabores familiares."
    AltosSeasonalTheme.Yamor -> "Maíz, cosecha y montaña: experiencias al aire libre con sabor andino."
    AltosSeasonalTheme.GuayaquilOctober -> "Independencia guayaquileña con ruta, comida y un plan para hacer algo distinto."
    AltosSeasonalTheme.RodeoMontuvio -> "Campo y destreza inspiran una visita con actividades fuertes y comida bien servida."
    AltosSeasonalTheme.Halloween -> "Reserva una salida divertida, con misterio visual, fotos y antojos de temporada."
    AltosSeasonalTheme.Difuntos -> "Una salida tranquila para compartir, recordar y disfrutar sabores tradicionales cerca de la montaña."
    AltosSeasonalTheme.CuencaIndependence -> "Arte, historia y aire libre para convertir el feriado en una experiencia memorable."
    AltosSeasonalTheme.MamaNegra -> "Color, comparsa y energía popular para una aventura familiar con sabor serrano."
    AltosSeasonalTheme.Quito -> "Celebra las fiestas con montaña, comida y experiencias que se sienten cerca de casa."
    AltosSeasonalTheme.Christmas -> "Trae a la familia, reserva una experiencia y acompáñala con comida de casa en ambiente navideño."
    AltosSeasonalTheme.NewYearsEve -> "Cierra el año con una ruta, fotos, comida y una reserva lista antes de los abrazos de medianoche."
}

fun AltosSeasonalTheme.restaurantHeroTitle(clientName: String): String {
    val name = clientName.trim()
    val base = when (this) {
        AltosSeasonalTheme.NewYear -> "Primer antojo del año"
        AltosSeasonalTheme.Diablada -> "Sabores con carácter"
        AltosSeasonalTheme.Reyes -> "Última mesa de temporada"
        AltosSeasonalTheme.Carnival -> "Antojos de Carnaval"
        AltosSeasonalTheme.ValentinesDay -> "Sabores para enamorar"
        AltosSeasonalTheme.FlowersFruits -> "Mesa entre flores y frutas"
        AltosSeasonalTheme.PawkarRaymi -> "Sabores que florecen"
        AltosSeasonalTheme.HolyWeek -> "Fanesca y tradición"
        AltosSeasonalTheme.Kasama -> "Sabores de nuevo ciclo"
        AltosSeasonalTheme.Chirimoya -> "Dulce temporada de fruta"
        AltosSeasonalTheme.MothersDay -> "Mamá elige primero"
        AltosSeasonalTheme.Pichincha -> "Mesa con orgullo nacional"
        AltosSeasonalTheme.CorpusChristi -> "Dulces y tradición"
        AltosSeasonalTheme.FathersDay -> "Para papá, bien servido"
        AltosSeasonalTheme.IntiRaymi -> "Mesa del sol y la cosecha"
        AltosSeasonalTheme.SanPedro -> "Sabores para zapatear"
        AltosSeasonalTheme.ChagrasMachachi -> "Mesa chagra de montaña"
        AltosSeasonalTheme.GuayaquilJuly -> "Sabor a fiesta juliana"
        AltosSeasonalTheme.AugustIndependence -> "Mesa del Primer Grito"
        AltosSeasonalTheme.VirgenDelCisne -> "Sabores para compartir camino"
        AltosSeasonalTheme.Yamor -> "Maíz, bebida y mesa andina"
        AltosSeasonalTheme.GuayaquilOctober -> "Mesa guayaquileña de fiesta"
        AltosSeasonalTheme.RodeoMontuvio -> "Platos fuertes de campo"
        AltosSeasonalTheme.Halloween -> "Antojos de misterio"
        AltosSeasonalTheme.Difuntos -> "Tradición en la mesa"
        AltosSeasonalTheme.CuencaIndependence -> "Sabores de feriado cuencano"
        AltosSeasonalTheme.MamaNegra -> "Mesa de comparsa"
        AltosSeasonalTheme.Quito -> "Antojos de fiestas quiteñas"
        AltosSeasonalTheme.Christmas -> "Mesa navideña en Los Altos"
        AltosSeasonalTheme.NewYearsEve -> "Último antojo del año"
    }
    return if (name.isBlank()) base else "Hola, $name • $base"
}

fun AltosSeasonalTheme.restaurantHeroSubtitle(defaultLevelTitle: String): String = when (this) {
    AltosSeasonalTheme.NewYear -> "Empieza con algo bien servido: platos fuertes, bebidas y reservas sin complicarte."
    AltosSeasonalTheme.Diablada -> "Una temporada intensa pide sabores con fuerza, parrilla y algo caliente para compartir."
    AltosSeasonalTheme.Reyes -> "Cierra la temporada navideña con una mesa familiar y un último gusto antes de volver a la rutina."
    AltosSeasonalTheme.Carnival -> "Platos serranos, bebidas y energía de feriado para venir con familia o amigos."
    AltosSeasonalTheme.ValentinesDay -> "Platos para compartir, detalles románticos y un ambiente cálido para venir en pareja o familia."
    AltosSeasonalTheme.FlowersFruits -> "Inspirado en Ambato: colores, fruta, pan y platos familiares para celebrar bonito."
    AltosSeasonalTheme.PawkarRaymi -> "Una temporada de florecimiento con sabores frescos, comida de casa y una mesa tranquila."
    AltosSeasonalTheme.HolyWeek -> "Fanesca, sabores tradicionales y una mesa familiar para vivir Semana Santa con calma."
    AltosSeasonalTheme.Kasama -> "Celebra nuevos comienzos con platos para compartir y un ambiente lleno de raíz ecuatoriana."
    AltosSeasonalTheme.Chirimoya -> "Un guiño dulce y fresco para acompañar platos familiares, postres y bebidas."
    AltosSeasonalTheme.MothersDay -> "Platos para consentir a mamá, compartir en familia y evitar que ella cocine ese día."
    AltosSeasonalTheme.Pichincha -> "Sabores ecuatorianos para celebrar historia, bandera y orgullo en la mesa."
    AltosSeasonalTheme.CorpusChristi -> "Dulces, tradición popular y comida de casa para una salida familiar."
    AltosSeasonalTheme.FathersDay -> "Platos fuertes, porciones generosas y una mesa pensada para celebrar a papá."
    AltosSeasonalTheme.IntiRaymi -> "Sol, cosecha y sabores andinos para compartir después de una buena salida."
    AltosSeasonalTheme.SanPedro -> "Música andina, tradición y platos con energía de fiesta para compartir."
    AltosSeasonalTheme.ChagrasMachachi -> "Sabores de campo, parrilla y montaña para una visita con alma chagra."
    AltosSeasonalTheme.GuayaquilJuly -> "Celebra las fiestas del puerto con comida familiar, bebidas y buen ambiente."
    AltosSeasonalTheme.AugustIndependence -> "Una fecha patria con platos ecuatorianos y una mesa lista para el feriado."
    AltosSeasonalTheme.VirgenDelCisne -> "Un momento de encuentro familiar con comida cálida y atención tranquila."
    AltosSeasonalTheme.Yamor -> "Maíz, tradición y sabores andinos para una mesa de temporada."
    AltosSeasonalTheme.GuayaquilOctober -> "Independencia, alegría y platos para compartir como feriado de costa en la sierra."
    AltosSeasonalTheme.RodeoMontuvio -> "Platos fuertes, sabor de campo y energía montuvia en una mesa familiar."
    AltosSeasonalTheme.Halloween -> "Antojos, bebidas y una presentación divertida para una visita diferente."
    AltosSeasonalTheme.Difuntos -> "Una fecha para sabores tradicionales, calma y conversación en familia."
    AltosSeasonalTheme.CuencaIndependence -> "Feriado, arte y sabores de casa para celebrar con una buena mesa."
    AltosSeasonalTheme.MamaNegra -> "Color, comparsa y platos serranos para una temporada alegre y familiar."
    AltosSeasonalTheme.Quito -> "Fiestas quiteñas con platos calientes, parrilla y ambiente de celebración."
    AltosSeasonalTheme.Christmas -> "Sabores de casa, parrilladas, bebidas y platos para compartir sin correr en cocina."
    AltosSeasonalTheme.NewYearsEve -> "Cierra el año con algo rico antes de los monigotes, cábalas y abrazos."
}.ifBlank { defaultLevelTitle.ifBlank { "Fotos, búsqueda y pedido paso a paso" } }

fun AltosSeasonalTheme.badgeImageVector(): ImageVector = seasonalIcon(this)
