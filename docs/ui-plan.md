# UI plan — multi-title config editor

Status: draft for review. Companion to
`docs/superpowers/specs/2026-11-05-feral-multi-title-design.md`.

Rendered direction: `prototype/multi-title.html` and `prototype/multi-title.png` (picker and guided
settings at tablet width, built with the tokens below).

## 1. Direction

**Thesis: a pit-wall instrument panel, not a settings app.** Dense but calm, high-contrast, almost
no chrome. Numbers are monospaced, labels are condensed and tracked, surfaces are near-black with
hairline rules instead of shadows. The tool looks like something an engineer would trust at 2 a.m.,
which is exactly when someone is editing a config file to make a game run.

This keeps the identity the current app already has — carbon background, acid-lime accent, Barlow
Condensed headings — and removes the two things that made it read as a GRID fan app: the racing-only
palette usage and Inter as the body face.

**Rejected:** Material default purple, rounded-pill everything, glassmorphism, gradient heroes,
emoji icons, game key art. All of those either look generic or drag in assets we do not have rights
to ship.

**References:** rally dashboards, synthesizer panels, Elektron/Teenage Engineering hardware, the
Android 12+ "monochrome + one hot accent" school. Precision over decoration.

## 2. Design tokens

### Colour

| Token | Value | Use |
|---|---|---|
| `ink` | `#0A0B09` | app background |
| `surface` | `#111310` | cards, rail |
| `surfaceHigh` | `#191C16` | inspector, selected row |
| `line` | `#2A2E23` | 1 dp borders, dividers |
| `text` | `#F3F5EA` | primary text |
| `muted` | `#8C9280` | secondary text, labels |
| `lime` | `#D7FF3F` | primary action, active state, focus |
| `amber` | `#FFB454` | warnings, unsaved changes |
| `red` | `#FF6252` | destructive, unreadable file |
| `steel` | `#7FA6B8` | informational, type badges |

Contrast: lime on ink ≈ 14:1, muted ≈ 4.7:1, amber ≈ 8.4:1. Status is never colour-only — every
state pairs its colour with a word or a shape.

### Type

| Role | Face | Size / weight | Notes |
|---|---|---|---|
| Wordmark, screen titles | Barlow Condensed | 28–40 sp / 600 | uppercase, 0.02 em tracking |
| Section headers | Barlow Condensed | 13 sp / 600 | uppercase, 0.14 em tracking |
| Body, labels | Space Grotesk | 14–16 sp / 400–500 | replaces Inter |
| Values, hex, paths | JetBrains Mono | 13–14 sp / 500 | tabular figures |
| Micro-labels | Space Grotesk | 11 sp / 500 | uppercase, 0.08 em |

All three are OFL and small enough to bundle as TTFs. Numbers use tabular figures so values do not
jitter when stepping.

### Space, shape, depth

- 4 dp scale: 4 / 8 / 12 / 16 / 24 / 32 / 48.
- Radii: 14 dp cards, 10 dp controls, 8 dp badges. Nothing fully rounded except status dots.
- Depth: surface steps plus 1 dp hairlines. No elevation shadows anywhere.
- Background: ink base, a 24 dp grid at 2.5% white, and a tiled 3% noise PNG. Both are static
  drawables — no runtime work.
- Touch targets: 48 dp minimum; settings rows 56 dp.

### Motion

- Standard transition 160 ms `ease-out`; inspector 220 ms.
- Picker cards stagger 25 ms each on first frame, fade + 8 dp rise. Once per process.
- No parallax, no looping animation, no shimmer except the picker skeleton.
- Save feedback is a bottom snackbar with the file name, not a modal.

## 3. Information architecture

```
Launch
  └─ Picker ──────────────┬──────────────┐
       │                  │              │
       ▼                  ▼              ▼
   Dashboard          Open file…     Access sheet
       │
       ├─ Settings (guided catalogue)
       │     └─ Inspector (docked / overlay / sheet)
       ├─ All values (registry browser)
       ├─ Raw XML
       └─ Backups
```

One document open at a time. The rail's game chip returns to the picker, with a dirty-state confirm.

## 4. Screens

### 4.1 Picker

