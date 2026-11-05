# Test data

Sanitised copies of real Feral `preferences` registries, used only for parser, catalogue and
round-trip tests. No game code, saves or assets are included. Analytics IDs and hardware identifiers
have been replaced; every key, type and value shape is preserved.

| File | Platform | Game version | Source |
|---|---|---|---|
| `preferences-sample.xml` | Android | GRID Legends v1.1.5 | publicly shared device file |
| `grid-autosport-preferences-sample.xml` | Android | GRID Autosport v1.10.6 | TB520FU dump |
| `alien-isolation-preferences-sample.xml` | Android | Alien: Isolation v1.4.3 | TB520FU dump |
| `saintsrow3-preferences-sample.xml` | Android | Saints Row: The Third v1.0.3 | TB520FU dump |
| `napoleon-preferences-sample.xml` | Android | NAPOLEON v1.3.4 | TB520FU dump |
| `hitman-blood-money-preferences-sample.xml` | Android | Hitman: BMR v1.3.1 | TB520FU dump |
| `lara-croft-gol-preferences-sample.xml` | Android | Lara Croft: Guardian of Light v1.2.7 | TB520FU dump |
| `lara-croft-too-preferences-sample.xml` | Android | Lara Croft: Temple of Osiris v1.2.3 | TB520FU dump |
| `railroads-preferences-sample.xml` | Android | Sid Meier's Railroads! v1.4.3 | TB520FU dump |
| `tomb-raider-preferences-sample.xml` | Android | Tomb Raider v1.4 | tester-supplied dump |
| `hitman-absolution-preferences-sample.xml` | Android | Hitman: Absolution v1.3.1 | tester-supplied dump |
| `company-of-heroes-preferences-sample.xml` | Android | Company of Heroes v1.8.1 | TB520FU first-launch dump |
| `total-war-empire-preferences-sample.xml` | Android | Total War: EMPIRE v1.7.1 | TB520FU dump |
| `tropico-3-preferences-sample.xml` | Android | Tropico 3 v1.4.4 | TB520FU dump |

`preferences-sample.xml` is a sanitized copy of a publicly shared GRID Legends Android file.

Console dumps of GRID Autosport and Alien were used during early discovery only; the Android key sets
differ, so the files here are the catalogue baseline.

## Registry roots and shapes

Most titles sit under `HKEY_CURRENT_USER/Software/Feral Interactive/<game>`. NAPOLEON does not:
its settings are under `HKEY_CURRENT_USER/Software/The Creative Assembly Limited/Napoleon Total War
- Mobile/Setup/GraphicsSettings`, which is why the title profile stores an absolute registry root
rather than a game key.

- **GRID Legends** — `GRIDLegends` → `GraphicsSettings` (live integers, `DevicePreset`, and
  `BatterySaver` / `Graphics` / `Performance` blocks of comma-separated triples) plus `Setup`.
- **GRID Autosport** — `GRID Autosport` → `Setup`, flat. Graphics settings exist three times as
  `gfxconfig_*` (live), `gfxconfig_high_*` and `gfxconfigpowersaver_*`, plus two
  `gfxconfig_default_*` hints. Quality values are strings (`ultra`, `high`, `off`, `8xmsaa`).
- **Alien: Isolation** — `Alien Isolation` → `Setup`, flat integers, `VideoSetting_*` values paired
  with `VideoSetting_*_Visible` gates, plus `DynamicResolutionScaling`, `TemporalAntiAliasing`,
  `Upscaler`. Some `_Visible` flags exist without their value key.
- **Saints Row: The Third** — `Saints Row The Third` → `Setup` (81 values) with three sibling preset
  blocks `BatterySaverPreset`, `GraphicsPreset`, `PerformancePreset` (23 values each). The active
  preset is named by `Setup/GameOptions/GfxMobilePreset` (`graphics`). Values mix `string`, `integer`
  and `double`.
- **NAPOLEON** — `The Creative Assembly Limited/Napoleon Total War - Mobile/Setup/GraphicsSettings`
  (26 integers: `ShadowsQuality`, `TextureQuality`, `UnitDetail`, `ParticleEffects`, …). No presets;
  `GraphicsQuality` in the parent `Setup` acts as a quality index.
- **Hitman: Blood Money** — `Hitman Blood Money` → `Setup` (272 flat values) plus four suffix
  families of 17 keys each: `_battery`, `_graphics`, `_perf`, `_plus`. Family values are
  comma-separated triples (`"16, 16, 16"`).
- **Lara Croft: Guardian of Light** — `Lara Croft and the Guardian of Light` → `Setup` (91 flat
  integers) plus `DynamicResolutionScaling`. `GraphicsPreset` is an integer index; the file has
  1,895 values overall, most of them outside the graphics block.
- **Lara Croft: Temple of Osiris** — `Lara Croft and the Temple of Osiris` has a Feral `Setup`
  block (136 values) and a Crystal Dynamics `Graphics` block with `BatterySaverPreset`,
  `GraphicsPreset` and `PerformancePreset`, including `FullscreenWidth`/`FullscreenHeight`
  (1728×1080 in the committed sample) and `MaxFramesPerSecond`.
- **Sid Meier's Railroads!** — `Original Developer/Sid Meier's Railroads!/Setup` (flat, mixed
  integer/binary/string values), with engine FPS and FSAA values under
  `IndirectX/Direct3D/Config`. No preset blocks.
- **Tomb Raider** — `Crystal Dynamics/Tomb Raider/Setup` with four preset blocks under
  `GraphicsSettings` and engine values under `IndirectX/Direct3D/Config`.
- **Hitman: Absolution** — `IO Interactive/Hitman Absolution` root with `_battery`, `_framerate` and
  `_graphics` suffix families; display and motion values live at the root or under `Setup`.
- **Company of Heroes** — `Feral Interactive/Company of Heroes/Setup` for display/FPS values,
  `Original Developer/Company of Heroes/Config` for graphics quality, and
  `IndirectX/Direct3D/Config` for engine FPS. `ScreenW`/`ScreenH` are referenced by the remap but
  absent from the first-launch file.
- **Total War: EMPIRE** — `The Creative Assembly Limited/Total War EMPIRE/Setup` with
  `GraphicsSettings` (quality and post-processing) and `MainMenuSettings` (battery saver and audio).
  `ScreenW`/`ScreenH` are in the remap but absent from the first-launch file.
- **Tropico 3** — `Haemimont Games/Tropico 3: Gold Edition/Setup` plus `GFX` for the string quality
  ladder; `IndirectX/Direct3D/Config` holds the engine frame cap.

## Value types seen

`integer`, `string`, `binary` (hex), `double` (six decimal places), and `array`
(`GfxAvailableOverrideFps = "30,40,60"`). The editor must carry all five; only `integer` and `string`
get friendly controls, `double` gets a decimal field, `array` stays read-only.
