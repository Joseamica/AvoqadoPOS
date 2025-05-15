package com.avoqado.pos.features.menu.domain.models

import java.io.Serializable

data class ModifierGroup(
    val id: String,
    val name: String,
    val description: String?,
    val required: Boolean,
    val multipleSelection: Boolean,
    val minSelection: Int,
    val maxSelection: Int,
    val venueId: String,
    val isActive: Boolean,
    val modifiers: List<ProductModifier> = emptyList()
) : Serializable

data class ProductModifier(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val venueId: String,
    val isActive: Boolean,
    val modifierGroupId: String,
    val isSelected: Boolean = false
) : Serializable
