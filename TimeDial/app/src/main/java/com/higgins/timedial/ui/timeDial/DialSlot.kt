package com.higgins.timedial.ui.timeDial

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.higgins.timedial.ui.theme.DefaultTheme

@Composable
fun DialSlot(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 23.sp,
    data: String
) {
    Text(
        modifier = modifier,
        text = data,
        fontSize = fontSize
    )
}

@Preview
@Composable
private fun Preview() {
    DefaultTheme {
        DialSlot(
            data = "1"
        )
    }
}