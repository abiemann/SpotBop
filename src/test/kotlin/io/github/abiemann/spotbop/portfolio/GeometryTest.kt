package io.github.abiemann.spotbop.portfolio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeometryTest {

    @Test
    fun circleUsesVisibleRadius() {
        assertTrue(circleContains(130f, 80f, 100f, 80f, 30f))
        assertFalse(circleContains(130.1f, 80f, 100f, 80f, 30f))
        assertFalse(circleContains(128f, 108f, 100f, 80f, 30f))
    }

    @Test
    fun rotatedEllipseTransformsTapIntoLocalSpace() {
        assertTrue(pointInRotatedEllipse(100f, 130f, 100f, 100f, 50f, 20f, 90f))
        assertFalse(pointInRotatedEllipse(150f, 100f, 100f, 100f, 50f, 20f, 90f))
    }

    @Test
    fun triangleAndPolygonClassifyInteriorPoints() {
        assertTrue(pointInTriangle(1f, 1f, 0f, 0f, 4f, 0f, 0f, 4f))
        assertFalse(pointInTriangle(4f, 4f, 0f, 0f, 4f, 0f, 0f, 4f))

        val square = floatArrayOf(0f, 0f, 4f, 0f, 4f, 4f, 0f, 4f)
        assertTrue(pointInPolygon(2f, 2f, square))
        assertFalse(pointInPolygon(5f, 2f, square))
    }

    @Test
    fun rectangleIncludesItsVisibleEdges() {
        assertTrue(pointInRectangle(0f, 0f, 0f, 0f, 10f, 10f))
        assertTrue(pointInRectangle(10f, 10f, 0f, 0f, 10f, 10f))
        assertFalse(pointInRectangle(10.1f, 10f, 0f, 0f, 10f, 10f))
    }

    @Test
    fun distanceToSegmentClampsBeyondEndpoints() {
        assertEquals(4f, distanceToSegmentSquared(-2f, 0f, 0f, 0f, 10f, 0f), 0f)
        assertEquals(9f, distanceToSegmentSquared(5f, 3f, 0f, 0f, 10f, 0f), 0f)
        assertEquals(4f, distanceToSegmentSquared(12f, 0f, 0f, 0f, 10f, 0f), 0f)
    }
}
