package com.higgins.fluxarchitecture

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.higgins.fluxarchitecture.MainViewModel.MainAction.ButtonClicked
import com.higgins.fluxarchitecture.MainViewModel.MainEffect.ShowToast
import com.higgins.fluxarchitecture.ui.theme.FluxArchitectureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FluxArchitectureTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory())
) {
    val state = viewModel.state.collectAsState().value
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Handles Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ShowToast -> showToast(context, effect.message)
            }
        }
    }

    Box(
        modifier = Modifier
            .background(colors.background)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.message,
                color = colors.onBackground
            )
            Button(
                onClick = { viewModel.reduce(ButtonClicked) }
            ) {
                Text(text = "Click Me!")
            }
        }
    }
}

fun showToast(
    context: Context,
    message: String
) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Preview
@Composable
private fun Preview() {
    FluxArchitectureTheme {
        MainScreen()
    }
}
