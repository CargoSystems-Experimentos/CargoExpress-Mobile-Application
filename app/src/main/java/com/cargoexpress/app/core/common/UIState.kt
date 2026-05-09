package com.cargoexpress.app.core.common

data class UIState<T>(
    val isLoading: Boolean = false,
    var data: T? = null,
    val message: String = ""
)