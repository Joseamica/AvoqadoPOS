package com.avoqado.pos.core.presentation.model

import com.avoqado.pos.core.presentation.utils.toAmountMx

data class OperationInfo(
    val dateTime: String,
    val acquirerName: String,
    val transactionId: String,
    val operationData: OperationData?,
    val authOperationCode: String,
    val subtotal: String,
    val tip: String,
) {
    val total: String
        get() {
            return (subtotal.toDouble() + tip.toDouble()).toString().toAmountMx()
        }
}

data class OperationData(
    val cardBrand: String,
    val cardType: String,
    val pan: String,
)
