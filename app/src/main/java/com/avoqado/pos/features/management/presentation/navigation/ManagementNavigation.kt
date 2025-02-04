package com.avoqado.pos.features.management.presentation.navigation

import android.content.Context
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.data.local.SessionManager
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductScreen
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductViewModel
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailScreen
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailViewModel
import com.avoqado.pos.router.composableHolder
import com.avoqado.pos.screens.home.HomeScreen
import com.avoqado.pos.screens.home.HomeViewModel

fun NavGraphBuilder.managementNavigation(
    navigationDispatcher: NavigationDispatcher,
    context: Context,
    snackbarDelegate: SnackbarDelegate
) {
    composableHolder(ManagementDests.Tables) {
        val homeViewModel = remember {
            HomeViewModel(
                navigationDispatcher = navigationDispatcher,
                sessionManager = SessionManager(context)
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
                managementRepository = OperationFlowHolder.managementRepository
            )
        }

        TableDetailScreen(
            tableDetailViewModel = tableDetailViewModel
        )
    }

    composableHolder(ManagementDests.SplitByProduct) {
        val splitByProductViewModel = remember {
            SplitByProductViewModel(
                navigationDispatcher = navigationDispatcher,
                managementRepository = OperationFlowHolder.managementRepository
            )
        }

        SplitByProductScreen(
            splitByProductViewModel
        )
    }
}