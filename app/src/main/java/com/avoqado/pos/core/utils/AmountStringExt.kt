package com.avoqado.pos.core.utils

fun String.toAmountMx(): String {
    val amount = this.toDouble() / 100
    return "%.2f".format(amount)
}

fun String.toAmountMXDouble(): Double {
    return this.toDouble() / 100
}