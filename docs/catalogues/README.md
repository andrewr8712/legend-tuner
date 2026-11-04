# Catalogue sources

Per-title review artifacts for the guided editor. Each file lists every control the app will show,
with its registry key, label, description, option vocabulary and the evidence behind it. The Kotlin
catalogue is transcribed from these files; the conformance test checks every key against the matching
fixture in `testdata/`.

| Title | File | State |
|---|---|---|
| GRID Legends | (existing `data/SettingsCatalog.kt`, to be ported) | reference implementation |
| GRID Autosport | `grid-autosport.md` | catalogue ready; preset prefix mapping unverified |
| Alien: Isolation | `alien-isolation.md` | catalogue ready; hidden-key handling documented |
| Saints Row: The Third | `saints-row-the-third.md` | catalogue ready; preset selector observed |
| NAPOLEON | `napoleon-total-war.md` | catalogue ready; quality index unverified |
| Hitman: Blood Money | `hitman-blood-money.md` | partial — triple-valued keys read-only |
| Lara Croft: Guardian of Light | `lara-croft-guardian-of-light.md` | catalogue ready; index ladders unverified |
| Lara Croft: Temple of Osiris | `lara-croft-temple-of-osiris.md` | catalogue ready; SSAA/AA mapping provisional |

## Sourcing rules

1. **Game strings first.** Labels and descriptions come from the APK's
   `assets/en.lproj/Localizable_Game.strings` where they exist (Alien's `_DESC`, Lara's setup dialog,
   NAPOLEON's tooltips, Hitman's preset names). They are marked ✓ in the tables.
2. **File evidence second.** Keys, types, option tokens and observed values come from a sanitised
   device dump in `testdata/`.
3. **Authored last.** Where the game ships no description, the description is written here. It must
   describe the actual rendering effect and its cost, not restate the label.

Never invent a range, an option token or a preset mapping. Unverified mappings are called out in the
file's *Open verification* section and the control ships as a raw value with a note.
