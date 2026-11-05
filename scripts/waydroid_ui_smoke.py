#!/usr/bin/env python3
"""Waydroid UI smoke test for Config Tuner.

Drives the real Compose UI over ADB. It installs the debug APK, imports the
committed Tomb Raider fixture through the system picker, changes Fullscreen,
saves, and verifies the XML value on disk.

Usage:
    python3 scripts/waydroid_ui_smoke.py
    python3 scripts/waydroid_ui_smoke.py --serial 192.168.240.112:5555 \
        --apk app/build/outputs/apk/debug/app-debug.apk
"""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

BOUNDS = re.compile(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
ROOT = Path(__file__).resolve().parents[1]
PACKAGE = "dev.legendtuner.app.debug"
ACTIVITY = f"{PACKAGE}/dev.legendtuner.app.MainActivity"


class UiError(RuntimeError):
    pass


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--serial", default="192.168.240.112:5555")
    parser.add_argument("--apk", default=str(ROOT / "app/build/outputs/apk/debug/app-debug.apk"))
    parser.add_argument("--fixture", default=str(ROOT / "testdata/tomb-raider-preferences-sample.xml"))
    parser.add_argument("--timeout", type=float, default=20.0)
    args = parser.parse_args()

    smoke = Smoke(args.serial, args.timeout)
    try:
        smoke.run(Path(args.apk), Path(args.fixture))
    except UiError as e:
        print(f"FAIL: {e}", file=sys.stderr)
        return 1
    print("PASS: Config Tuner imported, edited and saved in Waydroid")
    return 0


class Smoke:
    def __init__(self, serial: str, timeout: float):
        self.serial = serial
        self.timeout = timeout

    def adb(self, *args: str, check: bool = True) -> str:
        result = subprocess.run(
            ["adb", "-s", self.serial, *args],
            text=True, capture_output=True,
        )
        if check and result.returncode != 0:
            raise UiError(f"adb {' '.join(args)} failed: {result.stderr.strip()}")
        return result.stdout

    def dump(self) -> ET.Element:
        self.adb("shell", "uiautomator", "dump", "/sdcard/window.xml", check=False)
        raw = self.adb("exec-out", "cat", "/sdcard/window.xml")
        try:
            return ET.fromstring(raw)
        except ET.ParseError as e:
            raise UiError(f"could not parse UI hierarchy: {e}") from e

    def find(self, text: str, *, x_min: int = 0, exact: bool = True) -> tuple[int, int, int, int] | None:
        for node in self.dump().iter("node"):
            label = (node.get("text") or node.get("content-desc") or "").strip()
            if (label == text) if exact else (text.lower() in label.lower()):
                match = BOUNDS.match(node.get("bounds", ""))
                if not match:
                    continue
                x1, y1, x2, y2 = map(int, match.groups())
                if x1 < x_min:
                    continue
                return x1, y1, x2, y2
        return None

    def wait(self, text: str, *, x_min: int = 0, exact: bool = True) -> tuple[int, int, int, int]:
        deadline = time.monotonic() + self.timeout
        while time.monotonic() < deadline:
            bounds = self.find(text, x_min=x_min, exact=exact)
            if bounds:
                return bounds
            time.sleep(0.5)
        raise UiError(f"timed out waiting for UI text {text!r}")

    def tap(self, text: str, *, x_min: int = 0, exact: bool = True) -> None:
        x1, y1, x2, y2 = self.wait(text, x_min=x_min, exact=exact)
        self.adb("shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2))
        time.sleep(0.6)

    def tap_row(self, text: str, *, offset_x: int = 220) -> None:
        x1, y1, x2, y2 = self.wait(text, exact=True)
        self.adb("shell", "input", "tap", str(x1 + offset_x), str((y1 + y2) // 2))
        time.sleep(0.6)

    def run(self, apk: Path, fixture: Path) -> None:
        if not apk.is_file():
            raise UiError(f"APK not found: {apk}")
        if not fixture.is_file():
            raise UiError(f"fixture not found: {fixture}")

        remote = "/sdcard/Download/tomb-raider-smoke.xml"
        self.adb("install", "-r", str(apk))
        self.adb("push", str(fixture), remote)
        # Waydroid's DocumentsUI only exposes a pushed file after MediaProvider sees it.
        self.adb(
            "shell", "am", "broadcast",
            "-a", "android.intent.action.MEDIA_SCANNER_SCAN_FILE",
            "-d", f"file://{remote}",
            check=False,
        )
        self.adb("shell", "am", "force-stop", PACKAGE, check=False)
        self.adb("shell", "am", "start", "-W", "-n", ACTIVITY)
        self.wait("CONFIG TUNER")

        self.tap("Tomb Raider")
        self.wait("Manual file")
        self.tap("Manual file")

        # The picker may restore Recent or Downloads from the previous run.
        deadline = time.monotonic() + self.timeout
        while time.monotonic() < deadline:
            if self.find("tomb-raider-smoke.xml"):
                self.tap("tomb-raider-smoke.xml")
                break
            if self.find("Show roots"):
                self.tap("Show roots")
                if self.find("Downloads"):
                    self.tap("Downloads")
            time.sleep(0.5)
        else:
            raise UiError("Downloads picker never showed the fixture")

        self.wait("Edit settings")
        self.tap("Edit settings")
        self.wait("Fullscreen")
        self.tap_row("Fullscreen")
        self.wait("Stored key: FullScreen", exact=False)
        self.tap("Off", x_min=1500)
        self.tap("Save changes", x_min=1500)
        self.wait("Saved", exact=False)

        self.adb("pull", remote, "/tmp/tomb-raider-smoke-result.xml", check=True)
        value = read_tomb_fullscreen(Path("/tmp/tomb-raider-smoke-result.xml"))
        if value != "0":
            raise UiError(f"expected FullScreen=0 after save, got {value!r}")

        # Dirty Back must prompt instead of silently leaving the editor.
        self.tap("On", x_min=1500)
        self.adb("shell", "input", "keyevent", "4")
        self.wait("Discard unsaved changes?")
        self.tap("Discard")
        self.wait("Dashboard")

        # Restore the source fixture and leave the app stopped.
        self.adb("push", str(fixture), remote)
        self.adb(
            "shell", "am", "broadcast",
            "-a", "android.intent.action.MEDIA_SCANNER_SCAN_FILE",
            "-d", f"file://{remote}",
            check=False,
        )
        self.adb("shell", "am", "force-stop", PACKAGE, check=False)


def read_tomb_fullscreen(path: Path) -> str | None:
    root = ET.parse(path).getroot()
    for key in root.iter("key"):
        if key.get("name") != "Setup":
            continue
        chain = [key.get("name")]
        # Locate the Setup key under Tomb Raider, then inspect its direct values.
        parent_text = ET.tostring(key, encoding="unicode")
        if "Tomb Raider" not in parent_text:
            continue
        for value in key.findall("value"):
            if value.get("name") == "FullScreen":
                return (value.text or "").strip()
    return None


if __name__ == "__main__":
    raise SystemExit(main())
