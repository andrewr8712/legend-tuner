# Tomb Raider — guided catalogue source

> **Status:** implemented as `TombRaiderCatalog`.
>
> Source: first-launch `preferences` file supplied by the tester, game v1.4
> (`com.feralinteractive.tombraider_android`). Analytics and GPU identity values are sanitized in
> `testdata/tomb-raider-preferences-sample.xml`.

## Registry layout

Settings root:

```text
HKEY_CURRENT_USER/Software/Crystal Dynamics/Tomb Raider/Setup
```

Four preset blocks live under `Setup/GraphicsSettings`:

- `BatterySaverPreset`
- `GraphicsPreset`
- `PerformancePreset`
- `PerformancePlusPreset`

`GraphicsSettings` also carries `GraphicsPreset = 1` and `InitialGraphicsPreset = 2`; the catalogue
does not write the selector, so preset-tab edits are explicit rather than an automatic switch.

Engine values live under `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`.

## Controls

| Key | Label | Group | Observed |
|---|---|---|---|
| `ScreenW` / `ScreenH` | Screen resolution | Display | `1526 × 1080` |
| `FixedScreenHeight` | Render height | Display | `1080` |
| `FullScreen` | Fullscreen | Display | `1` |
| `RenderUnderNotch` | Render under notch | Display | `1` |
| `ShadowMode` | Shadow mode | Graphics | `1` |
| `ShadowResolution` | Shadow resolution | Graphics | `1` |
| `SSAOMode` | Ambient occlusion | Graphics | `1` |
| `DOFQuality` | Depth of field | Graphics | `1` |
| `LODScale` | Level of detail | Graphics | `2` |
| `TextureQuality` | Texture quality | Graphics | `-1` (Auto) |
| `BestTextureFilter` | Texture filtering | Graphics | `6` |
| `ReflectionQuality` | Reflections | Graphics | `0` |
| `HairQuality` | Hair quality (TressFX) | Graphics | `0` |
| `AntiAliasingMode` | Anti-aliasing | Graphics | `1` |
| `EnableTessellation` | Tessellation | Graphics | `0` |
| `EnablePostProcess` | Post-processing | Graphics | `1` |
| `EnableMotionBlur` | Motion blur | Graphics | `0` |
| `EnableScreenEffects` | Screen effects | Graphics | `1` |
| `EnableHighPrecisionRT` | High-precision render targets | Graphics | `0` |
| `OptimisationResolutionMultiplier` | Preset render scale | Graphics | `75` |
| `MotionAimSettings/Enabled` | Motion aim | Motion | `0` |
| `MotionAimSettings/SensitivityX` / `SensitivityY` | Motion aim sensitivity | Motion | `400.000000` |
| `MaxFramesPerSecond` | Frame cap | Performance | `60` (engine subtree) |
| `VulkanUseSeparateSubmitThread` | Separate Vulkan submit thread | Performance | `1` (engine subtree) |

The graphics values above are from `GraphicsPreset`; the other three blocks are selectable in the
app. Tier labels and the `AntiAliasingMode` ladder are inferred and need in-game confirmation.
Motion aim writes both `MotionAimSettings.Enabled` and `Setup.EnableMotionAim`.

## Open verification

1. Confirm the selector index for each preset block and whether the game rewrites preset blocks on
   exit.
2. Confirm the labels and accepted ranges for `ShadowMode`, `SSAOMode`, `AntiAliasingMode` and
   `BestTextureFilter`.
3. Confirm whether `MotionAimSettings.Enabled` or `EnableMotionAim` is authoritative when they
   disagree.
4. Confirm whether `MaxFramesPerSecond` or the per-preset cap is the limiter the game displays.
