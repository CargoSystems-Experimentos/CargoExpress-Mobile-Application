package com.cargoexpress.app.core.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurDto
import com.cargoexpress.app.core.data.remote.vehicle.VehicleDto
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val navController: NavController,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _entrepreneurState = mutableStateOf(UIState<EntrepreneurDto>())
    val entrepreneurState: State<UIState<EntrepreneurDto>> get() = _entrepreneurState

    private val _clientState = mutableStateOf(UIState<ClientDto>())
    val clientState: State<UIState<ClientDto>> get() = _clientState

    private val _vehiclesState = mutableStateOf(UIState<List<VehicleDto>>())
    val vehiclesState: State<UIState<List<VehicleDto>>> get() = _vehiclesState

    private val _driversState = mutableStateOf(UIState<List<DriverDto>>())
    val driversState: State<UIState<List<DriverDto>>> get() = _driversState

    fun getEntrepreneurProfile(entrepreneurId: Int) {
        _entrepreneurState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = entrepreneurRepository.getEntrepreneurById(entrepreneurId, Constants.TOKEN)
            if (result.isSuccess) {
                _entrepreneurState.value = UIState(data = result.getOrNull())
                loadVehiclesAndDrivers(result.getOrNull()?.id ?: 0)
            } else {
                _entrepreneurState.value = UIState(message = "Error retrieving profile")
            }
        }
    }

    fun getClientProfile(userId: Int) {
        _clientState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = clientRepository.getClientByUserId(userId, Constants.TOKEN)
            if (result.isSuccess) {
                _clientState.value = UIState(data = result.getOrNull())
            } else {
                _clientState.value = UIState(message = "Error retrieving client profile")
            }
        }
    }

    private fun loadVehiclesAndDrivers(entrepreneurId: Int) {
        loadVehicles(entrepreneurId)
        loadDrivers(entrepreneurId)
    }

    private fun loadVehicles(entrepreneurId: Int) {
        _vehiclesState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = entrepreneurRepository.getVehiclesByEntrepreneurId(entrepreneurId, Constants.TOKEN)
            if (result.isSuccess) {
                _vehiclesState.value = UIState(data = result.getOrNull())
            } else {
                _vehiclesState.value = UIState(message = "Error retrieving vehicles")
            }
        }
    }

    private fun loadDrivers(entrepreneurId: Int) {
        _driversState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = entrepreneurRepository.getDriversByEntrepreneurId(entrepreneurId, Constants.TOKEN)
            if (result is Resource.Success) {
                _driversState.value = UIState(data = result.data)
            } else {
                _driversState.value = UIState(message = result.message ?: "Error retrieving drivers")
            }
        }
    }

    fun logOut() {
        Constants.TOKEN = ""
        Constants.USER_ID = 0
        Constants.USER_NAME = ""
        Constants.ENTREPRENEUR_ID = 0
        Constants.USER_ROLE = ""
        goToLoginScreen()
    }

    private fun goToLoginScreen() {
        navController.navigate(Routes.Login.routes)
    }
}