package com.avoqado.pos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductRow(number: Int, name: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .weight(0.12f)
                .background(color = Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number.toString(), color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 16.sp, modifier = Modifier.weight(0.8f))
            Text(text = "$$price", fontSize = 16.sp, modifier = Modifier.weight(0.2f))
        }
    }
}