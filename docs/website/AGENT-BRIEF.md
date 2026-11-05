# Config Tuner website — agent brief

**Status:** current for app release **1.2.7** (2026-11-06)  
**Assumption:** the website is for **Config Tuner**, the Android app in this repository. If it is for
something else, replace the product section but keep the collaboration protocol.

This brief is a shared knowledge pack for the website agent. It includes verified product facts,
recommended visual direction, copy guardrails and available assets. I can review the website's copy,
layout, accessibility and colour decisions through the coordinator.

---

## 1. Product in one paragraph

Config Tuner is an unofficial, tablet-first Android app for editing the graphics settings of Feral
Interactive's Android ports. It replaces hand-editing the games' plaintext XML registry with guided
controls, validated writes, automatic backups and a raw XML fallback. It supports **16 titles** and
is distributed as a debug-signed APK through GitHub Releases.

Former name: **Legend Tuner**. The package identity stayed the same between 1.1.0 and 1.2.x so debug
installs update in place.

## 2. Verified facts — do not invent around these

| Fact | Value |
|---|---|
| Current release | **1.2.7** (versionCode 10) |
| Release URL | https://github.com/andrewr8712/legend-tuner/releases/tag/v1.2.7 |
| Latest download | https://github.com/andrewr8712/legend-tuner/releases/latest |
| Package | `dev.legendtuner.app.debug` |
| Android support | Android 8.0+ (`minSdk 26`), target SDK 35 |
| Distribution | Debug-signed APK on GitHub; **not on Google Play** |
| Supported titles | **16** |
| Unit tests | **47** |
| Network permission | **None** |
| Analytics / telemetry | **None** |
| Backups | 12 per title, title-scoped |
| Save path | validate → back up → write → read back → parse → rollback on failure |
| Access routes | Root, Shizuku, Manual file, Direct (older/permissive Android only) |

Do **not** claim: “all Feral games”, “every setting”, “no root ever needed”, “Play Store”,
“official”, “guaranteed safe on every device”, or “works with every game version”.

## 3. What the app actually does

- Detects installed supported titles by package.
- Opens each title's `Android/data/<package>/files/feral_app_support/preferences`.
- Shows grouped, plain-language controls with the stored registry key for provenance.
- Handles preset families and rows carefully instead of flattening raw values.
- Keeps a raw XML editor for keys the catalogue does not cover.
- Backs up before every save; verifies the written file and restores on failure.
- Uses no network, analytics or telemetry.

Android 11+ blocks normal apps from other apps' `Android/data` folders. The site should explain the
four routes honestly rather than presenting Root/Shizuku as an afterthought.

## 4. Supported titles

| Title | Guided controls |
|---|---:|
| GRID Legends | 28 |
| GRID Autosport | 29 |
| Alien: Isolation | 12 |
| Saints Row: The Third | 19 |
| NAPOLEON: Total War | 16 |
| Hitman: Blood Money | 18 |
| Lara Croft: Guardian of Light | 10 |
| Lara Croft: Temple of Osiris | 11 |
| Sid Meier's Railroads! | 16 |
| Tomb Raider | 24 |
| Hitman: Absolution | 25 |
| Company of Heroes | 24 |
| Total War: EMPIRE | 28 |
| Tropico 3 | 15 |
| ROME: Total War | 24 |
| Medieval II: Total War | 24 |

Packages and variants are in the repository README. Do not show an older count such as “12 titles”.

## 5. Recommended visual direction

### Primary: “field instrument / config bench”

The app already looks like a technical instrument: near-black surfaces, a single acid-lime signal colour,
condensed uppercase display type, thin rules, and restrained amber/red states. The website should feel
like the same product, not a generic gaming site.

- **Canvas:** near-black `#070908`, slightly lifted cards `#121512` / `#171B17`.
- **Signal:** lime `#D7FF3F` for primary CTA, active state, selected tab and key metrics only.
- **Body:** off-white `#F3F5EF`; muted `#98A092`; dim `#5E675D` for tertiary metadata.
- **Warnings:** amber `#FFB454`; errors/destructive: red `#FF6259`.
- **Type:** `Barlow Condensed` for display/headings; `Inter` or `IBM Plex Sans` for body;
  `JetBrains Mono` for paths, package names and registry keys.
