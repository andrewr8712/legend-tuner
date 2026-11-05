# Changelog — 1.2.6 (Config Tuner)

> Supersedes [1.2.5](CHANGELOG-1.2.5.md).

**Version:** 1.2.6 · versionCode 9
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.5)
**Display name:** Config Tuner

This release adds **ROME: Total War**, bringing the supported catalogue to fifteen titles.

## New title

### ROME: Total War

- Package: `com.feralinteractive.rometw`
- Game version: **v2.0.6RC3**
- **24 guided controls**
- Display: paired `ScreenW`/`ScreenH` resolution, fullscreen, render under notch and render height
- Frame rate: engine, battle, campaign and menu caps
- Battle quality: anti-aliasing, battle/campaign/unit shadows, building/effect/terrain/vegetation/
  unit quality, unit size, massive units and unit shaders
- Campaign quality: grass density, campaign terrain density, smoke/dust and AI movement paths
- Root: `The Creative Assembly Limited/Rome Total War/Setup`; engine keys live in the same block

## Why the stored presets are not tabs

The file contains four full `RomeGraphicsPresets` blocks (`Low`, `Medium`, `High`, `Highest`) and a
`PreviousGraphicsPreset` value, but `GraphicsPreset` is empty in the live dump. The editor exposes the
live `feral_*` values instead of guessing that a preset block is active. The preset blocks are left
available through Raw XML and are documented as an open verification item.

## Fixed

- The package is declared in Android `<queries>`, so an installed copy appears under
  **Detected Titles**.
- Sanitised fixture and per-title catalogue committed; analytics ID, GPU identity and the session
  buffer were replaced.

## Testing

- 46 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Waydroid smoke test still passes for the existing import/edit/save/Back flow.
- Live tablet load: **15 controls**, **1728 × 1080** resolution, **60 FPS** caps observed.

## Known issues carried forward

- **Alien: Isolation FPS cap:** 60/120 FPS works, but Feral's “Isolation has crashed” reporter can
  appear on the splash screen; the game continued in testing.
- **Saints Row motion controls:** the game shows the toggles enabled, but it did not respond to
  sensor input on Android 17 in testing; the settings are written correctly.
- **Company of Heroes resolution:** no live `ScreenW`/`ScreenH` pair in the first-launch file.
- **Total War: EMPIRE:** the `GraphicsQuality` index mapping and anti-aliasing vocabulary are
  inferred and marked for in-game verification.
- **Tropico 3:** the 2×–16× anisotropy vocabulary is inferred from the observed `Off` value.
- **ROME: Total War:** anti-aliasing mapping and live-value/preset rewrite behaviour remain
  unverified.

## Install

Download `ConfigTuner-1.2.6-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.6 (versionCode 9)
- Debug-signed with the same key as 1.2.5; installs over 1.2.5 in place
- Requires Android 8.0+

SHA-256: `4da77ca263c30661fd0229fe29d3f619832208f1111445c6ab7dcbeaa84ab35e`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
