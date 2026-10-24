# Stats Crash Fix, Real Local Analytics & Compact Focus Layout

This update resolves the duplicate key crash when syncing or rendering app usage in the Stats tab, replaces all hardcoded demo metrics with 100% authentic local analytics derived from completed focus sessions and `UsageStatsManager`, and compacts the Focus tab layout so the entire setup workflow fits cleanly on a single screen without scrolling.

### User Review & Critical Decisions

> [!IMPORTANT]
> All requirements from your specification are clear and ready for implementation. Review the summary below and click **Proceed** to apply the changes.

- **Duplicate Key Crash Prevention**: Aggregate foreground usage intervals per package via `.groupBy { it.packageName }`, filter out the Focus app's own package (`com.personal.focus`) and zero-duration entries, and enforce `.distinctBy { it.packageName }` before rendering the breakdown list.
- **Zero-State Real Analytics**: Remove all hardcoded weekly screentime numbers, the 4-hour baseline subtraction formula, and the simulated streak dots. Calculate **Time Saved** and **7-Day Focus Streak** strictly from completed focus sessions in the local database, and populate the **Weekly Screentime Activity** chart from real 7-day `UsageStatsManager` and local usage records (starting at `0` on a fresh install).
- **Single-Screen Compact Focus Layout**: Move the Ultra Focus trigger into the top header row alongside `INSTANT FOCUS` as a compact `#83D65C` pill button (`Ultra Focus ⚙`), remove the standalone `ULTRA FOCUS SETTINGS` card, and tighten vertical paddings and card spacing so `START FOCUS SESSION` is immediately visible without scrolling.

---

### 1. Overview & Core Concept

- **What It Does**:
  1. Eliminates the `IllegalArgumentException: Key "com.personal.focus" was already used` crash when syncing or displaying daily app usage.
  2. Transforms the Stats tab into a genuine local analytics dashboard where every metric (Streak, Time Saved, Daily/Weekly Screentime, and App Breakdown) starts at `0` and updates strictly from real user sessions and device usage data.
  3. Streamlines the Focus tab's vertical footprint so users can configure mode, duration, breaks, and Ultra Focus settings and tap `START FOCUS SESSION` on one screen.
- **Target Audience / Persona**: Users tracking real digital wellbeing progress and launching focus sessions quickly without unnecessary scrolling.
- **Key Value**: Crash-free reliability on sync, trustworthy zero-mock metrics, and faster one-screen session launch ergonomics.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  - **Stats Sync & Real Analytics Flow**: Opening the Stats tab or tapping **Sync Usage Stats** queries Android's `UsageStatsManager` across the last 7 days, aggregates duplicate package intervals, excludes Focus itself, and updates the 7-Day Streak, Time Saved hero card, Weekly Screentime bar chart, and Today's App Breakdown in real time. On a fresh install with no completed focus sessions, Streak is `0 Days` and Time Saved is `0h 0m Saved`.
  - **Compact Focus Setup Flow**: Opening the Focus tab displays the Permission Checklist (when permissions are missing), the `INSTANT FOCUS` header row with the bright green `Ultra Focus ⚙` action button on the right, the compact giant-digit timer preview card, the mode selector (`COUNTDOWN` | `STOPWATCH` | `POMODORO`), the `FOCUS DURATION` selector (`15m`–`120m`), the `BREAKS ALLOWED` selector (`0`–`4`), and the `START FOCUS SESSION` primary CTA—all visible at once.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: High-contrast OLED dark minimalism (`#000000` background, `#111412` elevated cards) paired with `#83D65C` neon green primary accents and `#FFD642` achievement gold.
  - *Typography & Hierarchy*: `Anton` giant timer digits (scaled slightly more compactly to reduce vertical height), `Clash Display` hero statistics, `Cabinet Grotesk` headers, and `Roboto Numbers` for usage metrics.
  - *Component Styling & Layout*: Compact vertical rhythm (`10dp`–`12dp` inter-card spacing and reduced internal card padding) on the Focus screen while preserving 48dp+ accessible touch targets.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Aggregating Multi-Bucket `UsageStatsManager` Intervals**
  - *Chosen Approach*: Group all returned `UsageStats` entries by `packageName`, sum their `totalTimeInForeground`, keep the latest `lastTimeUsed`, exclude `context.packageName` and system UI/launcher packages, and apply `.distinctBy { it.packageName }` in both repository state and the `LazyColumn`.
  - *Why*: `UsageStatsManager.queryUsageStats(INTERVAL_DAILY, ...)` frequently returns multiple buckets for the same package when day boundaries or system intervals overlap, which caused Compose's `LazyColumn(key = { it.packageName })` to crash with a duplicate key exception.
