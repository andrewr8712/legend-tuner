# Company of Heroes — guided catalogue source

> **Status:** implemented as `CompanyOfHeroesCatalog`.
>
> Source: first-launch `preferences` file pulled from the TB520FU after launching the game once,
> CoH v1.8.1 (`com.feralinteractive.companyofheroes_android`). The analytics ID is sanitized in
> `testdata/company-of-heroes-preferences-sample.xml`.

## Registry layout

Feral display and performance values live under:

```text
HKEY_CURRENT_USER/Software/Feral Interactive/Company of Heroes/Setup
```

The original game's graphics values live in a separate subtree:

```text
HKEY_CURRENT_USER/Software/Original Developer/Company of Heroes/Config
```

Engine values are under:

```text
HKEY_CURRENT_USER/Software/IndirectX/Direct3D/Config
```

`AutoValueRemap/GPURemap` references `ScreenW` and `ScreenH`, but the first-launch file does not
contain those values. The editor therefore exposes `FixedScreenHeight` rather than inventing a
resolution pair.

## Controls

| Key | Label | Group | Observed |
|---|---|---|---|
| `FullScreen` | Fullscreen | Display | `1` |
| `FixedScreenHeight` | Render height | Display | `1080` |
| `RenderUnderNotch` | Render under notch | Display | `1` |
| `antialiasing` | Anti-aliasing | Graphics | `0` |
| `shaderquality` | Shader quality | Graphics | `2` |
| `shadows` | Shadow quality | Graphics | `1` |
| `terraindetail` | Terrain detail | Graphics | `2` |
| `texturedetail` | Texture detail | Graphics | `2` |
| `treedetail` | Tree detail | Graphics | `1` |
| `effectsdensity` | Effects density | Graphics | `1` |
| `effectsfidelity` | Effects fidelity | Graphics | `1` |
| `shrubsdetail` | Shrub detail | Graphics | `1` |
| `modelquality` | Model quality | Graphics | `1` |
| `physics` | Physics effects | Graphics | `0` |
| `postprocessing` | Post-processing | Graphics | `0` |
| `reflections` | Reflections | Graphics | `1` |
| `ribbons` | Ribbons | Graphics | `0` |
| `objectscarring` | Object scarring | Graphics | `1` |
| `raindetail` | Rain detail | Graphics | `1` |
| `EnableFramerateManager` | Dynamic frame-rate manager | Performance | `1` (engine subtree) |
| `MaxFramesPerSecond` | Frame cap | Performance | `30` (engine subtree) |
| `FramerateManagerNormalIdleFPS` | Idle frame cap | Performance | `30` |
| `FramerateManagerNormalResponsiveFPS` | Responsive frame cap | Performance | `60` |
| `BatterySaverEnabled` | Battery saver | Performance | `0` |

The graphics tier labels are inferred. `antialiasing = 0` is the only observed token; the Low/High
labels for `1` and `2` need in-game confirmation.

## Not exposed

- `BatterySaverMaxFPS` is a binary-encoded value (`0000000000003e40`), not an integer FPS field.
- `modeldetail = 400` is stored as a wide integer with no confirmed menu mapping.
- The file contains many gameplay, UI, cloud, audio and telemetry values; they stay in Raw XML.
- `FeralAnalyticsInstallID` is device analytics data and is sanitized in the fixture.

## Open verification

1. Confirm the accepted range and labels for `antialiasing` and the graphics quality ladders.
2. Confirm whether `MaxFramesPerSecond` or `FramerateManagerNormalResponsiveFPS` is the limiter
   shown by the game.
3. Confirm whether the game writes `ScreenW`/`ScreenH` after the resolution is changed in-game.
4. Confirm whether the game rewrites the graphics config on exit.
