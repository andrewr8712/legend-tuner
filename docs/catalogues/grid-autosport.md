# GRID Autosport — guided catalogue source

Platform: Android 1.10.6 (`com.feralinteractive.gridas`), TB520FU dump.
Registry root: `HKEY_CURRENT_USER/Software/Feral Interactive/GRID Autosport/Setup`.
Preset model: `Prefixed` — the graphics set exists three times, plus two hint keys.

**What the artifacts provide.** The APK's `assets/en.lproj/Localizable_Game.strings` names the three
in-game graphics presets:

| Key | Text |
|---|---|
| `GridAutosport.AND.lng_feral_graphic_normal` | GRAPHICS |
| `GridAutosport.AND.lng_feral_graphic_high` | PERFORMANCE |
| `GridAutosport.AND.lng_feral_graphic_high2` | PERFORMANCE PLUS |

The per-option menu text is inside the encrypted OBB (`main.…obb` is not a zip), so labels below are
derived from the registry key names, and descriptions are **authored** for this app. Option tokens
and the value families are observed from the device file, so they are verified.

**Value families.**

| Prefix | Keys | Preset name key | Observed |
|---|---|---|---|
| `gfxconfig_` | 30 | `gfxconfig_preset_name = custom` | live values |
| `gfxconfig_high_` | 30 | — | 60 fps cap |
| `gfxconfigpowersaver_` | 31 | `gfxconfigpowersaver_preset_name = custom` | 30 fps cap |
| `gfxconfig_default_` | 2 | `gfxconfig_default_preset_name = ultralow` | hints, not a preset |

The mapping from the three internal prefixes to the three in-game preset labels is **not yet
verified**: a plausible reading is `gfxconfig_` = GRAPHICS (live), `gfxconfig_high_` = PERFORMANCE,
`gfxconfigpowersaver_` = PERFORMANCE PLUS, but that needs one in-game preset switch and a file diff
before the labels ship.

**Option vocabularies observed across the families:** `off`/`on`; `ultralow`, `low`, `medium`,
`high`, `ultra`; `off`, `4xmsaa`, `8xmsaa`; `custom`. `gfxconfig_max_fps` is an integer (`30`, `60`).

## Controls

