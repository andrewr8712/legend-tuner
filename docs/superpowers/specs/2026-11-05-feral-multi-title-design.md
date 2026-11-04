# Multi-title Feral config editor — design

**Status:** draft, awaiting approval
**Date:** 2026-11-05
**Supersedes:** the single-game assumptions in `DESIGN.md`
**Builds on:** Legend Tuner 1.1.0 (`applicationId dev.legendtuner.app`)

---

## 1. Intent and why

Feral Interactive's Android ports share one settings store: an XML registry at
`Android/data/<package>/files/feral_app_support/preferences`. Legend Tuner already edits it
correctly for GRID Legends, with root / Shizuku / SAF access, backups, read-back verification and
a raw XML view. The file format, the access layer and the safety pipeline are game-agnostic; only
the package, the registry key path, the preset model and the settings catalogue are GRID-specific.

The goal is one app that opens any Feral title's registry, edits it with the same safety
guarantees, and keeps GRID Legends' guided preset editor as the reference experience. The user
reaches a game through a picker on launch, not a tab bar.

Success looks like: launch → pick a detected Feral game → edit values (guided for GRID Legends,
raw-but-typed for everything else) → save → the game keeps the change. No regression for existing
GRID Legends users.

## 2. Goals

- A title table replaces the hardcoded package, path and key root.
- A picker screen on launch listing installed/readable Feral titles plus "open a file".
- Generic registry browsing and editing for any Feral `preferences` file: search, grouped by key
  path, type-aware validation, in-place mutation.
- GRID Legends keeps the current dashboard, preset cards, settings grid, inspector and raw editor.
- Backups, force-stop and verification continue to work per title.
- Existing unit tests keep passing; new tests cover the generic path.

## 3. Non-goals

- Feral titles whose settings live in engine text configs (`VFS/Local/preferences`,
  `VFS/User/Settings.ini`), e.g. Total War MEDIEVAL II. They stay out of the picker and are
  reachable only through "open a file" if the user supplies one.
- Editing `cache/specifications.xml`, save files, `SaveData/`, mod folders or `RemoteResources/`.
- Friendly per-title catalogues beyond GRID Legends (no Alien or XCOM 2 labelled controls).
- iOS, desktop or Switch paths.
- Renaming the `applicationId`, the Kotlin package, or the GitHub repository.
- `QUERY_ALL_PACKAGES`.

## 4. Anti-goals

- A tabbed interface holding several parsed documents at once. One active document per session is
  an invariant the save/verify/backup pipeline depends on.
- Guessing a game's registry key from its title, or inventing per-title key names that were never
  verified against a real file.
- Presenting a title as supported when its config path is unverified.
- Silently regenerating a registry document. Unknown keys, types, comments and ordering survive
  every edit.

## 5. Constraints

- Android 8.0+ (`minSdk 26`), compile/target 35, Kotlin + Compose Material 3, no new runtime
  dependency unless unavoidable.
- Android 11+ package visibility: every supported package needs its own `<queries>` entry.
- Android 11+ blocks other apps' `Android/data`; root/Shizuku/SAF remain the only routes.
- The app has no `INTERNET` permission and must keep it that way.
- JVM-only unit tests. No Robolectric; anything needing a `Context` is split into pure functions
  or given a `File`/`String` seam.

## 6. Approaches considered

**A — GRID-first with a hidden generic mode.** Smallest diff: keep the current entry flow, add a
switcher inside the app. Rejected: other games become a second-class path, and the first screen
still implies the app is about one game.

**B — Neutral picker, per-game workspace.** Launch screen lists detected titles; selecting one
opens the existing layout scoped to that game. Chosen.

**C — Generic tool only.** One file picker plus a registry tree. Rejected: discards the guided
editor that makes the app better than a text editor.

**Tabs vs picker.** Tabs were considered and rejected. Nineteen possible titles overflow a tab
strip on a 12.7″ tablet; tabs imply N live documents with N backup chains and N force-stop targets;
and a tab cannot express "installed but config not created yet". A picker plus a game chip in the
rail gives one-tap switching with one active document.

