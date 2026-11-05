# Sid Meier's Railroads! — guided catalogue source

> **Status:** implemented as `RailroadsCatalog`.
>
> Source: first-launch `preferences` file from the TB520FU, game v1.4.3
> (`com.feralinteractive.railroads_android`). Analytics and GPU identity values are sanitized in
> `testdata/railroads-preferences-sample.xml`.

## Registry layout

Main settings root:

```text
HKEY_CURRENT_USER/Software/Original Developer/Sid Meier's Railroads!/Setup
```

Engine values live in a separate subtree:

```text
HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config
```

`AutoValueRemap/GPURemap` also maps `FullScreen`, `ScreenW` and `ScreenH` back into the setup block;
the editor writes the values at their real locations, not through the remap.

## Controls

| Key | Label | Group | Observed |
|---|---|---|---|
| `ScreenW` / `ScreenH` | Screen resolution | Display | `1440 × 900` |
| `FixedScreenHeight` | Render height | Display | `900` |
| `FullScreen` | Fullscreen | Display | `1` |
| `RenderUnderNotch` | Render under notch | Display | `1` |
| `ShaderQuality` | Shader quality | Graphics | `2` |
| `ShadowQuality` | Shadow quality | Graphics | `2` |
| `TextureQuality` | Texture quality | Graphics | `2` |
| `TreeDensity` | Tree density | Graphics | `2` |
| `FSAALevel` | Anti-aliasing | Graphics | `2` (engine subtree) |
| `EnableFramerateManager` | Dynamic frame-rate manager | Performance | `1` (engine subtree) |
| `MaxFramesPerSecond` | Frame cap | Performance | `40` (engine subtree) |
| `FramerateManagerNormalIdleFPS` | Idle frame cap | Performance | `30` (engine subtree) |
| `FramerateManagerNormalResponsiveFPS` | Responsive frame cap | Performance | `40` (engine subtree) |
| `UseBatterySaver` | Battery saver | Performance | `0` |
| `BatterySaverModeMaxFPS` | Battery saver frame cap | Performance | `30` |
| `VulkanUseSeparateSubmitThread` | Separate Vulkan submit thread | Engine | `1` (engine subtree) |

The quality values are integers. Only `2` was observed for the four quality controls, and only
`2` for `FSAALevel`; the Low/Medium/High and Off/2×/4× labels are inferred and should be verified
against the game's own menus. `TreeDensity` is exposed with the same three-tier ladder, but the
game also stores a separate `BatterySaveTreeDensity` value (`1`) that the editor does not change.

## Not exposed

The file contains many unrelated gameplay, audio, alert, camera, cloud and telemetry values, plus
binary-encoded volumes and camera speeds. Those stay in Raw XML for now. `CardRenderer` and
`FeralAnalyticsInstallID` are device/analytics values and are not editable controls.

## Open verification

1. Confirm the accepted range and in-game labels for the four quality keys and `FSAALevel`.
2. Confirm whether `MaxFramesPerSecond` or `FramerateManagerNormalResponsiveFPS` is the value the
   in-game frame limiter actually shows, and whether the game rewrites either on exit.
3. Confirm which `ScreenW`/`ScreenH` values the game accepts; the observed pair is `1440 × 900`.
4. Confirm whether changing `FullScreen` or `FixedScreenHeight` requires a restart.
