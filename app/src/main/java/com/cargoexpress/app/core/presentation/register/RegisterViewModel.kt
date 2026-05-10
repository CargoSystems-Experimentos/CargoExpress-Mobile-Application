package com.cargoexpress.app.core.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.data.remote.user.ClientRequestDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurRequestDto
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.RegisterRepository
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.UIState

class RegisterViewModel(
    private val navController: NavController,
    private val registerRepository: RegisterRepository,
    private val loginRepository: LoginRepository,
    private val clientRepository: ClientRepository,
    private val entrepreneurRepository: EntrepreneurRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    // Para cliente
    fun signUpClient(
        username: String,
        password: String,
        name: String,
        phone: String,
        dni: String
    ) {
        _state.value = UIState(isLoading = true)

        viewModelScope.launch {
            registerRepository.registerUser(username, password) { result ->
                result.onSuccess { message ->
                    loginAfterRegisterClient(username, password, name, phone, dni)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error en el registro de usuario: $message")
                }
            }
        }
    }

    // Para entrepreneur
    fun signUpEntrepreneur(
        username: String,
        password: String,
        name: String,
        phone: String,
        ruc: String,
        logoImage: String
    ) {
        _state.value = UIState(isLoading = true)

        viewModelScope.launch {
            registerRepository.registerUser(username, password) { result ->
                result.onSuccess { message ->
                    loginAfterRegisterEntrepreneur(username, password, name, phone, ruc, logoImage)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error en el registro de usuario: $message")
                }
            }
        }
    }

    private fun loginAfterRegisterClient(
        username: String,
        password: String,
        name: String,
        phone: String,
        dni: String
    ) {
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    val userId = loginResponse.id
                    val token = loginResponse.token
                    createClient(userId, token, name, phone, dni)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error al iniciar sesión después del registro: $message")
                }
            }
        }
    }

    private fun loginAfterRegisterEntrepreneur(
        username: String,
        password: String,
        name: String,
        phone: String,
        ruc: String,
        logoImage: String
    ) {
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    val userId = loginResponse.id
                    val token = loginResponse.token
                    createEntrepreneur(userId, token, name, phone, ruc, logoImage)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error al iniciar sesión después del registro: $message")
                }
            }
        }
    }

    private fun createClient(userId: Int, token: String, name: String, phone: String, dni: String) {
        viewModelScope.launch {
            val clientRequest = ClientRequestDto(
                name = name,
                phone = phone,
                dni = dni,
                userId = userId
            )

            clientRepository.createClient(clientRequest, token)
                .onSuccess {
                    _state.value = UIState(isLoading = false, message = "Usuario y cliente creados exitosamente")
                    navController.navigate(Routes.Login.routes)
                }
                .onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error al crear el cliente: $message")
                }
        }
    }

    private fun createEntrepreneur(
        userId: Int,
        token: String,
        name: String,
        phone: String,
        ruc: String,
        logoImage: String
    ) {
        viewModelScope.launch {
            android.util.Log.d(
                "RegisterViewModel",
                "Creating entrepreneur with userId=$userId, logoImage=$logoImage"
            )
            val entrepreneurRequest = EntrepreneurRequestDto(
                name = name,
                phone = phone,
                ruc = ruc,
                logoImage = logoImage,
                userId = userId
            )

            entrepreneurRepository.createEntrepreneur(entrepreneurRequest, token)
                .onSuccess {
                    _state.value = UIState(isLoading = false, message = "Usuario y entrepreneur creados exitosamente")
                    navController.navigate(Routes.Login.routes)
                }
                .onFailure { exception: Throwable ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error al crear el entrepreneur: $message")
                }
        }
    }
}