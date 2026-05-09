package com.cargoexpress.app.core.presentation.login




import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.UIState

class LoginViewModel(
    private val navController: NavController,
    private val loginRepository: LoginRepository,
    private val entrepreneurRepository: EntrepreneurRepository
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


                    getEntrepreneurIdForUser(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    val message = exception.message ?: "Error desconocido"
                    _state.value = UIState(message = "Correo y/o contraseña incorrectos / $message")
                }
            }
        }
    }


    private fun getEntrepreneurIdForUser(userId: Int, token: String) {
        viewModelScope.launch {
            entrepreneurRepository.getEntrepreneurByUserId(
                userId = userId.toInt(),
                token = token
            ).onSuccess { entrepreneur ->

                Constants.ENTREPRENEUR_ID = entrepreneur.id


                goToRegistroScreen()
            }.onFailure { exception ->
                val message = exception.message ?: "Error obteniendo el ID del empresario"
                _state.value = UIState(message = message)
            }
        }
    }


    private fun goToVehicleScreen() {
        navController.navigate(Routes.VehicleList.routes)
    }

    private fun goToRegistroScreen(){
        navController.navigate(Routes.TripList.routes)
    }

    private fun goToRegisterScreen() {
        navController.navigate(Routes.Register.routes)
    }
}