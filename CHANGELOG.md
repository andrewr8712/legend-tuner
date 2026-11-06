# Changelog

> **Historical file.** Entries below stop at 1.2.1. Current release notes live in
> [`docs/CHANGELOG-1.3.2.md`](docs/CHANGELOG-1.3.2.md) and on
> [GitHub Releases](https://github.com/andrewr8712/legend-tuner/releases). The per-release files under
> `docs/` are the source of truth.

All notable changes to Config Tuner (formerly Legend Tuner) are documented here. The project follows [Semantic Versioning](https://semver.org/).

## [1.2.1] — 2026-11-05

### Fixed

- Hitman's inverted `Disable*` toggles now select the option they display. The inspector previously
  compared the raw stored token against the option list, so a stored `1` could show **Off** while
  highlighting **On**.
- The inspector footer now labels the raw token as **Stored value**, keeping the inverted storage
  visible without contradicting the displayed state.

### Verified

- TB520FU, Android 17: installed over 1.2.0; Hitman Performance+ Bloom showed **On** with the **On**
  option selected while `DisableBloom_plus = 0`.
- 19/19 unit tests passing; debug APK builds.

Release notes: [`docs/CHANGELOG-1.2.1.md`](docs/CHANGELOG-1.2.1.md).

## [1.2.0] — 2026-11-05

### Added

- **Multi-title support.** Title picker on launch for eight Feral Interactive games; installed games
  are cards, supported-but-absent games appear under **Also supported**.
- **Guided catalogues** for GRID Autosport, Alien: Isolation, Saints Row: The Third, NAPOLEON,
  Hitman: Blood Money, Lara Croft: Guardian of Light and Lara Croft: Temple of Osiris.
- **Preset variants as tabs** — block presets, GRID Autosport's prefixed families and Hitman's four
  suffixed families.
- Common-value dropdowns for render height (480–2160 px) and frame caps (24–144 fps).
- Saints Row screen resolution (`ScreenW`/`ScreenH` as a pair) and resolution scale (50–100 %).
- Manual import keeps the selected title, so GRID Legends can be edited from a saved file while the
  game is not installed.
- `docs/catalogues/` inventories and eight sanitised fixtures.

### Changed

- Display name is now **Config Tuner**; the base `applicationId` is unchanged, and distributed
  debug builds remain `dev.legendtuner.app.debug`, so 1.1.0 debug installs update in place.
- Version code 3. Registry validation is title-aware; GRID Legends keeps strict preset checks.
- Raw XML is now the expert fallback rather than the only path for non-GRID titles.

### Fixed

- Non-GRID titles no longer fail on GRID-specific validation after parsing.
- Lara Croft graphics root corrected to `Software/Crystal Dynamics/.../Graphics`.
- Hitman's `VideoSetting_ShadowMapping` key typo.
- Picker no longer reports "launch once" from a `canRead()` check that Android 11+ always denies.

### Verified

- TB520FU, Android 17, KernelSU root: GRID Autosport shading write and revert, Hitman triple write
  and revert, Saints Row resolution write and revert, GRID Legends manual import.
- 18/18 unit tests; debug APK builds.

Detailed release notes, including remaining verification gaps: [`docs/CHANGELOG-1.2.0.md`](docs/CHANGELOG-1.2.0.md).

## [1.1.0] — 2026-10-30

### Added

- Fixed render height now uses a dropdown of common 480p–2160p values instead of a slider.
- Dropdown labels mark the game default (838), Full HD, QHD, TB520FU native (1840), and 4K.
- A non-standard current height is automatically preserved as a custom dropdown entry.

### Changed

- Version code bumped to 2 so the APK updates over 1.0.0.
- Public repository setup: MIT license, CI build, release-based APK distribution.

### Verified

- Lenovo Yoga Tab Plus TB520FU, Android 17, Shizuku access.

## [1.0.0] — 2026-10-30

### Added

- Tablet-first adaptive Compose UI.
- Root, Shizuku, and manual SAF access.
- Battery Saver, Graphics, and Performance preset editing.
- Automatic backups, read-back verification, and restore-on-failure.
- Raw XML editor with unknown-key preservation.
- 30–144 fps cap, resolution scaling, dynamic resolution, visual/effect/display settings.