```
┌──────────────────────────────────────────────────────────────────────────┐
│ CONFIG TUNER                         ● SHIZUKU            [refresh]      │
│ Feral runtime settings                                                   │
│                                                                          │
│ ┌──────────────────────┐ ┌──────────────────────┐ ┌────────────────────┐ │
│ │ GA                   │ │ AI                   │ │ HBM                │ │
│ │ GRID Autosport       │ │ Alien: Isolation     │ │ Hitman: Blood…     │ │
│ │ 66 KB · v1.10.6      │ │ 24 KB · v1.4.3       │ │ Installed          │ │
│ │ READY             ›  │ │ READY             ›  │ │ Launch once        │ │
│ └──────────────────────┘ └──────────────────────┘ └────────────────────┘ │
│ ┌──────────────────────┐                                                 │
│ │ ＋  Open a preferences file…                                           │
│ └──────────────────────┘                                                 │
└──────────────────────────────────────────────────────────────────────────┘
```

- Monogram tiles, not logos: two letters, lime hairline box, condensed type.
- Card states: `READY` (size + version), `INSTALLED` (no file yet), `UNREADABLE` (red, with path),
  `CHECKING` (skeleton).
- Cards are buttons; a long-press menu offers "Copy path" and "Forget manual file".
- Empty state: three-route explanation (root / Shizuku / copy a file) with the access sheet opened
  in place.
- Sorting: last opened first, then ready before installed, then alphabetical.

### 4.2 Dashboard

```
┌──────────┬───────────────────────────────────────────────┬───────────────┐
│ GA       │  GRID AUTOSPORT              v1.10.6   READY   │  ACTIONS      │
│ ▸ Game   │  /sdcard/Android/data/com.feralinteractive…   │  ⚙ Settings   │
│          │  66 KB · edited 2 h ago · 681 values          │  ≡ All values │
│ Dashboard│                                               │  </> Raw XML  │
│ Settings │  ┌ PERFORMANCE ─────────────────────────────┐ │  ⧉ Backups    │
│ Values   │  │ 30 fps · Shadows Ultra · AA 8×            │ │               │
│ Raw XML  │  └───────────────────────────────────────────┘ │  ACCESS       │
│ Backups  │  Quick tune:  FPS   Shadows   AA               │  Shizuku      │
└──────────┴───────────────────────────────────────────────┴───────────────┘
```

- Hero shows what matters: version, path, size, age, value count.
- Preset summary appears only when the catalogue has presets.
- A single amber strip appears when the document is dirty ("Unsaved changes · Save").
- Specification notice when `cache/specifications.xml` exists: "This game may re-clamp values on an
  online launch."

### 4.3 Settings (guided)

```
┌──────────┬─────────────────────────────────┬──────────────────────────────┐
│ GA       │ CURRENT  GRAPHICS  PERFORMANCE  │  SHADOWS                     │
│          │ ─────────────────────────────── │  Dynamic shadow maps for…    │
│ Dashboard│ IMAGE QUALITY                   │                              │
│ Settings │ Shadows              Ultra   ›  │  ┌────────┬────────┬───────┐ │
│ Values   │ Shaders              High    ›  │  │ Ultra  │ High   │ …     │ │
│ Raw XML  │ Textures             High    ›  │  └────────┴────────┴───────┘ │
│ Backups  │ Anti-aliasing        8×      ›  │                              │
│          │ Anisotropic filter   Ultra   ›  │  Cost  ▮▮▮▮▯  GPU, fill     │
│          │                                 │  Applied to: CURRENT         │
│          │ DETAIL                          │  [ Apply ]  [ Reset ]        │
│          │ Vehicles             High    ›  │                              │
└──────────┴─────────────────────────────────┴──────────────────────────────┘
```

- Preset row only when `PresetModel` has variants; the live set is a tab labelled **Current**, the
  game's own names follow.
- Rows are 56 dp: label, value in mono, chevron. Tap opens the inspector; a long press applies the
  group's default.
- Groups: Image quality, Detail, Lighting & atmosphere, Performance, Display. Risky controls live
  behind an **Advanced** switch at the bottom, mirroring the games' own "enable advanced graphics".
- Inspector: description from the catalogue (game text where it exists), a segmented control for
  ≤5 options, stepper for ranges, cost meter where the catalogue declares one, and provenance-free
  copy — users get the plain reason, not our sourcing notes.

### 4.4 All values

- Scope switch: **This game** / **Entire registry** (amber hairline under the bar when entire).
- Search field, 120 ms debounce, result count on the right.
- Sticky group headers (`Setup`, `GraphicsSettings`, `IndirectX/Direct3D/Config`).
- Rows: name (Space Grotesk), type badge (steel), value (mono, ellipsised), 52 dp.
- Unknown type: badge shows the raw type, inspector is read-only with a pointer to Raw XML.

