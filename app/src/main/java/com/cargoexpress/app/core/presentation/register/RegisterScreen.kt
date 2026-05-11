package com.cargoexpress.app.core.presentation.register
import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.UIState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.presentation.ImagePicker

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val state by viewModel.state.observeAsState(UIState())
    val snackbarHostState = remember { SnackbarHostState() }
    val isGoogleOnboarding = Constants.TOKEN.isNotBlank() && Constants.USER_ID > 0 && Constants.USER_NAME.isNotBlank()

    var username by remember { mutableStateOf(if (isGoogleOnboarding) Constants.USER_NAME else "") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var isClient by remember { mutableStateOf(true) }

    LaunchedEffect(state.message) {
        if (state.message.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.message)
        }
    }

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
                // Encabezado
                Column() {
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
                        text = if (isGoogleOnboarding) "Completa tu perfil" else "Crea tu cuenta",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isGoogleOnboarding) "Elige tu tipo de perfil para continuar" else "Únete a nuestra red logística",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Selección de rol
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
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Cliente",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cliente", color = if (isClient) Color.Black else Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { isClient = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isClient) Color.Yellow else Color.LightGray
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Empresario",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Empresario", color = if (!isClient) Color.Black else Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Validacion correo electrónico:
                val isEmailValid = username.isBlank() || Patterns.EMAIL_ADDRESS.matcher(username).matches()
                val showEmailError = username.isNotBlank() && !isEmailValid

                // Validacion contraseña:
                val hasUppercase = password.any { it.isUpperCase() }
                val hasNumber = password.any { it.isDigit() }
                val hasSpecialChar = password.any { !it.isLetterOrDigit() }
                val hasMinLength = password.length >= 8
                val isPasswordValid = password.isBlank() || (hasUppercase && hasNumber && hasSpecialChar && hasMinLength)
                val showPasswordError = password.isNotBlank() && !isPasswordValid

                // Validacion DNI y RUC:
                val isDniValid = dni.length == 8 && dni.all { it.isDigit() }
                val showDniError = dni.isNotBlank() && !isDniValid

                val isRucValid = ruc.length == 11 && ruc.all { it.isDigit() }
                val showRucError = ruc.isNotBlank() && !isRucValid

                val isPhoneValid = phone.length == 9 && phone.all { it.isDigit() }
                val showPhoneError = phone.isNotBlank() && !isPhoneValid

                val idFieldValid = if (isClient) (dni.isNotBlank() && isDniValid) else (ruc.isNotBlank() && isRucValid)

                // Validación general del formulario:
                val credentialsValid = if (isGoogleOnboarding) {
                    true
                } else {
                    username.isNotBlank() && password.isNotBlank() && isEmailValid && isPasswordValid
                }

                val isFormValid = credentialsValid &&
                        name.isNotBlank() &&
                        isPhoneValid &&
                        idFieldValid

                if (!isGoogleOnboarding) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo electrónico") },
                        isError = showEmailError,
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    if (showEmailError) {
                        Text(
                            text = "El correo electrónico no es válido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null) } },
                        supportingText = { if (showPasswordError) Text(text = "La contraseña debe tener mínimo 8 caracteres, una letra mayuscula, un caracter especial y un número", color = Color.Red) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isClient) "Nombre completo" else "Nombre de la empresa") },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Nombre") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                var rawPhone by remember { mutableStateOf("") }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Peru",
                        modifier = Modifier
                            .padding(end = 8.dp),
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = rawPhone,
                        onValueChange = { input ->
                            rawPhone = input.filter { it.isDigit() }.take(9)
                            phone = rawPhone
                        },
                        label = { Text("Número de celular") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Número de celular") },
                        visualTransformation = PhoneVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showPhoneError,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

// Campos condicionales: DNI para Cliente
                if (isClient) {
                    OutlinedTextField(
                        value = dni,
                        onValueChange = {
                            // aceptar sólo dígitos y hasta 8 caracteres
                            dni = it.filter { ch -> ch.isDigit() }.take(8)
                        },
                        label = { Text("DNI") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Info, contentDescription = "DNI") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showDniError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    if (showDniError) {
                        Text(
                            text = "El DNI no es válido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    // Campos para Empresario: RUC
                    OutlinedTextField(
                        value = ruc,
                        onValueChange = {
                            // aceptar sólo dígitos y hasta 11 caracteres
                            ruc = it.filter { ch -> ch.isDigit() }.take(11)
                        },
                        label = { Text("RUC") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Info, contentDescription = "RUC") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        isError = showRucError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    if (showRucError) {
                        Text(
                            text = "El RUC no es válido",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botón Crear Cuenta
                Button(
                    onClick = {
                        if (isClient) {
                            viewModel.signUpClient(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                dni = dni
                            )
                        } else {
                            viewModel.signUpEntrepreneur(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                ruc = ruc,
                                logoImage = ""
                            )
                        }
                    },
                    enabled = isFormValid && !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        if (isGoogleOnboarding) "Completar Perfil" else "Crear Cuenta",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isGoogleOnboarding) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¿Tienes una cuenta? Accede desde aquí",
                        color = Color(0xFF2196F3),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Routes.Login.routes) },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
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

private class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(9)
        val transformed = buildString {
            trimmed.forEachIndexed { index, c ->
                append(c)
                if ((index == 2 || index == 5) && index != trimmed.lastIndex) {
                    append('-')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 6 -> offset + 1
                    else -> offset + 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 7 -> offset - 1
                    else -> offset - 2
                }.coerceAtLeast(0)
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}
