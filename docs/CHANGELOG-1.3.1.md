# Changelog — 1.3.1 (Config Tuner)

> Supersedes [1.3.0](CHANGELOG-1.3.0.md).

**Version:** 1.3.1 · versionCode 14
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.3.0)
**Display name:** Config Tuner

A readability and copy pass. No title or catalogue changes.

## Changed

- Raised the smallest metadata labels from 6–7.5sp to a minimum of 8sp across the picker, dashboard,
  editors, inspector, raw XML, backups and access screens.
- Increased the shared status-chip type from 6.5sp to 8sp.
- Replaced “this tablet” wording with “this device” in the setup and dashboard states.
- Verified the picker at normal and **1.3×** system font scale; the 19-title catalogue still fits.

## Testing

- 50 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Installed on the TB520FU and checked at both font scales.
- Website picker screenshot refreshed.

## Known issues carried forward

- Alien: Isolation’s Feral crash reporter can appear on the splash at high frame caps.
- Saints Row gyro toggles write correctly but did not respond on Android 17.
- Company of Heroes has no live `ScreenW`/`ScreenH` pair in its first-launch file.
- Total War: EMPIRE, Tropico 3, ROME, Medieval II and XCOM 2 have the documented unverified
  mapping/preset notes in their catalogue files.
- The root `CHANGELOG.md` is still historical; per-release notes remain the source of truth.

## Install

Download `ConfigTuner-1.3.1-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.3.1 (versionCode 14)
- Debug-signed with the same key as 1.3.0; installs over 1.3.0 in place
- Requires Android 8.0+

SHA-256: `995fb58913a183fddd8571f75d0c1bdf3af14e0b0a78ab7914c7f34d9362c200`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
