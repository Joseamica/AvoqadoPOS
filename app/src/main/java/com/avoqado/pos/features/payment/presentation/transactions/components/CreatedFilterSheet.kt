@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CreatedFilterSheet(
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    onDismiss: () -> Unit = {},
    onApplyFilter: (Pair<Long?, Long?>) -> Unit = {},
    preSelectedDates: Pair<Long?, Long?> = Pair(null, null)
) {
    val datesSelected = remember {
        mutableStateOf(preSelectedDates)
    }

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
                    text = "Filtrar por fecha de creación",
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                DatePickerButton(
                    modifier = Modifier.height(40.dp).weight(1f),
                    hint = "Desde",
                    selectedDate = datesSelected.value.first,
                    onDateChange = {
                        datesSelected.value = datesSelected.value.copy(first = it)
                    },
                    maxDate = datesSelected.value.second?.let {
                        Date().apply { time = it }
                    }
                )

                Text(
                    "-",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(16.dp)
                )

                DatePickerButton(
                    modifier = Modifier.height(40.dp).weight(1f),
                    hint = "Hasta",
                    selectedDate = datesSelected.value.second,
                    onDateChange = {
                        datesSelected.value = datesSelected.value.copy(second = it)
                    },
                    minDate =  datesSelected.value.first?.let {
                        Date().apply { time = it }
                    }
                )
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
                        onApplyFilter(datesSelected.value)
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

@Composable
fun DatePickerButton(
    modifier: Modifier,
    hint: String,
    selectedDate: Long?,
    onDateChange: (Long) -> Unit,
    minDate: Date? = null,
    maxDate: Date? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        onClick = {
            showDatePicker = !showDatePicker
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = selectedDate?.let { convertMillisToDate(it) } ?: hint,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Black
                ),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.DateRange,
                tint = Color.Black,
                contentDescription = "Select date"
            )
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { date -> date?.let(onDateChange) },
            onDismiss = { showDatePicker = false },
            limitDates = Pair(minDate, maxDate),
            selectedDate = selectedDate
        )
    }

}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    selectedDate: Long? = null,
    limitDates: Pair<Date?, Date?>? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate,
        selectableDates = limitDates?.let {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val startLimit = it.first?.let { utcTimeMillis > it.time } ?: true
                    val endLimit = it.second?.let { utcTimeMillis < it.time } ?: true
                    return startLimit && endLimit
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val startLimit = it.first?.let {
                        val calendar = Calendar.getInstance()
                        calendar.time = it
                        year >= calendar.get(Calendar.YEAR)
                    } ?: true
                    val endLimit = it.second?.let {
                        val calendar = Calendar.getInstance()
                        calendar.time = it
                        year <= calendar.get(Calendar.YEAR)
                    } ?: true
                    return startLimit && endLimit
                }
            }
        } ?: DatePickerDefaults.AllDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(
        Date(millis)
    )
}

@Urovo9100DevicePreview
@Composable
fun CreatedFilterSheetPreview() {
    AvoqadoTheme {
        CreatedFilterSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = true
            ),
        )
    }
}