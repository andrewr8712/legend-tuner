# Changelog — 1.2.9 (Config Tuner)

> Supersedes [1.2.8](CHANGELOG-1.2.8.md).

**Version:** 1.2.9 · versionCode 12
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.8)
**Display name:** Config Tuner

This release adds **ROME: Alexander**, bringing the supported catalogue to eighteen titles.

## New title

### ROME: Alexander

- Package: `com.feralinteractive.rometwalex`
- Game version: **v2.0.6RC2**
- **24 guided controls**
- Shares the ROME catalogue with its own root and build:
  resolution, fullscreen, render height, four frame caps, quality, shadows, units, grass and campaign
  settings
- Root: `The Creative Assembly Limited/Rome Total War - Alexander/Setup`

## Notes

- Live `feral_*` values are exposed; the `RomeGraphicsPresets` blocks are not tabs because
  `GraphicsPreset` is empty and preset/live rewrite behaviour is unverified.
- Package declared in Android `<queries>` so installed copies appear under **Detected Titles**.
- Sanitised fixture committed; analytics ID, GPU identity and session buffer were replaced.

## Testing

- 49 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Live tablet load: **24 controls**, **1728 × 1080** resolution, **60 FPS** caps observed.

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

## Install

Download `ConfigTuner-1.2.9-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.9 (versionCode 12)
- Debug-signed with the same key as 1.2.8; installs over 1.2.8 in place
- Requires Android 8.0+

SHA-256: `8940d384c4e8d403ccdaefaaf086053815200407c97342e9987ac228523f2364`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
