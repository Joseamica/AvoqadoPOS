package com.avoqado.pos.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.avoqado.pos.features.cart.data.repository.CartRepository
import com.avoqado.pos.features.cart.domain.models.CartItem
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(
    private val cartRepository: CartRepository = CartRepository.getInstance()
) : ViewModel() {
    
    /**
     * Factory for creating CartViewModel instances
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                return CartViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    
    // Expose cart state from repository
    val cart = cartRepository.cart
    
    // Convenience accessor for cart items
    val items: List<CartItem>
        get() = cart.value.items
        
    // Calculate total cart price
    val totalPrice: Double
        get() = cart.value.calculateTotal()
        
    // Get total number of items
    val itemCount: Int
        get() = cart.value.getTotalItemCount()
    
    // Forward repository operations
    fun addItem(item: CartItem) {
        cartRepository.addItem(item)
    }
    
    fun updateItemQuantity(itemId: String, quantity: Int) {
        cartRepository.updateItemQuantity(itemId, quantity)
    }
    
    fun removeItem(itemId: String) {
        cartRepository.removeItem(itemId)
    }
    
    fun clearCart() {
        cartRepository.clearCart()
    }
    
    // Check if cart is empty
    fun isCartEmpty(): Boolean {
        return cart.value.isEmpty()
    }
    
    // Proceed to checkout flow
    fun proceedToCheckout(): Boolean {
        // Validate cart is not empty
        if (isCartEmpty()) {
            return false
        }
        
        // At this point we would navigate to the payment screen
        // which you mentioned is already implemented
        return true
    }
}
