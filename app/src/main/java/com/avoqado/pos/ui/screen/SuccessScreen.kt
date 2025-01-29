package com.avoqado.pos.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.textColor

@Composable
fun SuccessScreen(message: String) {

    Box(
        modifier = Modifier

            .fillMaxSize()
            .background(primary),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_success),
                    contentDescription = null,
                )
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .width(309.dp)
                    .align(Alignment.Start),
                text = "Listo!",
                color = textColor,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                modifier = Modifier
                    .width(309.dp)
                    .align(Alignment.Start),
                text = message,
                color = textColor,
                fontSize = 25.sp
            )
        }
    }
}