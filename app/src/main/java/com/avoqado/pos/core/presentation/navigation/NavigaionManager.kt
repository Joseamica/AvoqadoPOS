package com.avoqado.pos.core.presentation.navigation


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NavigationManager {
    val navActions: SharedFlow<NavigationCommand>

    fun navigate(command: NavigationCommand)
}

class NavigationManagerImpl : NavigationManager {
    private val _navActions = MutableSharedFlow<NavigationCommand>(replay = 10)
    override val navActions = _navActions.asSharedFlow()


    override fun navigate(command: NavigationCommand) {
        _navActions.tryEmit(command)
    }
}