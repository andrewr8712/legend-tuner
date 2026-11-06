# Candidate games — research notes

These are PC-to-Android ports considered after the Feral catalogue was covered. None are supported
yet. The notes distinguish a **verified config format** from a guess.

| Candidate | Package | Why it is interesting | Config format | Confidence |
|---|---|---|---|---|
| Civilization VI | `com.aspyr.civvi` | Modern, still updated, deep graphics options | INI-like `AppOptions.txt` + `GraphicsOptions.txt`, internal data | **Verified on tablet** — [notes](civilization-vi.md) |
| Neverwinter Nights: Enhanced Edition | `com.beamdog.nwnandroid` | Actively patched PC port | `settings.tml` + `nwn.ini` | High, not device-verified |
| Baldur's Gate: EE / BGII: EE / Icewind Dale: EE / Planescape: Torment: EE | Beamdog packages | One Infinity Engine format could cover four titles | `baldur.ini` / engine `.ini` | High, not device-verified |
| Terraria | `com.and.games505.TerrariaPaid` | Large audience, long-running mobile port | `config.json` | High, not device-verified |
| Star Wars: Knights of the Old Republic | `com.aspyr.swkotor` | Classic PC port with a simple config | `swkotor.ini` | Medium |
| XCOM: Enemy Unknown | package to verify | Would fit the existing Feral-style registry if it uses it | Unknown | Low–medium |

## Why the list stops here

Unity games such as Dead Cells, Northgard, Slay the Spire, Balatro and Stardew Valley generally keep
settings in PlayerPrefs, binary saves or non-graphics save data. They are not good fits for a
plaintext settings editor without a different product design.

## Recommendation order

1. **Civilization VI** — highest modern value, but needs an INI backend.
2. **Beamdog Infinity Engine bundle** — one parser for four games.
3. **Neverwinter Nights: EE** — known text config.
4. **Terraria** — simple JSON, but fewer graphics controls.
5. **KOTOR / XCOM: Enemy Unknown** — lower priority.

See [civilization-vi.md](civilization-vi.md) for the device evidence and parser requirements.
