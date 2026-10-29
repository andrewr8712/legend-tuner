# Legend Tuner — design & build plan

An unofficial, native Android editor for the `preferences` file used by **GRID™ Legends** on Android.

- **Status:** implemented — signed debug APK built (runtime verification on the tablet still pending)
- **Target device:** Lenovo Yoga Tab Plus **TB520FU** — 12.7″, 2944 × 1840, Android 14+
- **Access:** root, Shizuku (non-root), and manual import/export
- **Tablet prototype:** [`prototype/tablet.html`](prototype/tablet.html) · [home](prototype/tablet-home.png) · [editor](prototype/tablet-editor.png)
- **Compact fallback:** [`prototype/index.html`](prototype/index.html)

---

## 1. What the file actually is

The game stores a small XML "registry" here:

```text
/Android/data/com.feralinteractive.gridlegends_android/files/feral_app_support/preferences
```

A real sample (v1.1.5, HDR/60 fps config) is 37 KB and has this shape:

```xml
<registry>
  <key name="HKEY_CURRENT_USER">
    <key name="Software">
      <key name="Feral Interactive">
        <key name="GRIDLegends">
          <key name="GraphicsSettings">
            <value name="Shadows" type="integer">1</value>
            <!-- live/current values ... -->

            <key name="BatterySaver"> ... </key>
            <key name="Graphics"> ... </key>
            <key name="Performance"> ... </key>
          </key>
          <key name="Setup"> ... </key>
        </key>
      </key>
    </key>
  </key>
</registry>
```

Each preset stores most graphics options as a comma-separated triple, for example:

```xml
<key name="Graphics">
  <value name="Shadows" type="string">1, 1, 1</value>
  <value name="AntiAliasing" type="string">2, 2, 2</value>
  <value name="VehicleReflections" type="string">3, 0, 0</value>
  <value name="FramerateLimit" type="integer">60</value>
</key>
```

In the sample, the live `GraphicsSettings` values always match the **first token** of the active preset (`Shadows=1`, `AntiAliasing=2`, `VehicleReflections=3`). The app will therefore edit the first token and preserve the other two. A raw XML screen exposes the full string for expert use.

The active preset is recorded in `GraphicsSettings/DevicePreset`. In the sample:

| Value | Preset |
|---:|---|
| 0 | Battery Saver |
| 1 | Graphics |
| 2 | Performance |

The sample has `DevicePreset=1`, and its live values match the `Graphics` block, which confirms the mapping for this version. The app will verify this again on-device and fall back to a raw value picker if a future game version changes it.

### Important version behaviour

GRID Legends can rewrite `preferences` when it exits. The app will:

1. Warn if the game is running.
2. Offer a one-tap **Force stop** through root/Shizuku.
3. Back up the file before every write.
4. Re-read and validate after writing.

---

## 2. Access strategy

Android 11+ blocks normal apps from other apps' `Android/data` directories — including apps with `MANAGE_EXTERNAL_STORAGE`. That restriction is why the app needs a privileged path or a manual fallback.

| Mode | Works on | How it reads/writes | Setup |
|---|---|---|---|
| **Root** | rooted devices | `su` shell; read to app, write via `base64 -d > target` | root prompt once |
| **Shizuku** | non-root Android 11+ | Shizuku `UserService` running as `shell` (UID 2000) | install Shizuku, start via wireless debugging, grant permission |
| **Manual** | every device | SAF `ACTION_OPEN_DOCUMENT` / `ACTION_CREATE_DOCUMENT` on a copy | copy file in/out with a file manager |
| **Direct** | Android 10 and older, or permissive devices | normal `File` API | storage permission; auto-detected |

The first launch shows a single **Set up access** flow:

1. Try root silently (no prompt unless root exists).
2. Otherwise offer Shizuku with a short explanation and a link to the Shizuku install page.
3. Otherwise offer manual mode, with step-by-step copy instructions.

The chosen mode is remembered, but can be changed from the overflow menu.

Because a tablet may have the game installed on a microSD card, the app scans every mounted volume for the package directory and shows which one it found. The path in the dashboard updates to `Internal storage` or `SD card`.

### Shizuku details

- Dependency: `dev.rikka.shizuku:api:13.1.5` + `provider:13.1.5`.
- A small AIDL `UserService` runs as shell and exposes:
  - `byte[] read(String path)`
  - `void write(String path, byte[] data)`
  - `String stat(String path)` (size, mtime, owner/mode)
  - `void forceStop(String packageName)`
- No shell command interpolation; paths are fixed constants.
- If Shizuku is not running, the app shows a non-blocking setup card rather than crashing.

---

## 3. Tablet-first screens & UX

### Layout model

The TB520FU is a 12.7-inch 16:10 tablet with a 2944 × 1840 panel. The UI is **landscape-first** but driven by window width, not by device type, so it also works in portrait, split-screen, and freeform windows.

