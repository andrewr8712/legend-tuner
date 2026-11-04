# Hitman: Blood Money — Reprisal — catalogue source

> **Status:** implemented as `HitmanCatalog`; rows below are the shipped controls.

Platform: Android 1.3.1 (`com.feralinteractive.hitmanbloodmoney_android`), TB520FU dump.
Root: `HKEY_CURRENT_USER/Software/Feral Interactive/Hitman Blood Money/Setup`.
Model: `Suffixed(["_battery", "_graphics", "_perf", "_plus"])`, 17 graphics keys per family.

**Preset labels are in the APK** (`Localizable_Game.strings`):

| Suffix | Game string | Label |
|---|---|---|
| `_battery` | `InGame.GraphicsPresetLow` | BATTERY SAVER |
| `_graphics` | `InGame.GraphicsPresetNormal` | GRAPHICS |
| `_perf` | `InGame.GraphicsPresetHigh` | PERFORMANCE |
| `_plus` | `InGame.GraphicsPresetHighest` | PERFORMANCE+ |

Base (unsuffixed) keys exist for some settings and are the live values (e.g.
`DirectionalShadowResolution = 1024`).

## The triple problem

Every suffixed value is a **comma-separated triple**, not a single value:

```
Anisotropy_graphics                = "16, 16, 16"
DirectionalShadowResolution_graphics = "1024, 1536, 2048"
IdleFPS_perf                       = "40, 40, 40"
FXAAEdgeQualityThreshold_graphics  = "0.063, 0.063, 0.063"
```

For shadow resolution the three numbers are a ladder (1024 / 1536 / 2048) rather than one setting, so
writing a single token would be wrong. Until the semantics are established, only keys whose triple is
uniform (`IdleFPS`, `ResponsiveFPS`, `Antialias`, `LevelOfDetail`, `TextureResolution`,
`EnableDropShadows`, `EnableSelfShadows`, `DisableDepthOfField`, `DisableBloom`, `DisableFinishFilter`)
can be shown as guided controls; the rest stay read-only in All values.

## Controls

| Key family | Label | Description | Observed triple (graphics) |
|---|---|---|---|
| `IdleFPS` | Idle frame cap | Frame cap when the player is still or the scene is static. | `30, 30, 30` |
| `ResponsiveFPS` | Active frame cap | Frame cap while input is active. | `30, 30, 40` |
| `Antialias` | Anti-aliasing | MSAA sample level. `0` is off. | `0, 0, 0` |
| `Anisotropy` | Anisotropic filtering | AF level for floor and wall textures seen at an angle. | `16, 16, 16` |
| `LevelOfDetail` | Level of detail | Distance at which props and characters drop to lower-detail meshes. | `2, 2, 2` |
| `TextureResolution` | Texture resolution | Texture streaming budget and mip selection. | `0, 0, 0` |
| `DirectionalShadowResolution` | Sun shadow resolution | Shadow map size for the directional light; the triple is a ladder, not one value. | `1024, 1536, 2048` |
| `DropShadowQuality` | Drop shadow quality | Quality of character and prop drop shadows. | `1, 2, 2` |
| `EnableDropShadows` | Drop shadows | Enables the cheap character shadow pass. | `1, 1, 1` |
| `EnableSelfShadows` | Self shadows | Lets characters cast shadows on themselves; subtle but costs a pass. | `1, 1, 1` |
| `DisableBloom` | Bloom (inverted) | `1` disables bloom; the key is negative, so the UI must invert it. | `0, 0, 0` |
| `DisableDepthOfField` | Depth of field (inverted) | `1` disables DOF. | `0, 0, 0` |
| `DisableFinishFilter` | Finish filter (inverted) | `1` disables the colour-grade finish pass. | `1, 0, 0` |
| `FXAAPreset` | FXAA preset | FXAA quality preset index. | `0, 2, 3` |
| `FXAAEdgeQualityThreshold` | FXAA edge threshold | Edge detection threshold; lower finds more edges. | `0.063, 0.063, 0.063` |
| `FXAAEdgeThresholdMin` | FXAA minimum threshold | Minimum contrast before FXAA acts. | `0.0312, 0.0312, 0.0312` |
| `FXAASubpixQuality` | FXAA subpixel quality | Subpixel aliasing removal strength. | `0.0, 0.0, 0.0` |

The game itself ships `InGame.AdvancedGraphics` and `InGame.AdvancedGraphicsPreset` ("DEVICE
PRESET", High/Medium/Low) strings plus a disclaimer that presets are assigned per device — evidence
that preset values are device-dependent, which is why four complete families exist in one file.

## Honest state

This is the one title where the guided screen cannot cover everything on day one. It ships with:
preset selection, the uniform-triple controls above, and a clear pointer to All values for the rest.
That is better than mis-writing a ladder as a single number.

## Open verification

1. Set shadow resolution in-game, quit, read `DirectionalShadowResolution_<preset>`: confirm which
   token the UI writes.
2. Confirm the active-preset key (not found in this file; the game may derive it from the device
   preset).
3. Confirm `Disable*` keys are inverted as their names imply.
