package com.cargoexpress.app.core.presentation.vehicle

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Resource
import pe.edu.upc.appturismo.common.UIState

class VehicleListViewModel(
    private val navController: NavHostController,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = mutableStateOf(UIState<List<Vehicle>>())
    val state: State<UIState<List<Vehicle>>> get() = _state

    fun getVehiclesForEntrepreneur(entrepreneurId: Int, token: String) {
        viewModelScope.launch {
            _state.value = UIState(isLoading = true)
            val result = vehicleRepository.getVehicleList(token, entrepreneurId)
            _state.value = when (result) {
                is Resource.Success -> UIState(data = result.data)
                is Resource.Error -> UIState(message = result.message ?: "An unknown error occurred")
            }
        }
    }
}