package com.cargoexpress.app.core.presentation.register

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel,
    onRegisterSuccess: (username: String, password: String) -> Unit
) {
    val state by viewModel.state.observeAsState(UIState())

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rawPhone by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var birthDateDisplay by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isClient by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    val isEmailValid = username.isBlank() || Patterns.EMAIL_ADDRESS.matcher(username).matches()
    val showEmailError = username.isNotBlank() && !isEmailValid
    val hasUppercase = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val hasMinLength = password.length >= 8
    val isPasswordValid = password.isBlank() || (hasUppercase && hasNumber && hasSpecialChar && hasMinLength)
    val showPasswordError = password.isNotBlank() && !isPasswordValid
    val isNameValid = name.isBlank() || name.length >= 8
    val showNameError = name.isNotBlank() && !isNameValid
    val isPhoneValid = phone.length == 9 && phone.all { it.isDigit() }
    val showPhoneError = phone.isNotBlank() && !isPhoneValid
    val isDniValid = dni.length == 8 && dni.all { it.isDigit() }
    val showDniError = dni.isNotBlank() && !isDniValid
    val isRucValid = ruc.length == 11 && ruc.all { it.isDigit() }
    val showRucError = ruc.isNotBlank() && !isRucValid
    val idFieldValid = if (isClient) (dni.isNotBlank() && isDniValid) else (ruc.isNotBlank() && isRucValid)
    val extraFieldValid = if (isClient) birthDate.isNotBlank() else (address.isNotBlank() && address.length <= 200)
    val isFormValid = username.isNotBlank() &&
            isEmailValid &&
            password.isNotBlank() &&
            isPasswordValid &&
            name.isNotBlank() &&
            isNameValid &&
            isPhoneValid &&
            idFieldValid &&
            extraFieldValid

    LaunchedEffect(state.message) {
        if (state.message.isNotEmpty()) {
            confirmModalSuccess = state.data != null
            confirmModalMessage = state.message
            showConfirmModal = true
        }
    }

    if (showDatePicker) {
        val maxDateMillis = remember {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -18)
            cal.timeInMillis
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = maxDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= maxDateMillis
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH) + 1
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    birthDate = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T00:00:00"
                    birthDateDisplay = "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year"
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (isClient) {
                                viewModel.registerClient(username, password, phone, name, dni, birthDate)
                            } else {
                                viewModel.registerEntrepreneur(username, password, phone, name, ruc, address)
                            }
                        },
                        enabled = isFormValid && !state.isLoading,
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Términos y Condiciones",
                        color = Color(0xFFE4D911),
                        style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Routes.TermsAndConditions.routes) },
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            pushStyle(SpanStyle(color = Color.White))
                            append("¿Tienes una cuenta? ")
                            pushStyle(SpanStyle(color = Color(0xFFE4D911), fontWeight = FontWeight.Bold))
                            append("Accede desde aquí")
                            pop()
                        },
                        color = Color(0xFF2196F3),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Routes.Login.routes) },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
            ) {
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

                // Email + Password group
                Column {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { if (it.length <= 60) username = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        isError = showEmailError,
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 4.dp, bottom = 8.dp),
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
                            text = "${username.length}/60",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { if (it.length <= 60) password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        supportingText = {
                            if (showPasswordError) Text(
                                "Mínimo 8 caracteres, una mayúscula, un número y un carácter especial",
                                color = Color.Red
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }

                //Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Role selection
                Text(
                    text = "¿Que función desempeñarás en CargoExpress?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
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
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Cliente",
                                color = if (isClient) Color.Black else Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
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
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Empresario",
                                color = if (!isClient) Color.Black else Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Name + [Phone + ID/Tax ID] + [Date/Address] group
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 60) name = it },
                        label = { Text(if (isClient) "Nombre completo" else "Nombre de la empresa") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        isError = showNameError,
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (showNameError) {
                                    Text(
                                        "Mínimo 8 caracteres",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                Text(
                                    text = "${name.length}/60",
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    )

                    // Phone + DNI/RUC horizontal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = rawPhone,
                                onValueChange = { input ->
                                    rawPhone = input.filter { it.isDigit() }.take(9)
                                    phone = rawPhone
                                },
                                label = { Text("Celular") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                maxLines = 1,
                                isError = showPhoneError,
                                visualTransformation = PhoneVisualTransformation(),
                                //textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "Peru",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            if (isClient) {
                                OutlinedTextField(
                                    value = dni,
                                    onValueChange = { dni = it.filter(Char::isDigit).take(8) },
                                    label = { Text("DNI") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    maxLines = 1,
                                    isError = showDniError,
                                    //textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (showDniError) {
                                    Text(
                                        "8 dígitos requeridos",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = ruc,
                                    onValueChange = { ruc = it.filter(Char::isDigit).take(11) },
                                    label = { Text("RUC") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    maxLines = 1,
                                    isError = showRucError,
                                    //textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (showRucError) {
                                    Text(
                                        "11 dígitos requeridos",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Date of Birth / Address
                    if (isClient) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = birthDateDisplay,
                                onValueChange = {},
                                label = { Text("Fecha de nacimiento") },
                                leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                                placeholder = { Text("Debes tener al menos 18 años") },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { if (it.length <= 200) address = it },
                            label = { Text("Dirección") },
                            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            maxLines = 1,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            supportingText = {
                                Text(
                                    text = "${address.length}/200",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                viewModel.clearState()
                if (confirmModalSuccess) {
                    onRegisterSuccess(username, password)
                }
            },
            onDismiss = {
                showConfirmModal = false
                viewModel.clearState()
            }
        )
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
