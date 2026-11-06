# Changelog — 1.3.3 (Config Tuner)

> Supersedes [1.3.2](CHANGELOG-1.3.2.md).

**Version:** 1.3.3 · versionCode 16
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.3.2)
**Display name:** Config Tuner

Test and regression polish. No title, catalogue or visible UI changes.

## Changed

- Added Compose UI smoke coverage for the picker → editor → save → back flow, including the
  long-title content description and the zero-installed-titles **Open a preferences file** action.
- Added a unit regression test for a read-permission failure, asserting it becomes a clear
  `ConfigLoadResult.Failure` instead of crashing.
- Updated the instrumentation stack to Espresso 3.7.0, which fixes the Android 17
  `InputManager.getInstance` failure seen with the older test dependency.
- Test-only tags were added to the picker, editor and empty state; they are inert in the shipping UI.

## Verification

- 51 unit tests passing, 0 failures.
- 2 Compose UI smoke tests passing on the TB520FU (Android 17).
- `lintDebug` passes with 13 warnings, 0 errors.
- Debug APK installed over 1.3.2 in place; picker checked on the tablet.

## Known issues carried forward

- Alien: Isolation's Feral crash reporter can appear on the splash at high frame caps.
- Saints Row gyro toggles write correctly but did not respond on Android 17.
- Company of Heroes has no live `ScreenW`/`ScreenH` pair in its first-launch file.
- Total War: EMPIRE, Tropico 3, ROME, Medieval II and XCOM 2 keep their documented unverified
  mapping/preset notes.

## Install

Download `ConfigTuner-1.3.3-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.3.3 (versionCode 16)
- Debug-signed with the same key as 1.3.2; installs over 1.3.2 in place
- Requires Android 8.0+

SHA-256: `8ce1d15b7d405443db454bc2fc3706b689769604b653522940d3c4dc279cbe8b`
