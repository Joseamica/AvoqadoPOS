package com.avoqadoapp.data

import com.menta.android.restclient.core.RestClientConfigure

class AppRestClientConfigure : RestClientConfigure {
    override fun loginDeeplink(): String =
        "menta://login.ui/unauthorized"

    override fun urlBase(): String =
        "https://api.menta.global/"
}