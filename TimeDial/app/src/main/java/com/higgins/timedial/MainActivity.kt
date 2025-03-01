package com.higgins.timedial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.higgins.timedial.ui.theme.DefaultTheme
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
                        SingleDial(
                            values = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                            startIndex = 0
                        )
                    }
                }
            }
        }
    }
}