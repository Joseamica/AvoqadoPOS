package com.avoqado.pos.features.menu.presentation.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import com.avoqado.pos.features.menu.domain.models.ProductModifier
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductDetailContent(
    isLoading: Boolean = false,
    product: AvoqadoProduct? = null,
    modifierGroups: List<ModifierGroup> = emptyList(),
    selectedModifiers: Map<String, List<ProductModifier>> = emptyMap(),
    totalPrice: Double = 0.0,
    quantity: Int = 1,
    error: String? = null,
    onModifierToggle: (ModifierGroup, ProductModifier) -> Unit = { _, _ -> },
    onQuantityChange: (Int) -> Unit = {},
    onAddToOrder: () -> Unit = {},
    onDismiss: () -> Unit = {},
    isModifierSelected: (ModifierGroup, ProductModifier) -> Boolean = { _, _ -> false },
    areRequiredSelectionsComplete: () -> Boolean = { true }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(1f)
            .clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = ModalBottomSheetDefaults.Elevation
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Handle loading, error and missing product states
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                }
                product == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se pudo cargar el producto",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    // Main content column with product details
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 70.dp) // Space for the add button
                    ) {
                        // Header with title and close button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product?.name ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            // Close icon
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                                    .clickable { onDismiss() }
                            )
                        }

                        // Product details
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            // Product name and price
                            // Text(
                            //     text = product?.name ?: "",
                            //     style = MaterialTheme.typography.titleLarge.copy(
                            //         fontWeight = FontWeight.Medium
                            //     ),
                            //     maxLines = 2,
                            //     overflow = TextOverflow.Ellipsis
                            // )
                            
                            // Spacer(modifier = Modifier.height(4.dp))
                            
                            val priceFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                            Text(
                                text = priceFormat.format(product.price).replace("MXN", ""),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Product description (if available)
                            if (!product.description.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            // Quantity selector
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cantidad",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFF5F5F5),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .height(32.dp)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable(enabled = quantity > 1) { 
                                                if (quantity > 1) onQuantityChange(quantity - 1) 
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "−",  // Unicode minus sign
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (quantity > 1) Color.Black else Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    
                                    Text(
                                        text = quantity.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.width(28.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { onQuantityChange(quantity + 1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                        // Modifier groups section
                        if (modifierGroups.isNotEmpty()) {
                            modifierGroups.forEach { modifierGroup ->
                                ModifierGroupCard(
                                    modifierGroup = modifierGroup,
                                    onModifierToggle = onModifierToggle,
                                    isModifierSelected = isModifierSelected
                                )
                            }
                        } else {
                            // No modifiers message
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin opciones de personalización",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        // Bottom space to prevent content from being hidden by the add button
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Add to cart button fixed at the bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = onAddToOrder,
                            enabled = areRequiredSelectionsComplete(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                disabledContainerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val priceFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                            Text(
                                text = "Agregar ${priceFormat.format(totalPrice)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModifierGroupCard(
    modifierGroup: ModifierGroup,
    onModifierToggle: (ModifierGroup, ProductModifier) -> Unit,
    isModifierSelected: (ModifierGroup, ProductModifier) -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Group header with title and selection info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = modifierGroup.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = if (modifierGroup.minSelection == modifierGroup.maxSelection && modifierGroup.minSelection == 1) {
                        "Selecciona una opción"
                    } else {
                        "Selecciona ${modifierGroup.minSelection}-${modifierGroup.maxSelection}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
            
            if (modifierGroup.required) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Requerido",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Modifier items in a more compact layout
        modifierGroup.modifiers.forEach { modifier ->
            val isSelected = isModifierSelected(modifierGroup, modifier)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModifierToggle(modifierGroup, modifier) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Standard Material Design selection indicators with proper colors
                if (modifierGroup.multipleSelection) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onModifierToggle(modifierGroup, modifier) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2E7D32),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                } else {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onModifierToggle(modifierGroup, modifier) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF2E7D32),
                            unselectedColor = Color.Gray
                        )
                    )
                }
                
                // Modifier name, description and price
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = modifier.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!modifier.description.isNullOrEmpty()) {
                        Text(
                            text = modifier.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    }
                }
                
                // Price tag if applicable
                if (modifier.price > 0) {
                    val priceFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    Text(
                        text = "+${priceFormat.format(modifier.price).replace("MXN", "")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (modifier != modifierGroup.modifiers.last()) {
                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.5f), 
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
    }
    
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

// Commenting out preview due to model parameters issue
// @Preview(showBackground = true)
// @Composable
// fun ProductDetailContentPreview() {
//     // TODO: Fix preview with all required parameters for AvoqadoProduct
// }
