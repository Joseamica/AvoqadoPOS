package com.avoqado.pos.features.menu.data.repository

import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.features.menu.data.mappers.AvoqadoMenuMapper
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.Language
import com.avoqado.pos.features.menu.domain.models.MenuCategory
import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import com.avoqado.pos.features.menu.domain.models.ProductModifier
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import timber.log.Timber

class MenuRepositoryImpl(
    private val avoqadoService: AvoqadoService
) : MenuRepository {
    
    // Store the current menu for use in the product detail screen
    private var currentMenu: AvoqadoMenu? = null

    /**
     * Gets available menus for a venue
     * @param venueId The venue ID
     * @return List of AvoqadoMenu objects
     */
    override suspend fun getAvoqadoMenus(venueId: String): List<AvoqadoMenu> {
        return try {
            // First, try to log the raw API response as a string to see exactly what's coming from the API
            val response = try {
                avoqadoService.getAvoqadoMenus(venueId).also { response ->
                    // Log raw menu count
                    Timber.d("======= API RESPONSE DEBUG =======")
                    Timber.d("Got raw response with ${response.avoqadoMenus.size} menus")
                    response.avoqadoMenus.forEachIndexed { index, menu ->
                        Timber.d("Raw menu $index: ${menu.name}, id=${menu.id}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception during API call: ${e.message}")
                throw e
            }
            
            // Detailed logging for debugging
            Timber.d("Processing ${response.avoqadoMenus.size} menus from API")
            
            // Try to map to domain models with explicit try/catch for each menu
            val domainMenus = response.avoqadoMenus.mapNotNull { networkMenu ->
                try {
                    // Debug the raw data
                    Timber.d("Attempting to map menu ${networkMenu.name} with fields:")
                    Timber.d("  id=${networkMenu.id}, longDesc=${networkMenu.description}, imageCover=${networkMenu.image}")
                    Timber.d("  orderByNumber=${networkMenu.orderByNumber}, isFixed=${networkMenu.isFixed}, categories.size=${networkMenu.categories.size}")
                    
                    val domainMenu = AvoqadoMenuMapper.mapToDomain(networkMenu)
                    Timber.d("Successfully mapped menu: ${domainMenu.name}, id=${domainMenu.id}")
                    domainMenu
                } catch (e: Exception) {
                    Timber.e(e, "Failed to map menu ${networkMenu.name}: ${e.message}")
                    e.printStackTrace() // Print full stack trace
                    Timber.e("Stack trace for mapping error: ${e.stackTraceToString()}")
                    null // Skip this menu if mapping fails
                }
            }
            
            Timber.d("Successfully mapped ${domainMenus.size} out of ${response.avoqadoMenus.size} menus")
            
            // Return the successfully mapped menus
            if (domainMenus.isEmpty() && response.avoqadoMenus.isNotEmpty()) {
                Timber.w("WARNING: All menus failed to map despite having ${response.avoqadoMenus.size} in the API response!")
            }
            
            // Use a hardcoded test menu if no menus could be mapped (for debugging)
            if (domainMenus.isEmpty()) {
                Timber.w("Creating a test menu for debugging")
                val testMenu = createTestMenu()
                Timber.d("Created test menu: ${testMenu.name}")
                listOf(testMenu)
            } else {
                domainMenus
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching avoqado menus for venueId: $venueId")
            e.printStackTrace() // Print full stack trace for debugging
            emptyList()
        }
    }
    
    /**
     * Creates a test menu for debugging purposes
     */
    private fun createTestMenu(): AvoqadoMenu {
        val testProduct = AvoqadoProduct(
            id = "test-product-id",
            name = "Test Product",
            description = "A test product for debugging",
            image = null,
            price = 10.0,
            venueId = "test-venue",
            isActive = true,
            orderByNumber = 1,
            categoryId = "test-category-id",
            modifierGroups = emptyList<ModifierGroup>()
        )
        
        val testCategory = MenuCategory(
            id = "test-category-id",
            name = "Test Category",
            description = "Test category for debugging",
            image = null,
            venueId = "test-venue",
            isActive = true,
            orderByNumber = 1,
            avoqadoProducts = listOf<AvoqadoProduct>(testProduct)
        )
        
        return AvoqadoMenu(
            id = "test-menu-id",
            name = "Test Menu (Debug)",
            description = "This is a test menu created for debugging",
            image = null,
            venueId = "test-venue",
            isActive = true,
            language = null,
            isFixed = true,
            startTime = null,
            endTime = null,
            orderByNumber = 1,
            categories = listOf<MenuCategory>(testCategory)
        )
    }
    
    /**
     * Gets modifiers for a specific product
     * @param venueId The venue ID
     * @param productId The product ID
     * @return List of ModifierGroup objects
     */
    override suspend fun getProductModifiers(venueId: String, productId: String): List<ModifierGroup> {
        Timber.d("===== FETCHING MODIFIERS =====")
        Timber.d("Requesting modifiers for product: $productId, venue: $venueId")
        
        return try {
            // Make API call with detailed logging
            val response = try {
                Timber.d("Making API call to get modifiers")
                avoqadoService.getProductModifiers(venueId, productId).also { response ->
                    Timber.d("API response received with ${response.modifierGroups.size} modifier groups")
                    response.modifierGroups.forEachIndexed { index, group ->
                        Timber.d("Modifier group $index: ${group.name} with ${group.modifiers.size} modifiers")
                        group.modifiers.forEachIndexed { modIndex, modifier ->
                            Timber.d("  Modifier $modIndex: ${modifier.name}, price=${modifier.priceString}")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during API call: ${e.message}")
                throw e
            }
            
            // Map to domain models
            try {
                val domainModels = response.modifierGroups.map { it.toDomainModel() }
                Timber.d("Successfully mapped ${domainModels.size} modifier groups to domain models")
                domainModels
            } catch (e: Exception) {
                Timber.e(e, "Error mapping modifier groups to domain models: ${e.message}")
                throw e
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching modifiers for product: $productId in venue: $venueId")
            Timber.e("Stack trace: ${e.stackTraceToString()}")
            
            // Return empty list but add detailed logging about the error
            Timber.d("Creating test modifier group for debugging since API failed")
            val testModifier = ProductModifier(
                id = "test-modifier-id",
                name = "Test Modifier",
                description = "This is a test modifier",
                price = 5.0,
                venueId = venueId,
                isActive = true,
                modifierGroupId = "test-group-id"
            )
            
            val testGroup = ModifierGroup(
                id = "test-group-id",
                name = "Test Group",
                description = "This is a test group",
                required = true,
                multipleSelection = false,
                minSelection = 1,
                maxSelection = 1,
                venueId = venueId,
                isActive = true,
                modifiers = listOf(testModifier)
            )
            
            // Always return test data when there's an API error to help with debugging
            return listOf(testGroup)
        }
    }
    
    /**
     * Sets the current menu to be accessed later
     * @param menu The AvoqadoMenu to set as current
     */
    override fun setCurrentMenu(menu: AvoqadoMenu?) {
        currentMenu = menu
    }
    
    /**
     * Gets the current menu that was previously set
     * @return The current AvoqadoMenu or null if none is set
     */
    override fun getCurrentMenu(): AvoqadoMenu? {
        return currentMenu
    }
}
