# Changelog — 1.2.0 (Config Tuner)

**Version:** 1.2.0 · versionCode 3
**Package:** `dev.legendtuner.app` (unchanged, so 1.1.0 installs update in place)
**Display name:** Config Tuner

This release turns the GRID Legends-only editor into a multi-title Feral config editor while keeping
the same guided experience and the same safety model.

## Added

- **Title picker on launch.** Detected games appear as cards; supported games that are not installed
  appear under **Also supported** and can be opened from a saved `preferences` file.
- **Seven new guided catalogues**, with per-title registry roots:
  - GRID Autosport — 29 controls
  - Alien: Isolation — 12
  - Saints Row: The Third — 19
  - NAPOLEON: Total War — 16
  - Hitman: Blood Money — 18
  - Lara Croft: Guardian of Light — 10
  - Lara Croft: Temple of Osiris — 11
- **Preset variants as tabs.** `PresetFamily` covers block presets (Saints Row, both Lara titles), key
  prefixes (GRID Autosport's performance/power-saver caches) and key suffixes (Hitman's four families).
- **Control kinds for real-world data:** toggle, choice, stepper, resolution pair, read-only; plus
  `triple` (Hitman comma values), `invert` (`Disable*` keys) and engine-key exemptions.
- **Common-value dropdowns:** render height 480–2160 px; frame caps 24–144 fps; Saints Row resolution
  1280 × 720 … 2560 × 1600 and resolution scale 50–100 %.
- **Manual import keeps title identity**, so a GRID Legends file opens with its guided editor even
  when the game is not installed.
- `docs/catalogues/` — eight per-title inventories with provenance and open questions.
- Eight sanitised `preferences` fixtures covering every supported title.

## Changed

- Access paths, force-stop and save messages are parameterised by package instead of hardcoded GRID
  Legends.
- Registry validation is split: GRID Legends keeps strict preset validation; every other title gets a
  structural check, because the GRID key paths do not exist elsewhere.
- The guided editor is the primary surface for every title; Raw XML is the expert fallback.
- `FeralTitle` carries `registryRoot`, an optional `controlsRoot` and an optional `titleCatalog`.

## Fixed

- **GRID-specific validation rejected every other title** ("Missing GRIDLegends/GraphicsSettings
  section") after the file had parsed correctly.
- **Lara Croft graphics live under `Software/Crystal Dynamics/.../Graphics`**, not the Feral `Setup`
  root; the catalogue now uses a separate control root.
- **Hitman's shadow-mapping key is `VideoSetting_ShadowMapping`** (no space), unlike
  `Shadow Map Resolution`; the typo made a stored setting look absent.
- **Picker no longer claims "launch once"** from a direct `canRead()` check, which is always false on
  Android 11+; cards say Installed and access is decided at open time.
- **Resolution/engine controls no longer disappear inside a preset family** — `ignoreSuffix` and
  `ignoreFamilyPath` keep them out of family scope.

## Verified on the TB520FU (Android 17, KernelSU root)

| Check | Result |
|---|---|
| GRID Autosport write | Shadows Medium → Ultra, saved; file read back `gfxconfig_shadows = ultra`; restored to medium |
| Hitman triple write | Performance+ Idle cap 60 → 59; file `IdleFPS_plus = "59, 59, 59"`; restored to `"60, 60, 60"` |
| Saints Row resolution | 1920 × 1080 written to `ScreenW`/`ScreenH`; restored to 2560 × 1440 |
| GRID Legends manual file | Imported a saved registry, dashboard and three-preset editor loaded, no game installed |
| Catalogue coverage | All eight titles render stored values; absent keys show "Not stored" |
| Tests | 18/18 passing; debug APK builds |

Only GRID Autosport (a rendering key), Hitman (a frame cap) and Saints Row (resolution) have had a
full write → verify → revert cycle. The other titles are verified as readers of their real files.

## Known gaps

- GRID Autosport's internal prefix → in-game preset-name mapping is still unverified; tabs are named
  Current / Performance cache / Power saver cache.
- Hitman's shadow-resolution ladder is read-only; `Disable*` inversion is named from the key, not
  observed.
- Saints Row tier tokens beyond the observed value are inferred and marked with amber dots.
- Aliens' texture-filter index order and NAPOLEON's quality index remain unverified.
- ScreenW/ScreenH may be recalculated by the game at launch; the UI says so.

## Release build

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Session metrics

Recorded from the local session database for this work (estimate, not a billed invoice):

| Metric | Value |
|---|---:|
| API calls | 472 |
| Input tokens | 847,623 |
| Output tokens | 411,729 |
| Cache-read tokens | 115,292,544 |
| Reasoning tokens | 202,917 |
| Estimated cost | $0.72 |
| Wall clock | ~3 h 09 m |
