package com.cargoexpress.app.core.data.remote.driver

import com.cargoexpress.app.core.domain.Driver

data class DriverDto(
    val id: Int,
    val name: String,
    val dni: String,
    val license: String,
    val contactNumber: String,
    val state: String,
    val entrepreneurId: Int
)

data class DriverPostDto(
    val name: String,
    val dni: String,
    val license: String,
    val contactNumber: String,
    val entrepreneurId: Int
)

data class DriverUpdateDto(
    val name: String,
    val contactNumber: String
)

data class DriverStateUpdateDto(val state: String)

fun DriverDto.toDriver() = Driver(
    id = id,
    name = name,
    dni = dni,
    license = license,
    contactNumber = contactNumber,
    state = state,
    entrepreneurId = entrepreneurId
)

fun Driver.toDriverPostDto() = DriverPostDto(
    name = name,
    dni = dni,
    license = license,
    contactNumber = contactNumber,
    entrepreneurId = entrepreneurId
)
