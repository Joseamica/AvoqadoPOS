package com.avoqado.pos.core.utils

fun String.toAmountMx(): String {
    val amount = if (this.contains(".")) this.toDouble() else this.toDouble() / 100
    return "%.2f".format(amount)
}

fun String.toAmountMXDouble(): Double {
    return this.toDouble() / 100
}