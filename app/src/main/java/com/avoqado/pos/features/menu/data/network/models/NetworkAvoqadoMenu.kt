package com.avoqado.pos.features.menu.data.network.models

import com.google.gson.annotations.SerializedName

data class AvoqadoMenuResponse(
    @SerializedName("avoqadoMenus")
    val avoqadoMenus: List<NetworkAvoqadoMenu>,
    
    @SerializedName("timestamp")
    val timestamp: Long
)

data class NetworkAvoqadoMenu(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("shortDesc")
    val shortDesc: String?,
    
    @SerializedName("longDesc")
    val description: String?,
    
    @SerializedName("imageCover")
    val image: String?,
    
    @SerializedName("active")
    val isActive: Boolean,
    
    @SerializedName("isFixed")
    val isFixed: Boolean?,
    
    @SerializedName("startTime")
    val startTime: String?,
    
    @SerializedName("endTime")
    val endTime: String?,
    
    @SerializedName("orderByNumber")
    val orderByNumber: Int?,
    
    @SerializedName("venueId")
    val venueId: String?,
    
    @SerializedName("languageId")
    val languageId: String?,
    
    @SerializedName("language")
    val language: NetworkLanguage?,
    
    @SerializedName("categories")
    val categories: List<NetworkMenuCategory>
)

data class NetworkLanguage(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("venueId")
    val venueId: String
)

data class NetworkMenuCategory(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("image")
    val image: String?,
    
    @SerializedName("displayBill")
    val displayBill: String?,
    
    @SerializedName("color")
    val color: String?,
    
    @SerializedName("pdf")
    val pdf: Boolean?,
    
    @SerializedName("venueId")
    val venueId: String?,
    
    @SerializedName("menuId")
    val menuId: String?,
    
    @SerializedName("active")
    val isActive: Boolean,
    
    @SerializedName("orderByNumber")
    val orderByNumber: Int?,
    
    @SerializedName("avoqadoProducts")
    val avoqadoProducts: List<NetworkAvoqadoProduct> = emptyList()
)

data class NetworkAvoqadoProduct(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("imageUrl")
    val image: String?,
    
    @SerializedName("price")
    val priceString: String,
    
    @SerializedName("sku")
    val sku: String?,
    
    @SerializedName("key")
    val key: String?,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("quantityUnit")
    val quantityUnit: String?,
    
    @SerializedName("instagramUrl")
    val instagramUrl: String?,
    
    @SerializedName("calories")
    val calories: String?,
    
    @SerializedName("venueId")
    val venueId: String?,
    
    @SerializedName("active")
    val isActive: Boolean = true,
    
    @SerializedName("orderByNumber")
    val orderByNumber: Int? = null,
    
    @SerializedName("sortOrder")
    val sortOrder: Int? = null,
    
    @SerializedName("categoryId")
    val categoryId: String? = null,
    
    @SerializedName("modifierGroups")
    val modifierGroups: List<NetworkModifierGroup>? = null
) {
    // Convert price string to Double with better error handling
    val price: Double
        get() {
            return try {
                // Try to handle various formats that might come from the API
                val cleaned = priceString.replace(",", ".").trim()
                cleaned.toDoubleOrNull() ?: 0.0
            } catch (e: Exception) {
                // Log the error and return 0.0 as fallback
                android.util.Log.e("NetworkAvoqadoProduct", "Error parsing price: $priceString", e)
                0.0
            }
        }
    
    // Use sortOrder if orderByNumber is null
    fun getEffectiveOrderByNumber(): Int {
        return orderByNumber ?: sortOrder ?: 0
    }
}
