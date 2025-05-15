package com.avoqado.pos.features.menu.di

import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.menu.data.repository.MenuRepositoryImpl
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import com.avoqado.pos.features.menu.presentation.menudetail.MenuDetailViewModel
import com.avoqado.pos.features.menu.presentation.menulist.MenuListViewModel
import com.avoqado.pos.core.data.network.AvoqadoService

/**
 * Factory functions for menu feature components
 */
object MenuFactory {
    
    /**
     * Creates a MenuRepository instance
     */
    fun createMenuRepository(avoqadoService: AvoqadoService): MenuRepository {
        return MenuRepositoryImpl(avoqadoService)
    }
    
    /**
     * Creates a MenuListViewModel instance
     */
    fun createMenuListViewModel(
        menuRepository: MenuRepository,
        navigationDispatcher: NavigationDispatcher,
        venueId: String
    ): MenuListViewModel {
        return MenuListViewModel(
            menuRepository = menuRepository,
            navigationDispatcher = navigationDispatcher,
            venueId = venueId
        )
    }
    
    /**
     * Creates a MenuDetailViewModel instance
     */
    fun createMenuDetailViewModel(
        menuRepository: MenuRepository,
        navigationDispatcher: NavigationDispatcher,
        menuId: String
    ): MenuDetailViewModel {
        return MenuDetailViewModel(
            menuRepository = menuRepository,
            navigationDispatcher = navigationDispatcher,
            menuId = menuId
        )
    }
}
