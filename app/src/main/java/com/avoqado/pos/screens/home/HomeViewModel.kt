package com.avoqado.pos.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.data.network.AvoqadoAPI
import com.avoqado.pos.data.network.models.NetworkTable
import com.avoqado.pos.data.network.models.NetworkVenue
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.home.models.Table
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (
    private val navigationDispatcher: NavigationDispatcher
): ViewModel() {

    private val _venues = MutableStateFlow(listOf<NetworkVenue>())
    val venues: StateFlow<List<NetworkVenue>> = _venues.asStateFlow()

    private val _selectedVenue = MutableStateFlow<NetworkVenue?>(null)
    val selectedVenue: StateFlow<NetworkVenue?> = _selectedVenue.asStateFlow()

    private val _tables = MutableStateFlow(listOf<Table>())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    init {
        fetchTables()
    }

    fun fetchTables(){
        //TODO: usar Avoqado api para cargar mesas
        viewModelScope.launch(Dispatchers.IO) {
            val result = AvoqadoAPI.apiService.getVenues()
            _venues.value = result
        }
    }

    fun onTableSelected(table: NetworkTable) {
        navigationDispatcher.navigateWithArgs(
            MainDests.TableDetail,
            NavigationArg.StringArg(
                MainDests.TableDetail.ARG_VENUE_ID,
                selectedVenue.value?.id ?:""
            ),
            NavigationArg.StringArg(
                MainDests.TableDetail.ARG_TABLE_ID,
                table.tableNumber?.toString() ?: ""
            )
        )
    }

    fun setSelectedVenue(index: Int) {
        _selectedVenue.value = _venues.value.get(index)
    }

}