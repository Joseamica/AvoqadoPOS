package com.avoqado.pos.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.SimpleToolbar
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import java.text.DecimalFormat

@Composable
fun CardReaderScreen(
    amount: String,
    currency: String,
    onNavigateBack: () -> Unit = {},
    onPayInCash: () -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier= Modifier.fillMaxSize()
    ) {
        SimpleToolbar(
            iconAction = IconAction(
                iconType = IconType.BACK,
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context
            ),
            title = "Pago con tarjeta",
            onAction = {
                onNavigateBack()
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(20.dp)
                .background(color = Color.White)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter= painterResource(R.drawable.ic_contact_payment),
                    contentDescription = ""
                )

                androidx.compose.material3.Text(
                    text = "Acerque, inserte o deslice la tarjeta",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )

                // Texto de cantidad
                androidx.compose.material3.Text(
                    text = "\$${DecimalFormat("#,###.00").format(amount.toDouble())}",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Button(
                onClick = {
                    onPayInCash()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_hand_cash),
                    contentDescription = "",
                    tint = Color.Black
                )

                Spacer(Modifier.width(8.dp))

                androidx.compose.material3.Text(
                    text = "Efectivo",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

        }
    }
}

@Urovo9100DevicePreview
@Composable
fun MyComposeScreenPreview() {
    CardReaderScreen("10000000.00", "$") // Reemplaza con el nombre de tu compositor
}