package com.cargoexpress.app.core.data.remote.ongoingtrip

import com.cargoexpress.app.core.domain.OngoingTrip

data class OngoingTripDto(
    val id: Int = 0,
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

data class OngoingTripDtoPost(
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

data class OngoingTripUpdateDto(
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)

fun OngoingTripDto.toOngoingTrip() = OngoingTrip(
    id = id,
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

fun OngoingTrip.toOngoingTripDtoPost() = OngoingTripDtoPost(
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)

fun OngoingTrip.toOngoingTripUpdateDto() = OngoingTripUpdateDto(
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    distance = distance,
    tripId = tripId
)
