package dev.legendtuner.app.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.data.BackupInfo
import dev.legendtuner.app.ui.theme.BgElevated
import dev.legendtuner.app.ui.theme.Dim
import dev.legendtuner.app.ui.theme.Ink
import dev.legendtuner.app.ui.theme.Lime
import dev.legendtuner.app.ui.theme.Line
import dev.legendtuner.app.ui.theme.Muted
import dev.legendtuner.app.ui.theme.Surface
import dev.legendtuner.app.ui.theme.SurfaceHighest

@Composable
fun BackupsScreen(
    state: AppState,
    onRestore: (String) -> Unit,
    onDelete: (String) -> Unit,
    onExport: () -> Unit,
    onOpenAccess: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<BackupInfo?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        PageHeader(
            crumb = "Legend Tuner / Backups",
            title = "Backups",
            subtitle = "Automatic snapshots stored inside the app",
            mode = state.accessMode,
            onSettings = onOpenAccess,
            actions = {
                GhostButton("Export copy", onExport, icon = LegendIcons.Download)
            }
        )
        Spacer(Modifier.height(16.dp))
        Surface(
            color = Lime.copy(alpha = 0.045f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Lime.copy(alpha = 0.16f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "A backup is written before every save, restore and manual write. The 12 most recent snapshots are kept. Restoring also backs up the current file first.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 15.sp),
                color = Muted,
                modifier = Modifier.padding(13.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        if (state.backups.isEmpty()) {
            Panel(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No backups yet", style = MaterialTheme.typography.headlineMedium, color = Ink)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "The first backup appears when you save or restore a configuration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(state.backups, key = { it.name }) { backup ->
                    BackupRow(backup, onRestore) { pendingDelete = it }
                }
                item { Spacer(Modifier.height(18.dp)) }
            }
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = Surface,
            titleContentColor = Ink,
            textContentColor = Muted,
            title = { Text("Delete backup?") },
            text = { Text("This removes the snapshot from the app. It does not touch the game file.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete?.let { onDelete(it.name) }
                    pendingDelete = null
                }) { Text("Delete", color = dev.legendtuner.app.ui.theme.Red) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel", color = Muted) }
            }
        )
    }
}

@Composable
private fun BackupRow(
    backup: BackupInfo,
    onRestore: (String) -> Unit,
    onDelete: (BackupInfo) -> Unit
) {
    Panel(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .padding(0.dp)
                    .then(Modifier),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = SurfaceHighest,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("↺", color = Lime, fontSize = 18.sp)
                    }
                }
            }
            Spacer(Modifier.size(11.dp))
            Column(Modifier.weight(1f)) {
                Text(backup.displayTime(), style = MaterialTheme.typography.titleSmall, color = Ink)
                Spacer(Modifier.height(3.dp))
                Text(
                    "${backup.displaySize()} · ${backup.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Dim,
                    maxLines = 1
                )
            }
            GhostButton("Restore", { onRestore(backup.name) }, icon = LegendIcons.Restore)
            Spacer(Modifier.size(6.dp))
            Surface(
                onClick = { onDelete(backup) },
                color = BgElevated,
                shape = RoundedCornerShape(9.dp),
                border = BorderStroke(1.dp, Line)
            ) {
                Icon(
                    LegendIcons.Delete,
                    contentDescription = "Delete backup",
                    tint = Dim,
                    modifier = Modifier.padding(9.dp).size(15.dp)
                )
            }
        }
    }
}
