package com.avoqado.pos.destinations

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.avoqado.pos.core.presentation.navigation.NavigationAction

sealed class MainDests : NavigationAction {

    data object Authorization: MainDests(){
        override val route: String
            get() = "authorization"

        override val deepLinks: List<NavDeepLink>
            get() = listOf(
                navDeepLink {
                    uriPattern = "menta://login.ui/unauthorized"
                }
            )
    }

    data object Splash: MainDests(){
        override val route: String
            get() = "splash"
    }
    data object SignIn: MainDests(){

        const val ARG_REDIRECT = "redirect"

        override val route: String
            get() = "signIn?$ARG_REDIRECT={$ARG_REDIRECT}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_REDIRECT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
    }

}