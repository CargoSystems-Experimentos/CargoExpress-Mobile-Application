package com.cargoexpress.app.core.presentation.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cargoexpress.app.R
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurDto
import com.cargoexpress.app.core.presentation.ImagePicker

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    val entrepreneurState by viewModel.entrepreneurState
    val clientState by viewModel.clientState
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Constants.USER_ROLE, Constants.USER_ID, Constants.ENTREPRENEUR_ID) {
        when (Constants.USER_ROLE) {
            "CLIENT" -> viewModel.getClientProfile(Constants.USER_ID)
            "ENTREPRENEUR" -> viewModel.getEntrepreneurProfile(Constants.ENTREPRENEUR_ID)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (Constants.USER_ROLE) {
                "CLIENT" -> {
                    if (clientState.isLoading) {
                        CircularProgressIndicator()
                    } else if (clientState.data != null) {
                        val client = clientState.data!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ProfileHeaderClient(client = client, selectedImageUri = selectedImageUri)

                                Spacer(modifier = Modifier.height(12.dp))

                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            "Cliente",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Work,
                                            contentDescription = null,
                                            tint = Color(0xFF999900)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                ProfileInfoItem(
                                    label = "DNI",
                                    value = client.dni,
                                    icon = Icons.Filled.Badge
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ProfileInfoItem(
                                    label = "Teléfono",
                                    value = client.phone,
                                    icon = Icons.Filled.Call
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { viewModel.logOut() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD700),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Logout,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Cerrar Sesión",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                ImagePicker { uri ->
                                    selectedImageUri = uri
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Error al cargar los datos del cliente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                "ENTREPRENEUR" -> {
                    if (entrepreneurState.isLoading) {
                        CircularProgressIndicator()
                    } else if (entrepreneurState.data != null) {
                        val entrepreneur = entrepreneurState.data!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ProfileHeaderEntrepreneur(entrepreneur = entrepreneur, selectedImageUri = selectedImageUri)

                                Spacer(modifier = Modifier.height(12.dp))

                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            "Empresario",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Work,
                                            contentDescription = null,
                                            tint = Color(0xFF999900)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                ProfileInfoItem(
                                    label = "RUC",
                                    value = entrepreneur.ruc,
                                    icon = Icons.Filled.Badge
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ProfileInfoItem(
                                    label = "Teléfono",
                                    value = entrepreneur.phone,
                                    icon = Icons.Filled.Call
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { viewModel.logOut() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD700),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Logout,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Cerrar Sesión",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                ImagePicker { uri ->
                                    selectedImageUri = uri
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Error al cargar los datos del empresario.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                else -> {
                    Text(
                        text = "No se encontró un rol de sesión válido.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderClient(client: ClientDto, selectedImageUri: Uri?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Foto del cliente",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = client.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = client.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProfileHeaderEntrepreneur(entrepreneur: EntrepreneurDto, selectedImageUri: Uri?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Logo del emprendedor",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = entrepreneur.logoImage,
                    contentDescription = "Logo del emprendedor",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_placeholder),
                    error = painterResource(R.drawable.ic_error)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = entrepreneur.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF999900)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}