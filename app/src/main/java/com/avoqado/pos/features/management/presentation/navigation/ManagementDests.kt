package com.avoqado.pos.features.management.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavigationAction

sealed class ManagementDests : NavigationAction {
    data object Home: ManagementDests(){
        override val route: String
            get() = "home"
    }

    data object TableDetail: ManagementDests(){
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

    data object SplitByProduct: ManagementDests(){
        override val route: String
            get() = "splitByProduct"
    }

    data object SplitByPerson: ManagementDests(){
        override val route: String
            get() = "splitByPerson"
    }

    data object VenueTables: ManagementDests() {
        override val route: String
            get() = "tables"
    }

    data object OpenShift: ManagementDests() {
        override val route: String
            get() = "openShift"
    }
}