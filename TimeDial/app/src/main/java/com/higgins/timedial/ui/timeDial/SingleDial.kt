package com.higgins.timedial.ui.timeDial

import android.nfc.Tag
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.higgins.timedial.dpToPx
import com.higgins.timedial.pxToDp
import com.higgins.timedial.ui.theme.DefaultTheme
import com.higgins.timedial.wrap
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val CENTER_INDEX = 4
private const val VISIBLE_ITEM_COUNT = 9
private const val Tag = "SingleDial"
@Composable
fun SingleDial(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    values: List<String>
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var firstIndex by remember { mutableIntStateOf(startIndex) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val animatedOffset = remember { Animatable(0f) }
    val coercedAnimatedOffset = animatedOffset.value % 24.dp.dpToPx(density)

    val itemHeightPx = 24.dp.dpToPx(density)
    LaunchedEffect(coercedAnimatedOffset) {
        val threshold = itemHeightPx / 2
        if (coercedAnimatedOffset > threshold) {
            val newIndex = firstIndex - 1
            firstIndex = if (newIndex >= 0) { (firstIndex - 1) % values.size }
            else { values.lastIndex % values.size }

            animatedOffset.snapTo(animatedOffset.value - itemHeightPx)

        } else if (coercedAnimatedOffset < -threshold) {
            firstIndex = (firstIndex + 1 + values.size) % values.size
            animatedOffset.snapTo(animatedOffset.value + itemHeightPx)
        }
    }

    Column(
        modifier = Modifier
            .height((itemHeightPx * 9).toInt().pxToDp(density))
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    scope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                }
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = coercedAnimatedOffset.roundToInt()
                    )
                },
        ) {
            val visibleValues = values.wrap(firstIndex, 9)
            visibleValues.forEachIndexed { index, value ->
                // Distance from the center
                val relativeIndex = index - CENTER_INDEX
                val yOffset = (relativeIndex * 24.dp.dpToPx(density))

                var fontSize = 0.sp
                when(index) {
                    0 -> {
                        fontSize = 6.sp
                    }
                    1 -> {
                        fontSize = 12.sp
                    }
                    2 -> {
                        fontSize = 17.sp
                    }
                    3 -> {
                        fontSize = 21.sp
                    }
                    CENTER_INDEX -> {
                        fontSize = 26.sp
                    }
                    5 -> {
                        fontSize = 21.sp
                    }
                    6 -> {
                        fontSize = 17.sp
                    }
                    7 -> {
                        fontSize = 12.sp
                    }
                    8 -> {
                        fontSize = 6.sp
                    }
                    else -> {
                        fontSize = 0.sp
                    }
                }

                DialSlot(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = yOffset.toInt()
                            )
                        },
                    data = value,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DefaultTheme {
        SingleDial(
            values = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        )
    }
}