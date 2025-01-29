package com.avoqado.pos.core.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.ui.screen.TextFieldState
import com.menta.android.core.utils.StringUtils
import kotlinx.coroutines.launch

class InputAmountViewModel(
    private val context: Context,
    private val validateAmountUseCase: ValidateAmountUseCase,

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

    private val _isValidAmount: MutableLiveData<String?> = MutableLiveData()
    val isValidAmount: MutableLiveData<String?>
        get() = _isValidAmount

    private val _goToListTransaction: MutableLiveData<Boolean> = MutableLiveData()
    val goToListTransaction: LiveData<Boolean> get() = _goToListTransaction

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

    fun isValidAmount(amount: String) {
        viewModelScope.launch {
            if (validateAmountUseCase.doExecute(amount)) {
                _isValidAmount.value = amount.replace(",", "").replace(".", "")
            } else {
                _isValidAmount.value = null
            }
        }
    }

    fun goToListTransaction() {
        _goToListTransaction.value = true
    }

}