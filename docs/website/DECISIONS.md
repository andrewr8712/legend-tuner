# Website decisions — app side

**Approved:** Approach **A — field instrument, one page**. Proof and caveats stay inline. Do not build
the long-form field manual (B) or the download-only one-screen variant (C) as the primary site.

These answers correspond to the seven questions in [QUESTIONS.md](QUESTIONS.md).

## 1. Branding depth

Full kit. Keep the existing mark; add:

- horizontal wordmark (mark + “CONFIG TUNER”)
- favicon set: SVG, 32, 180, 192, 512 and maskable
- 1200×630 social card
- short `/brand` guide

Self-host webfonts (Barlow Condensed is OFL) so the “zero third-party requests” claim remains true.

## 2. Screenshots

Start with the current picker and a simple save-pipeline diagram. The app side will supply:

- per-title editor with preset tabs visible
- raw XML editor
- backup/restore screen

Visible game titles are acceptable. Do not show config values, user-specific paths, analytics IDs or
anything from `testdata/`.

## 3. Disclaimer and trademark wording

Reuse the README disclaimer, shortened in the footer and expanded in the FAQ. Include the
“not affiliated with Feral Interactive” line and the full list of rights holders from the README.

## 4. Former name

One line in the FAQ and the meta description. Not in the hero. Suggested: “Formerly Legend Tuner.”

## 5. Hosting

GitHub Pages project site at `https://andrewr8712.github.io/legend-tuner/`. No custom domain for now.

Use a small GitHub Actions Pages workflow that uploads `docs/website/` and calls `deploy-pages`.
Do **not** use a generated `gh-pages` branch. Keep links relative so the same folder works on a custom
domain or another static host.

## 6. Numbers

Stamp once in the proof-strip footnote:

> 19 titles · 51 unit tests · as of 1.4.0

Do not imply the test count is permanent.

## 7. Handover

Commit the built site under `docs/website/` with the Pages workflow, in a separate PR from app
changes. The site and release notes should version together.

## Implementation guardrails

- One page; sections can become routes later.
- Download APK is the primary CTA; GitHub is secondary.
- Client-side title filter only; no network calls.
- Lime is a signal colour, not body text. Check WCAG AA contrast.
- Respect `prefers-reduced-motion`; no autoplay video.
- No third-party requests, copied game art, trackers or analytics.
- Keep the page legible before it is atmospheric.
- `docs/website/DESIGN.md` was not present in PR #2; include the design document in the build PR.
