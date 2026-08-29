# SpotBop

[![Portfolio sample CI](https://github.com/abiemann/SpotBop/actions/workflows/verify.yml/badge.svg)](https://github.com/abiemann/SpotBop/actions/workflows/verify.yml)

**An engine-free Kotlin Android arcade game built around deterministic animation, custom Canvas rendering, and testable gameplay systems.**

![SpotBop beach gameplay](docs/media/spotbop-beach-gameplay.png)

[Watch the gameplay trailer](https://youtu.be/S3gWAaQVtYw) · [Read the architecture case study](docs/ARCHITECTURE.md) · [Review the testing strategy](docs/TESTING.md) · [View the privacy policy](https://abiemann.github.io/spotbop-privacy/)

> **Release status:** SpotBop 1.0 is available through Google Play closed testing. The [Play listing](https://play.google.com/store/apps/details?id=biemann.android.spotbop) may only be visible or installable to approved testers until production access is granted.

## What this repository is

This is the public, buildable **portfolio edition** of SpotBop. It contains a representative set of production-derived Kotlin systems and tests, along with the architecture, delivery, and verification decisions behind the Android game.

The complete Android project remains private because its raw artwork and audio catalog was licensed or created for the packaged game, not cleared for redistribution as downloadable source assets. This repository deliberately excludes the installable application, signing material, raw artwork, raw audio, tester information, and private development history.

That boundary keeps the portfolio useful to evaluators without pretending third-party or generated media is open source.

## At a glance

| | |
|---|---|
| Platform | Native Android, Kotlin, minimum API 26 |
| Rendering | `SurfaceView`, dedicated render thread, hardware `Canvas` |
| Game structure | 15 campaign levels across five scene families, plus a bonus scene |
| Simulation | Time-derived motion designed to be deterministic and frame-rate independent |
| Persistence | Local settings, first-launch state, and high scores |
| Privacy | Offline; no Internet permission, advertising, analytics, or tracking SDK |
| Delivery | Signed Android App Bundle through Google Play closed testing |
| Developer | Alexander Biemann, with disclosed AI-assisted development |

## Public code samples

The files under [`src/`](src/) are intentionally small enough to review in one sitting while preserving the behavior and test style used by the Android application:

- [`GameClock.kt`](src/main/kotlin/io/github/abiemann/spotbop/portfolio/GameClock.kt) — a lifecycle-aware monotonic clock that removes paused intervals from logical game time.
- [`Motion.kt`](src/main/kotlin/io/github/abiemann/spotbop/portfolio/Motion.kt) — deterministic drift, path, and reflected-bounce motion expressed as pure functions of time.
- [`CampaignProgression.kt`](src/main/kotlin/io/github/abiemann/spotbop/portfolio/CampaignProgression.kt) — the 15-level campaign state machine, score accumulation, failure handling, and bonus-route transition.
- [`Geometry.kt`](src/main/kotlin/io/github/abiemann/spotbop/portfolio/Geometry.kt) — reusable hit-testing geometry for circular, elliptical, polygonal, and segment-based interactions.
- [`src/test`](src/test) — focused tests for timing boundaries, long-running determinism, campaign transitions, and visible-shape hit geometry.

These samples were copied from and lightly namespaced for this public repository. The algorithmic behavior is the same as the corresponding production systems; Android UI, resource, audio, and artwork dependencies are intentionally absent.

## Engineering decisions

### Logical time instead of frame accumulation

Actors are sampled from immutable configuration and elapsed logical time. A dropped frame changes what was drawn during that frame, but not where an actor eventually appears. The clock removes lifecycle and surface pauses from the timeline, preventing jumps when the app resumes.

### Interaction follows visible geometry

Tap handling is not reduced to oversized rectangular bounds. Procedural actors use analytic shapes, and bitmap actors in the full application can use visible-alpha hit regions so transparent pixels do not become invisible hazards.

### Screens and scenes have separate responsibilities

The application host installs small screen implementations for title, stage intro, gameplay, completion, settings, scores, credits, and transitions. Gameplay screens coordinate rules and timing; scene implementations own environment behavior and rendering.

### Verification targets boundary behavior

The test strategy emphasizes exact pause/resume boundaries, direct timestamp sampling, long-running bounds, screen handoff guards, scoring thresholds, hit geometry, and progression outcomes. At the portfolio snapshot, the private Android project contained **570 JVM test methods** and **147 Android instrumented test methods**. The distinction between declared device tests and observed passing JVM tests is documented in [docs/TESTING.md](docs/TESTING.md).

## Build and run the public sample

The public sample requires JDK 17 or newer. It has no Android SDK dependency.

Windows:

```powershell
.\gradlew.bat test
```

macOS or Linux:

```sh
./gradlew test
```

The workflow in [`.github/workflows/verify.yml`](.github/workflows/verify.yml) runs the same tests from a clean Ubuntu checkout.

## Development process and AI disclosure

SpotBop was developed by **Alexander Biemann** with OpenAI Codex and Anthropic Claude used as AI-assisted implementation tools. Alexander owned product direction, specifications, acceptance criteria, integration, review, debugging, test decisions, licensing work, and Google Play delivery. AI assistance is disclosed in the game and here so evaluators can assess both the implementation and the development process directly.

The project is strongest evidence of graphics, simulation, testing, and release engineering. It intentionally does not claim to demonstrate a conventional Compose/MVVM/coroutines application stack.

## Repository terms

This repository is source-visible for personal, non-commercial portfolio evaluation; it is **not open source**. Viewing, cloning one evaluation copy, and running the included sample tests are permitted. Reuse, redistribution, derivative products, model training, and commercial use are not granted. See [LICENSE](LICENSE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
