package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.buttonGrayColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMenuContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    onBackAction: (() -> Unit)? = null,
    title: String = "",
    showSettingsModal: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onToggleShift: () -> Unit = {},
    shiftStarted: Boolean = false,
    isShiftButtonDisabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val modalSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    Column(
        modifier = modifier,
    ) {
        TopAppBar(
            expandedHeight = 48.dp,
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                ),
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
                )
            },
            navigationIcon = {
                onBackAction?.let {
                    IconButton(
                        onClick = it,
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.icon_back),
                                contentDescription = null,
                            )
                        },
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onOpenSettings,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                        )
                    },
                )
            },
        )

        content()
    }

    if (showSettingsModal) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = Modifier.fillMaxWidth(),
            sheetState = modalSheetState,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Configuración",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(
                        onClick = onDismissRequest,
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_close_24),
                                contentDescription = null,
                            )
                        },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLogout,
                    modifier =
                        Modifier
                            .height(72.dp)
                            .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = buttonGrayColor,
                        ),
                ) {
                    Text(
                        text = "Cambiar mesero",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (!isShiftButtonDisabled) onToggleShift() },
                    modifier =
                        Modifier
                            .height(72.dp)
                            .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = if (isShiftButtonDisabled) Color.Gray else buttonGrayColor,
                        ),
                    enabled = !isShiftButtonDisabled,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (shiftStarted) "Cerrar Turno" else "Abrir Turno",
                            color = if (isShiftButtonDisabled) Color.Gray else Color.Black,
                            style = MaterialTheme.typography.titleSmall,
                        )

                        if (isShiftButtonDisabled) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(desde POS)",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
