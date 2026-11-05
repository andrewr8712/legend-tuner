# Changelog — 1.2.4 (Config Tuner)

> Supersedes [1.2.3](CHANGELOG-1.2.3.md).

**Version:** 1.2.4 · versionCode 7
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.3)
**Display name:** Config Tuner

This release makes Company of Heroes the twelfth supported title, adds unsaved-change protection and
makes preset tabs open on the game's active profile where the registry exposes it.

## New title

### Company of Heroes

- Package: `com.feralinteractive.companyofheroes_android`
- **24 guided controls**
- Display: fixed screen height, fullscreen, render under notch
- Graphics: shader, model, terrain, water, shadow, texture, anti-aliasing and frame settings
- Performance: frame cap, battery saver, separate Vulkan submit thread
- Roots: `Feral Interactive/Company of Heroes/Setup`,
  `Original Developer/Company of Heroes/Config` and
  `IndirectX/Direct3D/Config`
- `ScreenW`/`ScreenH` are not present in a first-launch file, so the editor exposes the observed
  fixed render height instead of inventing a resolution pair.

## Fixed

- **Unsaved changes are protected.** System Back and “choose another game” now show a
  **Discard unsaved changes?** confirmation instead of silently leaving the editor.
- **Tomb Raider opens on the active preset.** `GraphicsPreset` maps the live battery/graphics/
  performance/performance+ family, so the editor no longer starts on Battery saver by default.
- **Honest preset warnings.** Titles with preset families but no exposed active selector now say that
  the tabs edit stored blocks; no active profile is inferred.
- **Config loading boundary extracted** from `AppViewModel` into `ConfigFileLoader`, with tests for
  success, missing files and malformed XML.

## Testing

- 43 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Waydroid smoke test passes: install, Manual-file import, edit, save, on-disk read-back, and dirty
  Back confirmation.

## Known issues carried forward

- **Alien: Isolation FPS cap:** 60/120 FPS works, but Feral's “Isolation has crashed” reporter can
  appear on the splash screen; the game continued in testing.
- **Saints Row motion controls:** the game shows the toggles enabled, but it did not respond to
  sensor input on Android 17 in testing; the settings are written correctly.
- **Company of Heroes resolution:** no live `ScreenW`/`ScreenH` pair in the first-launch file.

## Install

Download `ConfigTuner-1.2.4-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.4 (versionCode 7)
- Debug-signed with the same key as 1.2.3; installs over 1.2.3 in place
- Requires Android 8.0+

SHA-256: `750e96142cf447f149dc139a9f1c148da73ea3411b5f681061611c8d58b1de93`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
