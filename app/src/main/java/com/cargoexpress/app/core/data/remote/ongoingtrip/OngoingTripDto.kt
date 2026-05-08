package com.cargoexpress.app.core.data.remote.ongoingtrip

import com.cargoexpress.app.core.domain.OngoingTrip

data class OngoingTripDto(
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

fun OngoingTripDto.toOngoingTrip() = OngoingTrip(
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

fun OngoingTrip.toOngoingTripDto() = OngoingTripDto(
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

