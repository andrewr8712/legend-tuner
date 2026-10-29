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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.Preset
import dev.legendtuner.app.data.SettingDef
import dev.legendtuner.app.data.SettingGroup
import dev.legendtuner.app.data.SettingsCatalog
import dev.legendtuner.app.ui.theme.Amber
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    state: AppState,
    dockedInspector: Boolean,
    twoColumns: Boolean,
    onSelectPreset: (Preset) -> Unit,
    onSelectSetting: (String?) -> Unit,
    onUpdate: (SettingDef, Int) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onOpenAccess: () -> Unit,
    onForceStop: () -> Unit,
    onReload: () -> Unit
) {
    val config = state.config ?: return
    val doc = config.document
    val selectedDef = state.selectedSettingKey?.let(SettingsCatalog::find)
    val selectedValue = selectedDef?.let { SettingsCatalog.value(doc, state.selectedPreset, it) }
    val selectedCap = selectedDef?.let { SettingsCatalog.capFor(doc, it) }

    var menuOpen by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = if (twoColumns) 24.dp else 16.dp)
    ) {
        PageHeader(
            crumb = "Legend Tuner / Graphics",
            title = "Graphics preset",
            subtitle = "Editing the preset GRID Legends will load on next launch",
            mode = state.accessMode,
            onSettings = onOpenAccess,
            actions = {
                if (state.dirty) {
                    Text(
                        "UNSAVED",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                        color = Amber,
                        modifier = Modifier
                            .border(1.dp, Amber.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                    )
                }
                if (state.dirty) {
                    GhostButton("Discard", onDiscard)
                }
                LegendButton(
                    text = if (state.saving) "Saving…" else "Save changes",
                    onClick = onSave,
                    enabled = !state.saving && (state.dirty || state.selectedPreset != doc.activePreset())
                )
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(LegendIcons.MoreVert, contentDescription = "More", tint = Muted)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Force stop GRID Legends") },
                            onClick = {
                                menuOpen = false
                                onForceStop()
                            },
                            enabled = state.forceStopAvailable
                        )
                        DropdownMenuItem(
                            text = { Text("Reload from disk") },
                            onClick = {
                                menuOpen = false
                                onReload()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Access & connection") },
                            onClick = {
                                menuOpen = false
                                onOpenAccess()
                            }
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(13.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Preset.entries.forEach { preset ->
                PresetCard(
                    preset = preset,
                    doc = doc,
                    selected = preset == state.selectedPreset,
                    active = preset == doc.activePreset(),
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectPreset(preset) }
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                if (twoColumns) {
                    Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            SettingsGroupCard(SettingGroup.FRAME_RATE, doc, state.selectedPreset, onSelectSetting)
                            SettingsGroupCard(SettingGroup.EFFECTS, doc, state.selectedPreset, onSelectSetting)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            SettingsGroupCard(SettingGroup.VISUALS, doc, state.selectedPreset, onSelectSetting)
                            SettingsGroupCard(SettingGroup.DISPLAY, doc, state.selectedPreset, onSelectSetting)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingGroup.entries.forEach { group ->
                            SettingsGroupCard(group, doc, state.selectedPreset, onSelectSetting)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        if (state.dirty) "UNSAVED CHANGES" else "ALL CHANGES SAVED",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                        color = if (state.dirty) Amber else Dim
                    )
                    Text(
                        "SAVING MAKES THIS PRESET ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                        color = Dim
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (dockedInspector) {
                SettingInspector(
                    def = selectedDef,
                    currentValue = selectedValue,
                    cap = selectedCap,
                    selectedPreset = state.selectedPreset,
                    onApply = { value ->
                        selectedDef?.let { onUpdate(it, value) }
                    },
                    onClose = { onSelectSetting(null) },
                    modifier = Modifier.width(356.dp).fillMaxHeight()
                )
            }
        }
    }

    if (!dockedInspector && selectedDef != null) {
        ModalBottomSheet(
            onDismissRequest = { onSelectSetting(null) },
            containerColor = Surface,
            contentColor = Ink,
            dragHandle = null
        ) {
            SettingInspector(
                def = selectedDef,
                currentValue = selectedValue,
                cap = selectedCap,
                selectedPreset = state.selectedPreset,
                onApply = { value ->
                    selectedDef.let { onUpdate(it, value) }
                },
                onClose = { onSelectSetting(null) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PresetCard(
    preset: Preset,
    doc: ConfigDocument,
    selected: Boolean,
    active: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val fps = doc.presetValue(preset, "FramerateLimit")?.firstIntOrNull
    val border = if (selected) Lime.copy(alpha = 0.5f) else Line
    val bg = if (selected) Lime.copy(alpha = 0.055f) else BgElevated
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = bg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 11.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(if (selected) Lime else Dim))
                Text(
                    preset.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Ink else Muted
                )
                Spacer(Modifier.weight(1f))
                if (active) {
                    Text(
                        "ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp, letterSpacing = 0.9.sp),
                        color = Lime
                    )
                }
            }
            Spacer(Modifier.height(7.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "${fps ?: "—"}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (selected) Ink else Muted
                )
                Text("fps", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim, modifier = Modifier.padding(start = 2.dp, bottom = 2.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    preset.description,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.1.sp),
                    color = Dim,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SettingsGroupCard(
    group: SettingGroup,
    doc: ConfigDocument,
    preset: Preset,
    onSelect: (String) -> Unit
) {
    val defs = SettingsCatalog.forGroup(group)
    Panel(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                group.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp),
                color = Muted
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${defs.size} SETTINGS",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.6.sp),
                color = Dim
            )
        }
        DividerLine()
        defs.forEachIndexed { index, def ->
            val value = SettingsCatalog.value(doc, preset, def)
            val cap = SettingsCatalog.capFor(doc, def)
            SettingRow(
                def = def,
                rawValue = value,
                capped = def.isCapped(value ?: 0, cap),
                onClick = { onSelect(def.key) }
            )
            if (index != defs.lastIndex) DividerLine(Modifier.padding(start = 15.dp))
        }
    }
}
