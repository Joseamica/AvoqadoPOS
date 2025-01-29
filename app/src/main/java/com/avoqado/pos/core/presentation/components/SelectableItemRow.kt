package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.ui.theme.AvoqadoTheme
import com.avoqado.pos.ui.theme.selectedItemColor
import com.avoqado.pos.ui.theme.unselectedItemColor

@Composable
fun<T> SelectableItemRow(
    isSelected: Boolean = false,
    label: String,
    data: T,
    onItemTap: (T) -> Unit,
    trailingLabel: String? = null
){
    val baseColor = if (isSelected) selectedItemColor else unselectedItemColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onItemTap(data)
            }
            .border(
                width = 1.dp,
                color = baseColor,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Icon(
                painter = painterResource(
                    if (isSelected) {
                        R.drawable.baseline_check_circle_24
                    } else {
                        R.drawable.baseline_add_circle_outline_24
                    }
                ),
                contentDescription = "",
                tint = baseColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = baseColor
                    )
                )
            }


            trailingLabel?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = baseColor
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSelectableIconRow(){
    AvoqadoTheme {
        SelectableItemRow(
            label = "Pan Francés",
            trailingLabel = "$2.10",
            data = "123",
            onItemTap = {}
        )
    }
}

@Preview
@Composable
fun PreviewSelectedSelectableIconRow(){
    AvoqadoTheme {
        SelectableItemRow(
            isSelected = true,
            label = "Pan Francés",
            trailingLabel = "$2.10",
            data = "123",
            onItemTap = {}
        )
    }
}
