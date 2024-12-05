package com.avoqadoapp.router.destinations

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqadoapp.core.navigation.NavigationAction


sealed class MainDests : NavigationAction {
    data object Splash: MainDests(){
        override val route: String
            get() = "splash"

    }

    data object Home: MainDests(){
        override val route: String
            get() = "home"
    }

    data object CardProcess: MainDests(){

        const val ARG_AMOUNT="amount"
        const val ARG_CURRENCY="currency"
        const val ARG_OPERATION_TYPE="operationType"

        override val route: String
            get() = "cardProcess?$ARG_AMOUNT={$ARG_AMOUNT}&$ARG_CURRENCY={$ARG_CURRENCY}&$ARG_OPERATION_TYPE={$ARG_OPERATION_TYPE}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_AMOUNT) {
                    type = NavType.StringType
                },
                navArgument(ARG_CURRENCY) {
                    type = NavType.StringType
                },
                navArgument(ARG_OPERATION_TYPE) {
                    type = NavType.StringType
                    defaultValue = "null"
                }
            )
    }
}