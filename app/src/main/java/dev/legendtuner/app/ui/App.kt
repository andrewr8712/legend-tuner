package dev.legendtuner.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.legendtuner.app.AppViewModel
import dev.legendtuner.app.data.SettingGroup
import dev.legendtuner.app.data.SettingsCatalog
import dev.legendtuner.app.ui.theme.Ink
import dev.legendtuner.app.ui.theme.Lime
import dev.legendtuner.app.ui.theme.Muted
import dev.legendtuner.app.ui.theme.Red

@Composable
fun LegendTunerApp(
    viewModel: AppViewModel,
    onRequestShizuku: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    var destination by rememberSaveable { mutableStateOf(AppDestination.DASHBOARD) }

    val openLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        viewModel.importManual(uri)
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        viewModel.exportCopy(uri)
    }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) viewModel.connectDirect()
    }

    val pickManual: () -> Unit = { openLauncher.launch(DocumentIntents.openPreferences()) }
    val exportCopy: () -> Unit = { exportLauncher.launch(DocumentIntents.createExport()) }
    val connectDirect: () -> Unit = {
        val needsPermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            storagePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            viewModel.connectDirect()
        }
    }

    val selectDestination: (AppDestination) -> Unit = { next ->
        when (next) {
            AppDestination.RAW -> viewModel.openRawXml()
            AppDestination.BACKUPS -> viewModel.refreshBackups()
            else -> Unit
        }
        destination = next
    }

    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    LaunchedEffect(state.message?.id) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = message.text,
            duration = if (message.error) SnackbarDuration.Long else SnackbarDuration.Short
        )
        viewModel.clearMessage()
    }

    LegendBackground {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when {
                        event.isCtrlPressed && event.key == Key.S -> {
                            if (destination == AppDestination.RAW) {
                                if (viewModel.applyRawXml()) viewModel.save(applyPreset = false)
                            } else {
                                viewModel.save()
                            }
                            true
                        }
                        event.key == Key.Escape -> {
                            viewModel.selectSetting(null)
                            true
                        }
                        else -> false
                    }
                }
        ) {
            val compact = maxWidth < 600.dp
            val twoColumns = maxWidth >= 840.dp
            val dockedInspector = maxWidth >= 1000.dp
            val setupWide = maxWidth >= 900.dp

            if (state.loading) {
                Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
                    LoadingScreen()
                }
            } else if (state.config == null) {
                Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
                    SetupScreen(
                        state = state,
                        wide = setupWide,
                        onConnectRoot = viewModel::connectRoot,
                        onConnectShizuku = viewModel::connectShizuku,
                        onRequestShizuku = onRequestShizuku,
                        onOpenShizuku = viewModel::openShizuku,
                        onPickManual = pickManual,
                        onConnectDirect = connectDirect
                    )
                }
            } else {
                Row(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    if (!compact) {
                        NavRail(
                            selected = destination,
                            mode = state.accessMode,
                            onSelect = selectDestination
                        )
                    }
                    Column(Modifier.weight(1f).fillMaxSize()) {
                        Box(Modifier.weight(1f).fillMaxSize()) {
                            when (destination) {
                                AppDestination.DASHBOARD -> DashboardScreen(
                                    state = state,
                                    wide = twoColumns,
                                    onEdit = {
                                        viewModel.selectSetting(null)
                                        destination = AppDestination.EDITOR
                                    },
                                    onBackups = { selectDestination(AppDestination.BACKUPS) },
                                    onRaw = { selectDestination(AppDestination.RAW) },
                                    onOpenAccess = viewModel::openAccessSheet,
                                    onQuickTune = { group ->
                                        viewModel.selectSetting(SettingsCatalog.forGroup(group).firstOrNull()?.key)
                                        destination = AppDestination.EDITOR
                                    }
                                )
                                AppDestination.EDITOR -> EditorScreen(
                                    state = state,
                                    dockedInspector = dockedInspector,
                                    twoColumns = twoColumns,
                                    onSelectPreset = viewModel::selectPreset,
                                    onSelectSetting = viewModel::selectSetting,
                                    onUpdate = viewModel::updateSetting,
                                    onSave = { viewModel.save() },
                                    onDiscard = viewModel::discardChanges,
                                    onOpenAccess = viewModel::openAccessSheet,
                                    onForceStop = viewModel::forceStopGame,
                                    onReload = viewModel::refreshAccess
                                )
                                AppDestination.BACKUPS -> BackupsScreen(
                                    state = state,
                                    onRestore = viewModel::restoreBackup,
                                    onDelete = viewModel::deleteBackup,
                                    onExport = exportCopy,
                                    onOpenAccess = viewModel::openAccessSheet
                                )
                                AppDestination.RAW -> RawXmlScreen(
                                    state = state,
                                    onTextChange = viewModel::updateRawXml,
                                    onApply = { viewModel.applyRawXml() },
                                    onSave = {
                                        if (viewModel.applyRawXml()) viewModel.save(applyPreset = false)
                                    },
                                    onReload = {
                                        viewModel.openRawXml()
                                        viewModel.refreshAccess()
                                    },
                                    onOpenAccess = viewModel::openAccessSheet
                                )
                            }
                        }
                        if (compact) {
                            CompactNav(selected = destination, onSelect = selectDestination)
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = dev.legendtuner.app.ui.theme.SurfaceHigh,
                    contentColor = Ink,
                    actionColor = Lime
                )
            }

            if (state.showAccessSheet) {
                AccessSheet(
                    state = state,
                    onDismiss = viewModel::closeAccessSheet,
                    onConnectRoot = viewModel::connectRoot,
                    onConnectShizuku = viewModel::connectShizuku,
                    onRequestShizuku = onRequestShizuku,
                    onOpenShizuku = viewModel::openShizuku,
                    onPickManual = pickManual,
                    onConnectDirect = connectDirect
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LEGEND TUNER", style = MaterialTheme.typography.headlineLarge, color = Ink)
            Text(
                "READING CONFIG",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                color = Muted
            )
            Spacer(Modifier.height(18.dp))
            CircularProgressIndicator(color = Lime, strokeWidth = 2.dp)
        }
    }
}
