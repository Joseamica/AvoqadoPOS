package com.avoqado.pos.features.payment.presentation.inputTipAmount

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.KeyboardSheet
import com.avoqado.pos.core.presentation.components.ToolbarWithIcon
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.payment.presentation.inputTipAmount.components.TipItemCard
import com.avoqado.pos.views.DeclinedPaymentActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTipScreen(
    inputTipViewModel: InputTipViewModel,
) {
    val context = LocalContext.current
    val showCustomKeyboard by inputTipViewModel.showCustomAmount.collectAsStateWithLifecycle()
    val tipPercentages by inputTipViewModel.tipPercentages.collectAsStateWithLifecycle()
    val tipPercentageLabels by inputTipViewModel.tipPercentageLabels.collectAsStateWithLifecycle()
    val paymentDeclined by inputTipViewModel.paymentDeclined.collectAsStateWithLifecycle()


    LaunchedEffect(paymentDeclined) {
        if (paymentDeclined) {
            val intent = Intent(context, DeclinedPaymentActivity::class.java)
            context.startActivity(intent)
        }
    }

    InputTipContent(
        onNavigateBack = {
            inputTipViewModel.navigateBack()
        },
        onPayWithoutTip = {
//            val intent = Intent(context, CardProcessActivity::class.java)
//            intent.putExtra(
//                "amount",
//                inputTipViewModel.subtotal.toAmountMx(),
//            )
//            intent.putExtra("tipAmount", "0.00")
//            intent.putExtra("currency", CURRENCY_LABEL)
//            intent.putExtra("operationType", OperationType.PAYMENT.name)
//            intent.putExtra("splitType", inputTipViewModel.splitType.value)
//            intent.putExtra("waiterName", inputTipViewModel.waiterName)
//            context.startActivity(intent)

            inputTipViewModel.startPayment(0.0)
        },
        onPayWithTip = {
//            val intent =
//                Intent(context, CardProcessActivity::class.java).apply {
//                    putExtra(
//                        "amount",
//                        inputTipViewModel.subtotal.toAmountMx(),
//                    )
//                    putExtra("tipAmount", it.toAmountMx())
//                    putExtra("currency", CURRENCY_LABEL)
//                    putExtra("operationType", OperationType.PAYMENT.name)
//                    putExtra("splitType", inputTipViewModel.splitType.value)
//                    putExtra("waiterName", inputTipViewModel.waiterName)
//                }
//            context.startActivity(intent)
            inputTipViewModel.startPayment(it.toAmountMx().toDouble())
        },
        totalAmount = inputTipViewModel.subtotal.toDouble(),
        waiterName = inputTipViewModel.waiterName,
        tipPercentages = tipPercentages,
        tipPercentageLabels = tipPercentageLabels,
        onCustomAmount = {
            inputTipViewModel.showCustomAmountKeyboard()
        },
    )

    if (showCustomKeyboard) {
        KeyboardSheet(
            onDismiss = {
                inputTipViewModel.hideCustomAmountKeyboard()
            },
            enableFormatChange = true,
            onAmountEntered = { amount, isPercentage ->
                inputTipViewModel.hideCustomAmountKeyboard()
                if (amount > 0) {
//                    val intent =
//                        Intent(context, CardProcessActivity::class.java).apply {
//                            putExtra(
//                                "amount",
//                                inputTipViewModel.subtotal.toAmountMx(),
//                            )
//                            if (isPercentage) {
//                                val subtotal = inputTipViewModel.subtotal.toAmountMx().toDouble()
//                                putExtra(
//                                    "tipAmount",
//                                    (subtotal * amount / 100.0).toString().toAmountMx(),
//                                )
//                            } else {
//                                putExtra(
//                                    "tipAmount",
//                                    amount.toString().toAmountMx(),
//                                )
//                            }
//
//                            putExtra("currency", CURRENCY_LABEL)
//                            putExtra("operationType", OperationType.PAYMENT.name)
//                            putExtra("splitType", inputTipViewModel.splitType.value)
//                            putExtra("waiterName", inputTipViewModel.waiterName)
//                        }
//                    context.startActivity(intent)
                    val tip = if (isPercentage) {
                        val subtotal = inputTipViewModel.subtotal.toAmountMx().toDouble()
                        (subtotal * amount / 100.0).toString().toAmountMx()
                    } else {
                        amount.toString().toAmountMx()
                    }

                    inputTipViewModel.startPayment(
                        tip = tip.toDouble()
                    )
                }
            },
            title = "Propina",
        )
    }
}

