package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class ShiftUpdateMessage(
    val id: String? = null,
    val turnId: Int? = null,
    val insideTurnId: Int? = null,
    val origin: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val fund: Double? = null,
    val cash: Double? = null,
    val card: Double? = null,
    val credit: Double? = null,
    val fum: String? = null,
    val cashier: String? = null,
    val venueId: String? = null,
    val active: Boolean = true,
    
    // Add these properties to make mapping easier
    @SerializedName("isStarted")
    val isStarted: Boolean = true,
    
    @SerializedName("isFinished") 
    val isFinished: Boolean = false,
    
    val updatedAt: Instant? = null,
    val createdAt: Instant? = null
) 
