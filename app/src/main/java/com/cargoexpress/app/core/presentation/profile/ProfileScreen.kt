package com.cargoexpress.app.core.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cargoexpress.app.R
import com.cargoexpress.app.core.data.remote.user.EntrepreneurDto
import com.cargoexpress.app.core.common.Constants
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cargoexpress.app.core.presentation.ImagePicker

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    val entrepreneurState by viewModel.entrepreneurState
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getEntrepreneurProfile(Constants.ENTREPRENEUR_ID)
        println("ENTREPRENEUR_ID: ${Constants.ENTREPRENEUR_ID}, TOKEN: ${Constants.TOKEN}")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (entrepreneurState.isLoading) {
                CircularProgressIndicator()
            } else if (entrepreneurState.data != null) {
                val entrepreneur = entrepreneurState.data

                // Logo y nombre
                if (entrepreneur != null) {
                    ProfileHeader(entrepreneur, selectedImageUri)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información clave del perfil
                if (entrepreneur != null) {
                    ProfileInfoItem(label = "RUC", value = entrepreneur.ruc)
                }
                if (entrepreneur != null) {
                    ProfileInfoItem(label = "Teléfono", value = entrepreneur.phone)
                }
                if (entrepreneur != null) {
                    ProfileInfoItem(label = "Dirección", value = entrepreneur.address)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de cerrar sesión
                Button(
                    onClick = { viewModel.logOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)) // Amarillo destacado
                ) {
                    Text(text = "Cerrar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Picker
                ImagePicker { uri ->
                    selectedImageUri = uri
                }
            } else {
                Text("Error al cargar los datos.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun ProfileHeader(entrepreneur: EntrepreneurDto, selectedImageUri: Uri?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del emprendedor en un círculo
        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Logo del emprendedor",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_error)
            )
        } else {
            AsyncImage(
                model = entrepreneur.logoImage,
                contentDescription = "Logo del emprendedor",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_error)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre del emprendedor
        Text(
            text = entrepreneur.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 20.sp
        )
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )
    }
}


