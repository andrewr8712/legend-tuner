# Hitman: Absolution — guided catalogue source

> **Status:** implemented as `HitmanAbsolutionCatalog`.
>
> Source: first-launch `preferences` file supplied by the tester, game v1.3.1
> (`com.feralinteractive.hitmanabsolution_android`). Analytics, Feral user ID and GPU identity values
> are sanitized in `testdata/hitman-absolution-preferences-sample.xml`.

## Registry layout

Most values live directly under:

```text
HKEY_CURRENT_USER/Software/IO Interactive/Hitman Absolution
```

Three suffix families duplicate the graphics values:

- `*_battery`
- `*_framerate`
- `*_graphics`

Display, frame-limit and motion values live at the root or under `Setup`; engine values are in
`HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`. `QualityProfile = 6` records the active
profile, but the catalogue does not write it.

## Controls

| Key | Label | Group | Observed |
|---|---|---|---|
| `ResolutionWidth` / `ResolutionHeight` | Screen resolution | Display | `1526 × 1080` |
| `FixedScreenHeight` | Render height | Display | `1080` |
| `Fullscreen` | Fullscreen | Display | `1` |
| `ExclusiveFullscreen` | Exclusive fullscreen | Display | `1` |
| `VSync` | Vertical sync | Display | `1` |
| `Bloom` | Bloom | Graphics | `0` |
| `Bokeh` | Depth of field / bokeh | Graphics | `0` |
| `FXAA` | FXAA | Graphics | `0` |
| `MSAA` | MSAA | Graphics | `0` |
| `LevelOfDetail` | Level of detail | Graphics | `0` |
| `ShadowQuality` | Shadow quality | Graphics | `0` |
| `SSAO` | Ambient occlusion | Graphics | `0` |
| `LPV` | Light propagation volumes | Graphics | `0` |
| `MirrorQuality` | Mirrors and reflections | Graphics | `0` |
| `Tessellation` | Tessellation | Graphics | `0` |
| `TextureFilter` | Texture filtering | Graphics | `0` |
| `TextureResolution` | Texture resolution | Graphics | `0` |
| `ResolutionScalingMultiplier` | Resolution scale | Graphics | `1.000000` |
| `MotionAimSettings/Enabled` | Motion aim | Motion | `1` |
| `MotionAimSettings/SensitivityX` / `SensitivityY` | Motion aim sensitivity | Motion | `400.000000` |
| `FrameRateLimit` | Frame cap | Performance | `60` (Setup) |
| `IdleFPS` | Idle frame cap | Performance | `30` (root) |
| `ResponsiveFPS` | Responsive frame cap | Performance | `40` (root) |
| `EnableFramerateManager` | Dynamic frame-rate manager | Performance | `1` (engine subtree) |
| `VulkanUseSeparateSubmitThread` | Separate Vulkan submit thread | Performance | `1` (engine subtree) |

The graphics values above are from `GraphicsPreset`/`_graphics`; the battery and framerate families
are selectable in the app. Tier labels are inferred. Motion aim writes both
`MotionAimSettings.Enabled` and `Setup.AdditionalMotionControls`.

## Open verification

1. Confirm the quality-profile index and how it maps to the three suffix families.
2. Confirm the labels and accepted ranges for `Bokeh`, `MSAA`, `ShadowQuality`, `SSAO`, `MirrorQuality`
   and `TextureResolution`.
3. Confirm whether `FrameRateLimit` or the per-family `ResponsiveFPS` is the displayed frame limit.
4. Confirm whether the game rewrites suffix families on exit.
