package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class Representative(
    @SerializedName("birth_date")
    val birthDate: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("representative_id")
    val representativeId: RepresentativeId?,
    @SerializedName("surname")
    val surname: String?
)