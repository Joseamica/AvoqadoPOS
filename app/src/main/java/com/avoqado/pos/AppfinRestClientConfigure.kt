package com.avoqado.pos

import com.menta.android.restclient.core.RestClientConfigure

class AppfinRestClientConfigure : RestClientConfigure {
    override fun loginDeeplink(): String =
        "menta://login.ui/unauthorized"

    override fun urlBase(): String =
       "https://api.menta.global/"
}
