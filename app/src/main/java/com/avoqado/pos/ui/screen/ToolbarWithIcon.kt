package com.avoqado.pos.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.views.MenuActivity

@Composable
fun ToolbarWithIcon(
    title: String,
    iconAction: IconAction? = null,
    onAction: () -> Unit = {},
    onActionSecond: () -> Unit = {},
    showSecondIcon: Boolean = false
) {
    TopAppBar(title = {
        Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(end = if (showSecondIcon) 46.dp else 0.dp),
                text = title,
                style = TextStyle(
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black
                )
            )
        }
    }, backgroundColor = Color.White, navigationIcon = {
        iconAction?.let {
            IconButton(onClick = {
                when (iconAction.flowStep) {
                    FlowStep.GO_TO_MENU -> {
                        val intent = Intent(iconAction.context, MenuActivity::class.java)
                        iconAction.context.startActivity(intent)
                    }

                    else -> {
                        onAction.invoke()
                    }
                }
            }) {
                when (iconAction.iconType) {
                    IconType.CANCEL -> {
                        Icon(painterResource(R.drawable.icon_home), contentDescription = null)
                    }

                    else -> {
                        Icon(
                            painterResource(if (!showSecondIcon) R.drawable.icon_home else R.drawable.icon_back),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }, actions = {
        if (!showSecondIcon) {
            IconButton(onClick = {
                onActionSecond()
            }) {
                Icon(painterResource(R.drawable.icon_note), contentDescription = "Note")
            }
        }
    }
    )
}