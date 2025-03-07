package com.higgins.timedial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.higgins.timedial.ui.theme.DefaultTheme
import com.higgins.timedial.ui.timeDial.NewSingleDial
import com.higgins.timedial.ui.timeDial.NumberPicker
import com.higgins.timedial.ui.timeDial.SingleDial

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DefaultTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        NewSingleDial(
                            state = remember { mutableIntStateOf(0) }
                        )
                        NumberPicker(remember { mutableIntStateOf(0) })
                    }
                }
            }
        }
    }
}