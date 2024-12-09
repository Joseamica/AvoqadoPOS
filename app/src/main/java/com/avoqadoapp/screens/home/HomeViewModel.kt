package com.avoqadoapp.screens.home


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgs
import com.avoqadoapp.CURRENCY_LABEL
import com.avoqadoapp.core.base.BaseViewModel
import com.avoqadoapp.core.navigation.NavigationArg
import com.avoqadoapp.core.navigation.NavigationDispatcher
import com.avoqadoapp.router.destinations.MainDests
import com.avoqadoapp.ui.components.TextFieldState
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils
import kotlinx.coroutines.launch

class HomeViewModel(
    private val navigationDispatcher: NavigationDispatcher
) : BaseViewModel<HomeViewState, HomeAction>(HomeViewState()) {

    companion object {
        const val MINIMUM_AMOUNT_REQUIRED = 1
    }

    private val inputFieldState =
        mutableStateOf(
            TextFieldState(
                textFieldValue = TextFieldValue(
                    text = "0,00",
                    selection = TextRange("0.00".length)
                ),
                notifyErrorState = {}
            )
        )



    val textFieldAmount: MutableState<TextFieldState> = inputFieldState


    override suspend fun handleActions(action: HomeAction) {
        when(action) {
            is HomeAction.FormatAmount -> {
                val currentTextFieldAmount = inputFieldState.value
                val amount = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(action.amount))
                inputFieldState.value = currentTextFieldAmount.copy(
                    textFieldValue = TextFieldValue(
                        text = amount,
                        selection = TextRange(amount.length)
                    )
                )
            }
            is HomeAction.ValidateAmount -> {
                viewModelScope.launch {
                    if (StringUtils.toDoubleAmount(action.amount) >= MINIMUM_AMOUNT_REQUIRED) {
                        val validatedAmount= action.amount.replace(",", "").replace(".", "")
                        navigationDispatcher.navigateWithArgs(
                            MainDests.CardProcess,
                            NavigationArg.StringArg(
                                MainDests.CardProcess.ARG_AMOUNT, validatedAmount
                            ),
                            NavigationArg.StringArg(
                                MainDests.CardProcess.ARG_CURRENCY, CURRENCY_LABEL
                            ),
                            NavigationArg.StringArg(
                                MainDests.CardProcess.ARG_OPERATION_TYPE, OperationType.PAYMENT.name
                            )
                        )
                    }
                }
            }
        }
    }

}