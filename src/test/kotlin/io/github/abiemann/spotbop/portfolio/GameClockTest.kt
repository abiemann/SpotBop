package io.github.abiemann.spotbop.portfolio

import org.junit.Assert.assertEquals
import org.junit.Test

class GameClockTest {

    @Test
    fun advancesWithSystemTimeWhileRunning() {
        val clock = GameClock()

        assertEquals(250L, clock.toGameTime(250L))
    }

    @Test
    fun freezesWhilePausedAndExcludesPausedTimeAfterResume() {
        val clock = GameClock()

        clock.pause(100L)
        assertEquals(100L, clock.toGameTime(175L))

        clock.resume(225L)
        assertEquals(150L, clock.toGameTime(275L))
    }

    @Test
    fun repeatedLifecycleCallbacksDoNotDoubleCountPauseTime() {
        val clock = GameClock()

        clock.pause(100L)
        clock.pause(150L)
        clock.resume(200L)
        clock.resume(250L)

        assertEquals(200L, clock.toGameTime(300L))
    }
}
