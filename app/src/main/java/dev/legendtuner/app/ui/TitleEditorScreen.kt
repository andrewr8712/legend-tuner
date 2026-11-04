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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.data.Control
import dev.legendtuner.app.data.ControlKind
import dev.legendtuner.app.data.TitleCatalog
import dev.legendtuner.app.data.TitleCatalogEngine
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleEditorScreen(
    state: AppState,
    catalog: TitleCatalog,
    root: List<String>,
    activeFamilyId: String?,
    dockedInspector: Boolean,
    twoColumns: Boolean,
    onSelect: (String?) -> Unit,
    onUpdate: (Control, String) -> Unit,
    onSelectFamily: (String) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onOpenAccess: () -> Unit
) {
    val config = state.config ?: return
    val doc = config.document
    val activeCatalog = catalog.withFamily(catalog.family(activeFamilyId))
    val selected = state.selectedSettingKey?.let(activeCatalog::find)

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = if (twoColumns) 24.dp else 16.dp)
    ) {
        PageHeader(
            crumb = "Config Tuner / ${state.activeLabel}",
            title = "Settings",
            subtitle = "Editing the values ${state.activeLabel} loads on next launch",
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
                    GhostButton("Discard", onDiscard)
                }
                LegendButton(
                    text = if (state.saving) "Saving…" else "Save changes",
                    onClick = onSave,
                    enabled = !state.saving && state.dirty
                )
            }
        )
        Spacer(Modifier.height(12.dp))

        if (catalog.families.size > 1) {
            val active = catalog.family(activeFamilyId)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                catalog.families.forEach { family ->
                    FamilyChip(
                        label = family.label,
                        selected = family.id == active?.id,
                        onClick = { onSelectFamily(family.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(2.dp))
        }

        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                if (twoColumns) {
                    val half = (catalog.groups.size + 1) / 2
                    Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            catalog.groups.take(half).forEach { group ->
                                GroupCard(group, activeCatalog, doc, root, state.revision, onSelect)
                            }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            catalog.groups.drop(half).forEach { group ->
                                GroupCard(group, activeCatalog, doc, root, state.revision, onSelect)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        catalog.groups.forEach { group ->
                            GroupCard(group, activeCatalog, doc, root, state.revision, onSelect)
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
                        "BACKUP BEFORE EVERY WRITE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
                        color = Dim
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (dockedInspector) {
                ControlInspector(
                    control = selected,
                    catalog = activeCatalog,
                    doc = doc,
                    root = root,
                    revision = state.revision,
                    onUpdate = onUpdate,
                    onClose = { onSelect(null) },
                    modifier = Modifier.width(356.dp).fillMaxHeight()
                )
            }
        }
    }

    if (!dockedInspector && selected != null) {
        ModalBottomSheet(
            onDismissRequest = { onSelect(null) },
            containerColor = Surface,
            contentColor = Ink,
            dragHandle = null
        ) {
            ControlInspector(
                control = selected,
                catalog = activeCatalog,
                doc = doc,
                root = root,
                revision = state.revision,
                onUpdate = onUpdate,
                onClose = { onSelect(null) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GroupCard(
    group: String,
    catalog: TitleCatalog,
    doc: dev.legendtuner.app.data.ConfigDocument,
    root: List<String>,
    @Suppress("UNUSED_PARAMETER") revision: Int,
    onSelect: (String) -> Unit
) {
    val controls = catalog.controlsFor(group)
    Panel(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                group.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp),
                color = Muted
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${controls.size} SETTINGS",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.6.sp),
                color = Dim
            )
        }
        DividerLine()
        controls.forEachIndexed { index, control ->
            ControlRow(
                control = control,
                value = TitleCatalogEngine.display(doc, root, catalog, control),
                hasKey = TitleCatalogEngine.value(doc, root, catalog, control) != null,
                onClick = { onSelect(control.key) }
            )
            if (index != controls.lastIndex) DividerLine(Modifier.padding(start = 15.dp))
        }
    }
}

@Composable
private fun ControlRow(
    control: Control,
    value: String,
    hasKey: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (hasKey) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(control.label, style = MaterialTheme.typography.bodyMedium, color = if (hasKey) Ink else Dim)
                if (control.note != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.size(5.dp).clip(CircleShape).background(Amber))
                }
            }
            if (!hasKey) {
                Text(
                    "Not stored in this file",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                    color = Dim
                )
            }
        }
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            color = if (hasKey) Muted else Dim
        )
        if (hasKey) {
            Spacer(Modifier.width(6.dp))
            Text("›", color = Dim, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ControlInspector(
    control: Control?,
    catalog: TitleCatalog,
    doc: dev.legendtuner.app.data.ConfigDocument,
    root: List<String>,
    @Suppress("UNUSED_PARAMETER") revision: Int,
    onUpdate: (Control, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BgElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Line)
    ) {
        if (control == null) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "SELECT A SETTING",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.4.sp),
                    color = Dim
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tap any row to see what it changes, what each option costs, and to write it.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                    color = Muted
                )
            }
            return@Surface
        }

        val current = TitleCatalogEngine.token(doc, root, catalog, control)
        val exists = TitleCatalogEngine.value(doc, root, catalog, control) != null
        val raw = TitleCatalogEngine.value(doc, root, catalog, control)?.text?.trim()
        val ladder = control.triple && exists && current == null

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${control.group.uppercase()} · ${stateCased(control.kind)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.2.sp),
                    color = Lime
                )
                Spacer(Modifier.weight(1f))
                Text("✕", color = Dim, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onClose))
            }
            Spacer(Modifier.height(10.dp))
            Text(control.label, style = MaterialTheme.typography.headlineMedium, color = Ink)
            Spacer(Modifier.height(8.dp))
            Text(
                control.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                color = Muted
            )
            Spacer(Modifier.height(16.dp))

            if (!exists) {
                Notice(
                    "This title does not store a value for this setting on this device. Config Tuner will not create one."
                )
            } else if (ladder) {
                Text(
                    raw ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
                Spacer(Modifier.height(10.dp))
                Notice(
                    "This key stores three different values — a preset ladder, not one number. Editing it here would corrupt the ladder, so it stays read-only."
                )
            } else {
                when (control.kind) {
                    ControlKind.STEPPER -> Stepper(control, current, onUpdate)
                    ControlKind.READONLY -> Text(
                        current ?: "—",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                    else -> control.choices.forEach { choice ->
                        ChoiceRow(
                            label = choice.label,
                            selected = choice.token.equals(current, ignoreCase = true),
                            onClick = { onUpdate(control, choice.token) }
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            control.note?.let { note ->
                Spacer(Modifier.height(12.dp))
                Notice(note)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Stored key: ${catalog.keyFor(control)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    letterSpacing = 0.2.sp
                ),
                color = Dim
            )
            if (exists) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Current value: ${current ?: "—"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    ),
                    color = Dim
                )
            }
        }
    }
}

private fun stateCased(kind: ControlKind): String = when (kind) {
    ControlKind.TOGGLE -> "TOGGLE"
    ControlKind.CHOICE -> "OPTIONS"
    ControlKind.STEPPER -> "VALUE"
    ControlKind.READONLY -> "READ ONLY"
}

@Composable
private fun FamilyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) Lime.copy(alpha = 0.07f) else BgElevated,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (selected) Lime.copy(alpha = 0.45f) else Line)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (selected) Lime else Dim)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.8.sp),
                color = if (selected) Ink else Muted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChoiceRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (selected) Lime.copy(alpha = 0.06f) else Color.Transparent,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, if (selected) Lime.copy(alpha = 0.35f) else LineSoft)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .border(1.dp, if (selected) Lime else Line, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Box(Modifier.size(6.dp).clip(CircleShape).background(Lime))
            }
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = if (selected) Ink else Muted)
        }
    }
}

@Composable
private fun Stepper(control: Control, current: String?, onUpdate: (Control, String) -> Unit) {
    val value = current?.trim()?.toIntOrNull() ?: control.min
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GhostButton("−", onClick = { onUpdate(control, (value - 1).coerceAtLeast(control.min).toString()) })
        Text(
            "$value${control.suffix}",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink,
            modifier = Modifier.width(90.dp)
        )
        GhostButton("+", onClick = { onUpdate(control, (value + 1).coerceAtMost(control.max).toString()) })
    }
}

@Composable
private fun Notice(text: String) {
    Surface(
        color = SurfaceHigh,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Amber.copy(alpha = 0.18f))
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 14.sp),
            color = Color(0xFFC7B99F),
            modifier = Modifier.padding(11.dp)
        )
    }
}
