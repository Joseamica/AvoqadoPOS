package com.avoqado.pos.features.management.domain

import com.avoqado.pos.features.management.domain.models.TableDetail

interface ManagementRepository {
    fun getTableBill()
    fun getCachedTable() : TableDetail?
    fun setTableCache(table: TableDetail)
}