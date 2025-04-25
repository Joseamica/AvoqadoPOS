package com.avoqado.pos.core.presentation.viewmodel

import androidx.lifecycle.ViewModel

class ListTransactionViewModel : ViewModel() {
    fun getDateTime(datetime: String): String = datetime

    fun getTransactionType(type: String): String =
        when (type) {
            "PAYMENT" -> "Venta"
            else -> "Devolución"
        }

    fun getStatus(status: String): String =
        when (status) {
            "APPROVED" -> "Aprobada"
            else -> "Rechazada"
        }
}
