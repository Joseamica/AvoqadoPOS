package com.avoqado.pos.features.payment.presentation.inputTipAmount

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.ui.screen.TextFieldState
import com.menta.android.core.utils.StringUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InputTipViewModel(
    val subtotal: String,
    val waiterName: String,
    private val validateAmountUseCase: ValidateAmountUseCase,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    private val _showCustomAmount = MutableStateFlow(false)
    val showCustomAmount: StateFlow<Boolean> = _showCustomAmount

    fun showCustomAmountKeyboard(){
        _showCustomAmount.update {
            true
        }
    }

    fun hideCustomAmountKeyboard(){
        _showCustomAmount.update {
            false
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

}