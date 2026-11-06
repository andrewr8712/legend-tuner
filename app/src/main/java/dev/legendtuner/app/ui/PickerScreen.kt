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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.TitleStatus
import dev.legendtuner.app.data.FeralTitle
import dev.legendtuner.app.ui.theme.Amber
import dev.legendtuner.app.ui.theme.BgElevated
import dev.legendtuner.app.ui.theme.Dim
import dev.legendtuner.app.ui.theme.Ink
import dev.legendtuner.app.ui.theme.Lime
import dev.legendtuner.app.ui.theme.Line
import dev.legendtuner.app.ui.theme.Muted
import dev.legendtuner.app.ui.theme.Surface as SurfaceColor
import dev.legendtuner.app.ui.theme.SurfaceHigh

@Composable
fun PickerScreen(
    state: AppState,
    wide: Boolean,
    onOpenTitle: (FeralTitle) -> Unit,
    onOpenMissing: (FeralTitle) -> Unit,
    onOpenFile: () -> Unit,
    onOpenAccess: () -> Unit,
    onRefresh: () -> Unit
) {
    val installed = state.titleStatuses.filter { it.installed }
    // Compact cards need more horizontal room; a phone reads better as one full-width row.
    val columns = if (wide) 3 else 1

    Column(
        Modifier
            .fillMaxSize()
            .testTag("picker")
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (wide) 28.dp else 18.dp, vertical = 20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    "CONFIG TUNER",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 40.sp, letterSpacing = 0.5.sp),
                    color = Ink
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Feral runtime settings · read, verify, write",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    state.accessMode?.label ?: "No access",
                    lime = state.accessMode != null
                )
                GhostButton("Access", onOpenAccess)
            }
        }

        Spacer(Modifier.height(26.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "DETECTED TITLES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp),
                color = Muted
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${installed.size} installed",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.6.sp),
                color = Dim
            )
            Spacer(Modifier.width(10.dp))
            GhostButton("Refresh", onRefresh)
        }
        Spacer(Modifier.height(12.dp))

        if (state.titleStatuses.isEmpty()) {
            Panel(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = Lime, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Looking for installed Feral titles…", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            }
        } else if (installed.isEmpty()) {
            Panel(Modifier.fillMaxWidth().testTag("empty-state")) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("No supported titles found", style = MaterialTheme.typography.headlineSmall, color = Ink)
                    Text(
                        "Install a supported Feral game and launch it once, or open a preferences file you already have.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                    Spacer(Modifier.height(4.dp))
                    GhostButton("Open a preferences file", onOpenFile, modifier = Modifier.testTag("empty-open-file"))
                }
            }
        } else {
            installed.chunked(columns).forEach { rowItems ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { status ->
                        TitleCard(status, Modifier.weight(1f)) { onOpenTitle(status.title) }
                    }
                    repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        val missing = state.titleStatuses.filter { !it.installed }
        if (missing.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text(
                "ALSO SUPPORTED",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp),
                color = Muted
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BgElevated,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line)
            ) {
                Column {
                    missing.forEachIndexed { index, status ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .testTag("missing-${status.title.id}")
                                .clickable { onOpenMissing(status.title) }
                                .semantics {
                                    contentDescription = "Open saved ${status.title.label} preferences file"
                                    role = Role.Button
                                }
                                .padding(horizontal = 15.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .border(1.dp, Line, RoundedCornerShape(7.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(status.title.monogram, color = Dim, fontSize = 10.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(status.title.label, style = MaterialTheme.typography.bodyMedium, color = Muted)
                                Text(
                                    "Not installed · tap to open a saved file",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                    color = Dim
                                )
                            }
                            StatusChip("Not installed", lime = false)
                        }
                        if (index != missing.lastIndex) DividerLine(Modifier.padding(start = 53.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Surface(
            onClick = onOpenFile,
            modifier = Modifier.fillMaxWidth().testTag("open-file"),
            color = BgElevated,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Line)
        ) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .border(1.dp, Line, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Ink, fontSize = 15.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text("Open a preferences file…", style = MaterialTheme.typography.bodyMedium, color = Muted)
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Local only · no network permission · every write is backed up and verified",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
            color = Dim
        )
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun TitleCard(status: TitleStatus, modifier: Modifier, onClick: () -> Unit) {
    val ready = status.readable
    val title = status.title
    val detail = when {
        title.titleCatalog != null -> "${title.titleCatalog.controls.size} controls"
        title.legacyGuided -> "Guided + raw XML"
        else -> "Raw XML"
    }

    Surface(
        onClick = onClick,
        modifier = modifier.testTag("title-${title.id}").semantics(mergeDescendants = true) {
            contentDescription = buildString {
                append(title.label)
                append(if (ready) ", ready. " else ", installed, setup needed. ")
                append(detail)
            }
            role = Role.Button
        },
        color = SurfaceColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (ready) Lime.copy(alpha = 0.22f) else Line)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .border(1.dp, if (ready) Lime else Line, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title.monogram,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
                    color = if (ready) Lime else Muted
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title.label,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(if (ready) "Ready" else "Installed", lime = ready)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        detail,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.4.sp),
                        color = Dim,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            Text("›", color = Dim, fontSize = 17.sp)
        }
    }
}

@Composable
fun GenericDashboardScreen(
    state: AppState,
    wide: Boolean,
    onEdit: () -> Unit,
    onRaw: () -> Unit,
    onBackups: () -> Unit,
    onOpenAccess: () -> Unit,
    onChooseGame: () -> Unit
) {
    val config = state.config ?: return
    val doc = config.document
    val catalog = state.activeTitle?.titleCatalog
    val version = doc.value(state.activeTitle?.registryRoot?.dropLast(1) ?: emptyList(), "GameVersionString")?.text
        ?: doc.value(state.activeTitle?.registryRoot?.dropLast(1) ?: emptyList(), "GameInstallVersionString")?.text
        ?: state.activeLabel
    val values = doc.allValues().size

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (wide) 26.dp else 18.dp)
    ) {
        PageHeader(
            crumb = "Config Tuner / ${state.activeLabel}",
            title = state.activeLabel,
            subtitle = config.file.displayPath,
            mode = state.accessMode,
            onSettings = onOpenAccess,
            actions = { StatusChip("Registry file") }
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Panel(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("GAME", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp), color = Lime)
                        Spacer(Modifier.height(8.dp))
                        Text(version, style = MaterialTheme.typography.headlineMedium, color = Ink, maxLines = 2)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "$values values · ${formatSize(config.file.size)} · ${config.file.volumeLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            if (catalog != null) {
                                "${catalog.controls.size} settings are editable with plain-language controls. Raw XML stays available for anything the catalogue does not cover."
                            } else {
                                "The guided editor for this title is not built yet. Browse the whole registry in Raw XML; writes are backed up and verified like GRID Legends."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                            color = Muted
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (catalog != null) {
                        ActionTile("Edit settings", "${catalog.controls.size} controls", LegendIcons.Tune, Modifier.weight(1f), onEdit)
                    }
                    ActionTile("Raw XML", "$values values", LegendIcons.Code, Modifier.weight(1f), onRaw)
                    ActionTile("Backups", "${state.backups.size} snapshots", LegendIcons.Backups, Modifier.weight(1f), onBackups)
                }
            }
            Column(Modifier.weight(0.75f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Panel(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("FILE", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp), color = Dim)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            config.file.displayPath,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 13.sp),
                            color = Muted
                        )
                    }
                }
                GhostButton("Choose another game", onChooseGame, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(16.dp))
        Surface(
            color = dev.legendtuner.app.ui.theme.AmberWash,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Amber.copy(alpha = 0.18f))
        ) {
            Row(Modifier.padding(13.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                androidx.compose.material3.Icon(
                    LegendIcons.Warning, null, tint = Amber, modifier = Modifier.size(15.dp)
                )
                Text(
                    "Close the game before saving. ${state.activeLabel} can rewrite this file when it exits, which may undo your changes.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 14.sp),
                    color = Color(0xFFC7B99F)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}