@Composable
fun InputTipContent(
    onNavigateBack: () -> Unit,
    totalAmount: Double,
    waiterName: String,
    tipPercentages: List<Float>, // Percentages from venue settings (e.g., 0.12f, 0.15f, 0.18f)
    tipPercentageLabels: List<String>, // Labels from venue settings (e.g., "12%", "15%", "18%")
    onPayWithoutTip: () -> Unit,
    onPayWithTip: (String) -> Unit,
    onCustomAmount: () -> Unit,
) {
    val context = LocalContext.current

    // Calculate tips based on the venue's percentage settings
    val tip1 by remember(tipPercentages) {
        derivedStateOf {
            totalAmount * tipPercentages.getOrElse(0) { 0.12f }.toDouble()
        }
    }
    val tip2 by remember(tipPercentages) {
        derivedStateOf {
            totalAmount * tipPercentages.getOrElse(1) { 0.15f }.toDouble()
        }
    }

    val tip3 by remember(tipPercentages) {
        derivedStateOf {
            totalAmount * tipPercentages.getOrElse(2) { 0.18f }.toDouble()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB)), // Changed from Color.White to #F6F7FB
    ) {
        ToolbarWithIcon(
            title = "\$${totalAmount.toString().toAmountMx()}",
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK,
            ),
            onAction = {
                onNavigateBack()
            },
            showSecondIcon = true,
        )

        // Main content with tip cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (waiterName.isNullOrBlank()) "Agrega una propina para el mesero" else "Agrega una propina para $waiterName",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.tipwaiter),
                contentDescription = "Tip Waiter",
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TipItemCard(
                    percentage = tipPercentageLabels.getOrElse(0) { "12%" },
                    amount = "+ \$${tip1.toString().toAmountMx()}",
                    isPopular = false,
                    onClickTip = {
                        onPayWithTip(tip1.toString())
                    },
                )
                TipItemCard(
                    percentage = tipPercentageLabels.getOrElse(1) { "15%" },
                    amount = "+ \$${tip2.toString().toAmountMx()}",
                    isPopular = true,
                    onClickTip = {
                        onPayWithTip(tip2.toString())
                    },
                )
                TipItemCard(
                    percentage = tipPercentageLabels.getOrElse(2) { "18%" },
                    amount = "+ \$${tip3.toString().toAmountMx()}",
                    isPopular = false,
                    onClickTip = {
                        onPayWithTip(tip3.toString())
                    },
                )
            }
        }

        // Bottom row with both buttons - added elevation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onCustomAmount() },
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                // .shadow(elevation = 0.7.dp, shape = RoundedCornerShape(12.dp)),  // Added elevation
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(width = 0.6.dp, color = Color(0xFFE5E5E5)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_edit),
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Por monto",
                        color = Color.Black,
                        fontSize = 10.sp,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onPayWithoutTip() },
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp),
                // .shadow(elevation = 0.7.dp, shape = RoundedCornerShape(12.dp)),  // Added elevation
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(width = 0.6.dp, color = Color(0xFFE5E5E5)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sin propina",
                        color = Color.Black,
                        fontSize = 10.sp,
                    )
                }
            }


        }
        Spacer(Modifier.height(36.dp))

    }
}

@Urovo9100DevicePreview()
@Composable
fun PreviewInputTipContent() {
    AvoqadoTheme {
        InputTipContent(
            totalAmount = 60.0,
            waiterName = "Juan",
            tipPercentages = listOf(0.12f, 0.15f, 0.18f),
            tipPercentageLabels = listOf("12%", "15%", "18%"),
            onNavigateBack = {},
            onPayWithoutTip = {},
            onPayWithTip = {},
            onCustomAmount = {},
        )
    }
}