# NAPOLEON: Total War — guided catalogue source

> **Status:** implemented as `NapoleonCatalog`; rows below are the shipped controls.

Platform: Android; game version 1.3.4 (`com.feralinteractive.napoleon_android`), TB520FU dump.
Root: `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Napoleon Total War - Mobile/Setup`.
Settings block: `…/Setup/GraphicsSettings` (26 integers).
Model: `None` — flat, no preset blocks; `Setup/GraphicsQuality` is a quality index.

**Labels.** The APK ships generic Audio & Graphics tooltips
(`Napoleon.Fdev.Tooltips_Frontend_AudioGraphics_*`) but not per-quality labels for the 17 battle
settings; those are authored from the key. The game does ship the option words
(`Napoleon.Fdev.AND.Options_*`: Low, Medium, High, Ultra).

## GraphicsSettings

| Key | Label | Description | Range / observed |
|---|---|---|---|
| `ShadowsQuality` | Shadows | Shadow map resolution and filtering for units, buildings and terrain. The single biggest battle-frame cost after unit count. | 0–3 · `2` |
| `SoftShadows` | Soft shadows | Blurs shadow edges; slightly wider filter, small extra cost. | 0/1 · `0` |
| `TextureQuality` | Texture quality | Texture resolution for units, buildings and terrain. Higher tiers need more VRAM and bandwidth. | 0–3 · `2` |
| `TextureFiltering` | Texture filtering | Anisotropic filtering level for ground and unit textures seen at an angle. | 0–3 · `2` |
| `AntiAliasing` | Anti-aliasing | MSAA on geometric edges. Expensive on a tablet GPU when large formations are on screen. | 0–x · `0` |
| `UnitDetail` | Unit detail | Soldier mesh detail, LOD distances and animation quality. Scales with the number of men on the field. | 0–3 · `2` |
| `BuildingDetail` | Building detail | Settlement and fortification mesh detail on the battle map. | 0–3 · `2` |
| `ParticleEffects` | Particle effects | Smoke, musket fire, cannon impacts and dust. The game's own tooltip: "Higher settings may negatively impact performance, especially in large battles." | 0–3 · `3` |
| `GrassQuality` | Grass quality | Ground cover density and draw distance. | 0–3 · `2` |
| `TreesQuality` | Tree quality | Tree LOD and billboard distance. | 0–3 · `2` |
| `WaterQuality` | Water quality | Sea and river shading, reflections and wave detail. | 0–3 · `2` |
| `SkyQuality` | Sky quality | Cloud and atmosphere quality; mostly visible in battle lighting. | 0–3 · `1` |
| `TerrainQuality` | Terrain quality | Ground mesh resolution and blending layers. | 0–3 · `0` |
| `ShipQuality` | Ship detail | Naval unit mesh and sail detail. | 0–3 · `0` |
| `HDR` | HDR | High-dynamic-range lighting and tone mapping. | 0/1 · `1` |
| `Gamma` | Gamma | Brightness/gamma, stored as a double. The wrapper's tooltip: "Set light/dark balance level." | double · `2.000000` |
| `gfx_dead_unit_render_time` | Dead unit render time | Seconds corpses stay on the field. Not a quality setting; a battle-load control. | int · `3` |

## Setup (driving the graphics menu)

| Key | Label | Description | Observed |
|---|---|---|---|
| `GraphicsQuality` | Quality preset | Index written by the preset picker; individual GraphicsSettings values are what the engine reads. | `0` |
| `GameResolutionMultiplier` | Resolution multiplier | Internal render scale relative to the panel. | `1.000000` |
| `FixedScreenHeight` | Render height | Internal vertical resolution. | `1080` |
| `FramerateManager*` keys | Frame-rate manager | Idle/responsive caps for campaign and battle, with `_Battery` variants for the battery-saver mode. | `30`–`60` |
| `HideSystemUIInFullscreen` | Immersive mode | Hides Android system bars in fullscreen. | `1` |

## Tooltips the game does ship

- Battery saver: "Limit performance to increase battery life."
- Depth of field: "Enable depth of field effects which some players may find visually
  uncomfortable."
- Distortion effects: "Enable distortion effects which some players may find visually
  uncomfortable."
- Brightness: "Set game brightness level." Gamma: "Set light/dark balance level."

## Open verification

1. Move `GraphicsQuality` through its range and read back which `GraphicsSettings` values change —
   that gives the preset table and the true index range.
2. Confirm whether `AntiAliasing` is a toggle or a level on mobile.
3. Confirm the `0–3` upper bound per key; only one device value is observed.
