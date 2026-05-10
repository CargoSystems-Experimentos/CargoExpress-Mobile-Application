package com.cargoexpress.app.core.data.remote.trip

import android.os.Build
import androidx.annotation.RequiresApi
import com.cargoexpress.app.core.domain.Trip

data class TripDto(
    val id: Int,
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
    val entrepreneurId: Int,
    val evidenceImg: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun TripDto.toTrip() = Trip(
    id = id,
    name = name,
    type = type,
    weight = weight,
    loadLocation = loadLocation,
    loadDate = loadDate,
    unloadLocation = unloadLocation,
    unloadDate = unloadDate,
    driverId = driverId,
    vehicleId = vehicleId,
    clientId = clientId,
    entrepreneurId = entrepreneurId,
    evidenceImg = evidenceImg
)

fun TripDto.toTripLegacy(): Trip {
    return Trip(
        id = id,
        name = name,
        type = type,
        weight = weight,
        loadLocation = loadLocation,
        loadDate = loadDate,
        unloadLocation = unloadLocation,
        unloadDate = unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId,
        evidenceImg = evidenceImg
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
    val entrepreneurId: Int,
    val evidenceImg: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun TripDtoPost.toTrip(): Trip {
    return Trip(
        id = 0,
        name = name,
        type = type,
        weight = weight,
        loadLocation = loadLocation,
        loadDate = loadDate,
        unloadLocation = unloadLocation,
        unloadDate = unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId,
        evidenceImg = evidenceImg
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Trip.toTripDto(): TripDtoPost {
    return TripDtoPost(
        name = name,
        type = type,
        weight = weight,
        loadLocation = loadLocation,
        loadDate = loadDate,
        unloadLocation = unloadLocation,
        unloadDate = unloadDate,
        driverId = driverId,
        vehicleId = vehicleId,
        clientId = clientId,
        entrepreneurId = entrepreneurId,
        evidenceImg = evidenceImg
    )
}