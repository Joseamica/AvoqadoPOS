package com.avoqado.pos.features.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Cart badge component showing number of items in cart
 * Can be placed in app bars or other navigation components
 */
@Composable
fun CartBadge(
    viewModel: CartViewModel,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cart by viewModel.cart.collectAsState()
    val itemCount = cart.getTotalItemCount()
    
    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable { onCartClick() },
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = itemCount.toString(),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Shopping Cart",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Floating cart button that appears when items are in cart
 */
@Composable
fun FloatingCartButton(
    viewModel: CartViewModel,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cart by viewModel.cart.collectAsState()
    val itemCount = cart.getTotalItemCount()
    
    if (itemCount > 0) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { onCartClick() },
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = itemCount.toString(),
                            fontSize = 12.sp
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "View Cart",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
