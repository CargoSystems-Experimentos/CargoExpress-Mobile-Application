package com.cargoexpress.app.core.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationModal(
    isSuccess: Boolean,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm
) {
    val accentColor = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
    val title = if (isSuccess) "¡Éxito!" else "Error"
    val buttonLabel = if (isSuccess) "Aceptar" else "Cerrar"

    Dialog(
        onDismissRequest = { if (!isSuccess) onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = !isSuccess)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 36.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = if (isSuccess) onConfirm else onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = buttonLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
