package com.avoqado.pos.core.presentation.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationCommand
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.features.management.presentation.navigation.managementNavigation
import com.avoqado.pos.features.authorization.presentation.authorization.AuthorizationDialog
import com.avoqado.pos.features.authorization.presentation.authorization.AuthorizationViewModel
import com.avoqado.pos.features.authorization.presentation.signIn.SignInScreen
import com.avoqado.pos.features.authorization.presentation.signIn.SignInViewModel
import com.avoqado.pos.features.authorization.presentation.splash.SplashScreen
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel
import com.avoqado.pos.features.payment.presentation.navigation.paymentNavigation
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.core.viewmodel.TrxData
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppRouter(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    externalTokenData: ExternalTokenData,
    masterKeyData: MasterKeyData,
    trxData: TrxData,
    context: Context
) {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberFullScreenBottomSheetNavigator()
    navController.navigatorProvider.addNavigator(bottomSheetNavigator)
    val snackbarHostState = remember { SnackbarHostState() }

    snackbarDelegate.apply {
        this.snackbarHostState = snackbarHostState
        coroutineScope = rememberCoroutineScope()
    }

    val user = AvoqadoApp.sessionManager.getAvoqadoSession()

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
            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetElevation = 0.dp,
                sheetBackgroundColor = Color.Transparent,
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
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
                                storage = AvoqadoApp.storage,
                                sessionManager = AvoqadoApp.sessionManager,
                                serialNumber = AvoqadoApp.terminalSerialCode,
                                snackbarDelegate = snackbarDelegate,
                                terminalRepository = AvoqadoApp.terminalRepository,
                                managementRepository = AvoqadoApp.managementRepository
                            )
                        }

                        SplashScreen(
                            viewModel = splashViewModel,
                            externalTokenData = externalTokenData,
                            masterKeyData = masterKeyData
                        )
                    }

                    composableHolder(MainDests.SignIn) {
                        val signInViewModel: SignInViewModel = remember {
                            SignInViewModel(
                                navigationDispatcher = navigationDispatcher,
                                snackbarDelegate = snackbarDelegate,
                                authorizationRepository = AvoqadoApp.authorizationRepository,
                                sessionManager = AvoqadoApp.sessionManager,
                                redirect = it.arguments?.getString(MainDests.SignIn.ARG_REDIRECT)
                            )
                        }

                        SignInScreen(signInViewModel)
                    }

                    dialogHolder(MainDests.Authorization) {
                        val viewModel = remember {
                            AuthorizationViewModel(
                                navigationDispatcher = navigationDispatcher,
                                storage = AvoqadoApp.storage,
                                sessionManager = AvoqadoApp.sessionManager
                            )
                        }
                        AuthorizationDialog(
                            viewModel = viewModel,
                            externalTokenData = externalTokenData,
                            masterKeyData = masterKeyData
                        )
                    }

                    managementNavigation(
                        navigationDispatcher = navigationDispatcher,
                        snackbarDelegate = snackbarDelegate
                    )

                    paymentNavigation(
                        navigationDispatcher = navigationDispatcher,
                        snackbarDelegate = snackbarDelegate,
                        trxData = trxData
                    )
                }
            }
        }
    )
}

@Composable
fun rememberFullScreenBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = true
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = animationSpec,
        skipHalfExpanded = skipHalfExpanded
    )

    if (skipHalfExpanded) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.currentValue }
                .collectLatest { currentValue ->
                    with(sheetState) {
                        val isOpening =
                            currentValue == ModalBottomSheetValue.Hidden && targetValue == ModalBottomSheetValue.HalfExpanded
                        val isClosing =
                            currentValue == ModalBottomSheetValue.Expanded && targetValue == ModalBottomSheetValue.HalfExpanded
                        when {
                            isOpening -> show()
                            isClosing -> hide()
                        }
                    }
                }
        }
    }
    return remember(sheetState) {
        BottomSheetNavigator(sheetState)
    }
}
