package com.cargoexpress.app.core.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.AuditLogRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tripRepository: TripRepository,
    private val auditLogRepository: AuditLogRepository,
    private val vehicleRepository: VehicleRepository,
    private val driverRepository: DriverRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _progressTrips = MutableStateFlow<List<Trip>>(emptyList())
    val progressTrips: StateFlow<List<Trip>> = _progressTrips

    private val _alertSummaries = MutableStateFlow<List<HomeAlertSummary>>(emptyList())
    val alertSummaries: StateFlow<List<HomeAlertSummary>> = _alertSummaries

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _tripsEmpty = MutableStateFlow(false)
    val tripsEmpty: StateFlow<Boolean> = _tripsEmpty

    private val _alertsEmpty = MutableStateFlow(false)
    val alertsEmpty: StateFlow<Boolean> = _alertsEmpty

    private val _availableDriverCount = MutableStateFlow(0)
    val availableDriverCount: StateFlow<Int> = _availableDriverCount

    private val _availableVehicleCount = MutableStateFlow(0)
    val availableVehicleCount: StateFlow<Int> = _availableVehicleCount

    private val _awaitingTripCount = MutableStateFlow(0)
    val awaitingTripCount: StateFlow<Int> = _awaitingTripCount

    private val _progressTripCount = MutableStateFlow(0)
    val progressTripCount: StateFlow<Int> = _progressTripCount

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = Constants.TOKEN
            if (Constants.USER_ROLE == "ENTREPRENEUR") {
                loadEntrepreneurData(token, Constants.ENTREPRENEUR_ID)
            } else {
                loadClientData(token, Constants.CLIENT_ID)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadEntrepreneurData(token: String, entrepreneurId: Int) {
        val allTrips = when (val r = tripRepository.getTrips(token, entrepreneurId)) {
            is Resource.Success -> r.data ?: emptyList()
            is Resource.Error -> emptyList()
        }

        val tripAuditLogDtos = when (val r = auditLogRepository.getTripAuditLogDtos(token, entrepreneurId)) {
            is Resource.Success -> r.data ?: emptyList()
            is Resource.Error -> emptyList()
        }

        val progressTripIdsFromLogs = tripAuditLogDtos
            .filter { dto ->
                dto.action == "UPDATE" && dto.modifiedFields["state"]?.toString() == "PROGRESS"
            }
            .mapNotNull { dto -> extractEntityId(dto.modifiedFields) }
            .toSet()

        val progressTrips = allTrips
            .filter { trip ->
                trip.state == "PROGRESS" &&
                    (progressTripIdsFromLogs.isEmpty() || trip.id in progressTripIdsFromLogs)
            }
            .take(3)

        _progressTrips.value = progressTrips
        _tripsEmpty.value = progressTrips.isEmpty()

        val progressTripItems = allTrips.filter { it.state == "PROGRESS" }
        val allAlerts = mutableListOf<HomeAlertSummary>()
        for (trip in progressTripItems) {
            when (val r = alertRepository.getAlertsByTripId(token, trip.id)) {
                is Resource.Success -> r.data?.forEach { alert ->
                    allAlerts.add(
                        HomeAlertSummary(
                            tripName = trip.name,
                            alertTitle = alert.title,
                            alertType = alert.type,
                            alertDate = alert.date,
                            tripId = trip.id
                        )
                    )
                }
                is Resource.Error -> {}
            }
        }
        val alertSummaries = allAlerts.sortedByDescending { it.alertDate }.take(3)

        _alertSummaries.value = alertSummaries
        _alertsEmpty.value = alertSummaries.isEmpty()

        val vehiclesResult = vehicleRepository.getVehicleList(token, entrepreneurId)
        if (vehiclesResult is Resource.Success) {
            _availableVehicleCount.value = vehiclesResult.data?.count { it.state == "AVAILABLE" } ?: 0
        }

        val driversResult = driverRepository.getDrivers(token, entrepreneurId)
        if (driversResult is Resource.Success) {
            _availableDriverCount.value = driversResult.data?.count { it.state == "AVAILABLE" } ?: 0
        }
    }

    private suspend fun loadClientData(token: String, clientId: Int) {
        val allTrips = when (val r = tripRepository.getTripsByClientId(token, clientId)) {
            is Resource.Success -> r.data ?: emptyList()
            is Resource.Error -> emptyList()
        }

        val progressTrips = allTrips.filter { it.state == "PROGRESS" }.take(3)
        _progressTrips.value = progressTrips
        _tripsEmpty.value = progressTrips.isEmpty()

        val progressTripItems = allTrips.filter { it.state == "PROGRESS" }
        val allAlerts = mutableListOf<HomeAlertSummary>()
        for (trip in progressTripItems) {
            when (val r = alertRepository.getAlertsByTripId(token, trip.id)) {
                is Resource.Success -> r.data?.forEach { alert ->
                    allAlerts.add(
                        HomeAlertSummary(
                            tripName = trip.name,
                            alertTitle = alert.title,
                            alertType = alert.type,
                            alertDate = alert.date,
                            tripId = trip.id
                        )
                    )
                }
                is Resource.Error -> {}
            }
        }
        val sortedAlerts = allAlerts.sortedByDescending { it.alertDate }.take(3)
        _alertSummaries.value = sortedAlerts
        _alertsEmpty.value = sortedAlerts.isEmpty()

        _awaitingTripCount.value = allTrips.count { it.state == "AWAITING" }
        _progressTripCount.value = allTrips.count { it.state == "PROGRESS" }
    }

    private fun extractEntityId(fields: Map<String, Any?>): Int? =
        (fields["id"] as? Double)?.toInt()
            ?: (fields["id"] as? String)?.toIntOrNull()

    private fun extractTripId(fields: Map<String, Any?>): Int? =
        (fields["tripId"] as? Double)?.toInt()
            ?: (fields["tripId"] as? String)?.toIntOrNull()
            ?: (fields["trip_id"] as? Double)?.toInt()
            ?: (fields["trip_id"] as? String)?.toIntOrNull()
            ?: (fields["id"] as? Double)?.toInt()
            ?: (fields["id"] as? String)?.toIntOrNull()
}
