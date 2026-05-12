package com.cargoexpress.app.core.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.presentation.phoneauth.OtpPhase
import com.cargoexpress.app.core.presentation.phoneauth.PhoneAuthHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navController: NavController,
    private val loginRepository: LoginRepository,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    private val _otpPhase = MutableStateFlow<OtpPhase>(OtpPhase.Idle)
    val otpPhase: StateFlow<OtpPhase> = _otpPhase

    // Pending profile data, set only after OTP verification
    private var pendingUserId = 0
    private var pendingToken = ""
    private var pendingUsername = ""
    private var pendingRole = ""
    private var pendingEntrepreneurId = 0
    private var pendingClientId = 0
    private var pendingPhone = ""

    fun signIn(username: String, password: String) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            loginRepository.signIn(username, password) { result ->
                result.onSuccess { loginResponse ->
                    pendingUserId = loginResponse.id
                    pendingToken = loginResponse.token
                    pendingUsername = loginResponse.username
                    fetchProfileAndPrepareOtp(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    _state.value = UIState(isLoading = false, message = "Correo y/o contraseña incorrectos: ${exception.message}")
                }
            }
        }
    }

    private fun fetchProfileAndPrepareOtp(userId: Int, token: String) {
        viewModelScope.launch {
            entrepreneurRepository.getEntrepreneurByUserId(userId, token).onSuccess { entrepreneur ->
                pendingRole = "ENTREPRENEUR"
                pendingEntrepreneurId = entrepreneur.id
                pendingClientId = 0
                pendingPhone = entrepreneur.phone
                _state.value = UIState(isLoading = false)
                _otpPhase.value = OtpPhase.Sending(formatPhone(entrepreneur.phone))
            }.onFailure {
                clientRepository.getClientByUserId(userId, token).onSuccess { client ->
                    pendingRole = "CLIENT"
                    pendingClientId = client.id
                    pendingEntrepreneurId = 0
                    pendingPhone = client.phone
                    _state.value = UIState(isLoading = false)
                    _otpPhase.value = OtpPhase.Sending(formatPhone(client.phone))
                }.onFailure {
                    _state.value = UIState(isLoading = false, message = "No se encontró perfil de entrepreneur ni cliente")
                }
            }
        }
    }

    fun onOtpSent() {
        val phone = phoneFromSendingPhase() ?: return
        _otpPhase.value = OtpPhase.AwaitingCode(phone)
    }

    fun onOtpAutoVerified() {
        commitSessionAndNavigate()
    }

    fun onOtpSendError(error: String) {
        _otpPhase.value = OtpPhase.Idle
        _state.value = UIState(message = "Error al enviar código: $error")
        clearPendingSession()
    }

    fun verifyOtp(code: String) {
        val awaiting = _otpPhase.value as? OtpPhase.AwaitingCode ?: return
        _otpPhase.value = OtpPhase.Verifying
        PhoneAuthHelper.verificarCodigo(
            codigo = code,
            onSuccess = { commitSessionAndNavigate() },
            onError = { error ->
                _otpPhase.value = OtpPhase.AwaitingCode(awaiting.phone, error = "Código incorrecto. Intenta de nuevo.")
            }
        )
    }

    fun resendOtp() {
        val phone = when (val phase = _otpPhase.value) {
            is OtpPhase.AwaitingCode -> phase.phone
            else -> return
        }
        _otpPhase.value = OtpPhase.Resending(phone)
    }

    fun cancelOtp() {
        _otpPhase.value = OtpPhase.Idle
        PhoneAuthHelper.reset()
        clearPendingSession()
    }

    private fun commitSessionAndNavigate() {
        Constants.USER_ID = pendingUserId
        Constants.TOKEN = pendingToken
        Constants.USER_NAME = pendingUsername
        Constants.USER_ROLE = pendingRole
        Constants.ENTREPRENEUR_ID = pendingEntrepreneurId
        Constants.CLIENT_ID = pendingClientId
        _otpPhase.value = OtpPhase.Idle
        navController.navigate(Routes.TripList.routes)
    }

    private fun clearPendingSession() {
        pendingUserId = 0
        pendingToken = ""
        pendingUsername = ""
        pendingRole = ""
        pendingEntrepreneurId = 0
        pendingClientId = 0
        pendingPhone = ""
    }

    private fun phoneFromSendingPhase(): String? = when (val p = _otpPhase.value) {
        is OtpPhase.Sending -> p.phone
        is OtpPhase.Resending -> p.phone
        else -> null
    }

    private fun formatPhone(phone: String): String {
        val digits = phone.filter(Char::isDigit)
        return if (phone.startsWith("+")) "+$digits" else "+51$digits"
    }
}