## 7. What gets built

### 7.1 Title table

New `app/src/main/java/dev/legendtuner/app/data/FeralTitle.kt`:

```kotlin
data class FeralTitle(
    val id: String,           // stable slug, used for preferences + backup namespace
    val label: String,        // display name
    val packageName: String,
    val gameKey: String?,     // registry key under Software/Feral Interactive; null = auto-detect
    val guided: Boolean       // true only for GRID Legends
)

object FeralTitles {
    val GRID_LEGENDS = FeralTitle(
        id = "grid-legends",
        label = "GRID Legends",
        packageName = "com.feralinteractive.gridlegends_android",
        gameKey = "GRIDLegends",
        guided = true
    )
    val ALL: List<FeralTitle> = listOf(
        GRID_LEGENDS,
        FeralTitle("grid-autosport", "GRID Autosport", "com.feralinteractive.gridas", null, false),
        FeralTitle("alien-isolation", "Alien: Isolation", "com.feralinteractive.alienisolation_android", null, false),
        FeralTitle("xcom2", "XCOM 2 Collection", "com.feralinteractive.xcom2_android", null, false),
        FeralTitle("company-of-heroes", "Company of Heroes", "com.feralinteractive.companyofheroes_android", null, false),
        FeralTitle("empire", "Total War: EMPIRE", "com.feralinteractive.empire_android", null, false),
        FeralTitle("railroads", "Sid Meier's Railroads!", "com.feralinteractive.railroads_android", null, false),
        FeralTitle("rome", "ROME: Total War", "com.feralinteractive.rometw", null, false),
        FeralTitle("rome-bi", "ROME: Total War – BI", "com.feralinteractive.rometwbi", null, false),
        FeralTitle("rome-alexander", "ROME: Total War – Alexander", "com.feralinteractive.rometwalex", null, false),
        FeralTitle("napoleon", "Total War: NAPOLEON", "com.feralinteractive.napoleon_android", null, false)
    )
    fun byPackage(pkg: String): FeralTitle? = ALL.firstOrNull { it.packageName == pkg }
}
```

Only GRID Legends carries a `gameKey`, because it is the only one verified against a real Android
sample. For everything else the key is resolved at load time: if
`HKEY_CURRENT_USER/Software/Feral Interactive` has exactly one child key, that child is the game
key; otherwise the browser starts at `Feral Interactive` and the user sees all children. No title
key is invented.

The package list comes from Feral's Play developer listing. Titles whose config path was never
confirmed (Tropico, Hitman, Saints Row, Lara Croft, GRID Autosport Custom Edition) are deliberately
absent: the picker must not promise a file it cannot name.

### 7.2 Access layer and sessions

Privileged access becomes a session, not something rebuilt per title. Today `RootAccess.locate()`
spawns `su` per volume and `ShizukuAccess.connect()` re-binds the user service on every call;
probing eleven titles that way would mean dozens of `su` spawns or eleven binder binds per picker
load.

New `access/AccessSession.kt`:

```kotlin
enum class AccessMode { ROOT, SHIZUKU, DIRECT }   // privileged transports only

interface AccessSession {
    val mode: AccessMode
    suspend fun locate(packageName: String): LocatedFile?   // one stat per volume
    suspend fun read(path: String): ByteArray
    suspend fun write(path: String, bytes: ByteArray)
    suspend fun forceStop(packageName: String): Boolean
    fun close()
}
```

`AccessFactory.open(context, mode)` returns one session per app session — one Shizuku bind, one su
availability check. A document is then a `DocumentSource`:

```kotlin
sealed interface DocumentSource {
    val mode: AccessMode?
    suspend fun read(path: String): ByteArray
    suspend fun write(path: String, bytes: ByteArray)
    suspend fun forceStop(): Boolean
}
class SessionSource(val session: AccessSession, val packageName: String) : DocumentSource
class ManualSource(val saf: SafAccess) : DocumentSource            // mode = null
```

`LoadedConfig` holds a `DocumentSource` plus the `LocatedFile` instead of a `FileAccess`.
`DirectFileAccess` disappears; `Direct` is a session whose `locate`/read/write use the `File` API.
`SafAccess` stays document-scoped and is never part of the session.

