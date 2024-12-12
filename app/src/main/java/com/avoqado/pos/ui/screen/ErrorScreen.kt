package com.avoqado.pos.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.ui.theme.textColor

@Composable
fun ErrorScreen(
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_info),
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
            androidx.compose.material.Text(
                modifier = Modifier
                    .width(309.dp)
                    .align(Alignment.Start),
                text = title,
                color = textColor,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )

            androidx.compose.material.Text(
                modifier = Modifier
                    .width(309.dp)
                    .align(Alignment.Start),
                text = message,
                color = textColor,
                fontSize = 25.sp
            )
            /*PrimaryButton(
                text = "Listo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 14.dp, 0.dp)
                    .height(57.dp)
                    .align(Alignment.End),
                onClick = {
                }
            )*/
        }
    }
}