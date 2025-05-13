package com.avoqado.pos.features.payment.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavAnimation
import com.avoqado.pos.core.presentation.navigation.NavigationAction
import com.avoqado.pos.features.payment.presentation.transactions.SummaryTabs

sealed class PaymentDests : NavigationAction {
    data object LeaveReview : PaymentDests() {
        const val ARG_SUBTOTAL = "arg_subtotal"
        const val ARG_WAITER = "arg_waiter"
        const val ARG_SPLIT_TYPE = "arg_split_type"
        const val ARG_VENUE_NAME = "arg_venue_name"

        override val route: String
            get() = "leaveReview?$ARG_SUBTOTAL={$ARG_SUBTOTAL}&$ARG_WAITER={${ARG_WAITER}}&$ARG_SPLIT_TYPE={${ARG_SPLIT_TYPE}}&$ARG_VENUE_NAME={${ARG_VENUE_NAME}}"

        override val arguments: List<NamedNavArgument>
            get() =
                listOf(
                    navArgument(ARG_SUBTOTAL) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_WAITER) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_SPLIT_TYPE) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_VENUE_NAME) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                )

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
    
    data object InputTip : PaymentDests() {
        const val ARG_SUBTOTAL = "arg_subtotal"
        const val ARG_WAITER = "arg_waiter"
        const val ARG_SPLIT_TYPE = "arg_split_type"

        override val route: String
            get() = "inputTip?$ARG_SUBTOTAL={$ARG_SUBTOTAL}&$ARG_WAITER={${ARG_WAITER}}&$ARG_SPLIT_TYPE={${ARG_SPLIT_TYPE}}"

        override val arguments: List<NamedNavArgument>
            get() =
                listOf(
                    navArgument(ARG_SUBTOTAL) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_WAITER) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_SPLIT_TYPE) {
                        type = NavType.StringType
                        nullable = false
                    },
                )

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object PaymentResult : PaymentDests() {
        override val route: String
            get() = "paymentResult"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object TransactionsSummary : PaymentDests() {
        const val ARG_TAB = "arg_tab"

        override val route: String
            get() = "transactions?$ARG_TAB={$ARG_TAB}"

        override val arguments: List<NamedNavArgument>
            get() =
                listOf(
                    navArgument(ARG_TAB) {
                        type = NavType.StringType
                        nullable = false
                        defaultValue = SummaryTabs.RESUMEN.name
                    },
                )

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
    
    data object PaymentDetail : PaymentDests() {
        const val ARG_PAYMENT_ID = "arg_payment_id"
        
        override val route: String
            get() = "payment_detail/{$ARG_PAYMENT_ID}"
            
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_PAYMENT_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object QuickPayment : PaymentDests() {
        override val route: String
            get() = "quickPayment"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
}
