# Architecture Case Study

## Context

SpotBop is a fixed-landscape native Android arcade game. It deliberately avoids
a game engine so timing, lifecycle, state ownership, rendering, and interaction
remain visible in the Kotlin and Android SDK implementation.

The complete application targets Android API 26 and newer. A dedicated render
thread draws through `SurfaceView` and a hardware `Canvas`, while the Android UI
thread delivers lifecycle and touch events.

## System shape

```text
GameActivity
└── GameView (SurfaceView and screen coordinator)
    ├── RenderThread -> hardware Canvas at approximately 60 FPS
    ├── GameClock + manual gameplay pause clock
    ├── Screen
    │   ├── Title / Stage Intro / Gameplay
    │   ├── Level Complete / Game Over
    │   └── Settings / Scores / Credits / Transitions
    ├── CampaignProgression + local stores
    └── SceneFactory
        ├── Underwater / Beach / Land
        ├── Clouds / Space
        └── Atom Box bonus scene
```

## Deterministic simulation

Most movement is a pure function of immutable configuration and logical elapsed
time. Position is sampled rather than accumulated per frame. A skipped frame
therefore does not introduce permanent drift, and tests can jump directly to an
exact time boundary.

The production `GameClock` stores an immutable state snapshot behind a volatile
reference. Pause and resume transitions are synchronized; steady-state clock
reads allocate nothing. Paused system time is subtracted from logical time so
surface loss or an Android lifecycle pause cannot advance the simulation.

The public [`GameClock.kt`](../src/main/kotlin/io/github/abiemann/spotbop/portfolio/GameClock.kt)
and [`Motion.kt`](../src/main/kotlin/io/github/abiemann/spotbop/portfolio/Motion.kt)
preserve these mechanics without Android dependencies.

## Screen and scene boundaries

Screens own user-facing flow. Scenes own environment-specific behavior and
drawing. This avoids putting title navigation, progression, scoring, and every
environment into one renderer. Guarded handoffs and run tokens prevent callbacks
from an old screen from mutating a newly installed one.

Campaign progression is kept outside rendering. Five ordered scenes contain
three authored difficulty levels each. Failure accumulates the attempted score
but does not advance; the terminal successful level completes the campaign. A
Beach-to-Atom bonus route has an explicit transition so its combined score is
committed exactly once.

## Interaction geometry

Tap priority distinguishes absorbing foreground effects, hazards, valid game
objects, and misses. Procedural actors use analytic hit shapes such as circles,
rotated ellipses, triangles, polygons, and line-segment distances. In the full
application, selected bitmap actors use visible-alpha hit data so transparent
pixels are not clickable.

## State and privacy

Settings, first-launch state, and local high scores use Android private storage.
The game declares no Internet permission and integrates no advertising,
analytics, tracking, or crash-reporting SDK. Optional Do Not Disturb access is
used only to control interruptions during play.

## Deliberate limitations

- The custom renderer is appropriate evidence for graphics and simulation, but
  it is not an example of a Compose/MVVM application.
- The full Android repository remains private because raw media redistribution
  rights differ from the right to package that media in the game.
- The public code is a representative, buildable selection rather than a claim
  that the complete Play-delivered application is open source.
