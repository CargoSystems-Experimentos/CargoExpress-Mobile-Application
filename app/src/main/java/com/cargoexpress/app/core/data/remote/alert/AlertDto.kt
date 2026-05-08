package com.cargoexpress.app.core.data.remote.alert

import com.cargoexpress.app.core.domain.Alert
import java.time.LocalDateTime

data class AlertDto(
    val title: String,
    val description: String,
    val date: String,
    val tripId: Int
)

fun AlertDto.toAlert() = Alert(
    title = title,
    description = description,
    date = date,
    tripId = tripId
)

fun Alert.toAlertDto() = AlertDto(
    title = title,
    description = description,
    date = date,
    tripId = tripId
)
