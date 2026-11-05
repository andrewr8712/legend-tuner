# Config Tuner

[![Android CI](https://github.com/andrewr8712/legend-tuner/actions/workflows/android.yml/badge.svg)](https://github.com/andrewr8712/legend-tuner/actions/workflows/android.yml)
[![Latest release](https://img.shields.io/github/v/release/andrewr8712/legend-tuner)](https://github.com/andrewr8712/legend-tuner/releases/latest)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **How this project was built.** It has been made with DeepSeek V4.1-Flash-1017, by someone who is
> not a developer. The initial prompt was:
>
> > There is an android game i like to play called Grid:Legends.
> > It has a plaintext configuration file that can be manually edited to adjust various graphical and
> > performance options.
> >
> > Instead of doing this in a text editor, I would like a native android app with a gui for easy
> > adjustments. Can you help develop this?
>
> The only other input was, after trying the result:
>
> > Can the fixed render height slider be changed - it's too difficult to set an exact value.
> > replace it with a drop down of values that correspond to common screen resolution heights.
>
> The 1.2.0 overhaul was steered the same way: the guided GUI for every title, the settings missing
> from Hitman's registry, and the resolution controls all came from comparing the app against the
> real files.

An unofficial, tablet-first Android app for editing the graphics settings of **Feral Interactive**'s
Android ports — with readable, guided controls instead of hand-editing XML.

Formerly Legend Tuner, which edited GRID Legends only. Version 1.2.x keeps the same package
identity as 1.1.0, so existing debug installs update in place, and adds seventeen more titles.

![Config Tuner title picker](docs/screenshots/picker.png)

## Why

Every Feral Android port writes a plaintext XML registry to:

```text
Android/data/<package>/files/feral_app_support/preferences
```

The schema differs per game, but the shape does not: `registry > key > value` entries with string,
integer, double and array types. The games do not expose every setting in their own menus, and
Android 11+ blocks normal apps from other apps' `Android/data` folders. Config Tuner gives each title
a guided editor over that file, with backups and verified writes, plus a raw XML editor for the rest.

## Supported titles

| Title | Package | Guided controls | Variants |
|---|---|---:|---|
| GRID Legends | `com.feralinteractive.gridlegends_android` | 28 | Battery Saver · Graphics · Performance |
| GRID Autosport | `com.feralinteractive.gridas` | 29 | Current · Performance cache · Power saver cache |
| Alien: Isolation | `com.feralinteractive.alienisolation_android` | 12 | flat |
| Saints Row: The Third | `com.feralinteractive.saintsrow3_android` | 19 | Graphics · Battery saver · Performance |
| NAPOLEON: Total War | `com.feralinteractive.napoleon_android` | 16 | flat (quality index) |
| Hitman: Blood Money | `com.feralinteractive.hitmanbloodmoney_android` | 18 | Live · Battery saver · Graphics · Performance · Performance+ |
| Lara Croft: Guardian of Light | `com.feralinteractive.laracroftgol_android` | 10 | Graphics · Battery saver · Performance · Performance+ |
| Lara Croft: Temple of Osiris | `com.feralinteractive.laracrofttoo_android` | 11 | Graphics · Battery saver · Performance |
| Sid Meier's Railroads! | `com.feralinteractive.railroads_android` | 16 | flat |
| Tomb Raider | `com.feralinteractive.tombraider_android` | 24 | Battery saver · Graphics · Performance · Performance+ |
| Hitman: Absolution | `com.feralinteractive.hitmanabsolution_android` | 25 | Battery saver · Framerate · Graphics |
| Company of Heroes | `com.feralinteractive.companyofheroes_android` | 24 | flat, split roots |
| Total War: EMPIRE | `com.feralinteractive.empire_android` | 28 | flat, quality + menu subtrees |
| Tropico 3 | `com.feralinteractive.tropico3` | 15 | flat, `GFX` + engine subtree |
| ROME: Total War | `com.feralinteractive.rometw` | 24 | flat live values; stored presets not exposed |
| Medieval II: Total War | `com.feralinteractive.medieval2_android` | 24 | flat, `VideoSettings` subtree |
| ROME: Barbarian Invasion | `com.feralinteractive.rometwbi` | 24 | shares ROME catalogue; own root/build |
| ROME: Alexander | `com.feralinteractive.rometwalex` | 24 | shares ROME catalogue; own root/build |

Titles are detected by package. A card is only actionable when the game is installed, or when you
open a saved `preferences` file for it. Any other supported title is listed under **Also supported**
and can be opened from a file pulled from a backup or another device.

## Features

- **Game picker on launch** — one active document at a time, with installed titles first.
- **Guided editors for all eighteen titles** — grouped cards, plain-language labels, a description for
  every control, option lists with the stored key shown for provenance.
- **Preset variants as tabs** — per-preset blocks (Saints Row, Lara Croft), prefixed families
  (GRID Autosport) and suffixed families (Hitman) are first-class, not raw keys. Tomb Raider opens
  on the game’s active preset; titles that do not expose an active selector show a clear warning.
- **Unsaved-change protection** — system Back and “choose another game” ask before discarding a
  draft.
- **Common-value dropdowns** — render height (480–2160 px) and frame caps (24–144 fps) are lists of
  common values rather than free steppers.
- **Screen resolution and resolution scale** — Saints Row exposes `ScreenW`/`ScreenH` as a paired
  resolution list (1280 × 720 … 2560 × 1600) and the render scale as 50–100 %.
- **Triple-aware Hitman controls** — uniform triples are editable and written back as a triple;
  ladders such as `1024, 1536, 2048` stay read-only, because flattening them would corrupt the preset.
- **Inverted keys handled correctly** — Hitman's `DisableBloom`/`DisableDepthOfField`/
  `DisableFinishFilter` are shown as Bloom/DOF/Finish filter, On meaning enabled.
- **Raw XML fallback** — full registry editing with validation and unknown-key preservation.
- **Automatic backups** — the previous file is stored before every save and restore; each title keeps
  its own 12 snapshots and restores are scoped to that title.
- **Safe writes** — validate, back up, write, read back, parse, restore on failure.
- **Manual import keeps the title** — open a GRID Legends file from anywhere and get its guided
  editor, not a generic raw view.
- **No network permission, no analytics, no telemetry, no game assets.**

## Screenshots

| Guided editor | Preset families | Resolution controls |
|---|---|---|
| ![GRID Autosport settings](docs/screenshots/grid-autosport-settings.png) | ![Hitman preset tabs](docs/screenshots/hitman-preset-tabs.png) | ![Saints Row resolution](docs/screenshots/saints-row-resolution.png) |

Captured on a Lenovo Yoga Tab Plus TB520FU. The picker shows installed titles first and any supported
game that is not installed under **Also supported**.

## Install

**[Download the latest APK](https://github.com/andrewr8712/legend-tuner/releases/latest)** and open it
on the tablet, or:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

- Package: `dev.legendtuner.app` (debug builds install as `dev.legendtuner.app.debug`)
- Minimum Android: 8.0 (API 26) · Target: Android 15 (API 35)
- Version 1.2.9 (versionCode 12)
- Signed debug build

## First run

1. Launch Config Tuner. Installed Feral titles appear as cards.
2. Pick a title. If Android blocks direct access, choose a route:
   - **Root** — read/write through `su`; works on the TB520FU with KernelSU.
   - **Shizuku** — a shell-UID service for non-root devices; restart after reboot.
   - **Direct** — only usable on older/permissive Android versions.
   - **Manual file** — pick a copy with the system picker; saving writes back to that copy.

The expected path is shown on the setup screen for the selected title, for example:

```text
/Android/data/com.feralinteractive.gridas/files/feral_app_support/preferences
```

Android's system picker cannot browse `Android/data` itself. Manual mode is the fallback for devices
without Root or Shizuku, and for titles that are not installed.

## Use

1. Fully close the game. Feral titles rewrite `preferences` when they exit.
2. Open **Settings** (or **Graphics** for GRID Legends) and choose a preset tab.
3. Tap a row to read its description and pick a value.
4. Press **Save changes**. A backup is written first, then the file is written and verified.
5. Launch the game.

`Ctrl+S` saves and `Esc` closes the inspector with a keyboard attached.

### What is not editable, and why

- A control whose key is absent from the file is shown as **Not stored in this file** and cannot be
  written — Config Tuner never creates registry values.
- Hitman's per-preset values are comma triples. Uniform triples are editable; ladders are read-only.
- Amber dots mark rows where the option list or tier names still need one in-game confirmation.
- Menu entries the game shows but does not store on Android (Lara Croft's volumetric/water effects,
  Alien's field of view) are deliberately absent.

## Safety

- Close the game before saving.
- Every write creates a backup in app-private storage first.
- Writes are verified by reading the file back and parsing it; the original is restored on failure.
- No `INTERNET` permission and no network requests.
- Editing game configuration is at your own risk. This is an unofficial fan tool.

## Build

Requirements: JDK 17 or 21 (JDK 21 is used locally), Android SDK platform 35, build-tools 35.0.0.

```bash
git clone https://github.com/andrewr8712/legend-tuner.git
cd legend-tuner

export JAVA_HOME=/path/to/jdk
export ANDROID_HOME=/path/to/android-sdk

./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

A clean checkout uses Gradle's machine-local debug signing key and leaves the release unsigned.
Maintainers can supply a local `keystore.properties` (ignored by git) to sign both debug and
release builds with the same local identity, then run `./gradlew :app:assembleRelease`; the signed
artifact is `app/build/outputs/apk/release/app-release.apk`. See
[keystore/README.md](keystore/README.md).

## Tests

```bash
./gradlew :app:testDebugUnitTest
```

49 tests cover registry parsing and round-trip preservation, preset edits, live-value mirroring,
unknown keys and types, malformed XML, icon vectors, the title table, inverted-toggle selection,
catalogue conformance and the config-loading boundary. Every guided control must have a label, a
description of at least 40 characters, a known group, and unique keys with at least two options for
selectable controls.

For a real UI pass on the host, use the Waydroid smoke harness:

```bash
python3 scripts/waydroid_ui_smoke.py
```

It imports a fixture, edits and saves a value, verifies the XML, and checks the unsaved-Back dialog.

## Project layout

```text
app/src/main/java/dev/legendtuner/app/
  access/        root shell, Shizuku service, SAF, per-title path discovery
  data/          registry parser, title table, catalogues, presets, backups
  ui/            picker, guided editor, dashboard, raw XML, backups
app/src/main/aidl/  Shizuku IFileService
docs/catalogues/    per-title control inventories and provenance
docs/superpowers/specs/  multi-title design record
testdata/           twelve sanitised real preferences samples
docs/WAYDROID-TESTING.md  Waydroid ADB setup and automated UI smoke
docs/website/       website agent brief, design tokens, mark and current screenshot
scripts/            Waydroid UI smoke harness
docs/screenshots/   real device captures used in this README
prototype/          tablet design prototype and screenshots
DESIGN.md           historical GRID Legends schema notes and design rationale
docs/CHANGELOG-1.2.9.md  current release notes (ROME: Alexander)
docs/CHANGELOG-1.2.8.md  previous release notes (ROME: Barbarian Invasion)
docs/CHANGELOG-1.2.7.md  older release notes (Medieval II: Total War)
docs/CHANGELOG-1.2.6.md  older release notes (ROME: Total War)
docs/CHANGELOG-1.2.5.md  older release notes (Total War: EMPIRE, Tropico 3)
docs/CHANGELOG-1.2.4.md  older release notes (Company of Heroes, unsaved changes, Waydroid smoke)
docs/CHANGELOG-1.2.3.md  older release notes (Railroads, Tomb Raider, Hitman: Absolution)
docs/CHANGELOG-1.2.2.md  previous release notes (backup safety, FPS/resolution, motion controls)
docs/CHANGELOG-1.2.1.md  older patch notes (inverted-toggle fix)
docs/CHANGELOG-1.2.0.md  previous release notes, verification and known gaps
```

## Compatibility

- Designed for the Lenovo Yoga Tab Plus TB520FU (12.7″, 2944 × 1840).
- Verified on Android 17 with KernelSU root; Shizuku and manual modes remain supported.
- `minSdk 26` / `targetSdk 35`.
- Direct filesystem access is only usable on older or permissive Android versions.

## Disclaimer

This project is unofficial and is not affiliated with or endorsed by Feral Interactive, Sega,
Codemasters, Electronic Arts, Eidos, IO Interactive, Crystal Dynamics, The Creative Assembly, Deep
Silver, Koch Media, THQ Nordic or the franchises named above. All game titles and related marks are
the property of their respective owners. No game code, artwork, audio or save data is included; the
sanitised preferences samples in `testdata/` exist only to test the parser. See [NOTICE](NOTICE).

## License

Code is released under the [MIT License](LICENSE). See [NOTICE](NOTICE) for bundled font licences and
trademark attribution.
