package com.avoqadoapp.screens.cardProcess

sealed class CardProcessAction {
    data class LogCardProcess(val log: String) : CardProcessAction()
    data object StartPaymentProcess : CardProcessAction()
}