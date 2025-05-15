package com.avoqado.pos.features.menu.presentation.menulist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import com.avoqado.pos.features.menu.presentation.navigation.MenuDests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the menu list screen
 */
class MenuListViewModel(
    private val menuRepository: MenuRepository,
    private val navigationDispatcher: NavigationDispatcher,
    private val venueId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MenuListUiState())
    val uiState: StateFlow<MenuListUiState> = _uiState.asStateFlow()
    
    init {
        fetchMenus()
    }
    
    /**
     * Fetch menus from the repository
     */
    fun fetchMenus() {
        viewModelScope.launch {
            Timber.d("Starting to fetch menus for venueId: $venueId")
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val menus = menuRepository.getAvoqadoMenus(venueId)
                Timber.d("Received ${menus.size} menus from repository")
                
                if (menus.isEmpty()) {
                    Timber.w("No menus returned from repository!")
                } else {
                    // Log details about each menu
                    menus.forEachIndexed { index, menu ->
                        Timber.d("Menu $index: ${menu.name}, id: ${menu.id}, categories: ${menu.categories.size}")
                    }
                }
                
                _uiState.update { prevState ->
                    val newState = prevState.copy(
                        isLoading = false,
                        menus = menus,
                        error = if (menus.isEmpty()) "No hay menús disponibles" else null
                    )
                    
                    Timber.d("Updated UI state: isLoading=${newState.isLoading}, menus.size=${newState.menus.size}, error=${newState.error}")
                    newState
                }
                
                // Log current UI state to verify update happened
                val currentState = _uiState.value
                Timber.d("Current UI state after update: isLoading=${currentState.isLoading}, menus.size=${currentState.menus.size}, error=${currentState.error}")
                
            } catch (e: Exception) {
                Timber.e(e, "Error fetching menus: ${e.message}")
                e.printStackTrace()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar los menús"
                    )
                }
            }
        }
    }
    
    /**
     * Navigate to menu detail
     */
    fun navigateToMenuDetail(menuId: String) {
        // Find the menu by ID and pass it to the detail screen
        viewModelScope.launch {
            val menu = _uiState.value.menus.find { it.id == menuId }
            if (menu != null) {
                navigationDispatcher.navigateWithArgs(
                    MenuDests.MenuDetail,
                    com.avoqado.pos.core.presentation.navigation.NavigationArg.StringArg(
                        MenuDests.MenuDetail.ARG_MENU_ID,
                        menuId
                    )
                )
            }
        }
    }
    
    /**
     * Navigate back
     */
    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }
}

/**
 * UI state for menu list
 */
data class MenuListUiState(
    val isLoading: Boolean = false,
    val menus: List<AvoqadoMenu> = emptyList(),
    val error: String? = null
)
