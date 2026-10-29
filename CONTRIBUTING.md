# Contributing

Thanks for taking a look at Legend Tuner.

## Development

Requirements: JDK 17, Android SDK platform 35, build-tools 35.0.0.

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

Keep changes focused. The parser preserves unknown keys and values; avoid replacing that with a fixed schema.

## Pull requests

- Describe the behavior change and the device/Android version used to test it.
- Add or update tests for parser, catalog, or backup changes.
- Do not commit `keystore.properties`, a `.jks`, `local.properties`, `dist/`, or build output.
- Do not include real game config files or personal identifiers.

## Reporting bugs

Include the app version, Android version, tablet model, access mode (Root/Shizuku/Manual), and the exact error text. Redact account IDs and device identifiers from any attached file.

This is an unofficial project and is not affiliated with the game's rights holders.
