package com.higgins.timedial.ui.timeDial

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.higgins.timedial.dpToPx
import com.higgins.timedial.pxToDp
import com.higgins.timedial.spToPx
import com.higgins.timedial.ui.theme.DefaultTheme
import com.higgins.timedial.wrap
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val NUMBER_OF_CELLS = 9
private const val CENTER_INDEX = 4
private const val TAG = "SingleDial2"
@Composable
fun SingleDial(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    values: List<String>
) {
    // Environment
    val density = LocalDensity.current

    // The height of a single dial cell
    val cellHeight = 24.sp.spToPx(density)
    val cellHeightMap = remember { mutableStateMapOf<Int, Int>() }

    // The height of the container for the dial cells
    val columnHeight = (cellHeight * NUMBER_OF_CELLS).roundToInt().pxToDp(density)
    // Current First Index
    var currentStartIndex by remember { mutableIntStateOf(startIndex) }

    // The Y Offset from the drag gesture
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val coercedDragOffsetY = dragOffsetY % (cellHeightMap[CENTER_INDEX] ?: 80)
    val draggableState = rememberDraggableState { delta ->
        dragOffsetY += delta
        if (dragOffsetY >= (cellHeightMap[CENTER_INDEX] ?: 80)) {
            val newIndex = currentStartIndex - 1
            currentStartIndex = if (newIndex < 0) {
                values.lastIndex
            } else {
                newIndex
            }
        } else if (dragOffsetY <= (-(cellHeightMap[CENTER_INDEX] ?: 80))) {
            val newIndex = currentStartIndex + 1
            currentStartIndex = if (newIndex >= values.size) 0 else newIndex
        }
        Log.d(TAG, "Drag Offset: $dragOffsetY")
        Log.d(TAG, "Cell Height: $cellHeight")
    }
    Box(
        modifier = modifier
            .background(Color.Red)
            .height(columnHeight)
            .width(56.dp)
            .offset {
                IntOffset(
                    x = 0,
                    y = coercedDragOffsetY.roundToInt()
                )
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = draggableState,
                onDragStopped = {
                    dragOffsetY = 0f
                }
            )
    ) {
        val visibleValues = values.wrap(currentStartIndex, 9)

        visibleValues.forEachIndexed { index, value ->
            val distanceFromCenter = index - CENTER_INDEX

            val scale = calculateScale(
                distanceFromCenter = distanceFromCenter,
                dragOffset = dragOffsetY,
                cellHeight = cellHeightMap[CENTER_INDEX] ?: 80
            )

            val yOffset = calculateYOffset(
                distanceFromCenter = distanceFromCenter,
                cellHeightMap = cellHeightMap,
                index = index
            )

            DialSlot(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = yOffset.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .onGloballyPositioned {
                        Log.d(TAG, "On Globally Positioned")
                        if (cellHeightMap[index] == null) {
                            cellHeightMap[index] = it.size.height
                        }
                        Log.d(TAG, "Cell Height Map: ${cellHeightMap}")
                    },
                data = value,
                fontSize = 24.sp
            )
        }
    }
}

private fun calculateYOffset(
    index: Int,
    distanceFromCenter: Int,
    cellHeightMap: Map<Int, Int>
): Float {
    return if (distanceFromCenter < 0) {
        -cellHeightMap.filterKeys { it < CENTER_INDEX && it >= index }.values.sumOf { it }.toFloat()

    } else if (distanceFromCenter > 0) {
        cellHeightMap.filterKeys { it > CENTER_INDEX && it <= index }.values.sumOf { it }.toFloat()
    } else {
        0f
    }
}


/**
 * Calculates the scale of a cell based on it's index and the current progress of the dragOffset.
 */
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

@Preview
@Composable
private fun Preview() {
    DefaultTheme {
        SingleDial(
            startIndex = 0,
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        )
    }
}