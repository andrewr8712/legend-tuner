# Changelog — 1.2.7 (Config Tuner)

> Supersedes [1.2.6](CHANGELOG-1.2.6.md).

**Version:** 1.2.7 · versionCode 10
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.6)
**Display name:** Config Tuner

This release adds **Medieval II: Total War**, bringing the supported catalogue to sixteen titles.

## New title

### Medieval II: Total War

- Package: `com.feralinteractive.medieval2_android`
- Game version: **v1.5.3RC2**
- **24 guided controls**
- Display: paired `ScreenW`/`ScreenH` resolution, fullscreen, render under notch and render height
- Frame rate: idle, responsive, battery-saver idle and battery-saver responsive caps
- Battle quality: anti-aliasing, bloom, building/effect/grass/shadow/terrain/texture/unit/vegetation
  detail, unit size, graphics preset, reflections and shader version
- Root: `Original Developer/Medieval II Total War/Setup`, with the quality values in `VideoSettings`

## Notes

- Quality values are lowercase strings (`low`, `medium`, `high`), not integer tiers.
- `GraphicalQuality` is a preset name in the live `VideoSettings` block; no separate preset blocks are
  exposed. Changing it alone may not rewrite the individual quality values.
- `feral_unit_size` is an index; the Small/Normal/Large labels are inferred.
- `ShaderVersion` and `TextureFiltering` are exposed conservatively; only index 2 is observed.

## Fixed

- The package is declared in Android `<queries>`, so an installed copy appears under
  **Detected Titles**.
- Sanitised fixture and per-title catalogue committed; analytics ID and GPU identity were replaced.

## Testing

- 47 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Waydroid smoke test still passes for the existing import/edit/save/Back flow.
- Live tablet load: **24 controls**, **1728 × 1080** resolution, **60 FPS** responsive cap observed.

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
- **Medieval II: Total War:** shader/filter index ranges, `GraphicalQuality` rewrite behaviour and
  anti-aliasing mapping remain unverified.

## Install

Download `ConfigTuner-1.2.7-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.7 (versionCode 10)
- Debug-signed with the same key as 1.2.6; installs over 1.2.6 in place
- Requires Android 8.0+

SHA-256: `853c552e7fc69017546e60453891c8284ce05846b96fe3ad08be208e6a6be9f3`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
