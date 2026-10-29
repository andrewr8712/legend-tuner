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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.data.Preset
import dev.legendtuner.app.data.SettingDef
import dev.legendtuner.app.data.SettingScope
import dev.legendtuner.app.data.SettingType
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
import kotlin.math.roundToInt

@Composable
fun SettingInspector(
    def: SettingDef?,
    currentValue: Int?,
    cap: Int?,
    selectedPreset: Preset,
    onApply: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Panel(modifier) {
        if (def == null) {
            EmptyInspector()
            return@Panel
        }

        var pending by remember(def.key, currentValue) {
            mutableIntStateOf(
                when (def.type) {
                    SettingType.PERCENT, SettingType.INTEGER ->
                        (currentValue ?: defaultFor(def, cap)).coerceIn(def.min, def.max)
                    else -> currentValue ?: defaultFor(def, cap)
                }
            )
        }
        val presetScope = def.scope == SettingScope.PRESET
        val capped = cap != null && pending > cap
        val options = remember(def, cap, currentValue) {
            val base = def.optionsFor(cap)
            if (currentValue != null && currentValue !in base && def.type != SettingType.PERCENT && def.type != SettingType.INTEGER) {
                (base + currentValue).sorted()
            } else base
        }

        Column(Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState())) {
            Row(
                Modifier.fillMaxWidth().padding(start = 18.dp, end = 12.dp, top = 16.dp, bottom = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (presetScope) "${selectedPreset.label.uppercase()} PRESET · ${def.group.label.uppercase()}" else "LIVE · ${def.group.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp),
                        color = Dim
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(def.label, style = MaterialTheme.typography.headlineLarge, color = Ink)
                }
                Text(
                    "✕",
                    color = Dim,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClose)
                        .padding(8.dp)
                )
            }
            Text(
                def.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 15.sp),
                color = Muted,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
            DividerLine(Modifier.padding(horizontal = 18.dp))

            Spacer(Modifier.height(8.dp))
            when (def.type) {
                SettingType.DROPDOWN -> {
                    HeightDropdown(
                        pending = pending,
                        options = options,
                        currentValue = currentValue,
                        onSelect = { pending = it }
                    )
                }
                SettingType.PERCENT, SettingType.INTEGER -> {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Value", style = MaterialTheme.typography.bodyMedium, color = Muted)
                            Text(
                                if (def.type == SettingType.PERCENT) "$pending%" else pending.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Lime
                            )
                        }
                        Slider(
                            value = pending.toFloat(),
                            onValueChange = { pending = it.roundToInt().coerceIn(def.min, def.max) },
                            valueRange = def.min.toFloat()..def.max.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Lime,
                                activeTrackColor = Lime,
                                inactiveTrackColor = SurfaceHighest
                            )
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${def.min}", style = MaterialTheme.typography.labelSmall, color = Dim)
                            Text("${def.max}", style = MaterialTheme.typography.labelSmall, color = Dim)
                        }
                    }
                }
                SettingType.TOGGLE, SettingType.LEVEL, SettingType.FPS, SettingType.OPTIONS -> {
                    options.forEach { option ->
                        OptionRow(
                            label = def.displayValue(option),
                            hint = when {
                                def.type == SettingType.FPS && option == 60 -> "Recommended"
                                def.type == SettingType.LEVEL && cap != null && option == cap -> "Device cap"
                                def.type == SettingType.TOGGLE && option == 1 -> null
                                else -> null
                            },
                            selected = pending == option,
                            onClick = { pending = option }
                        )
                    }
                }
            }

            if (def.type == SettingType.LEVEL) {
                Spacer(Modifier.height(10.dp))
                PerformanceRow(SettingsCatalog.performanceCost(def, pending, cap))
                if (cap != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Device-reported cap for this setting: $cap." + if (capped) " You are above the cap; test stability." else "",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.2.sp),
                        color = if (capped) Amber else Dim,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }

            if (def.risky) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = AmberWash,
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, Amber.copy(alpha = 0.18f)),
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Text(
                        "Advanced setting. A value that does not match the game's UI layout can make menus or HUD elements render incorrectly.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp, lineHeight = 13.sp),
                        color = Color(0xFFC7B99F),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Raw: ${currentValue ?: "—"}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 7.sp),
                    color = Dim
                )
                Spacer(Modifier.weight(1f))
                GhostButton("Cancel", onClose)
                LegendButton(
                    text = if (presetScope) "Apply to ${selectedPreset.shortLabel}" else "Apply",
                    onClick = { onApply(pending) },
                    enabled = currentValue != null
                )
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeightDropdown(
    pending: Int,
    options: List<Int>,
    currentValue: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
        Text(
            "RENDER HEIGHT",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.2.sp),
            color = Dim
        )
        Spacer(Modifier.height(7.dp))
        Box {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceHigh,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Line)
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("$pending px", style = MaterialTheme.typography.headlineSmall, color = Lime)
                        Text(
                            heightHint(pending),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.2.sp),
                            color = Dim
                        )
                    }
                    Text("▾", color = Muted, fontSize = 17.sp)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(300.dp)
            ) {
                options.forEach { option ->
                    val selected = option == pending
                    DropdownMenuItem(
                        text = {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "$option px",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selected) Lime else Ink
                                    )
                                    Text(
                                        heightHint(option),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.1.sp),
                                        color = Dim
                                    )
                                }
                                if (selected) {
                                    Text("✓", color = Lime, fontSize = 15.sp)
                                } else if (currentValue == option) {
                                    Text("CURRENT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp), color = Muted)
                                }
                            }
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Common 16:9 and 16:10 render heights. Use Raw XML for an exact non-standard value.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp, letterSpacing = 0.1.sp),
            color = Dim
        )
    }
}

