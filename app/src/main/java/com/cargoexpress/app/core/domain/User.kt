package com.cargoexpress.app.core.domain

data class User(
    val id: Int,
    val username: String,
    val phone: String,
    val state: Boolean,
    val modifiedAt: String
)
