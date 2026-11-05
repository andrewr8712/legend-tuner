# Alien: Isolation — guided catalogue source

> **Status:** implemented as `AlienIsolationCatalog`; rows below are the shipped controls.

Platform: Android; game version 1.4.3 (`com.feralinteractive.alienisolation_android`), TB520FU dump.
Registry root: `HKEY_CURRENT_USER/Software/Feral Interactive/Alien Isolation/Setup`.
Preset model: `None` — one flat settings block, no preset variants.

**Label and description source.** The APK ships its own menu strings in
`assets/en.lproj/Localizable_Game.strings` (`AlienIsolation.Odev.AI_UI_MENU_*` plus `*_DESC`) and a
shared quality-name vocabulary (`AI_UI_MENU_QUALITY_NAME_*`). Where a label and description are
quoted below they are the game's own English text; `authored` marks text written for this app because
the game ships none.

**Value mapping.** `integer` values. Toggles use `0` = Off, `1` = On, matching the game's
`QUALITY_NAME_OFF` / `QUALITY_NAME_ON` strings. Quality ladders use the game's five quality names in
order: `0` Very Low, `1` Low, `2` Medium, `3` High, `4` Ultra. The ladder order is inferred from the
shared name set; per-setting maximums are clamped by the device specification file, so the editor
offers the ladder but does not assume a setting's current value is its maximum.

## Controls

| Registry key | Label (game) | Description | Type | Observed | Notes |
|---|---|---|---|---|---|
| `VideoSetting_AntiAliasing` | Anti-aliasing | Post-process edge smoothing. Cheaper than MSAA; softens fine edges and thin geometry. | Toggle/level | 0 | `_Visible = 0` on this device: the game hides it, but the value exists. |
| `VideoSetting_ChromaticAberrations` | Chromatic Aberration | "Toggle chromatic aberration on/off". Separates red/blue channels at the frame edges for a lens effect. | Toggle | 1 | Cost is negligible; a taste setting. |
| `VideoSetting_Deep Colour (30bpp)` | Deep Color | "Toggle deep color on/off". Requests a 10-bit swapchain where the panel supports it. | Toggle | 0 | Hidden on this device; unrelated to performance. |
| `VideoSetting_Depth of Field` | Depth of Field | "Toggle depth of field on/off". Blurs out-of-focus depth ranges around the focal plane. | Toggle | 1 | Meaningful GPU cost in close-quarters scenes. |
| `VideoSetting_Field Of View` | Field Of View | "Adjust field of view in game". Widens the rendered view; the game disables it in some modes. | Not exposed | key absent | `_Visible = 1` but the value was never materialised. Do not create it; show a note instead. |
| `VideoSetting_FilmGrainIntensity` | Film Grain Intensity | "Adjust the film grain intensity". Strength of the full-screen grain overlay. | Not exposed | key absent | `_Visible = 1`, value absent — same rule as FOV. |
| `VideoSetting_Level of Detail` | Level of Detail | "Adjust level of detail in game". Distance at which props, debris and set dressing swap to lower-detail meshes. | Quality ladder | 1 | Streams geometry; higher values raise load on storage and CPU. |
| `VideoSetting_MotionBlur` | Motion Blur | "Toggle motion blur on/off". Camera-space blur on fast movement. | Toggle | 0 | Off on this device. |
| `VideoSetting_Particles` | Particles | "Adjust particle effect quality". Density and resolution of steam, sparks, smoke and ambient VFX. | Quality ladder | 0 | Heaviest in the reactor and engineering spaces. |
| `VideoSetting_Planar Reflections` | Planar Reflections | "Toggle planar reflections on/off". Mirrored pass for floors, water and glass. | Toggle | 1 | Doubles draw cost in reflective rooms. |
| `VideoSetting_Screen Space Ambient Occlusion` | SSAO | "Adjust screen space ambient occlusion". Contact shadows in creases and corners. | Toggle/level | 0 | `_Visible = 0` on this device. |
| `VideoSetting_ShadowMapResolution` | Shadow Map Resolution | "Adjust the resolution of shadow maps". Texel density of the shadow atlas. | Quality ladder | 1 | Main shadow cost multiplier. |
| `VideoSetting_ShadowMapping` | Shadow Mapping | "Adjust shadow mapping quality". Filtering and cascade coverage for dynamic shadows. | Quality ladder | 1 | Distinct from map resolution. |
| `VideoSetting_Texture Filtering` | Texture Filtering | "Adjust the texture filtering quality". Anisotropic filtering level for surfaces seen at grazing angles. | Filter list | 2 | Game names: Bilinear, Anisotropic 1x/2x/4x/8x/16x, Box/Gaussian. Index mapping is not verified; expose raw value with the names. |
| `VideoSetting_Vertical Sync` | Vertical Sync | "Toggle V-Sync on/off". Locks presentation to the display refresh; removes tearing, adds latency. | Toggle | 0 | `_Visible = 0` on this device. |
| `VideoSetting_Volumetric Lighting` | Volumetric Lighting | "Toggle volumetric lighting on/off". Light shafts and fog scattering. | Toggle | 0 | Big cost in foggy corridors. |

Hidden resolution controls, present only as `_Visible` flags on this device: `VideoSetting_Full
Screen`, `VideoSetting_FullScreen Resolution 24bpp`, `VideoSetting_FullScreen Resolution 30bpp`,
`VideoSetting_Windowed Resolution`. Their menu text exists ("Screen Size", "Fullscreen Resolution",
"Windowed Resolution"); none of the value keys exists, and Android resolution is driven by
`FixedScreenHeight` instead.

## Engine-level controls (no menu string)

| Registry key | Label | Description | Type | Observed |
|---|---|---|---|---|
| `FixedScreenHeight` | Render height | Internal render height in pixels. The game scales to the panel; lower values raise frame rate. | Dropdown (480–2160 px) | 1080 |
| `FullScreen` | Fullscreen | Engine fullscreen flag. On Android the surface is always full-bleed; kept for parity. | Toggle | 1 |

## Display order

Follow the game's Video menu: Anti-aliasing, Texture Filtering, Shadow Mapping, Shadow Map
Resolution, SSAO, Planar Reflections, Volumetric Lighting, Depth of Field, Motion Blur, Particles,
Chromatic Aberration, Film Grain Intensity, V-Sync, Deep Color, Field Of View, then the engine
render-height control. Group them in the app as **Image quality**, **Effects**, **Display**, matching
the existing GRID Legends editor.

## Open verification

1. Walk the in-game Video menu on the TB520FU and confirm which controls it shows (the `_Visible`
   flags say AA, SSAO, V-Sync, FOV and film grain are hidden).
2. Change one control from each group, quit, and confirm the written key and value.
3. Confirm the quality ladder direction with one known pair (e.g. set Shadow Map Resolution to its
   highest in-game value and read the integer back).
