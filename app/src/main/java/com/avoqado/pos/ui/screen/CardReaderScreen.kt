package com.avoqado.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardReaderScreen(amount: String, currency: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(color = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto "Total"
            androidx.compose.material3.Text(
                text = "Total",
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 50.dp)
            )

            // Texto de cantidad
            androidx.compose.material3.Text(
                text = currency + amount,
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .paddingFromBaseline(top = 20.dp)
                    .padding(start = 20.dp)
            )
        }

        androidx.compose.material3.Text(
            text = "Acerque, inserte o deslice la tarjeta",
            color = Color.Black,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, top = 450.dp, bottom = 50.dp)
        )
    }
}

@Preview
@Composable
fun MyComposeScreenPreview() {
    CardReaderScreen("1.00", "$") // Reemplaza con el nombre de tu compositor
}