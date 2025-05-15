package com.avoqado.pos.features.cart.presentation.navigation

import com.avoqado.pos.core.presentation.navigation.NavAnimation
import com.avoqado.pos.core.presentation.navigation.NavigationAction

/**
 * Navigation destinations for the cart feature
 */
object CartDests {
    // Main cart screen destination
    object Cart : NavigationAction {
        override val route = "cart"
        override val navAnimation: NavAnimation?
            get() = NavAnimation.none()
    }
}
