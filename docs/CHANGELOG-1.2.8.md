# Changelog — 1.2.8 (Config Tuner)

> Supersedes [1.2.7](CHANGELOG-1.2.7.md).

**Version:** 1.2.8 · versionCode 11
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.7)
**Display name:** Config Tuner

This release adds **ROME: Barbarian Invasion**, bringing the supported catalogue to seventeen titles.

## New title

### ROME: Barbarian Invasion

- Package: `com.feralinteractive.rometwbi`
- Game version: **v2.0.6RC2**
- **24 guided controls**
- Shares the ROME: Total War catalogue with its own registry root and build:
  resolution, fullscreen, render height, four frame caps, quality, shadows, units, grass and campaign
  settings
- Root: `The Creative Assembly Limited/Rome Total War - Barbarian Invasion/Setup`

## Notes

- The live `feral_*` values are exposed. The `RomeGraphicsPresets` blocks are not exposed as tabs
  because `GraphicsPreset` is empty and the preset/live rewrite behaviour is unverified for this
  build as well.
- The package is declared in Android `<queries>`, so an installed copy appears under
  **Detected Titles**.
- Sanitised fixture committed; analytics ID, GPU identity and the session buffer were replaced.

## Testing

- 48 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Live tablet load: **24 controls**, **1728 × 1080** resolution, **60 FPS** caps observed.

## Known issues carried forward

- **Alien: Isolation FPS cap:** 60/120 FPS works, but Feral's “Isolation has crashed” reporter can
  appear on the splash screen; the game continued in testing.
- **Saints Row motion controls:** the game shows the toggles enabled, but it did not respond to
  sensor input on Android 17 in testing; the settings are written correctly.
- **Company of Heroes resolution:** no live `ScreenW`/`ScreenH` pair in the first-launch file.
- **Total War: EMPIRE:** the `GraphicsQuality` index mapping and anti-aliasing vocabulary are
  inferred and marked for in-game verification.
- **Tropico 3:** the 2×–16× anisotropy vocabulary is inferred from the observed `Off` value.
- **ROME / Barbarian Invasion:** anti-aliasing mapping and live-value/preset rewrite behaviour
  remain unverified.
- **Medieval II: Total War:** shader/filter index ranges, `GraphicalQuality` rewrite behaviour and
  anti-aliasing mapping remain unverified.

## Install

Download `ConfigTuner-1.2.8-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.8 (versionCode 11)
- Debug-signed with the same key as 1.2.7; installs over 1.2.7 in place
- Requires Android 8.0+

SHA-256: `a4cb93119a0daf2828e00924e32ae19c0b29976b9ab1e4aa5b7d067176dc8e5e`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
