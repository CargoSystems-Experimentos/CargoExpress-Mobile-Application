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
    import androidx.compose.ui.text.input.VisualTransformation
    import androidx.compose.ui.text.style.TextDecoration
    import androidx.compose.ui.unit.sp
    import com.cargoexpress.app.core.common.Routes
    import com.cargoexpress.app.core.presentation.ImagePicker

    @Composable
    fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
        val state by viewModel.state.observeAsState(UIState())
        val snackbarHostState = remember { SnackbarHostState() }

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var ruc by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var logoUri by remember { mutableStateOf<Uri?>(null) } // Cambiar logo a Uri
        var showPassword by remember { mutableStateOf(false) }

        var isClient by remember { mutableStateOf(true) } // Estado para controlar si es cliente o empresario

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
                    Column(){
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

                    val isEmailValid = username.isBlank() || Patterns.EMAIL_ADDRESS.matcher(username).matches()
                    val hasUppercase = password.any { it.isUpperCase() }
                    val hasNumber = password.any { it.isDigit() }
                    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
                    val hasMinLength = password.length >= 8
                    val isPasswordValid = password.isBlank() || (hasUppercase && hasNumber && hasSpecialChar && hasMinLength)
                    val showPasswordError = password.isNotBlank() && !isPasswordValid
                    val showEmailError = username.isNotBlank() && !isEmailValid
                    val isFormValid = username.isNotBlank() &&
                            password.isNotBlank() &&
                            name.isNotBlank() &&
                            phone.isNotBlank() &&
                            isEmailValid &&
                            isPasswordValid
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Correo electrónico"
                            )
                        },
                        isError = showEmailError,
                        shape = RoundedCornerShape(16.dp),
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
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Contraseña"
                            )
                        },
                        isError = showPasswordError,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        supportingText = {
                            if (showPasswordError) {
                                Text(
                                    text = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isClient) "Nombre completo" else "Nombre de la empresa") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Nombre completo"
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
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
                            value = formatPhone(rawPhone),
                            onValueChange = { input ->
                                rawPhone = input.filter { it.isDigit() }.take(9)
                                phone = rawPhone
                            },
                            label = { Text("Número de celular") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = "Número de celular"
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Boton Crear Cuenta
                    Button(
                        onClick = {
                            viewModel.signUp(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                ruc = "12345678911",
                                address = "Address",
                                isEntrepreneur = !isClient,
                                logoImage = ""
                            )
                        },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            "Crear Cuenta",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

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

    private fun formatPhone(phone: String): String {
        return phone.chunked(3).joinToString("-")
    }