| Key (live) | Label | Description | Options | Notes |
|---|---|---|---|---|
| `gfxconfig_shadows` | Shadows | Dynamic shadow maps for cars, trackside furniture and the crowd. Higher tiers raise map resolution, cascade distance and the number of casters. | Ultra Low → Ultra | Major CPU and GPU cost on full grids. |
| `gfxconfig_shaders` | Shaders | Shading tier for track and car materials: surface response, specular detail and lighting model complexity. | Ultra Low → Ultra | Affects every lit pixel. |
| `gfxconfig_textures` | Textures | Texture resolution and mip selection for tracks, cars and props. | Ultra Low → Ultra | High needs the HQ texture packs; costs VRAM and bandwidth. |
| `gfxconfig_particles` | Particles | Density and softness of dust, smoke, sparks and tyre effects. | Ultra Low → Ultra | Spikes at race starts and collisions. |
| `gfxconfig_crowd` | Crowd | Spectator count, animation rate and draw distance. | Ultra Low → Ultra | CPU-heavy on grandstands; low tier thins the crowd. |
| `gfxconfig_vehicles` | Vehicles | Opponent car mesh detail, materials and LOD bias. | Ultra Low → Ultra | Scales with grid size. |
| `gfxconfig_trees` | Trees | Track-side vegetation density, LOD and billboarding distance. | Ultra Low → Ultra | Alpha-tested foliage is fill-rate sensitive. |
| `gfxconfig_objects` | Objects | Props, barriers, pit equipment and trackside clutter. | Ultra Low → Ultra | Draw-call heavy in pit lanes. |
| `gfxconfig_track` | Track detail | Road surface decals, markings and track-side detail layers. | Ultra Low → Ultra | Small cost, visible at speed. |
| `gfxconfig_water` | Water | Water shading, reflections and tessellation on wet circuits. | Ultra Low → Ultra | Only wet races. |
| `gfxconfig_groundCover` | Ground cover | Grass, gravel scatter and small ground clutter. | Ultra Low → Ultra | Cheap at low tiers; fills gaps between track and scenery. |
| `gfxconfig_mirrors` | Mirrors | Planar mirror pass for the rear-view mirrors. | Ultra Low → Ultra | Renders the scene a second time. |
| `gfxconfig_car_reflection` | Car reflections | Environment reflection resolution and update rate on car paint. | Ultra Low → Ultra | Visible on every car in frame. |
| `gfxconfig_cloth` | Cloth | Flags, banners and soft-body cloth shading and simulation detail. | Ultra Low → Ultra | Minor unless many flags are on screen. |
| `gfxconfig_skidmarks` | Skidmarks | Rubber marks left on the road and how long they persist. | Off / On | Persistent decals cost memory. |
| `gfxconfig_night_lighting` | Night lighting | Lightmap and headlight quality for night races. | Ultra Low → Ultra | No effect in daylight. |
| `gfxconfig_global_illumination` | Global illumination | Indirect light from track and sky, softening shadowed areas. | Off / On | Medium GPU cost, large visual difference. |
| `gfxconfig_advanced_lighting` | Advanced lighting | Extra per-light passes for headlights, brake lights and trackside lamps. | Off / On | Cost grows with the number of lights. |
| `gfxconfig_advanced_fog` | Advanced fog | Layered distance fog and atmospheric scattering. | Off / On | Cheap, but washes out distance at low tiers. |
| `gfxconfig_dynamic_ambient_occ` | Ambient occlusion | Screen-space contact shadows in creases and under cars. | Ultra Low → Ultra | Noticeable on car undersides and kerbs. |
| `gfxconfig_dynamic_ambient_occ_soft` | Soft ambient occlusion | Blurs the AO result for a softer, wider contact shadow. | Off / On | Small extra cost over AO. |
| `gfxconfig_intel_aoit` | Intel AOIT | Order-independent transparency for foliage and particles on supported GPUs. | Off / On | Vendor-specific; ignored elsewhere. |
| `gfxconfig_avsm` | AVSM | Adaptive volumetric shadow maps for particle shadows. | Off / On | Expensive; off by default on this device. |
| `gfxconfig_anisotropic_filtering` | Anisotropic filtering | Texture sharpness on surfaces viewed at a grazing angle, such as the road ahead. | Ultra Low → Ultra | Cheap; usually worth raising first. |
| `gfxconfig_multisampling` | Anti-aliasing | MSAA sample count for geometric edges. | Off / 4× / 8× | 8× is a large bandwidth cost on a tablet. |
| `gfxconfig_max_fps` | Frame rate limit | Cap applied by the graphics preset. | 30 / 60 | Integer value; the live set is separate from the two preset sets. |
| `gfxconfig_ai_count` | AI count | Density of AI-car visual and audio updates. | Ultra Low → Ultra | Semantics are the least certain row here; verify in-game before exposing a friendly label. |
| `gfxconfig_voice_count` | Voice count | Maximum simultaneous audio voices for engines, tyres and ambience. | Ultra Low → Ultra | Audio rather than rendering cost. |
| `gfxconfig_second_screen` | Second screen | Detail level for the second-screen view. | Ultra Low → Ultra | No effect in single-screen use. |
| `gfxconfig_preset_name` | Preset | Name the game records for the current set (`custom` after edits). | read-only | Not a control; shown as the active preset. |

## Display and frame rate

| Key | Label | Description | Type | Observed |
|---|---|---|---|---|
| `MaxFramesPerSecond` | Frame rate cap (engine) | Engine-level cap; the graphics presets write `gfxconfig_max_fps`. | Stepper | 0 (unset) |
| `FullScreen` | Fullscreen | Engine fullscreen flag; Android is always full-bleed. | Toggle | 1 |
| `ScreenW` / `ScreenH` | Resolution | Reported backbuffer size. | read-only | 1920 × 1080 |
| `FeralAudioDynamicRange` | Dynamic range | Audio dynamic range preset (`high`, `low`). | Select | high |
| `FeralAudioSystemEQ` | Speaker EQ | Output equalisation profile (`tv`, `headphones`, …). | Select | flat |

## Grouping

- **Image quality:** shadows, shaders, textures, anti-aliasing, anisotropic filtering, car
  reflections, mirrors, ambient occlusion, soft AO, AVSM, Intel AOIT.
- **Detail:** vehicles, trees, objects, track, crowd, ground cover, water, cloth, skidmarks.
- **Lighting & atmosphere:** global illumination, advanced lighting, advanced fog, night lighting.
- **Performance:** frame rate limit, AI count, voice count, second screen.

## Open verification

1. Switch each in-game preset (GRAPHICS / PERFORMANCE / PERFORMANCE PLUS) in turn, quit, and diff
   `preferences` to bind labels to prefixes. Until then, expose the sets as **Current / High / Power
   saver**.
2. Confirm which of `ai_count`, `voice_count` and `second_screen` the in-game menu actually exposes;
   if it does not, leave them in All values only.
3. Change one control per group, quit, and confirm the key and token that changed.
