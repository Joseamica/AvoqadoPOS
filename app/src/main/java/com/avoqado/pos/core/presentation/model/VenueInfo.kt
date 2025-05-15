package com.avoqado.pos.core.presentation.model

import com.avoqado.pos.core.data.network.models.Feature

data class VenueInfo(
    val name: String,
    val id: String,
    val address: String,
    val phone: String,
    val acquisition: String,
    val feature: Feature? = null,
)
