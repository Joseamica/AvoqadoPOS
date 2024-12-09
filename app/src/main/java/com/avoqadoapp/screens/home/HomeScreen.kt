package com.avoqadoapp.screens.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqadoapp.CURRENCY_LABEL
import com.avoqadoapp.screens.DoPaymentActivity
import com.avoqadoapp.screens.home.HomeViewModel.Companion.MINIMUM_AMOUNT_REQUIRED
import com.avoqadoapp.ui.components.TextFieldAmount
import com.avoqadoapp.ui.components.TextFieldState
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils
import timber.log.Timber

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onAction = viewModel.submitAction,
        inputFieldState = viewModel.textFieldAmount
    )
}

@Composable
fun HomeContent(
    state: HomeViewState,
    inputFieldState: MutableState<TextFieldState>,
    onAction: (HomeAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldAmount(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .pointerInput(Unit) {
                        focusRequester.requestFocus()
                    }
                    .focusRequester(focusRequester),
                textFieldState = inputFieldState,
                onTextChange = {
                    onAction(HomeAction.FormatAmount(it))
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(57.dp)
                    .align(Alignment.End),
                onClick = {
                    Timber.i("ingreso: ${inputFieldState.value.textFieldValue.text}")
                    val input = inputFieldState.value.textFieldValue.text
                    if (StringUtils.toDoubleAmount(input) >= MINIMUM_AMOUNT_REQUIRED) {
                        val validatedAmount= input.replace(",", "").replace(".", "")
                        val intent = Intent(context, DoPaymentActivity::class.java)
                        intent.putExtra("amount", validatedAmount)
                        intent.putExtra("currency", CURRENCY_LABEL)
                        intent.putExtra("operationType", OperationType.PAYMENT.name)
                        context.startActivity(intent)
                    }
                }
            ) {
                Text(text = "Continuar")
            }
        }
    }
}