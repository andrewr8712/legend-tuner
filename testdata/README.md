# Test data

Sanitised copies of real Feral Interactive `preferences` registries, used only for parser,
catalogue and round-trip tests. No game code, saves or assets are included. Analytics IDs and
hardware identifiers have been replaced; every key, type and value shape is preserved.

| File | Platform | Game version | Source |
|---|---|---|---|
| `preferences-sample.xml` | Android | GRID Legends v1.1.5 | publicly shared device file |
| `grid-autosport-preferences-sample.xml` | Android | GRID Autosport v1.10.6 | pulled from TB520FU via adb, sanitised |
| `alien-isolation-preferences-sample.xml` | Android | Alien: Isolation v1.4.3 | pulled from TB520FU via adb, sanitised |

`preferences-sample.xml` is a sanitized copy of a publicly shared GRID Legends Android `preferences`
file. Device identifiers, analytics IDs, and opaque binary blobs were removed or replaced.

The GRID Autosport and Alien: Isolation files are Android dumps from the target tablet. Console
dumps of the same titles were used during early discovery only; the Android key sets differ
(Android adds touch/virtual-pad and download-content keys, and drops some console-only settings), so
these files are the catalogue baseline.

Source structure: `<registry>` → `HKEY_CURRENT_USER` → `Software` → `Feral Interactive` → game key
→ settings.

- GRID Legends: `GRIDLegends` → `GraphicsSettings` (live integers, `DevicePreset`, and the
  `BatterySaver` / `Graphics` / `Performance` blocks of comma-separated triples) plus `Setup`.
- GRID Autosport: `GRID Autosport` → `Setup`, flat values. Graphics settings exist three times as
  `gfxconfig_*` (live), `gfxconfig_high_*` and `gfxconfigpowersaver_*`, plus two
  `gfxconfig_default_*` hint keys. Quality values are strings (`ultra`, `high`, `off`, `8xmsaa`,
  `custom`); `gfxconfig_max_fps` is an integer.
- Alien: Isolation: `Alien Isolation` → `Setup`, flat integers with `VideoSetting_*` values paired
  with `VideoSetting_*_Visible` gates, plus `DynamicResolutionScaling`, `TemporalAntiAliasing` and
  `Upscaler` subkeys. Some `_Visible` flags exist without their value key, which means the game is
  hiding that option on this device.
