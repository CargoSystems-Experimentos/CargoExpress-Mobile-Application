
package com.cargoexpress.app.core.presentation.fleet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cargoexpress.app.core.common.Routes

@Composable
fun FleetScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FleetItem(
            title = "Visualizar Conductores",
            description = "Ver la lista de conductores registrados en su empresa",
            visualizarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onVisualizarClick = { navController.navigate(Routes.DriverList.routes) }
        )
        FleetItem(
            title = "Visualizar Vehículos",
            description = "Ver la lista de vehículos registrados en su empresa",
            visualizarColor = Color(0xFFF1F504),
            modificarColor = Color(0xFFF1F504),
            onVisualizarClick = { navController.navigate(Routes.VehicleList.routes) }
        )
    }
}

@Composable
fun FleetItem(
    title: String,
    description: String,
    visualizarColor: Color,
    modificarColor: Color,
    onVisualizarClick: () -> Unit
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
                onClick = onVisualizarClick,
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = visualizarColor
                )
            ) {
                Text(text = "Visualizar", color = Color.Black)
            }
        }
    }
}

