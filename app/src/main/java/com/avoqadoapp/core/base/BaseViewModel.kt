package com.avoqadoapp.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<ScreenState : Any, Action>(
    initialState: ScreenState
) : ViewModel() {

    val mState: MutableStateFlow<ScreenState> = MutableStateFlow(initialState)
    open val state: StateFlow<ScreenState> = mState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState
    )

    private val actions = MutableSharedFlow<Action>()

    open val viewObservables: List<Flow<Unit>> = emptyList()

    init {
        collectActions()
    }

    open suspend fun handleActions(action: Action) {
        // To or not to override, some screens mights not have actions
    }

    private fun collectActions() = viewModelScope.launch {
        actions.collect { handleActions(it) }
    }

    val submitAction: (action: Action) -> Unit = {
        viewModelScope.launch { actions.emit(it) }
    }

    inline fun updateState(crossinline function: ScreenState.() -> ScreenState) {
        mState.update(function)
    }
}
