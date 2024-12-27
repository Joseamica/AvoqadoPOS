package com.avoqado.pos.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.avoqado.pos.core.navigation.NavigationAction

fun NavGraphBuilder.dialogHolder(
    action: NavigationAction,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    dialog(
        route = action.route,
        arguments = action.arguments,
        dialogProperties = dialogProperties
    ) {
        content()
    }
}

fun NavGraphBuilder.composableHolder(
    action: NavigationAction,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = action.route,
        arguments = action.arguments,
        enterTransition = action.navAnimation?.enterTransition,
        exitTransition = action.navAnimation?.exitTransition,
        popEnterTransition = action.navAnimation?.popEnterTransition,
        popExitTransition = action.navAnimation?.popExitTransition,
        deepLinks = action.deepLinks
    ) {
        content(it)
    }
}

fun NavGraphBuilder.composableHolderWithArgs(
    action: NavigationAction,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = action.route,
        arguments = action.arguments,
        enterTransition = action.navAnimation?.enterTransition,
        exitTransition = action.navAnimation?.exitTransition,
        popEnterTransition = action.navAnimation?.popEnterTransition,
        popExitTransition = action.navAnimation?.popExitTransition,
        deepLinks = action.deepLinks
    ) { navBackStack ->
        content(navBackStack)
    }
}
