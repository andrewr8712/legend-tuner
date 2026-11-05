# Total War: EMPIRE

**Package:** `com.feralinteractive.empire_android`  
**Game version:** v1.7.1 (serial `EMPIRE v1.7.1`)  
**Registry root:** `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Total War EMPIRE/Setup`  
**Quality block:** `.../Setup/GraphicsSettings`  
**Menu block:** `.../Setup/MainMenuSettings`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`

## Control inventory

| Group | Control | Key | Path | Type | Observed | Options |
|---|---|---|---|---|---|---|
| Battle quality | Shadows | `ShadowsQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Texture quality | `TextureQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Texture filtering | `TextureFiltering` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Unit detail | `UnitDetail` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Building detail | `BuildingDetail` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Particle effects | `ParticleEffects` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Grass quality | `GrassQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Tree quality | `TreesQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Water quality | `WaterQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Sky quality | `SkyQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Terrain quality | `TerrainQuality` | GraphicsSettings | integer | 1 | 0–3 tiers |
| Battle quality | Ship detail | `ShipQuality` | GraphicsSettings | integer | 2 | 0–3 tiers |
| Battle quality | Anti-aliasing | `AntiAliasing` | GraphicsSettings | integer | 0 | 0–3, mapping provisional |
| Battle quality | Soft shadows | `SoftShadows` | GraphicsSettings | integer | 0 | On/Off |
| Battle quality | HDR | `HDR` | GraphicsSettings | integer | 1 | On/Off |
| Battle quality | Ambient occlusion | `SSAO` | GraphicsSettings | integer | 0 | On/Off |
| Battle quality | Depth of field | `DepthOfField` | GraphicsSettings | integer | 0 | On/Off |
| Battle quality | Distortion | `Distortion` | GraphicsSettings | integer | 1 | On/Off |
| Battle quality | Volumetric effects | `VolumetricEffects` | GraphicsSettings | integer | 1 | On/Off |
| Display | Fullscreen | `FullScreen` | Setup | integer | 1 | On/Off |
| Display | Render under notch | `RenderUnderNotch` | Setup | integer | 1 | On/Off |
| Display | Render height | `FixedScreenHeight` | Setup | integer | 1080 | 480–2160 px list |
| Display | Resolution scale | `GameResolutionMultiplier` | Setup | double | 1.000000 | 50 / 75 / 100 % |
| Display | Graphics preset | `GraphicsQuality` | Setup | integer | 0 | Very Low–Ultra, mapping inferred |
| Frame rate | Dynamic frame-rate manager | `EnableFramerateManager` | IndirectX/Direct3D/Config | integer | 1 | On/Off |
| Frame rate | Battle frame cap | `FramerateManagerBattleResponsiveFPS` | IndirectX/Direct3D/Config | integer | 30 | 24–144 fps |
| Frame rate | Campaign frame cap | `FramerateManagerCampaignResponsiveFPS` | IndirectX/Direct3D/Config | integer | 30 | 24–144 fps |
| Frame rate | Battery saver | `BatterySaver` | MainMenuSettings | integer | 0 | On/Off |

## Notes

- `ScreenW`/`ScreenH` appear in `AutoValueRemap` but **are not present** in the first-launch
  `Setup` block. The editor therefore exposes the observed `FixedScreenHeight` and resolution scale,
  not a fabricated resolution pair.
- The quality controls mirror NAPOLEON's `GraphicsSettings` shape. The 0–3 tier labels are inferred;
  the stored values are verified.
- `AntiAliasing` is a level control here, not a plain toggle; this mapping is marked provisional.
- `BatterySaver` lives in `MainMenuSettings`, not under the graphics block.

## Open verification

- The `GraphicsQuality` index-to-preset mapping and whether changing it rewrites the individual
  quality keys.
- The complete `AntiAliasing` vocabulary and the `0–3` tier label order.
- Whether `ScreenW`/`ScreenH` appear after a display mode change on this build.
