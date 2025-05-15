package com.avoqado.pos.features.menu.domain.repository

import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.ModifierGroup

interface MenuRepository {
    /**
     * Gets available menus for a venue
     * @param venueId The venue ID
     * @return List of AvoqadoMenu objects
     */
    suspend fun getAvoqadoMenus(venueId: String): List<AvoqadoMenu>
    
    /**
     * Gets modifiers for a specific product
     * @param venueId The venue ID
     * @param productId The product ID
     * @return List of ModifierGroup objects
     */
    suspend fun getProductModifiers(venueId: String, productId: String): List<ModifierGroup>
    
    /**
     * Sets the current menu to be accessed later
     * @param menu The AvoqadoMenu to set as current
     */
    fun setCurrentMenu(menu: AvoqadoMenu?)
    
    /**
     * Gets the current menu that was previously set
     * @return The current AvoqadoMenu or null if none is set
     */
    fun getCurrentMenu(): AvoqadoMenu?
}
