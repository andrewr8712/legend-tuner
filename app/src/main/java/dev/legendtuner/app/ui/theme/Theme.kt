package dev.legendtuner.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LegendColors = darkColorScheme(
    primary = Lime,
    onPrimary = Color(0xFF11150B),
    primaryContainer = Color(0xFF242B20),
    onPrimaryContainer = Lime,
    secondary = LimeDim,
    onSecondary = Color(0xFF11150B),
    background = Bg,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = Muted,
    outline = Line,
    outlineVariant = LineSoft,
    error = Red,
    onError = Color(0xFF1A0908),
    tertiary = Amber,
    onTertiary = Color(0xFF1A1205)
)

@Composable
fun LegendTunerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LegendColors,
        typography = LegendTypography,
        content = content
    )
}
