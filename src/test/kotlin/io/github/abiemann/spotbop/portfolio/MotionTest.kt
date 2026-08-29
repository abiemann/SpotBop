package io.github.abiemann.spotbop.portfolio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MotionTest {

    @Test
    fun driftStartsAtCentreAndIsDeterministic() {
        val motion = DriftMotion(0.5, 0.5, 0.3, 0.2, 10.0, 7.0, 9.0)

        assertEquals(0.5, driftPosition(motion, 0.0).x, 1e-12)
        assertEquals(0.5, driftPosition(motion, 0.0).y, 1e-12)
        assertEquals(
            driftPosition(motion, 3.3).x,
            driftPosition(motion, 3.3).x,
            0.0,
        )
    }

    @Test
    fun loopingAndClampedPathsReachExpectedPositions() {
        val looping = PathMotion(0.0, 0.3, 1.0, 0.3, 4.0, 0.0, loop = true)
        val clamped = looping.copy(loop = false)

        assertEquals(0.5, pathPosition(looping, 2.0).x, 1e-12)
        assertEquals(pathPosition(looping, 0.0), pathPosition(looping, 4.0))
        assertEquals(1.0, pathPosition(clamped, 99.0).x, 1e-12)
    }

    @Test
    fun horizontalBounceReflectsWithoutLeavingBounds() {
        val motion = beachMotion(speed = 0.4)

        assertEquals(0.9, horizontalBouncePosition(motion, 1.0, 1, 0.1, 0.9).x, 1e-12)
        assertEquals(0.5, horizontalBouncePosition(motion, 2.0, 1, 0.1, 0.9).x, 1e-12)
        assertEquals(0.1, horizontalBouncePosition(motion, 3.0, 1, 0.1, 0.9).x, 1e-12)
        assertEquals(0.1, horizontalBouncePosition(motion, 1.0, -1, 0.1, 0.9).x, 1e-12)
    }

    @Test
    fun horizontalBounceHasExactApexAndLanding() {
        val motion = beachMotion(bounceHeight = 0.6, bouncePeriodSeconds = 2.0)

        assertEquals(0.3, horizontalBouncePosition(motion, 1.0, 1, 0.1, 0.9).y, 1e-12)
        assertEquals(0.9, horizontalBouncePosition(motion, 2.0, 1, 0.1, 0.9).y, 1e-12)
        assertEquals(0.3, horizontalBouncePosition(motion, 21.0, 1, 0.1, 0.9).y, 1e-12)
    }

    @Test
    fun horizontalBounceRemainsDeterministicAndBoundedAtLongTimes() {
        val motion = beachMotion()
        val first = horizontalBouncePosition(motion, 123_456.75, 1, 0.08, 0.92)
        val again = horizontalBouncePosition(motion, 123_456.75, 1, 0.08, 0.92)

        assertEquals(first, again)
        assertTrue(first.x in 0.08..0.92)
        assertTrue(first.y in (motion.baselineY - motion.bounceHeight)..motion.baselineY)
    }

    @Test
    fun reflectedPositionReversesVisualRotationAtTheWall() {
        val motion = beachMotion(speed = 0.4)
        fun rotation(time: Double): Float = horizontalBounceRotationDegrees(
            motion = motion,
            elapsedSeconds = time,
            directionSign = 1,
            minX = 0.1,
            maxX = 0.9,
            viewportWidthPixels = 1_000.0,
            ballRadiusPixels = 100.0,
        )

        assertTrue(rotation(0.9) < rotation(1.0))
        assertTrue(rotation(1.1) < rotation(1.0))
        assertEquals(0f, rotation(2.0), 1e-4f)
    }

    private fun beachMotion(
        speed: Double = 0.30,
        bounceHeight: Double = 0.6,
        bouncePeriodSeconds: Double = 2.0,
    ) = HorizontalBounceMotion(
        startX = 0.5,
        baselineY = 0.9,
        speed = speed,
        bounceHeight = bounceHeight,
        bouncePeriodSeconds = bouncePeriodSeconds,
    )
}
