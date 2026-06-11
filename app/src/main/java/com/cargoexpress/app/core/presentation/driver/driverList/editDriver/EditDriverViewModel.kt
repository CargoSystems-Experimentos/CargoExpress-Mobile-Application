package com.cargoexpress.app.core.presentation.driver.driverList.editDriver

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.remote.driver.DriverStateUpdateDto
import com.cargoexpress.app.core.data.remote.driver.DriverUpdateDto
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.domain.Driver
import kotlinx.coroutines.launch

class EditDriverViewModel(
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val _driverState = mutableStateOf(UIState<Driver>())
    val driverState: State<UIState<Driver>> get() = _driverState

    fun loadDriver(driverId: Int) {
        viewModelScope.launch {
            _driverState.value = UIState(isLoading = true)
            val result = driverRepository.getDriverById(driverId)
            _driverState.value = when (result) {
                is Resource.Success -> UIState(data = result.data)
                is Resource.Error -> UIState(message = result.message ?: "Error al cargar el conductor")
            }
        }
    }

    fun updateDriver(driverId: Int, name: String, contactNumber: String, onResult: (Resource<DriverUpdateDto>) -> Unit) {
        viewModelScope.launch {
            onResult(driverRepository.updateDriver(driverId, name, contactNumber))
        }
    }

    fun updateDriverState(driverId: Int, state: String, onResult: (Resource<DriverStateUpdateDto>) -> Unit) {
        viewModelScope.launch {
            onResult(driverRepository.updateDriverState(driverId, state))
        }
    }
}
