package com.cargoexpress.app.core.presentation.register

import android.app.Activity
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.presentation.phoneauth.OtpPhase
import com.cargoexpress.app.core.presentation.phoneauth.OtpVerificationDialog
import com.cargoexpress.app.core.presentation.phoneauth.PhoneAuthHelper

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val state by viewModel.state.observeAsState(UIState())
    val otpPhase by viewModel.otpPhase.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isClient by remember { mutableStateOf(true) }

    // Trigger SMS when phase transitions to Sending or Resending
    LaunchedEffect(otpPhase) {
        val activity = context as? Activity ?: return@LaunchedEffect
        when (otpPhase) {
            is OtpPhase.Sending -> {
                val phoneNum = (otpPhase as OtpPhase.Sending).phone
                PhoneAuthHelper.enviarCodigo(
                    phoneNumber = phoneNum,
                    activity = activity,
                    onCodeSent = { viewModel.onOtpSent() },
                    onAutoVerified = { viewModel.onOtpAutoVerified() },
                    onError = { viewModel.onOtpSendError(it) }
                )
            }
            is OtpPhase.Resending -> {
                val phoneNum = (otpPhase as OtpPhase.Resending).phone
                PhoneAuthHelper.reenviarCodigo(
                    phoneNumber = phoneNum,
                    activity = activity,
                    onCodeSent = { viewModel.onOtpSent() },
                    onAutoVerified = { viewModel.onOtpAutoVerified() },
                    onError = { viewModel.onOtpSendError(it) }
                )
            }
            else -> {}
        }
    }

    LaunchedEffect(state.message) {
        if (state.message.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.message)
        }
    }

    OtpVerificationDialog(
        otpPhase = otpPhase,
        onVerify = { code -> viewModel.verifyOtpAndRegister(code) },
        onResend = { viewModel.resendOtp() },
        onCancel = { viewModel.cancelOtp() }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Column {
                    Text(
                        text = "CARGOEXPRESS",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            letterSpacing = 1.5.sp,
                            fontSize = 17.sp
                        ),
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Crea tu cuenta",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Únete a nuestra red logística",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Text(
                    text = "¿Que función desempeñarás en CargoExpress?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = { isClient = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClient) Color.Yellow else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cliente", color = if (isClient) Color.Black else Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = { isClient = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isClient) Color.Yellow else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Empresario", color = if (!isClient) Color.Black else Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                val isEmailValid = username.isBlank() || Patterns.EMAIL_ADDRESS.matcher(username).matches()
                val showEmailError = username.isNotBlank() && !isEmailValid

                val hasUppercase = password.any { it.isUpperCase() }
                val hasNumber = password.any { it.isDigit() }
                val hasSpecialChar = password.any { !it.isLetterOrDigit() }
                val hasMinLength = password.length >= 8
                val isPasswordValid = password.isBlank() || (hasUppercase && hasNumber && hasSpecialChar && hasMinLength)
                val showPasswordError = password.isNotBlank() && !isPasswordValid

                val isDniValid = dni.length == 8 && dni.all { it.isDigit() }
                val showDniError = dni.isNotBlank() && !isDniValid
                val isRucValid = ruc.length == 11 && ruc.all { it.isDigit() }
                val showRucError = ruc.isNotBlank() && !isRucValid
                val isPhoneValid = phone.length == 9 && phone.all { it.isDigit() }
                val showPhoneError = phone.isNotBlank() && !isPhoneValid
                val idFieldValid = if (isClient) (dni.isNotBlank() && isDniValid) else (ruc.isNotBlank() && isRucValid)

                val isFormValid = username.isNotBlank() &&
                        password.isNotBlank() &&
                        name.isNotBlank() &&
                        isPhoneValid &&
                        isEmailValid &&
                        isPasswordValid &&
                        idFieldValid

                OutlinedTextField(
                    value = username,
                    onValueChange = { if (it.length <= 100) username = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    isError = showEmailError,
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showEmailError) {
                        Text(
                            text = "El correo electrónico no es válido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = "${username.length}/100",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { if (it.length <= 100) password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                        }
                    },
                    supportingText = {
                        if (showPasswordError) Text("La contraseña debe tener mínimo 8 caracteres, una letra mayúscula, un carácter especial y un número", color = Color.Red)
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 60) name = it },
                    label = { Text(if (isClient) "Nombre completo" else "Nombre de la empresa") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    supportingText = {
                        Text(
                            text = "${name.length}/60",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                var rawPhone by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Peru", modifier = Modifier.padding(end = 8.dp), color = Color.Gray)
                    OutlinedTextField(
                        value = rawPhone,
                        onValueChange = { input ->
                            rawPhone = input.filter { it.isDigit() }.take(9)
                            phone = rawPhone
                        },
                        label = { Text("Número de celular") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showPhoneError,
                        visualTransformation = PhoneVisualTransformation(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (isClient) {
                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it.filter(Char::isDigit).take(8) },
                        label = { Text("DNI") },
                        leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showDniError,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    )
                    if (showDniError) {
                        Text("El DNI no es válido", color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    OutlinedTextField(
                        value = ruc,
                        onValueChange = { ruc = it.filter(Char::isDigit).take(11) },
                        label = { Text("RUC") },
                        leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showRucError,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    )
                    if (showRucError) {
                        Text("El RUC no es válido", color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isClient) {
                            viewModel.initiateClientRegistration(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                dni = dni
                            )
                        } else {
                            viewModel.initiateEntrepreneurRegistration(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                ruc = ruc
                            )
                        }
                    },
                    enabled = isFormValid && !state.isLoading && otpPhase == OtpPhase.Idle,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        "Crear Cuenta",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Términos y Condiciones",
                    color = Color(0xFF2196F3),
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Routes.TermsAndConditions.routes) },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "¿Tienes una cuenta? Accede desde aquí",
                    color = Color(0xFF2196F3),
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Routes.Login.routes) },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val out = StringBuilder()
        for (i in digits.indices) {
            if (i == 3 || i == 6) out.append('-')
            out.append(digits[i])
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var result = offset
                if (offset >= 3 && digits.length > 3) result++
                if (offset >= 6 && digits.length > 6) result++
                return result
            }
            override fun transformedToOriginal(offset: Int): Int {
                var result = offset
                if (digits.length > 3 && offset > 3) result--
                if (digits.length > 6 && offset > 7) result--
                return result.coerceIn(0, digits.length)
            }
        }
        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
