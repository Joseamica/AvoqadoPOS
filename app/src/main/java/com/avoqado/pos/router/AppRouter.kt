package com.avoqado.pos.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationCommand
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.data.local.SessionManager
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.management.presentation.navigation.managementNavigation
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductScreen
import com.avoqado.pos.features.management.presentation.splitProduct.SplitByProductViewModel
import com.avoqado.pos.screens.authorization.AuthorizationDialog
import com.avoqado.pos.screens.authorization.AuthorizationViewModel
import com.avoqado.pos.screens.home.HomeScreen
import com.avoqado.pos.screens.home.HomeViewModel
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipScreen
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipViewModel
import com.avoqado.pos.screens.splash.SplashScreen
import com.avoqado.pos.screens.splash.SplashViewModel
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailScreen
import com.avoqado.pos.features.management.presentation.tableDetail.TableDetailViewModel
import com.avoqado.pos.features.payment.presentation.navigation.paymentNavigation
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultScreen
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultViewModel
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppRouter(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    context: Context
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    snackbarDelegate.apply {
        this.snackbarHostState = snackbarHostState
        coroutineScope = rememberCoroutineScope()
    }

    LaunchedEffect(key1 = Unit) {
        try {
            navigationDispatcher.navigationCommands
                .collectLatest { navigationCommand ->
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
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = MainDests.Splash.route,
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
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

                dialogHolder(MainDests.Authorization) {
                    val viewModel = remember {
                        AuthorizationViewModel(
                            navigationDispatcher = navigationDispatcher,
                            storage = Storage(context)
                        )
                    }
                    AuthorizationDialog(viewModel = viewModel, externalTokenData = ExternalTokenData(context), masterKeyData = MasterKeyData(context))
                }

                managementNavigation(
                    navigationDispatcher = navigationDispatcher,
                    context = context,
                    snackbarDelegate = snackbarDelegate
                )

                paymentNavigation(navigationDispatcher)
            }
        }
    )
}