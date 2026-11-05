# Saints Row: The Third — guided catalogue source

> **Status:** implemented as `SaintsRowCatalog`; rows below are the shipped controls.

Platform: Android; game version 1.0.3 (`com.feralinteractive.saintsrow3_android`), TB520FU dump.
Root: `HKEY_CURRENT_USER/Software/Feral Interactive/Saints Row The Third/Setup`.
Model: `Blocks(Setup, [BatterySaverPreset, GraphicsPreset, PerformancePreset],
selector = GameOptions/GfxMobilePreset)` — 23 values per block.
Types: `string`, `integer`, `double`; one `array`.

**Labels.** Marked ✓ where the APK's `assets/en.lproj/Localizable_Game.strings` carries the string
(`SaintsRow3.Odev.platform_PC_*`, uppercase). Others are authored from the key. Descriptions are
authored; the game ships no per-option tooltips, only confirmation dialogs.

## Controls (per preset block)

| Key | Label | Description | Options (observed) |
|---|---|---|---|
| `GfxShadow` | Shadow detail ✓ | Shadow map resolution and filtering for world, characters and vehicles. Higher tiers sharpen contact shadows and extend their draw distance. | `medium` |
| `GfxLighting` | Lighting detail ✓ | Quality of the deferred lighting pass: light count, specular response and ambient contribution. | `high` |
| `GfxSSAO` | Ambient occlusion ✓ | Screen-space contact shadows in corners, under cars and around characters. | `off`, `low` |
| `GfxSSLR` | Screen space reflections ✓ | Screen-space reflections on glass, wet roads and polished surfaces. | `off`, `on` |
| `GfxVolumetricFog` | Volumetric fog | Light shafts and distance fog scattering; the setting behind "quality of volumetric god rays" in the game's own dialog. | `low` |
| `GfxSceneCharacters` | Character detail | Mesh and material detail for pedestrians and gang members. | `medium` |
| `GfxSceneVehicles` | Vehicle detail | Mesh LOD bias and material quality for traffic and parked cars. | `medium` |
| `GfxSceneMeshes` | World detail | Props, street furniture and building detail layers. | `medium` |
| `GfxSceneTrees` | Tree detail | Vegetation LOD and billboard distance. | `medium` |
| `GfxSceneUndergrowth` | Undergrowth | Grass, weeds and ground clutter density. | `medium` |
| `GfxSceneVFX` | Effects detail | Particle, debris and explosion effect density. | `medium` |
| `GfxAA` | Anti-aliasing ✓ | Post-process edge smoothing. `taa` is temporal AA; other values are MSAA modes. | `taa` |
| `GfxAnisotropic` | Anisotropic filtering ✓ | Texture sharpness on surfaces seen at grazing angles, such as roads and roofs. | `8x` |
| `GfxCubemapReflections` | Cubemap reflections | Reflection probes for cars and glass; the game's dialog calls this "increased reflection quality with dynamic objects". | `1` |
| `GfxResolutionScale` | Resolution scale | Internal render scale multiplier. 1.0 renders at the panel resolution. | `1.000000` |
| `GfxMinScaling` / `GfxMaxScaling` | Dynamic resolution range | Bounds for the dynamic resolution scaler when it is active. | `0.60` – `0.85` |
| `GfxSharpen` | Sharpening | Contrast-adaptive sharpen applied after upscaling. | `0.050000` |
| `GfxLodMultiplier` | LOD multiplier | Scales all level-of-detail distances at once. | `1.000000` |
| `GfxIdleFps` / `GfxResponsiveFps` | Idle / responsive FPS | Frame cap when nothing is happening, and when input is active. | `30`, `60` |
| `GfxCubemapSpecFadeDistSqStart` / `End` | Reflection fade | Distance where car and glass reflections begin to fade and where they stop. | `3000` – `10000` |

## Controls (Setup, outside the blocks)

| Key | Label | Description | Observed |
|---|---|---|---|
| `GameOptions/GfxMobilePreset` | Active preset | Which block the game applies: `battery`, `graphics`, `performance`. | `graphics` |
| `GameOptions/GfxAdvancedGraphics` | Advanced graphics | Unlocks the per-setting menu; the app mirrors this with its own Advanced switch. | `0` |
| `GameOptions/GfxDoF` / `GfxMotionBlur` / `GfxFilmGrain` | Depth of field / motion blur / film grain | Post effects applied regardless of preset. | `off` |
| `GameOptions/GfxOverrideFpsEnabled` / `GfxOverrideFps` | Frame rate override | Manual cap on top of the preset's responsive/idle caps. | `0`, `0` |
| `GfxAvailableOverrideFps` | Available frame caps | `array` of values the UI offers. | `30,40,60` |
| `GfxEnableHighResCharacters` | High-res characters | Streams higher-resolution character textures where installed. | `1` |
| `FixedScreenHeight` | Render height | Internal vertical resolution. | `800` |
| `FullScreen` / `ScreenW` / `ScreenH` | Display | Engine display flags and reported backbuffer. | `1`, `1280×800` |

## Preset labels

The three blocks have no label strings of their own; the UI exposes three mobile presets and
`GfxMobilePreset` selects one. Block names are the best available mapping: **Battery Saver**,
**Graphics**, **Performance**. Confirm by switching preset in-game and diffing the file.

## Open verification

1. Switch each preset, quit, diff: confirm the selector value and whether the game rewrites the
   block on exit.
2. Confirm the allowed values for `GfxShadow`, `GfxSSAO`, `GfxAA` and `GfxAnisotropic`; only one
   device value is observed here.
3. Confirm whether `GfxResolutionScale`, `GfxMinScaling` and `GfxMaxScaling` are doubles the game
   rounds, or fixed-point.
