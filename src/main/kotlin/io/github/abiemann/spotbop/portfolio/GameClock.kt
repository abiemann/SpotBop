package io.github.abiemann.spotbop.portfolio

/**
 * Monotonic game time that excludes intervals when rendering is inactive.
 *
 * State changes happen only on lifecycle or surface transitions. Steady-state
 * reads take one immutable snapshot and do not allocate.
 */
internal class GameClock {

    private class State(
        val paused: Boolean,
        val pausedAtSystemNanos: Long,
        val totalPausedNanos: Long,
    )

    @Volatile
    private var state = State(
        paused = false,
        pausedAtSystemNanos = 0L,
        totalPausedNanos = 0L,
    )

    fun nowNanos(): Long = toGameTime(System.nanoTime())

    fun toGameTime(systemTimeNanos: Long): Long {
        val snapshot = state
        val activeSystemTime = if (snapshot.paused) {
            snapshot.pausedAtSystemNanos
        } else {
            systemTimeNanos
        }
        return activeSystemTime - snapshot.totalPausedNanos
    }

    fun pause() = pause(System.nanoTime())

    @Synchronized
    internal fun pause(systemTimeNanos: Long) {
        val snapshot = state
        if (snapshot.paused) return

        state = State(
            paused = true,
            pausedAtSystemNanos = systemTimeNanos,
            totalPausedNanos = snapshot.totalPausedNanos,
        )
    }

    fun resume() = resume(System.nanoTime())

    @Synchronized
    internal fun resume(systemTimeNanos: Long) {
        val snapshot = state
        if (!snapshot.paused) return

        state = State(
            paused = false,
            pausedAtSystemNanos = 0L,
            totalPausedNanos = snapshot.totalPausedNanos +
                systemTimeNanos - snapshot.pausedAtSystemNanos,
        )
    }
}
