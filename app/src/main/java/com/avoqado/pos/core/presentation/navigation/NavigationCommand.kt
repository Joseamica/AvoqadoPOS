package com.avoqado.pos.core.presentation.navigation

import androidx.navigation.NavOptions

sealed class NavigationCommand {
    data class NavigateWithAction(
        val navAction: NavigationAction,
    ) : NavigationCommand()

    data class NavigateWithRoute(
        val route: String,
        val navOptions: NavOptions = NavOptions.Builder().build(),
    ) : NavigationCommand()

    data class NavigateWithArguments(
        val navAction: NavigationAction,
        val args: List<NavigationArg>,
    ) : NavigationCommand()

    data class PopToDestination(
        val route: String,
        val inclusive: Boolean,
    ) : NavigationCommand()

    data object Back : NavigationCommand()

    data class BackWithArguments(
        val args: List<NavigationArg>,
    ) : NavigationCommand()

    data class NavigateToUrlExternally(
        val httpLink: String,
    ) : NavigationCommand()
}
