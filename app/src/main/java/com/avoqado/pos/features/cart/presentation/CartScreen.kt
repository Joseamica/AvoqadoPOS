package com.avoqado.pos.features.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.features.cart.domain.models.CartItem
import java.text.NumberFormat
import java.util.*

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val cart by viewModel.cart.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Cart header
        CartHeader(
            itemCount = cart.getTotalItemCount(),
            onBackClick = onBackClick
        )
        
        if (cart.isEmpty()) {
            // Empty cart state
            EmptyCartView()
        } else {
            // Cart items
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(cart.items, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityIncrease = { viewModel.updateItemQuantity(item.id, item.quantity + 1) },
                        onQuantityDecrease = { viewModel.updateItemQuantity(item.id, item.quantity - 1) },
                        onRemove = { viewModel.removeItem(item.id) }
                    )
                }
            }
            
            // Price summary and checkout button
            CartSummary(
                subtotal = cart.calculateSubtotal(),
                tax = cart.calculateTax(),
                total = cart.calculateTotal(),
                onCheckoutClick = onCheckoutClick
            )
        }
    }
}

@Composable
fun CartHeader(
    itemCount: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = "Atrás",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        
        Text(
            text = "Carrito",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "$itemCount ${if (itemCount == 1) "producto" else "productos"}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
    
    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
}

@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Agrega algunos productos a tu carrito para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product name and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (item.selectedModifiers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.getFormattedModifiers(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    if (item.notes != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Note: ${item.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                val priceFormatter = NumberFormat.getCurrencyInstance().apply {
                    currency = Currency.getInstance("MXN")
                }
                Text(
                    text = priceFormatter.format(item.calculateTotalPrice()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quantity controls and remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        onClick = onQuantityDecrease,
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (item.quantity > 1) Color.Black else Color.Gray
                        )
                    }
                    
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = onQuantityIncrease,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                // Remove button
                TextButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    subtotal: Double,
    tax: Double,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    val priceFormatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance("MXN")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Price breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = priceFormatter.format(subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IVA (16%)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = priceFormatter.format(tax),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = priceFormatter.format(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Get current context for payment connector
            val localContext = LocalContext.current
            
            // Checkout button
            Button(

                onClick = { 
                    // Start the payment process using our connector
                    CartPaymentConnector.startPaymentFromCart(localContext)
                    // Also call the provided onCheckoutClick for navigation purposes
                    onCheckoutClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row{
                    Text(
                        text = "Pagar",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }
        }
    }
}
