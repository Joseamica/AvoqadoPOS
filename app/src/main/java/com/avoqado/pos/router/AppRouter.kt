package com.avoqado.pos.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationCommand
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.core.usecase.ValidateAmountUseCase
import com.avoqado.pos.data.local.SessionManager
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.authorization.AuthorizationDialog
import com.avoqado.pos.screens.authorization.AuthorizationViewModel
import com.avoqado.pos.screens.home.HomeScreen
import com.avoqado.pos.screens.home.HomeViewModel
import com.avoqado.pos.screens.inputTipAmount.InputTipScreen
import com.avoqado.pos.screens.inputTipAmount.InputTipViewModel
import com.avoqado.pos.screens.splash.SplashScreen
import com.avoqado.pos.screens.splash.SplashViewModel
import com.avoqado.pos.screens.tableDetail.TableDetailScreen
import com.avoqado.pos.screens.tableDetail.TableDetailViewModel
import com.avoqado.pos.ui.screen.TipSelectionScreen
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@Composable
fun AppRouter(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    context: Context
) {
    Log.i("AppRouter", "StartComposable")
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    snackbarDelegate.apply {
        this.snackbarHostState = snackbarHostState
        coroutineScope = rememberCoroutineScope()
    }

    LaunchedEffect(key1 = Unit) {
        Log.i("AppRouter", "Launched triggered")
        try {
            navigationDispatcher.navigationCommands
                .onEach {
                    Log.i("AppRouter", "Navigation command received: $it")
                }
                .collectLatest { navigationCommand ->
                    Log.i("AppRouter", "Processing navigation command: $navigationCommand")
                    when (navigationCommand) {
                        NavigationCommand.Back -> navController.popBackStack()
                        is NavigationCommand.NavigateWithAction -> navController.navigate(
                            route = navigationCommand.navAction.route,
                            navOptions = navigationCommand.navAction.navOptions
                        )

                        is NavigationCommand.PopToDestination -> navController.popBackStack(
                            route = navigationCommand.route,
                            inclusive = navigationCommand.inclusive
                        )

                        is NavigationCommand.NavigateWithArguments -> {
                            var route = navigationCommand.navAction.route
                            for (arg in navigationCommand.args) {
                                val value = when (arg) {
                                    is NavigationArg.IntArg -> arg.value.toString()
                                    is NavigationArg.StringArg -> arg.value
                                    is NavigationArg.BooleanArg -> arg.value.toString()
                                    is NavigationArg.StringArrayArg -> {
                                        arg.value.joinToString("&") { "${arg.key}=$it" }
                                            .removePrefix("${arg.key}=")
                                    }
                                }
                                route = route.replace("{${arg.key}}", value)
                            }
                            navController.navigate(
                                route = route,
                                navOptions = navigationCommand.navAction.navOptions
                            )
                        }

                        is NavigationCommand.NavigateWithRoute -> navController.navigate(
                            route = navigationCommand.route,
                            navOptions = navigationCommand.navOptions
                        )

                        is NavigationCommand.BackWithArguments -> {
                            for (arg in navigationCommand.args) {
                                when (arg) {
                                    is NavigationArg.IntArg -> navController.previousBackStackEntry?.savedStateHandle?.set(
                                        arg.key,
                                        arg.value
                                    )

                                    is NavigationArg.StringArg -> navController.previousBackStackEntry?.savedStateHandle?.set(
                                        arg.key,
                                        arg.value
                                    )

                                    is NavigationArg.BooleanArg -> navController.previousBackStackEntry?.savedStateHandle?.set(
                                        arg.key,
                                        arg.value
                                    )

                                    is NavigationArg.StringArrayArg -> navController.previousBackStackEntry?.savedStateHandle?.set(
                                        arg.key,
                                        arg.value
                                    )
                                }
                            }
                            navController.popBackStack()
                        }

                        is NavigationCommand.NavigateToUrlExternally -> {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(navigationCommand.httpLink)
                                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                }

        } catch (e: Exception) {
            Log.e("AppRouter", "Error collecting navigation commands", e)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                snackbarDelegate.snackbar(data)
            }
        },
        content = { padding ->
            Log.i("AppRouter", "Composing content")
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = MainDests.Splash.route
            ) {
                composableHolder(MainDests.Splash) {
                    val splashViewModel = remember {
                        SplashViewModel(
                            navigationDispatcher = navigationDispatcher,
                            storage = Storage(context),
                            sessionManager = SessionManager(context),
                            serialNumber =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                            } else {
                                Build.SERIAL ?: "Unknown"
                            }
                        )
                    }

                    SplashScreen(
                        viewModel = splashViewModel,
                        externalTokenData = ExternalTokenData(context),
                        masterKeyData = MasterKeyData(context)
                    )
                }

                composableHolder(MainDests.SignIn) {

                }

                composableHolder(MainDests.Tables) {
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

                composableHolder(MainDests.TableDetail) {
                    val tableDetailViewModel = remember {
                        TableDetailViewModel(
                            navigationDispatcher = navigationDispatcher,
                            snackbarDelegate = snackbarDelegate,
                            tableNumber = it.arguments?.getString(MainDests.TableDetail.ARG_TABLE_ID) ?: "",
                            venueId = it.arguments?.getString(MainDests.TableDetail.ARG_VENUE_ID) ?: ""
                        )
                    }

                    TableDetailScreen(
                        tableDetailViewModel = tableDetailViewModel
                    )
                }

                composableHolder(MainDests.InputTip) {
                    val subtotal = it.arguments?.getString(MainDests.InputTip.ARG_SUBTOTAL) ?: "0.00"
                    val inputTipViewModel = remember {
                        InputTipViewModel(
                            subtotal = subtotal,
                            navigationDispatcher = navigationDispatcher,
                            validateAmountUseCase = ValidateAmountUseCase()
                        )
                    }

                    TipSelectionScreen(inputTipViewModel = inputTipViewModel)
                    //InputTipScreen(inputTipViewModel = inputTipViewModel)
                }

                dialogHolder(MainDests.Authorization) {
                    val viewModel = remember {
                        AuthorizationViewModel(
                            navigationDispatcher = navigationDispatcher,
                            storage = Storage(context)
                        )
                    }
                    AuthorizationDialog(viewModel = viewModel, externalTokenData = ExternalTokenData(context), masterKeyData = MasterKeyData(context))
                }
            }
        }
    )
}