# Testing and Verification

## Portfolio sample

The public sample is deliberately platform-independent. Its tests run on the
JVM and cover:

- clock pause/resume boundaries and repeated lifecycle callbacks;
- deterministic, bounded motion at long elapsed times;
- wall reflection, hop apexes, and path wrapping;
- progression across all 15 campaign levels;
- failure, reset, alternate starting-stage, and bonus-route behavior; and
- circular, elliptical, polygonal, triangular, rectangular, and segment-based
  interaction geometry.

Run them with:

```text
./gradlew test
```

GitHub Actions runs the same command on every push and pull request.

## Complete Android project snapshot

The private Android repository was verified on August 28, 2026 before this
portfolio edition was created.

| Suite | Methods | What the number means |
|---|---:|---|
| JVM unit tests | 570 | Observed passing: 570; failing: 0; skipped: 0 |
| Android instrumented tests | 147 | Test methods declared for device/emulator execution |
| Total test methods | 717 | JVM and Android methods combined |

The 147 instrumented methods are not presented as a claim that every test passes
on every Android device. They require an emulator or physical device and cover
Android resources, bitmap registration and alpha behavior, renderer integration,
navigation, settings, notification-policy access, and complete UI flows.

The private project's clean Ubuntu CI additionally completed JVM tests, Android
lint, and debug APK assembly. The full local verification completed the same
quality gates and produced a debug APK.

## Test design

Tests emphasize invariants and boundary behavior rather than screenshot-only
checks:

1. **Direct sampling:** a pose sampled at time `t` must not depend on how many
   frames were rendered before it.
2. **Pause invariance:** repeated pause/resume callbacks must not double-count
   paused time.
3. **Long-duration bounds:** reflected motion remains within authored limits far
   beyond a normal play session.
4. **Exact thresholds:** scoring and progression define inclusive and exclusive
   boundaries explicitly.
5. **Visible interaction:** hit geometry matches the shape a player sees.
6. **Stale-event safety:** old callbacks and transitions cannot mutate a new
   screen flow.
