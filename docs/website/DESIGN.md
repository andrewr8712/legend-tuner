# Config Tuner site — design document

**Status:** draft for approval · 2026-11-06
**Source of truth for facts:** `docs/website/AGENT-BRIEF.md` (1.2.8) and the repository README.
**Nothing is built until this is approved.**

---

## 1. The problem worth solving

Someone hears about Config Tuner, or opens the GitHub release page, and has to decide in about
thirty seconds whether to install a debug-signed APK that asks for root or Shizuku access to their
game's files. The current README answers that, but it is a wall of Markdown written for people who
already care.

The site has one job: make that decision possible without hype. It should look like the app — a tool
for reading and writing a file that the game does not want you to touch — and it should state the
uncomfortable parts (root, debug signature, the game rewrites settings on exit) before the download
button, not after it.

Secondary job: give the project a home that outlives a README — a place for release news, the
17-title catalogue, and the safety model.

## 2. Goals and non-goals

**Goals**

- Explain the product in one screen: what it edits, why Android makes that hard, what it will not do.
- Get a prepared visitor to the correct APK in two clicks, with the version and build type visible.
- Make the safety model legible: backup before write, read-back verification, rollback on failure,
  no network permission at all.
- Be usable on the tablet that will install the app, and on a phone in portrait.
- Ship a small brand kit so the project looks like one thing across GitHub, the APK and the site.

**Non-goals**

- No account system, no comments, no newsletter, no analytics, no cookies, no third-party requests.
- No fake company, no fake support team, no invented testimonials.
- No Feral artwork, logos or box art; the screenshots are UI only.
- Not a docs site: installation and troubleshooting live in the README, and the site links to it.
- No claim of user numbers, ratings or "thousands of players".

## 3. Three approaches

**A — Field instrument, one page (recommended).**
One scrolling page that behaves like the app: near-black, one lime signal, condensed uppercase
headings, hairline rules, real UI captures. Proof and caveats sit inline, next to the claim they
qualify. Cheap to maintain, matches the app exactly, works as a GitHub Pages project site.

**B — Field manual, long-form.**
Same visual language but a document: a numbered manual with a technical appendix (the XML schema,
the access-route comparison, the catalogue). Better for the sceptical root user who wants to read
before installing; worse as a landing page, slower to keep current with releases, duplicates the
README.

**C — Download-first single screen.**
Hero, one paragraph, one button, footer. Lowest friction for someone who already knows the app;
useless to everyone else, and it throws away the 17-title catalogue and the safety story, which are
the actual reasons to trust it.

**Recommendation: A.** It keeps the brief's direction (the app's own instrument look), carries the
proof that B would bury and C would drop, and stays a single static page that a non-developer can
edit. B's best parts — the route comparison and the catalogue provenance — become two compact
sections inside A rather than a separate document.

## 4. Information architecture

Single page, `index.html`, plus a second page `brand/` for the kit. Section ids double as nav anchors.

| # | id | Purpose | Content |
|---|----|---------|---------|
| 0 | — | Header, sticky | mark + wordmark, nav (How it works · Safety · Titles · Install · FAQ), Download APK |
| 1 | `top` | Hero | H1, one-sentence explainer, Download + GitHub, three micro-facts, hero visual |
| 2 | `proof` | Proof strip | 17 titles · 48 unit tests · no network permission · MIT, stamped "as of 1.2.8" |
| 3 | `how` | How it works | 4 steps, line-drawn connectors, the file path in mono |
| 4 | `safety` | Safety | 3 guarantees + 3 honest caveats, side by side |
| 5 | `titles` | Supported titles | filter field, 17 cards with control counts, "not installed" behaviour |
| 6 | `shots` | Screenshots | 4-image tab set: picker, guided editor, preset tabs, resolution |
| 7 | `install` | Install | 4 steps, requirements, the four access routes compared, debug-build warning |
| 8 | `faq` | FAQ | 6 `<details>` items |
| 9 | `foot` | Footer | disclaimer, trademark line, licence, links, how it was built |

**Hero copy (draft).**

> Unofficial · Android 8.0+ · 17 titles
> # Feral settings without the XML.
> Every Feral Android port keeps its real graphics settings in a plaintext registry that Android
> will not let you open. Config Tuner gives you readable controls for that file — backed up,
> written back, verified.
> **[ Download APK ]** [ View on GitHub ]
> `Android/data/<package>/files/feral_app_support/preferences`

