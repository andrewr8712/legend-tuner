package dev.legendtuner.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.Preset
import dev.legendtuner.app.data.SettingGroup
import dev.legendtuner.app.data.SettingsCatalog
import dev.legendtuner.app.ui.theme.Amber
import dev.legendtuner.app.ui.theme.AmberWash
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

@Composable
fun DashboardScreen(
    state: AppState,
    wide: Boolean,
    onEdit: () -> Unit,
    onBackups: () -> Unit,
    onRaw: () -> Unit,
    onOpenAccess: () -> Unit,
    onQuickTune: (SettingGroup) -> Unit
) {
    val config = state.config ?: return
    val doc = config.document
    val activePreset = doc.activePreset()
    val version = doc.value(ConfigDocument.SETUP_PATH, "GameVersionString")?.text
        ?: doc.value(ConfigDocument.SETUP_PATH, "GameInstallVersionString")?.text
        ?: "GRID Legends"
    val fps = doc.presetValue(activePreset, "FramerateLimit")?.firstIntOrNull
    val textures = doc.presetValue(activePreset, "TextureQuality")?.firstIntOrNull
    val shadows = doc.presetValue(activePreset, "Shadows")?.firstIntOrNull
    val frameMax = doc.presetValue(activePreset, "FrameSizeMaximumPercentage")?.firstIntOrNull
    val dynamic = doc.presetValue(activePreset, "DynamicResolution")?.firstIntOrNull
    val height = doc.int(ConfigDocument.SETUP_PATH, "FixedScreenHeight")
    val textureLabel = SettingsCatalog.find("TextureQuality")?.displayValue(textures) ?: "—"
    val shadowLabel = SettingsCatalog.find("Shadows")?.displayValue(shadows) ?: "—"
    val dynamicLabel = SettingsCatalog.find("DynamicResolution")?.displayValue(dynamic) ?: "—"

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (wide) 26.dp else 18.dp)
    ) {
        PageHeader(
            crumb = "Legend Tuner / Dashboard",
            title = "Dashboard",
            subtitle = if (state.gameInstalled) "GRID Legends detected on this tablet" else "GRID Legends is not installed",
            mode = state.accessMode,
            onSettings = onOpenAccess,
            actions = {
                StatusChip(if (state.gameInstalled) "Game detected" else "Not installed", lime = state.gameInstalled)
            }
        )
        Spacer(Modifier.height(16.dp))

        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1.34f)) {
                    HeroPanel(
                        state = state,
                        preset = activePreset,
                        version = version,
                        fps = fps,
                        textureLabel = textureLabel,
                        shadowLabel = shadowLabel,
                        dynamicLabel = dynamicLabel,
                        height = height,
                        textureRaw = textures,
                        shadowRaw = shadows,
                        frameMax = frameMax,
                        onQuickTune = onQuickTune
                    )
                }
                Column(Modifier.weight(0.86f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardActions(state, onEdit, onBackups, onRaw, onOpenAccess)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HeroPanel(
                    state = state,
                    preset = activePreset,
                    version = version,
                    fps = fps,
                    textureLabel = textureLabel,
                    shadowLabel = shadowLabel,
                    dynamicLabel = dynamicLabel,
                    height = height,
                    textureRaw = textures,
                    shadowRaw = shadows,
                    frameMax = frameMax,
                    onQuickTune = onQuickTune
                )
                DashboardActions(state, onEdit, onBackups, onRaw, onOpenAccess)
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "LOCAL ONLY · NO NETWORK PERMISSION",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                color = Dim
            )
            Text(
                "TABLET LAYOUT · ${if (wide) "WIDE" else "COMPACT"}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                color = Dim
            )
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun HeroPanel(
    state: AppState,
    preset: Preset,
    version: String,
    fps: Int?,
    textureLabel: String,
    shadowLabel: String,
    dynamicLabel: String,
    height: Int?,
    textureRaw: Int?,
    shadowRaw: Int?,
    frameMax: Int?,
    onQuickTune: (SettingGroup) -> Unit
) {
    val file = state.config?.file
    Panel(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    "UNOFFICIAL CONFIG EDITOR",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                    color = Lime
                )
                Text(
                    "FAN MADE · NO TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                    color = Dim,
                    modifier = Modifier
                        .border(1.dp, Line, RoundedCornerShape(5.dp))
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("YOUR GRID.", style = MaterialTheme.typography.displayMedium, color = Ink, maxLines = 1)
            Text("YOUR FRAME.", style = MaterialTheme.typography.displayMedium, color = Muted, maxLines = 1)
            Spacer(Modifier.height(13.dp))
            Text(
                "Fine-tune GRID Legends on the big screen. Edit the game's own XML registry with readable controls, automatic backups, and a raw editor when you need it.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 17.sp),
                color = Muted,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(Modifier.height(17.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickTune("Frame rate", "FPS & scaling", Modifier.weight(1f)) { onQuickTune(SettingGroup.FRAME_RATE) }
                QuickTune("Visuals", "Textures · $textureLabel", Modifier.weight(1f)) { onQuickTune(SettingGroup.VISUALS) }
                QuickTune("Effects", "${SettingsCatalog.forGroup(SettingGroup.EFFECTS).size} options", Modifier.weight(1f)) { onQuickTune(SettingGroup.EFFECTS) }
            }
            Spacer(Modifier.height(14.dp))

            Snapshot(preset, shadowLabel, textureLabel, frameMax, shadowRaw, textureRaw)
            Spacer(Modifier.height(14.dp))

            Surface(
                color = Color(0x99090B09),
                shape = RoundedCornerShape(13.dp),
                border = BorderStroke(1.dp, Line)
            ) {
                Column(Modifier.padding(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(Lime))
                        Text(
                            "GAME FOUND",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp),
                            color = Lime
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            state.accessMode?.label?.uppercase() ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, letterSpacing = 1.sp),
                            color = Lime,
                            modifier = Modifier
                                .border(1.dp, Lime.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        file?.displayPath ?: "—",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp,
                            lineHeight = 13.sp
                        ),
                        color = Muted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(7.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text(version, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim)
                        Dot()
                        Text(formatSize(file?.size ?: 0L), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim)
                        Dot()
                        Text(file?.volumeLabel ?: "—", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim)
                    }
                    DividerLine(Modifier.padding(top = 10.dp))
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                        Stat("${fps ?: "—"}", "FPS CAP", Modifier.weight(1f), first = true)
                        Stat(textureLabel.uppercase(), "TEXTURES", Modifier.weight(1f))
                        Stat(dynamicLabel.uppercase(), "DYN. RES", Modifier.weight(1f))
                        Stat("${height ?: "—"}", "RENDER HEIGHT", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTune(label: String, sub: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = BgElevated,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Line)
    ) {
        Row(Modifier.padding(horizontal = 11.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Ink)
                Text(sub, style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.2.sp), color = Dim)
            }
        }
    }
}

@Composable
private fun Snapshot(
    preset: Preset,
    shadowLabel: String,
    textureLabel: String,
    frameMax: Int?,
    shadowRaw: Int?,
    textureRaw: Int?
) {
    Surface(
        color = Color(0xFF0C0F0B),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LineSoft)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 11.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "CURRENT PRESET SNAPSHOT",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 1.2.sp),
                    color = Dim
                )
                Text(
                    "${preset.label.uppercase()} · ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.8.sp),
                    color = Lime
                )
            }
            Spacer(Modifier.height(7.dp))
            SnapshotRow("Shadows", ((shadowRaw ?: 0) / 3f).coerceIn(0f, 1f), shadowLabel)
            SnapshotRow("Textures", ((textureRaw ?: 0) / 3f).coerceIn(0f, 1f), textureLabel)
            SnapshotRow("Resolution", ((frameMax ?: 0) / 100f).coerceIn(0f, 1f), frameMax?.let { "$it%" } ?: "—")
        }
    }
}

