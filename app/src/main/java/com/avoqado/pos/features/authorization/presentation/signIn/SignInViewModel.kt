package com.avoqado.pos.features.authorization.presentation.signIn

import android.util.Log
import androidx.lifecycle.ViewModel
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel (
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate,
    private val authorizationRepository: AuthorizationRepository
): ViewModel() {

    companion object {
        val codeLength = 4
    }

    private val _email = MutableStateFlow("test@avoqado.io")
    val email: StateFlow<String> = _email.asStateFlow()

    fun setEmail(email: String) {
        _email.update {
            email
        }
    }

    fun goToNextScreen(passcode: String){
        try {
            authorizationRepository.login(user = email.value, passcode = passcode)
            navigationDispatcher.navigateTo(MainDests.Splash)
        } catch (e:Exception) {
            if (e is AvoqadoError.BasicError) {
                snackbarDelegate.showSnackbar(
                    message = e.message
                )
            } else {
                Log.e("SignInViewModel", "",e)
            }
        }
    }
}