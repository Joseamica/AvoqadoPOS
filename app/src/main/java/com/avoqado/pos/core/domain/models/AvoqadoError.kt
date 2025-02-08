package com.avoqado.pos.core.domain.models

sealed class AvoqadoError: Exception() {
    data class BasicError(override val message: String) : AvoqadoError()
    data object Unauthorized : AvoqadoError()
}