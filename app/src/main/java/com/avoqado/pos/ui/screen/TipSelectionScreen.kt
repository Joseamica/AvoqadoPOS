package com.avoqado.pos.ui.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.screens.inputTipAmount.InputTipViewModel
import com.avoqado.pos.screens.tableDetail.TableDetailViewModel
import com.avoqado.pos.views.CardProcessActivity
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils

@Composable
fun TipSelectionScreen(
    inputTipViewModel: InputTipViewModel,
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    Scaffold(containerColor = Color.White) { paddingValues ->
        ToolbarWithIcon(
            title = "Queda por pagar: ${inputTipViewModel.subtotal}",
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
            onAction = {
                inputTipViewModel.navigateBack()




            },
            showSecondIcon = true
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Agregar propina",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {},
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Custom amount",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                    },
                    clickOnDone = {
                        inputTipViewModel.isValidAmount(clearTip = false)?.let { amount ->
                            val intent = Intent(context, CardProcessActivity::class.java)
                            intent.putExtra(
                                "amount",
                                StringUtils.notFormatAmount(inputTipViewModel.subtotal)
                            )
                            intent.putExtra("tipAmount", amount)
                            intent.putExtra("isCustomTip", "true")
                            intent.putExtra("currency", CURRENCY_LABEL)
                            intent.putExtra("operationType", OperationType.PAYMENT.name)
                            context.startActivity(intent)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TipCard(percentage = "18%", amount = "+ $9", isPopular = false, onClickTip = {
                    processTipAndNavigate(
                        amount = inputTipViewModel.subtotal,
                        context = context,
                        0.18,
                        CURRENCY_LABEL,
                        OperationType.PAYMENT.name
                    )
                })
                TipCard(percentage = "20%", amount = "+ $10", isPopular = true, onClickTip = {
                    processTipAndNavigate(
                        amount = inputTipViewModel.subtotal,
                        context = context,
                        0.20,
                        CURRENCY_LABEL,
                        OperationType.PAYMENT.name
                    )

                })
                TipCard(percentage = "25%", amount = "+ $12.50", isPopular = false, onClickTip = {
                    processTipAndNavigate(
                        amount = inputTipViewModel.subtotal,
                        context = context,
                        0.25,
                        CURRENCY_LABEL,
                        OperationType.PAYMENT.name
                    )
                })
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    inputTipViewModel.isValidAmount(clearTip = false)?.let { amount ->
                        val intent = Intent(context, CardProcessActivity::class.java)
                        intent.putExtra(
                            "amount",
                            StringUtils.notFormatAmount(inputTipViewModel.subtotal)
                        )
                        intent.putExtra("tipAmount", amount)
                        intent.putExtra("currency", CURRENCY_LABEL)
                        intent.putExtra("operationType", OperationType.PAYMENT.name)
                        context.startActivity(intent)
                    }
                    inputTipViewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Sin propinas",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TipCard(percentage: String, amount: String, isPopular: Boolean, onClickTip: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .clickable { onClickTip() }
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isPopular) {
                Text(
                    text = "Popular",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Blue, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = percentage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amount,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}


fun processTipAndNavigate(
    amount: String,
    context: Context,
    tipPercentage: Double,
    currency: String,
    operationType: String
) {

    val intent = Intent(context, CardProcessActivity::class.java).apply {
        putExtra(
            "amount",
            StringUtils.notFormatAmount(amount)
        )
        putExtra("tipAmount", StringUtils.notFormatAmount(tipPercentage.toString()))
        putExtra("currency", currency)
        putExtra("operationType", operationType)
    }
    context.startActivity(intent)
}

