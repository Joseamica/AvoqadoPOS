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
import androidx.compose.foundation.layout.paddingFromBaseline
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R

@Composable
fun CardReaderScreen(amount: String, currency: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                text = "\$$amount",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Button(
            onClick = {

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

@Preview
@Composable
fun MyComposeScreenPreview() {
    CardReaderScreen("1.00", "$") // Reemplaza con el nombre de tu compositor
}