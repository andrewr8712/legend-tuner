# Legend Tuner — Token, Cost & Performance Report

> **Snapshot:** 2026-10-30 12:35 AWST (2026-10-30 04:35 UTC)  
> **Model:** `deepseek-flash` — DeepSeek-V4.1-Flash-1017  
> **Project:** [Legend Tuner](https://github.com/andrewr8712/legend-tuner) · [v1.1.0 release](https://github.com/andrewr8712/legend-tuner/releases/tag/v1.1.0)  
> **Prepared for:** GitHub upload · **Currency:** USD

---

## Executive summary

| Metric | Result |
|---|---:|
| Total processed tokens | **117,654,795** |
| Uncached input | **283,007** |
| Cached input read | **117,035,648** |
| Output tokens | **336,140** |
| Cache-hit rate | **99.76%** |
| Estimated API cost | **$0.60** |
| Wall-clock since first request | **8.06 h** |
| Active completed-turn time | **86.4 min** |

The large token total is dominated by **cache reads of the growing conversation context**, not unique new text. Only **283,007 tokens** were uncached input. Caching reduced the estimated bill from about **$17.96** to about **$0.60**.

---

## 1. Current DeepSeek pricing applied

Rates are per **1 million tokens**, from the official [DeepSeek Models & Pricing](https://api-docs.deepseek.com/quick_start/pricing/) page, retrieved **2026-10-30**.

### `deepseek-flash` — DeepSeek-V4.1-Flash-1017

| Component | Off-peak | Peak |
|---|---:|---:|
| Input — cache hit | $0.003 | $0.006 |
| Input — cache miss | $0.15 | $0.30 |
| Output | $0.60 | $1.20 |

**Peak hours:** 01:00–04:00 and 06:00–10:00 UTC, Monday–Friday, excluding Chinese public holidays. All other hours are off-peak.

### Search & Fetch

Search and Fetch are listed at **$0.75 per 1,000 successful requests**. Server-side tool calls through DeepSeek Harness are free until **00:00 Beijing time, 1 November 2026**; the 76 searches in this project were therefore not separately charged.

---

## 2. Token statistics

| Token category | Tokens | Share of total |
|---|---:|---:|
| Input — cache miss | 283,007 | 0.24% |
| Input — cache hit | 117,035,648 | 99.47% |
| Output | 336,140 | 0.29% |
| Cache write | 0 | 0.00% |
| **Total** | **117,654,795** | **100.00%** |

### Context and step profile

| Metric | Value |
|---|---:|
| Model responses | 401 |
| Steps | 402 |
| Tool calls | 410 |
| Web searches | 76 |
| Turns started / completed | 11 / 10 (current turn in progress) |
| Average tokens per response | 293,403 |
| Largest single response context | 580,847 |
| Largest cache read | 576,512 |
| Peak-window responses | 2 of 401 |
| Peak-window token share | 0.90% |

The maximum context of **580,847 tokens** used about **58%** of the model's 1M-token window.

> **Important:** cached tokens are repeated context. They are not 117 million unique words, nor 117 million tokens of new source material.

---

## 3. Cost breakdown

Costs are time-weighted: each model response was priced according to whether its timestamp fell inside a peak window.

| Component | Tokens | Cost | Share of bill |
|---|---:|---:|---:|
| Cache-hit input | 117,035,648 | $0.3543 | 58.6% |
| Cache-miss input | 283,007 | $0.0426 | 7.0% |
| Output | 336,140 | $0.2079 | 34.4% |
| **Total** | **117,654,795** | **$0.6047** | **100.0%** |

### Cost scenarios

| Scenario | Estimated cost |
|---|---:|
| **Actual time-weighted** | **$0.605** |
| If all usage were off-peak | $0.595 |
| If all usage were peak | $1.190 |
| If cache reads were billed as cache misses | $17.96 |

**Estimated cache savings:** about **$17.36** (roughly 96.6% of the no-cache equivalent).

---

## 4. Performance summary

### Agent throughput

| Metric | Value |
|---|---:|
| Wall-clock from first request | 8.06 h |
| Active completed-turn time | 86.4 min (1.44 h) |
| Processed tokens per active minute | ≈ 1,362,180 |
| Output tokens per active minute | ≈ 3,892 |
| Cache-hit rate | 99.76% |
| Peak context | 580,847 tokens |

Most of the wall-clock time was idle time between turns; completed-turn work accounted for about **1.4 hours**. Throughput is dominated by re-reading the long cached context each step.

### Delivery

| Item | Result |
|---|---|
| Project | Legend Tuner — tablet-first GRID Legends config editor |
| Repository | [andrewr8712/legend-tuner](https://github.com/andrewr8712/legend-tuner) |
| Release | [v1.1.0](https://github.com/andrewr8712/legend-tuner/releases/tag/v1.1.0) |
| Source | 80 tracked files · 4,829 Kotlin lines · 4 commits |
| Tests | 10/10 unit tests passing |
| CI | Green — [Android CI](https://github.com/andrewr8712/legend-tuner/actions) |
| APK | 9.94 MB · version 1.1.0 (code 2) · APK Signature Scheme v2 |
| Build | Final full build 6m 52s · CI 2m 38s |
| Runtime | Installed and working on Lenovo Yoga Tab Plus TB520FU, Android 17, Shizuku |
| Privacy | No `INTERNET` permission, no telemetry |

### What was delivered

- Adaptive tablet UI with navigation rail, two-column dashboard, preset cards, and docked inspector.
- Root, Shizuku, and manual SAF access.
- Battery Saver / Graphics / Performance preset editing.
- Fixed render height dropdown (480p–2160p, including 838 game default and 1840 TB520FU native).
- 30–144 FPS cap, resolution scaling, dynamic resolution, visuals, effects, and display settings.
- Automatic backups, read-back verification, restore-on-failure, raw XML editor, and unknown-key preservation.
- Public repository with MIT license, CI, issue template, security policy, and GitHub release.

---

## 5. Method and caveats

- Token figures come from the session ledger; `inputTokens` is treated as cache-miss input, `cacheReadTokens` as cache-hit input, and `cacheWriteTokens` was zero.
- Cost is **API-equivalent**, not an invoice. Taxes, plan discounts, rounding, credits, or a different billing arrangement may change the actual charge.
- Peak/off-peak cost uses each response timestamp and the published peak windows. Only **2** responses fell in a peak window; **0.90%** of tokens were billed at peak rates.
- The 76 search-side requests are not separately token-metered in the session record; Search/Fetch is free until 1 November 2026.
- Snapshot taken at **2026-10-30 12:35 AWST**. Generating and reading this report adds a small amount of usage beyond the snapshot.
- Model rates and peak windows are subject to change. Re-check the [official pricing page](https://api-docs.deepseek.com/quick_start/pricing/) before relying on the estimate for accounting.

---

## Links

- [Repository](https://github.com/andrewr8712/legend-tuner)
- [Latest release](https://github.com/andrewr8712/legend-tuner/releases/latest)
- [GitHub Actions](https://github.com/andrewr8712/legend-tuner/actions)
- [DeepSeek Models & Pricing](https://api-docs.deepseek.com/quick_start/pricing/)
