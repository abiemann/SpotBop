package io.github.abiemann.spotbop.portfolio

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

data class NormPos(val x: Double, val y: Double)

data class DriftMotion(
    val centerX: Double,
    val centerY: Double,
    val amplitudeX: Double,
    val amplitudeY: Double,
    val periodX: Double,
    val periodY: Double,
    val rotationPeriodSeconds: Double,
)

data class PathMotion(
    val fromX: Double,
    val fromY: Double,
    val toX: Double,
    val toY: Double,
    val durationSeconds: Double,
    val phase: Double,
    val loop: Boolean,
)

data class HorizontalBounceMotion(
    val startX: Double,
    val baselineY: Double,
    val speed: Double,
    val bounceHeight: Double,
    val bouncePeriodSeconds: Double,
)

private const val TWO_PI = 2.0 * PI

/** Drift-free position: the same time always produces the same pose. */
fun driftPosition(motion: DriftMotion, elapsedSeconds: Double): NormPos = NormPos(
    motion.centerX + motion.amplitudeX * sin(TWO_PI * elapsedSeconds / motion.periodX),
    motion.centerY + motion.amplitudeY * sin(TWO_PI * elapsedSeconds / motion.periodY),
)

fun driftRotationDegrees(motion: DriftMotion, elapsedSeconds: Double): Float =
    ((elapsedSeconds / motion.rotationPeriodSeconds) * 360.0).mod(360.0).toFloat()

/** A linear path that can either loop or clamp at its destination. */
fun pathPosition(path: PathMotion, elapsedSeconds: Double): NormPos {
    var progress = elapsedSeconds / path.durationSeconds + path.phase
    progress = if (path.loop) {
        progress - floor(progress)
    } else {
        progress.coerceIn(0.0, 1.0)
    }

    return NormPos(
        path.fromX + (path.toX - path.fromX) * progress,
        path.fromY + (path.toY - path.fromY) * progress,
    )
}

/** Lossless wall reflection plus a periodic parabolic hop. */
fun horizontalBouncePosition(
    motion: HorizontalBounceMotion,
    elapsedSeconds: Double,
    directionSign: Int,
    minX: Double,
    maxX: Double,
): NormPos = NormPos(
    horizontalBounceX(motion, elapsedSeconds, directionSign, minX, maxX),
    horizontalBounceY(motion, elapsedSeconds),
)

internal fun horizontalBounceX(
    motion: HorizontalBounceMotion,
    elapsedSeconds: Double,
    directionSign: Int,
    minX: Double,
    maxX: Double,
): Double {
    require(directionSign == -1 || directionSign == 1) {
        "directionSign must be -1 or 1"
    }
    require(minX < maxX) { "horizontal bounce bounds must have positive width" }
    require(motion.speed > 0.0) { "horizontal bounce speed must be positive" }

    val span = maxX - minX
    val start = motion.startX.coerceIn(minX, maxX)
    val distance = start - minX + directionSign * motion.speed * elapsedSeconds
    val reflected = positiveModulo(distance, span * 2.0)
    return minX + if (reflected <= span) reflected else span * 2.0 - reflected
}

internal fun horizontalBounceY(
    motion: HorizontalBounceMotion,
    elapsedSeconds: Double,
): Double {
    require(motion.bouncePeriodSeconds > 0.0) {
        "horizontal bounce period must be positive"
    }
    val progress = positiveModulo(
        elapsedSeconds,
        motion.bouncePeriodSeconds,
    ) / motion.bouncePeriodSeconds
    val lift = 4.0 * motion.bounceHeight * progress * (1.0 - progress)
    return motion.baselineY - lift
}

/** No-slip visual rotation; reflected X reverses spin at a wall. */
internal fun horizontalBounceRotationDegrees(
    motion: HorizontalBounceMotion,
    elapsedSeconds: Double,
    directionSign: Int,
    minX: Double,
    maxX: Double,
    viewportWidthPixels: Double,
    ballRadiusPixels: Double,
): Float {
    require(viewportWidthPixels > 0.0) { "viewport width must be positive" }
    require(ballRadiusPixels > 0.0) { "ball radius must be positive" }

    val startX = horizontalBounceX(motion, 0.0, directionSign, minX, maxX)
    val currentX = horizontalBounceX(
        motion,
        elapsedSeconds,
        directionSign,
        minX,
        maxX,
    )
    val signedDistancePixels = (currentX - startX) * viewportWidthPixels
    return (signedDistancePixels / ballRadiusPixels * 180.0 / PI).toFloat()
}

internal fun positiveModulo(value: Double, modulus: Double): Double {
    val remainder = value % modulus
    return if (remainder < 0.0) remainder + modulus else remainder
}
