package com.cargoexpress.app.core.presentation.vehicle.editVehicle

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.remote.vehicle.VehicleNameUpdateDto
import com.cargoexpress.app.core.data.remote.vehicle.VehicleStateUpdateDto
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.launch

class EditVehicleViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _vehicleState = mutableStateOf(UIState<Vehicle>())
    val vehicleState: State<UIState<Vehicle>> get() = _vehicleState

    fun loadVehicle(vehicleId: Int) {
        viewModelScope.launch {
            _vehicleState.value = UIState(isLoading = true)
            val result = vehicleRepository.getVehicleById(vehicleId)
            _vehicleState.value = when (result) {
                is Resource.Success -> UIState(data = result.data)
                is Resource.Error -> UIState(message = result.message ?: "Error al cargar el vehículo")
            }
        }
    }

    fun updateVehicleName(vehicleId: Int, name: String, onResult: (Resource<VehicleNameUpdateDto>) -> Unit) {
        viewModelScope.launch {
            val result = vehicleRepository.updateVehicleName(vehicleId, name)
            onResult(result)
        }
    }

    fun updateVehicleState(vehicleId: Int, state: String, onResult: (Resource<VehicleStateUpdateDto>) -> Unit) {
        viewModelScope.launch {
            val result = vehicleRepository.updateVehicleState(vehicleId, state)
            onResult(result)
        }
    }
}
