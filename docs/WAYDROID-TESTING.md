# Testing in Waydroid

Waydroid is the fast UI loop for Config Tuner. It does not replace a real Feral game on a tablet, but it exercises the Compose UI, the system document picker, file writes and the Back confirmation against a real Android runtime.

## Prerequisites

- Waydroid is running on the host.
- The ADB connection is authorised:

```bash
waydroid status
waydroid adb connect
adb -s 192.168.240.112:5555 shell getprop ro.build.version.sdk
```

The default serial used by the harness is `192.168.240.112:5555`.

## Run the smoke test

```bash
python3 scripts/waydroid_ui_smoke.py
```

The script installs `app/build/outputs/apk/debug/app-debug.apk`, imports `testdata/tomb-raider-preferences-sample.xml` through the **Manual file** picker, changes **Fullscreen** to **Off**, saves it, verifies the XML on disk, then checks that a dirty Back press shows **Discard unsaved changes?**.

Useful options:

```bash
python3 scripts/waydroid_ui_smoke.py \
  --serial 192.168.240.112:5555 \
  --apk app/build/outputs/apk/debug/app-debug.apk \
  --fixture testdata/tomb-raider-preferences-sample.xml
```

## MediaProvider quirk

A file pushed with `adb push` is visible in DocumentsUI but is not readable by the app until MediaProvider has scanned it. The harness broadcasts the scan automatically. For manual testing:

```bash
adb -s 192.168.240.112:5555 shell am broadcast \
  -a android.intent.action.MEDIA_SCANNER_SCAN_FILE \
  -d file:///sdcard/Download/tomb-raider-smoke.xml
```

This is a Waydroid/MediaProvider issue, not an app permission bug.

## What this does not cover

- Root and Shizuku access paths.
- A real installed Feral game and its private `Android/data` directory.
- Sensor/gyro behaviour.
- Production signing or store installation.
