package com.avoqado.pos.features.menu.presentation.menudetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.MenuCategory
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import com.avoqado.pos.features.menu.presentation.navigation.MenuDests
import com.avoqado.pos.features.cart.presentation.CART_ROUTE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class MenuDetailViewModel(
    private val menuId: String,
    private val menuRepository: MenuRepository,
    private val navigationDispatcher: NavigationDispatcher,
    private val venueId: String = AvoqadoApp.sessionManager.getVenueId() ?: ""
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MenuDetailUiState())
    val uiState: StateFlow<MenuDetailUiState> = _uiState.asStateFlow()
    
    // Store the current menu
    private var currentMenu: AvoqadoMenu? = null
    
    // Store the currently selected category
    private val _selectedCategory = MutableStateFlow<MenuCategory?>(null)
    val selectedCategory: StateFlow<MenuCategory?> = _selectedCategory.asStateFlow()
    
    init {
        loadMenu()
    }
    
    /**
     * Load the menu from the repository based on the menu ID
     */
    private fun loadMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Fetch all menus and find the one with the matching ID
                val menus = menuRepository.getAvoqadoMenus(venueId = venueId)
                val menu = menus.find { it.id == menuId }
                
                if (menu != null) {
                    currentMenu = menu
                    
                    // Set the current menu in the repository for use in product detail
                    if (menuRepository is com.avoqado.pos.features.menu.data.repository.MenuRepositoryImpl) {
                        menuRepository.setCurrentMenu(menu)
                    }
                    
                    // If the menu has categories, select the first one by default
                    if (menu.categories.isNotEmpty()) {
                        _selectedCategory.value = menu.categories.first()
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            menu = menu,
                            categories = menu.categories,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Menú no encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading menu details")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar el menú"
                    )
                }
            }
        }
    }
    
    /**
     * Select a category
     */
    fun selectCategory(category: MenuCategory) {
        _selectedCategory.value = category
    }
    
    /**
     * Navigate back
     */
    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }
    
    /**
     * Navigate to product detail
     */
    fun navigateToProductDetail(product: AvoqadoProduct) {
        // Set the current product in the repository so it can be accessed in the product detail screen
        menuRepository.setCurrentMenu(currentMenu)
        
        // Navigate using the updated ProductDetail route with arguments
        navigationDispatcher.navigateTo(
            route = MenuDests.ProductDetail.createRoute(product.id, venueId)
        )
    }
    
    /**
     * Navigate to shopping cart
     */
    fun navigateToCart() {
        navigationDispatcher.navigateTo(CART_ROUTE)
    }
}

/**
 * UI state for menu detail
 */
data class MenuDetailUiState(
    val isLoading: Boolean = false,
    val menu: AvoqadoMenu? = null,
    val categories: List<MenuCategory> = emptyList(),
    val error: String? = null
)
