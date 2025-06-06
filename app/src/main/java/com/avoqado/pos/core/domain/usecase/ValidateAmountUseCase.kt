package com.avoqado.pos.core.domain.usecase

import com.avoqado.pos.core.presentation.utils.toAmountMXDouble

//import com.menta.android.core.usecase.UseCase
//import com.menta.android.core.utils.StringUtils

class ValidateAmountUseCase {
    fun doExecute(params: String): Boolean = params.toAmountMXDouble() >= MINIMUM_AMOUNT_REQUIRED

    private companion object {
        const val MINIMUM_AMOUNT_REQUIRED = 1
    }
}