Force-stop validates the package against `FeralTitles.ALL` before building the `am force-stop`
string, so no caller-supplied string reaches the shell.

### 7.3 Discovery

New `data/TitleDiscovery.kt`:

```kotlin
enum class TitleState { READABLE, INSTALLED_ONLY, PRESENT_UNUSABLE }

data class TitleStatus(
    val title: FeralTitle,
    val state: TitleState,
    val path: String?,     // resolved path when readable
    val size: Long?,
    val modified: Long?
)

suspend fun discover(context: Context, session: AccessSession?): List<TitleStatus>
```

- Installed check via `PackageManager` for every title (`<queries>` covers this).
- Readable check only when a session exists: one `locate()` per installed title through the shared
  session. No file contents are read, so discovery stays a handful of stats.
- `PRESENT_UNUSABLE` is session-local: set when an open attempt fails to parse or fails
  `validateRegistry()`. Discovery never parses, so the state is cleared on the next refresh.
- Titles that are not installed are omitted. If nothing is installed, the picker shows only
  "open a file" and an access setup card.
- With no session (manual-only or no privileged route), installed titles show as `INSTALLED_ONLY`
  and the picker explains that editing needs root, Shizuku, or a copied file.

Discovery runs on picker load and on refresh.

### 7.4 Document model

`ConfigDocument` keeps its tree-walking API and loses its GRID assumption:

- `parse()` accepts any `<registry>` document.
- `init` no longer throws a GRID-specific message; the root check stays.
- Split validation:
  - `validateRegistry()` — root is `registry`, at least one key, every `value` has a `name` and
    a non-empty `type`. **Types are not enumerated**: any type string is accepted and preserved,
    because the anti-goal of dropping unknown values outranks strict typing.
  - `validateGuided(title)` — for GRID Legends only: `GraphicsSettings` exists, `DevicePreset`
    parses, the active preset block exists.
- New `fun snapshot(): Map<String, String>` — key `"$path/$name"`, value `"$type\u0000$text"`.
  This is the generic verification primitive, and it covers unknown types too.
- New `fun setText(path: List<String>, name: String, text: String): Boolean` — same as `setValue`
  but named for the generic editor; `setValue` stays for the existing callers.
- GRID-only helpers (`graphicsSettings`, `setup`, `setPresetFirstToken`, `applyPresetToLive`,
  `ensureActivePresetExists`, `maximumFor`) remain, but their path constants become
  `GRID_ROOT = listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRIDLegends")`, and
  callers reach them only through the guided branch.

New `data/RegistryModel.kt` (pure Kotlin, unit-testable):

```kotlin
data class ValueRow(val path: List<String>, val name: String, val type: String, val text: String)

enum class ValueScope { GAME, ENTIRE }

fun rows(doc: ConfigDocument): List<ValueRow>
fun inScope(rows: List<ValueRow>, gameRoot: List<String>?, scope: ValueScope): List<ValueRow>
fun search(rows: List<ValueRow>, query: String): List<ValueRow>
fun groupLabel(row: ValueRow, gameRoot: List<String>?): String
fun validateText(type: String, text: String): String?        // null = valid; null type => read-only
```

**Scope.** The registry holds real engine settings outside the game key — `Software/IndirectX/…`,
`Software/SystemMonitor/Config`, `Software/FeralNet/Login`, `AutoValueRemap/GPURemap/…`, plus
`HKEY_LOCAL_MACHINE/Hardware/…` inventory. The browser therefore has two scopes:

