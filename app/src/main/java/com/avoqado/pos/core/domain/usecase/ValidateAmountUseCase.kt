package com.avoqado.pos.core.domain.usecase

import com.menta.android.core.usecase.UseCase
import com.menta.android.core.utils.StringUtils

class ValidateAmountUseCase : UseCase<String, Boolean> {
    override fun doExecute(params: String): Boolean {
        return StringUtils.toDoubleAmount(params) >= MINIMUM_AMOUNT_REQUIRED
    }

    private companion object {
        const val MINIMUM_AMOUNT_REQUIRED = 1
    }
}