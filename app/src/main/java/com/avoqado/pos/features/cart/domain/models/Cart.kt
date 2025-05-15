package com.avoqado.pos.features.cart.domain.models

/**
 * Represents the shopping cart containing all selected items
 */
data class Cart(
    val items: List<CartItem> = emptyList(),
    val venueId: String? = null
) {
    /**
     * Calculate the subtotal (before tax)
     */
    fun calculateSubtotal(): Double {
        return items.sumOf { it.calculateTotalPrice() }
    }
    
    /**
     * Calculate tax amount based on subtotal
     * Using default tax rate of 16% (0.16)
     */
    fun calculateTax(): Double {
        return calculateSubtotal() * 0.16 // 16% tax rate
    }
    
    /**
     * Calculate the total (including tax)
     */
    fun calculateTotal(): Double {
        return calculateSubtotal() + calculateTax()
    }
    
    /**
     * Get total number of items in cart (considering quantities)
     */
    fun getTotalItemCount(): Int {
        return items.sumOf { it.quantity }
    }
    
    /**
     * Check if cart is empty
     */
    fun isEmpty(): Boolean {
        return items.isEmpty()
    }
}
