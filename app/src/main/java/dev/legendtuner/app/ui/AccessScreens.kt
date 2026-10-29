package dev.legendtuner.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
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
import dev.legendtuner.app.access.GamePaths
import dev.legendtuner.app.ui.theme.Amber
import dev.legendtuner.app.ui.theme.AmberWash
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

@Composable
fun SetupScreen(
    state: AppState,
    wide: Boolean,
    onConnectRoot: () -> Unit,
    onConnectShizuku: () -> Unit,
    onRequestShizuku: () -> Unit,
    onOpenShizuku: () -> Unit,
    onPickManual: () -> Unit,
    onConnectDirect: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (wide) 52.dp else 22.dp, vertical = 26.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    "LEGEND TUNER",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Ink
                )
                Text(
                    "NATIVE CONFIG EDITOR",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                    color = Lime
                )
            }
            StatusChip(if (state.gameInstalled) "Game installed" else "Game not detected", lime = state.gameInstalled)
        }
        Spacer(Modifier.height(30.dp))
        Text(
            "LET'S FIND\nYOUR CONFIG.",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = if (wide) 64.sp else 48.sp),
            color = Ink
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Legend Tuner edits GRID Legends' own preferences file. On Android 11 and newer, normal apps cannot read another app's Android/data folder, so choose a privileged or manual route.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 17.sp),
            color = Muted,
            modifier = Modifier.widthIn(max = 720.dp)
        )
        Spacer(Modifier.height(18.dp))
        Surface(
            color = BgElevated,
            shape = RoundedCornerShape(11.dp),
            border = BorderStroke(1.dp, LineSoft),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "/Android/data/${GamePaths.GAME_PACKAGE}/files/feral_app_support/preferences",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    lineHeight = 13.sp
                ),
                color = Muted,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RootCard(state, onConnectRoot)
                    ShizukuCard(state, onConnectShizuku, onRequestShizuku, onOpenShizuku)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ManualCard(onPickManual)
                    DirectCard(state, onConnectDirect)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RootCard(state, onConnectRoot)
                ShizukuCard(state, onConnectShizuku, onRequestShizuku, onOpenShizuku)
                ManualCard(onPickManual)
                DirectCard(state, onConnectDirect)
            }
        }
        Spacer(Modifier.height(18.dp))
        Surface(
            color = AmberWash,
            shape = RoundedCornerShape(13.dp),
            border = BorderStroke(1.dp, Amber.copy(alpha = 0.17f))
        ) {
            Text(
                "Before you edit: fully close GRID Legends. The game rewrites preferences when it exits. If the game is installed on a microSD card, connect with Root or Shizuku so the app can scan every volume.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 14.sp),
                color = Color(0xFFC7B99F),
                modifier = Modifier.padding(13.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "NO INTERNET PERMISSION · NO ANALYTICS · UNOFFICIAL FAN TOOL",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp),
            color = Dim
        )
        Spacer(Modifier.height(26.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessSheet(
    state: AppState,
    onDismiss: () -> Unit,
    onConnectRoot: () -> Unit,
    onConnectShizuku: () -> Unit,
    onRequestShizuku: () -> Unit,
    onOpenShizuku: () -> Unit,
    onPickManual: () -> Unit,
    onConnectDirect: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        contentColor = Ink,
        dragHandle = null
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 26.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("ACCESS & CONNECTION", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp), color = Lime)
            Spacer(Modifier.height(5.dp))
            Text("Choose a route", style = MaterialTheme.typography.headlineLarge, color = Ink)
            Spacer(Modifier.height(6.dp))
            Text(
                "Changing access does not change your config. The next connection reloads from disk.",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
            Spacer(Modifier.height(16.dp))
            RootCard(state, onConnectRoot)
            Spacer(Modifier.height(10.dp))
            ShizukuCard(state, onConnectShizuku, onRequestShizuku, onOpenShizuku)
            Spacer(Modifier.height(10.dp))
            ManualCard(onPickManual)
            Spacer(Modifier.height(10.dp))
            DirectCard(state, onConnectDirect)
            Spacer(Modifier.height(16.dp))
            Text(
                "The app stores a local backup before every write. It has no network permission.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp, letterSpacing = 0.2.sp),
                color = Dim
            )
        }
        }
    }
}

