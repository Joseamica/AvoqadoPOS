package com.avoqado.pos.features.cart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.core.presentation.router.composableHolder
import com.avoqado.pos.features.cart.presentation.CartScreen
import com.avoqado.pos.features.cart.presentation.CartViewModel

/**
 * Adds cart destinations to the navigation graph
 * 
 * @param navController Main navigation controller
 * @param onCheckout Callback when checkout is selected
 */
fun NavGraphBuilder.cartGraph(
    navController: NavController,
    onCheckout: () -> Unit = {}
) {
    composableHolder(CartDests.Cart) { navBackStackEntry ->
        CartScreenComposable(
            navController = navController,
            onCheckout = onCheckout
        )
    }
}

@Composable
private fun CartScreenComposable(
    navController: NavController,
    onCheckout: () -> Unit
) {
    val cartViewModel = remember { CartViewModel() }
    
    CartScreen(
        viewModel = cartViewModel,
        onBackClick = { navController.popBackStack() },
        onCheckoutClick = onCheckout
    )
}
