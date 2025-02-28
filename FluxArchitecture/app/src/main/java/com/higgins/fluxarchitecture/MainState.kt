package com.higgins.fluxarchitecture

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainState {
    var message: String by mutableStateOf("I am a Screen!")
}