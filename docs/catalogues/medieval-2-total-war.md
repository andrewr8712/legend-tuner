# Medieval II: Total War

**Package:** `com.feralinteractive.medieval2_android`  
**Game version:** v1.5.3RC2  
**Registry root:** `HKEY_CURRENT_USER/Software/Original Developer/Medieval II Total War/Setup`  
**Video block:** `.../Setup/VideoSettings`

## Control inventory

| Group | Control | Key | Path | Type | Observed | Options |
|---|---|---|---|---|---|---|
| Display | Screen resolution | `ScreenW` + `ScreenH` | Setup | integer pair | 1728 × 1080 | common pairs |
| Display | Fullscreen | `FullScreen` | Setup | integer | 1 | On/Off |
| Display | Render under notch | `RenderUnderNotch` | Setup | integer | 1 | On/Off |
| Display | Render height | `FixedScreenHeight` | Setup | integer | 1080 | 480–2160 px list |
| Frame rate | Idle frame cap | `IdleFPS` | Setup | integer | 33 | 24–144 / 33 fps |
| Frame rate | Responsive frame cap | `ResponsiveFPS` | Setup | integer | 60 | 24–144 fps |
| Frame rate | Battery-saver idle cap | `IdleFPS_battery` | Setup | integer | 20 | 24–144 fps |
| Frame rate | Battery-saver responsive cap | `ResponsiveFPS_battery` | Setup | integer | 30 | 24–144 fps |
| Battle quality | Anti-aliasing | `Antialiasing` | VideoSettings | integer | 0 | 0–3, mapping provisional |
| Battle quality | Bloom | `Bloom` | VideoSettings | integer | 1 | On/Off |
| Battle quality | Building detail | `BuildingDetail` | VideoSettings | string | high | low / medium / high |
| Battle quality | Campaign shadows | `CampaignShadows` | VideoSettings | integer | 1 | On/Off |
| Battle quality | Effect quality | `EffectsDetail` | VideoSettings | string | medium | low / medium / high |
| Battle quality | Unit size | `feral_unit_size` | VideoSettings | integer | 2 | Small / Normal / Large |
| Battle quality | Graphics preset | `GraphicalQuality` | VideoSettings | string | medium | low / medium / high |
| Battle quality | Grass quality | `GrassDetail` | VideoSettings | string | medium | low / medium / high |
| Battle quality | Reflections | `Reflections` | VideoSettings | integer | 1 | On/Off |
| Battle quality | Shader version | `ShaderVersion` | VideoSettings | integer | 2 | Model 1–3, range unverified |
| Battle quality | Shadows | `ShadowQuality` | VideoSettings | string | high | low / medium / high |
| Battle quality | Terrain quality | `TerrainQuality` | VideoSettings | string | medium | low / medium / high |
| Battle quality | Texture filtering | `TextureFiltering` | VideoSettings | integer | 2 | 0–4 index, order unverified |
| Battle quality | Texture quality | `TextureQuality` | VideoSettings | string | high | low / medium / high |
| Battle quality | Unit detail | `UnitDetail` | VideoSettings | string | high | low / medium / high |
| Battle quality | Vegetation quality | `VegetationDetail` | VideoSettings | string | high | low / medium / high |

## Notes

- Medieval II stores quality names as lowercase strings (`low`, `medium`, `high`), not integers.
- `GraphicalQuality` is a preset name in the live `VideoSettings` block; there are no separate
  preset blocks. Editing it alone may not rewrite the individual quality values.
- `feral_unit_size` is an index, not the number of men; the Small/Normal/Large labels are inferred.
- `ShaderVersion` and `TextureFiltering` are exposed conservatively; only index 2 is observed.

## Open verification

- Whether changing `GraphicalQuality` should also rewrite the individual quality values.
- The accepted `ShaderVersion` and `TextureFiltering` ranges.
- The `Antialiasing` integer-to-mode mapping.
