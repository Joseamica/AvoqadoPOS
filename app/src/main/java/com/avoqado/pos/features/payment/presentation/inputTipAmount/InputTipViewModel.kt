package com.avoqado.pos.features.payment.presentation.inputTipAmount

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.core.usecase.ValidateAmountUseCase
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.ui.screen.TextFieldState
import com.menta.android.core.utils.StringUtils

class InputTipViewModel(
    val subtotal: String,
    val waiterName: String,
    private val validateAmountUseCase: ValidateAmountUseCase,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    private val _textFieldAmount = mutableStateOf(
        TextFieldState(
            textFieldValue = TextFieldValue(
                text = "0,00",
                selection = TextRange("0.00".length)
            ),
            notifyErrorState = {}
        )
    )
    val textFieldAmount: MutableState<TextFieldState> = _textFieldAmount

    fun formatAmount(value: String) {
        val currentTextFieldAmount = _textFieldAmount.value
        val amount = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(value))
        _textFieldAmount.value = currentTextFieldAmount.copy(
            textFieldValue = TextFieldValue(
                text = amount,
                selection = TextRange(amount.length)
            )
        )
    }

    fun isValidAmount(clearTip: Boolean = false): String? {
        val tip = if (clearTip) "0" else _textFieldAmount.value.textFieldValue.text
        val total = StringUtils.toDoubleAmount(tip)

        return if (validateAmountUseCase.doExecute(total.toString())) {
            total.toString().replace(",", "").replace(".", "")
        } else {
            if (clearTip) {
                "0"
            } else {
                null
            }
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

}