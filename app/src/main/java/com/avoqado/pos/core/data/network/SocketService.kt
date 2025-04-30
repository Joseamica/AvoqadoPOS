package com.avoqado.pos.core.data.network

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.avoqado.pos.core.data.network.models.PaymentUpdateMessage
import com.avoqado.pos.core.data.network.models.ShiftUpdateMessage
import com.avoqado.pos.core.domain.mappers.ShiftMapper
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Service responsible for maintaining Socket.IO connections across the app lifecycle.
 * This ensures connections aren't dropped when navigating between screens.
 */
class SocketService : Service() {
    private val binder = SocketBinder()
    private var isServiceStarted = false

    // Expose the socket manager instance for direct access
    val socketManager: SocketIOManager = SocketIOManager
    
    // Expose flows for collecting events
    val messageFlow: SharedFlow<PaymentUpdateMessage> = socketManager.messageFlow
    val venueMessageFlow: SharedFlow<PaymentUpdateMessage> = socketManager.venueMessageFlow
    val shiftMessageFlow: SharedFlow<ShiftUpdateMessage> = socketManager.shiftMessageFlow
    val isConnected: StateFlow<Boolean> = socketManager.isWebSocketConnected

    inner class SocketBinder : Binder() {
        fun getService(): SocketService = this@SocketService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SocketService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceStarted) {
            Log.d(TAG, "SocketService started")
            // Initialize the socket connection when service starts
            socketManager.connect(socketManager.getServerUrl())
            isServiceStarted = true
        }
        return START_STICKY
    }

    /**
     * Join a venue room to receive venue-level updates
     */
    fun joinVenueRoom(venueId: String) {
        if (venueId.isNotEmpty()) {
            socketManager.joinMobileRoom(venueId)
        }
    }

    /**
     * Join a specific table room to receive table-level updates
     */
    fun joinTableRoom(venueId: String, tableId: String) {
        if (venueId.isNotEmpty() && tableId.isNotEmpty()) {
            socketManager.subscribeToTable(venueId, tableId)
        }
    }

    /**
     * Leave a specific table room
     */
    fun leaveTableRoom() {
        socketManager.unsubscribe()
    }

    /**
     * Leave a venue room
     */
    fun leaveVenueRoom(venueId: String) {
        if (venueId.isNotEmpty()) {
            socketManager.leaveMobileRoom(venueId)
        }
    }

    /**
     * Maps a ShiftUpdateMessage to a domain Shift model
     */
    fun mapShiftToDomain(shiftUpdateMessage: ShiftUpdateMessage) = 
        ShiftMapper.mapToDomain(shiftUpdateMessage)

    override fun onDestroy() {
        Log.d(TAG, "SocketService destroyed")
        // Disconnect only when service is destroyed
        socketManager.disconnect()
        isServiceStarted = false
        super.onDestroy()
    }

    companion object {
        private const val TAG = "SocketService"
    }
} 