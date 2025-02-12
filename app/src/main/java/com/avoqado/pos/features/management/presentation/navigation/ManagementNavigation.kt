package com.avoqado.pos.features.management.presentation.navigation

import android.content.Context
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.features.management.domain.usecases.ListenTableEventsUseCase
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductScreen
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductViewModel
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailScreen
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailViewModel
import com.avoqado.pos.router.composableHolder
import com.avoqado.pos.features.management.presentation.home.HomeScreen
import com.avoqado.pos.features.management.presentation.home.HomeViewModel
import com.avoqado.pos.features.management.presentation.splitPerson.SplitByPersonScreen
import com.avoqado.pos.features.management.presentation.splitPerson.SplitByPersonViewModel
import com.menta.android.printer.i9100.core.DevicePrintImpl

fun NavGraphBuilder.managementNavigation(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    devicePrintImpl: DevicePrintImpl
) {
    composableHolder(ManagementDests.Tables) {
        val homeViewModel = remember {
            HomeViewModel(
                navigationDispatcher = navigationDispatcher,
                sessionManager = AvoqadoApp.sessionManager
            )
        }
        HomeScreen(
            homeViewModel = homeViewModel
        )
    }

    composableHolder(ManagementDests.TableDetail) {
        val tableDetailViewModel = remember {
            TableDetailViewModel(
                navigationDispatcher = navigationDispatcher,
                snackbarDelegate = snackbarDelegate,
                tableNumber = it.arguments?.getString(ManagementDests.TableDetail.ARG_TABLE_ID) ?: "",
                venueId = it.arguments?.getString(ManagementDests.TableDetail.ARG_VENUE_ID) ?: "",
                managementRepository = AvoqadoApp.managementRepository,
                listenTableEventsUseCase = ListenTableEventsUseCase(AvoqadoApp.managementRepository)
            )
        }

        TableDetailScreen(
            tableDetailViewModel = tableDetailViewModel,
            devicePrintImpl = devicePrintImpl
        )
    }

    composableHolder(ManagementDests.SplitByProduct) {
        val splitByProductViewModel = remember {
            SplitByProductViewModel(
                navigationDispatcher = navigationDispatcher,
                managementRepository = AvoqadoApp.managementRepository,
                paymentRepository = AvoqadoApp.paymentRepository
            )
        }

        SplitByProductScreen(
            splitByProductViewModel
        )
    }

    composableHolder(ManagementDests.SplitByPerson) {
        val splitByProductViewModel = remember {
            SplitByPersonViewModel(
                navigationDispatcher = navigationDispatcher,
                managementRepository = AvoqadoApp.managementRepository,
                paymentRepository = AvoqadoApp.paymentRepository
            )
        }

        SplitByPersonScreen(
            splitByProductViewModel
        )
    }
}