Hero visual: the path block above rendered as a terminal line with a lime caret, then the 1.2.8
picker capture in a simple tablet frame, bleeding off the right edge on wide screens.

**Proof strip:** `17` titles · `48` unit tests · `0` network permissions · MIT. Footnote:
"Counts as of 1.2.8".

**How it works (4 steps):**

1. **Pick the title.** Installed Feral games are detected by package; the rest can be opened from a
   saved file.
2. **Choose a route.** Root, Shizuku, manual file, or direct on older Android. Android 11+ blocks the
   folder for normal apps — this is the honest cost of editing another app's data.
3. **Edit guided controls.** Grouped, plain-language rows with the stored registry key shown for
   provenance; preset families stay tabs, not flattened keys.
4. **Save, verified.** Validate → back up → write → read back → parse. Any failure restores the
   previous file.

**Safety — guarantees:** backup before every save (12 per title); read-back and parse after every
write; automatic rollback on failure. **Caveats:** the game rewrites `preferences` on exit, so close
it before editing; the APK is debug-signed, so Play Protect may warn; the app never creates registry
values that are absent from the file.

**Titles:** client-side filter by name; each card shows title, guided-control count, and a "not
installed" state; one line: "Not listed? The raw XML editor still opens any Feral `preferences` file
you can supply."

**Install:** 1) download the APK on the tablet, 2) allow install from this source, 3) open and pick a
title, 4) choose a route if Android blocks the folder. Requirements list: Android 8.0+, 10 MB, no
Play Store, version 1.2.8 (versionCode 11), debug-signed.

**FAQ:** why root/Shizuku is needed; is it safe; what happens when the game updates; why debug-signed;
does it collect anything (no network permission at all); can it work with a title that is not listed;
what happened to Legend Tuner.

**Footer:** "Unofficial fan tool. Not affiliated with or endorsed by Feral Interactive. Game titles
and marks are trademarks of their respective owners. No game code, artwork or save data is included."
Plus MIT, GitHub, issues, and one honest line: "Built by a non-developer with an AI coding agent."

## 5. Visual system

Tokens come from `docs/website/design-tokens.css` unchanged — the site must not drift from the app.

- **Surfaces:** canvas `#070908`, panel `#0C0F0B`, card `#121512`, raised `#171B17`, inset `#1D221B`.
- **Lines:** `#272D24` (structure), `#20251F` (subtle).
- **Ink:** `#F3F5EF` body, `#98A092` secondary, `#5E675D` metadata only (never body copy).
- **Signal:** lime `#D7FF3F`; dim `#B9DD31` for hover; wash `rgba(215,255,63,.08)` for selection;
  text-on-lime `#11150B` at 12.6:1.
- **States:** amber `#FFB454` for caveats, red `#FF6259` for destructive/stop.
- **Type:** Barlow Condensed 600 uppercase for display and section labels (tracking `0.02em`);
  Inter 400/500 for body; JetBrains Mono 400/500 for paths, packages, keys, version strings.
  Scale: 12 / 14 / 16 / 18 / 24 / 34 / 56 / 88 px, fluid `clamp()` between 320 and 1440.
- **Grid:** 12 columns, max 1280 px, 24 px gutters, 64/96 px section rhythm, hairline rules instead
  of shadows. Radii 2–8 px; no pills except status chips (uppercase, 11 px, tracking 0.08em).
- **Buttons:** primary = lime fill, near-black label, 44 px min height, 1 px inner rule on hover;
  secondary = transparent with `#272D24` border, ink label, lime underline on hover; focus = 2 px
  lime outline at 2 px offset, always visible on keyboard.
- **Motion:** 120–200 ms ease-out; staggered card entrance ≤ 40 ms apart; a single 600 ms line draw
  under the hero headline; hover lift of 1 px only. All of it behind
  `prefers-reduced-motion: no-preference`.
- **No** glassmorphism, gradients as decoration, purple neon, huge rounded pills, fake game art.

Accessibility targets: WCAG 2.2 AA, body text ≥ 4.5:1 (ink on canvas is 16.9:1), no colour-only
signalling (amber/red always paired with a word), 44 px targets, visible focus, semantic landmarks,
`<details>` FAQ keyboard-native, filter results announced via `aria-live="polite"`, images with
descriptive alt, tab set as real `role="tablist"` with arrow-key support.

## 6. Brand kit

Shipped in `assets/brand/` and documented at `brand/index.html`.

