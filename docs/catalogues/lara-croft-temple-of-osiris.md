# Lara Croft and the Temple of Osiris — catalogue source

> **Status:** implemented as `LaraTooCatalog`; rows below are the shipped controls.

Platform: Android; game version 1.2.3 (`com.feralinteractive.laracrofttoo_android`), TB520FU dump.
Root: `HKEY_CURRENT_USER/Software/Feral Interactive/Lara Croft and the Temple of Osiris/Setup`.
Model: `None` — flat integers, no preset blocks.

**Labels are in the APK** (`Localizable_Game.strings`,
`Odev.gamecontent_menu_interface_pcmenu_displaysettingssubmenu_*`). The port also ships two real
tooltips (`Fdev.DisplaySettings_Tooltip_*`), quoted below. Unmarked descriptions are authored.

## Controls

| Key | Label | Description | Observed |
|---|---|---|---|
| `ShadowQuality` | Shadows ✓ | Shadow map resolution and filtering for characters, tombs and braziers. | `3` |
| `ReflectionQuality` | Reflections ✓ | Planar and screen-space reflections on water and polished floors. | `1` |
| `AmbientOcclusionQuality` | Ambient occlusion | Contact shadows where geometry meets; softens the look of stone joints and rubble. | `1` |
| `DOFQuality` | Depth of field | Blurs distant or foreground depth ranges for a cinematic look; some players find it uncomfortable. | `1` |
| `SSAAQuality` | Anti-aliasing ✓ (mapping provisional) | Supersampling level. The menu string is "ANTI ALIASING"; the only AA-shaped key is `SSAAQuality`. Verify before labelling it in the UI. | `1` |
| `TextureQuality` | Texture detail ✓ | Texture resolution for environment and characters. | `2` |
| `TextureFiltering` | Texture filtering ✓ | Point, Bilinear, Trilinear or 1×–16× anisotropic ✓. | `4` |
| `VSync` | VSync ✓ | Locks presentation to the display refresh; removes tearing, adds latency. | `0` |
| `MaxFramesPerSecond` | Frame rate limiter | Game tooltip: "Determines the highest possible FPS. Set a lower FPS limit for a more stable experience. FPS limit relies on Vsync Interval, therefore some values may not be compatible with your screen's refresh rate." | `60` |
| `EnableAdvancedGraphicsOption` | Advanced options | Game tooltip: "Show additional options which may impact performance." Mirrors the app's Advanced switch. | `0` |
| `FixedScreenHeight` | Render height | Internal vertical resolution. | `1080` |
| `FullScreen` | Fullscreen | Engine fullscreen flag; Android is always full-bleed. | `1` |

## Menu entries with no stored value here

The dialog also exposes **Fullscreen effects**, **Volumetric effects** and **Water effects** ✓, but
this file has no matching keys — the port applies them from the engine defaults. Do not invent
controls for them.

## Notes

- Every control is a single integer; ranges are 0–3 or a small filter list. Only one device value per
  setting is observed, so declare ranges from the menu's option list rather than from the file.
- `EnableParticleCompressor*` and `ParticleSlowdown*` are performance-adaptation internals, not user
  settings; leave them to All values.

## Open verification

1. Set Shadows and Reflections to each menu option and read the integers back.
2. Confirm `SSAAQuality` is the "ANTI ALIASING" control.
3. Confirm the texture-filtering index order.
