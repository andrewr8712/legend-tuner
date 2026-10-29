package dev.legendtuner.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.legendtuner.app.access.AccessMode
import dev.legendtuner.app.access.AccessFactory
import dev.legendtuner.app.access.FileAccess
import dev.legendtuner.app.access.GamePaths
import dev.legendtuner.app.access.LocatedFile
import dev.legendtuner.app.access.RootAccess
import dev.legendtuner.app.access.RootShell
import dev.legendtuner.app.access.SafAccess
import dev.legendtuner.app.access.ShizukuAccess
import dev.legendtuner.app.access.ShizukuManager
import dev.legendtuner.app.data.BackupInfo
import dev.legendtuner.app.data.BackupStore
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.Preset
import dev.legendtuner.app.data.SettingDef
import dev.legendtuner.app.data.SettingScope
import dev.legendtuner.app.data.SettingType
import dev.legendtuner.app.data.SettingsCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

data class DisplayInfo(
    val widthPx: Int,
    val heightPx: Int,
    val refreshRate: Int
)

data class LoadedConfig(
    val document: ConfigDocument,
    val access: FileAccess,
    val file: LocatedFile,
    val originalBytes: ByteArray
)

data class UiMessage(
    val text: String,
    val error: Boolean = false,
    val id: Long = System.nanoTime()
)

