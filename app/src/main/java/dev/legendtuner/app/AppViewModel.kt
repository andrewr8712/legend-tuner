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
import dev.legendtuner.app.access.FileReplaceException
import dev.legendtuner.app.access.GamePaths
import dev.legendtuner.app.access.LocatedFile
import dev.legendtuner.app.access.RootAccess
import dev.legendtuner.app.access.RootShell
import dev.legendtuner.app.access.SafAccess
import dev.legendtuner.app.access.ShizukuAccess
import dev.legendtuner.app.access.ShizukuManager
import dev.legendtuner.app.access.replaceWithRollback
import dev.legendtuner.app.data.BackupInfo
import dev.legendtuner.app.data.BackupStore
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.Control
import dev.legendtuner.app.data.ControlKind
import dev.legendtuner.app.data.FeralTitle
import dev.legendtuner.app.data.FeralTitles
import dev.legendtuner.app.data.Preset
import dev.legendtuner.app.data.TitleCatalogEngine
import dev.legendtuner.app.data.SettingDef
import dev.legendtuner.app.data.SettingScope
import dev.legendtuner.app.data.SettingType
import dev.legendtuner.app.data.SettingsCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

data class TitleStatus(
    val title: FeralTitle,
    val installed: Boolean,
    val readable: Boolean
)

