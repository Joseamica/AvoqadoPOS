package com.avoqadoapp.core.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

suspend fun Flow<NavigationCommand>.handleNavigation(navController: NavHostController){
    this.collectLatest { navigationCommand ->
        when (navigationCommand) {
            NavigationCommand.Back -> navController.popBackStack()

            is NavigationCommand.NavigateWithAction ->
                navController.navigate(
                    route = navigationCommand.navAction.route,
                    navOptions = navigationCommand.navAction.navOptions
                )

            is NavigationCommand.PopToDestination ->
                navController.popBackStack(
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
                    route = route.replace(
                        "{${arg.key}}",
                        value
                    )
                }
                navController.navigate(
                    route = route,
                    navOptions = navigationCommand.navAction.navOptions
                )
            }

            is NavigationCommand.NavigateWithRoute ->
                navController.navigate(
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
        }
    }
}