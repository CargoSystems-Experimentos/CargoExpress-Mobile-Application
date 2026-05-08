package com.cargoexpress.app.core.data.remote.driver

import com.cargoexpress.app.core.domain.Driver


data class DriverDto(
    val id: Int,
    val name: String,
    val dni: String,
    val license: String,
    val contactNumber: String,
    val entrepreneurId: Int
)

fun DriverDto.toDriver() = Driver(
    id = id,
    name = name,
    dni = dni,
    license = license,
    contactNumber = contactNumber,
    entrepreneurId = entrepreneurId
)

fun Driver.toDriverDto() = DriverDto(
    id = id,
    name = name,
    dni = dni,
    license = license,
    contactNumber = contactNumber,
    entrepreneurId = entrepreneurId
)