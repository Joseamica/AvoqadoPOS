package com.avoqado.pos.core.presentation.navigation

sealed class NavigationArg(
    open val key: String,
) {
    data class StringArg(
        override val key: String,
        val value: String,
    ) : NavigationArg(key)

    data class IntArg(
        override val key: String,
        val value: Int,
    ) : NavigationArg(key)

    data class BooleanArg(
        override val key: String,
        val value: Boolean,
    ) : NavigationArg(key)

    data class StringArrayArg(
        override val key: String,
        val value: Array<String>,
    ) : NavigationArg(key)
}
