package com.avoqado.pos.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.ui.theme.primary
import com.avoqado.pos.views.InputAmountActivity
import com.avoqado.pos.views.MenuActivity

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToolbarWithIcon(title: String, iconAction: IconAction? = null) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    text = title,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        },
        backgroundColor = primary,
        navigationIcon = {
            iconAction?.let {
                IconButton(onClick = {
                    when (iconAction.flowStep) {
                        FlowStep.GO_TO_MENU -> {
                            val intent = Intent(iconAction.context, MenuActivity::class.java)
                            iconAction.context.startActivity(intent)
                        }

                        else -> {
                        }
                    }
                }
                ) {
                    when (iconAction.iconType) {
                        IconType.CANCEL -> {
                            Icon(Icons.Filled.Close, contentDescription = null)
                        }

                        else -> {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun Preview() {
    ToolbarWithIcon(
        "Transacciones",
        IconAction(
            IconType.CANCEL,
            FlowStep.GO_TO_MENU,
            InputAmountActivity()
        )
    )
}