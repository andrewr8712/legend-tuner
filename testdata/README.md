# Test data

Sanitised copies of real Feral Interactive `preferences` registries, used only for parser,
catalogue and round-trip tests. No game code, saves or assets are included. Analytics IDs and
hardware identifiers have been replaced; every key, type and value shape is preserved.

| File | Platform | Game version | Source |
|---|---|---|---|
| `preferences-sample.xml` | Android | GRID Legends v1.1.5 | publicly shared device file |
| `grid-autosport-preferences-sample.xml` | Switch | GRID Autosport v1.10.1 | publicly posted `preferences` paste, June 2025 |
| `alien-isolation-preferences-sample.xml` | Switch | Alien: Isolation v1.1.1 | publicly posted save dump (GBAtemp thread) |

`preferences-sample.xml` is a sanitized copy of a publicly shared GRID Legends Android `preferences`
file. Device identifiers, analytics IDs, and opaque binary blobs were removed or replaced.

The GRID Autosport and Alien: Isolation files come from the Switch builds: the registry format, hives
and key names are engine-level, but the Android builds may add or drop keys. They are schema
baselines for catalogue work and tests, not proof of the Android key set for those titles.

Source structure: `<registry>` → `HKEY_CURRENT_USER` → `Software` → `Feral Interactive` → game key
→ settings.

- GRID Legends: `GRIDLegends` → `GraphicsSettings` (live integers, `DevicePreset`, and the
  `BatterySaver` / `Graphics` / `Performance` blocks of comma-separated triples) plus `Setup`.
- GRID Autosport: `GRID Autosport` → `Setup`, flat values; graphics settings exist three times as
  `gfxconfig_*` (live), `gfxconfig_high_*` and `gfxconfigpowersaver_*`; quality values are strings
  such as `ultra`, `high`, `off`, `8xmsaa`.
- Alien: Isolation: `Alien Isolation` → `Setup`, flat integers with `VideoSetting_*` values paired
  with `VideoSetting_*_Visible` gates, plus `DynamicResolutionScaling`, `TemporalAntiAliasing` and
  `Upscaler` subkeys.
