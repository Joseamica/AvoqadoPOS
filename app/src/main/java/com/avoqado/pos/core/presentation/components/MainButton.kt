package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.ui.theme.AvoqadoTheme
import com.avoqado.pos.util.Utils.EMPTY_STRING

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    colorDisable: Color = Color.LightGray,
    enableButton: Boolean = true,
    shape: Shape = RoundedCornerShape(10.dp),
    onClickR: () -> Unit,
    drawableId: Int? = null,
){
    Button(
        onClick = onClickR,
        shape = shape,
        enabled = enableButton,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = colorDisable,
            containerColor = color,
            contentColor = textColor
        ),
        modifier = modifier
    ) {
        Row (
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(
                text = text,
//            fontFamily = FontFamily(Font(R.font.source_sans_pro)),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 23.sp
            )

            if (drawableId != null) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Image(
                    painterResource(id = drawableId),
                    contentDescription = EMPTY_STRING,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainButton(){
    AvoqadoTheme {
        MainButton(
            text = "Pagar $777.00",
            onClickR = {}
        )
    }
}