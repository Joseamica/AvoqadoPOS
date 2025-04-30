package com.avoqado.pos.core.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.TimeUnit

/**
 * Singleton class to manage application configuration throughout the app.
 * This provides a single source of truth for all configurable values.
 */
object AppConfig {
    private const val PREFS_NAME = "avoqado_app_config"
    
    // Keys for different config types
    private const val KEY_API_BASE_URL = "api_base_url"
    private const val KEY_SOCKET_URL = "socket_url"
    private const val KEY_NETWORK_TIMEOUT = "network_timeout"
    private const val KEY_SOCKET_RECONNECT_ATTEMPTS = "socket_reconnect_attempts"
    private const val KEY_SOCKET_RECONNECT_DELAY = "socket_reconnect_delay"
    private const val KEY_SOCKET_UPDATE_INTERVAL = "socket_update_interval"
    
    // Default values
    private const val DEFAULT_SERVER_URL = "https://1c87-189-203-45-177.ngrok-free.app"
    private const val DEFAULT_API_PATH = "/v1/"
    private const val DEFAULT_NETWORK_TIMEOUT_SECONDS = 60L  // Reduced from 120s
    private const val DEFAULT_SOCKET_RECONNECT_ATTEMPTS = 10
    private const val DEFAULT_SOCKET_RECONNECT_DELAY_MS = 1000L
    private const val DEFAULT_SOCKET_UPDATE_INTERVAL_MS = 1000L
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize AppConfig with application context.
     * Must be called before using any other methods.
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // ------------------- Server URL Configuration -------------------
    
    /**
     * Returns the base URL for REST API calls (including the path).
     */
    fun getApiBaseUrl(): String {
        val baseUrl = getServerUrl()
        return if (baseUrl.endsWith("/")) {
            baseUrl + "v1/"
        } else {
            "$baseUrl/v1/"
        }
    }
    
    /**
     * Returns the socket server URL (without the path)
     */
    fun getSocketUrl(): String {
        return prefs?.getString(KEY_SOCKET_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }
    
    /**
     * Returns the base server URL (without the path)
     */
    fun getServerUrl(): String {
        return prefs?.getString(KEY_API_BASE_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }
    
    /**
     * Set a new API base URL (without the path)
     */
    fun setServerUrl(url: String) {
        prefs?.edit {
            putString(KEY_API_BASE_URL, url)
        }
    }
    
    /**
     * Set a new socket server URL
     */
    fun setSocketUrl(url: String) {
        prefs?.edit {
            putString(KEY_SOCKET_URL, url)
        }
    }
    
    // ------------------- Network Configuration -------------------
    
    /**
     * Returns the network timeout in seconds for HTTP requests
     */
    fun getNetworkTimeoutSeconds(): Long {
        return prefs?.getLong(KEY_NETWORK_TIMEOUT, DEFAULT_NETWORK_TIMEOUT_SECONDS) 
            ?: DEFAULT_NETWORK_TIMEOUT_SECONDS
    }
    
    /**
     * Set the network timeout in seconds for HTTP requests
     */
    fun setNetworkTimeoutSeconds(seconds: Long) {
        prefs?.edit {
            putLong(KEY_NETWORK_TIMEOUT, seconds)
        }
    }
    
    // ------------------- Socket Configuration -------------------
    
    /**
     * Get the number of reconnection attempts for socket connections
     */
    fun getSocketReconnectAttempts(): Int {
        return prefs?.getInt(KEY_SOCKET_RECONNECT_ATTEMPTS, DEFAULT_SOCKET_RECONNECT_ATTEMPTS)
            ?: DEFAULT_SOCKET_RECONNECT_ATTEMPTS
    }
    
    /**
     * Set the number of reconnection attempts for socket connections
     */
    fun setSocketReconnectAttempts(attempts: Int) {
        prefs?.edit {
            putInt(KEY_SOCKET_RECONNECT_ATTEMPTS, attempts)
        }
    }
    
    /**
     * Get the delay between reconnection attempts in milliseconds
     */
    fun getSocketReconnectDelayMs(): Long {
        return prefs?.getLong(KEY_SOCKET_RECONNECT_DELAY, DEFAULT_SOCKET_RECONNECT_DELAY_MS)
            ?: DEFAULT_SOCKET_RECONNECT_DELAY_MS
    }
    
    /**
     * Set the delay between reconnection attempts in milliseconds
     */
    fun setSocketReconnectDelayMs(delayMs: Long) {
        prefs?.edit {
            putLong(KEY_SOCKET_RECONNECT_DELAY, delayMs)
        }
    }
    
    /**
     * Get the minimum interval between emitted socket updates in milliseconds
     */
    fun getSocketUpdateIntervalMs(): Long {
        return prefs?.getLong(KEY_SOCKET_UPDATE_INTERVAL, DEFAULT_SOCKET_UPDATE_INTERVAL_MS)
            ?: DEFAULT_SOCKET_UPDATE_INTERVAL_MS
    }
    
    /**
     * Set the minimum interval between emitted socket updates in milliseconds
     */
    fun setSocketUpdateIntervalMs(intervalMs: Long) {
        prefs?.edit {
            putLong(KEY_SOCKET_UPDATE_INTERVAL, intervalMs)
        }
    }
    
    // For backward compatibility with existing code using ServerConfig
    object ServerConfig {
        fun getApiBaseUrl() = AppConfig.getApiBaseUrl()
        fun getSocketUrl() = AppConfig.getSocketUrl()
        fun getServerUrl() = AppConfig.getServerUrl()
        fun setServerUrl(url: String) = AppConfig.setServerUrl(url)
        fun setSocketUrl(url: String) = AppConfig.setSocketUrl(url)
        fun initialize(context: Context) = AppConfig.initialize(context)
    }
}
