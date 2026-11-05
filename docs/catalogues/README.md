# Catalogue sources

Per-title review artifacts for the guided editor. Each file lists every control the app shows, with
its registry key, label, description, option vocabulary and the evidence behind it. Every catalogue
is implemented in Kotlin and the conformance test enforces labels, descriptions, groups, unique
keys and options for selectable controls.

| Title | Source | Kotlin | Controls | Variants |
|---|---|---|---:|---|
| GRID Legends | `DESIGN.md` + `SettingsCatalog.kt` | `data/SettingsCatalog.kt` | 28 | Battery Saver · Graphics · Performance |
| GRID Autosport | `grid-autosport.md` | `GridAutosportCatalog` | 29 | Current · Performance cache · Power saver cache |
| Alien: Isolation | `alien-isolation.md` | `AlienIsolationCatalog` | 13 | flat |
| Saints Row: The Third | `saints-row-the-third.md` | `SaintsRowCatalog` | 22 | Graphics · Battery saver · Performance |
| NAPOLEON | `napoleon-total-war.md` | `NapoleonCatalog` | 21 | flat, `GraphicsSettings` subtree |
| Hitman: Blood Money | `hitman-blood-money.md` | `HitmanCatalog` | 18 | Live + four suffix families |
| Lara Croft: Guardian of Light | `lara-croft-guardian-of-light.md` | `LaraGolCatalog` | 10 | four block presets |
| Lara Croft: Temple of Osiris | `lara-croft-temple-of-osiris.md` | `LaraTooCatalog` | 12 | three block presets |
| Sid Meier's Railroads! | `sid-meiers-railroads.md` | `RailroadsCatalog` | 16 | flat, engine subtree |
| Tomb Raider | `tomb-raider.md` | `TombRaiderCatalog` | 24 | four block presets |
| Hitman: Absolution | `hitman-absolution.md` | `HitmanAbsolutionCatalog` | 25 | three suffix families |
| Company of Heroes | `company-of-heroes.md` | `CompanyOfHeroesCatalog` | 24 | flat, split roots |
| Total War: EMPIRE | `total-war-empire.md` | `EmpireTotalWarCatalog` | 28 | flat, `GraphicsSettings` + `MainMenuSettings` |
| Tropico 3 | `tropico-3.md` | `Tropico3Catalog` | 15 | flat, `GFX` + engine subtree |
| ROME: Total War | `rome-total-war.md` | `RomeTotalWarCatalog` | 24 | flat live values, stored presets not exposed |
| Medieval II: Total War | `medieval-2-total-war.md` | `Medieval2TotalWarCatalog` | 24 | flat, `VideoSettings` subtree |
| ROME: Barbarian Invasion | `rome-barbarian-invasion.md` | `RomeTotalWarCatalog` | 24 | shares ROME catalogue; own root/build |
| ROME: Alexander | `rome-alexander.md` | `RomeTotalWarCatalog` | 24 | shares ROME catalogue; own root/build |

Files live in `app/src/main/java/dev/legendtuner/app/data/TitleCatalog.kt` (GRID Autosport, Alien)
and `TitleCatalogs.kt` (the rest).

## Implementation notes

- **Absolute roots.** NAPOLEON and both Lara Croft titles do not keep graphics values under
  `Software/Feral Interactive/<game>/Setup`. The title profile stores the value root separately and
  the catalogue adds a preset block path where needed.
- **Three preset shapes.** Blocks with a selector (Saints Row, Lara Croft), prefixed families
  (GRID Autosport) and suffixed families (Hitman) share one `PresetFamily` model.
- **Engine keys ignore preset scope.** Render height and `ScreenW`/`ScreenH` are not part of a preset
  block, so those controls bypass the family suffix and family path.
- **Hitman triples.** Uniform triples are editable and written back as `a, a, a`; ladders stay
  read-only with the raw triple shown.
- **Resolution pair.** Saints Row's `ScreenW` + `ScreenH` are edited as one resolution control and
  written together; other titles with only a render height use the height list.

## Sourcing rules

1. **Game strings first.** Labels and descriptions come from the APK's
   `assets/en.lproj/Localizable_Game.strings` where they exist (Alien's `_DESC`, Lara's setup dialog,
   NAPOLEON's tooltips, Hitman's preset names). They are marked ✓ in the tables.
2. **File evidence second.** Keys, types, option tokens and observed values come from a sanitised
   device dump in `testdata/`.
3. **Authored last.** Where the game ships no description, the description is written here. It must
   describe the actual rendering effect and its cost, not restate the label.

Never invent a range, an option token or a preset mapping. Unverified mappings are called out in the
file's *Open verification* section, and the control ships with a note (the amber dot in the UI).

## Still unverified

| Title | Open question |
|---|---|
| GRID Autosport | Which internal prefix maps to which in-game preset name; `ai_count` semantics |
| Alien: Isolation | Texture-filter index order; per-setting maximums |
| Saints Row | Allowed tier tokens beyond the observed value; selector rewrite on exit |
| NAPOLEON | `GraphicsQuality` index range and what each index changes |
| Hitman | Ladder semantics for shadow resolution; `Disable*` inversion verified in the editor/file, not in-game |
| Lara Croft GoL | `BestTextureFilter` index order; AA mode set |
| Lara Croft ToO | `SSAAQuality` ↔ "ANTI ALIASING" mapping; filter index order |
| Railroads | Quality and FSAA tier labels; whether `MaxFramesPerSecond` or the responsive cap is the in-game limiter |
| Tomb Raider | Preset selector index; AA and filter ladders |
| Hitman: Absolution | Quality-profile mapping; suffix-family rewrite on exit |
| Company of Heroes | `antialiasing` tokens; model detail mapping; whether ScreenW/H are written after a display change |
| Total War: EMPIRE | `GraphicsQuality` preset mapping; anti-aliasing vocabulary; whether ScreenW/H appear after a display change |
| Tropico 3 | Anisotropy 2×–16× tokens; whether BatterySaverFPS is a hard cap |
| ROME: Total War | AA mapping; active preset/live-value rewrite behaviour; full building-quality range |
| Medieval II: Total War | Shader/filter index ranges; `GraphicalQuality` rewrite behaviour; AA mapping |
