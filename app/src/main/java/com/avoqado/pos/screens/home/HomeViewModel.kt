package com.avoqado.pos.screens.home

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.home.models.Table
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel (
    private val navigationDispatcher: NavigationDispatcher
): ViewModel() {

    private val _tables = MutableStateFlow(listOf<Table>())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    fun fetchTables(){
        //TODO: usar Avoqado api para cargar mesas
    }

    fun onTableSelected(table: Table) {
        navigationDispatcher.navigateWithArgs(
            MainDests.TableDetail,
            NavigationArg.StringArg(
                MainDests.TableDetail.ARG_TABLE_ID,
                table.id
            )
        )
    }

}