data class AppState(
    val loading: Boolean = true,
    val connecting: Boolean = false,
    val saving: Boolean = false,
    val config: LoadedConfig? = null,
    val gameInstalled: Boolean = false,
    val selectedPreset: Preset = Preset.GRAPHICS,
    val selectedSettingKey: String? = null,
    val dirty: Boolean = false,
    val revision: Int = 0,
    val message: UiMessage? = null,
    val backups: List<BackupInfo> = emptyList(),
    val rawXml: String = "",
    val rawError: String? = null,
    val showAccessSheet: Boolean = false,
    val rootDetected: Boolean = false,
    val shizukuInstalled: Boolean = false,
    val shizukuRunning: Boolean = false,
    val shizukuHasPermission: Boolean = false,
    val directAvailable: Boolean = false,
    val displayInfo: DisplayInfo = DisplayInfo(0, 0, 0),
    val setupVisible: Boolean = true
) {
    val accessMode: AccessMode? get() = config?.access?.mode
    val forceStopAvailable: Boolean get() = accessMode == AccessMode.ROOT || accessMode == AccessMode.SHIZUKU
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val prefs = app.getSharedPreferences("legend_tuner", Context.MODE_PRIVATE)
    private val backupStore = BackupStore(app)
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var loaded: LoadedConfig? = null
    private var revision = 0

    init {
        _state.value = _state.value.copy(
            gameInstalled = GamePaths.isGameInstalled(app),
            shizukuInstalled = ShizukuManager.isInstalled(app),
            displayInfo = readDisplayInfo(),
            backups = backupStore.list()
        )
        viewModelScope.launch { refreshAccessOptions() }
        viewModelScope.launch { autoConnect() }
    }

    // ------------------------------------------------------------ access setup

    private suspend fun refreshAccessOptions() {
        val root = withContext(Dispatchers.IO) { RootShell.hasSuBinary() }
        val running = ShizukuManager.isBinderAlive()
        val granted = ShizukuManager.hasPermission()
        val direct = withContext(Dispatchers.IO) { AccessFactory.directPossible(app) }
        _state.value = _state.value.copy(
            rootDetected = root,
            shizukuRunning = running,
            shizukuHasPermission = granted,
            directAvailable = direct,
            shizukuInstalled = ShizukuManager.isInstalled(app),
            gameInstalled = GamePaths.isGameInstalled(app)
        )
    }

    private suspend fun autoConnect() {
        val savedMode = prefs.getString(KEY_MODE, null)
        if (savedMode != null) {
            when (savedMode) {
                AccessMode.MANUAL.name -> {
                    val uri = prefs.getString(KEY_MANUAL_URI, null)?.let(Uri::parse)
                    if (uri != null && importUri(uri, silent = true)) return
                }
                AccessMode.ROOT.name -> {
                    if (connectRootInternal(silent = true)) return
                }
                AccessMode.SHIZUKU.name -> {
                    if (connectShizukuInternal(silent = true)) return
                }
            }
        }

        // First launch / stale saved mode: try the least disruptive paths first.
        if (withContext(Dispatchers.IO) { RootShell.hasSuBinary() }) {
            if (connectRootInternal(silent = true)) return
        }
        val direct = withContext(Dispatchers.IO) { AccessFactory.tryDirect(app) }
        if (direct != null && loadWith(direct, silent = true)) return
        if (ShizukuManager.hasPermission()) {
            if (connectShizukuInternal(silent = true)) return
        }

        _state.value = _state.value.copy(loading = false, setupVisible = true)
    }

    fun connectRoot() {
        viewModelScope.launch {
            _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
            connectRootInternal(silent = false)
        }
    }

    private suspend fun connectRootInternal(silent: Boolean): Boolean {
        return try {
            val access = AccessFactory.tryRoot(app)
                ?: throw IllegalStateException("Root access was denied or su is unavailable.")
            val ok = loadWith(access, silent)
            if (!ok) {
                _state.value = _state.value.copy(
                    connecting = false,
                    setupVisible = true,
                    message = if (silent) null else UiMessage("Root is available, but the GRID Legends config file was not found.", true)
                )
            }
            ok
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                connecting = false,
                loading = false,
                setupVisible = true,
                message = if (silent) _state.value.message else UiMessage(e.message ?: "Root access failed.", true)
            )
            false
        }
    }

    fun connectShizuku() {
        viewModelScope.launch {
            _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
            connectShizukuInternal(silent = false)
        }
    }

    private suspend fun connectShizukuInternal(silent: Boolean): Boolean {
        return try {
            if (!ShizukuManager.isBinderAlive()) {
                throw IllegalStateException("Shizuku is not running. Open Shizuku, start it with wireless debugging, then try again.")
            }
            if (!ShizukuManager.hasPermission()) {
                throw SecurityException("Grant Legend Tuner permission in Shizuku first.")
            }
            val access = ShizukuAccess.connect(app)
            val ok = loadWith(access, silent)
            if (!ok) {
                _state.value = _state.value.copy(
                    connecting = false,
                    setupVisible = true,
                    message = if (silent) null else UiMessage("Shizuku is connected, but the GRID Legends config file was not found.", true)
                )
            }
            ok
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                connecting = false,
                loading = false,
                setupVisible = true,
                message = if (silent) _state.value.message else UiMessage(e.message ?: "Shizuku connection failed.", true)
            )
            false
        }
    }

    fun onShizukuPermissionResult(granted: Boolean) {
        if (granted) {
            connectShizuku()
        } else {
            _state.value = _state.value.copy(
                shizukuHasPermission = false,
                connecting = false,
                message = UiMessage("Shizuku permission was not granted.", true)
            )
        }
    }

    fun requestShizukuPermission(requestCode: Int) {
        if (!ShizukuManager.isBinderAlive()) {
            _state.value = _state.value.copy(
                message = UiMessage("Shizuku is not running. Open Shizuku first, then start it with wireless debugging.", true)
            )
            return
        }
        ShizukuManager.requestPermission(requestCode)
    }

    fun openShizuku() {
        if (ShizukuManager.isInstalled(app)) {
            ShizukuManager.openApp(app)
        } else {
            ShizukuManager.openDownloadPage(app)
        }
    }

    fun connectDirect() {
        viewModelScope.launch {
            _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
            val access = withContext(Dispatchers.IO) { AccessFactory.tryDirect(app) }
            if (access == null) {
                _state.value = _state.value.copy(
                    connecting = false,
                    loading = false,
                    setupVisible = true,
                    message = UiMessage(
                        "Direct access is not available. Android 11+ blocks Android/data for normal apps; use Root, Shizuku, or manual import.",
                        true
                    )
                )
            } else {
                loadWith(access, silent = false)
            }
        }
    }

    fun importManual(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
            if (importUri(uri, silent = false)) {
                _state.value = _state.value.copy(connecting = false)
            }
        }
    }

    private suspend fun importUri(uri: Uri, silent: Boolean): Boolean {
        return try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            runCatching { app.contentResolver.takePersistableUriPermission(uri, flags) }
                .onFailure { runCatching { app.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } }
            val access = SafAccess(app, uri)
            val ok = loadWith(access, silent)
            if (ok) prefs.edit().putString(KEY_MANUAL_URI, uri.toString()).apply()
            ok
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                connecting = false,
                loading = false,
                setupVisible = true,
                message = if (silent) _state.value.message else UiMessage(e.message ?: "Could not open that file.", true)
            )
            false
        }
    }

    private suspend fun loadWith(access: FileAccess, silent: Boolean): Boolean {
        val file = access.locate()
        if (file == null) {
            _state.value = _state.value.copy(loading = false, setupVisible = true)
            if (!silent) {
                _state.value = _state.value.copy(
                    message = UiMessage("The GRID Legends preferences file was not found. Launch the game once, or use manual mode.", true)
                )
            }
            return false
        }
        val bytes = access.read(file.path)
        val doc = ConfigDocument.parse(bytes)
        doc.validate()
        loaded = LoadedConfig(doc, access, file, bytes)
        prefs.edit()
            .putString(KEY_MODE, access.mode.name)
            .apply()
        val preset = doc.ensureActivePresetExists()
        revision++
        _state.value = _state.value.copy(
            loading = false,
            connecting = false,
            config = loaded,
            selectedPreset = preset,
            selectedSettingKey = null,
            dirty = false,
            revision = revision,
            setupVisible = false,
            showAccessSheet = false,
            message = if (silent) null else UiMessage("Connected using ${access.mode.label}.")
        )
        return true
    }

    fun refreshAccessStatus() {
        _state.value = _state.value.copy(displayInfo = readDisplayInfo())
        viewModelScope.launch { refreshAccessOptions() }
    }

    fun openAccessSheet() {
        _state.value = _state.value.copy(showAccessSheet = true)
        viewModelScope.launch { refreshAccessOptions() }
    }

    fun closeAccessSheet() {
        _state.value = _state.value.copy(showAccessSheet = false)
    }

    fun refreshAccess() {
        viewModelScope.launch {
            _state.value = _state.value.copy(connecting = true)
            try {
                refreshAccessOptions()
                val access = loaded?.access
                if (access != null) {
                    loadWith(access, silent = false)
                } else {
                    _state.value = _state.value.copy(connecting = false, loading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    connecting = false,
                    loading = false,
                    message = UiMessage("Could not reload: ${e.message}", true)
                )
            }
        }
    }

    // --------------------------------------------------------------- editing

    fun selectPreset(preset: Preset) {
        if (loaded == null) return
        _state.value = _state.value.copy(selectedPreset = preset, selectedSettingKey = null)
    }

    fun selectSetting(key: String?) {
        _state.value = _state.value.copy(selectedSettingKey = key)
    }

    fun updateSetting(def: SettingDef, value: Int) {
        val current = loaded ?: return
        val doc = current.document
        val clamped = when (def.type) {
            SettingType.PERCENT, SettingType.INTEGER -> value.coerceIn(def.min, def.max)
            else -> value
        }
        if (!SettingsCatalog.set(doc, _state.value.selectedPreset, def, clamped)) {
            _state.value = _state.value.copy(message = UiMessage("This setting is not present in the selected preset.", true))
            return
        }
        if (def.key == "FrameSizeMaximumPercentage" || def.key == "FrameSizeMinimumPercentage") {
            val max = doc.presetValue(_state.value.selectedPreset, "FrameSizeMaximumPercentage")?.firstIntOrNull
            val min = doc.presetValue(_state.value.selectedPreset, "FrameSizeMinimumPercentage")?.firstIntOrNull
            if (max != null && min != null && min > max) {
                if (def.key == "FrameSizeMaximumPercentage") doc.setPresetInteger(_state.value.selectedPreset, "FrameSizeMinimumPercentage", max)
                else doc.setPresetInteger(_state.value.selectedPreset, "FrameSizeMaximumPercentage", min)
            }
        }
        revision++
        _state.value = _state.value.copy(
            config = loaded,
            dirty = true,
            revision = revision,
            selectedSettingKey = def.key
        )
    }

    fun discardChanges() {
        val current = loaded ?: return
        runCatching {
            val doc = ConfigDocument.parse(current.originalBytes)
            doc.validate()
            loaded = current.copy(document = doc)
            revision++
            _state.value = _state.value.copy(
                config = loaded,
                selectedPreset = doc.ensureActivePresetExists(),
                selectedSettingKey = null,
                dirty = false,
                revision = revision,
                message = UiMessage("Changes discarded.")
            )
        }.onFailure { _state.value = _state.value.copy(message = UiMessage("Could not reload the original file.", true)) }
    }

    // ------------------------------------------------------------------ save

    fun save(applyPreset: Boolean = true) {
        val current = loaded ?: return
        if (_state.value.saving) return
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true)
            var backup: BackupInfo? = null
            try {
                val doc = current.document
                if (applyPreset) doc.applyPresetToLive(_state.value.selectedPreset)
                doc.validate()
                val bytes = doc.serialize()

                backup = withContext(Dispatchers.IO) {
                    backupStore.save(current.originalBytes, _state.value.selectedPreset.keyName)
                }

                withContext(Dispatchers.IO) { current.access.write(current.file.path, bytes) }

                // Verify the write by reading it back and parsing it.
                val verifiedBytes = withContext(Dispatchers.IO) { current.access.read(current.file.path) }
                val verified = ConfigDocument.parse(verifiedBytes)
                verified.validate()
                if (verified.activePreset() != _state.value.selectedPreset) {
                    error("Verification failed: active preset did not persist")
                }

                val newFile = withContext(Dispatchers.IO) { current.access.locate() } ?: current.file
                loaded = LoadedConfig(verified, current.access, newFile, verifiedBytes)
                revision++
                _state.value = _state.value.copy(
                    config = loaded,
                    saving = false,
                    dirty = false,
                    revision = revision,
                    backups = withContext(Dispatchers.IO) { backupStore.list() },
                    message = UiMessage("Saved. Launch GRID Legends to apply the preset.")
                )
            } catch (e: Exception) {
                var restored = false
                if (backup != null) {
                    restored = runCatching {
                        withContext(Dispatchers.IO) { current.access.write(current.file.path, current.originalBytes) }
                    }.isSuccess
                }
                _state.value = _state.value.copy(
                    saving = false,
                    message = UiMessage(
                        buildString {
                            append("Save failed: ")
                            append(e.message ?: "unknown error")
                            if (backup != null) {
                                append(if (restored) ". The original was restored." else ". A backup is available in Backups.")
                            }
                        },
                        true
                    )
                )
            }
        }
    }

    fun forceStopGame() {
        val current = loaded ?: return
        viewModelScope.launch {
            val ok = runCatching { withContext(Dispatchers.IO) { current.access.forceStop() } }.getOrDefault(false)
            _state.value = _state.value.copy(
                message = if (ok) UiMessage("GRID Legends was force-stopped.") else UiMessage(
                    "Force stop is only available with Root or Shizuku. Close the game manually.", true
                )
            )
        }
    }

    // ----------------------------------------------------------------- raw xml

    fun openRawXml() {
        val current = loaded ?: return
        _state.value = _state.value.copy(
            rawXml = current.document.serialize().toString(StandardCharsets.UTF_8),
            rawError = null
        )
    }

    fun updateRawXml(text: String) {
        _state.value = _state.value.copy(rawXml = text, rawError = null)
    }

    fun applyRawXml(): Boolean {
        val current = loaded ?: return false
        return try {
            val doc = ConfigDocument.parse(_state.value.rawXml)
            doc.validate()
            loaded = current.copy(document = doc)
            revision++
            _state.value = _state.value.copy(
                config = loaded,
                selectedPreset = doc.ensureActivePresetExists(),
                selectedSettingKey = null,
                dirty = true,
                revision = revision,
                rawError = null,
                message = UiMessage("Raw XML applied to the draft. Use Save to write it.")
            )
            true
        } catch (e: Exception) {
            _state.value = _state.value.copy(rawError = e.message ?: "Invalid XML", message = UiMessage("XML error: ${e.message}", true))
            false
        }
    }

    // ---------------------------------------------------------------- backups

    fun refreshBackups() {
        _state.value = _state.value.copy(backups = backupStore.list())
    }

    fun restoreBackup(name: String) {
        val current = loaded ?: return
        viewModelScope.launch {
            try {
                val backupBytes = withContext(Dispatchers.IO) { backupStore.read(name) }
                val doc = ConfigDocument.parse(backupBytes)
                doc.validate()
                withContext(Dispatchers.IO) {
                    backupStore.save(current.originalBytes, "before-restore")
                    current.access.write(current.file.path, backupBytes)
                }
                val verifiedBytes = withContext(Dispatchers.IO) { current.access.read(current.file.path) }
                val verified = ConfigDocument.parse(verifiedBytes)
                verified.validate()
                loaded = current.copy(document = verified, originalBytes = verifiedBytes)
                revision++
                _state.value = _state.value.copy(
                    config = loaded,
                    selectedPreset = verified.ensureActivePresetExists(),
                    dirty = false,
                    revision = revision,
                    backups = backupStore.list(),
                    message = UiMessage("Backup restored.")
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(message = UiMessage("Restore failed: ${e.message}", true))
            }
        }
    }

    fun deleteBackup(name: String) {
        backupStore.delete(name)
        _state.value = _state.value.copy(backups = backupStore.list(), message = UiMessage("Backup deleted."))
    }

    fun exportCopy(uri: Uri) {
        val current = loaded ?: return
        viewModelScope.launch {
            try {
                val bytes = current.document.serialize()
                withContext(Dispatchers.IO) {
                    app.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
                        ?: error("Could not open the export destination")
                }
                _state.value = _state.value.copy(message = UiMessage("Copy exported."))
            } catch (e: Exception) {
                _state.value = _state.value.copy(message = UiMessage("Export failed: ${e.message}", true))
            }
        }
    }

    // ------------------------------------------------------------------ misc

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun showMessage(text: String, error: Boolean = false) {
        _state.value = _state.value.copy(message = UiMessage(text, error))
    }

    fun clearRawError() {
        _state.value = _state.value.copy(rawError = null)
    }

    fun hasStoredManualUri(): Boolean = prefs.getString(KEY_MANUAL_URI, null) != null

    private fun readDisplayInfo(): DisplayInfo {
        return runCatching {
            val wm = app.getSystemService(WindowManager::class.java)
            @Suppress("DEPRECATION")
            val display = wm.defaultDisplay
            @Suppress("DEPRECATION")
            val metrics = android.util.DisplayMetrics().also { display.getRealMetrics(it) }
            DisplayInfo(
                widthPx = metrics.widthPixels,
                heightPx = metrics.heightPixels,
                refreshRate = display.refreshRate.toInt().coerceAtLeast(60)
            )
        }.getOrElse {
            val metrics = app.resources.displayMetrics
            DisplayInfo(metrics.widthPixels, metrics.heightPixels, 60)
        }
    }

    companion object {
        private const val KEY_MODE = "access_mode"
        private const val KEY_MANUAL_URI = "manual_uri"
    }
}
