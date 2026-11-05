# Questions for Config Tuner

The website agent can append questions here. Keep them specific.

## Open — for the website agent

- What stack and hosting target is the site using?
- Is this a single landing page or a multi-page site with docs/blog?
- Is the primary CTA “Download APK” or “View on GitHub”?
- Does the site need a live title filter, or is a static table enough?
- Are there brand assets beyond the app mark and screenshots?

## Answered — by the website agent

The five questions above, decided. Change any of them and I'll follow the change.

- **Stack and hosting:** static HTML, CSS and vanilla JavaScript — no build step, relative links. It
  deploys to GitHub Pages (project site or custom domain) or any static host by copying the folder.
- **Single page or multi-page:** one landing page for launch, with the titles, install and FAQ sections
  written so they can move to their own routes later without a rewrite.
- **Primary CTA:** Download APK (latest release). GitHub is the secondary action, in the hero and the
  footer.
- **Live title filter:** yes — client-side search over a static title list. No network calls, so it
  works offline and matches the app's no-telemetry story.
- **Brand assets:** the mark and design tokens are the whole identity today. Plan: derive a wordmark
  (mark + “CONFIG TUNER”), a favicon set (SVG, 32, 180, 192, 512 and a maskable variant), a 1200×630
  social card and a one-page brand guide, all shipped under `docs/website/brand/`. Say so if you would
  rather keep branding lean and reuse the mark as-is.

## Open — for the app side

Each has a default in italics; “default” is a valid answer for any of them.

1. **Branding depth** — full kit as described above, or lean (mark reused as-is)? *Default: full kit.*
2. **Screenshots** — can you capture 1.2.8 screens for the per-title editor with preset tabs open, the
   raw XML editor and the backup/restore dialog? Are visible game titles acceptable? *Default: build
   with the 1.2.8 picker plus a diagram of the save pipeline; add the others when supplied.*
3. **Disclaimer and trademark wording** — is there an existing string in the app's About screen or on
   the release page that you want reused verbatim? *Default: the brief's wording plus a “not affiliated
   with Feral Interactive; game titles are trademarks of their respective owners” line in the footer
   and FAQ.*
4. **Former name** — should the site say “formerly Legend Tuner”, and where? *Default: one line in the
   FAQ and the meta description, not in the hero.*
5. **Hosting target** — GitHub Pages under `andrewr8712.github.io/legend-tuner`, a custom domain, or
   files you will place yourself? *Default: relative-link static build that works in all three cases.*
6. **Numbers on the page** — should “17 titles” and “48 unit tests” be stamped “as of 1.2.8”, or left
   unstamped? *Default: stamped once, in the proof strip footnote.*
7. **Handover** — do you want the built site committed under `docs/website/` in this repo, or delivered
   as a separate project folder for you to place? *Default: `docs/website/` in this repo, so releases and
   the site version together.*

## Answered — app facts

- **Current release:** 1.3.0, versionCode 13, debug-signed.
- **Supported titles:** 19; see README for packages and control counts.
- **Network/analytics:** none.
- **Android support:** 8.0+.
