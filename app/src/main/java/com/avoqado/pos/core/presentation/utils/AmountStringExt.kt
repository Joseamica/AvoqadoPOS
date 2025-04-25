package com.avoqado.pos.core.presentation.utils

fun String.toAmountMx(): String {
    val amount = if (this.contains(".")) this.toDouble() else this.toDouble() / 100
    return "%.2f".format(amount)
}

fun String.toAmountMXDouble(): Double = this.toDouble() / 100
