package com.avoqadoapp.core.navigation

import kotlinx.coroutines.flow.SharedFlow

interface NavigationManager {
    val navActions: SharedFlow<NavigationCommand>

    fun navigate(command: NavigationCommand)
}