# Config Tuner website

Static marketing site for Config Tuner, published at
<https://andrewr8712.github.io/legend-tuner/>.

No build step, no framework, no dependencies, no third-party requests. The files in this
folder are the deployed site.

## Layout

```
docs/website/
  index.html            one landing page: hero, proof, how it works, safety,
                        titles, screenshots, install, FAQ, footer
  brand/index.html      brand guide: logo rules, colour, type, asset list, voice
  site.webmanifest      PWA metadata (name, icons, colours)
  sitemap.xml           two URLs
  robots.txt            allows everything, points at the sitemap
  404.html              (optional) GitHub Pages fallback
  assets/css/site.css   design tokens + all page styles
  assets/css/brand.css  brand-guide-only styles
  assets/js/site.js     title filter, screenshot tabs, scroll reveal, nav state
  assets/fonts/         Barlow Condensed 600, Inter variable, JetBrains Mono variable
                        (latin subset, woff2, SIL OFL, self-hosted on purpose)
  assets/shots/         app screenshots (webp + png fallback) used on the page
  assets/brand/         mark, lockup, wordmark, favicons, maskable icon, social card
  DESIGN.md             approved design doc (kept for context, not published)
  DECISIONS.md          approved decisions from the app side (not published)
  README.md             this file (not published)
```

The deploy workflow deletes `*.md` from the uploaded artifact, so the docs stay in the repo
without being served.

## Local preview

```sh
python3 -m http.server 8000 --directory docs/website
# http://localhost:8000/
```

Use a server rather than `file://`: the woff2 files are loaded with CORS, which `file://` blocks.

## Editing rules

- **Facts come from the repo.** Title list, package ids, control counts and version numbers are
  copied from the app `README.md` and the current GitHub release. If a title is added or a count
  changes, update `#titles` (the 19 cards) and the proof strip together.
- **Numbers are stamped, not live.** The page says "19 titles · 51 unit tests · as of 1.4.0".
  Bump the stamp when the facts change; never claim a number that is not in the release notes.
- **No forbidden claims.** No "all Feral games", "every setting", "no root ever needed",
  "Play Store", "official", "guaranteed safe on every device", or support for untested versions.
- **Screenshots** must come from the shipping app. No game artwork, no promotional renders.
  Keep the alt text describing what the screen shows, not marketing copy.
- **Colour and type** are defined once as custom properties at the top of `assets/css/site.css`.
  Do not hard-code hex values in new markup; use the tokens.
- **Brand assets** are generated, not hand-drawn: `mark.svg` is the master, and the lockup and
  wordmark are outlined paths derived from it. If the mark changes, regenerate the rest.

## Accessibility and performance

Targets, verified before each merge:

- WCAG 2.2 AA: axe-core reports zero violations on `/` and `/brand/`, at 1440 px and 390 px.
- Every text colour is at least 4.5:1 against its surface.
- Keyboard: skip link, focus-visible rings, tabs support arrow keys, FAQ uses native `<details>`.
- `prefers-reduced-motion: reduce` disables the reveal and the caret animation.
- JavaScript off: all four screenshot panels show in sequence, the title filter is simply absent.
- No layout shift from images or fonts (all images have width/height, fonts are preloaded and
  `font-display: swap`).
- Page weight: ~330 KB for the HTML, CSS, JS, fonts and the hero screenshot; ~600 KB if every
  screenshot tab is opened. Screenshots ship as WebP with a PNG fallback via `<picture>`.

## Deploying

`.github/workflows/pages.yml` publishes this folder on every push to `main` that touches it.

One-time setup, if the workflow has never run:

1. Repository → **Settings → Pages → Source: GitHub Actions**.
2. Push to `main`, or run the “Deploy website” workflow manually.
3. The site appears at `https://<owner>.github.io/<repo>/`.

If the repo is ever renamed or moved to another host, update the absolute URLs in `index.html`
(`canonical`, `og:url`, `og:image`), in `brand/index.html` (`canonical`) and in `sitemap.xml`.

## Verification commands

```sh
npx html-validate docs/website/index.html docs/website/brand/index.html docs/website/404.html
```

For the browser checks (axe, screenshots, viewport widths), see the notes in the PR that added
this folder; they were run with Playwright against a local HTTP server at 320/390/768/1024/1440 px.

## Licence

Site code and brand assets: MIT, same as the app. Typefaces: Barlow Condensed, Inter and
JetBrains Mono, each under the SIL Open Font License (self-hosted, latin subset only).