@Composable
private fun RootCard(state: AppState, onConnect: () -> Unit) {
    AccessCard(
        index = "01",
        title = "Root",
        subtitle = if (state.rootDetected) "su detected · authorize once" else "No su binary detected",
        status = if (state.rootDetected) "AVAILABLE" else "NOT DETECTED",
        statusLime = state.rootDetected,
        icon = "⌘",
        onClick = onConnect,
        enabled = state.rootDetected && !state.connecting
    )
}

@Composable
private fun ShizukuCard(
    state: AppState,
    onConnect: () -> Unit,
    onRequest: () -> Unit,
    onOpenShizuku: () -> Unit
) {
    val status = when {
        !state.shizukuInstalled -> "NOT INSTALLED"
        !state.shizukuRunning -> "NOT RUNNING"
        !state.shizukuHasPermission -> "PERMISSION NEEDED"
        else -> "READY"
    }
    val subtitle = when {
        !state.shizukuInstalled -> "Install Shizuku to use the shell route"
        !state.shizukuRunning -> "Open Shizuku and start it with wireless debugging"
        !state.shizukuHasPermission -> "Grant Legend Tuner access inside Shizuku"
        else -> "Binder running · shell UID 2000"
    }
    val onClick: () -> Unit = when {
        !state.shizukuInstalled -> onOpenShizuku
        !state.shizukuRunning -> onOpenShizuku
        !state.shizukuHasPermission -> onRequest
        else -> onConnect
    }
    AccessCard(
        index = "02",
        title = "Shizuku",
        subtitle = subtitle,
        status = status,
        statusLime = status == "READY",
        icon = "⌁",
        onClick = onClick,
        enabled = !state.connecting
    )
}

@Composable
private fun ManualCard(onPick: () -> Unit) {
    AccessCard(
        index = "03",
        title = "Manual file",
        subtitle = "Pick a copy with the system picker; save writes back",
        status = "ALWAYS AVAILABLE",
        statusLime = false,
        icon = "▤",
        onClick = onPick,
        enabled = true
    )
}

@Composable
private fun DirectCard(state: AppState, onConnect: () -> Unit) {
    val legacy = android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q
    val available = state.directAvailable || legacy
    AccessCard(
        index = "04",
        title = "Direct file access",
        subtitle = when {
            state.directAvailable -> "The file is visible to the app"
            legacy -> "Legacy storage route for Android 10 and older"
            else -> "Blocked by Android 11+ scoped storage"
        },
        status = if (state.directAvailable) "AVAILABLE" else if (legacy) "LEGACY" else "UNAVAILABLE",
        statusLime = state.directAvailable,
        icon = "↗",
        onClick = onConnect,
        enabled = available
    )
}

@Composable
private fun AccessCard(
    index: String,
    title: String,
    subtitle: String,
    status: String,
    statusLime: Boolean,
    icon: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        color = if (enabled) BgElevated else Surface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (enabled) Line else LineSoft)
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (enabled) SurfaceHighest else SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, color = if (enabled) Lime else Dim, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(index, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = Dim)
                    Text(title, style = MaterialTheme.typography.titleMedium, color = if (enabled) Ink else Muted)
                }
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = Dim)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(
                        Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (statusLime) Lime else Dim)
                    )
                    Text(
                        status,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp, letterSpacing = 0.7.sp),
                        color = if (statusLime) Lime else Dim
                    )
                }
                Spacer(Modifier.height(7.dp))
                Text("›", color = if (enabled) Lime else Dim, fontSize = 19.sp)
            }
        }
    }
}
