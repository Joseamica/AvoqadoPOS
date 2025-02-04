package com.avoqado.pos.features.payment.presentation.paymentResult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.backgroundPrimaryColor
import com.avoqado.pos.core.presentation.theme.buttonGrayColor
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.ui.screen.SimpleToolbar
import com.avoqado.pos.ui.screen.ToolbarWithIcon


@Composable
fun PaymentResultScreen(
    viewModel: PaymentResultViewModel
){
    val state by viewModel.paymentResult.collectAsStateWithLifecycle()

    PaymentResultContent(
        state = state,
        onGoToHome = {
            viewModel.goToHome()
        },
        onNewPayment = {
            viewModel.newPayment()
        }
    )
}

@Composable
fun PaymentResultContent(
    state: PaymentResultViewState,
    onGoToHome: () -> Unit = {},
    onNewPayment: () -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundPrimaryColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            SimpleToolbar(
                title = "Nuevo pago",
                iconAction = IconAction(
                    iconType = IconType.CANCEL,
                    flowStep = FlowStep.NAVIGATE_BACK,
                    context = context
                ),
                onAction = {
                    onGoToHome()
                },
                onActionSecond = {
                    onNewPayment()
                }
            )

            Box(
                modifier = Modifier.padding(
                    top = 24.dp
                )
            ) {
                Image(
                    painter = painterResource(R.drawable.ilu_ticket_background),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 90.dp),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )

                state.qrCode?.let {
                    Box (
                        modifier = Modifier.size(180.dp)
                            .border(
                                width = 10.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .align(Alignment.TopCenter)
                    ){

                    }
                }


                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = 40.dp
                        )
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Escanea el código QR para descargar el recibo y dejar una calificación",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
                    )

                    Spacer(Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "Total pagado",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("\$${state.totalAmount.toString().toAmountMx()}",
                            style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.height(16.dp))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color.LightGray
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Total", modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Text("\$${state.subtotalAmount.toString().toAmountMx()}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Propina", modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Text("\$${state.tipAmount.toString().toAmountMx()}")
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }

        }

        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = buttonGrayColor
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_print_24),
                contentDescription = "",
                tint = buttonGrayColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "O imprime el recibo",
                color = buttonGrayColor,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))
    }

}

@Urovo9100DevicePreview
@Composable
fun PaymentResultContentPreview() {
    AvoqadoTheme {
        PaymentResultContent(
            state = PaymentResultViewState()
        )
    }
}