data class AppState(
    val loading: Boolean = true,
    val connecting: Boolean = false,
    val saving: Boolean = false,
    val restoring: Boolean = false,
    val config: LoadedConfig? = null,
    val titleStatuses: List<TitleStatus> = emptyList(),
    val activeTitle: FeralTitle? = null,
    val activeFamilyId: String? = null,
    val manualFile: Boolean = false,
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
    val guided: Boolean get() = activeTitle?.guided == true
    val activeLabel: String get() = activeTitle?.label ?: "Manual file"
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val prefs = app.getSharedPreferences("legend_tuner", Context.MODE_PRIVATE)
    private val backupStore = BackupStore(app)
    private val operationMutex = Mutex()
    private val configLoader = ConfigFileLoader()
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var loaded: LoadedConfig? = null
    private var revision = 0

    init {
        _state.value = _state.value.copy(
            gameInstalled = GamePaths.isInstalled(app, FeralTitles.GRID_LEGENDS.packageName),
            shizukuInstalled = ShizukuManager.isInstalled(app),
            displayInfo = readDisplayInfo()
        )
        viewModelScope.launch {
            refreshAccessOptions()
            refreshTitleStatuses()
            _state.value = _state.value.copy(loading = false, setupVisible = false)
        }
    }

    // ------------------------------------------------------------ title picker

    fun refreshTitleStatuses() {
        viewModelScope.launch {
            val statuses = withContext(Dispatchers.IO) {
                FeralTitles.ALL.map { title ->
                    val installed = GamePaths.isInstalled(app, title.packageName)
                    TitleStatus(
                        title = title,
                        installed = installed,
                        readable = installed && GamePaths.isReadableDirectly(app, title.packageName)
                    )
                }
            }
            _state.value = _state.value.copy(titleStatuses = statuses)
        }
    }

    fun openTitle(title: FeralTitle) {
        if (_state.value.loading) return
        if (rejectIfBusy("Opening ${title.label}")) return
        _state.value = _state.value.copy(
            loading = true,
            activeTitle = title,
            activeFamilyId = null,
            manualFile = false,
            message = null,
            setupVisible = false,
            showAccessSheet = false,
            restoring = false,
            backups = emptyList()
        )
        viewModelScope.launch {
            var lastError: String? = null
            try {
                operationMutex.withLock {
                    val saved = prefs.getString(KEY_MODE, null)
                    val order = buildList {
                        if (saved != null) add(saved)
                        add(AccessMode.ROOT.name)
                        add(AccessMode.DIRECT.name)
                        add(AccessMode.SHIZUKU.name)
                    }.distinct()

                    for (mode in order) {
                        val access = try {
                            when (mode) {
                                AccessMode.ROOT.name -> AccessFactory.tryRoot(app, title.packageName)
                                AccessMode.SHIZUKU.name -> ShizukuAccess.connect(app, title.packageName)
                                AccessMode.DIRECT.name -> AccessFactory.tryDirect(app, title.packageName)
                                else -> null
                            }
                        } catch (e: Exception) {
                            lastError = e.message ?: "access failed"
                            null
                        } ?: continue
                        if (loadWith(access, silent = true)) return@withLock
                    }

                    val error = _state.value.message
                        ?: lastError?.let { UiMessage("Could not open ${title.label}: $it", true) }
                    _state.value = _state.value.copy(
                        loading = false,
                        connecting = false,
                        setupVisible = true,
                        message = error
                    )
                }
            } catch (e: Exception) {
                failLoad("Could not open ${title.label}: ${e.message ?: "unknown error"}")
            } finally {
                if (_state.value.loading) {
                    _state.value = _state.value.copy(loading = false, connecting = false, setupVisible = true)
                }
            }
        }
    }

    fun openMissingTitle(title: FeralTitle) {
        if (rejectIfBusy("Opening ${title.label}")) return
        _state.value = _state.value.copy(
            loading = false,
            activeTitle = title,
            activeFamilyId = null,
            manualFile = true,
            setupVisible = true,
            showAccessSheet = false,
            message = null,
            restoring = false,
            backups = emptyList()
        )
    }

    fun backToPicker() {
        if (rejectIfBusy("Returning to the title picker")) return
        loaded = null
        _state.value = _state.value.copy(
            config = null,
            activeTitle = null,
            activeFamilyId = null,
            manualFile = false,
            loading = false,
            saving = false,
            restoring = false,
            connecting = false,
            dirty = false,
            rawXml = "",
            rawError = null,
            selectedSettingKey = null,
            showAccessSheet = false,
            setupVisible = false,
            backups = emptyList()
        )
        refreshTitleStatuses()
    }

    // ------------------------------------------------------------ access setup

    private suspend fun refreshAccessOptions() {
        val root = withContext(Dispatchers.IO) { RootShell.hasSuBinary() }
        val running = ShizukuManager.isBinderAlive()
        val granted = ShizukuManager.hasPermission()
        val direct = withContext(Dispatchers.IO) {
            _state.value.activeTitle?.let { AccessFactory.directPossible(app, it.packageName) } ?: false
        }
        _state.value = _state.value.copy(
            rootDetected = root,
            shizukuRunning = running,
            shizukuHasPermission = granted,
            directAvailable = direct,
            shizukuInstalled = ShizukuManager.isInstalled(app),
            gameInstalled = GamePaths.isInstalled(app, FeralTitles.GRID_LEGENDS.packageName)
        )
    }

    fun connectRoot() {
        val title = _state.value.activeTitle ?: return
        if (rejectIfBusy("Root connection")) return
        _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
        viewModelScope.launch {
            try {
                operationMutex.withLock { connectRootInternal(title, silent = false) }
            } catch (e: Exception) {
                failLoad(e.message ?: "Root access failed.")
            } finally {
                if (_state.value.connecting) {
                    _state.value = _state.value.copy(connecting = false)
                }
            }
        }
    }

    private suspend fun connectRootInternal(title: FeralTitle, silent: Boolean): Boolean {
        return try {
            val access = AccessFactory.tryRoot(app, title.packageName)
                ?: throw IllegalStateException("Root access was denied or su is unavailable.")
            val ok = loadWith(access, silent)
            if (!ok) {
                _state.value = _state.value.copy(
                    connecting = false,
                    setupVisible = true,
                    message = _state.value.message
                        ?: if (silent) null
                        else UiMessage("Root is available, but the ${title.label} config could not be loaded.", true)
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
        val title = _state.value.activeTitle ?: return
        if (rejectIfBusy("Shizuku connection")) return
        _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
        viewModelScope.launch {
            try {
                operationMutex.withLock { connectShizukuInternal(title, silent = false) }
            } catch (e: Exception) {
                failLoad(e.message ?: "Shizuku connection failed.")
            } finally {
                if (_state.value.connecting) {
                    _state.value = _state.value.copy(connecting = false)
                }
            }
        }
    }

    private suspend fun connectShizukuInternal(title: FeralTitle, silent: Boolean): Boolean {
        return try {
            if (!ShizukuManager.isBinderAlive()) {
                throw IllegalStateException("Shizuku is not running. Open Shizuku, start it with wireless debugging, then try again.")
            }
            if (!ShizukuManager.hasPermission()) {
                throw SecurityException("Grant Legend Tuner permission in Shizuku first.")
            }
            val access = ShizukuAccess.connect(app, title.packageName)
            val ok = loadWith(access, silent)
            if (!ok) {
                _state.value = _state.value.copy(
                    connecting = false,
                    setupVisible = true,
                    message = _state.value.message
                        ?: if (silent) null
                        else UiMessage("Shizuku is connected, but the ${title.label} config could not be loaded.", true)
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
        val title = _state.value.activeTitle ?: return
        if (rejectIfBusy("Direct connection")) return
        _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
        viewModelScope.launch {
            try {
                operationMutex.withLock {
                    val access = withContext(Dispatchers.IO) { AccessFactory.tryDirect(app, title.packageName) }
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
                        if (!loadWith(access, silent = false)) {
                            _state.value = _state.value.copy(connecting = false, setupVisible = true)
                        }
                    }
                }
            } catch (e: Exception) {
                failLoad(e.message ?: "Direct access failed.")
            } finally {
                if (_state.value.connecting) {
                    _state.value = _state.value.copy(connecting = false)
                }
            }
        }
    }

    fun importManual(uri: Uri) {
        if (rejectIfBusy("Importing a file")) return
        _state.value = _state.value.copy(connecting = true, showAccessSheet = false)
        viewModelScope.launch {
            try {
                operationMutex.withLock { importUri(uri, silent = false) }
            } catch (e: Exception) {
                failLoad(e.message ?: "Could not open that file.")
            } finally {
                if (_state.value.connecting) {
                    _state.value = _state.value.copy(connecting = false)
                }
            }
        }
    }

    private suspend fun importUri(uri: Uri, silent: Boolean): Boolean {
        return try {
            _state.value = _state.value.copy(manualFile = true)
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
        val title = _state.value.activeTitle
        val scope = title?.id ?: MANUAL_BACKUP_SCOPE
        val result = configLoader.load(access, title)
        if (result is ConfigLoadResult.Failure) {
            val failure = result.failure
            if (failure.notFound) {
                _state.value = _state.value.copy(loading = false, connecting = false, setupVisible = true)
                if (!silent) _state.value = _state.value.copy(message = UiMessage(failure.message, true))
            } else {
                failLoad(failure.message)
            }
            return false
        }
        val loaded = (result as ConfigLoadResult.Success).config
        this.loaded = loaded
        prefs.edit()
            .putString(KEY_MODE, access.mode.name)
            .apply()
        val preset = presetFor(loaded.document, title)
        val backups = try {
            withContext(Dispatchers.IO) { backupStore.list(scope) }
        } catch (e: Exception) {
            emptyList()
        }
        val activeFamily = title?.titleCatalog?.activeFamilyId(loaded.document)
        revision++
        _state.value = _state.value.copy(
            loading = false,
            connecting = false,
            config = loaded,
            activeFamilyId = activeFamily,
            selectedPreset = preset,
            selectedSettingKey = null,
            dirty = false,
            revision = revision,
            setupVisible = false,
            showAccessSheet = false,
            backups = backups,
            message = if (silent) null else UiMessage("Connected using ${access.mode.label}.")
        )
        return true
    }

    /**
     * GRID Legends presets only. Other titles are flat registries, so a missing preset block must
     * never fail a load; they simply open without a preset model.
     */
    private fun presetFor(doc: ConfigDocument, title: FeralTitle? = _state.value.activeTitle): Preset =
        if (title?.legacyGuided == true) doc.ensureActivePresetExists() else Preset.GRAPHICS

    private fun validateFor(doc: ConfigDocument, title: FeralTitle? = _state.value.activeTitle) {
        if (title?.legacyGuided == true) doc.validate() else doc.validateRegistry()
    }

    private fun backupScope(): String = _state.value.activeTitle?.id ?: MANUAL_BACKUP_SCOPE

    private fun rejectIfBusy(action: String): Boolean {
        if (!_state.value.saving && !_state.value.restoring && !_state.value.connecting) return false
        _state.value = _state.value.copy(
            message = UiMessage("$action is unavailable while another file operation is running.", true)
        )
        return true
    }

    private fun failLoad(message: String) {
        _state.value = _state.value.copy(
            loading = false,
            connecting = false,
            setupVisible = true,
            message = UiMessage(message, true)
        )
    }

    /** Applies a guided control for a catalogued title. Never creates a missing key. */
    fun updateTitleControl(control: Control, token: String) {
        if (rejectIfBusy("Editing")) return
        val current = loaded ?: return
        val title = _state.value.activeTitle ?: return
        val base = title.titleCatalog ?: return
        val catalog = base.withFamily(base.family(_state.value.activeFamilyId))
        val root = title.valueRoot ?: return
        if (control.kind == ControlKind.RESOLUTION) {
            if (!TitleCatalogEngine.writeResolution(current.document, root, catalog, control, token)) {
                _state.value = _state.value.copy(
                    message = UiMessage("This file does not store ScreenW and ScreenH together; nothing was changed.", true)
                )
                return
            }
            revision++
            _state.value = _state.value.copy(dirty = true, revision = revision)
            return
        }
        if (current.document.value(TitleCatalogEngine.targetPath(root, catalog, control), catalog.keyFor(control)) == null) {
            _state.value = _state.value.copy(
                message = UiMessage("${control.label} is not stored in this file, so it was left untouched.", true)
            )
            return
        }
        if (!TitleCatalogEngine.write(current.document, root, catalog, control, token)) {
            _state.value = _state.value.copy(message = UiMessage("Could not update ${control.label}.", true))
            return
        }
        revision++
        _state.value = _state.value.copy(dirty = true, revision = revision)
    }

    fun selectFamily(id: String) {
        if (rejectIfBusy("Changing the selected family")) return
        _state.value = _state.value.copy(activeFamilyId = id, selectedSettingKey = null)
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
        if (rejectIfBusy("Reloading the file")) return
        _state.value = _state.value.copy(connecting = true)
        viewModelScope.launch {
            try {
                operationMutex.withLock {
                    refreshAccessOptions()
                    val access = loaded?.access
                    if (access != null) {
                        loadWith(access, silent = false)
                    } else {
                        _state.value = _state.value.copy(connecting = false, loading = false)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    connecting = false,
                    loading = false,
                    message = UiMessage("Could not reload: ${e.message}", true)
                )
            } finally {
                if (_state.value.connecting) {
                    _state.value = _state.value.copy(connecting = false)
                }
            }
        }
    }

    // --------------------------------------------------------------- editing

    fun selectPreset(preset: Preset) {
        if (rejectIfBusy("Changing the selected preset")) return
        if (loaded == null) return
        _state.value = _state.value.copy(selectedPreset = preset, selectedSettingKey = null)
    }

    fun selectSetting(key: String?) {
        _state.value = _state.value.copy(selectedSettingKey = key)
    }

    fun updateSetting(def: SettingDef, value: Int) {
        if (rejectIfBusy("Editing")) return
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
        if (rejectIfBusy("Discarding changes")) return
        val current = loaded ?: return
        runCatching {
            val doc = ConfigDocument.parse(current.originalBytes)
            validateFor(doc)
            loaded = current.copy(document = doc)
            revision++
            _state.value = _state.value.copy(
                config = loaded,
                activeFamilyId = _state.value.activeTitle?.titleCatalog?.activeFamilyId(doc),
                selectedPreset = presetFor(doc),
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
        if (rejectIfBusy("Saving")) return
        val title = _state.value.activeTitle
        val scope = title?.id ?: MANUAL_BACKUP_SCOPE
        _state.value = _state.value.copy(saving = true)
        viewModelScope.launch {
            var backup: BackupInfo? = null
            try {
                operationMutex.withLock {
                    if (loaded !== current) {
                        _state.value = _state.value.copy(
                            message = UiMessage("Save cancelled because the active file changed.", true)
                        )
                        return@withLock
                    }
                    val preset = _state.value.selectedPreset
                    val doc = current.document
                    if (applyPreset && title?.legacyGuided == true) doc.applyPresetToLive(preset)
                    validateFor(doc, title)
                    val bytes = doc.serialize()

                    backup = withContext(Dispatchers.IO) {
                        backupStore.save(scope, current.originalBytes, preset.keyName)
                    }

                    withContext(Dispatchers.IO) { current.access.write(current.file.path, bytes) }

                    // Verify the write by reading it back and parsing it.
                    val verifiedBytes = withContext(Dispatchers.IO) { current.access.read(current.file.path) }
                    val verified = ConfigDocument.parse(verifiedBytes)
                    validateFor(verified, title)
                    if (title?.legacyGuided == true && verified.activePreset() != preset) {
                        error("Verification failed: active preset did not persist")
                    }

                    val newFile = withContext(Dispatchers.IO) { current.access.locate() } ?: current.file
                    loaded = LoadedConfig(verified, current.access, newFile, verifiedBytes)
                    revision++
                    val backups = try {
                        withContext(Dispatchers.IO) { backupStore.list(scope) }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    _state.value = _state.value.copy(
                        config = loaded,
                        dirty = false,
                        revision = revision,
                        backups = backups,
                        message = UiMessage("Saved. Launch ${title?.label ?: "the game"} to apply the changes.")
                    )
                }
            } catch (e: Exception) {
                var restored = false
                if (backup != null) {
                    restored = runCatching {
                        withContext(Dispatchers.IO) { current.access.write(current.file.path, current.originalBytes) }
                    }.isSuccess
                }
                _state.value = _state.value.copy(
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
            } finally {
                _state.value = _state.value.copy(saving = false)
            }
        }
    }

    fun forceStopGame() {
        if (rejectIfBusy("Force-stopping the game")) return
        val current = loaded ?: return
        viewModelScope.launch {
            val ok = runCatching { withContext(Dispatchers.IO) { current.access.forceStop() } }.getOrDefault(false)
            _state.value = _state.value.copy(
                message = if (ok) UiMessage("${_state.value.activeLabel} was force-stopped.") else UiMessage(
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
        if (rejectIfBusy("Applying raw XML")) return false
        val current = loaded ?: return false
        return try {
            val doc = ConfigDocument.parse(_state.value.rawXml)
            validateFor(doc)
            loaded = current.copy(document = doc)
            revision++
            _state.value = _state.value.copy(
                config = loaded,
                selectedPreset = presetFor(doc),
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
        viewModelScope.launch {
            val backups = try {
                operationMutex.withLock {
                    withContext(Dispatchers.IO) { backupStore.list(backupScope()) }
                }
            } catch (e: Exception) {
                emptyList()
            }
            _state.value = _state.value.copy(backups = backups)
        }
    }

    fun restoreBackup(name: String) {
        val current = loaded ?: return
        if (rejectIfBusy("Restoring a backup")) return
        val title = _state.value.activeTitle
        val scope = title?.id ?: MANUAL_BACKUP_SCOPE
        _state.value = _state.value.copy(restoring = true)
        viewModelScope.launch {
            var backupCreated = false
            try {
                operationMutex.withLock {
                    if (loaded !== current) {
                        _state.value = _state.value.copy(
                            message = UiMessage("Restore cancelled because the active file changed.", true)
                        )
                        return@withLock
                    }

                    val backupBytes = withContext(Dispatchers.IO) { backupStore.read(scope, name) }
                    val backupDoc = ConfigDocument.parse(backupBytes)
                    validateFor(backupDoc, title)

                    val draftBytes = if (_state.value.dirty) {
                        withContext(Dispatchers.IO) { current.document.serialize() }
                    } else {
                        current.originalBytes
                    }
                    val label = if (_state.value.dirty) "before-restore-draft" else "before-restore"

                    val verifiedBytes = withContext(Dispatchers.IO) {
                        backupStore.save(scope, draftBytes, label)
                        backupCreated = true
                        replaceWithRollback(
                            access = current.access,
                            path = current.file.path,
                            replacement = backupBytes,
                            rollback = current.originalBytes
                        ) { bytes ->
                            val parsed = ConfigDocument.parse(bytes)
                            validateFor(parsed, title)
                        }
                    }

                    val verified = ConfigDocument.parse(verifiedBytes)
                    validateFor(verified, title)
                    loaded = current.copy(document = verified, originalBytes = verifiedBytes)
                    revision++
                    val backups = try {
                        withContext(Dispatchers.IO) { backupStore.list(scope) }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    _state.value = _state.value.copy(
                        config = loaded,
                        selectedPreset = presetFor(verified, title),
                        dirty = false,
                        revision = revision,
                        backups = backups,
                        message = UiMessage("Backup restored.")
                    )
                }
            } catch (e: Exception) {
                val replaceFailure = e as? FileReplaceException
                val suffix = when {
                    replaceFailure?.rolledBack == true && replaceFailure.rollbackVerified ->
                        ". The previous file was restored."
                    replaceFailure?.rolledBack == true ->
                        ". The previous file was rewritten, but verification failed."
                    backupCreated ->
                        ". A backup is available in Backups."
                    else -> ""
                }
                val backups = try {
                    withContext(Dispatchers.IO) { backupStore.list(scope) }
                } catch (listFailure: Exception) {
                    emptyList()
                }
                _state.value = _state.value.copy(
                    backups = backups,
                    message = UiMessage(
                        "Restore failed: ${e.cause?.message ?: e.message ?: "unknown error"}$suffix",
                        true
                    )
                )
            } finally {
                _state.value = _state.value.copy(restoring = false)
            }
        }
    }

    fun deleteBackup(name: String) {
        if (rejectIfBusy("Deleting a backup")) return
        val scope = backupScope()
        viewModelScope.launch {
            try {
                operationMutex.withLock {
                    withContext(Dispatchers.IO) { backupStore.delete(scope, name) }
                    val backups = withContext(Dispatchers.IO) { backupStore.list(scope) }
                    _state.value = _state.value.copy(backups = backups, message = UiMessage("Backup deleted."))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(message = UiMessage("Could not delete the backup: ${e.message}", true))
            }
        }
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
        private const val MANUAL_BACKUP_SCOPE = "manual"
    }
}
