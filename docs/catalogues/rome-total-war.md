# ROME: Total War

**Package:** `com.feralinteractive.rometw`  
**Game version:** v2.0.6RC3  
**Registry root:** `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Rome Total War/Setup`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`  
**Stored presets:** `HKEY_CURRENT_USER/RomeGraphicsPresets/{Low,Medium,High,Highest}`

## Control inventory

| Group | Control | Key | Type | Observed | Options |
|---|---|---|---|---|---|
| Display | Screen resolution | `ScreenW` + `ScreenH` | integer pair | 1728 × 1080 | common pairs |
| Display | Fullscreen | `FullScreen` | integer | 1 | On/Off |
| Display | Render under notch | `RenderUnderNotch` | integer | 1 | On/Off |
| Display | Render height | `FixedScreenHeight` | integer | 1080 | 480–2160 px list |
| Frame rate | Engine frame cap | `MaxFramesPerSecond` | integer | 60 | 24–144 fps |
| Frame rate | Battle frame cap | `BattleMaxFramesPerSecond` | integer | 60 | 24–144 fps |
| Frame rate | Campaign frame cap | `CampaignMaxFramesPerSecond` | integer | 60 | 24–144 fps |
| Frame rate | Menu frame cap | `MainMenuFPS` | integer | 30 | 24–144 fps |
| Battle quality | Anti-aliasing | `feral_aa_quality` | integer | 0 | 0–3, mapping provisional |
| Battle quality | Battle shadows | `feral_battle_map_shadows` | integer | 1 | On/Off |
| Battle quality | Campaign shadows | `feral_campaign_map_shadows` | integer | 1 | On/Off |
| Battle quality | Detailed unit shadows | `feral_detailed_unit_shadows` | integer | 1 | On/Off |
| Battle quality | Building detail | `feral_building_quality` | integer | 5 | 0–5 tiers |
| Battle quality | Effect quality | `feral_effect_quality` | integer | 4 | 0–4 tiers |
| Battle quality | Terrain quality | `feral_terrain_quality` | integer | 3 | 0–3 tiers |
| Battle quality | Vegetation quality | `feral_vegetation_quality` | integer | 3 | 0–3 tiers |
| Battle quality | Unit quality | `feral_unit_quality` | integer | 3 | 0–3 tiers |
| Battle quality | Unit size | `feral_unit_size` | integer | 80 | 80 / 160 men |
| Battle quality | Massive units | `feral_unit_size_massive_enabled` | integer | 0 | On/Off |
| Battle quality | Unit shaders | `feral_use_unit_shaders` | integer | 1 | On/Off |
| Campaign quality | Grass quality | `feral_grass_quality` | integer | 75 | Off / 25 / 50 / 75 / 100% |
| Campaign quality | Campaign terrain density | `feral_campaign_terrain_pixel_density` | integer | 23 | Standard 15 / High 23 |
| Campaign quality | Smoke and dust | `feral_smoke_dust` | integer | 1 | On/Off |
| Campaign quality | Show AI movements | `feral_show_ai_movements` | integer | 1 | On/Off |

## Notes

- The `feral_*` keys at `Setup` are the live values. The game also stores four complete preset
  blocks under `RomeGraphicsPresets`; those blocks are intentionally not exposed as editor tabs
  until their selector/rewrite behaviour is verified.
- `GraphicsPreset` is empty in this dump and `PreviousGraphicsPreset` is `Highest`; neither is used
  as an active-tab inference.
- `feral_grass_quantity_modifier` is a binary double and is not exposed as an integer control.
- `FPSCapMode` is `dynamic`; the four numeric caps are exposed separately.

## Open verification

- The `feral_aa_quality` integer-to-mode mapping.
- Whether editing the live `Setup` values is enough, or whether the active preset block must also be
  rewritten when the game exits.
- Whether `feral_building_quality` uses the full 0–5 range in the UI.
