package com.cargoexpress.app.core.presentation.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RecordScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val token = ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecordItem(
            title = "Registrar Viajes",
            description = "Registrar o modificar un viaje de transporte para su empresa",
            registrarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onRegistrarClick = {navController.navigate("register_trip?token=$token")}
        )
        RecordItem(
            title = "Registrar Gasto",
            description = "Registrar o modificar un gasto de un viaje de transporte para su empresa",
            registrarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onRegistrarClick = { navController.navigate("register_expense?token=$token") }
        )
        RecordItem(
            title = "Registrar Conductor",
            description = "Registrar o modificar un conductor para su empresa",
            registrarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onRegistrarClick = { navController.navigate("register_driver?token=$token") }
        )
        RecordItem(
            title = "Registrar Vehículo",
            description = "Registrar o modificar un vehículo de transporte para su empresa",
            registrarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onRegistrarClick = { navController.navigate("register_vehicle?token=$token")}
        )
    }
}

@Composable
fun RecordItem(
    title: String,
    description: String,
    registrarColor: Color,
    modificarColor: Color,
    onRegistrarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onRegistrarClick,
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = registrarColor
                )
            ) {
                Text(text = "Registrar", color = Color.Black)
            }

            Button(
                onClick = {  },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = modificarColor
                )
            ) {
                Text(text = "Modificar", color = Color.Black)
            }
        }
    }
}