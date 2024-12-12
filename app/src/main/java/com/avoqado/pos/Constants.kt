package com.avoqado.pos

import com.menta.android.common_cross.util.CURRENCY_LABEL_MX

//    "id": "bd0fc6c6-d698-444a-b4ce-baa2af32f9c7",
//    "api_key": "8vn8MTN02NvZff8NwWv4NhI8UOHKAloWxMc6JMIwKTFqoKb4Z5aoKdw2eFZhiUAK",
//    "user": "apikey+avoqado@menta.global",
//    "user_type": "CUSTOMER",
//    "customer_id": "4b9d4822-9c94-4056-b58f-b84c7d214ed4",
//    "create_date": "2024-11-11T22:41:03.77331Z",
//    "update_date": "2024-11-11T22:41:03.773327Z"

val merchantId = "8e341c9a-0298-4aa1-ba6b-be11a526560f"
val customerId = "4b9d4822-9c94-4056-b58f-b84c7d214ed4"
val merchantApiKey = "KnLfOhIkTql8OUQ2NCltlUb6qonNMcNpTsky3iaz4IfNn8tzN8Rl0Dzc16THsz8E"
val terminalId = "7335c5cd-1d99-4eb7-abfb-9c43c5e9a122"
val CURRENCY_LABEL = CURRENCY_LABEL_MX // Usar CURRENCY_LABEL_ARG si es Argentina. Usar CURRENCY_LABEL_MX si es Mexico
val ACQUIRER_NAME = Acquirer.BANORTE.name // Usar Acquirer.GPS.name para Argentina. Usar Acquirer.BANORTE.name para México
val COUNTRY_CODE = Country.MEX.code // Usar Country.ARG.code para Argentina. Usar Country.MEX.code para México
fun doTagListTest(): MutableList<String> { //TODO Los TAGs dependen de cada adquirencia, revise en la documentación cuál corresponse
    val tagList: MutableList<String> = ArrayList()
    tagList.add("9F26")
    tagList.add("82")
    tagList.add("9F36")
    tagList.add("9F10")
    tagList.add("9F33")
    tagList.add("95")
    tagList.add("9F37")
    tagList.add("9A")
    tagList.add("9C")
    tagList.add("9F02")
    tagList.add("9F03")
    tagList.add("9F27")
    tagList.add("9F34")
    tagList.add("5F2A")
    tagList.add("9F1A")
    tagList.add("5F25")
    tagList.add("84")
    tagList.add("9F1E")
    tagList.add("9F6E")

    return tagList

}

fun doTagListMxTest(): MutableList<String> { //TODO Los TAGs dependen de cada adquirencia, revise en la documentación cuál corresponse
    val tagList: MutableList<String> = ArrayList()
    tagList.add("4f")
    tagList.add("50")
    tagList.add("57")
    tagList.add("5A")
    tagList.add("82")
    tagList.add("84")
    tagList.add("8A")
    tagList.add("95")
    tagList.add("9A")
    tagList.add("9B")
    tagList.add("9C")
    tagList.add("5F20")
    tagList.add("5F24")
    tagList.add("5F25")
    tagList.add("5F28")
    tagList.add("5F2A")
    tagList.add("5F30")
    tagList.add("5F34")
    tagList.add("9F02")
    tagList.add("9F03")
    tagList.add("9F07")
    tagList.add("9F09")
    tagList.add("9F0D")
    tagList.add("9F0E")
    tagList.add("9F0F")
    tagList.add("9F10")
    tagList.add("9F12")
    tagList.add("9F15")
    tagList.add("9F1A")
    tagList.add("9F1C")
    tagList.add("9F1E")
    tagList.add("9F21")
    tagList.add("9F26")
    tagList.add("9F27")
    tagList.add("9F33")
    tagList.add("9F34")
    tagList.add("9F35")
    tagList.add("9F36")
    tagList.add("9F37")
    tagList.add("9F39")
    tagList.add("9F41")
    tagList.add("9F53")
    tagList.add("9F6E")

    return tagList

}
