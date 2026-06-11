package com.cargoexpress.app.core.data.remote.alert

import com.cargoexpress.app.core.domain.Alert

data class AlertDto(
    val id: Int,
    val title: String,
    val type: String,
    val description: String,
    val date: String,
    val tripId: Int
)

data class AlertPostDto(
    val title: String,
    val type: String,
    val description: String,
    val date: String,
    val tripId: Int
)

fun AlertDto.toAlert() = Alert(
    id = id,
    title = title,
    type = type,
    description = description,
    date = date,
    tripId = tripId
)

fun Alert.toAlertPostDto() = AlertPostDto(
    title = title,
    type = type,
    description = description,
    date = date,
    tripId = tripId
)
