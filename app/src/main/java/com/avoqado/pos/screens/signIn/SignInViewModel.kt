package com.avoqado.pos.screens.signIn

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.navigation.NavigationDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel (
    private val navigationDispatcher: NavigationDispatcher
): ViewModel() {

    companion object {
        val codeLength = 6
    }

    private val _otp = MutableStateFlow(List(codeLength) { "" })
    val otp: StateFlow<List<String>> = _otp.asStateFlow()

    fun setOtp(value: String, pos: Int) {
        _otp.value = _otp.value.toMutableList().apply {
            set(pos, value)
        }
    }
}