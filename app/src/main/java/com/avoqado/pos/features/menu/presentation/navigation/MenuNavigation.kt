package com.avoqado.pos.features.menu.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.router.bottomSheetHolder
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.menu.presentation.menudetail.MenuDetailScreen
import com.avoqado.pos.features.menu.presentation.menudetail.MenuDetailViewModel
import com.avoqado.pos.features.menu.presentation.menulist.MenuListScreen
import com.avoqado.pos.features.menu.presentation.menulist.MenuListViewModel
import com.avoqado.pos.features.menu.presentation.productdetail.ProductDetailSheet
import com.avoqado.pos.features.menu.presentation.productdetail.ProductDetailViewModel
import timber.log.Timber

fun NavGraphBuilder.menuNavigation(
    navigationDispatcher: NavigationDispatcher
) {
    // Menu List Screen
    composable(
        route = ManagementDests.MenuList.route,
        arguments = ManagementDests.MenuList.arguments,
        enterTransition = { ManagementDests.MenuList.navAnimation?.enterTransition?.invoke(this) },
        exitTransition = { ManagementDests.MenuList.navAnimation?.exitTransition?.invoke(this) },
        popEnterTransition = { ManagementDests.MenuList.navAnimation?.popEnterTransition?.invoke(this) },
        popExitTransition = { ManagementDests.MenuList.navAnimation?.popExitTransition?.invoke(this) }
    ) { backStackEntry ->
        val venueId = backStackEntry.arguments?.getString(ManagementDests.MenuList.ARG_VENUE_ID) ?: ""
        
        val viewModel = remember {
            MenuListViewModel(
                menuRepository = AvoqadoApp.menuRepository,
                navigationDispatcher = navigationDispatcher,
                venueId = venueId
            )
        }
        
        MenuListScreen(viewModel)
    }
    
    // Menu Detail Screen
    composable(
        route = MenuDests.MenuDetail.route,
        arguments = MenuDests.MenuDetail.arguments,
        enterTransition = { MenuDests.MenuDetail.navAnimation?.enterTransition?.invoke(this) },
        exitTransition = { MenuDests.MenuDetail.navAnimation?.exitTransition?.invoke(this) },
        popEnterTransition = { MenuDests.MenuDetail.navAnimation?.popEnterTransition?.invoke(this) },
        popExitTransition = { MenuDests.MenuDetail.navAnimation?.popExitTransition?.invoke(this) }
    ) { backStackEntry ->
        val menuId = backStackEntry.arguments?.getString(MenuDests.MenuDetail.ARG_MENU_ID) ?: ""
        
        val viewModel = remember {
            MenuDetailViewModel(
                menuRepository = AvoqadoApp.menuRepository,
                navigationDispatcher = navigationDispatcher,
                menuId = menuId
            )
        }
        
        MenuDetailScreen(viewModel)
    }
    
    // Product Detail Bottom Sheet
    dialog(
        route = MenuDests.ProductDetail.route,
        arguments = MenuDests.ProductDetail.arguments
    ) { navBackStackEntry ->
        // Extract arguments from the back stack entry
        val productId = navBackStackEntry.arguments?.getString(MenuDests.ProductDetail.ARG_PRODUCT_ID) ?: ""
        val venueId = navBackStackEntry.arguments?.getString(MenuDests.ProductDetail.ARG_VENUE_ID) ?: ""
        
        // Get the current menu from the repository
        val menuRepository = AvoqadoApp.menuRepository
        val currentMenu = menuRepository.getCurrentMenu()
        
        // Find the actual product by ID from the current menu
        val product = if (currentMenu != null) {
            // Search for the product in all categories
            currentMenu.categories
                .flatMap { category -> category.avoqadoProducts }
                .find { product -> product.id == productId }
        } else {
            null
        }
        
        // If product was found, use it. Otherwise create a placeholder and log an error.
        val finalProduct = product ?: run {
            Timber.e("Product not found: $productId. Using placeholder instead.")
            AvoqadoProduct(
                id = productId,
                name = "Product Not Found",
                description = "This product could not be loaded correctly",
                price = 0.0,  // Will be replaced by a fallback price in ViewModel
                image = null,
                categoryId = "",
                venueId = venueId,
                isActive = true,
                orderByNumber = 0
            )
        }
        
        Timber.d("Loading product: ${finalProduct.id}, name=${finalProduct.name}, price=${finalProduct.price}")
        
        val viewModel = remember {
            ProductDetailViewModel(
                product = finalProduct,
                menuRepository = menuRepository,
                navigationDispatcher = navigationDispatcher,
                venueId = venueId
            )
        }
        
        @OptIn(ExperimentalMaterial3Api::class)
        ProductDetailSheet(viewModel)
    }
}
