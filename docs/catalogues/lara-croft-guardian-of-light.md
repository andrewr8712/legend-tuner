# Lara Croft: Guardian of Light — catalogue source

> **Status:** implemented as `LaraGolCatalog`; rows below are the shipped controls.

Platform: Android; game version 1.2.7 (`com.feralinteractive.laracroftgol_android`), TB520FU dump.
Root: `HKEY_CURRENT_USER/Software/Feral Interactive/Lara Croft and the Guardian of Light/Setup`.
Model: `None` — flat integers; `GraphicsPreset` is an integer index.

**Labels and option words are in the APK** (`Localizable_Game.strings`, the Setup dialog strings
`LaraCroftGoL.Override.051xx`). Labels marked ✓ are the game's own; descriptions are authored — the
dialog has no per-setting tooltips.

## Controls

| Key | Label | Description | Observed |
|---|---|---|---|
| `TextureDetail` | Texture detail ✓ | Texture resolution for characters, ruins and ground. | `0` (Low/Medium/High ✓) |
| `BestTextureFilter` | Texture filtering ✓ | Filter mode: Point, Bilinear, Trilinear, 1×–16× anisotropic ✓. | `10` (index mapping unverified) |
| `AntiAliasing` | Antialiasing mode ✓ | Edge smoothing mode; on this engine it is a post-process choice, not MSAA samples. | `0` |
| `EnableShadows` | Enable shadows ✓ | Dynamic shadows from characters, braziers and geometry. | `0` |
| `EnableReflection` | Enable reflections ✓ | Reflections in water and polished stone. | `0` |
| `EnableVSync` | Enable VSync ✓ | Locks presentation to the display refresh. | `0` |
| `EnableFullscreenEffects` | Enable fullscreen effects ✓ | Full-screen post effects: colour grade, bloom and similar passes. | `1` |
| `MaxFramesPerSecond` | Frame rate cap | Engine cap; the dynamic resolution target is separate. | `60` |
| `DRSFPSTarget` | Dynamic resolution target | Frame rate the dynamic resolution scaler aims for. | `120` |
| `GraphicsPreset` | Quality preset | Integer index written by the preset picker; the individual keys are what the renderer reads. | `0` |
| `FixedScreenHeight` | Render height | Internal vertical resolution. | `1080` |
| `ScreenW` / `ScreenH` | Display | Reported backbuffer size. | `1728 × 1080` |

## Menu entries without a stored key on this device

The Setup dialog also exposes **Enable Depth of Field**, **Enable Volumetric Effects**, **Enable
Water Effects** and **Enable Triple Buffering** (✓), but no matching values exist in this Android
file — the port likely hard-codes them or writes them only when toggled. Do not render controls for
keys that are absent.

## Notes

- `Enable*` keys are `0`/`1`.
- The 1,895 values in the file are mostly not graphics: saves, unlocks and per-level state live in
  other subtrees. The guided catalogue must stay scoped to `Setup`.
- `GraphicsPreset` values are unverified beyond `0`; the preset table needs one in-game switch.

## Open verification

1. Cycle `TextureDetail` and `AntiAliasing` in-game and read the integers back — that fixes both
   ladders.
2. Cycle `BestTextureFilter` to bind `10` to one of the named modes.
3. Switch the in-game preset and diff to confirm which keys change.
