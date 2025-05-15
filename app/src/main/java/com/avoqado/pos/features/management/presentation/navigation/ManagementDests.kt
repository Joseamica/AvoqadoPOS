package com.avoqado.pos.features.management.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavAnimation
import com.avoqado.pos.core.presentation.navigation.NavigationAction

sealed class ManagementDests : NavigationAction {
    data object Home : ManagementDests() {
        override val route: String
            get() = "home"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object TableDetail : ManagementDests() {
        const val ARG_TABLE_ID = "ARG_TABLE_ID"
        const val ARG_VENUE_ID = "ARG_VENUE_ID"

        override val route: String
            get() = "tableDetail?$ARG_VENUE_ID={${ARG_VENUE_ID}}&$ARG_TABLE_ID={${ARG_TABLE_ID}}"

        override val arguments: List<NamedNavArgument>
            get() =
                listOf(
                    navArgument(ARG_TABLE_ID) {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(ARG_VENUE_ID) {
                        type = NavType.StringType
                        nullable = false
                    },
                )

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object SplitByProduct : ManagementDests() {
        override val route: String
            get() = "splitByProduct"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object SplitByPerson : ManagementDests() {
        override val route: String
            get() = "splitByPerson"
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
    
    data object ShiftNotStartedBS : ManagementDests() {
        override val route: String
            get() = "shiftNotStartedBottomSheet"
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object VenueTables : ManagementDests() {
        override val route: String
            get() = "tables"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }

    data object OpenShift : ManagementDests() {
        override val route: String
            get() = "openShift"

        override val navAnimation: NavAnimation?
            get() = NavAnimation.none()
    }
    
    data object MenuList : ManagementDests() {
        const val ARG_VENUE_ID = "ARG_VENUE_ID"
        
        override val route: String
            get() = "menuList?$ARG_VENUE_ID={${ARG_VENUE_ID}}"
            
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_VENUE_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
    
    data object MenuDetail : ManagementDests() {
        const val ARG_MENU_ID = "ARG_MENU_ID"
        
        override val route: String
            get() = "menuDetail?$ARG_MENU_ID={${ARG_MENU_ID}}"
            
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_MENU_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
            
        fun createRoute(menuId: String): String {
            return "menuDetail?$ARG_MENU_ID=$menuId"
        }
    }
}
