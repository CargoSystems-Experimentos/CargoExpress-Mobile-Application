    package com.cargoexpress.app.core.presentation.register

    import android.net.Uri
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Visibility
    import androidx.compose.material.icons.filled.VisibilityOff
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.SnackbarHost
    import androidx.compose.material3.SnackbarHostState
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.runtime.*
    import androidx.compose.runtime.livedata.observeAsState
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.text.input.PasswordVisualTransformation
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import pe.edu.upc.appturismo.common.UIState
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.input.VisualTransformation
    import coil.compose.AsyncImage
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Bienvenid@ a CargoExpress! Estamos felices de que decidas unirte a nosotros",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Por favor, selecciona tu rol:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Botón para seleccionar Cliente
                    Button(
                        onClick = { isClient = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isClient) Color.Yellow else Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Cliente", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para seleccionar Empresario
                    Button(
                        onClick = { isClient = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isClient) Color.Yellow else Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Empresario", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    TextField(
                        value = ruc,
                        onValueChange = { ruc = it },
                        label = { Text("RUC") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    if (!isClient) {
                        Text(
                            text = "Selecciona el logo de tu empresa:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Selector de imagen para Empresario
                        ImagePicker(onImageSelected = { uri ->
                            logoUri = uri // Guardar URI seleccionada
                        })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.signUp(
                                username = username,
                                password = password,
                                name = name,
                                phone = phone,
                                ruc = ruc,
                                address = address,
                                isEntrepreneur = !isClient,
                                logoImage = logoUri?.toString() // Convertir URI a String para enviarla al servidor
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Crear Cuenta", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¿Tienes una cuenta? Accede desde aquí",
                        color = Color.Gray,
                        modifier = Modifier.clickable { navController.navigate(Routes.Login.routes) }
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
