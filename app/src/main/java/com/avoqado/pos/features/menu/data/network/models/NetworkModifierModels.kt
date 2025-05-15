package com.avoqado.pos.features.menu.data.network.models

import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import com.avoqado.pos.features.menu.domain.models.ProductModifier
import com.google.gson.annotations.SerializedName
import timber.log.Timber

data class NetworkModifierGroupResponse(
    @SerializedName("data")
    val data: ResponseData? = null
) {
    // Provide a safe access to modifierGroups
    val modifierGroups: List<NetworkModifierGroup>
        get() = data?.modifierGroups ?: emptyList()
}

data class ResponseData(
    @SerializedName("modifierGroups")
    val modifierGroups: List<NetworkModifierGroup> = emptyList()
)

data class NetworkModifierGroup(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("required")
    val required: Boolean = false,
    @SerializedName("min")
    val minSelection: Int = 0,
    @SerializedName("max")
    val maxSelection: Int = 0,
    @SerializedName("multiMax")
    val multiMax: Int = 0,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("active")
    val isActive: Boolean,
    @SerializedName("modifiers")
    val modifiers: List<NetworkModifier>
) {
    // Set multipleSelection based on max value
    val multipleSelection: Boolean
        get() = multiMax > 1 || maxSelection > 1
    
    fun toDomainModel(): ModifierGroup {
        return ModifierGroup(
            id = id,
            name = name,
            description = description,
            required = required,
            multipleSelection = multipleSelection,
            minSelection = minSelection,
            maxSelection = maxSelection,
            venueId = venueId,
            isActive = isActive,
            modifiers = modifiers.map { it.toDomainModel() }
        )
    }
}

data class NetworkModifier(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("extraPrice")
    val priceString: String = "0",
    // For API compatibility - some endpoints might use price instead of extraPrice
    @SerializedName("price")
    val altPriceString: String? = null,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("active")
    val isActive: Boolean = true,
    @SerializedName("modifierGroupId")
    val modifierGroupId: String = ""
) {
    // Convert price string to Double with better error handling
    val price: Double
        get() {
            return try {
                // Try both price fields and handle various formats
                val priceToUse = altPriceString ?: priceString
                val cleaned = priceToUse.replace(",", ".").trim()
                cleaned.toDoubleOrNull() ?: 0.0
            } catch (e: Exception) {
                // Return 0.0 as fallback
                Timber.e("Error parsing price from strings: $priceString, $altPriceString", e)
                0.0
            }
        }
        
    fun toDomainModel(): ProductModifier {
        return ProductModifier(
            id = id,
            name = name,
            description = description,
            price = price,
            venueId = venueId,
            isActive = isActive,
            modifierGroupId = modifierGroupId
        )
    }
}
