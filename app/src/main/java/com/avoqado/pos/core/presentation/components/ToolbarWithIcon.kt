package com.avoqado.pos.core.presentation.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithIcon(
    title: String,
    iconAction: IconAction? = null,
    onAction: () -> Unit = {},
    onActionSecond: () -> Unit = {},
    showSecondIcon: Boolean = false,
    secondIconRes: Int = R.drawable.icon_note,
    color: Color = Color.White,
    contentColor: Color = Color.Black,
) {
    TopAppBar(
        expandedHeight = 48.dp,
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = color,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
            ),
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Text(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .padding(
                                end = if (showSecondIcon) 0.dp else 46.dp,
                            ),
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(color = contentColor),
                )
            }
        },
        navigationIcon = {
            iconAction?.let {
                IconButton(onClick = {
                    when (iconAction.flowStep) {
                        FlowStep.GO_TO_MENU -> {

                        }

                        else -> {
                            onAction.invoke()
                        }
                    }
                }) {
                    when (iconAction.iconType) {
                        IconType.CANCEL -> {
                            Icon(painterResource(R.drawable.icon_home), contentDescription = null, tint = contentColor)
                        }

                        IconType.BACK -> {
                            Icon(painterResource(R.drawable.icon_back), contentDescription = null, tint = contentColor)
                        }
                    }
                }
            }
        },
        actions = {
            if (showSecondIcon) {
                IconButton(onClick = {
                    onActionSecond()
                }) {
                    Icon(painterResource(secondIconRes), contentDescription = "Note", tint = contentColor)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleToolbar(
    title: String,
    iconAction: IconAction? = null,
    onAction: () -> Unit = {},
    onActionSecond: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    isRefreshing: Boolean = false,
    enableSecondAction: Boolean = true,
) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
        title = {
            onActionSecond?.let {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 50.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    MainButton(
                        modifier = Modifier.height(50.dp),
                        text = title,
                        onClickR = it,
                        contentPadding = PaddingValues(vertical = 4.dp),
                        enableButton = enableSecondAction
                    )
                }
            }
        },
        actions = {
            onRefresh?.let { refreshAction ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = refreshAction,
                        enabled = !isRefreshing,
                        content = {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.Black
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                                    contentDescription = "Refresh",
                                    tint = Color.Black
                                )
                            }
                        },
                    )
                }
            }
        },
        navigationIcon = {
            iconAction?.let {
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        when (iconAction.flowStep) {
                            FlowStep.GO_TO_MENU -> {

                            }

                            else -> {
                                onAction.invoke()
                            }
                        }
                    },
                ) {
                    when (iconAction.iconType) {
                        IconType.CANCEL -> {
                            Icon(painterResource(R.drawable.icon_home), contentDescription = null)
                        }

                        IconType.BACK -> {
                            Icon(painterResource(R.drawable.icon_back), contentDescription = null)
                        }
                    }
                }
            }
        },
    )
}
