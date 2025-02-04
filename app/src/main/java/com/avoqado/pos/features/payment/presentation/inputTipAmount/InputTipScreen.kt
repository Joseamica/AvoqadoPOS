package com.avoqado.pos.features.payment.presentation.inputTipAmount

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.CustomKeyboard
import com.avoqado.pos.core.presentation.components.KeyboardSheet
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.payment.presentation.inputTipAmount.components.TipItemCard
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.views.CardProcessActivity
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTipScreen(
    inputTipViewModel: InputTipViewModel
) {
    val context = LocalContext.current
    val showCustomKeyboard by inputTipViewModel.showCustomAmount.collectAsStateWithLifecycle()

    InputTipContent(
        onNavigateBack = {
            inputTipViewModel.navigateBack()
        },
        onPayWithoutTip = {
            val intent = Intent(context, CardProcessActivity::class.java)
            intent.putExtra(
                "amount",
                StringUtils.notFormatAmount(inputTipViewModel.subtotal.toAmountMx())
            )
            intent.putExtra("tipAmount", "0.00")
            intent.putExtra("currency", CURRENCY_LABEL)
            intent.putExtra("operationType", OperationType.PAYMENT.name)
            context.startActivity(intent)
            inputTipViewModel.navigateBack()
        },
        onPayWithTip = {
            val intent = Intent(context, CardProcessActivity::class.java).apply {
                putExtra(
                    "amount",
                    StringUtils.notFormatAmount(inputTipViewModel.subtotal.toAmountMx())
                )
                putExtra("tipAmount", StringUtils.notFormatAmount(it.toAmountMx()))
                putExtra("currency", CURRENCY_LABEL)
                putExtra("operationType", OperationType.PAYMENT.name)
            }
            context.startActivity(intent)
            inputTipViewModel.navigateBack()
        },
        totalAmount = inputTipViewModel.subtotal.toDouble(),
        waiterName = "",
        onCustomAmount = {
            inputTipViewModel.showCustomAmountKeyboard()
        }
    )

    if (showCustomKeyboard) {
        KeyboardSheet(
            onDismiss = {
                inputTipViewModel.hideCustomAmountKeyboard()
            },
            onAmountEntered = { amount ->
                inputTipViewModel.hideCustomAmountKeyboard()
            },
            title = "Propina"
        )
    }

}

@Composable
fun InputTipContent(
    onNavigateBack: () -> Unit,
    totalAmount: Double,
    waiterName: String,
    onPayWithoutTip: () -> Unit,
    onPayWithTip: (String) -> Unit,
    onCustomAmount: () -> Unit
) {
    val context = LocalContext.current

    val tip1 by remember {
        derivedStateOf {
            totalAmount * 0.12
        }
    }
    val tip2 by remember {
        derivedStateOf {
            totalAmount * 0.15
        }
    }

    val tip3 by remember {
        derivedStateOf {
            totalAmount * 0.18
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        ToolbarWithIcon(
            title = "\$${totalAmount.toString().toAmountMx()}",
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
            onAction = {
                onNavigateBack()
            },
            showSecondIcon = true
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Agrega una propina para $waiterName",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        onCustomAmount()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            painter = painterResource(R.drawable.icon_edit),
                            contentDescription = "",
                            tint = Color.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Monto",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 16.dp
                    ),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TipItemCard(
                        percentage = "12%",
                        amount = "+ \$${tip1.toString().toAmountMx()}",
                        isPopular = false,
                        onClickTip = {
                            onPayWithTip(tip1.toString())
                        }
                    )
                    TipItemCard(
                        percentage = "15%",
                        amount = "+ \$${tip2.toString().toAmountMx()}",
                        isPopular = true,
                        onClickTip = {
                            onPayWithTip(tip2.toString())
                        }
                    )
                    TipItemCard(
                        percentage = "18%",
                        amount = "+ \$${tip3.toString().toAmountMx()}",
                        isPopular = false,
                        onClickTip = {
                            onPayWithTip(tip3.toString())
                        }
                    )
                }

            }

            Button(
                onClick = {
                    onPayWithoutTip()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Sin propina",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview()
@Composable
fun PreviewInputTipContent() {
    AvoqadoTheme {
        InputTipContent(
            totalAmount = 60.0,
            waiterName = "Juan",
            onNavigateBack = {},
            onPayWithoutTip = {},
            onPayWithTip = {},
            onCustomAmount = {}
        )
    }
}