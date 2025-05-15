package com.avoqado.pos.features.cart.domain.models

import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.ProductModifier
import java.util.UUID

/**
 * Represents an item in the shopping cart
 */
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: AvoqadoProduct,
    val selectedModifiers: Map<String, List<ProductModifier>> = emptyMap(),
    val quantity: Int = 1,
    val notes: String? = null
) {
    /**
     * Calculate the total price for this item including modifiers
     */
    fun calculateTotalPrice(): Double {
        val basePrice = product.price
        val modifiersPrice = selectedModifiers.values.flatten()
            .sumOf { it.price ?: 0.0 }
        
        return (basePrice + modifiersPrice) * quantity
    }
    
    /**
     * Get a formatted string of selected modifiers for display
     */
    fun getFormattedModifiers(): String {
        if (selectedModifiers.isEmpty()) return ""
        
        return selectedModifiers.values.flatten()
            .joinToString(", ") { it.name }
    }
}
