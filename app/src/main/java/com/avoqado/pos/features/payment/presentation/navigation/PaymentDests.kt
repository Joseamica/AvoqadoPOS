package com.avoqado.pos.features.payment.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavigationAction

sealed class PaymentDests : NavigationAction {
    data object InputTip: PaymentDests(){

        const val ARG_SUBTOTAL = "arg_subtotal"
        const val ARG_WAITER = "arg_waiter"

        override val route: String
            get() = "inputTip?$ARG_SUBTOTAL={$ARG_SUBTOTAL}&$ARG_WAITER={$ARG_WAITER}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_SUBTOTAL) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(ARG_WAITER) {
                    type = NavType.StringType
                    nullable = false
                },
            )
    }

    data object PaymentResult: PaymentDests(){
        override val route: String
            get() = "paymentResult"
    }
}