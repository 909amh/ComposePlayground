package com.higgins.timedial.ui.timeDial

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.higgins.timedial.dpToPx
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val NUMBER_OF_CELLS = 9
private const val CENTER_INDEX = 4
private const val TAG = "NewSingleDial"
@Composable
fun NewSingleDial(
    modifier: Modifier = Modifier,
    state: MutableState<Int>,
    range: IntRange = 0..12
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val columnHeight = 200.dp
    val halvedColumnHeight = columnHeight / NUMBER_OF_CELLS
    val halvedColumnHeightPx = halvedColumnHeight.dpToPx(density)

    fun animatedStateValue(offset: Float): Int = state.value - (offset / halvedColumnHeightPx).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        val offsetRange = remember(state.value, range) {
            val value = state.value
            val first = -(range.last - value) * halvedColumnHeightPx
            val last = -(range.first - value) * halvedColumnHeightPx
            first..last
        }
        updateBounds(offsetRange.start, offsetRange.endInclusive)
    }
    val coercedAnimatedOffset = animatedOffset.value % halvedColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    scope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset {
                    IntOffset(
                        x = 0,
                        y = coercedAnimatedOffset.roundToInt()
                    )
                }
        ) {
            val baseModifier = Modifier.align(Alignment.Center)
            for (i in 0..NUMBER_OF_CELLS) {
                val relativeIndex = i - CENTER_INDEX
                val scale = calculateScale(
                    distanceFromCenter = relativeIndex,
                    dragOffset = coercedAnimatedOffset,
                    cellHeight = halvedColumnHeightPx.roundToInt()
                )
                val xOffset = calculateXOffset(
                    distanceFromCenter = relativeIndex,
                    dragOffset = coercedAnimatedOffset,
                    cellHeight = halvedColumnHeightPx.roundToInt()
                )
                val alpha = calculateAlpha(
                    distanceFromCenter = relativeIndex,
                    dragOffset = coercedAnimatedOffset,
                    cellHeight = halvedColumnHeightPx.roundToInt()
                )
                DialSlot(
                    modifier = baseModifier
                        .offset {
                            IntOffset(
                                x = xOffset.toInt(),
                                y = (halvedColumnHeightPx * relativeIndex).toInt()
                            )
                        }
                        .scale(scale)
                        .alpha(alpha),
                    data = (animatedStateValue + relativeIndex).toString()
                )
            }
        }
    }
}

private fun calculateScale(
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

private fun calculateBaseScale(
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
private fun calculateBaseXOffset(
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
private fun calculateXOffset(
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

private fun calculateBaseAlpha(
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

private fun calculateAlpha(
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

suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}