package com.higgins.fluxarchitecture

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<Action: BaseViewModel.Action, Effect: BaseViewModel.Effect>: ViewModel() {
    abstract fun reduce(action: Action)

    interface Action
    interface Effect
}