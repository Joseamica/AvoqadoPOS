package com.avoqadoapp.screens.cardProcess

data class CardProcessViewState(
    val info: List<String> = emptyList<String>(),
    val isPaymentStarted: Boolean = false
)
