package com.avoqado.pos.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavOptions

typealias enterAnim =
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?

typealias exitAnim =
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?

interface NavigationAction {
    val route: String
    val navOptions: NavOptions
        get() = NavOptions.Builder().build()
    val arguments: List<NamedNavArgument>
        get() = emptyList()
    val navAnimation: NavAnimation?
        get() = null
    val deepLinks: List<NavDeepLink>
        get() = emptyList()
}

data class NavAnimation(
    val enterTransition: enterAnim,
    val exitTransition: exitAnim,
    val popEnterTransition: enterAnim,
    val popExitTransition: exitAnim
) {
    companion object {
        fun horizontalSlide() = NavAnimation(
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        )
    }
}