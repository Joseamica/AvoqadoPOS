package com.avoqado.pos.features.management.presentation.tables

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.core.data.network.models.NetworkTable
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.management.presentation.home.models.Table
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TablesViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _venues = MutableStateFlow(listOf<NetworkVenue>())
    val venues: StateFlow<List<NetworkVenue>> = _venues.asStateFlow()

    private val _selectedVenue = MutableStateFlow<NetworkVenue?>(null)
    val selectedVenue: StateFlow<NetworkVenue?> = _selectedVenue.asStateFlow()

    private val _tables = MutableStateFlow(listOf<Table>())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    fun toggleSettingsModal(value: Boolean){
        _showSettings.update {
            value
        }
    }

    init {
        fetchTables()
    }

    fun fetchTables() {
        //TODO: usar Avoqado api para cargar mesas
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AvoqadoAPI.apiService.getVenues()
                val venueId = sessionManager.getVenueId()

                if (venueId.isNotEmpty()) {
                    _venues.value = result.filter { it.id == venueId }
                    _selectedVenue.value = result.firstOrNull { it.id == venueId }.also {
                        it?.let { venue ->
                            sessionManager.saveVenueInfo(venue)
                        }
                    }
                } else {
                    _venues.value = result
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching tables", e)
            }
        }
    }

    fun onTableSelected(table: NetworkTable) {
        navigationDispatcher.navigateWithArgs(
            ManagementDests.TableDetail,
            NavigationArg.StringArg(
                ManagementDests.TableDetail.ARG_VENUE_ID,
                selectedVenue.value?.id ?: ""
            ),
            NavigationArg.StringArg(
                ManagementDests.TableDetail.ARG_TABLE_ID,
                table.tableNumber?.toString() ?: ""
            )

        )
    }

    fun setSelectedVenue(index: Int) {
        _selectedVenue.value = _venues.value.get(index).also {
            sessionManager.saveVenueInfo(it)
        }

    }

    fun onBackAction(){
        navigationDispatcher.navigateBack()
    }
    fun logout() {
        sessionManager.clearAvoqadoSession()
        navigationDispatcher.popToDestination(MainDests.Splash, inclusive = true)
        navigationDispatcher.navigateWithArgs(
            MainDests.SignIn,
            NavigationArg.StringArg(
                MainDests.SignIn.ARG_REDIRECT,
                ManagementDests.Home.route
            )
        )
    }

}