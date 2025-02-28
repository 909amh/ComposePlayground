package com.higgins.fluxarchitecture

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.higgins.fluxarchitecture.MainViewModel.MainAction.ButtonClicked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: BaseViewModel<MainViewModel.MainAction, MainViewModel.MainEffect>() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect = _effect.asSharedFlow()

    sealed interface MainAction: Action {
        data object ButtonClicked: MainAction
    }

    sealed interface MainEffect: Effect {
        data class ShowToast(val message: String): MainEffect
    }

    override fun reduce(action: MainAction) {
        when (action) {
            is ButtonClicked -> onClickButton()
        }
    }

    private fun onClickButton() {
        val newMessage = "I am changing State!"
        _state.value.message = newMessage

        viewModelScope.launch(Dispatchers.Default) {
            _effect.emit(MainEffect.ShowToast(newMessage))
        }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel()
            }
        }
    }
}