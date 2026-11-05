# Changelog — 1.3.0 (Config Tuner)

> Supersedes [1.2.9](CHANGELOG-1.2.9.md).

**Version:** 1.3.0 · versionCode 13
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.9)
**Display name:** Config Tuner

This release adds **XCOM 2 Collection** and replaces the oversized installed-title tiles with a
compact picker built for the full catalogue.

## New title

### XCOM 2 Collection

- Package: `com.feralinteractive.xcom2_android`
- Game version: **v1.6.1RC6**
- **25 guided controls**
- Display: paired `ScreenW`/`ScreenH`, fullscreen, render under notch, render height and resolution
  scale
- Frame rate: engine, idle, responsive and battery-saver caps
- Image quality: ambient occlusion, bloom, decals, depth of field, dirty lens, effects, FXAA, draw
  distance, screen-space reflections, shadows, soft shadows, subsurface scattering, texture detail,
  texture filtering and distortion
- Root: `Feral Interactive/XCOM 2/Setup`; engine cap in `IndirectX/Direct3D/Config`

The live `GFX*` values are exposed. The `_battery` and `_graphics` copies of the quality family are
not exposed as tabs because `GraphicsMode = 1` has not been mapped to a named mode in this build;
Raw XML remains available for them.

## Changed

- Installed titles are now compact horizontal rows: three per row on tablets, one per row on phones.
  Each row shows the monogram, title, installed/ready state and guided-control count.
- The full catalogue now fits without a wall of oversized cards.
- Counts were realigned with the catalogues: Alien 13, Saints Row 22, NAPOLEON 21, Lara Croft: Temple
  of Osiris 12.

## Testing

- 50 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Live tablet load: XCOM 2 **25 controls**, **1728 × 1080** resolution and **60 FPS** engine cap.
- Compact picker visually checked on the tablet at the full 18/19-title catalogue size.

## Known issues carried forward

- **Alien: Isolation FPS cap:** Feral's “Isolation has crashed” reporter can appear on the splash;
  the game continued in testing.
- **Saints Row motion controls:** toggles enable, but gyro input did not respond on Android 17.
- **Company of Heroes resolution:** no live `ScreenW`/`ScreenH` pair in the first-launch file.
- **Total War: EMPIRE:** `GraphicsQuality` and anti-aliasing mapping are inferred.
- **Tropico 3:** 2×–16× anisotropy vocabulary is inferred.
- **ROME / Barbarian Invasion / Alexander:** anti-aliasing mapping and live/preset rewrite behaviour
  remain unverified.
- **Medieval II: Total War:** shader/filter ranges, `GraphicalQuality` rewrite and AA mapping remain
  unverified.
- **XCOM 2 Collection:** `GraphicsMode` and the `_battery`/`_graphics` preset rewrite behaviour remain
  unverified.

## Install

Download `ConfigTuner-1.3.0-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.3.0 (versionCode 13)
- Debug-signed with the same key as 1.2.9; installs over 1.2.9 in place
- Requires Android 8.0+

SHA-256: `9e6fa042ae622813454b8815b9aa6f6e755802e5843040fa6ec141c2ec25c4e3`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
