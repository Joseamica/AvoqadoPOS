package com.avoqado.pos.core.data.network

import android.util.Log
import com.avoqado.pos.core.data.network.models.PaymentUpdateMessage
import com.avoqado.pos.core.data.network.models.ShiftUpdateMessage
import com.avoqado.pos.core.data.network.AppConfig
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

object SocketIOManager {
    private var socket: Socket? = null
    private var currentRoomId: JSONObject? = null
    private var currentVenueRoomId: String? = null

    private val _messageFlow = MutableSharedFlow<PaymentUpdateMessage>(replay = 0)
    val messageFlow: SharedFlow<PaymentUpdateMessage> = _messageFlow.asSharedFlow()

    // New flow for venue-level events with rate limiting
    private val _venueMessageFlow = MutableSharedFlow<PaymentUpdateMessage>(replay = 0)
    val venueMessageFlow: SharedFlow<PaymentUpdateMessage> = _venueMessageFlow.asSharedFlow()

    // New flow for shift events
    private val _shiftMessageFlow = MutableSharedFlow<ShiftUpdateMessage>(replay = 0)
    val shiftMessageFlow: SharedFlow<ShiftUpdateMessage> = _shiftMessageFlow.asSharedFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gson = Gson()
    private var serverUrl: String? = null

    private val _isWebSocketConnected = MutableStateFlow(false)
    val isWebSocketConnected: StateFlow<Boolean> = _isWebSocketConnected.asStateFlow()
    
    // Rate limiting for venue updates
    private var lastVenueUpdateTime = 0L
    private var pendingVenueUpdate: PaymentUpdateMessage? = null
    private var venueUpdateJob: Job? = null

    fun getServerUrl(): String {
        // If we already have a stored URL, return it
        if (!serverUrl.isNullOrEmpty()) {
            return serverUrl!!
        }

        // Get the URL from the centralized AppConfig
        return AppConfig.getSocketUrl()
    }

