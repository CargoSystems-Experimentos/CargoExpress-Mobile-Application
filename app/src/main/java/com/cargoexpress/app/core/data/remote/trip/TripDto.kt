package com.cargoexpress.app.core.data.remote.trip

import android.os.Build
import androidx.annotation.RequiresApi
import com.cargoexpress.app.core.domain.Trip

data class TripDto(
    val id: Int,
    val name: Name,
    val cargoData: CargoData,
    val tripData: TripData,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int
)

data class Name(val tripName: String)
data class CargoData(val type: String, val weight: Int)
data class TripData(
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun TripDto.toTrip() = Trip(
    id = id,
    tripName = name.tripName,
    cargoType = cargoData.type,
    weight = cargoData.weight,
    loadLocation = tripData.loadLocation,
    loadDate = tripData.loadDate,
    unloadLocation = tripData.unloadLocation,
    unloadDate = tripData.unloadDate,
    driverId = driverId,
    vehicleId = vehicleId,
    clientId = clientId,
    entrepreneurId = entrepreneurId
)

fun TripDto.toTripLegacy(): Trip {
    return Trip(
        id = id,
        tripName = name.tripName,
        cargoType = cargoData.type,
        weight = cargoData.weight,
        loadLocation = tripData.loadLocation,
        loadDate = tripData.loadDate,
        unloadLocation = tripData.unloadLocation,
        unloadDate = tripData.unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId
    )
}

data class TripDtoPost(
    val name: String,
    val type: String,
    val weight: Int,
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int
)

@RequiresApi(Build.VERSION_CODES.O)
fun TripDtoPost.toTrip(): Trip {
    return Trip(
        id = 0,
        tripName = name,
        cargoType = type,
        weight = weight,
        loadLocation = loadLocation,
        loadDate = loadDate,
        unloadLocation = unloadLocation,
        unloadDate = unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Trip.toTripDto(): TripDtoPost {
    return TripDtoPost(
        name = tripName,
        type = cargoType,
        weight = weight,
        loadLocation = loadLocation,
        loadDate = loadDate,
        unloadLocation = unloadLocation,
        unloadDate = unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId
    )
}