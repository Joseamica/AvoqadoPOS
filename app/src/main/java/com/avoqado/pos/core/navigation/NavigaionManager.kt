package com.avoqado.pos.core.navigation

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch

interface NavigationManager {
    val navActions: SharedFlow<NavigationCommand>

    fun navigate(command: NavigationCommand)
}

class NavigationManagerImpl : NavigationManager {
    private val _navActions = MutableSharedFlow<NavigationCommand>(replay = 10)
    override val navActions = _navActions.asSharedFlow()

    init {
       GlobalScope.launch(Dispatchers.IO) {
           _navActions.subscriptionCount.collectLatest {
               Log.i("NavigationManagerImpl", "NavManager has a new subscriber -> $it buffer -> ${_navActions.replayCache.size}")
           }
       }
    }

    override fun navigate(command: NavigationCommand) {
        Log.i("NavigationManagerImpl", "NavManager has subscribers -> ${_navActions.subscriptionCount.value}")
        val emited = _navActions.tryEmit(command)
        Log.i("NavigationManagerImpl", "$command is emited -> $emited to $navActions buffer -> ${_navActions.replayCache.size}")
    }
}