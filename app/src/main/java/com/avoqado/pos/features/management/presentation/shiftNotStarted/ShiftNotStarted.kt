package com.avoqado.pos.features.management.presentation.shiftNotStarted

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R

@Composable
fun ShiftNotStartedSheet(
    viewModel: ShiftNotStartedViewModel
) {

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = ModalBottomSheetDefaults.Elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Abrir turno",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (isLoading.not()) {
                            viewModel.onBack()
                        }
                    },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = null
                        )
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Para continuar se requiere abrir turno.",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.onOpenShift() },
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Abrir turno",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}