@Composable
private fun SnapshotRow(name: String, fraction: Float, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(name, style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.width(76.dp))
        Box(
            Modifier
                .weight(1f)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SurfaceHighest)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Lime.copy(alpha = 0.78f))
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Muted, modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun Stat(value: String, key: String, modifier: Modifier, first: Boolean = false) {
    Column(
        modifier
            .padding(start = if (first) 0.dp else 12.dp)
            .then(if (first) Modifier else Modifier.border(0.dp, Color.Transparent))
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Ink, maxLines = 1)
        Text(
            key,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, letterSpacing = 1.sp),
            color = Dim,
            maxLines = 1
        )
    }
}

@Composable
private fun DashboardActions(
    state: AppState,
    onEdit: () -> Unit,
    onBackups: () -> Unit,
    onRaw: () -> Unit,
    onOpenAccess: () -> Unit
) {
    Surface(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        color = Lime,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("→", fontSize = 23.sp, color = Color(0xFF11150B))
                Text("01", style = MaterialTheme.typography.labelSmall, color = Color(0x6611150B))
            }
            Spacer(Modifier.height(8.dp))
            Text("Edit\ngraphics", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp), color = Color(0xFF11150B))
            Spacer(Modifier.height(6.dp))
            Text("3 presets · ${SettingsCatalog.settings.size} settings", style = MaterialTheme.typography.labelMedium, color = Color(0xFF3A451C))
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ActionTile("Backups", "${state.backups.size} snapshots", LegendIcons.Backups, Modifier.weight(1f), onBackups)
        ActionTile("Raw XML", "Advanced editing", LegendIcons.Code, Modifier.weight(1f), onRaw)
    }
    Panel(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceHighest),
                contentAlignment = Alignment.Center
            ) {
                Text("▭", color = Lime, fontSize = 17.sp)
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "DETECTED DISPLAY",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 1.3.sp),
                    color = Dim
                )
                Text(
                    "${state.displayInfo.widthPx} × ${state.displayInfo.heightPx} · ${state.displayInfo.refreshRate} Hz",
                    style = MaterialTheme.typography.labelLarge,
                    color = Ink
                )
            }
        }
    }
    Surface(
        color = AmberWash,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Amber.copy(alpha = 0.18f))
    ) {
        Row(Modifier.padding(13.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(LegendIcons.Warning, null, tint = Amber, modifier = Modifier.size(15.dp))
            Text(
                "Close the game before saving. GRID Legends rewrites this file when it exits, which can undo your changes. A backup is created before every write.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 14.sp),
                color = Color(0xFFC7B99F)
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = Surface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line)
    ) {
        Column(Modifier.padding(13.dp)) {
            Icon(icon, null, tint = Muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.2.sp), color = Dim)
        }
    }
}

@Composable
private fun Dot() {
    Box(Modifier.size(2.dp).clip(CircleShape).background(Dim))
}
