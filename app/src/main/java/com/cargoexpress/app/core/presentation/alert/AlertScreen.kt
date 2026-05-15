package com.cargoexpress.app.core.presentation.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Alert
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    tripId: Int,
    tripRepository: TripRepository,
    navController: NavController,
    alertRepository: AlertRepository,
) {
    val viewModel: AlertViewModel = viewModel(
        factory = AlertViewModelFactory(alertRepository, tripRepository)
    )
    val alerts by viewModel.alerts.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val tripName by viewModel.tripName.collectAsState()
    val isEntrepreneur = Constants.USER_ROLE == "ENTREPRENEUR"

    var showDialog by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertDescription by remember { mutableStateOf("") }
    var ongoingTripId by remember { mutableStateOf<Int?>(null) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    LaunchedEffect(tripId) {
        viewModel.loadTripName(tripId)
        when (val result = tripRepository.getOngoingTripByTripId(tripId)) {
            is Resource.Success -> { ongoingTripId = result.data?.id }
            is Resource.Error -> { ongoingTripId = null }
        }
    }

    val tripAlerts = alerts.filter { alert ->
        ongoingTripId != null && alert.ongoingTripId == ongoingTripId
    }

    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            showDialog = false
            alertTitle = ""
            alertDescription = ""
            viewModel.resetCreateSuccess()
            confirmModalSuccess = true
            confirmModalMessage = "Alerta registrada correctamente"
            showConfirmModal = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas – ${tripName.ifBlank { "Viaje #$tripId" }}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("gps/$tripId") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver al GPS")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (isEntrepreneur) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nueva Alerta") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (tripAlerts.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "Sin alertas registradas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isEntrepreneur) {
                        Text(
                            text = "Usa el botón + para crear una alerta",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tripAlerts) { alert ->
                        AlertItemCard(alert = alert)
                    }
                }
            }
        }
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = { showConfirmModal = false },
            onDismiss = { showConfirmModal = false }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isLoading) showDialog = false },
            title = { Text("Nueva Alerta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = alertTitle,
                        onValueChange = { if (it.length <= 50) alertTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.message.isNotEmpty(),
                        supportingText = {
                            Text(
                                text = "${alertTitle.length}/50",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    OutlinedTextField(
                        value = alertDescription,
                        onValueChange = { if (it.length <= 200) alertDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = {
                            Text(
                                text = "${alertDescription.length}/200",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            },
            confirmButton = {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                } else {
                    Button(
                        onClick = {
                            if (alertTitle.isNotBlank() && alertDescription.isNotBlank()) {
                                viewModel.createAlert(tripId, alertTitle, alertDescription)
                            }
                        },
                        enabled = alertTitle.isNotBlank() && alertDescription.isNotBlank()
                    ) {
                        Text("Crear Alerta")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = !uiState.isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(uiState.message) {
        if (uiState.message.isNotEmpty() && !uiState.isLoading) {
            confirmModalSuccess = false
            confirmModalMessage = uiState.message
            showConfirmModal = true
            showDialog = false
        }
    }
}

@Composable
private fun AlertItemCard(alert: Alert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (alert.date.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatAlertDateTime(alert.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

private fun formatAlertDateTime(raw: String): String {
    val inputFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss"
    )
    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    for (pattern in inputFormats) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                isLenient = false
            }
            val date = parser.parse(raw)
            if (date != null) return outputFormat.format(date)
        } catch (_: Exception) {
        }
    }
    return raw
}