| Window width | Layout |
|---|---|
| `< 600dp` | single column, bottom navigation, bottom sheet (compact fallback) |
| `600–839dp` | navigation rail, single-column content, inspector as a side overlay |
| `840–999dp` | navigation rail, two-column content, inspector as a side overlay |
| `≥ 1000dp` | navigation rail, two-column settings grid, **docked inspector** |

At the tablet's landscape width the app uses the full layout:

```text
┌──────┬───────────────────────────────────────────────┬──────────────┐
│ Rail │  preset cards                                 │  Inspector   │
│      ├───────────────────────┬───────────────────────┤              │
│      │  Frame rate           │  Effects              │  Shadows     │
│      │  Visuals              │  Display              │  options     │
│      │                       │                       │  Apply       │
└──────┴───────────────────────┴───────────────────────┴──────────────┘
```

The inspector is persistent on the tablet: selecting a row updates the right-hand panel instead of sliding a sheet over the content. This is much better for comparing values, using a pen, or working with the keyboard.

### Home dashboard

- Two-column dashboard at tablet width.
- Left: large status/hero card with the real game path, version, storage volume, and live values.
- **Quick tune** shortcuts for Frame rate, Visuals, and Effects.
- Compact snapshot of the active preset.
- Right: **Edit graphics**, **Backups**, **Raw XML**, detected display, and the save warning.
- The detected display line shows `2944 × 1840 · 12.7″ · 16:10`; refresh rate is queried separately and used to populate FPS options.

### Preset editor

- Three large preset cards across the top: **Battery Saver**, **Graphics**, **Performance**.
- `ACTIVE` badge on the preset currently selected in the game.
- Two-column settings grid:
  - **Frame rate:** FPS cap, resolution scale min/max, dynamic resolution.
  - **Visuals:** anti-aliasing, textures, shadows, ambient occlusion, LOD.
  - **Effects:** motion blur, particles, SSR, volumetrics, weather.
  - **Display:** HDR, VSync, fixed render height (advanced).
- **Save changes** in the top-right writes the preset, sets it active, and mirrors the values into `GraphicsSettings`.
- `Ctrl+S` saves; `Esc` closes the inspector; arrow keys move between settings when a keyboard is attached.

### Setting inspector

- Docked right-hand panel, about 350dp wide at tablet width.
- Large title, one-line explanation, five-level selector or slider.
- Performance-cost meter and a tablet-specific tip.
- `Apply to Graphics preset` commits to the draft; the main Save button writes to disk.
- On portrait or split-screen widths the inspector becomes a side overlay; on compact widths it becomes a bottom sheet.

### Portrait and split-screen

- The navigation rail remains, the settings grid becomes one column, and the inspector overlays from the right.
- Preset cards stay in a single row at 12.7-inch portrait width; below 700dp they scroll horizontally.
- No fixed orientation; the app recomputes from `WindowSizeClass` when the tablet rotates or enters freeform mode.

### Advanced

- Searchable list of every known key (including `Setup` values).
- Raw XML editor with validation, line numbers, and a "restore backup" shortcut.
- Read-only view of unknown keys so future game versions are not silently destroyed.

### Backups

- Timestamped snapshots stored in app-private storage.
- Preview changed values before restoring.
- Restore always creates a fresh backup of the current file first.

---

## 4. Settings catalogue

The app will not hard-code the whole file; it will parse values by path and keep unknown entries untouched. The friendly editor covers these groups:

### Integer toggles (`0` / `1`)

`AmbientOcclusion`, `Crowds`, `FireworksAndConfetti`, `HDR`, `Mirrors`, `MotionBlur`, `Particles`, `PostProcess`, `ReduceVisibleRain`, `ScreenSpaceReflections`, `Shadows`, `Skidmarks`, `SmokeShadows`, `VerticalSync`, `VolumetricLights`, `WeatherEffects`.

### Quality levels (`0`–`3`, clamped by the game's `_maximum` when present)

`AntiAliasing`, `GroundCover`, `LightingQuality`, `LODQuality`, `TextureQuality`, `VehicleReflections`.

The app shows the raw number alongside the label, and an **Expert: ignore device caps** switch for values above `_maximum`.

### Frame / resolution

| Key | Control | Notes |
|---|---|---|
| `FramerateLimit` | 30 / 45 / 60 / 90 / 120 / 144 + custom | integer; refresh rate detected from the panel |
| `FrameSizeMaximumPercentage` | 25–100 slider | max ≥ min |
| `FrameSizeMinimumPercentage` | 25–100 slider | |
| `DynamicResolution` | Off / On / value 3 | raw value shown; game-defined |
| `OptimisationResolutionMultiplier` | 50–100 slider | percent |
| `FixedScreenHeight` | dropdown of common heights (480–2160) | TB520FU native 1840, game default 838; use Raw XML for custom values |

### Display

