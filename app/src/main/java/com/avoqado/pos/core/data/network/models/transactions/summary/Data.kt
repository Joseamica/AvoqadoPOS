package com.avoqado.pos.core.data.network.models.transactions.summary

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("dateRange")
    val dateRange: DateRange?,
    @SerializedName("summary")
    val summary: Summary?,
    @SerializedName("waiterTips")
    val waiterTips: List<WaiterTip?>?,
)
