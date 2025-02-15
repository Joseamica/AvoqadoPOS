package com.avoqado.pos.core.data.local

import android.content.Context
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.data.network.models.Terminal
import com.avoqado.pos.core.presentation.model.VenueInfo
import com.google.gson.Gson

class SessionManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        const val VENUE_ID = "venue_id"
        const val VENUE_INFO = "venue_info"
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

    fun saveVenueInfo(venue: NetworkVenue) {
        sharedPreferences.edit()
            .putString(VENUE_INFO, Gson().toJson(venue))
            .apply()
    }

    fun getVenueInfo(): NetworkVenue? {
        val venueJson = sharedPreferences.getString(VENUE_INFO, "") ?: ""
        return if (venueJson.isNotEmpty()) {
            Gson().fromJson(venueJson, NetworkVenue::class.java)
        } else {
            null
        }
    }
}