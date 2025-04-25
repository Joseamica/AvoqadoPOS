package com.avoqado.pos.core.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.enums.MmTypeCurrencyEnum
import com.menta.android.common_cross.util.Utils
import com.menta.android.keys.admin.core.repository.parametro.ParametroDB
import java.math.BigDecimal
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object Utils {
    fun incrementBatch(context: Context) {
        Thread {
            if (mustIncrementBatch(context)) {
                val parameterDB = ParametroDB(context)
                val batch: Int = parameterDB.getValueParam(BATCH).toInt() + 1
                parameterDB.setValueParam(BATCH, batch.toString())
                saveIncrementBatch(context)
            }
        }.start()
    }

    fun mustIncrementBatch(context: Context): Boolean = isNextDate(KEY_NEXT_INCREMENT_BATCH_DATE, context)

    fun saveIncrementBatch(context: Context) {
        saveDate(KEY_NEXT_INCREMENT_BATCH_DATE, context)
    }

    @SuppressLint("SimpleDateFormat")
    fun isNextDate(
        key: String?,
        context: Context,
    ): Boolean {
        try {
            val hourDateFormat: DateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT)
            val dateCurrent = hourDateFormat.format(Date())
            val dateSaved = getDataSave(key, context) ?: return true

            val currentDate = hourDateFormat.parse(dateCurrent)
            val savedDate = hourDateFormat.parse(dateSaved)

            if (currentDate != null && savedDate != null) {
                val ret = currentDate.compareTo(savedDate)
                return ret >= 0
            }
        } catch (e: ParseException) {
            Log.e(TAG, e.message!!)
            e.printStackTrace()
        }
        return false
    }

    @SuppressLint("SimpleDateFormat")
    fun saveDate(
        key: String?,
        context: Context,
    ) {
        val dateFormat: DateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT)
        val currentDate = Date()
        val days = 1
        val dateChange = generateNewDate(currentDate, days)
        val editor = Utils.createEncryptedSharedPreferences(PREF_TRANSACTIONAL_DATA, context).edit()
        editor.putString(key, dateFormat.format(dateChange))
        editor.apply()
    }

    private fun getDataSave(
        key: String?,
        context: Context,
    ): String? {
        val prefs = Utils.createEncryptedSharedPreferences(PREF_TRANSACTIONAL_DATA, context)
        return prefs.getString(key, null)
    }

    private fun generateNewDate(
        date: Date,
        days: Int,
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    @Composable
    fun UtilButtonView(
        text: String,
        color: Color,
        textColor: Color = Color.White,
        colorDisable: Color = Color.LightGray,
        enableButton: Boolean = true,
        shape: Shape = RoundedCornerShape(8.dp),
        onClickR: () -> Unit,
        drawableId: Int? = null,
    ) {
        Button(
            onClick = onClickR,
            shape = shape,
            enabled = enableButton,
            colors =
                ButtonDefaults.buttonColors(
                    disabledContainerColor = colorDisable,
                    containerColor = color,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(6.dp),
        ) {
            Text(text = text, color = textColor, fontSize = 16.sp)
            if (drawableId != null) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Image(
                    painterResource(id = drawableId),
                    contentDescription = EMPTY_STRING,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
            }
        }
    }

    /* Implementation:
     *      MmUtlAmountTextViewV2(
     *          amount = "0.0505",
     *          isVisible = visible,
     *          currencyType = "BTC",
     *          fullVisibility = true,
     *          fullCoinDisplay = true,
     *          maxDecimal = 6
     *      )
     */
    @Composable
    fun MmUtlAmountTextViewV2(
        modifier: Modifier = Modifier,
        amount: String,
        textColor: Color = Color.Black,
        currencyType: String = "$",
        isVisible: Boolean = true,
        fullVisibility: Boolean = false,
        baseSize: Int = BASE_SIZE_30,
        coinDisplay: Boolean = true,
        font: TextStyle = mumoSubTitleBold,
        currencyDisplay: Boolean = true,
        currencyFullDisplay: Boolean = false,
        maxDecimal: Int = 2,
    ) {
        val am =
            try {
                BigDecimal(amount).toPlainString()
            } catch (e: NumberFormatException) {
                "0"
            }

        val isDigital = 1 == 1
        val amountParts = am.split(".")
        var entero =
            (BigDecimal(amountParts[0]).abs().toPlainString())
                .reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()
        var decimal = if (amountParts.size > 1) amountParts[1] else "00"
        if (decimal.length < 2) decimal += "0"
        val bigInt = amountParts[0] != "0" || !isDigital || decimal == "00"
        val bigDecimal = (decimal.length > 2 || !bigInt || decimal != "00") && isDigital
        if (fullVisibility && !isVisible) {
            Text(
                text = "●  ●  ●  ● ",
            )
        } else {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    if (coinDisplay) {
                        if (currencyType in
                            listOf(
                                MmTypeCurrencyEnum.MXN.name,
                                MmTypeCurrencyEnum.USD.name,
                            )
                        ) {
                            Text(
                                color = textColor,
                                text = "$",
                                style =
                                    font.copy(
                                        fontSize = if (bigInt) baseSize.sp else (baseSize / INT_2).sp,
                                        color = textColor,
                                    ),
                            )
                        }
                    }
                    if (isVisible) {
                        if (maxDecimal == 0) {
                            Text(
                                color = textColor,
                                text = entero,
                                style = font,
                            )
                        } else {
                            if (bigInt) entero = "$entero."
                            Text(
                                color = textColor,
                                text = entero,
                                style =
                                    font.copy(
                                        fontSize = if (bigInt) baseSize.sp else (baseSize / INT_2).sp,
                                        color = textColor,
                                    ),
                            )
                            if (bigDecimal && !bigInt) decimal = ".$decimal"
                            Text(
                                text =
                                    truncateString(
                                        decimal,
                                        if (bigDecimal && !bigInt) maxDecimal + 1 else maxDecimal,
                                    ),
                                style =
                                    font.copy(
                                        fontSize = if (bigDecimal) baseSize.sp else (baseSize / INT_2).sp,
                                        color = textColor,
                                    ),
                            )
                        }
                    } else {
                        Text(
                            text = "●  ●  ●  ● ",
                        )
                    }
                }
                if (currencyDisplay) {
                    if (currencyFullDisplay) {
                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = currencyType,
                        style =
                            font.copy(
                                fontSize = if (bigInt) baseSize.sp else (baseSize / INT_2).sp,
                                color = textColor,
                            ),
                    )
                }
            }
        }
    }

    fun truncateString(
        input: String,
        maxLength: Int,
    ): String =
        if (input.length > maxLength) {
            input.substring(0, maxLength)
        } else {
            input
        }

    val mumoSubTitleBold =
        TextStyle(
            color = Color.Black,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.mulish_regular)),
        )

    private const val PATTERN_DATE_FORMAT = "dd/MM/yyyy"
    private const val PREF_TRANSACTIONAL_DATA = "transactional_data"
    private const val KEY_NEXT_INCREMENT_BATCH_DATE = "next_increment_batch_date"
    private const val TAG = "Utils"
    private const val BATCH = "batch"
    const val EMPTY_STRING = ""
    const val INT_2 = 2
    const val INT_3 = 3
    const val INT_4 = 4
    const val INT_5 = 5
    const val INT_60 = 60
    const val BASE_SIZE_15 = 15
    const val BASE_SIZE_16 = 16
    const val BASE_SIZE_25 = 25
    const val BASE_SIZE_30 = 11
    const val BASE_SIZE_35 = 35
}
