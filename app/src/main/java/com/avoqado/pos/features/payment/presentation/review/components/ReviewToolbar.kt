package com.avoqado.pos.features.payment.presentation.review.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewToolbar(
    subtotal: String,
    iconAction: IconAction? = null,
    onAction: () -> Unit = {},
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black,
        ),
        title = {
            // Payment Card in the toolbar
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.7.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_card),
                        contentDescription = "Payment Amount",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp),
                    )
                    
                    Text(
                        text = "monto a pagar",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "$${subtotal} MXN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        },
        navigationIcon = {
            iconAction?.let {
                IconButton(onClick = {
                    when (iconAction.flowStep) {
                        FlowStep.GO_TO_MENU -> {
                            // Handle menu navigation if needed
                        }
                        else -> {
                            onAction.invoke()
                        }
                    }
                }) {
                    when (iconAction.iconType) {
                        IconType.CANCEL -> {
                            Icon(painterResource(R.drawable.icon_back), contentDescription = null, tint = Color.Black)
                        }
                        IconType.BACK -> {
                            Icon(painterResource(R.drawable.icon_back), contentDescription = null, tint = Color.Black)
                        }
                    }
                }
            }
        }
    )
}
