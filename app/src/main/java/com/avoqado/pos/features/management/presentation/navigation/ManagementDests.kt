package com.avoqado.pos.features.management.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavigationAction

sealed class ManagementDests : NavigationAction {
    data object Tables: ManagementDests(){
        override val route: String
            get() = "tables"
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
}