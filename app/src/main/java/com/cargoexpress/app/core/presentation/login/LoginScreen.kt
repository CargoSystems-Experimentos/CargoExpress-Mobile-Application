package com.cargoexpress.app.core.presentation.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cargoexpress.app.BuildConfig
import com.cargoexpress.app.R
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.UIState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    val tag = "LoginScreen"
    val state by viewModel.state.observeAsState(UIState())
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var emailState by rememberSaveable { mutableStateOf("") }
    var passwordState by rememberSaveable { mutableStateOf("") }

    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()
        GoogleSignIn.getClient(context, options)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(tag, "Google launcher resultCode=${result.resultCode}")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (!idToken.isNullOrBlank()) {
                Log.d(tag, "Google account received email=${account.email}")
                viewModel.signInWithGoogle(idToken)
            } else {
                viewModel.onGoogleSignInClientError("Google no devolvio idToken")
            }
        } catch (e: ApiException) {
            Log.e(tag, "Google client ApiException code=${e.statusCode}", e)
            if (result.resultCode == Activity.RESULT_CANCELED) {
                viewModel.onGoogleSignInClientError("Google Sign-In cancelado o bloqueado (code=${e.statusCode})")
            } else {
                viewModel.onGoogleSignInClientError("Error de Google Sign-In: ${e.statusCode}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Google client unexpected exception", e)
            viewModel.onGoogleSignInClientError("Error inesperado en Google Sign-In")
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // Reemplaza con tu recurso de logo
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
                    onValueChange = { emailState = it },
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

                OutlinedButton(
                    onClick = {
                        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
                            viewModel.onGoogleSignInClientError("GOOGLE_WEB_CLIENT_ID no configurado")
                            return@OutlinedButton
                        }
                        Log.d(tag, "Launching Google Sign-In")
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Continuar con Google", fontWeight = FontWeight.SemiBold)
                }

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    modifier = Modifier.clickable {
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = Color(0xFF2196F3)
                )

                Spacer(modifier = Modifier.height(24.dp))

                ClickableText(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(color = Color.White))
                        append("¿No tienes una cuenta? ")
                        pushStyle(SpanStyle(color = Color(0xFFE4D911), fontWeight = FontWeight.Bold))
                        append("Crear una cuenta")
                        pop()
                    },
                    onClick = {
                        navController.navigate(Routes.Register.routes)
                    },
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
        onValueChange = { onPasswordChange(it) },
        label = { Text("Contraseña") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Contraseña"
            )
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (showPassword)
                Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility

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
