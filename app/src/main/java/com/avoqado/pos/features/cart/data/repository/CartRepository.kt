package com.avoqado.pos.features.cart.data.repository

import com.avoqado.pos.features.cart.domain.models.Cart
import com.avoqado.pos.features.cart.domain.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repository to manage the shopping cart
 */
class CartRepository private constructor() {
    
    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()
    
    companion object {
        @Volatile
        private var INSTANCE: CartRepository? = null
        
        fun getInstance(): CartRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CartRepository()
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Add an item to the cart. If an identical item exists,
     * increase the quantity instead of adding a new item
     */
    fun addItem(item: CartItem) {
        _cart.update { currentCart ->
            // Find if there's a matching item (same product and modifiers)
            val existingItemIndex = currentCart.items.indexOfFirst { 
                it.product.id == item.product.id && 
                it.selectedModifiers == item.selectedModifiers 
            }
            
            if (existingItemIndex >= 0) {
                // Update existing item quantity
                val updatedItems = currentCart.items.toMutableList()
                val existingItem = updatedItems[existingItemIndex]
                updatedItems[existingItemIndex] = existingItem.copy(
                    quantity = existingItem.quantity + item.quantity
                )
                currentCart.copy(items = updatedItems)
            } else {
                // Add new item
                currentCart.copy(items = currentCart.items + item)
            }
        }
    }
    
    /**
     * Update an item's quantity in the cart
     */
    fun updateItemQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(itemId)
            return
        }
        
        _cart.update { currentCart ->
            val updatedItems = currentCart.items.map { 
                if (it.id == itemId) it.copy(quantity = quantity) else it 
            }
            currentCart.copy(items = updatedItems)
        }
    }
    
    /**
     * Remove an item from the cart
     */
    fun removeItem(itemId: String) {
        _cart.update { currentCart ->
            val updatedItems = currentCart.items.filter { it.id != itemId }
            currentCart.copy(items = updatedItems)
        }
    }
    
    /**
     * Clear the entire cart
     */
    fun clearCart() {
        _cart.update { Cart() }
    }
    
    /**
     * Set the venue ID for this cart
     */
    fun setVenueId(venueId: String) {
        _cart.update { it.copy(venueId = venueId) }
    }
}
