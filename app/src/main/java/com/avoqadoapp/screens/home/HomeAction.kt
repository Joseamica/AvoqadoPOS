package com.avoqadoapp.screens.home

sealed class HomeAction {
    data class FormatAmount(val amount: String): HomeAction()
    data class ValidateAmount(val amount: String): HomeAction()
}