package com.avoqado.pos.core.domain.models

enum class SplitType(
    val value: String,
) {
    PERPRODUCT("PERPRODUCT"),
    EQUALPARTS("EQUALPARTS"),
    CUSTOMAMOUNT("CUSTOMAMOUNT"),
    FULLPAYMENT("FULLPAYMENT"),
}
