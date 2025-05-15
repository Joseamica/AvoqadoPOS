package com.avoqado.pos.features.menu.domain.models

import java.io.Serializable

data class AvoqadoMenu(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val venueId: String,
    val isActive: Boolean,
    val language: Language?,
    val isFixed: Boolean,
    val startTime: String?,
    val endTime: String?,
    val orderByNumber: Int,
    val categories: List<MenuCategory> = emptyList()
) : Serializable

data class Language(
    val id: String,
    val name: String,
    val code: String,
    val venueId: String
) : Serializable

data class MenuCategory(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val venueId: String,
    val isActive: Boolean,
    val orderByNumber: Int,
    val avoqadoProducts: List<AvoqadoProduct> = emptyList()
) : Serializable

data class AvoqadoProduct(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val price: Double,
    val venueId: String,
    val isActive: Boolean,
    val orderByNumber: Int,
    val categoryId: String?,
    val modifierGroups: List<ModifierGroup> = emptyList()
) : Serializable
