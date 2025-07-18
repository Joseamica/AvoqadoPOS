package com.avoqado.pos.core.domain.models

data class ShiftSummary(
    val tips: List<Pair<String, String>>,
    val averageTipPercentage: Double,
    val ordersCount: Int,
    val ratingsCount: Int,
    val totalSales: Double,
    val totalTips: Double,
)
