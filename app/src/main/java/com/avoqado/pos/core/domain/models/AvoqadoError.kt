package com.avoqado.pos.core.domain.models

sealed class AvoqadoError: Exception() {
    data class BasicError(override val message: String, val code: Int? = null) : AvoqadoError()
    data object Unauthorized : AvoqadoError()
}