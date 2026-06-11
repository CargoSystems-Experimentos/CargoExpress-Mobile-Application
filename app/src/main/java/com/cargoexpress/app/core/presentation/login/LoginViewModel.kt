package com.cargoexpress.app.core.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navController: NavController,
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    private var pendingUserId = 0
    private var pendingToken = ""
    private var pendingUsername = ""

    fun signIn(username: String, password: String) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    pendingUserId = loginResponse.id
                    pendingToken = loginResponse.token
                    pendingUsername = loginResponse.username
                    fetchRoleAndNavigate(loginResponse.id, loginResponse.token)
                }.onFailure {
                    _state.value = UIState(isLoading = false, message = "Correo y/o contraseña incorrectos")
                }
            }
        }
    }

    private fun fetchRoleAndNavigate(userId: Int, token: String) {
        viewModelScope.launch {
            when (val roleResource = userRepository.getUserRole(userId, token)) {
                is Resource.Success -> {
                    val isEntrepreneur = roleResource.data ?: false
                    if (isEntrepreneur) fetchEntrepreneurProfile(userId, token)
                    else fetchClientProfile(userId, token)
                }
                is Resource.Error -> {
                    _state.value = UIState(isLoading = false, message = "No se pudo obtener el rol del usuario")
                }
            }
        }
    }

    private fun fetchEntrepreneurProfile(userId: Int, token: String) {
        viewModelScope.launch {
            entrepreneurRepository.getAllEntrepreneurs(token).onSuccess { entrepreneurs ->
                val entrepreneur = entrepreneurs.find { it.userId == userId }
                if (entrepreneur != null) {
                    commitSessionAndNavigate("ENTREPRENEUR", entrepreneur.id, 0)
                } else {
                    _state.value = UIState(isLoading = false, message = "No se encontró perfil de empresario")
                }
            }.onFailure {
                _state.value = UIState(isLoading = false, message = "Error al obtener perfil de empresario")
            }
        }
    }

    private fun fetchClientProfile(userId: Int, token: String) {
        viewModelScope.launch {
            clientRepository.getAllClients(token).onSuccess { clients ->
                val client = clients.find { it.userId == userId }
                if (client != null) {
                    commitSessionAndNavigate("CLIENT", 0, client.id)
                } else {
                    _state.value = UIState(isLoading = false, message = "No se encontró perfil de cliente")
                }
            }.onFailure {
                _state.value = UIState(isLoading = false, message = "Error al obtener perfil de cliente")
            }
        }
    }

    private fun commitSessionAndNavigate(role: String, entrepreneurId: Int, clientId: Int) {
        Constants.USER_ID = pendingUserId
        Constants.TOKEN = pendingToken
        Constants.USER_NAME = pendingUsername
        Constants.USER_ROLE = role
        Constants.ENTREPRENEUR_ID = entrepreneurId
        Constants.CLIENT_ID = clientId
        _state.value = UIState(isLoading = false)
        navController.navigate(Routes.TripList.routes)
    }
}
