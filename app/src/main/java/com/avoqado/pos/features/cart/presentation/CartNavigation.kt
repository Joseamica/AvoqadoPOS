package com.avoqado.pos.features.cart.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Navigation route for cart screen
const val CART_ROUTE = "cart"

/**
 * Adds cart-related destinations to NavGraph
 */
fun NavGraphBuilder.cartGraph(
    navController: NavController,
    onCheckoutClick: () -> Unit
) {
    composable(CART_ROUTE) {
        val cartViewModel = CartViewModel()
        CartScreen(
            viewModel = cartViewModel,
            onBackClick = { navController.navigateUp() },
            onCheckoutClick = onCheckoutClick
        )
    }
}

/**
 * Extension function to navigate to cart screen
 */
fun NavController.navigateToCart() {
    this.navigate(CART_ROUTE)
}
