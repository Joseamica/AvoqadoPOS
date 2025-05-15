package com.avoqado.pos.features.menu.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.cart.data.repository.CartRepository
import com.avoqado.pos.features.cart.domain.models.CartItem
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.ProductModifier
import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductDetailViewModel(
    private val product: AvoqadoProduct,
    private val menuRepository: MenuRepository,
    private val navigationDispatcher: NavigationDispatcher,
    private val cartRepository: CartRepository = CartRepository.getInstance(),
    private val venueId: String = AvoqadoApp.sessionManager.getVenueId() ?: ""
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    private val _selectedModifiers = MutableStateFlow<Map<String, List<ProductModifier>>>(emptyMap())
    val selectedModifiers: StateFlow<Map<String, List<ProductModifier>>> = _selectedModifiers.asStateFlow()
    
    private val _totalPrice = MutableStateFlow(0.0) // Initialize to 0 and set properly in init
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    
    private val _quantity = MutableStateFlow(1) // Default quantity is 1
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    init {
        // Log the product details for debugging
        Timber.d("==== PRODUCT DETAIL INITIALIZATION ====")
        Timber.d("Product ID: ${product.id}")
        Timber.d("Product Name: ${product.name}")
        Timber.d("Product Description: ${product.description}")
        Timber.d("Product Price: ${product.price}")
        Timber.d("Product VenueId: ${product.venueId}")
        Timber.d("Product isActive: ${product.isActive}")
        Timber.d("Product orderByNumber: ${product.orderByNumber}")
        Timber.d("Product categoryId: ${product.categoryId}")
        Timber.d("Product ModifierGroups Count: ${product.modifierGroups.size}")

        // Explicitly set initial price with try-catch for debugging
        try {
            val basePrice = product.price
            Timber.d("Setting initial price to $basePrice")
            _totalPrice.value = basePrice
            
            // Check if the price is valid
            if (basePrice <= 0.0) {
                Timber.w("WARNING: Initial product price is $basePrice which seems invalid!")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error setting initial price")
            // Set a fallback price for debugging
            _totalPrice.value = 10.0 
            Timber.d("Used fallback price: 10.0")
        }
        
        // Update UI state with complete product
        _uiState.update { it.copy(product = product) }
        
        // Double check the total price flow
        Timber.d("Initial totalPrice in StateFlow: ${_totalPrice.value}")
        
        loadModifiers()
    }
    
    /**
     * Load modifiers for the product
     */
    private fun loadModifiers() {
        // First show modifiers from the product model (may be incomplete)
        _uiState.update { 
            it.copy(
                isLoading = true,
                modifierGroups = product.modifierGroups
            )
        }
        
        // Initialize selected modifiers map
        val initialSelections = product.modifierGroups.associateWith { emptyList<ProductModifier>() }
            .mapKeys { it.key.id }
        _selectedModifiers.update { initialSelections }
        
        // Then fetch fresh modifiers from API
        viewModelScope.launch {
            try {
                Timber.d("Fetching modifiers from API for product: ${product.id}")
                val modifiers = menuRepository.getProductModifiers(venueId, product.id)
                
                if (modifiers.isNotEmpty()) {
                    Timber.d("Loaded ${modifiers.size} modifier groups from API")
                    modifiers.forEach { group ->
                        Timber.d("Group: ${group.name} with ${group.modifiers.size} modifiers")
                    }
                    
                    // Update UI with the fresh data
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            modifierGroups = modifiers
                        )
                    }
                    
                    // Update selected modifiers map with fresh data
                    val freshSelections = modifiers.associateWith { emptyList<ProductModifier>() }
                        .mapKeys { it.key.id }
                    _selectedModifiers.update { freshSelections }
                } else {
                    Timber.d("No modifiers found for product: ${product.id}")
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load modifiers from API")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error loading modifiers: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    /**
     * Toggle selection of a modifier
     */
    fun toggleModifier(modifierGroup: ModifierGroup, modifier: ProductModifier) {
        val groupId = modifierGroup.id
        val currentSelections = _selectedModifiers.value[groupId] ?: emptyList()
        
        val newSelections = if (modifierGroup.multipleSelection) {
            // For multiple selection groups
            if (currentSelections.any { it.id == modifier.id }) {
                // Remove if already selected
                currentSelections.filter { it.id != modifier.id }
            } else {
                // Add if not selected and within max selection limit
                if (currentSelections.size < modifierGroup.maxSelection) {
                    currentSelections + modifier
                } else {
                    currentSelections
                }
            }
        } else {
            // For single selection groups
            if (currentSelections.any { it.id == modifier.id }) {
                // Remove if already selected
                emptyList()
            } else {
                // Replace with new selection
                listOf(modifier)
            }
        }
        
        _selectedModifiers.update { current ->
            current.toMutableMap().apply {
                put(groupId, newSelections)
            }
        }
        
        // Recalculate total price
        calculateTotalPrice()
    }
    
    /**
     * Update the product quantity and recalculate total price
     */
    fun updateQuantity(newQuantity: Int) {
        if (newQuantity in 1..99) { // Reasonable limits for quantity
            _quantity.value = newQuantity
            calculateTotalPrice()
        }
    }
    
    /**
     * Calculate the total price based on selected modifiers and quantity
     */
    private fun calculateTotalPrice() {
        val basePrice = product.price
        var modifiersPrice = 0.0
        
        // Sum up the price of all selected modifiers
        _selectedModifiers.value.forEach { (_, modifiers) ->
            modifiersPrice += modifiers.sumOf { it.price }
        }
        
        // Multiple the total per-unit price by the quantity
        _totalPrice.value = (basePrice + modifiersPrice) * _quantity.value
        
        Timber.d("Total price updated: $basePrice (base) + $modifiersPrice (modifiers) × ${_quantity.value} (qty) = ${_totalPrice.value}")
    }
    
    /**
     * Check if a modifier is selected
     */
    fun isModifierSelected(modifierGroup: ModifierGroup, modifier: ProductModifier): Boolean {
        val selections = _selectedModifiers.value[modifierGroup.id] ?: emptyList()
        return selections.any { it.id == modifier.id }
    }
    
    /**
     * Add product with selected modifiers to order
     */
    fun addToOrder() {
        // Create a cart item from current selection
        val cartItem = CartItem(
            product = product,
            selectedModifiers = _selectedModifiers.value,
            quantity = _quantity.value
        )
        
        // Add to cart repository
        cartRepository.addItem(cartItem)
        
        // Set venue ID for the cart if not already set
        if (cartRepository.cart.value.venueId == null && venueId.isNotEmpty()) {
            cartRepository.setVenueId(venueId)
        }
        
        // Show a brief success message (would be handled by UI)
        Timber.d("Added to cart: ${product.name} x ${_quantity.value} - Total: ${_totalPrice.value}")
        
        // Close the bottom sheet
        dismissBottomSheet()
    }
    
    /**
     * Dismiss the bottom sheet
     */
    fun dismissBottomSheet() {
        navigationDispatcher.navigateBack()
    }
    
    /**
     * Check if all required modifier groups have selections
     */
    fun areRequiredSelectionsComplete(): Boolean {
        val requiredGroups = _uiState.value.modifierGroups.filter { it.required }
        
        return requiredGroups.all { group ->
            val selections = _selectedModifiers.value[group.id] ?: emptyList()
            selections.size >= group.minSelection
        }
    }
}

/**
 * UI state for product detail
 */
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: AvoqadoProduct? = null,
    val modifierGroups: List<ModifierGroup> = emptyList(),
    val error: String? = null
)
