package com.avoqado.pos.screens.inputTipAmount

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.ui.screen.PrimaryButton
import com.avoqado.pos.ui.screen.TextFieldAmount
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.avoqado.pos.views.CardProcessActivity
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils

@Composable
fun InputTipScreen(
    inputTipViewModel: InputTipViewModel
) {

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ToolbarWithIcon(
            "Ingresar propina",
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
            onAction = {
                inputTipViewModel.navigateBack()
            }
        )

        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldAmount(
                modifier = Modifier
                    .layoutId("textFieldAmount")
                    .padding(horizontal = 20.dp)
                    .pointerInput(Unit) {
                        focusRequester.requestFocus()
                    }
                    .focusRequester(focusRequester),
                textFieldState = inputTipViewModel.textFieldAmount,
                onTextChange = {
                    inputTipViewModel.formatAmount(it)
                }
            )
        }

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                text = "Continuar",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(57.dp)
                    .align(Alignment.End),
                onClick = {
                    inputTipViewModel.isValidAmount(clearTip = false)?.let { amount ->
                        val intent = Intent(context, CardProcessActivity::class.java)
                        intent.putExtra("amount", StringUtils.notFormatAmount(inputTipViewModel.subtotal))
                        intent.putExtra("tipAmount", amount)
                        intent.putExtra("currency", CURRENCY_LABEL)
                        intent.putExtra("operationType", OperationType.PAYMENT.name)
                        context.startActivity(intent)
                    }
                    inputTipViewModel.navigateBack()
                }
            )
            PrimaryButton(
                text = "Continuar sin propina",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(57.dp)
                    .align(Alignment.End),
                onClick = {
                    inputTipViewModel.isValidAmount(clearTip = true)?.let { amount ->
                        val intent = Intent(context, CardProcessActivity::class.java)
                        intent.putExtra("amount", StringUtils.notFormatAmount(inputTipViewModel.subtotal))
                        intent.putExtra("tipAmount", "0")
                        intent.putExtra("currency", CURRENCY_LABEL)
                        intent.putExtra("operationType", OperationType.PAYMENT.name)
                        context.startActivity(intent)
                    }
                    inputTipViewModel.navigateBack()
                }
            )

        }

    }
}