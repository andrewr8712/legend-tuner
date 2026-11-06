# Changelog — 1.4.0 (Config Tuner)

> Supersedes [1.3.3](CHANGELOG-1.3.3.md).

**Version:** 1.4.0 · versionCode 17
**Package:** `dev.legendtuner.app` (release build)
**Display name:** Config Tuner

First release-signed build. The app is functionally the same as 1.3.3; this release changes how it
is signed and distributed, so it can become the permanent public install.

## Changed

- Added a dedicated 4096-bit RSA release key (`CN=Config Tuner`, valid to 2054). The release APK is
  signed with APK Signature Scheme v2 and v3 and is not debuggable.
- Release builds now use `dev.legendtuner.app`; debug builds remain
  `dev.legendtuner.app.debug`. The two packages can be installed side by side.
- Documented the release-key setup and the separate debug/release signing configuration in
  `keystore/README.md`.

## Compatibility

`dev.legendtuner.app` is a **new package**. It does not update an existing
`dev.legendtuner.app.debug` install, and app-private settings or root/Shizuku grants do not migrate.
Install the signed release separately and grant access again.

## Verification

- 51 unit tests passing, 0 failures.
- 2 Compose UI smoke tests passing on the TB520FU (Android 17).
- `lintDebug` passes with 13 warnings, 0 errors.
- Signed release installed alongside the debug build and launched to the picker.
- Root write test on the TB520FU: Saints Row: The Third at 2560×1440 with render height 1440;
  in-game frame rate was suitably reduced, and both values were still present after quitting.
- `apksigner`: v2 true, v3 true; signer certificate
  `C1:1B:75:6D:D6:EC:91:BB:98:66:C8:42:CB:D2:64:2B:9C:01:F4:BC:43:5F:2F:CB:71:60:8D:B0:5D:FB:E8:47`.

## Known issues carried forward

- Alien: Isolation's Feral crash reporter can appear on the splash at high frame caps.
- Saints Row gyro toggles write correctly but did not respond on Android 17.
- Company of Heroes has no live `ScreenW`/`ScreenH` pair in its first-launch file.
- Total War: EMPIRE, Tropico 3, ROME, Medieval II and XCOM 2 keep their documented unverified
  mapping/preset notes.

## Install

Download `ConfigTuner-1.4.0.apk` from the release.

- Package: `dev.legendtuner.app`
- Version: 1.4.0 (versionCode 17)
- Size: 6,946,773 bytes (6.7 MB)
- Signed with the project's dedicated release key; not on Google Play
- Requires Android 8.0+
- Existing `dev.legendtuner.app.debug` users must install this as a separate app

SHA-256: `3d6e23ec5d2dcdbeb45dd21cb5ecdb1e483df23e57efb3fe11eba70cecf0a02f`
