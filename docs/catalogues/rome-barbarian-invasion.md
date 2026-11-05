# ROME: Barbarian Invasion

**Package:** `com.feralinteractive.rometwbi`  
**Game version:** v2.0.6RC2  
**Registry root:** `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Rome Total War - Barbarian Invasion/Setup`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`  
**Stored presets:** `HKEY_CURRENT_USER/RomeGraphicsPresets/{Low,Medium,High,Highest}`

This title uses the same catalogue and registry shape as **ROME: Total War**, with its own package,
root and game build. The control inventory, option vocabulary and caveats are therefore identical to
[rome-total-war.md](rome-total-war.md):

- 24 guided controls
- Paired `ScreenW`/`ScreenH` resolution, fullscreen, render under notch and render height
- Engine, battle, campaign and menu frame caps
- Anti-aliasing, shadow, building, effect, terrain, vegetation, unit, grass and campaign settings
- Live `feral_*` values are exposed; the `RomeGraphicsPresets` blocks are not exposed as tabs because
  `GraphicsPreset` is empty and the preset/live rewrite behaviour is unverified

## Notes specific to this build

- `ScreenW`/`ScreenH` are present in the live file: **1728 × 1080**.
- The responsive frame cap is **60 FPS**; the engine cap is **60 FPS**.
- `BufferedPacketData`, the analytics install ID and the GPU identity string were sanitised in the
  committed fixture.

## Open verification

- Whether the same active-preset/live-value rewrite issue as ROME applies to Barbarian Invasion.
- `feral_aa_quality` mapping and the full building-quality range.
