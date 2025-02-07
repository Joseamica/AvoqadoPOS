package com.avoqado.pos.features.management.domain.usecases

import com.avoqado.pos.core.domain.models.PaymentUpdate
import com.avoqado.pos.features.management.domain.ManagementRepository
import kotlinx.coroutines.flow.Flow

class ListenTableEventsUseCase(
    private val managementRepository: ManagementRepository
) {
    operator fun invoke(action: ListenTableAction): Flow<PaymentUpdate> {
        when(action) {
            is ListenTableAction.Connect -> {
                managementRepository.connectToTableEvents(action.venueId, action.tableId)
                return managementRepository.listenTableEvents()
            }
            ListenTableAction.Disconnect -> {
                managementRepository.stopListeningTableEvents()
                return managementRepository.listenTableEvents()
            }
        }
    }
}

sealed class ListenTableAction {
    data class Connect(val venueId: String, val tableId: String) : ListenTableAction()
    data object Disconnect : ListenTableAction()
}