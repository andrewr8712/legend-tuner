# Changelog — 1.2.1 (Config Tuner)

> Supersedes [1.2.0](CHANGELOG-1.2.0.md); superseded by [1.2.2](CHANGELOG-1.2.2.md).

**Version:** 1.2.1 · versionCode 4
**Package:** `dev.legendtuner.app.debug` (debug build; unchanged from 1.2.0)
**Display name:** Config Tuner

This is a focused patch release for the inverted-toggle fix found after 1.2.0 shipped.

## Fixed

- **Hitman's `Disable*` toggles selected the wrong option.** The UI labelled the value correctly —
  for example, `DisableBloom_plus = 0` displayed **On** — but the option list compared the raw token
  against the choices and highlighted **On** for `1` / **Off** for `0`. `Control.effectiveToken()`
  now applies the inversion once for both labelling and selection.
- The inspector footer now says **Stored value** for the raw token, so the inverted storage remains
  explicit rather than looking like a contradiction.

## Verified

| Check | Result |
|---|---|
| Unit tests | 19/19 passing |
| Debug build | `assembleDebug` succeeds |
| Device | Lenovo Yoga Tab Plus TB520FU, Android 17 |
| Install | 1.2.1 installed over 1.2.0 in place |
| Hitman check | Performance+ → Bloom: **On** selected, `Stored key: DisableBloom_plus`, `Stored value: 0` |
| Logcat | No app errors |

No settings were changed or saved during the verification.

## Install

Download `ConfigTuner-1.2.1-debug.apk` from the release.

- Package: `dev.legendtuner.app.debug`
- Version: 1.2.1 (versionCode 4)
- Debug-signed, same key/package as the 1.2.0 debug build
- Requires Android 8.0+

SHA-256: `c7b6b0f8203746292ab4115ca8e3b9888c78bb3ceb1bd38c44721a1a5752183f`

## Build

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
