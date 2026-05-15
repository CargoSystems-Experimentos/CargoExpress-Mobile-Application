package com.cargoexpress.app.core.presentation.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cargoexpress.app.R
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.presentation.phoneauth.OtpPhase
import com.cargoexpress.app.core.presentation.phoneauth.OtpVerificationDialog
import com.cargoexpress.app.core.presentation.phoneauth.PhoneAuthHelper

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    val state by viewModel.state.observeAsState(UIState())
    val otpPhase by viewModel.otpPhase.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var emailState by rememberSaveable { mutableStateOf("") }
    var passwordState by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    // Trigger SMS when phase transitions to Sending or Resending
    LaunchedEffect(otpPhase) {
        val activity = context as? Activity ?: return@LaunchedEffect
        when (otpPhase) {
            is OtpPhase.Sending -> {
                val phone = (otpPhase as OtpPhase.Sending).phone
                PhoneAuthHelper.enviarCodigo(
                    phoneNumber = phone,
                    activity = activity,
                    onCodeSent = { viewModel.onOtpSent() },
                    onAutoVerified = { viewModel.onOtpAutoVerified() },
                    onError = { viewModel.onOtpSendError(it) }
                )
            }
            is OtpPhase.Resending -> {
                val phone = (otpPhase as OtpPhase.Resending).phone
                PhoneAuthHelper.reenviarCodigo(
                    phoneNumber = phone,
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
        onVerify = { code -> viewModel.verifyOtp(code) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Bienvenido a CargoExpress",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = emailState,
                    onValueChange = { if (it.length <= 100) emailState = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo electrónico") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                PasswordTextField(password = passwordState, onPasswordChange = { passwordState = it })

                Button(
                    onClick = { viewModel.signIn(emailState, passwordState) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE4D911)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Iniciar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                ClickableText(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(color = Color.White))
                        append("¿No tienes una cuenta? ")
                        pushStyle(SpanStyle(color = Color(0xFFE4D911), fontWeight = FontWeight.Bold))
                        append("Crear una cuenta")
                        pop()
                    },
                    onClick = { navController.navigate(Routes.Register.routes) },
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { if (it.length <= 100) onPasswordChange(it) },
        label = { Text("Contraseña") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Contraseña"
            )
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}
