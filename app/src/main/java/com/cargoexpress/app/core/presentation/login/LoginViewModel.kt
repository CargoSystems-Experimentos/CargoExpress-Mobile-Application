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
import android.util.Log
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.UIState

class LoginViewModel(
    private val navController: NavController,
    private val loginRepository: LoginRepository,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    fun signIn(username: String, password: String) {
        _state.value = UIState(isLoading = true)
        Log.d(TAG, "Starting credential sign-in for username=$username")
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    Log.d(TAG, "Credential sign-in success. userId=${loginResponse.id}")
                    Constants.USER_ID = loginResponse.id
                    Constants.TOKEN = loginResponse.token
                    Constants.USER_NAME = loginResponse.username

                    // Intenta determinar si es entrepreneur o cliente
                    determineUserRole(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    Log.e(TAG, "Credential sign-in failed", exception)
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Correo y/o contraseña incorrectos: $message")
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _state.value = UIState(isLoading = true)
        Log.d(TAG, "Starting Google sign-in with idToken length=${idToken.length}")
        viewModelScope.launch {
            loginRepository.googleSignIn(idToken) { result ->
                result.onSuccess { loginResponse ->
                    Log.d(TAG, "Google sign-in success. userId=${loginResponse.id}")
                    Constants.USER_ID = loginResponse.id
                    Constants.TOKEN = loginResponse.token
                    Constants.USER_NAME = loginResponse.username
                    determineUserRole(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    Log.e(TAG, "Google sign-in failed from backend", exception)
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(isLoading = false, message = "Error en Google Sign-In: $message")
                }
            }
        }
    }

    fun onGoogleSignInClientError(message: String) {
        Log.e(TAG, "Google Sign-In client error: $message")
        _state.value = UIState(isLoading = false, message = message)
    }

    private fun determineUserRole(userId: Int, token: String) {
        Log.d(TAG, "Resolving user role for userId=$userId")
        viewModelScope.launch {
            entrepreneurRepository.getEntrepreneurByUserId(userId, token).onSuccess { entrepreneur ->
                Log.d(TAG, "User role resolved as ENTREPRENEUR. entrepreneurId=${entrepreneur.id}")
                Constants.ENTREPRENEUR_ID = entrepreneur.id
                Constants.USER_ROLE = "ENTREPRENEUR"
                _state.value = UIState(isLoading = false)
                goToTripListScreen()
            }.onFailure {
                clientRepository.getClientByUserId(userId, token).onSuccess { client ->
                    Log.d(TAG, "User role resolved as CLIENT")
                    Constants.USER_ROLE = "CLIENT"
                    Constants.ENTREPRENEUR_ID = 0
                    _state.value = UIState(isLoading = false)
                    goToClientHomeScreen()
                }.onFailure {
                    Log.w(TAG, "No profile found. Redirecting to register to complete profile")
                    _state.value = UIState(isLoading = false)
                    navController.navigate(Routes.Register.routes)
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
