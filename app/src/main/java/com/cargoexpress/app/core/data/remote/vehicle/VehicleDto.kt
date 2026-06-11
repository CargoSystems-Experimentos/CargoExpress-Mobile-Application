package com.cargoexpress.app.core.data.remote.vehicle

import com.cargoexpress.app.core.domain.Vehicle

data class VehicleDto(
    val id: Int,
    val name: String,
    val model: String,
    val plate: String,
    val tractorPlate: String,
    val maxLoad: Double,
    val volume: Double,
    val state: String,
    val entrepreneurId: Int
)

data class VehiclePostDto(
    val name: String,
    val model: String,
    val plate: String,
    val tractorPlate: String,
    val maxLoad: Double,
    val volume: Double,
    val entrepreneurId: Int
)

data class VehicleNameUpdateDto(val name: String)
data class VehicleStateUpdateDto(val state: String)

fun VehicleDto.toVehicle() = Vehicle(
    id = id,
    name = name,
    model = model,
    plate = plate,
    tractorPlate = tractorPlate,
    maxLoad = maxLoad,
    volume = volume,
    state = state,
    entrepreneurId = entrepreneurId
)

fun Vehicle.toVehiclePostDto() = VehiclePostDto(
    name = name,
    model = model,
    plate = plate,
    tractorPlate = tractorPlate,
    maxLoad = maxLoad,
    volume = volume,
    entrepreneurId = entrepreneurId
)
