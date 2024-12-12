package com.avoqado.pos.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avoqado.pos.OperationFlowHolder
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.bin.InstallmentsApp

class InstallmentViewModel(
) : ViewModel() {

    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    private val _installmentSelected: MutableLiveData<InstallmentsApp> = MutableLiveData()
    val installmentSelected: LiveData<InstallmentsApp> get() = _installmentSelected

    fun setInstallmentSelected(installmentWithTotal: InstallmentsApp) {
        operationFlow?.installments =
        installmentWithTotal.code.toString().padStart(2, '0')
        operationFlow?.amount?.total =
            installmentWithTotal.totalAmount//installmentWithTotal.totalAmountWithCft

        _installmentSelected.value = installmentWithTotal
    }
}