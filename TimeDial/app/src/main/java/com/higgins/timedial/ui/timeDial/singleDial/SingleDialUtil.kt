package com.higgins.timedial.ui.timeDial.singleDial

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.higgins.timedial.dpToPx
import kotlin.math.absoluteValue

fun calculateScale(
    distanceFromCenter: Int,
    dragOffset: Float,
    cellHeight: Int
): Float {
    val baseScale = calculateBaseScale(distanceFromCenter)
    val dragProgress = (dragOffset.absoluteValue / cellHeight)

    val finalScale = when {
        // Dragging Down
        dragOffset > 0f -> {
            val nextIndex = distanceFromCenter + 1
            val nextBaseScale = calculateBaseScale(nextIndex)
            baseScale + (nextBaseScale - baseScale) * dragProgress
        }
        // Dragging Up
        dragOffset < 0f -> {
            val nextIndex = distanceFromCenter - 1
            val nextBaseScale = calculateBaseScale(nextIndex)
            baseScale + (nextBaseScale - baseScale) * dragProgress
        }
        else -> baseScale
    }

    return finalScale
}

fun calculateBaseScale(
    distanceFromCenter: Int
): Float {
    return when (distanceFromCenter.absoluteValue) {
        0 -> 1f
        1 -> 0.91f
        2 -> 0.73f
        3 -> 0.52f
        else -> 0.0f
    }
}

@Composable
fun calculateBaseXOffset(
    distanceFromCenter: Int
): Float {
    val density = LocalDensity.current
    return when (distanceFromCenter.absoluteValue) {
        0 -> 0f
        1 -> 1.dp.dpToPx(density)
        2 -> 2.dp.dpToPx(density)
        3 -> 3.dp.dpToPx(density)
        4 -> 4.dp.dpToPx(density)
        else -> 0f
    }
}

@Composable
fun calculateXOffset(
    distanceFromCenter: Int,
    dragOffset: Float,
    cellHeight: Int
): Float {
    val baseXOffset = calculateBaseXOffset(distanceFromCenter)
    val dragProgress = dragOffset.absoluteValue / cellHeight

    val finalXOffset = when {
        // Dragging Down
        dragOffset > 0f -> {
            val nextIndex = distanceFromCenter + 1
            val nextBaseXOffset = calculateBaseXOffset(nextIndex)
            baseXOffset + (nextBaseXOffset - baseXOffset) * dragProgress
        }
        // Dragging Up
        dragOffset < 0f -> {
            val nextIndex = distanceFromCenter - 1
            val nextBaseXOffset = calculateBaseXOffset(nextIndex)
            baseXOffset + (nextBaseXOffset - baseXOffset) * dragProgress
        }
        else -> baseXOffset
    }

    return finalXOffset
}

fun calculateBaseAlpha(
    distanceFromCenter: Int
): Float {
    return when (distanceFromCenter.absoluteValue) {
        0 -> 1f
        1 -> 0.8f
        2 -> 0.6f
        3 -> 0.4f
        4 -> 0.2f
        else -> 1f
    }
}

fun calculateAlpha(
    distanceFromCenter: Int,
    dragOffset: Float,
    cellHeight: Int
): Float {
    val baseAlpha = calculateBaseAlpha(distanceFromCenter)
    val dragProgress = dragOffset.absoluteValue / cellHeight

    val finalAlpha = when {
        // Dragging Down
        dragOffset > 0f -> {
            val nextIndex = distanceFromCenter + 1
            val nextBaseAlpha = calculateBaseAlpha(nextIndex)
            baseAlpha + (nextBaseAlpha - baseAlpha) * dragProgress
        }
        // Dragging Up
        dragOffset < 0f -> {
            val nextIndex = distanceFromCenter - 1
            val nextBaseAlpha = calculateBaseAlpha(nextIndex)
            baseAlpha + (nextBaseAlpha - baseAlpha) * dragProgress
        }
        else -> baseAlpha
    }

    return finalAlpha
}