- `GAME` (default): rows under the resolved game root. When `gameKey` is null and
  `Software/Feral Interactive` has exactly one child, that child is used; with several children the
  scope is `Software/Feral Interactive` (all games' subtrees) rather than the whole file. Never
  includes `HKEY_LOCAL_MACHINE`.
- `ENTIRE`: every value in the document, including engine and hardware sections. Reached through an
  explicit "Entire registry" toggle that carries a one-line warning; it is not the default because
  most of those rows are bookkeeping, not settings.

`groupLabel` strips the game root when present, otherwise shows the path from the hive. `search`
always runs inside the active scope, matching name and path case-insensitively.

**Types.** `validateText` returns `null` (valid) for `integer` only when the text parses as `Long`
(leading `-` allowed), for `binary` when empty or even-length hex, and for `string` always. Any other
type has no validator: its row renders with the raw type as its badge and is **read-only** in the
inspector, with a hint that raw XML editing is available. This keeps a future Feral type from
making a title impossible to open or save.


### 7.5 Editing and saving

The generic editor mutates values in place and marks the document dirty. There is no semantic
clamping outside the guided catalogue — but there is type validation and a mandatory backup.

Save path, shared by both modes:

1. If guided, `applyPresetToLive(selectedPreset)` before serialising.
2. `val intended = doc.snapshot()`; `doc.validateRegistry()` (plus `validateGuided` when guided).
3. Back up `originalBytes` under the active title's namespace.
4. Serialise and write through the active access.
5. Read back, parse, `validateRegistry()`, and compare `snapshot()` to `intended` for exact
   equality of keys and values.
6. If guided, additionally require `activePreset() == selectedPreset`.
7. On any failure, rewrite `originalBytes` and report that the original was restored.

Step 5 replaces the current GRID-only "did the preset persist" check and is strictly stronger: any
dropped key, changed type or mangled text fails the save.

### 7.6 State and navigation

`AppState` gains:

- `picker: List<TitleStatus>`, `pickerLoading: Boolean`
- `session: AccessSession?`, `privilegedMode: AccessMode?` — held independently of any document, so
  the picker and the access chip have a source before a file is open
- `activeTitle: FeralTitle?` (null when a manual file is open)
- `manualEntry: ManualEntry?` — remembered SAF URI plus its display name, shown as a picker card
- `valueScope: ValueScope`
- `selectedValue: Pair<String, String>?` (path, name) for the generic inspector

`LoadedConfig` gains `title: FeralTitle?` and holds a `DocumentSource`.

`AppDestination` becomes `PICKER, DASHBOARD, GRID_EDITOR, VALUES, RAW, BACKUPS`. The rail shows:

| Destination | Guided (GRID) | Generic |
|---|---|---|
| Dashboard | ✓ | ✓ |
| Graphics | ✓ | — |
| Values | — | ✓ |
| Raw XML | ✓ | ✓ |
| Backups | ✓ | ✓ |

The rail header carries the active title's label plus a chevron; tapping it returns to the picker
(confirming first if the document is dirty). Compact widths mirror this in the bottom bar.

Launch behaviour changes: the app prepares the best privileged session silently (root → direct →
Shizuku) but does **not** open a file. It shows the picker. `last_title_id` is stored only to
sort/highlight that card. This removes the current auto-load, which would otherwise pick a game on
the user's behalf.

Manual files are a source, not a mode. `KEY_MODE` now stores only a privileged mode
(`ROOT`/`SHIZUKU`/`DIRECT`); a remembered SAF URI is stored separately and rendered as a "Manual
file" card on the picker. It is never reopened automatically. Opening it creates a `ManualSource`;
a privileged session may stay alive at the same time, and the access chip keeps showing it. A manual
document has no package, so force-stop and discovery are unavailable while it is open.

Switching titles reuses the one session. Closing a document returns to the picker without tearing
down the session; the session is closed when the process ends.

### 7.7 Screens

**PickerScreen** (new). Wordmark, "Choose a game", access chip (mode + change), refresh.
`LazyVerticalGrid` of cards, minimum 260 dp wide, so 2 up on a tablet in portrait and 3 up in
landscape.

```
┌ GRID Legends ─────────────┐  ┌ Alien: Isolation ─────────┐
│ 37 KB · edited 2 days ago │  │ Installed                 │
│ Edit                      │  │ Launch the game once      │
└───────────────────────────┘  └───────────────────────────┘
──────────────────────────────────────────────────────────────
Open a preferences file…
```

Card states: `READABLE` → size + mtime + "Edit"; `INSTALLED_ONLY` → "Installed · launch the game
once" with no Edit affordance; `PRESENT_UNUSABLE` → "File present but not a Feral registry" with
the resolved path and an option to open a different file. A footer row opens SAF; a hint below
explains root/Shizuku when no title is readable.

**RegistryScreen** (new). Header: scope switch (`This game` / `Entire registry`), search field, value
count. Then a `LazyColumn` grouped by key path. Group header is the path relative to the game root
(e.g. `Setup`); each row shows name, type badge and a truncated value. Tapping a row opens the
inspector: type, current raw text, a field (numeric keyboard for `integer`, monospace for `binary`),
inline validation, Apply. Rows with a type outside `integer`/`string`/`binary` render with the raw
type as the badge and a read-only inspector pointing at Raw XML. On ≥1000 dp the inspector docks as
a ~350 dp panel, matching the existing editor; on compact widths it is a bottom sheet. Search
matches name and path inside the active scope; an empty result states the query and the value count.
Switching to `Entire registry` shows a one-line warning above the list; the choice is remembered for
the session, not persisted.

**DashboardScreen** becomes title-aware. Common: title label, path, access mode, size/mtime,
Backups, Raw XML. Guided adds the preset snapshot, quick-tune shortcuts and "Edit graphics".
Generic adds a value count and "Browse values", and shows `GameVersionString` /
`GameInstallVersionString` when the game's `Setup` key contains it.

**EditorScreen, SettingInspector, RawXmlScreen** keep their layout. Copy loses "GRID Legends"
where it refers to the app's scope, keeps it where it refers to the game.

**AccessScreens** copy generalises to "the selected game's preferences file"; the three-route
explanation is unchanged.

### 7.8 Backups

`BackupStore` takes a namespace and a root directory:

```kotlin
class BackupStore(private val root: File, private val namespace: String) {
    constructor(context: Context, namespace: String) : this(File(context.filesDir, "backups"), namespace)
    // files land in root/<namespace>/, prune(12) per namespace
}
```

Namespace is the title's package name, or `manual` for SAF-opened files. One-time migration: when
the GRID namespace is first opened, move legacy `files/backups/preferences-*.xml` into
`backups/com.feralinteractive.gridlegends_android/`; idempotent, and a no-op once the directory is
empty. Backup labels: preset key name when guided, `values` otherwise. The Backups screen only
lists the active namespace.

### 7.9 Manifest and naming

`<queries>` gains one `<package>` per entry in `FeralTitles.ALL` (11 entries plus Shizuku).

Display name: the launcher label and the loading wordmark become **Config Tuner**; the subtitle in
the README explains the Feral scope. The `applicationId`, Kotlin package and repository name stay
as they are, so existing installs update in place and the release links keep working. This is a
string change and is easy to revert if a different name is preferred.

### 7.10 Version

1.2.0, versionCode 3. CHANGELOG entry covering the picker, generic editor and title table;
README gains a "Supported titles" table with the same confirmed/unverified distinction used in the
investigation.

## 8. Test fixtures and tests

New fixture `app/src/test/resources/alien-isolation-sample.xml`: a synthetic registry using the
documented Alien: Isolation key names (`Setup`, `VideoSetting_*`, `VideoSetting_*_Visible`,
`DynamicResolutionScaling`, `TemporalAntiAliasing`, `Upscaler`) and the shared framework subtrees.
It is authored for schema coverage, contains no user data, and is labelled synthetic in the test
resource header.

Tests:

1. `FeralTitlesTest` — unique ids and packages; every package matches
   `^com\.feralinteractive\.[a-z0-9_]+$`; exactly one guided title; GRID Legends is it.
2. `RegistryModelTest` — grouping, search by name and path, `GAME` scope excludes
   `HKEY_LOCAL_MACHINE` and the engine subtrees, `ENTIRE` includes them, multi-child
   `Feral Interactive` fallback, `validateText` for integer (rejects `1.5`, accepts `-3`), binary
   (rejects odd length and non-hex, accepts empty), string, and an unknown type (validated as
   read-only, not as text).
3. `GenericDocumentTest` — the Alien fixture parses, round-trips byte-for-byte through
   parse→serialize→parse with identical snapshots, rejects `validateGuided`, accepts
   `validateRegistry`, preserves a `type="float"` value through an edit and a save cycle, and a
   changed value is caught by snapshot comparison.
4. `TitleDiscoveryTest` — the pure state mapping: installed + located → `READABLE`; installed + no
   file → `INSTALLED_ONLY`; open attempt failed → `PRESENT_UNUSABLE`; not installed → omitted.
5. `BackupStoreTest` — namespace isolation (two namespaces cannot see each other's files), legacy
   migration runs once and is idempotent, prune keeps 12 per namespace. Runs against a temp dir.
6. `SaveVerificationTest` — snapshot equality detects a removed key, a changed type, a changed
   value, and an unknown-type value.
7. Existing `ConfigDocumentTest`, `SettingsCatalogTest`, `HeightDropdownTest` pass unchanged except
   for the root-validation message.

The session layer itself is verified on device rather than in JVM tests: one Shizuku bind and a
bounded number of `su` calls for a full discovery pass, measured on the TB520FU.

Manual verification on the TB520FU: GRID Legends guided flow end-to-end (preset change, fixed
height, save, relaunch); one generic title (Alien: Isolation or GRID Autosport) browse → edit →
save → relaunch; manual SAF path on a copied file; migration of existing 1.1.0 backups.

## 9. Risks

| Risk | Mitigation |
|---|---|
| Guided GRID regression while generalising paths | Existing 10 tests stay; GRID golden fixture unchanged; guided branch is the only caller of preset helpers |
| Generic edit corrupts a game's config | Type validation, exact snapshot verify, automatic restore, backup before every write, unknown-node preservation |
| Unverified title lacks `preferences` | Only confirmed titles are listed; unreadable ones show as installed-only; "open a file" always available |
| Package name reaching a shell command | Force-stop accepts only `FeralTitles.ALL` packages |
| Package visibility hides installed games | Explicit `<queries>` entries; no `QUERY_ALL_PACKAGES` |
| Scoped storage makes a readable file look absent | Card copy says "not readable with the current access mode"; access sheet reachable from the picker |
| Two games' backups collide | Per-namespace directories, migrated legacy folder |
| Generic editor encourages blind binary edits | Type badge, hex validation, monospace field, backup + verify behind every save |

## 10. Left to the builder

- Exact Compose decomposition of the picker and registry screens, spacing and card proportions;
  reuse `Components.kt` primitives and the existing type scale.
- Copy wording for empty, error and installed-only states.
- Whether the picker groups titles (GRID / Total War / other) or keeps one grid.
- Keyboard and pen behaviour in the value inspector beyond the existing Ctrl+S / Esc handling.
- Diff presentation when restoring a backup (current screen lists snapshots only).
- Whether discovery also scans unprofiled `feral_app_support` folders when root/Shizuku is active.
  Out of scope for 1.2.0; note it in the README as a possible follow-up.

## 11. Calls made during review

These were open in the first draft and settled by the author; they are listed so they can be
overruled cheaply before implementation.

1. **Generic browser scope.** Default to the game's subtree, with an explicit "Entire registry"
   toggle for engine and hardware sections. Chosen over game-only (hides `IndirectX`,
   `SystemMonitor`, `AutoValueRemap` settings that live in the same file) and over everything-by-
   default (buries a few dozen settings under hundreds of inventory rows).
2. **Unknown value types.** Loaded, preserved and shown read-only, never rejected. Rejecting them
   would make a single future type block every screen, since load, raw apply, save and restore all
   validate.
3. **Access lifetime.** One privileged `AccessSession` per app session, with the mode held outside
   the document. Probing titles with per-title access objects would rebind Shizuku or spawn `su`
   dozens of times on every picker load.
4. **Manual files.** A source, not an access mode: surfaced as a picker card, never auto-opened,
   allowed alongside a privileged session, no force-stop.
5. **Unparseable but present file.** A session-local `PRESENT_UNUSABLE` card state, set only after
   an open attempt fails. Discovery still never reads file contents.
