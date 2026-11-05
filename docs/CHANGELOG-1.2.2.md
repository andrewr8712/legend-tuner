# Changelog — 1.2.2 (Config Tuner)

> Supersedes [1.2.1](CHANGELOG-1.2.1.md); superseded by [1.2.3](CHANGELOG-1.2.3.md).

**Version:** 1.2.2 · versionCode 5
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.1)
**Display name:** Config Tuner

This release focuses on data safety, the missing per-title controls reported after 1.2.1, and
release/CI hardening.

## Safety

- **Backups are now scoped per title.** A snapshot created for one game can no longer be listed,
  read, restored, or pruned through another title’s directory. Retention is 12 snapshots per title.
- **Restore is failure-safe.** Restore now writes a before-restore snapshot, preserves an unsaved
  draft in a separate snapshot, verifies the write, and attempts a verified rollback if anything
  fails.
- **Restore is confirmed.** The Backups screen names the active title and snapshot time before
  replacing the file.
- **File operations are serialized.** Save, restore, reload, and title loading share one operation
  lock; editing is blocked while a file operation is running.
- **Load failures are contained.** Missing, unreadable, malformed, or invalid config files now
  return to setup with an error instead of escaping the coroutine.
- Existing pre-1.2.2 backups are preserved but deliberately not exposed: they carry no title
  identity and cannot be restored safely.

## New controls

- **Alien: Isolation** — engine frame-rate cap (30/60/120), mirrored into
  `PerformancePresets/GraphicsMaxFramerate` so the game no longer resets the cap to 33 FPS on launch.
- **NAPOLEON** — dynamic frame-rate manager toggle, battle/campaign frame caps, render height, and
  resolution scale.
- **Lara Croft: Temple of Osiris** — fullscreen resolution via `FullscreenWidth`/`FullscreenHeight`.
- **Saints Row: The Third** — Motion aim, Motion aim sensitivity (both axes), and the separate
  vehicle Gyro controls toggle.

## Build and release

- Release builds use the local `keystore.properties` identity when present; debug and release are
  both signed from the same local key.
- CI now runs unit tests, `lintDebug`, `assembleDebug`, and `assembleRelease`.
- The debug manifest no longer exports Compose’s preview activity.
- Added Android 12+ data-extraction rules and removed the obsolete legacy-storage attribute.
- API-27 theme attributes moved to `values-v27`, so API 26 remains lint-clean.

## Known issues

- **Alien: Isolation FPS cap:** 60 and 120 FPS are accepted and the overlay showed ~60 FPS, but
  Feral’s “Isolation has crashed” reporter can appear on the splash screen. The game continued in
  testing. This is a game-side crash report; it is not a Config Tuner crash.
- **Saints Row motion controls:** the game’s own Controls screen shows Motion Aim and Gyro Controls
  enabled, and the tablet gyro works in a third-party app, but the game did not respond to sensor
  input on Android 17 in testing. The settings are written correctly; the remaining failure appears
  to be in the game/OS sensor path.
- **NAPOLEON resolution:** the game does not persist a native `ScreenW`/`ScreenH` pair in its
  `Setup` block, so the editor exposes render height and resolution scale instead of inventing keys.
- This remains a **debug-signed APK** for the `.debug` package. It is not a production signing
  identity.

## Verified

| Check | Result |
|---|---|
| Unit tests | 36/36 passing |
| Lint | `lintDebug` passes, 12 warnings, 0 errors |
| Debug build | `assembleDebug` succeeds |
| Release build | `assembleRelease` succeeds when local signing is configured |
| Device | Lenovo Yoga Tab Plus TB520FU, Android 17 |
| In-place update | Debug package/key unchanged from 1.2.1 |
| Controls | Alien FPS/resolution, Napoleon FPS/resolution, Lara Too resolution, Saints Row motion controls verified in the editor |
| Logcat | No Config Tuner crash during the feature checks |

No game configs were left modified during release verification.

## Install

Download `ConfigTuner-1.2.2-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.2 (versionCode 5)
- Debug-signed with the same key as 1.2.1; installs over 1.2.1 in place
- Requires Android 8.0+

SHA-256: `7451f5265ae9268fbc2547ca66906c250982dce687d766b07c77e43ff1c7e921`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