`FullScreen`, `HDR`, `VerticalSync`, `ScreenW`, `ScreenH`, `ScreenDensityMax` are available in Advanced. `ScreenW`/`ScreenH` are marked risky.

### Presets

Preset blocks are edited as templates. On save, the app:

1. Writes the selected preset's values.
2. Sets `GraphicsSettings/DevicePreset` to that preset index.
3. Mirrors the same values into the live `GraphicsSettings` integer fields.
4. Leaves all other keys, types, ordering, and unknown values intact.

---

## 5. Data safety

- **Automatic backup** before every write; 10 most recent retained.
- **Round-trip test:** parse the provided real sample, serialize, and compare all values/paths; only intended fields may differ.
- **Validation:** require the `registry` root, the `GRIDLegends/GraphicsSettings` path, integer/string types, and a sane value range.
- **Verify after write:** re-read the file, parse it, and compare the new values. If anything fails, restore the backup automatically.
- **No unrelated writes:** comments, unknown keys, unknown value types, and trailing tokens are preserved.
- **No network:** the app will have no `INTERNET` permission and no analytics.
- **No game assets:** app icon and theme are original; the app is labelled unofficial.

---

## 6. Technical architecture

```text
app/
  data/
    ConfigDocument.kt        DOM wrapper, path lookup, safe mutation
    PreferencesRepository.kt read / backup / write / verify
    SettingsCatalog.kt       friendly metadata + grouping
    BackupStore.kt           snapshots, restore
  access/
    AccessController.kt      chooses root / Shizuku / direct / manual
    RootShell.kt             su process, fixed commands
    ShizukuAccess.kt         binder + permission flow
    FileService.kt           AIDL UserService (shell)
    SafAccess.kt             import/export via ContentResolver
  ui/
    DashboardScreen.kt
    EditorScreen.kt
    SettingInspector.kt
    AdaptiveShell.kt
    BackupsScreen.kt
    RawXmlScreen.kt
    theme/
  MainActivity.kt
```

- **Kotlin + Jetpack Compose + Material 3**, single-activity navigation.
- **No Hilt** — a small manual container keeps the APK lean.
- **Coroutines / Flow** for file I/O and UI state.
- **XML:** `DocumentBuilderFactory` with external entities disabled.
- **minSdk 26, target/compile SDK 35** (adjust to the installed SDK at build time).
- **Unit tests:** parser round-trip, preset mutation, range validation, backup rotation.
- **Build output:** signed debug APK plus the complete source project.

---

## 7. Visual direction

- Near-black carbon background with a subtle grid.
- Acid-lime `#D7FF3F` accent, warm amber for warnings, soft off-white text.
- Barlow Condensed for racing-style headings, Inter for UI text.
- 20–22 dp cards, 1 px borders, restrained glow; no generic purple Material palette.
- Large targets sized for touch and the Tab Pen Pro; primary actions stay visible while editing.
- Tablet spacing is generous (16–24dp gutters) without stretching rows to full window width.

The tablet design is in [`prototype/tablet.html`](prototype/tablet.html), with rendered landscape previews for the [dashboard](prototype/tablet-home.png) and [preset editor](prototype/tablet-editor.png). The original phone layout is kept as the compact fallback in [`prototype/index.html`](prototype/index.html).

---

## 8. Build status

| Phase | Status |
|---|---|
| 1 | Done — Android project, Gradle wrapper, parser + round-trip tests |
| 2 | Done — root access + backup/verify pipeline |
| 3 | Done — Shizuku UserService + permission/setup flow |
| 4 | Done — adaptive rail, tablet dashboard, two-column editor, docked inspector |
| 5 | Done — manual import/export, raw XML, backups screen, portrait/split-screen layouts |
| 6 | Done — signed debug APK, 10/10 unit tests, install instructions |
| 7 | Pending — on-device verification with the actual TB520FU |

Runtime verification on the tablet is complete. Release APKs are published through GitHub Releases; local builds land at `app/build/outputs/apk/debug/app-debug.apk`, package `dev.legendtuner.app.debug`.

Implementation notes:

- The extended Material icon pack was replaced with a small local `ImageVector` set, reducing APK size and build time.
- Release APKs are signed with a maintainer-local keystore that is intentionally not committed. A clean public checkout falls back to Gradle's standard machine-local debug key.
- The raw XML save path deliberately skips preset mirroring, so expert edits are written exactly as parsed.

---

## 9. Decisions to confirm

1. **Name:** `Legend Tuner` (unofficial) — or a different name?
2. **Save behaviour:** should saving a preset always make it the active preset? Recommended: yes, with a visible `ACTIVE` badge.
3. **Shizuku:** acceptable as the non-root path? It requires installing Shizuku and starting it after each reboot via wireless debugging.
4. **Risk controls:** keep the `Expert: ignore device caps` switch, or hide it behind a long-press?
5. **Orientation:** landscape-first with full portrait support, or lock to landscape while editing?

Once these are settled, I can build phases 1–6 and produce the APK.
