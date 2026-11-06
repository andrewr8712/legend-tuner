# Changelog — 1.3.2 (Config Tuner)

> Supersedes [1.3.1](CHANGELOG-1.3.1.md).

**Version:** 1.3.2 · versionCode 15
**Package:** `dev.legendtuner.app.debug` (debug build; same package/key as 1.3.1)
**Display name:** Config Tuner

Accessibility and empty-state polish. No title or catalogue changes.

## Changed

- Added merged accessibility semantics to installed-title cards: screen readers now announce the
  title, readiness state and control count as one button, rather than walking through decorative
  glyphs.
- Added explicit semantics to **Also supported** rows and the nav-rail monogram.
- Added an **Open a preferences file** action to the zero-installed-titles empty state, so the state
  has a direct recovery path instead of only explanatory copy.
- Marked the root `CHANGELOG.md` as historical and pointed it at the per-release notes.

## Verification

- 50 unit tests passing.
- `lintDebug` passes with 12 warnings, 0 errors.
- Installed on the TB520FU; title-card content descriptions and empty-state action verified.
- Picker still verified at normal and 1.3× font scale from 1.3.1.

## Known issues carried forward

- Alien: Isolation's Feral crash reporter can appear on the splash at high frame caps.
- Saints Row gyro toggles write correctly but did not respond on Android 17.
- Company of Heroes has no live `ScreenW`/`ScreenH` pair in its first-launch file.
- Total War: EMPIRE, Tropico 3, ROME, Medieval II and XCOM 2 keep their documented unverified
  mapping/preset notes.

## Install

Download `ConfigTuner-1.3.2-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.3.2 (versionCode 15)
- Debug-signed with the same key as 1.3.1; installs over 1.3.1 in place
- Requires Android 8.0+

SHA-256: `523038dbf2b8507fa05e5f8843f50a21632027655d9f1301b9249ad77ffce635`
