package com.cargoexpress.app.core.presentation.driver.driverList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.repository.DriverRepository
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState

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
            try {
                if (Constants.TOKEN.isBlank() || Constants.ENTREPRENEUR_ID == 0) {
                    _state.value = UIState(
                        isLoading = false,
                        message = "Token o Entrepreneur ID no inicializados"
                    )
                    return@launch
                }

                val result = driverRepository.getDrivers(Constants.TOKEN, Constants.ENTREPRENEUR_ID)

                if (result is Resource.Success) {
                    val drivers = result.data
                    if (drivers != null && drivers.isNotEmpty()) {
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
                        _state.value = UIState(data = driversInfo, isLoading = false)
                    } else {
                        _state.value = UIState(
                            isLoading = false,
                            message = "No hay conductores registrados para este empresario",
                            data = emptyList()
                        )
                    }
                } else if (result is Resource.Error) {
                    val errorMsg = result.message
                    _state.value = UIState(
                        isLoading = false,
                        message = "Error al cargar conductores: $errorMsg"
                    )
                }
            } catch (e: Exception) {
                _state.value = UIState(
                    isLoading = false,
                    message = "Excepción: ${e.message}"
                )
            }
        }
    }

    fun setEditDriver(driver: DriverDto) {
        _editDriver.value = driver
    }
}