package com.avoqado.pos.features.payment.presentation.transactions

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.menta.android.common_cross.data.datasource.local.model.Transaction
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.model.TrxListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class TransactionSummaryViewModel(
    private val sessionManager: SessionManager,
    private val snackbarDelegate: SnackbarDelegate,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {
    val currentUser = sessionManager.getAvoqadoSession()
    private val _paymentResult = MutableStateFlow<List<Transaction>>(emptyList())
    val paymentResult: StateFlow<List<Transaction>> = _paymentResult.asStateFlow()

    fun handleTransactionResponse(response: TrxListResponse) {
        if (response.statusResult?.statusType == StatusType.SUCCESS) {
            _paymentResult.update {
                response.content
            }
        } else {
            Timber.e(response.statusResult?.message?:"Error en la consulta")
            snackbarDelegate.showSnackbar(
                state = SnackbarState.Default,
                message = "Error en la consulta"
            )
        }
    }

    fun navigateBack(){
        navigationDispatcher.navigateBack()
    }
}