- **Decision 2: Authentic Time Saved & Streak Calculation**
  - *Chosen Approach*: Compute **Time Saved** directly from the sum of `totalFocusTimeSpentMillis` of completed/active focus sessions recorded in Room DB, and compute the **7-Day Focus Streak** by checking which of the last 7 calendar days have at least one completed focus session.
  - *Why*: Replaces the artificial `4h - screentime` estimate and hardcoded `index <= todayIndex` check marks with真实 user effort that starts at `0` on a fresh install.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        Android System & Local DB                         │
│  ┌────────────────────────────┐        ┌──────────────────────────────┐  │
│  │     UsageStatsManager      │        │        FocusDatabase         │  │
│  │ (7-Day Foreground Buckets) │        │ (Sessions & DailyAppUsage)   │  │
│  └─────────────┬──────────────┘        └──────────────┬───────────────┘  │
└────────────────┼──────────────────────────────────────┼──────────────────┘
                 │ Group by packageName                 │ Flow<Sessions> &
                 │ Exclude self & sum durations         │ Flow<DailyUsage>
                 ▼                                      ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       Reactive Repository Layer                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │                       UsageStatsRepository                         │  │
│  │ • todayUsage (distinct by packageName, sorted desc, self excluded) │  │
│  │ • weeklyUsageByDate (7-day real minutes from UsageStats + Room)    │  │
│  │ • totalTimeSavedMillis (real completed focus duration from Room)   │  │
│  │ • completedStreakDays (set of dateKeys with completed sessions)    │  │
│  └───────────────────────────────┬────────────────────────────────────┘  │
└──────────────────────────────────┼───────────────────────────────────────┘
                                   │ StateFlow
                 ┌─────────────────┴─────────────────┐
                 ▼                                   ▼
┌─────────────────────────────────┐ ┌──────────────────────────────────────┐
│           StatsScreen           │ │        FocusScreen (Compact)         │
│ • 7-Day Streak (0 on fresh app) │ │ • Header: INSTANT FOCUS +            │
│ • Real Time Saved Hero Card     │ │   [Ultra Focus ⚙] (#83D65C button)   │
│ • Real 7-Day Weekly Bar Chart   │ │ • Compact Timer Preview Card         │
│ • Deduplicated App Breakdown    │ │ • Mode / Duration / Break Selectors  │
│ • Instant Sync Button           │ │ • START FOCUS SESSION (No-scroll)    │
└─────────────────────────────────┘ └──────────────────────────────────────┘
```

- **Data Model & State**:
  - `DailyAppUsageRecord`: Keyed by `(dateKey, packageName)` in Room so each day stores a single aggregated record per package.
  - `UsageStatsRepository`: Exposes `todayUsage` (deduplicated and sorted), `weeklyDayMinutes: StateFlow<Map<String, Int>>` (real minutes per `yyyy-MM-dd` over the current week), `completedFocusDates: StateFlow<Set<String>>`, `currentStreakCount: StateFlow<Int>`, and `totalTimeSavedMillis: StateFlow<Long>` (derived from real focus sessions).
- **Interactive Component & State Mapping**:
  - **Sync Usage Stats Button (`StatsScreen`)**: Invokes `usageRepo.syncUsageFromSystem(context)` on `Dispatchers.IO`, immediately refreshing both today's breakdown and the 7-day weekly bar chart without duplicate keys.
  - **Header `Ultra Focus ⚙` Button (`FocusScreen`)**: Styled with `#83D65C` background and bold black text in the `INSTANT FOCUS` header row; tapping opens the `ModalBottomSheet` containing Strict Mode, YouTube Study Mode, Uninstall Protection, Notification Silencing, and Home Screen Trap toggles.
