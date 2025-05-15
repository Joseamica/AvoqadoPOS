package com.avoqado.pos.features.management.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.router.bottomSheetHolder
import com.avoqado.pos.core.presentation.router.composableHolder
import com.avoqado.pos.features.management.domain.usecases.ListenTableEventsUseCase
import com.avoqado.pos.features.menu.presentation.menudetail.MenuDetailScreen
import com.avoqado.pos.features.menu.presentation.menudetail.MenuDetailViewModel
import com.avoqado.pos.features.menu.presentation.menulist.MenuListScreen
import com.avoqado.pos.features.menu.presentation.menulist.MenuListViewModel
import com.avoqado.pos.features.management.presentation.home.HomeScreen
import com.avoqado.pos.features.management.presentation.home.HomeViewModel
import com.avoqado.pos.features.management.presentation.shiftNotStarted.ShiftNotStartedSheet
import com.avoqado.pos.features.management.presentation.shiftNotStarted.ShiftNotStartedViewModel
import com.avoqado.pos.features.management.presentation.splitPerson.SplitByPersonScreen
import com.avoqado.pos.features.management.presentation.splitPerson.SplitByPersonViewModel
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductScreen
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductViewModel
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailScreen
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailViewModel
import com.avoqado.pos.features.management.presentation.tables.TablesScreen
import com.avoqado.pos.features.management.presentation.tables.TablesViewModel

fun NavGraphBuilder.managementNavigation(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
) {
    composableHolder(ManagementDests.Home) {
        val homeViewModel =
            remember {
                HomeViewModel(
                    navigationDispatcher = navigationDispatcher,
                    sessionManager = AvoqadoApp.sessionManager,
                    terminalRepository = AvoqadoApp.terminalRepository,
                    snackbarDelegate = snackbarDelegate,
                )
            }
        HomeScreen(
            homeViewModel = homeViewModel,
        )
    }

    composableHolder(ManagementDests.TableDetail) {
        val tableDetailViewModel =
            remember {
                TableDetailViewModel(
                    navigationDispatcher = navigationDispatcher,
                    snackbarDelegate = snackbarDelegate,
                    tableNumber = it.arguments?.getString(ManagementDests.TableDetail.ARG_TABLE_ID) ?: "",
                    venueId = it.arguments?.getString(ManagementDests.TableDetail.ARG_VENUE_ID) ?: "",
                    managementRepository = AvoqadoApp.managementRepository,
                )
            }

        TableDetailScreen(
            tableDetailViewModel = tableDetailViewModel,
        )
    }

    composableHolder(ManagementDests.SplitByProduct) {
        val splitByProductViewModel =
            remember {
                SplitByProductViewModel(
                    navigationDispatcher = navigationDispatcher,
                    managementRepository = AvoqadoApp.managementRepository,
                    paymentRepository = AvoqadoApp.paymentRepository,
                    sessionManager = AvoqadoApp.sessionManager,
                )
            }

        SplitByProductScreen(
            splitByProductViewModel,
        )
    }

    composableHolder(ManagementDests.SplitByPerson) {
        val splitByPersonViewModel =
            remember {
                SplitByPersonViewModel(
                    navigationDispatcher = navigationDispatcher,
                    managementRepository = AvoqadoApp.managementRepository,
                    paymentRepository = AvoqadoApp.paymentRepository,
                    sessionManager = AvoqadoApp.sessionManager,
                )
            }

        SplitByPersonScreen(
            splitByPersonViewModel,
        )
    }

    composableHolder(ManagementDests.VenueTables) {
        val tablesViewModel =
            remember {
                TablesViewModel(
                    navigationDispatcher = navigationDispatcher,
                    sessionManager = AvoqadoApp.sessionManager,
                    managementRepository = AvoqadoApp.managementRepository,
                    snackbarDelegate = snackbarDelegate,
                    terminalRepository = AvoqadoApp.terminalRepository,
                )
            }

        TablesScreen(
            tablesViewModel,
        )
    }

    bottomSheetHolder(ManagementDests.OpenShift) {
        val viewModel =
            remember {
                ShiftNotStartedViewModel(
                    navigationDispatcher = navigationDispatcher,
                    terminalRepository = AvoqadoApp.terminalRepository,
                    snackbarDelegate = snackbarDelegate,
                    sessionManager = AvoqadoApp.sessionManager,
                )
            }

        ShiftNotStartedSheet(viewModel = viewModel)
    }
    
    composableHolder(ManagementDests.MenuList) {
        val menuListViewModel =
            remember {
                MenuListViewModel(
                    menuRepository = AvoqadoApp.menuRepository,
                    navigationDispatcher = navigationDispatcher,
                    venueId = it.arguments?.getString(ManagementDests.MenuList.ARG_VENUE_ID) ?: ""
                )
            }
        
        MenuListScreen(viewModel = menuListViewModel)
    }
    
    composableHolder(ManagementDests.MenuDetail) {
        val menuDetailViewModel =
            remember {
                MenuDetailViewModel(
                    menuId = it.arguments?.getString(ManagementDests.MenuDetail.ARG_MENU_ID) ?: "",
                    menuRepository = AvoqadoApp.menuRepository,
                    navigationDispatcher = navigationDispatcher,
                    venueId = AvoqadoApp.sessionManager.getVenueId() ?: ""
                )
            }
        
        MenuDetailScreen(viewModel = menuDetailViewModel)
    }
}