- **Layout:** 12-column grid, max 1280px, generous outer margins, thin 1px lines, square-ish corners
  (2–8px). Avoid glassmorphism, purple neon, huge rounded pills and fake game artwork.
- **Motion:** small line draws, hover lifts, staggered card entrances. Respect
  `prefers-reduced-motion`; never autoplay a heavy video.
- **Imagery:** real app screenshots in simple device frames. No copyrighted Feral game art, logos or
  box art. The screenshots are UI only.

### Alternative: “warm archive”

If the site needs a softer editorial feel, use warm paper/olive instead of pure black: `#F1EBDD`
paper, `#1E241B` ink, `#B9DD31` lime, amber accents, Barlow Condensed headlines. Keep the same
information hierarchy. This is an option, not the default; the dark instrument look is more coherent
with the app.

### Colour rules

- Lime is a **signal**, not a background for long text. Use near-black text on lime buttons.
- Body copy should be off-white or muted grey on near-black, never lime.
- Reserve amber for caveats (“debug build”, “root recommended”) and red for destructive actions.
- Do not use low-contrast `#5E675D` for essential body text; it is for metadata only.
- Check WCAG AA contrast for all text, focus states and button labels.

## 6. Suggested site map

1. **Hero** — “Feral settings without the XML.” One-sentence explanation, primary Download APK button,
   secondary GitHub link.
2. **Proof strip** — 16 titles · 47 unit tests · no network · open source. Use only verified numbers.
3. **How it works** — pick a title → choose Root/Shizuku/Manual → edit guided controls → verified save.
4. **Safety** — backup before write, read-back verification, rollback on failure, no telemetry.
5. **Supported titles** — searchable/filterable table or compact grid; show control counts.
6. **Screenshots** — picker + editor + preset tabs; newest screenshot first.
7. **Install** — GitHub release, Android 8.0+, debug-signed, no Play Store.
8. **FAQ** — Why root? Is it safe? What happens on game updates? Why is it debug-signed? Does it
   collect data? Can I use it with a game not listed?
9. **Footer** — unofficial fan tool disclaimer, MIT licence, GitHub, issue tracker.

## 7. Copy guidance

- Voice: technical, direct, slightly wry. Short sentences. No hype.
- Lead with the user problem: Feral hides useful graphics settings in a registry file; Android blocks
  easy access; Config Tuner makes the file readable and recoverable.
- Say **“guided controls over the game's own file”**, not “mods” or “trainer”.
- Be explicit that the app writes the game's own preferences and the game may rewrite them on exit.
- Be explicit that the APK is debug-signed and installed outside the Play Store.
- The project is built by a non-developer with an AI coding agent; that honesty is part of the story.
  Do not turn it into a fake company or a fake support team.

## 8. Assets in this repository

| Asset | Path | Notes |
|---|---|---|
| App mark (SVG) | `docs/website/config-tuner-mark.svg` | Lime “L + signal bars” on near-black; derived from the launcher icon |
| Design tokens | `docs/website/design-tokens.css` | Exact app palette, type stacks, spacing |
| Current picker | `docs/website/picker-1.2.7.png` | 1600×1000; 15 installed titles visible |
| Older screenshots | `docs/screenshots/*.png` | Real device captures; picker is from an older release |
| App icon vector | `app/src/main/res/drawable/ic_launcher_foreground.xml` | Source for the mark |
| Title list | `README.md` | Authoritative 16-title table |

If you need a specific screen, ask; I can capture it from the tablet or Waydroid.

## 9. Collaboration protocol

The website agent can:

- read this brief, `design-tokens.css` and the repository README;
- ask questions through the coordinator (the user) or leave questions in
  `docs/website/QUESTIONS.md`;
- propose a stack, URL structure, wireframe or copy deck;
- request screenshots, missing facts, or a review of colour/accessibility choices.

I can review:

- factual claims against the app and release notes;
- copy for accuracy and tone;
- colour contrast, type scale, responsive behaviour and accessibility;
- screenshots and visual consistency with the app.

Please do not modify app source, release artifacts or `testdata/` as part of website work. Website
files should live under `docs/website/` or a separate web project directory.
