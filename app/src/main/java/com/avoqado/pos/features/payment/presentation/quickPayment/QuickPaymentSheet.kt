package com.avoqado.pos.features.payment.presentation.quickPayment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.CustomKeyboard
import com.avoqado.pos.core.presentation.components.formatAmount
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.hintTextColor
import com.avoqado.pos.core.presentation.theme.lightGrayNumberField

@Composable
fun QuickPaymentSheet(
    viewModel: QuickPaymentViewModel
) {
    var amount by remember { mutableStateOf(0L) } // Store amount in cents
    val formattedAmount =
        remember(amount) { formatAmount(amount, isPercentage = false) }

    Surface(
        modifier = Modifier.clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = ModalBottomSheetDefaults.Elevation
    ) {
        Column {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cantidad personalizada",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(R.drawable.baseline_close_24),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                        .clickable {
                            viewModel.onBack()
                        }
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .background(color = lightGrayNumberField)
                    .fillMaxWidth()
            ) {
                Text(
                    text = formattedAmount,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = hintTextColor,
                        fontFamily = AppFont.EffraFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 24.dp,
                            horizontal = 32.dp
                        ),
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                CustomKeyboard(
                    modifier = Modifier.fillMaxWidth(),
                    togglePercentage = false,
                    onNumberClick = { digit ->
                        when (digit) {
                            -3 -> amount = 0 // Clear amount
                            -4 -> amount *= 100 // Add two zeros
                            else -> amount = (amount * 10 + digit).coerceAtMost(1500000)
                        }
                    },
                    onBackspaceClick = {
                        amount /= 10 // Remove last digit
                    },
                    onConfirmClick = {
                        viewModel.submitAmount(amount / 100.0) // Convert cents to dollars
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}