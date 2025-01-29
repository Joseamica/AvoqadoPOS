package com.avoqado.pos.destinations

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.avoqado.pos.core.navigation.NavigationAction

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
        override val route: String
            get() = "signIn"
    }

    data object Tables: MainDests(){
        override val route: String
            get() = "tables"
    }

    data object TableDetail: MainDests(){
        const val ARG_TABLE_ID = "ARG_TABLE_ID"
        const val ARG_VENUE_ID = "ARG_VENUE_ID"

        override val route: String
            get() = "tableDetail?$ARG_VENUE_ID={${ARG_VENUE_ID}}&$ARG_TABLE_ID={${ARG_TABLE_ID}}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_TABLE_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(ARG_VENUE_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
    }

    data object SplitByProduct: MainDests(){
        override val route: String
            get() = "splitByProduct"
    }

    data object InputTip: MainDests(){

        const val ARG_SUBTOTAL = "arg_subtotal"

        override val route: String
            get() = "inputTip?$ARG_SUBTOTAL={$ARG_SUBTOTAL}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_SUBTOTAL) {
                    type = NavType.StringType
                    nullable = false
                }
            )
    }
}