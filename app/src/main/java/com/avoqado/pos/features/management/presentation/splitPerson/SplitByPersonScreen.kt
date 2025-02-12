package com.avoqado.pos.features.management.presentation.splitPerson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.core.presentation.components.MainButton
import com.avoqado.pos.core.presentation.components.SelectableItemRow
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.ui.screen.ToolbarWithIcon

@Composable
fun SplitByPersonScreen(
    splitByPersonViewModel: SplitByPersonViewModel
) {

    val state by splitByPersonViewModel.state.collectAsStateWithLifecycle()

    SplitByPersonContent(
        onNavigateBack = splitByPersonViewModel::navigateBack,
        onPayTapped = splitByPersonViewModel::navigateToPayment,
        onChangeSplitSize = splitByPersonViewModel::updateSplitPartySize,
        onPersonSelected = splitByPersonViewModel::onItemSelected,
        totalSelectedAmount = state.totalSelectedAmount,
        totalPendingAmount = state.totalPendingAmount,
        splitPartySelected = state.splitPartySelected,
        splitPartySize = state.splitPartySize,
        splitPartyPaidSize = state.splitPartyPaidSize
    )
}


@Composable
fun SplitByPersonContent(
    onNavigateBack: () -> Unit = {},
    onPayTapped: () -> Unit = {},
    onChangeSplitSize: (Boolean) -> Unit = {},
    onPersonSelected: (Int) -> Unit = {},
    totalPendingAmount: String,
    totalSelectedAmount: String,
    splitPartySize: Int,
    splitPartyPaidSize: Int,
    splitPartySelected: List<Int>
) {
    val context = LocalContext.current
    val amountSplit by remember {
        derivedStateOf {
            "%.2f".format(totalPendingAmount.toDouble() / splitPartySize)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            ToolbarWithIcon(
                color = Color.Black,
                contentColor = Color.White,
                title = "Queda por pagar: \$$totalPendingAmount",
                iconAction = IconAction(
                    iconType = IconType.BACK,
                    flowStep = FlowStep.NAVIGATE_BACK,
                    context = context
                ),
                onAction = {
                    onNavigateBack()
                }
            )

            if (splitPartyPaidSize == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 28.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainButton(
                        modifier = Modifier.size(60.dp),
                        text = "-",
                        onClickR = {
                            onChangeSplitSize(false)
                        },
                        color = Color.White,
                        textColor = Color.Black
                    )

                    Text(
                        text = "$splitPartySize partes",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontFamily = AppFont.EffraFamily
                        )
                    )

                    MainButton(
                        modifier = Modifier.size(60.dp),
                        text = "+",
                        onClickR = {
                            onChangeSplitSize(true)
                        },
                        color = Color.White,
                        textColor = Color.Black
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = 16.dp
                )
        ) {
            items(count = splitPartySize) { index ->

                val isPaid = index < splitPartyPaidSize
                val isSelected = splitPartySelected.contains(index)

                SelectableItemRow(
                    label = "Persona ${index + 1}",
                    data = index,
                    isSelected = isSelected,
                    trailingLabel = if (isPaid) {
                        "Pagado"
                    } else {
                        "\$$amountSplit"
                    },
                    onItemTap = if (isPaid) {
                        null
                    } else {
                        {
                            onPersonSelected(it)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (splitPartySelected.isNotEmpty()) {
            MainButton(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 16.dp
                ).padding(bottom = 24.dp),
                onClickR = onPayTapped,
                text = "Pagar ${splitPartySelected.size} partes • \$$totalSelectedAmount"
            )
        }
    }
}

@Urovo9100DevicePreview
@Composable
fun SplitByPersonContentPreview() {
    AvoqadoTheme {
        SplitByPersonContent(
            totalPendingAmount = "555.00",
            totalSelectedAmount = "222.00",
            splitPartySize = 5,
            splitPartyPaidSize = 2,
            splitPartySelected = listOf(2, 3),
        )
    }
}