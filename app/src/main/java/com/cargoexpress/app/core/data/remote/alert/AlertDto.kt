package com.cargoexpress.app.core.data.remote.alert

import com.cargoexpress.app.core.domain.Alert

data class AlertDto(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val ongoingTripId: Int
)

fun AlertDto.toAlert() = Alert(
    id = id,
    title = title,
    description = description,
    date = date,
    ongoingTripId = ongoingTripId
)

fun Alert.toAlertDto() = AlertDto(
    id = id,
    title = title,
    description = description,
    date = date,
    ongoingTripId = ongoingTripId
)