private fun heightHint(value: Int): String = when (value) {
    480 -> "480p · low"
    540 -> "540p"
    600 -> "600p"
    720 -> "720p · HD"
    768 -> "768p · tablet"
    800 -> "800p · tablet"
    838 -> "Game default"
    900 -> "900p"
    960 -> "960p"
    1080 -> "1080p · Full HD"
    1200 -> "1200p · 16:10"
    1440 -> "1440p · QHD"
    1600 -> "1600p · 16:10"
    1800 -> "1800p"
    1840 -> "TB520FU native"
    2160 -> "2160p · 4K"
    else -> "Custom height"
}

@Composable
private fun EmptyInspector() {
    Column(Modifier.padding(20.dp)) {
        Text("INSPECTOR", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp), color = Dim)
        Spacer(Modifier.height(8.dp))
        Text("Select a setting", style = MaterialTheme.typography.headlineMedium, color = Ink)
        Spacer(Modifier.height(7.dp))
        Text(
            "Choose any row in the two columns to edit it here. The settings grid stays visible, so you can compare values while tuning.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 15.sp),
            color = Muted
        )
    }
}

@Composable
private fun OptionRow(label: String, hint: String?, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(1.dp, if (selected) Lime else Muted.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(Lime))
        }
        Spacer(Modifier.width(11.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Lime else Ink
        )
        Spacer(Modifier.weight(1f))
        if (hint != null) {
            Text(hint, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim)
        }
    }
}

@Composable
private fun PerformanceRow(cost: Int) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "PERFORMANCE COST",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 1.2.sp),
            color = Dim
        )
        Spacer(Modifier.weight(1f))
        PerformanceBars(cost)
        Spacer(Modifier.width(8.dp))
        Text("$cost / 5", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Muted)
    }
}

private fun defaultFor(def: SettingDef, cap: Int?): Int = when (def.type) {
    SettingType.TOGGLE -> 0
    SettingType.LEVEL -> (cap ?: 0).coerceIn(0, def.max)
    SettingType.FPS -> 60
    SettingType.PERCENT -> 70
    SettingType.OPTIONS -> def.options.firstOrNull() ?: 0
    SettingType.INTEGER -> def.min
    SettingType.DROPDOWN -> if (1080 in def.options) 1080 else def.options.firstOrNull() ?: 1080
}
