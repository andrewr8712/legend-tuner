# Changelog — 1.2.5 (Config Tuner)

> Supersedes [1.2.4](CHANGELOG-1.2.4.md).

**Version:** 1.2.5 · versionCode 8
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.2.4)
**Display name:** Config Tuner

This release adds **Total War: EMPIRE** and **Tropico 3**, bringing the supported catalogue to
fourteen titles.

## New titles

### Total War: EMPIRE

- Package: `com.feralinteractive.empire_android`
- **28 guided controls**
- Battle quality: shadows, textures, filtering, unit/building detail, particles, grass, trees,
  water, sky, terrain, ships, anti-aliasing and six post-processing toggles
- Display: fullscreen, render under notch, render height, resolution scale and graphics preset
- Frame rate: dynamic manager, battle cap, campaign cap and battery saver
- Root: `The Creative Assembly Limited/Total War EMPIRE/Setup`, with `GraphicsSettings` and
  `MainMenuSettings` subtrees
- `ScreenW`/`ScreenH` exist only in `AutoValueRemap`, not in the first-launch `Setup` block, so the
  editor exposes render height and resolution scale instead of inventing a resolution pair.

### Tropico 3

- Package: `com.feralinteractive.tropico3`
- **15 guided controls**
- Display: paired `ScreenW`/`ScreenH` resolution, fullscreen, render under notch and render height
- Graphics: scene resolution, shadows, textures, water, terrain, grass, view distance, anisotropy
  and post-processing
- Performance: engine frame cap and battery saver frame cap
- Root: `Haemimont Games/Tropico 3: Gold Edition/Setup`, with the string quality ladder in `GFX`
  and the engine cap in `IndirectX/Direct3D/Config`

## Fixed

- Both packages are declared in Android `<queries>`, so installed copies appear under
  **Detected Titles** rather than **Also Supported**.
- Sanitised fixtures and per-title catalogues are committed; analytics IDs, the GPU identity string
  and Tropico's session buffer were replaced.

## Testing

- 45 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Debug build succeeds.
- Waydroid smoke test still passes for the existing import/edit/save/Back flow.

## Known issues carried forward

- **Alien: Isolation FPS cap:** 60/120 FPS works, but Feral's “Isolation has crashed” reporter can
  appear on the splash screen; the game continued in testing.
- **Saints Row motion controls:** the game shows the toggles enabled, but it did not respond to
  sensor input on Android 17 in testing; the settings are written correctly.
- **Company of Heroes resolution:** no live `ScreenW`/`ScreenH` pair in the first-launch file.
- **Total War: EMPIRE:** the `GraphicsQuality` index mapping and anti-aliasing vocabulary are
  inferred and marked for in-game verification.
- **Tropico 3:** the 2×–16× anisotropy vocabulary is inferred from the observed `Off` value.

## Install

Download `ConfigTuner-1.2.5-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.5 (versionCode 8)
- Debug-signed with the same key as 1.2.4; installs over 1.2.4 in place
- Requires Android 8.0+

SHA-256: `7e7e8df514a6dac183dba6d34810f1d2caf073f7ddbf3066962d46fe25df08c7`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
