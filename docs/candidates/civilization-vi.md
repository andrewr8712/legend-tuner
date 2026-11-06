# Civilization VI — config investigation

**Status:** investigated, **not supported**. This is a record of what the game actually
stores; no app code or parser has been added.

## Device evidence

- Device: Lenovo Yoga Tab Plus TB520FU, Android 17
- Package: `com.aspyr.civvi`
- Version: 1.2.5 (versionCode 4042322)
- `minSdk 28` / target 35
- Installed and launched once, so the settings files exist

## Where the config lives

```text
/data/user/0/com.aspyr.civvi/files/Sid Meier's Civilization VI/
├── AppOptions.txt       5.2 KB
├── GraphicsOptions.txt  3.7 KB
├── UserOptions.txt      8.5 KB
├── InputSettings.json   10 KB
└── SoundOpts.txt        570 B
```

This is **internal app data**, not `/sdcard/Android/data`. Root can read it; Shizuku's shell UID
cannot. A non-root user would need to copy the files out with a root file manager and use a manual
import route.

## Format

The two relevant files are INI-like text:

```ini
;Altering this number will cause the app to overwrite this file with defaults
Version 10

[Video]
PerformanceImpact 0
MSAA 1
VSync 1
ShadowMapResolution 2048
RefreshRateInHz 60

[AO]
EnableAO 0

[Bloom]
EnableBloom 0
```

They are **not** XML registry files. `GraphicsOptions.txt` also contains duplicate sections — for
example `[Video]` appears twice and `[General]` appears twice — so a naive INI parser that keys by
section alone will lose data.

## Candidate controls if support is added later

### `AppOptions.txt`

| Section | Key | Observed | Meaning |
|---|---|---|---|
| `[Video]` | `RenderWidth` | 1152 | render width in pixels |
| `[Video]` | `RenderHeight` | 720 | render height in pixels |
| `[Video]` | `FullScreen` | 2 | 0 windowed, 1 fullscreen, 2 borderless |
| `[Video]` | `UIUpscale` | 0.000000 | UI scale adjustment |
| `[Language]` | `DisplayLanguage` | `en_US` | UI language |
| `[UI]` | `ColorblindAdaptation` | 0 | colorblind mode |
| `[UI]` | `TooltipBehavior` | 1 | tooltip behavior |
| `[Performance]` | `MaxJobThreads` | -1 | engine worker threads |

### `GraphicsOptions.txt`

| Section | Key | Observed | Meaning |
|---|---|---|---|
| `[Video]` | `PerformanceImpact` | 0 | master performance profile; -1 required for individual changes |
| `[Video]` | `MemoryImpact` | 0 | master memory profile |
| `[Video]` | `MSAA` | 1 | MSAA sample count |
| `[Video]` | `MSAAQuality` | 0 | MSAA quality |
| `[Video]` | `VSync` | 1 | vertical sync |
| `[Video]` | `ShadowMapResolution` | 2048 | shadow map size |
| `[Video]` | `AODepthResolution` | 1024 | AO depth map |
| `[Video]` | `AORenderResolution` | 1024 | AO render target |
| `[Video]` | `RefreshRateInHz` | 60 | fullscreen refresh rate |
| `[Video]` | `ReducedAssetTextures` | 0 | reduced texture set |
| `[Terrain]` | `TerrainSynthesisDetailLevel` | 2 | 1 full / 2 low |
| `[Terrain]` | `TerrainQuality` | 0 | 0–4 |
| `[Terrain]` | `ReducedTerrainMaterials` | 1 | memory-saving terrain materials |
| `[General]` | `SSReflectPasses` | 4 | SSR passes; 0 disables |
| `[General]` | `UseLowResWater` | 0 | low-res water maps |
| `[General]` | `UseLowQualityWaterShader` | 1 | cheap water shader |
| `[General]` | `VFXDetailLevel` | 0 | 0 low / 1 high |
| `[General]` | `ClutterDetailLevel` | 0 | 0 low / 1 high |
| `[General]` | `AvailableViews` | 2 | 0 strategic / 1 3D / 2 both |
| `[AO]` | `EnableAO` | 0 | ambient occlusion |
| `[Bloom]` | `EnableBloom` | 0 | bloom |
| `[Shadows]` | `EnableShadows` | 0 | shadows |
| `[DynamicLighting]` | `EnableDynamicLighting` | 1 | dynamic lighting |
| `[CloudShadows]` | `EnableCloudShadows` | 0 | cloud shadows |
| `[Clutter]` | `EnableClutter` | 1 | map clutter |
| `[Video]` | `HighQualityTextures` | 1 | drop mips / texture quality |

## Why this is not a catalogue add

The current Config Tuner pipeline assumes one XML file:

```text
ConfigFileLoader -> ConfigDocument -> TitleCatalog -> write/read-back/parse
```

Civ VI would need:

1. A new **INI document** that preserves comments, line order and duplicate section names.
2. A **second file** in the same session (`AppOptions.txt` + `GraphicsOptions.txt`), with a backup
   for each.
3. A title-level **format discriminator**, not just another `TitleCatalog`.
4. A root-only internal path, plus a manual-import fallback for non-root users.
5. Read-back validation that re-parses the INI and verifies the changed key/value pair.

That is a separate backend, not a new title entry.

## Risks and open questions

- `PerformanceImpact`/`MemoryImpact` may override every individual graphics key while not `-1`.
- The game may rewrite the files on exit; the editor must warn about this.
- The `Version` header is a write-guard: changing it causes defaults to be restored.
- Duplicate `[Video]` and `[General]` sections must be preserved.
- The two files may be written at different times; backups and rollback need to cover both.
- No raw config has been committed; the observed values above are enough to design a parser.

## Recommendation

Deferred. If Civ VI support is prioritised later, build it as a separate **INI editor** with
`GraphicsOptions.txt` first, `AppOptions.txt` second, and root access only for the initial version.
Do not force it into the Feral registry model.
