package com.avoqado.pos.features.menu.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avoqado.pos.core.presentation.navigation.NavAnimation
import com.avoqado.pos.core.presentation.navigation.NavigationAction
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import java.io.Serializable

sealed class MenuDests : NavigationAction {
    data object MenuList : MenuDests() {
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
    
    data object MenuDetail : MenuDests() {
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
    }
    
    data object ProductDetail : MenuDests() {
        const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"
        const val ARG_VENUE_ID = "ARG_VENUE_ID"
        
        // Base route without arguments
        private const val BASE_ROUTE = "productDetail"
        
        override val route: String
            get() = "$BASE_ROUTE?$ARG_PRODUCT_ID={$ARG_PRODUCT_ID}&$ARG_VENUE_ID={$ARG_VENUE_ID}"
            
        // Helper function to create the route with specific values
        fun createRoute(productId: String, venueId: String): String {
            return "$BASE_ROUTE?$ARG_PRODUCT_ID=$productId&$ARG_VENUE_ID=$venueId"
        }
            
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ARG_PRODUCT_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(ARG_VENUE_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
            
        // This is a bottom sheet destination
        val isBottomSheet: Boolean = true
            
        override val navAnimation: NavAnimation?
            get() = NavAnimation.fade()
    }
}
