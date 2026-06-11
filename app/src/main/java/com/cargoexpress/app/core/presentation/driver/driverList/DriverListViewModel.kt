package com.cargoexpress.app.core.presentation.driver.driverList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.domain.Driver
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState

class DriverListViewModel(private val navController: NavController, private val driverRepository: DriverRepository)
    : ViewModel() {

    private val _state = mutableStateOf(UIState<List<Driver>>())
    val state: State<UIState<List<Driver>>> get() = _state

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
                _state.value = when (result) {
                    is Resource.Success -> UIState(data = result.data ?: emptyList(), isLoading = false)
                    is Resource.Error -> UIState(
                        isLoading = false,
                        message = "Error al cargar conductores: ${result.message}"
                    )
                }
            } catch (e: Exception) {
                _state.value = UIState(isLoading = false, message = "Excepción: ${e.message}")
            }
        }
    }
}
