package com.cargoexpress.app.core.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    fun registerClient(
        username: String,
        password: String,
        phone: String,
        name: String,
        dni: String,
        birthDate: String
    ) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            registerRepository.registerClient(username, password, phone, name, dni, birthDate) { result ->
                result.onSuccess {
                    _state.value = UIState(data = Unit, message = "¡Cuenta creada exitosamente!")
                }.onFailure { exception ->
                    _state.value = UIState(message = exception.message ?: "Error al registrar")
                }
            }
        }
    }

    fun registerEntrepreneur(
        username: String,
        password: String,
        phone: String,
        name: String,
        ruc: String,
        address: String
    ) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            registerRepository.registerEntrepreneur(username, password, phone, name, ruc, address) { result ->
                result.onSuccess {
                    _state.value = UIState(data = Unit, message = "¡Cuenta creada exitosamente!")
                }.onFailure { exception ->
                    _state.value = UIState(message = exception.message ?: "Error al registrar")
                }
            }
        }
    }

    fun clearState() {
        _state.value = UIState()
    }
}
