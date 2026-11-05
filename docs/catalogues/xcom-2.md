# XCOM 2 Collection

**Package:** `com.feralinteractive.xcom2_android`  
**Game version:** v1.6.1RC6  
**Registry root:** `HKEY_CURRENT_USER/Software/Feral Interactive/XCOM 2/Setup`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`

## Control inventory

| Group | Control | Key | Type | Observed | Options |
|---|---|---|---|---|---|
| Display | Screen resolution | `ScreenW` + `ScreenH` | integer pair | 1728 × 1080 | common pairs |
| Display | Fullscreen | `FullScreen` | integer | 1 | On/Off |
| Display | Render under notch | `RenderUnderNotch` | integer | 1 | On/Off |
| Display | Render height | `FixedScreenHeight` | integer | 1080 | 480–2160 px |
| Display | Resolution scale | `GameResolutionScaleNumerator` | integer | 10000 | 50 / 75 / 100 % |
| Frame rate | Engine frame cap | `MaxFramesPerSecond` | integer | 60 | 24–144 fps |
| Frame rate | Idle frame cap | `IdleFPS` | integer | 30 | 24–144 fps |
| Frame rate | Responsive frame cap | `NonIdleFPS` | integer | 40 | 24–144 fps |
| Frame rate | Battery-saver idle cap | `IdleFPS_battery` | integer | 20 | 24–144 fps |
| Frame rate | Battery-saver responsive cap | `NonIdleFPS_battery` | integer | 30 | 24–144 fps |
| Image quality | Ambient occlusion | `GFXAmbientOcclusion` | integer | 0 | On/Off |
| Image quality | Bloom | `GFXBloom` | integer | 1 | On/Off |
| Image quality | Decals | `GFXDecals` | integer | 0 | Low / Medium / High |
| Image quality | Depth of field | `GFXDepthOfField` | integer | 0 | On/Off |
| Image quality | Dirty lens | `GFXDirtyLens` | integer | 1 | On/Off |
| Image quality | Effects quality | `GFXEffects` | integer | 0 | Low / Medium / High |
| Image quality | FXAA | `GFXFXAA` | integer | 1 | On/Off |
| Image quality | Draw distance | `GFXMaxDrawDistance` | integer | 0 | Low / Medium / High |
| Image quality | Screen-space reflections | `GFXScreenSpaceReflections` | integer | 0 | On/Off |
| Image quality | Shadow quality | `GFXShadowQuality` | integer | 0 | 0–3 tiers |
| Image quality | Soft shadows | `GFXShadowType` | integer | 0 | On/Off |
| Image quality | Subsurface scattering | `GFXSubsurfaceScattering` | integer | 0 | On/Off |
| Image quality | Texture detail | `GFXTextureDetail` | integer | 0 | Low–Ultra |
| Image quality | Texture filtering | `GFXTextureFiltering` | integer | 0 | 0–4 levels |
| Image quality | Distortion | `DisableDistortion` | integer | 0 | On/Off, stored inverted |

## Notes

- The live `GFX*` keys are exposed. The file also contains `_battery` and `_graphics` copies of the
  quality family; those preset blocks are not exposed as tabs because `GraphicsMode = 1` has not been
  mapped to a named mode in this build. Raw XML remains available for the unused blocks.
- `DisableDistortion` is inverted: the editor shows **Distortion On** when the stored value is `0`.
- `GameResolutionScaleNumerator` is written against the observed denominator of `10000`; the
  denominator is left untouched.

## Open verification

- `GraphicsMode` values and whether editing live `GFX*` values is enough for the game's active mode.
- Tier vocabulary for `GFXShadowQuality`, `GFXTextureFiltering` and `GFXMaxDrawDistance`.
- Whether disabling `GFXFXAA` is preferable to another AA path on this build.
