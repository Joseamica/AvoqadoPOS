package com.avoqado.pos.features.authorization.presentation.signIn


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel (
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate,
    private val sessionManager: SessionManager,
    private val authorizationRepository: AuthorizationRepository,
    private val redirect: String?
): ViewModel() {

    companion object {
        val codeLength = 4
    }

    fun goToNextScreen(passcode: String){
       viewModelScope.launch {
           try {
               authorizationRepository.login(
                   passcode = passcode,
                   venueId = sessionManager.getVenueId()
               )
               redirect?.let {
                   navigationDispatcher.navigateTo(
                       route = it,
                       navOptions = NavOptions.Builder()
                           .setPopUpTo(
                               MainDests.SignIn.route,
                               true
                           )
                           .build()
                   )
               } ?: run {
                   navigationDispatcher.navigateBack()
                   navigationDispatcher.navigateTo(MainDests.Splash)
               }
           } catch (e:Exception) {
               if (e is AvoqadoError.BasicError) {
                   snackbarDelegate.showSnackbar(
                       message = e.message
                   )
               } else {
                   Timber.e(e)
               }
           }
       }
    }
}