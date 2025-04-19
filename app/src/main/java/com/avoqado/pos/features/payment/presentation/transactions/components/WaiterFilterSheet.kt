package com.avoqado.pos.features.payment.presentation.transactions.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaiterFilterSheet(
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    onDismiss: () -> Unit = {},
    onApplyFilter: (List<String>) -> Unit = {},
    waiterList: List<Pair<String, String>> = emptyList(),
    preSelectedWaiters: List<String> = emptyList()
) {
    // Usar mutableStateListOf para que los cambios en la lista actualicen la UI
    val selectedWaiters = remember { mutableStateListOf<String>().apply { addAll(preSelectedWaiters) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        sheetState = sheetState
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrar por mesero",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(R.drawable.baseline_close_24),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(
                    items = waiterList,
                    key = { it.first } // Usar el ID del mesero como clave única
                ) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedWaiters.contains(item.first)) {
                                    selectedWaiters.remove(item.first)
                                } else {
                                    selectedWaiters.add(item.first)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.second,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = selectedWaiters.contains(item.first),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedWaiters.add(item.first)
                                } else {
                                    selectedWaiters.remove(item.first)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Black,
                                uncheckedColor = Color.Gray,
                                checkmarkColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    onClick = {
                        selectedWaiters.clear()
                        onApplyFilter(emptyList())
                    }
                ) {
                    Text(
                        text = "Limpiar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        onApplyFilter(selectedWaiters.toList())
                    }
                ) {
                    Text(
                        text = "Aplicar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Urovo9100DevicePreview
@Composable
fun WaiterFilterSheetPreview() {
    AvoqadoTheme {
        WaiterFilterSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = true
            ),
            waiterList = listOf(
                Pair("1","Diego"),
                Pair("2","Carlos"), // Asegurar IDs únicos en el preview
                Pair("3","Ana"),
                Pair("4","María"),
            ),
            preSelectedWaiters = listOf("1","3")
        )
    }
}