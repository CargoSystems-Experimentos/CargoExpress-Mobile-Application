package com.cargoexpress.app.core.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.ClientRepository
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.UIState

class LoginViewModel(
    private val navController: NavController,
    private val loginRepository: LoginRepository,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    fun signIn(username: String, password: String) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    Constants.USER_ID = loginResponse.id
                    Constants.TOKEN = loginResponse.token
                    Constants.USER_NAME = loginResponse.username

                    // Intenta determinar si es entrepreneur o cliente
                    determineUserRole(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Correo y/o contraseña incorrectos: $message")
                }
            }
        }
    }
    private fun determineUserRole(userId: Int, token: String) {
        viewModelScope.launch {
            entrepreneurRepository.getEntrepreneurByUserId(userId, token).onSuccess { entrepreneur ->
                Constants.ENTREPRENEUR_ID = entrepreneur.id
                Constants.USER_ROLE = "ENTREPRENEUR"
                _state.value = UIState(isLoading = false)
                goToTripListScreen()
            }.onFailure {
                clientRepository.getClientByUserId(userId, token).onSuccess { client ->
                    Constants.USER_ROLE = "CLIENT"
                    Constants.ENTREPRENEUR_ID = 0
                    _state.value = UIState(isLoading = false)
                    goToClientHomeScreen()
                }.onFailure {
                    _state.value = UIState(isLoading = false, message = "No se encontró perfil de entrepreneur ni cliente")
                }
            }
        }
    }

    private fun goToTripListScreen() {
        navController.navigate(Routes.TripList.routes)
    }

    private fun goToClientHomeScreen() {
        // Navega a la pantalla principal del cliente
        // Puedes cambiar esto según tu estructura de rutas
        navController.navigate(Routes.TripList.routes) // O la ruta que uses para clientes
    }
}