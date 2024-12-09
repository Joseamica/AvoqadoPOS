package com.avoqadoapp.router


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.avoqadoapp.core.navigation.NavigationCommand
import com.avoqadoapp.core.navigation.NavigationDispatcher
import com.avoqadoapp.core.navigation.NavigationManager
import com.avoqadoapp.core.navigation.composableHolder
import com.avoqadoapp.core.navigation.handleNavigation
import com.avoqadoapp.data.AppRestClientConfigure
import com.avoqadoapp.router.destinations.MainDests
import com.avoqadoapp.screens.cardProcess.CardProcessScreen
import com.avoqadoapp.screens.cardProcess.CardProcessViewModel
import com.avoqadoapp.screens.home.HomeScreen
import com.avoqadoapp.screens.home.HomeViewModel
import com.avoqadoapp.screens.splash.SplashScreen
import com.avoqadoapp.screens.splash.SplashViewModel
import com.avoqadoapp.ui.theme.AvoqadoAppTheme
import com.menta.android.core.viewmodel.CardProcessData
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.restclient.core.RestClientConfiguration.configure
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainActivity : ComponentActivity() {

    val navigationDispatcher: NavigationDispatcher = NavigationDispatcher(
        object : NavigationManager {
            private val _navActions = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 10)
            override val navActions = _navActions.asSharedFlow()

            override fun navigate(command: NavigationCommand) {
                _navActions.tryEmit(command)
            }
        }
    )

    lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //TODO: move this to right layer
        configure(AppRestClientConfigure())

        storage = Storage(applicationContext)
        setContent {
            val navController = rememberNavController()

            LaunchedEffect(key1 = Unit) {
                navigationDispatcher.navigationCommands.handleNavigation(navController)
            }

            AvoqadoAppTheme {
                Scaffold { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = MainDests.Splash.route
                    ) {
                        composableHolder(MainDests.Splash) {
                            val viewModel = SplashViewModel(
                                navigationDispatcher = navigationDispatcher,
                                storage = storage,
                                masterKeyData = MasterKeyData(applicationContext),
                                externalTokenData = ExternalTokenData(applicationContext)
                            )
                            SplashScreen(viewModel)
                        }
                        composableHolder(MainDests.Home) {
                            val viewModel = HomeViewModel(navigationDispatcher)
                            HomeScreen(viewModel = viewModel)
                        }
                        composableHolder(MainDests.CardProcess) {
                            val context = LocalContext.current
                            val viewModel = CardProcessViewModel(
                                savedStateHandle = it.savedStateHandle
                            )
                            val cardProcessData =
                                CardProcessData()

                            val binValidationData: BinValidationData = BinValidationData(context)

                            CardProcessScreen(viewModel, cardProcessData, binValidationData)
                        }
                    }
                }
            }
        }
    }
}