### 4.5 Raw XML and Backups

- Raw XML: JetBrains Mono 12.5 sp, line numbers in muted, parse error strip pinned to the bottom,
  Save disabled until the document parses.
- Backups: rows of timestamp + label + size, newest first; restore asks for confirmation, preview
  of changed keys is a later addition.

### 4.6 Access sheet

- Bottom sheet, three route cards with live status: Root, Shizuku, Manual copy.
- Each card: one-line what-it-needs, one-line what-it-can-do. No walls of text.

## 5. Component inventory

| Component | States |
|---|---|
| `GameCard` | ready, installed, unreadable, checking |
| `AccessChip` | root, shizuku, direct, none, connecting |
| `PresetTab` | current, active, modified, unavailable |
| `ControlRow` | normal, modified, missing-key, read-only, advanced |
| `ValueRow` | normal, modified, unknown-type, long-value |
| `Inspector` | docked, overlay, sheet, validation-error |
| `Banner` | dirty, spec-warning, unreadable, permission |
| `EmptyState` | no games, no values, no backups, no search hits |

## 6. Progressive disclosure model

1. **Launch:** only installed, supported titles. Nothing parsed.
2. **Open a game:** dashboard summary; one parse; no raw keys.
3. **Settings:** friendly labels and current values only.
4. **Advanced switch:** risky and seldom-used catalogue entries.
5. **All values:** every key, typed and searchable, behind an explicit entry.
6. **Raw XML:** the whole document, behind a warning strip.

Each level is a deliberate action, never a scroll. Nothing in the guided screen shows a registry key
name; nothing in All values pretends to be a friendly label.

## 7. Performance budget (Compose)

- Picker first frame < 500 ms: discovery is async, cards render from `PackageManager` synchronously,
  file stats arrive per card.
- No file reads on the picker; one parse on open; one serialize per save; raw XML serializes only
  when that screen is opened.
- Catalogues are compiled Kotlin objects — no JSON parsing, no reflection.
- Settings and value lists are `LazyColumn` with stable keys; rows read by key so editing one value
  does not recompose the page.
- The parsed document is held in the view model with a monotonically increasing revision; screens
  observe `(revision, key)` rather than the whole DOM.
- Search builds one lowercase index per document revision, not per keystroke.
- No packaged images beyond the noise tile and launcher icon. No network, so no image pipeline.

## 8. Accessibility

- 48 dp targets; rows 56 dp; 12 dp gutters between adjacent targets.
- Text scales to 1.3× without clipping; the two-column grid collapses to one column at large font
  sizes, not just narrow widths.
- TalkBack: rows announce "Shadows, Ultra, opens options"; save announces "Saved to GRID Autosport".
- Focus order follows the visual grid; keyboard: arrows move rows, Enter opens, Esc closes, Ctrl+S
  saves (already implemented).
- Status uses text plus colour; the lime accent is never the only signal.

## 9. Copy rules

- Labels come from the game where it ships them; otherwise short nouns ("Shadows", not "Shadow
  Quality Level").
- Descriptions say what the setting changes and what it costs. One or two sentences. No "enhance
  your experience".
- Errors state what happened, which file, and the next action. No apologies, no stack traces.
- Buttons are verbs: Save changes, Open a file, Restore, Force stop.

## 10. Build order

1. Tokens, type faces, background, `ConfigTunerTheme`.
2. `PickerScreen` with cards and states (static data first).
3. Rail game chip + `AppDestination` restructure.
4. `SettingsScreen` from `TitleCatalog` + `CatalogEngine`, GRID Legends first.
5. `AllValuesScreen` + value inspector.
6. Dashboard variants, banners, dirty state.
7. Raw XML / Backups polish.
8. Accessibility pass and font-scale layout checks.

## 11. Open decisions

1. Display name: **Config Tuner** (current proposal) versus keeping Legend Tuner.
2. Per-title accent tint: recommended **no** — one lime accent, identity comes from the title's
   monogram and name.
3. Density: recommended comfortable 56 dp rows; a compact 44 dp toggle could come later for
   power users.
4. Whether the picker shows unreadable files as cards or only as an error after tapping. Current
   design: card, because that is the state users need to understand.
