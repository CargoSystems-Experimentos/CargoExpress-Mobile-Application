package com.cargoexpress.app.core.data.remote.ongoingtrip

import com.cargoexpress.app.core.domain.OngoingTrip

data class OngoingTripDto(
    val id: Int = 0,
    val state: String = "",
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

data class OngoingTripDtoPost(
    val state: String,
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

fun OngoingTripDto.toOngoingTrip() = OngoingTrip(
    id = id,
    state = state,
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

fun OngoingTrip.toOngoingTripDto() = OngoingTripDto(
    id = id,
    state = state,
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

fun OngoingTrip.toOngoingTripDtoPost() = OngoingTripDtoPost(
    state = state,
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)
