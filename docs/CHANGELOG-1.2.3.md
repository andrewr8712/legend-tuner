# Changelog — 1.2.3 (Config Tuner)

> Supersedes [1.2.2](CHANGELOG-1.2.2.md).

**Version:** 1.2.3 · versionCode 6
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.2)
**Display name:** Config Tuner

This release adds three more Feral titles and fixes package detection for the new cards.

## New titles

### Sid Meier's Railroads!

- Package: `com.feralinteractive.railroads_android`
- **16 guided controls**
- Display: resolution, render height, fullscreen, render under notch
- Graphics: shader/shadow/texture/tree quality, anti-aliasing
- Performance: dynamic frame-rate manager, frame cap, idle/responsive caps, battery saver
- Engine: separate Vulkan submit thread
- Roots: `Original Developer/Sid Meier's Railroads!/Setup` and
  `IndirectX/Direct3D/Config`

### Tomb Raider

- Package: `com.feralinteractive.tombraider_android`
- **24 guided controls**
- Four preset tabs: Battery saver, Graphics, Performance, Performance+
- Display, quality, motion aim/sensitivity and engine frame cap
- Roots: `Crystal Dynamics/Tomb Raider/Setup` (with `GraphicsSettings` blocks) and
  `IndirectX/Direct3D/Config`

### Hitman: Absolution

- Package: `com.feralinteractive.hitmanabsolution_android`
- **25 guided controls**
- Three preset tabs: Battery saver, Framerate, Graphics
- Display, quality, motion aim/sensitivity and frame caps
- `_battery`, `_framerate` and `_graphics` suffix families under
  `IO Interactive/Hitman Absolution`

## Fixed

- All three new packages are now declared in Android `<queries>`, so installed copies are detected
  and shown under **Detected Titles** rather than **Also Supported**.
- Sanitised fixtures are committed for Railroads, Tomb Raider and Hitman: Absolution; analytics IDs,
  the Feral user ID and GPU identity strings were replaced.

## Known issues carried forward

- **Alien: Isolation FPS cap:** 60/120 FPS works, but Feral's “Isolation has crashed” reporter can
  appear on the splash screen; the game continued in testing.
- **Saints Row motion controls:** the game shows the toggles enabled, but it did not respond to
  sensor input on Android 17 in testing; the settings are written correctly.

## Verified

| Check | Result |
|---|---|
| Unit tests | 39/39 passing |
| Lint | `lintDebug` passes, 12 warnings, 0 errors |
| Debug build | `assembleDebug` succeeds |
| Device | Lenovo Yoga Tab Plus TB520FU, Android 17 |
| Railroads | Live file opens; 16 controls render with observed values |
| Tomb/Hitman | Titles appear in the picker; catalogue paths covered by fixture tests |
| In-place update | Debug package/key unchanged from 1.2.2 |

## Install

Download `ConfigTuner-1.2.3-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.3 (versionCode 6)
- Debug-signed with the same key as 1.2.2; installs over 1.2.2 in place
- Requires Android 8.0+

SHA-256: `ca4f5336a53a3b82389d62a38ee6bd4ee3d193ee066ce15866484c6778a6d8c1`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
