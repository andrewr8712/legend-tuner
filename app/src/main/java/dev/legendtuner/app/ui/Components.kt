package dev.legendtuner.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.access.AccessMode
import dev.legendtuner.app.data.SettingDef
import dev.legendtuner.app.data.SettingScope
import dev.legendtuner.app.data.SettingType
import dev.legendtuner.app.ui.theme.Amber
import dev.legendtuner.app.ui.theme.Bg
import dev.legendtuner.app.ui.theme.BgElevated
import dev.legendtuner.app.ui.theme.Dim
import dev.legendtuner.app.ui.theme.Ink
import dev.legendtuner.app.ui.theme.Lime
import dev.legendtuner.app.ui.theme.Line
import dev.legendtuner.app.ui.theme.LineSoft
import dev.legendtuner.app.ui.theme.Muted
import dev.legendtuner.app.ui.theme.Surface
import dev.legendtuner.app.ui.theme.SurfaceHigh
import dev.legendtuner.app.ui.theme.SurfaceHighest

enum class AppDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", LegendIcons.Dashboard),
    EDITOR("Graphics", LegendIcons.Tune),
    BACKUPS("Backups", LegendIcons.Backups),
    RAW("Raw XML", LegendIcons.Code)
}

@Composable
fun LegendBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0D09), Bg, Color(0xFF050705))
                )
            )
            drawRect(
                Brush.radialGradient(
                    colors = listOf(Lime.copy(alpha = 0.065f), Color.Transparent),
                    center = Offset(size.width * 0.42f, -size.height * 0.18f),
                    radius = size.width * 0.62f
                )
            )
            val step = 44.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(Color.White.copy(alpha = 0.016f), Offset(x, 0f), Offset(x, size.height), 1f)
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(Color.White.copy(alpha = 0.016f), Offset(0f, y), Offset(size.width, y), 1f)
                y += step
            }
        }
        content()
    }
}

@Composable
fun NavRail(
    selected: AppDestination,
    mode: AccessMode?,
    onSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(86.dp)
            .fillMaxHeight()
            .background(Color(0x66070908))
            .border(width = 1.dp, color = LineSoft, shape = RoundedCornerShape(0.dp))
            .padding(top = 18.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Lime),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "L",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF11150B),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))
        AppDestination.entries.forEach { destination ->
            val active = destination == selected
            Column(
                Modifier
                    .padding(horizontal = 9.dp, vertical = 3.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) SurfaceHighest else Color.Transparent)
                    .clickable { onSelect(destination) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    destination.icon,
                    contentDescription = destination.label,
                    tint = if (active) Lime else Dim,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    destination.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.3.sp),
                    color = if (active) Lime else Dim,
                    maxLines = 1
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (mode != null) Lime else Dim)
            )
            Text(
                mode?.label?.uppercase() ?: "SETUP",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, letterSpacing = 0.7.sp),
                color = Dim
            )
        }
    }
}

@Composable
fun CompactNav(
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xF2070908))
            .border(width = 1.dp, color = LineSoft)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppDestination.entries.forEach { destination ->
            val active = destination == selected
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(destination) }
                    .padding(vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    destination.icon,
                    contentDescription = destination.label,
                    tint = if (active) Lime else Dim,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    destination.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                    color = if (active) Lime else Dim,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun Panel(
    modifier: Modifier = Modifier,
    color: Color = Surface,
    borderColor: Color = Line,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp
    ) {
        Column(content = content)
    }
}

@Composable
fun PageHeader(
    crumb: String,
    title: String,
    subtitle: String? = null,
    mode: AccessMode?,
    onSettings: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                crumb.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                color = Dim
            )
            Spacer(Modifier.weight(1f))
            if (mode != null) {
                ModeChip(mode)
                Spacer(Modifier.width(8.dp))
            }
            Surface(
                onClick = onSettings,
                shape = RoundedCornerShape(8.dp),
                color = BgElevated,
                border = BorderStroke(1.dp, Line)
            ) {
                Row(
                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(LegendIcons.Settings, null, tint = Muted, modifier = Modifier.size(13.dp))
                    Text("Access", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Muted)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.headlineLarge, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                actions()
            }
        }
    }
}

@Composable
fun ModeChip(mode: AccessMode) {
    Row(
        Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(Lime.copy(alpha = 0.07f))
            .border(1.dp, Lime.copy(alpha = 0.28f), RoundedCornerShape(7.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(Lime))
        Text(
            mode.label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.8.sp),
            color = Lime
        )
    }
}

@Composable
fun StatusChip(text: String, lime: Boolean = true) {
    Row(
        Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(if (lime) Lime.copy(alpha = 0.07f) else BgElevated)
            .border(1.dp, if (lime) Lime.copy(alpha = 0.26f) else Line, RoundedCornerShape(7.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (lime) Box(Modifier.size(4.dp).clip(CircleShape).background(Lime))
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.9.sp),
            color = if (lime) Lime else Muted
        )
    }
}

@Composable
fun LegendButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        color = if (enabled) Lime else SurfaceHighest,
        shape = RoundedCornerShape(11.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (icon != null) Icon(icon, null, tint = Color(0xFF11150B), modifier = Modifier.size(15.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) Color(0xFF11150B) else Dim
            )
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = BgElevated,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) Icon(icon, null, tint = Muted, modifier = Modifier.size(14.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }
}

@Composable
fun DividerLine(modifier: Modifier = Modifier, color: Color = LineSoft) {
    Box(modifier.fillMaxWidth().height(1.dp).background(color))
}

@Composable
fun SectionTitle(text: String, trailing: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = Muted
        )
        Spacer(Modifier.weight(1f))
        if (trailing != null) {
            Text(trailing, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp), color = Dim)
        }
    }
}

@Composable
fun SettingRow(
    def: SettingDef,
    rawValue: Int?,
    capped: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(def.label, style = MaterialTheme.typography.bodyMedium, color = Ink)
                if (capped) {
                    Box(Modifier.size(5.dp).clip(CircleShape).background(Amber))
                }
            }
            if (def.scope != SettingScope.PRESET) {
                Text(
                    if (def.scope == SettingScope.SETUP) "Setup · live" else "Live setting",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.5.sp),
                    color = Dim
                )
            }
        }
        Text(
            def.displayValue(rawValue),
            style = MaterialTheme.typography.labelLarge,
            color = if (def.type == SettingType.LEVEL && (rawValue ?: 0) >= 2) Lime else Muted
        )
        Spacer(Modifier.width(7.dp))
        Text("›", color = Dim, fontSize = 16.sp)
    }
}

@Composable
fun PerformanceBars(cost: Int, max: Int = 5) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(max) { index ->
            Box(
                Modifier
                    .width(23.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < cost) Lime else SurfaceHighest)
            )
        }
    }
}

fun formatSize(bytes: Long): String = when {
    bytes >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f MB", bytes / (1024f * 1024f))
    bytes >= 1024 -> String.format(java.util.Locale.US, "%.1f KB", bytes / 1024f)
    else -> "$bytes B"
}

fun formatRefresh(hz: Int): String = if (hz > 0) "$hz Hz" else "—"