    fun connect(url: String) {
        serverUrl = url
        if (socket == null) {
            try {
                // Configure socket options with reconnection settings
                val options =
                    IO.Options
                        .builder()
                        .setReconnection(true)
                        .setReconnectionAttempts(AppConfig.getSocketReconnectAttempts())
                        .setReconnectionDelay(AppConfig.getSocketReconnectDelayMs())
                        .build()

                socket = IO.socket(url, options)
                socket?.connect()

                // Listen for connection events
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIO", "Connected to $url")
                    _isWebSocketConnected.value = true

                    // Resubscribe to room if there was one
                    if (currentRoomId != null) {
                        socket?.emit("joinRoom", currentRoomId)
                        Log.d("SocketIO", "Resubscribed to room after reconnect: $currentRoomId")
                    }

                    // Resubscribe to venue room if there was one
                    if (currentVenueRoomId != null) {
                        socket?.emit(
                            "joinMobileRoom",
                            JSONObject().apply {
                                put("venueId", currentVenueRoomId)
                            },
                        )
                        Log.d("SocketIO", "Resubscribed to venue room after reconnect: $currentVenueRoomId")
                    }
                }

                socket?.on(Socket.EVENT_DISCONNECT) {
                    Log.d("SocketIO", "Disconnected from server")
                    _isWebSocketConnected.value = false
                }

                socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e("SocketIO", "Connection error: ${args.firstOrNull()}")
                    _isWebSocketConnected.value = false
                }
            } catch (e: URISyntaxException) {
                Log.e("SocketIO", "Socket connection error: ${e.message}")
                _isWebSocketConnected.value = false
            }
        } else {
            Log.d("SocketIO", "Socket already initialized")
        }
    }

    fun subscribeToTable(
        venueId: String,
        billName: String,
    ) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot subscribe - socket not initialized")
            serverUrl?.let { connect(it) } // Try to reconnect if we have a URL
            return
        }

        val newRoomId =
            JSONObject().apply {
                put("venueId", venueId)
                put("table", billName)
            }

        // Check if we're already subscribed to this room (proper JSONObject comparison)
        if (currentRoomId != null &&
            currentRoomId!!.optString("venueId") == venueId &&
            currentRoomId!!.optString("table") == billName
        ) {
            Log.d("SocketIO", "Already subscribed to room: $newRoomId")
            return // Already subscribed to this room
        }

        // Unsubscribe from previous room if needed
        unsubscribe()

        socket?.emit("joinRoom", newRoomId)
        currentRoomId = newRoomId
        Log.d("SocketIO", "Subscribed to room: $newRoomId")

        // Register for updatePos events
        socket?.on("updatePos", onUpdateOrder)
    }

    fun joinMobileRoom(venueId: String) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot join mobile room - socket not initialized")
            serverUrl?.let { connect(it) }
            return
        }

        if (currentVenueRoomId == venueId) {
            Log.d("SocketIO", "Already joined venue room: $venueId")
            return
        }

        // Leave previous venue room if any
        if (currentVenueRoomId != null) {
            leaveMobileRoom(currentVenueRoomId!!)
        }

        val roomData =
            JSONObject().apply {
                put("venueId", venueId)
            }

        socket?.emit("joinMobileRoom", roomData)
        currentVenueRoomId = venueId
        Log.d("SocketIO", "Joined venue room: $venueId")

        // Register for venue-level updatePos events
        socket?.on("updatePos", onVenueUpdate)

        // Register for shift update events
        socket?.on("shiftUpdate", onShiftUpdate)
    }

    fun leaveMobileRoom(venueId: String) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot leave mobile room - socket not initialized")
            return
        }

        val roomData =
            JSONObject().apply {
                put("venueId", venueId)
            }

        socket?.emit("leaveMobileRoom", roomData)
        socket?.off("updatePos", onVenueUpdate)
        socket?.off("shiftUpdate", onShiftUpdate)
        currentVenueRoomId = null
        
        // Cancel any pending update job
        venueUpdateJob?.cancel()
        pendingVenueUpdate = null
        
        Log.d("SocketIO", "Left venue room: $venueId")
    }

    fun unsubscribe() {
        if (currentRoomId != null) {
            socket?.emit("leaveRoom", currentRoomId)
            Log.d("SocketIO", "Unsubscribed from room: $currentRoomId")
            socket?.off("updatePos", onUpdateOrder) // Remove listener
            currentRoomId = null
        }
    }

    private val onUpdateOrder =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received updatePos event for table")
            if (args.isEmpty() || args[0] == null) {
                Log.e("SocketIO", "Empty or null args in updatePos event")
                return@Listener
            }

            val rawData = args[0]
            Log.d("SocketIO", "Raw data received: $rawData")

            try {
                if (rawData !is JSONObject) {
                    Log.e("SocketIO", "Received data is not a JSONObject: ${rawData::class.java.name}")
                    return@Listener
                }
                val data = rawData as JSONObject
                Log.d("SocketIO", "Parsed initial JSONObject: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val dataField = data.opt("data")
                    if (dataField !is JSONObject) {
                        Log.e("SocketIO", "'data' field is not a JSONObject: ${dataField?.javaClass?.name}")
                        return@Listener
                    }
                    val paymentJson = dataField as JSONObject
                    Log.d("SocketIO", "Payment JSON: $paymentJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(paymentJson.toString(), PaymentUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed PaymentUpdateMessage: $message")
                            _messageFlow.emit(message)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            Log.e("SocketIO", "GSON parsing error for PaymentUpdateMessage: $paymentJson", e)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error emitting parsed PaymentUpdateMessage", e)
                        }
                    }
                } else {
                    Log.w("SocketIO", "Received updatePos does not contain 'data' field: $data")
                    
                    // Handle the case where the message format is simple (billId and status only)
                    if (data.has("billId") && data.has("status")) {
                        val billId = data.optString("billId")
                        val status = data.optString("status")
                        
                        Log.d("SocketIO", "Processing simplified message format: billId=$billId, status=$status")
                        
                        coroutineScope.launch {
                            try {
                                // Create a simplified PaymentUpdateMessage
                                val message = PaymentUpdateMessage(
                                    billId = billId,
                                    status = status
                                )
                                Log.d("SocketIO", "Created PaymentUpdateMessage from simplified format: $message")
                                _messageFlow.emit(message)
                            } catch (e: Exception) {
                                Log.e("SocketIO", "Error emitting simplified PaymentUpdateMessage", e)
                            }
                        }
                    } else {
                        Log.e("SocketIO", "Received data is missing required fields (billId and status): $data")
                    }
                }
            } catch (e: org.json.JSONException) {
                Log.e("SocketIO", "Error parsing initial JSONObject for updatePos", e)
            } catch (e: Exception) {
                Log.e("SocketIO", "Unexpected error handling updatePos socket event", e)
            }
        }

    // New listener for venue-level events with rate limiting
    private val onVenueUpdate =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received updatePos event for venue")
            if (args.isEmpty() || args[0] == null) {
                Log.e("SocketIO", "Empty or null args in venue updatePos event")
                return@Listener
            }

            val rawData = args[0]
            Log.d("SocketIO", "Raw data received for venue: $rawData")

            try {
                if (rawData !is JSONObject) {
                    Log.e("SocketIO", "Received venue data is not a JSONObject: ${rawData::class.java.name}")
                    return@Listener
                }
                val data = rawData as JSONObject
                Log.d("SocketIO", "Parsed initial venue JSONObject: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val dataField = data.opt("data")
                    if (dataField !is JSONObject) {
                        Log.e("SocketIO", "'data' field in venue update is not a JSONObject: ${dataField?.javaClass?.name}")
                        return@Listener
                    }
                    val paymentJson = dataField as JSONObject
                    Log.d("SocketIO", "Venue payment JSON: $paymentJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(paymentJson.toString(), PaymentUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed venue PaymentUpdateMessage: $message")

                            // Apply rate limiting to venue updates
                            processVenueUpdate(message)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            Log.e("SocketIO", "GSON parsing error for venue PaymentUpdateMessage: $paymentJson", e)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error processing parsed venue PaymentUpdateMessage", e)
                        }
                    }
                } else {
                    Log.w("SocketIO", "Received venue updatePos does not contain 'data' field: $data")
                    
                    // Handle the case where the message format is simple (billId and status only)
                    if (data.has("billId") && data.has("status")) {
                        val billId = data.optString("billId")
                        val status = data.optString("status")
                        
                        Log.d("SocketIO", "Processing simplified venue message format: billId=$billId, status=$status")
                        
                        coroutineScope.launch {
                            try {
                                // Create a simplified PaymentUpdateMessage
                                val message = PaymentUpdateMessage(
                                    billId = billId,
                                    status = status
                                )
                                Log.d("SocketIO", "Created venue PaymentUpdateMessage from simplified format: $message")
                                processVenueUpdate(message)
                            } catch (e: Exception) {
                                Log.e("SocketIO", "Error processing simplified venue PaymentUpdateMessage", e)
                            }
                        }
                    } else {
                        Log.e("SocketIO", "Received venue data is missing required fields (billId and status): $data")
                    }
                }
            } catch (e: org.json.JSONException) {
                Log.e("SocketIO", "Error parsing initial JSONObject for venue updatePos", e)
            } catch (e: Exception) {
                Log.e("SocketIO", "Unexpected error handling venue updatePos socket event", e)
            }
        }
        
    // Rate-limited processing of venue updates to prevent overwhelming the system
    private fun processVenueUpdate(message: PaymentUpdateMessage) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastVenueUpdateTime
        
        // If not enough time has elapsed since the last update, delay this one
        if (timeSinceLastUpdate < AppConfig.getSocketUpdateIntervalMs()) {
            // Cancel any already pending update
            venueUpdateJob?.cancel()
            
            // Store this as the pending update (latest one wins)
            pendingVenueUpdate = message
            
            // Schedule the update for later
            venueUpdateJob = coroutineScope.launch {
                val delayTime = AppConfig.getSocketUpdateIntervalMs() - timeSinceLastUpdate
                Log.d("SocketIO", "Rate limiting venue update, will emit in ${delayTime}ms")
                delay(delayTime)
                
                pendingVenueUpdate?.let { update ->
                    Log.d("SocketIO", "Emitting delayed venue update: $update")
                    _venueMessageFlow.emit(update)
                    lastVenueUpdateTime = System.currentTimeMillis()
                    pendingVenueUpdate = null
                }
            }
        } else {
            // Emit immediately if enough time has passed
            coroutineScope.launch {
                Log.d("SocketIO", "Emitting venue update immediately")
                _venueMessageFlow.emit(message)
                lastVenueUpdateTime = currentTime
                pendingVenueUpdate = null
            }
        }
    }

    // New listener for shift update events
    private val onShiftUpdate =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received shift update event")
            if (args.isEmpty() || args[0] == null) {
                Log.e("SocketIO", "Empty or null args in shift update event")
                return@Listener
            }

            val rawData = args[0]
            Log.d("SocketIO", "Raw data received for shift: $rawData")

            try {
                if (rawData !is JSONObject) {
                    Log.e("SocketIO", "Received shift data is not a JSONObject: ${rawData::class.java.name}")
                    return@Listener
                }
                val data = rawData as JSONObject
                Log.d("SocketIO", "Parsed initial shift JSONObject: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val dataField = data.opt("data")
                    if (dataField !is JSONObject) {
                        Log.e("SocketIO", "'data' field in shift update is not a JSONObject: ${dataField?.javaClass?.name}")
                        return@Listener
                    }
                    val shiftJson = dataField as JSONObject
                    Log.d("SocketIO", "Shift JSON: $shiftJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(shiftJson.toString(), ShiftUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed ShiftUpdateMessage: $message")
                            _shiftMessageFlow.emit(message)
                        } catch (e: com.google.gson.JsonSyntaxException) {
                            Log.e("SocketIO", "GSON parsing error for ShiftUpdateMessage: $shiftJson", e)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error emitting parsed ShiftUpdateMessage", e)
                        }
                    }
                } else {
                    Log.w("SocketIO", "Received shift update does not contain 'data' field: $data")
                }
            } catch (e: org.json.JSONException) {
                Log.e("SocketIO", "Error parsing initial JSONObject for shift update", e)
            } catch (e: Exception) {
                Log.e("SocketIO", "Unexpected error handling shift socket event", e)
            }
        }

    fun disconnect() {
        unsubscribe()

        // Also leave venue room if needed
        if (currentVenueRoomId != null) {
            leaveMobileRoom(currentVenueRoomId!!)
        }

        socket?.disconnect()
        socket = null
        Log.d("SocketIO", "Disconnected from WebSocket")
    }

    // Helper method to check connection status
    fun isConnected(): Boolean = socket?.connected() == true
}
