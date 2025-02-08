package com.avoqado.pos.core.data.local

import android.content.Context
import com.avoqado.pos.core.data.network.models.Terminal
import com.google.gson.Gson

class SessionManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        const val VENUE_ID = "venue_id"
        const val TERMINAL_INFO = "terminal_info"
    }

    fun saveVenueId(venueId: String) {
        sharedPreferences
            .edit()
            .putString(VENUE_ID, venueId)
            .apply()
    }

    fun getVenueId(): String {
        return sharedPreferences
            .getString(VENUE_ID, "") ?: ""
    }

    fun saveTerminalInfo(terminal: Terminal) {
        sharedPreferences
            .edit()
            .putString(TERMINAL_INFO, Gson().toJson(terminal))
            .apply()
    }

    fun getTerminalInfo(): Terminal? {
        val terminalJson = sharedPreferences.getString(TERMINAL_INFO, "") ?: ""
        return if (terminalJson.isNotEmpty()) {
            Gson().fromJson(terminalJson, Terminal::class.java)
        } else {
            null
        }
    }
}