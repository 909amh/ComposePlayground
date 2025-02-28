package com.higgins.timedial

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

fun List<String>.wrap(firstIndex: Int, length: Int): List<String> {
    val size = this.size
    if (size == 0 || length <= 0) {
        return emptyList()
    }

    val wrappedList = mutableListOf<String>()
    for (i in 0 until length) {
        val index = (firstIndex + i) % size
        wrappedList.add(this[index])
    }

    return wrappedList
}

fun Dp.dpToPx(density: Density) = with(density) { this@dpToPx.toPx() }
fun Int.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }