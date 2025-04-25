package com.avoqado.pos.features.management.presentation.splitPerson

data class SplitByPersonViewState(
    val loading: Boolean = false,
    val totalPendingAmount: String = "0.00",
    val splitPartySize: Int = 0,
    val splitPartyPaidSize: Int = 0,
    val splitPartySelected: List<Int> = emptyList(),
) {
    val totalSelectedAmount: String
        get() = "%.2f".format(totalPendingAmount.toDouble() / splitPartySize * splitPartySelected.size)
}
