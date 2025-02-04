package com.avoqado.pos.core.presentation.components

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.hintTextColor
import com.avoqado.pos.core.presentation.theme.lightGrayNumberField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardSheet(
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    onDismiss: () -> Unit,
    onAmountEntered: (Double) -> Unit,
    title: String? = null
) {
    var amount by remember { mutableStateOf(0L) } // Store amount in cents
    val formattedAmount = remember(amount) { formatAmount(amount) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        sheetState = sheetState
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title ?: "",
                    style = MaterialTheme.typography.titleSmall.copy(
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
                            onDismiss()
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

            Box (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ){
                CustomKeyboard(
                    modifier = Modifier.fillMaxWidth(),
                    onNumberClick = { digit ->
                        amount = (amount * 10 + digit).coerceAtMost(999999) // Max $9999.99
                    },
                    onBackspaceClick = {
                        amount /= 10 // Remove last digit
                    },
                    onConfirmClick = {
                        onAmountEntered(amount / 100.0) // Convert cents to dollars
                        onDismiss()
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// Formatting function for currency display
fun formatAmount(amount: Long): String {
    val dollars = amount / 100
    val cents = amount % 100
    return String.format("$%,d.%02d", dollars, cents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewKeyboardSheet() {
    AvoqadoTheme {
        KeyboardSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            ),
            onDismiss = {},
            title = "Propina",
            onAmountEntered = { amount ->
                // Do nothing
            }
        )
    }
}