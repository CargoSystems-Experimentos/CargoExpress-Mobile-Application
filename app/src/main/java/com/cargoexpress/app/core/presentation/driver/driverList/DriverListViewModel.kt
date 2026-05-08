package com.cargoexpress.app.core.presentation.driver.driverList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.repository.DriverRepository
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource
import pe.edu.upc.appturismo.common.UIState

class DriverListViewModel(private val navController: NavController, private val driverRepository: DriverRepository)
    : ViewModel() {

    private val _state = mutableStateOf(UIState<List<DriverDto>>())
    val state: State<UIState<List<DriverDto>>> get() = _state

    private val _editDriver = mutableStateOf<DriverDto?>(null)
    val editDriver: State<DriverDto?> get() = _editDriver

    fun goBack() {
        navController.popBackStack()
    }

    fun getDriverList() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = driverRepository.getDrivers(Constants.TOKEN, Constants.ENTREPRENEUR_ID)
            if (result is Resource.Success) {
                val drivers = result.data
                if (drivers != null) {
                    val driversInfo = drivers.map { driver ->
                        DriverDto(
                            id = driver.id,
                            name = driver.name,
                            dni = driver.dni,
                            license = driver.license,
                            contactNumber = driver.contactNumber,
                            entrepreneurId = driver.entrepreneurId
                        )
                    }
                    _state.value = UIState(data = driversInfo)
                } else {
                    _state.value = UIState(message = "No drivers found")
                }
            } else if (result is Resource.Error) {
                _state.value = UIState(message = "Error with drivers")
            }
        }
    }

    fun setEditDriver(driver: DriverDto) {
        _editDriver.value = driver
    }
}