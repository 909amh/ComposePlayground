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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.higgins.timedial.dpToPx
import com.higgins.timedial.pxToDp
import com.higgins.timedial.ui.theme.DefaultTheme
import com.higgins.timedial.wrap
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val NUMBER_OF_CELLS = 9
private const val CENTER_INDEX = 4
private const val TAG = "SingleDial2"
@Composable
fun SingleDial2(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    values: List<String>
) {
    // Environment
    val density = LocalDensity.current

    // The height of a single dial cell
    val cellHeight = 24.dp.dpToPx(density)
    // The height of the container for the dial cells
    val columnHeight = (cellHeight * NUMBER_OF_CELLS).roundToInt().pxToDp(density)
    // Current First Index
    var currentStartIndex by remember { mutableIntStateOf(startIndex) }

    // The Y Offset from the drag gesture
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val coercedDragOffsetY = dragOffsetY % cellHeight
    val draggableState = rememberDraggableState { delta ->
        dragOffsetY += delta
        if (dragOffsetY >= cellHeight) {
            val newIndex = currentStartIndex - 1
            currentStartIndex = if (newIndex < 0) {
                values.lastIndex
            } else {
                newIndex
            }
            dragOffsetY = 0f
        } else if (dragOffsetY <= -cellHeight) {
            val newIndex = currentStartIndex + 1
            currentStartIndex = if (newIndex > values.size) 0 else newIndex
            dragOffsetY = 0f
        }
        Log.v(TAG, "Drag Offset: ${dragOffsetY}")
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
                state = draggableState
            )
    ) {
        val visibleValues = values.wrap(currentStartIndex, 9)
        visibleValues.forEachIndexed { index, value ->
            val distanceFromCenter = index - CENTER_INDEX
            val scale = 1 + (1 - (distanceFromCenter.absoluteValue * 0.5f))

            DialSlot(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = distanceFromCenter * cellHeight.roundToInt()
                        )
                    }
                    .scale(scale),
                data = value,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DefaultTheme {
        SingleDial2(
            startIndex = 0,
            values = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        )
    }
}