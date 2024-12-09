package com.avoqadoapp.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.menta.android.common_cross.util.Utils
import com.menta.android.keys.admin.core.repository.parametro.ParametroDB
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

    fun mustIncrementBatch(context: Context): Boolean {
        return isNextDate(KEY_NEXT_INCREMENT_BATCH_DATE, context)
    }

    fun saveIncrementBatch(context: Context) {
        saveDate(KEY_NEXT_INCREMENT_BATCH_DATE, context)
    }

    @SuppressLint("SimpleDateFormat")
    fun isNextDate(key: String?, context: Context): Boolean {
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
    fun saveDate(key: String?, context: Context) {
        val dateFormat: DateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT)
        val currentDate = Date()
        val days = 1
        val dateChange = generateNewDate(currentDate, days)
        val editor = Utils.createEncryptedSharedPreferences(PREF_TRANSACTIONAL_DATA, context).edit()
        editor.putString(key, dateFormat.format(dateChange))
        editor.apply()
    }

    private fun getDataSave(key: String?, context: Context): String? {
        val prefs = Utils.createEncryptedSharedPreferences(PREF_TRANSACTIONAL_DATA, context)
        return prefs.getString(key, null)
    }

    private fun generateNewDate(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    private const val PATTERN_DATE_FORMAT = "dd/MM/yyyy"
    private const val PREF_TRANSACTIONAL_DATA = "transactional_data"
    private const val KEY_NEXT_INCREMENT_BATCH_DATE = "next_increment_batch_date"
    private const val TAG = "Utils"
    private const val BATCH = "batch"


}