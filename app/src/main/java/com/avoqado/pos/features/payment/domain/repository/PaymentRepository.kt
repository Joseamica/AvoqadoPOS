package com.avoqado.pos.features.payment.domain.repository

import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.presentation.review.ReviewRating
import com.example.content_core_service.transaction_service.models.TransactionModel

interface PaymentRepository {
    fun getCachePaymentInfo(): PaymentInfoResult?

    fun setCachePaymentInfo(paymentInfoResult: PaymentInfoResult)

    fun clearCachePaymentInfo()

    suspend fun recordPayment(
        venueId: String,
        tableNumber: String,
        waiterName: String,
        tpvId: String,
        splitType: String,
        status: String,
        amount: Int,
        tip: Int,
        billId: String,
        token: String,
        paidProductsId: List<String>,
        adquirer: TransactionModel?,
        reviewRating: ReviewRating? = null,
    ): String
    
    suspend fun recordFastPayment(
        venueId: String,
        waiterName: String,
        tpvId: String,
        splitType: String,
        status: String,
        amount: Int,
        tip: Int,
        token: String,
        paidProductsId: List<String>,
        adquirer: TransactionModel?,
        reviewRating: ReviewRating? = null,
    ): String
}
