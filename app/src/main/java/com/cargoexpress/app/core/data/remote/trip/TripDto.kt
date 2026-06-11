package com.cargoexpress.app.core.data.remote.trip

import com.cargoexpress.app.core.domain.Trip

data class TripDto(
    val id: Int,
    val name: String,
    val state: String,
    val type: String,
    val weight: Double,
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int
)

data class TripPostDto(
    val name: String,
    val type: String,
    val weight: Double,
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int
)

data class TripDetailsUpdateDto(
    val name: String,
    val type: String,
    val weight: Double,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int
)

data class TripScheduleUpdateDto(
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String
)

data class TripStateUpdateDto(val state: String)

fun TripDto.toTrip() = Trip(
    id = id,
    name = name,
    state = state,
    type = type,
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

fun Trip.toTripPostDto() = TripPostDto(
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
    entrepreneurId = entrepreneurId
)

fun Trip.toTripDetailsUpdateDto() = TripDetailsUpdateDto(
    name = name,
    type = type,
    weight = weight,
    driverId = driverId,
    vehicleId = vehicleId,
    clientId = clientId
)

fun Trip.toTripScheduleUpdateDto() = TripScheduleUpdateDto(
    loadLocation = loadLocation,
    loadDate = loadDate,
    unloadLocation = unloadLocation,
    unloadDate = unloadDate
)
