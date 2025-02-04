package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R

@Composable
fun CustomKeyboard(
    modifier: Modifier = Modifier,
    onNumberClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val keys = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
        listOf(-3, 0, -3) // -1 for backspace, -2 for confirm, -3 for empty space
    )

   Row(
       modifier = modifier.height(intrinsicSize = IntrinsicSize.Max),
       horizontalArrangement = Arrangement.Center
   ) {
       Column(
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.spacedBy(8.dp),
       ) {
           keys.forEach { row ->
               Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                   row.forEach { key ->
                       when (key) {
                           -1 -> KeyboardButton(
                               modifier = Modifier.size(80.dp),
                               icon = R.drawable.baseline_backspace_24,
                               onClick = onBackspaceClick
                           )
                           -2 -> KeyboardButton(
                               modifier = Modifier.size(80.dp),
                               icon = R.drawable.ic_success,
                               isConfirm = true,
                               onClick = onConfirmClick
                           )
                           -3 -> Spacer(modifier = Modifier.size(80.dp))
                           else -> KeyboardButton(
                               modifier = Modifier.size(80.dp),
                               text = key.toString(),
                               onClick = { onNumberClick(key) }
                           )
                       }
                   }
               }
           }
       }

       Spacer(modifier = Modifier.width(8.dp))

       Column {
           KeyboardButton(
               modifier = Modifier.height(80.dp).width(120.dp),
               icon = R.drawable.baseline_backspace_24,
               onClick = onBackspaceClick
           )

           Spacer(modifier = Modifier.height(8.dp))

           KeyboardButton(
               modifier = Modifier.width(120.dp).weight(1f),
               icon = R.drawable.ic_success,
               isConfirm = true,
               onClick = onConfirmClick
           )
       }
   }
}

// Keyboard Button Composable
@Composable
fun KeyboardButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: Int? = null,
    isConfirm: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
            .background(
                color = if (isConfirm) Color.Black else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isConfirm) Color.Transparent else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        text?.let {
            Text(
                text = it,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        icon?.let {
            Icon(
                painter = painterResource(icon),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
                tint = if (isConfirm) Color.White else Color.Black
            )
        }
    }
}