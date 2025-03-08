package com.higgins.timedial.ui.timeDial.singleDial

import android.graphics.Paint.Align
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
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
import com.higgins.timedial.ui.timeDial.DialSlot
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val NUMBER_OF_CELLS = 9
private const val CENTER_INDEX = 4
private const val TAG = "NewSingleDial"
@Composable
fun NewSingleDial(
    modifier: Modifier = Modifier,
    state: MutableState<Int>,
    showCenterLines: Boolean = false,
    range: IntRange = 0..12
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val columnHeight = 200.dp
    val halvedColumnHeight = columnHeight / NUMBER_OF_CELLS
    val halvedColumnHeightPx = halvedColumnHeight.dpToPx(density)
    val spacingMultiplier = 1.5f
    val cellHeightWithSpacing = halvedColumnHeightPx * spacingMultiplier

    fun animatedStateValue(offset: Float): Int = state.value - (offset / cellHeightWithSpacing).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        val offsetRange = remember(state.value, range) {
            val value = state.value
            val first = -(range.last - value) * cellHeightWithSpacing
            val last = -(range.first - value) * cellHeightWithSpacing
            first..last
        }
        updateBounds(offsetRange.start, offsetRange.endInclusive)
    }
    val coercedAnimatedOffset = animatedOffset.value % cellHeightWithSpacing
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
                }
            )
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            if (showCenterLines) {
                HorizontalDivider(
                    modifier = Modifier
                        .offset { IntOffset(0, -halvedColumnHeightPx.roundToInt() / NUMBER_OF_CELLS) }
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
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
                        cellHeight = cellHeightWithSpacing.roundToInt()
                    )
                    val xOffset = calculateXOffset(
                        distanceFromCenter = relativeIndex,
                        dragOffset = coercedAnimatedOffset,
                        cellHeight = cellHeightWithSpacing.roundToInt()
                    )
                    val alpha = calculateAlpha(
                        distanceFromCenter = relativeIndex,
                        dragOffset = coercedAnimatedOffset,
                        cellHeight = cellHeightWithSpacing.roundToInt()
                    )
                    val data = (animatedStateValue + relativeIndex)
                    val dataString = if (data < range.first || data > range.last) {
                        ""
                    } else {
                        data.toString()
                    }

                    DialSlot(
                        modifier = baseModifier
                            .offset {
                                IntOffset(
                                    x = xOffset.toInt(),
                                    y = (cellHeightWithSpacing * relativeIndex).toInt()
                                )
                            }
                            .scale(scale)
                            .alpha(alpha),
                        data = dataString
                    )
                }
            }
            if (showCenterLines) {
                HorizontalDivider(
                    modifier = Modifier
                        .offset { IntOffset(0, halvedColumnHeightPx.roundToInt()) }
                )
            }
        }
    }
}