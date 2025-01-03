package com.avoqado.pos.core.navigation

import android.util.Log
import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.Flow


class NavigationDispatcher (
    private val navigationManager: NavigationManager
) {
    val navigationCommands: Flow<NavigationCommand> = navigationManager.navActions

    fun navigateTo(command: NavigationCommand) {
        navigationManager.navigate(command)
    }

    fun navigateTo(navAction: NavigationAction) {
        Log.i("NavigationDispatcher", "navigateTo: ${navAction.route}")
        navigationManager.navigate(NavigationCommand.NavigateWithAction(navAction))
    }

    fun navigateTo(
        route: String,
        navOptions: NavOptions = NavOptions.Builder().build()
    ) {
        navigationManager.navigate(NavigationCommand.NavigateWithRoute(route, navOptions))
    }

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }

    fun navigateBackWithResult(vararg arg: NavigationArg) {
        navigationManager.navigate(NavigationCommand.BackWithArguments(arg.toList()))
    }

    fun popToDestination(navAction: NavigationAction, inclusive: Boolean) {
        navigationManager.navigate(NavigationCommand.PopToDestination(navAction.route, inclusive))
    }

    fun popToDestination(route: String, inclusive: Boolean) {
        navigationManager.navigate(NavigationCommand.PopToDestination(route, inclusive))
    }

    fun navigateWithArgs(navAction: NavigationAction, vararg arg: NavigationArg) {
        navigationManager.navigate(NavigationCommand.NavigateWithArguments(navAction, arg.toList()))
    }

    fun openHttpLinkExternally(url: String) {
        navigationManager.navigate(NavigationCommand.NavigateToUrlExternally(url))
    }
}