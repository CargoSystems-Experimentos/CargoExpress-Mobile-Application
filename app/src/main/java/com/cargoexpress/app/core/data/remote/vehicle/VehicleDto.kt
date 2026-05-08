package com.cargoexpress.app.core.data.remote.vehicle

import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.domain.Vehicle

data class VehicleDto (
    val id: Int,
    val model: String,
    val plate: String,
    val tractorPlate: String,
    val maxLoad: Float,
    val volume: Float,
    val entrepreneurId: Int

)

fun VehicleDto.toVehicle() = Vehicle(
    id=id,
    model=model,
    plate=plate,
    tractorPlate=tractorPlate,
    maxLoad=maxLoad,
    volume=volume,
    entrepreneurId=entrepreneurId)


fun Vehicle.toVehicleDto() = VehicleDto(
    id = id,
    model = model,
    plate = plate,
    tractorPlate = tractorPlate,
    maxLoad = maxLoad,
    volume = volume,
    entrepreneurId = entrepreneurId
)