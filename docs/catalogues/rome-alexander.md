# ROME: Alexander

**Package:** `com.feralinteractive.rometwalex`  
**Game version:** v2.0.6RC2  
**Registry root:** `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Rome Total War - Alexander/Setup`  
**Engine block:** `HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config`  
**Stored presets:** `HKEY_CURRENT_USER/RomeGraphicsPresets/{Low,Medium,High,Highest}`

This title uses the same catalogue and registry shape as **ROME: Total War** and **ROME: Barbarian
Invasion**, with its own package, root and game build. The 24-control inventory, option vocabulary and
caveats are documented in [rome-total-war.md](rome-total-war.md).

## Notes specific to this build

- Live resolution: **1728 × 1080**
- Engine, battle and campaign caps: **60 FPS**; menu cap: **30 FPS**
- `GraphicsPreset` is empty and `PreviousGraphicsPreset` is `Highest`, so the stored
  `RomeGraphicsPresets` blocks are not exposed as tabs.
- The live `feral_*` values are exposed instead; Raw XML remains available for the preset blocks.
- Analytics ID, GPU identity and the session buffer were sanitised in the fixture.

## Open verification

- Whether Alexander shares ROME's active-preset/live-value rewrite behaviour.
- `feral_aa_quality` mapping and the full building-quality range.
