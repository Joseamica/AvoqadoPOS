package com.avoqado.pos.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.menta.android.common_cross.util.TransactionUtils.APPROVED

class ListTransactionViewModel : ViewModel() {

    fun getDateTime(datetime: String): String {
        return datetime
    }

    fun getTransactionType(type: String): String {
        return when (type) {
            "PAYMENT" -> "Venta"
            else -> "Devolución"
        }
    }

    fun getStatus(status: String): String {
        return when (status) {
            "APPROVED" -> "Aprobada"
            else -> "Rechazada"
        }
    }
}