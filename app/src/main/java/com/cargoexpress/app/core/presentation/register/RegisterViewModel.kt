package com.cargoexpress.app.core.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.remote.user.ClientRequestDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurRequestDto
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.RegisterRepository
import com.cargoexpress.app.core.presentation.phoneauth.OtpPhase
import com.cargoexpress.app.core.presentation.phoneauth.PhoneAuthHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val navController: NavController,
    private val registerRepository: RegisterRepository,
    private val loginRepository: LoginRepository,
    private val clientRepository: ClientRepository,
    private val entrepreneurRepository: EntrepreneurRepository
) : ViewModel() {

    private val _state = MutableLiveData<UIState<Unit>>(UIState())
    val state: LiveData<UIState<Unit>> get() = _state

    private val _otpPhase = MutableStateFlow<OtpPhase>(OtpPhase.Idle)
    val otpPhase: StateFlow<OtpPhase> = _otpPhase

    // Pending registration data
    private var pendingUsername = ""
    private var pendingPassword = ""
    private var pendingName = ""
    private var pendingPhone = ""
    private var pendingDni = ""
    private var pendingRuc = ""
    private var pendingIsClient = true

    fun initiateClientRegistration(
        username: String,
        password: String,
        name: String,
        phone: String,
        dni: String
    ) {
        pendingUsername = username
        pendingPassword = password
        pendingName = name
        pendingPhone = phone
        pendingDni = dni
        pendingIsClient = true
        _otpPhase.value = OtpPhase.Sending(formatPhone(phone))
    }

    fun initiateEntrepreneurRegistration(
        username: String,
        password: String,
        name: String,
        phone: String,
        ruc: String
    ) {
        pendingUsername = username
        pendingPassword = password
        pendingName = name
        pendingPhone = phone
        pendingRuc = ruc
        pendingIsClient = false
        _otpPhase.value = OtpPhase.Sending(formatPhone(phone))
    }

    fun onOtpSent() {
        val phone = when (val p = _otpPhase.value) {
            is OtpPhase.Sending -> p.phone
            is OtpPhase.Resending -> p.phone
            else -> return
        }
        _otpPhase.value = OtpPhase.AwaitingCode(phone)
    }

    fun onOtpAutoVerified() {
        proceedWithRegistration()
    }

    fun onOtpSendError(error: String) {
        _otpPhase.value = OtpPhase.Idle
        _state.value = UIState(message = "Error al enviar código: $error")
        clearPendingData()
    }

    fun verifyOtpAndRegister(code: String) {
        val awaiting = _otpPhase.value as? OtpPhase.AwaitingCode ?: return
        _otpPhase.value = OtpPhase.Verifying
        PhoneAuthHelper.verificarCodigo(
            codigo = code,
            onSuccess = { proceedWithRegistration() },
            onError = {
                _otpPhase.value = OtpPhase.AwaitingCode(awaiting.phone, error = "Código incorrecto. Intenta de nuevo.")
            }
        )
    }

    fun resendOtp() {
        val phone = (_otpPhase.value as? OtpPhase.AwaitingCode)?.phone ?: return
        _otpPhase.value = OtpPhase.Resending(phone)
    }

    fun cancelOtp() {
        _otpPhase.value = OtpPhase.Idle
        PhoneAuthHelper.reset()
        clearPendingData()
    }

    private fun proceedWithRegistration() {
        val profileError = validateProfileFields()
        if (profileError != null) {
            _otpPhase.value = OtpPhase.Idle
            _state.value = UIState(message = profileError)
            clearPendingData()
            return
        }
        _otpPhase.value = OtpPhase.Idle
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            registerRepository.registerUser(pendingUsername, pendingPassword) { result ->
                result.onSuccess {
                    if (pendingIsClient) loginAfterRegisterClient()
                    else loginAfterRegisterEntrepreneur()
                }.onFailure { exception ->
                    _state.value = UIState(isLoading = false, message = "Error en el registro: ${exception.message}")
                }
            }
        }
    }

    private fun validateProfileFields(): String? {
        if (pendingName.isBlank()) return "El nombre no puede estar vacío"
        val phoneDigits = pendingPhone.filter(Char::isDigit)
        if (phoneDigits.length != 9) return "El número de celular debe tener 9 dígitos"
        if (pendingIsClient) {
            if (pendingDni.length != 8 || !pendingDni.all(Char::isDigit))
                return "El DNI debe tener 8 dígitos numéricos"
        } else {
            if (pendingRuc.length != 11 || !pendingRuc.all(Char::isDigit))
                return "El RUC debe tener 11 dígitos numéricos"
            if (!pendingRuc.startsWith("10") && !pendingRuc.startsWith("20"))
                return "El RUC debe comenzar con 10 o 20"
        }
        return null
    }

    private fun loginAfterRegisterClient() {
        viewModelScope.launch {
            loginRepository.signIn(pendingUsername, pendingPassword) { result ->
                result.onSuccess { loginResponse ->
                    createClient(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    _state.value = UIState(isLoading = false, message = "Error al iniciar sesión después del registro: ${exception.message}")
                }
            }
        }
    }

    private fun loginAfterRegisterEntrepreneur() {
        viewModelScope.launch {
            loginRepository.signIn(pendingUsername, pendingPassword) { result ->
                result.onSuccess { loginResponse ->
                    createEntrepreneur(loginResponse.id, loginResponse.token)
                }.onFailure { exception ->
                    _state.value = UIState(isLoading = false, message = "Error al iniciar sesión después del registro: ${exception.message}")
                }
            }
        }
    }

    private fun createClient(userId: Int, token: String) {
        viewModelScope.launch {
            clientRepository.createClient(
                ClientRequestDto(name = pendingName, phone = pendingPhone, dni = pendingDni, userId = userId),
                token
            ).onSuccess {
                _state.value = UIState(isLoading = false, message = "Cuenta creada exitosamente")
                clearPendingData()
                navController.navigate(Routes.Login.routes)
            }.onFailure { exception ->
                _state.value = UIState(isLoading = false, message = "Error al crear el cliente: ${exception.message}")
            }
        }
    }

    private fun createEntrepreneur(userId: Int, token: String) {
        viewModelScope.launch {
            entrepreneurRepository.createEntrepreneur(
                EntrepreneurRequestDto(name = pendingName, phone = pendingPhone, ruc = pendingRuc, logoImage = "", userId = userId),
                token
            ).onSuccess {
                _state.value = UIState(isLoading = false, message = "Cuenta creada exitosamente")
                clearPendingData()
                navController.navigate(Routes.Login.routes)
            }.onFailure { exception ->
                _state.value = UIState(isLoading = false, message = "Error al crear el empresario: ${exception.message}")
            }
        }
    }

    private fun clearPendingData() {
        pendingUsername = ""
        pendingPassword = ""
        pendingName = ""
        pendingPhone = ""
        pendingDni = ""
        pendingRuc = ""
    }

    private fun formatPhone(phone: String): String {
        val digits = phone.filter(Char::isDigit)
        return if (phone.startsWith("+")) "+$digits" else "+51$digits"
    }
}
