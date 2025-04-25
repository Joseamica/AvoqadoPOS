package com.avoqado.pos.features.management.data.cache

import com.avoqado.pos.features.management.data.model.TableCacheEntity
import kotlinx.coroutines.flow.MutableStateFlow

class ManagementCacheStorage {
    private val tableDetail = MutableStateFlow<TableCacheEntity?>(null)

    fun getTable() = tableDetail.value

    fun setTable(table: TableCacheEntity) {
        tableDetail.value = table
    }

    fun clear() {
        tableDetail.value = null
    }
}
