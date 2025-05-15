package com.avoqado.pos.features.payment.data

import timber.log.Timber
import com.avoqado.pos.features.payment.data.cache.PaymentCacheStorage
import com.avoqado.pos.features.payment.data.mappers.toCache
import com.avoqado.pos.features.payment.data.mappers.toDomain
import com.avoqado.pos.features.payment.data.network.AvoqadoService
import com.avoqado.pos.features.payment.data.network.models.RecordPaymentBody
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.review.ReviewRating
import com.menta.android.core.model.Adquirer

class PaymentRepositoryImpl(
    private val paymentCacheStorage: PaymentCacheStorage,
    private val avoqadoService: AvoqadoService,
) : PaymentRepository {
    override fun getCachePaymentInfo(): PaymentInfoResult? = paymentCacheStorage.getPaymentInfo()?.toDomain()

    override fun setCachePaymentInfo(paymentInfoResult: PaymentInfoResult) {
        paymentCacheStorage.setPaymentInfo(paymentInfoResult.toCache())
    }

    override fun clearCachePaymentInfo() {
        paymentCacheStorage.clear()
    }

    override suspend fun recordPayment(
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
        adquirer: Adquirer?,
        reviewRating: ReviewRating?,
    ): String {
        try {
            var body =
                RecordPaymentBody(
                    method = adquirer?.let { "CARD" } ?: "CASH",
                    tpvId = tpvId,
                    waiterName = waiterName,
                    splitType = splitType,
                    status = status,
                    amount = amount,
                    tip = tip,
                    venueId = venueId,
                    source = "AVOQADO_TPV",
                    paidProductsId = paidProductsId,
                    token = token,
                    isInternational = adquirer?.let { data -> data.capture?.card?.isInternational } ?: false,
                    reviewRating = reviewRating?.name,
                )

            adquirer?.let {
                body =
                    body.copy(
                        cardBrand = it.capture?.card?.brand,
                        last4 =
                            it.capture
                                ?.card
                                ?.maskedPan
                                ?.let { pan -> pan.substring(pan.length - 4) },
                        typeOfCard = it.capture?.card?.type,
                        currency = it.amount.currency,
                        bank = it.capture?.card?.bank,
                        mentaAuthorizationReference = it.authorization?.retrievalReferenceNumber,
                        mentaOperationId = it.id,
                        mentaTicketId = it.ticketId.toString(),
                    )
            }
            val result =
                avoqadoService.recordPayment(
                    venueId = venueId,
                    tableNumber = tableNumber,
                    recordPaymentBody = body,
                )
        } catch (e: Exception) {
            Timber.e( "Error recording payment", e)
        }

        return adquirer?.let { it.id ?: "" } ?: token
    }
    
    override suspend fun recordFastPayment(
        venueId: String,
        waiterName: String,
        tpvId: String,
        splitType: String,
        status: String,
        amount: Int,
        tip: Int,
        token: String,
        paidProductsId: List<String>,
        adquirer: Adquirer?,
        reviewRating: ReviewRating?,
    ): String {
        try {
            var body =
                RecordPaymentBody(
                    method = adquirer?.let { "CARD" } ?: "CASH",
                    tpvId = tpvId,
                    waiterName = waiterName,
                    splitType = splitType,
                    status = status,
                    amount = amount,
                    tip = tip,
                    venueId = venueId,
                    source = "AVOQADO_TPV",
                    paidProductsId = paidProductsId,
                    token = token,
                    isInternational = adquirer?.let { data -> data.capture?.card?.isInternational } ?: false,
                    reviewRating = reviewRating?.name,
                )

            adquirer?.let {
                body =
                    body.copy(
                        cardBrand = it.capture?.card?.brand,
                        last4 =
                            it.capture
                                ?.card
                                ?.maskedPan
                                ?.let { pan -> pan.substring(pan.length - 4) },
                        typeOfCard = it.capture?.card?.type,
                        currency = it.amount.currency,
                        bank = it.capture?.card?.bank,
                        mentaAuthorizationReference = it.authorization?.retrievalReferenceNumber,
                        mentaOperationId = it.id,
                        mentaTicketId = it.ticketId.toString(),
                    )
            }
            val result =
                avoqadoService.recordFastPayment(
                    venueId = venueId,
                    recordPaymentBody = body,
                )
        } catch (e: Exception) {
            Timber.e("Error recording fast payment", e)
        }

        return adquirer?.let { it.id ?: "" } ?: token
    }
}
