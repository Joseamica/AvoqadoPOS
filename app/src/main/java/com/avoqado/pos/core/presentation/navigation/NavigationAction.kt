package com.avoqado.pos.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        // Animación horizontal estándar
        fun horizontalSlide() = NavAnimation(
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        )
        
        // Animación de fade simple - más eficiente para dispositivos con poca RAM
        fun fade() = NavAnimation(
            enterTransition = { fadeIn(initialAlpha = 0.3f) },
            exitTransition = { fadeOut(targetAlpha = 0.3f) },
            popEnterTransition = { fadeIn(initialAlpha = 0.3f) },
            popExitTransition = { fadeOut(targetAlpha = 0.3f) }
        )
        
        // Sin animación - opción más eficiente para dispositivos muy limitados
        fun none() = NavAnimation(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        )
    }
}