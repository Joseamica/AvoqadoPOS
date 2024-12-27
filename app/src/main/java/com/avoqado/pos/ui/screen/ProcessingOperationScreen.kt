package com.avoqado.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.ui.theme.primary
import com.avoqado.pos.ui.theme.textColor
import com.avoqado.pos.ui.theme.textlightGrayColor

@Composable
fun ProcessingOperationScreen(
    title: String,
    message: String,
    showLoading: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(textlightGrayColor)
    ) {
        if (showLoading) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(120.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressCircleSmart()
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 24.dp,end=24.dp, bottom = 50.dp)
        ) {
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.mulish_bold)),
                fontSize = 35.sp,
                color = textColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = message,
                fontFamily = FontFamily(Font(R.font.mulish_regular)),
                fontSize = 25.sp,
                color = textColor,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun ProcessingOperationScreenPreview() {
    ProcessingOperationScreen(
        title = "Aguarde",
        message = "Mientras procesamos su pago"
    )
}
