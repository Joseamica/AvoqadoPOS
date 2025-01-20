package com.avoqado.pos.ui.screen

import android.content.Intent
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
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.screens.inputTipAmount.InputTipViewModel
import com.avoqado.pos.views.CardProcessActivity
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils

@Composable
fun TipSelectionScreen(
    inputTipViewModel: InputTipViewModel
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$50.00",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Agregar propina para Lucas",
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
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TipCard(percentage = "18%", amount = "+ $9", isPopular = false)
                TipCard(percentage = "20%", amount = "+ $10", isPopular = true)
                TipCard(percentage = "25%", amount = "+ $12.50", isPopular = false)
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

@Preview(showSystemUi = true)
@Composable
fun tipSelection() {
}
