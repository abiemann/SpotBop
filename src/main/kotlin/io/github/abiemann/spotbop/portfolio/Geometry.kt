package io.github.abiemann.spotbop.portfolio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun pointInRotatedEllipse(
    x: Float,
    y: Float,
    centerX: Float,
    centerY: Float,
    radiusX: Float,
    radiusY: Float,
    rotationDegrees: Float,
): Boolean {
    val theta = rotationDegrees * PI / 180.0
    val cosTheta = cos(theta).toFloat()
    val sinTheta = sin(theta).toFloat()
    val dx = x - centerX
    val dy = y - centerY

    return pointInEllipse(
        dx * cosTheta + dy * sinTheta,
        -dx * sinTheta + dy * cosTheta,
        0f,
        0f,
        radiusX,
        radiusY,
    )
}

internal fun circleContains(
    x: Float,
    y: Float,
    centerX: Float,
    centerY: Float,
    radius: Float,
): Boolean {
    val dx = x - centerX
    val dy = y - centerY
    return radius > 0f && dx * dx + dy * dy <= radius * radius
}

internal fun pointInTriangle(
    px: Float,
    py: Float,
    ax: Float,
    ay: Float,
    bx: Float,
    by: Float,
    cx: Float,
    cy: Float,
): Boolean {
    val d1 = triangleSign(px, py, ax, ay, bx, by)
    val d2 = triangleSign(px, py, bx, by, cx, cy)
    val d3 = triangleSign(px, py, cx, cy, ax, ay)
    val hasNegative = d1 < 0f || d2 < 0f || d3 < 0f
    val hasPositive = d1 > 0f || d2 > 0f || d3 > 0f
    return !(hasNegative && hasPositive)
}

internal fun pointInPolygon(x: Float, y: Float, vertices: FloatArray): Boolean {
    require(vertices.size >= 6 && vertices.size % 2 == 0) {
        "a polygon requires at least three x/y vertex pairs"
    }

    var inside = false
    var previous = vertices.size - 2
    var current = 0
    while (current < vertices.size) {
        val currentX = vertices[current]
        val currentY = vertices[current + 1]
        val previousX = vertices[previous]
        val previousY = vertices[previous + 1]
        if ((currentY > y) != (previousY > y) &&
            x < (previousX - currentX) * (y - currentY) /
                (previousY - currentY) + currentX
        ) {
            inside = !inside
        }
        previous = current
        current += 2
    }
    return inside
}

internal fun pointInEllipse(
    x: Float,
    y: Float,
    centerX: Float,
    centerY: Float,
    radiusX: Float,
    radiusY: Float,
): Boolean {
    if (radiusX <= 0f || radiusY <= 0f) return false
    val dx = (x - centerX) / radiusX
    val dy = (y - centerY) / radiusY
    return dx * dx + dy * dy <= 1f
}

internal fun pointInRectangle(
    x: Float,
    y: Float,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
): Boolean = x in left..right && y in top..bottom

internal fun distanceToSegmentSquared(
    px: Float,
    py: Float,
    ax: Float,
    ay: Float,
    bx: Float,
    by: Float,
): Float {
    val vx = bx - ax
    val vy = by - ay
    val lengthSquared = vx * vx + vy * vy
    if (lengthSquared == 0f) {
        val dx = px - ax
        val dy = py - ay
        return dx * dx + dy * dy
    }

    val progress = ((px - ax) * vx + (py - ay) * vy) / lengthSquared
    val clamped = progress.coerceIn(0f, 1f)
    val dx = px - (ax + clamped * vx)
    val dy = py - (ay + clamped * vy)
    return dx * dx + dy * dy
}

private fun triangleSign(
    px: Float,
    py: Float,
    ax: Float,
    ay: Float,
    bx: Float,
    by: Float,
): Float = (px - bx) * (ay - by) - (ax - bx) * (py - by)
