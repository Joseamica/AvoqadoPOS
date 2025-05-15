package com.avoqado.pos.features.menu.data.mappers

import com.avoqado.pos.features.menu.data.network.models.NetworkAvoqadoMenu
import com.avoqado.pos.features.menu.data.network.models.NetworkAvoqadoProduct
import com.avoqado.pos.features.menu.data.network.models.NetworkLanguage
import com.avoqado.pos.features.menu.data.network.models.NetworkMenuCategory
import com.avoqado.pos.features.menu.data.network.models.NetworkModifierGroup
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.Language
import com.avoqado.pos.features.menu.domain.models.MenuCategory
import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import timber.log.Timber

/**
 * Mapper functions to convert network models to domain models
 */
object AvoqadoMenuMapper {
    
    fun mapToDomain(networkMenu: NetworkAvoqadoMenu): AvoqadoMenu {
        // Ensure all types match exactly with the domain model
        val id: String = networkMenu.id
        val name: String = networkMenu.name
        val description: String? = networkMenu.description
        val image: String? = networkMenu.image
        
        // Use explicit getter for venueId to avoid unresolved reference
        val venueIdValue = try {
            networkMenu.venueId
        } catch (e: Exception) {
            Timber.e("Error accessing venueId: ${e.message}")
            null
        }
        val venueId: String = venueIdValue ?: "" // Default to empty string if null
        
        val isActive: Boolean = networkMenu.isActive
        val isFixed: Boolean = networkMenu.isFixed ?: true // Default to true if null
        val startTime: String? = networkMenu.startTime
        val endTime: String? = networkMenu.endTime
        val orderByNumber: Int = networkMenu.orderByNumber ?: 0 // Default to 0 if null
        
        // Handle language separately with explicit null check
        val languageValue = try {
            networkMenu.language
        } catch (e: Exception) {
            Timber.e("Error accessing language: ${e.message}")
            null
        }
        
        val language: Language? = languageValue?.let { mapLanguageToDomain(it) }
        
        // Map categories with explicit type declaration
        val categories: List<MenuCategory> = networkMenu.categories.map { mapCategoryToDomain(it) }
        
        return AvoqadoMenu(
            id = id,
            name = name,
            description = description,
            image = image,
            venueId = venueId,
            isActive = isActive,
            language = language,
            isFixed = isFixed,
            startTime = startTime,
            endTime = endTime,
            orderByNumber = orderByNumber,
            categories = categories
        )
    }
    
    private fun mapLanguageToDomain(networkLanguage: NetworkLanguage): Language {
        // Explicit type declarations to ensure type safety
        val id: String = networkLanguage.id
        val name: String = networkLanguage.name
        val code: String = networkLanguage.code
        val venueId: String = networkLanguage.venueId
        
        return Language(
            id = id,
            name = name,
            code = code,
            venueId = venueId
        )
    }
    
    private fun mapCategoryToDomain(networkCategory: NetworkMenuCategory): MenuCategory {
        // Explicit type declarations to ensure type safety
        val id: String = networkCategory.id
        val name: String = networkCategory.name
        val description: String? = networkCategory.description
        val image: String? = networkCategory.image
        val venueId: String = networkCategory.venueId ?: "" // Default to empty string if null
        val isActive: Boolean = networkCategory.isActive
        val orderByNumber: Int = networkCategory.orderByNumber ?: 0 // Default to 0 if null
        
        // Map products with explicit type declaration
        val avoqadoProducts: List<AvoqadoProduct> = networkCategory.avoqadoProducts.map { mapProductToDomain(it) }
        
        return MenuCategory(
            id = id,
            name = name,
            description = description,
            image = image,
            venueId = venueId,
            isActive = isActive,
            orderByNumber = orderByNumber,
            avoqadoProducts = avoqadoProducts
        )
    }
    
    private fun mapProductToDomain(networkProduct: NetworkAvoqadoProduct): AvoqadoProduct {
        // Log full product data for debugging
        Timber.d("Mapping NetworkAvoqadoProduct: id=${networkProduct.id}, name=${networkProduct.name}")
        Timber.d("Price string: '${networkProduct.priceString}', calculated price=${networkProduct.price}")
        
        // Explicit type declarations to ensure type safety
        val id: String = networkProduct.id
        val name: String = networkProduct.name
        val description: String? = networkProduct.description
        val image: String? = networkProduct.image
        
        // Price handling with safety checks
        val price: Double = try {
            networkProduct.price
        } catch (e: Exception) {
            Timber.e("Error parsing price: ${e.message}, priceString='${networkProduct.priceString}'")
            0.0 // Default price if parsing fails
        }
        
        val venueId: String = networkProduct.venueId ?: "" // Default to empty string if null
        val isActive: Boolean = networkProduct.isActive
        val orderByNumber: Int = networkProduct.getEffectiveOrderByNumber() // Use the helper function
        val categoryId: String = networkProduct.categoryId ?: "" // Default to empty string if null
        
        // Detailed modifier groups logging
        if (networkProduct.modifierGroups == null) {
            Timber.d("Product ${networkProduct.id} has null modifierGroups")
        } else {
            Timber.d("Product ${networkProduct.id} has ${networkProduct.modifierGroups.size} modifier groups")
            networkProduct.modifierGroups.forEachIndexed { index, group ->
                Timber.d("  Group $index: id=${group.id}, name=${group.name}, modifiers=${group.modifiers.size}")
            }
        }
        
        // Map modifier groups with explicit type declaration for safety
        val modifierGroups: List<ModifierGroup> = try {
            if (networkProduct.modifierGroups != null) {
                val mappedGroups = mutableListOf<ModifierGroup>()
                for (modifierGroup in networkProduct.modifierGroups) {
                    try {
                        val domainGroup = modifierGroup.toDomainModel()
                        mappedGroups.add(domainGroup)
                        Timber.d("Successfully mapped modifier group: ${modifierGroup.id} (${modifierGroup.name})")
                    } catch (e: Exception) {
                        Timber.e("Failed to map modifier group ${modifierGroup.id}: ${e.message}")
                        Timber.e(e.stackTraceToString())
                    }
                }
                mappedGroups
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e("Error mapping modifier groups for product ${networkProduct.id}: ${e.message}")
            Timber.e(e.stackTraceToString())
            emptyList()
        }
        
        return AvoqadoProduct(
            id = id,
            name = name,
            description = description,
            image = image,
            price = price,
            venueId = venueId,
            isActive = isActive,
            orderByNumber = orderByNumber,
            categoryId = categoryId,
            modifierGroups = modifierGroups
        )
    }
}
