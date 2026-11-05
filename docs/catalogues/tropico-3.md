# Tropico 3

**Package:** `com.feralinteractive.tropico3`  
**Game version:** v1.4.4 (serial `Tropico v1.4.4`)  
**Android config:** `HKEY_CURRENT_USER/Software/Haemimont Games/Tropico 3: Gold Edition/Setup`  
**Graphics block:** `.../Setup/GFX`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`

## Control inventory

| Group | Control | Key | Path | Type | Observed | Options |
|---|---|---|---|---|---|---|
| Display | Screen resolution | `ScreenW` + `ScreenH` | Setup | integer pair | 1914 × 1080 | common 16:9 / 16:10 pairs |
| Display | Fullscreen | `FullScreen` | Setup | integer | 1 | On/Off |
| Display | Render under notch | `RenderUnderNotch` | Setup | integer | 1 | On/Off |
| Display | Render height | `FixedScreenHeight` | Setup | integer | 1080 | 480–2160 px list |
| Graphics | Scene resolution | `SceneRez` | GFX | string | High | Off / Low / Medium / High |
| Graphics | Shadows | `ShadowQuality` | GFX | string | High | Off / Low / Medium / High |
| Graphics | Texture quality | `TextureLevel` | GFX | string | High | Off / Low / Medium / High |
| Graphics | Water quality | `WaterQuality` | GFX | string | High | Off / Low / Medium / High |
| Graphics | Terrain quality | `TerrainQuality` | GFX | string | Low | Off / Low / Medium / High |
| Graphics | Grass quality | `GrassQuality` | GFX | string | Off | Off / Low / Medium / High |
| Graphics | View distance | `ViewDistance` | GFX | string | High | Off / Low / Medium / High |
| Graphics | Anisotropic filtering | `Anisotropy` | GFX | string | Off | Off / 2× / 4× / 8× / 16× |
| Graphics | Post-processing | `PostProcessing` | GFX | string | Off | Off / Low / Medium / High |
| Performance | Engine frame cap | `MaxFramesPerSecond` | IndirectX/Direct3D/Config | integer | 30 | 24–144 fps |
| Performance | Battery saver cap | `BatterySaverFPS` | Setup | integer | 30 | 24–144 fps |

## Notes

- `ScreenW`/`ScreenH` are present in the live file and are edited as one resolution control.
- The quality keys are strings, not integer tiers. The option vocabulary above is the observed
  `Off`/`Low`/`Medium`/`High` vocabulary; the 2×–16× anisotropy tokens are inferred from the stored
  `Off` value and the usual Feral filtering ladder.
- `BatterySaverFPS` and `MaxFramesPerSecond` are separate controls: the latter lives in the engine
  subtree and is not part of `Setup`.
- Not exposed: `ArchitectMode*`, `FoliageDensity`/`FoliageLODMultiplier` (floating-point strings),
  `AllowSendCrashReports`, `AllowSendUsageData`, and the specification/update URL values.

## Open verification

- Whether the game accepts the `2×`–`16×` anisotropy tokens in this build.
- Whether `BatterySaverFPS` is a hard cap or only advisory while battery saver is active.
