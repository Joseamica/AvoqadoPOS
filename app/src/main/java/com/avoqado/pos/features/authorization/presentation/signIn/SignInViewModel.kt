package com.avoqado.pos.features.authorization.presentation.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate,
    private val sessionManager: SessionManager,
    private val authorizationRepository: AuthorizationRepository,
    private val redirect: String?,
) : ViewModel() {
    companion object {
        val codeLength = 4
    }

    private val _otpValue = MutableStateFlow("")
    val otp: StateFlow<String> = _otpValue.asStateFlow()

    fun updateOtp(otp: String) {
        _otpValue.update {
            if (otp.isEmpty()) {
                ""
            } else {
                it + otp
            }
        }

        if (_otpValue.value.length == 4) {
            goToNextScreen(_otpValue.value)
        }
    }

    fun deleteDigit() {
        if (_otpValue.value.isNotEmpty()) {
            _otpValue.update {
                it.dropLast(1)
            }
        }
    }

    fun goToNextScreen(passcode: String) {
        viewModelScope.launch {
            try {
                authorizationRepository.login(
                    passcode = passcode,
                    venueId = sessionManager.getVenueId(),
                )

                // Check if redirect is not null and is a valid route
                if (redirect != null && redirect.isNotEmpty() && !redirect.startsWith("{") && !redirect.endsWith("}")) {
                    Timber.i("Sign in redirecting to : $redirect")
                    navigationDispatcher.navigateTo(
                        route = redirect,
                        navOptions =
                            NavOptions
                                .Builder()
                                .setPopUpTo(
                                    MainDests.SignIn.route,
                                    true,
                                ).build(),
                    )
                } else {
                    // Default navigation path when redirect is invalid or null
                    navigationDispatcher.navigateBack()
                    navigationDispatcher.navigateTo(MainDests.Splash)
                }
            } catch (e: Exception) {
                if (e is AvoqadoError.BasicError) {
                    _otpValue.update {
                        ""
                    }
                    snackbarDelegate.showSnackbar(
                        message = e.message,
                    )
                } else {
                    Timber.e(e)
                }
            }
        }
    }
}