- **Mark:** the app's existing SVG, unchanged — it is the launcher icon and must stay recognisable.
  Its "signal bars" are the brand's one memorable element.
- **Wordmark:** "CONFIG TUNER" set in Barlow Condensed 600, uppercase, tracking `0.02em`, baseline
  optically aligned to the mark's bars. Horizontal lockup (mark left, 12 px gap) and stacked lockup
  for square uses. Clear space = the mark's bar height on all sides.
- **Favicons:** `favicon.svg`, 32 px PNG, `apple-touch-icon` 180 px (mark on canvas, 20 % padding),
  192/512 PNG, 512 maskable with the mark inside the 80 % safe circle. `site.webmanifest` with
  canvas background and lime theme colour.
- **Social card:** 1200×630, canvas, hairline frame, wordmark top-left, the H1 in two lines, lime
  rule, "17 titles · no network permission · no telemetry" — final line is "Debug-signed APK for
  Android 8.0+".
- **Brand guide page:** tokens with swatches and contrast ratios, type specimens, lockup rules and
  five don'ts, download links for SVG/PNG assets. Linked from the footer, not the main nav.
- **Copy as brand:** the voice rules from the brief (short declaratives, no exclamation marks, caveats
  before claims) are part of the kit and get one short section on the guide page.

## 7. Technical approach

Static files, no build step, no dependencies at runtime:

```
docs/website/
  index.html
  brand/index.html
  assets/css/site.css
  assets/js/site.js
  assets/fonts/*.woff2            self-hosted, subset, OFL licences included
  assets/brand/*.svg|*.png
  assets/shots/*.png              copied from docs/screenshots + picker-1.2.8.png
  site.webmanifest
  README.md                       handover notes
```

- Semantic HTML5; CSS custom properties from the token file; one small JS file for the title filter,
  the screenshot tabs, the scroll-spy nav and reveal animations (all optional — the page works with
  JS disabled: all 17 titles are in the HTML, filtering is progressive enhancement).
- **Zero third-party requests:** fonts self-hosted, no CDN, no analytics, no embeds. This is a
  product claim the site itself should honour.
- **Deployment:** the source lives in `docs/website/`, which GitHub Pages cannot serve directly
  (Pages publishes a branch root or `/docs`). Two clean options: (a) a small `pages.yml` workflow
  that uploads `docs/website` as the Pages artifact — one new file under `.github/workflows/`;
  (b) a `gh-pages` branch carrying the built folder. Recommend (a); it keeps `main` as the only
  source of truth. Both need the maintainer's approval, and neither touches app source.
- **Image handling:** screenshots served at 2× and downscaled via `srcset`; no WebP conversion
  pipeline, so `docs/screenshots` stays the only copy. Each image gets explicit width/height to
  avoid layout shift.
- **Performance budget:** < 250 KB JS+CSS gzipped, < 900 KB images on first load (lazy below the
  fold), LCP < 1.5 s on a mid-range tablet over 4G.

## 8. Verification before handover

1. `html-validate` clean; no duplicate ids, no missing alt.
2. Link check: every internal anchor resolves, every external URL returns 200 (releases latest,
   GitHub, licence).
3. Contrast: computed ratios for every ink/surface pair ≥ 4.5:1 body, ≥ 3:1 large text and UI edges.
4. Headless Chromium (installed temporarily for verification only): screenshots at 320, 768, 1024,
   1440 px; axe-core scan with no serious violations; keyboard pass through nav, tabs, filter, FAQ.
5. JS-disabled pass: titles, FAQ and install steps all present and readable.
6. `prefers-reduced-motion` pass: no transforms, no line draw.
7. Fact check against the brief: 1.2.8, 17 titles with exact control counts, 48 tests, Android 8.0+,
   no network permission, debug-signed, no Play Store mention.

## 9. Open questions

Seven are filed in `docs/website/QUESTIONS.md` (PR #2), each with a default. Nothing in section 7
blocks on them; the two that could change output are **branding depth** (full kit vs lean) and
**screenshots** (which 1.2.8 captures exist).

## 10. What I will not do without a second approval

- Commit a `pages.yml` workflow or create a `gh-pages` branch.
- Modify app source, release artifacts, `testdata/`, `docs/screenshots/` or `README.md`.
- Publish anything to a domain, or open the site as "official".

**Approval needed:** approach A, the section order, the brand direction (mark unchanged + new
wordmark/favicons/social card), and the deployment option (a) above.
