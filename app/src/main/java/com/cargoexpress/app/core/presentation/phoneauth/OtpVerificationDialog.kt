package com.cargoexpress.app.core.presentation.phoneauth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val Yellow = Color(0xFFE4D911)
private val YellowLight = Color(0xFFFFF8E1)
private val YellowBorder = Color(0xFFF9A825)

@Composable
fun OtpVerificationDialog(
    otpPhase: OtpPhase,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onCancel: () -> Unit
) {
    if (otpPhase == OtpPhase.Idle) return

    val phone = when (otpPhase) {
        is OtpPhase.Sending -> otpPhase.phone
        is OtpPhase.Resending -> otpPhase.phone
        is OtpPhase.AwaitingCode -> otpPhase.phone
        else -> ""
    }
    val error = (otpPhase as? OtpPhase.AwaitingCode)?.error ?: ""
    val isBusy = otpPhase is OtpPhase.Sending ||
            otpPhase is OtpPhase.Resending ||
            otpPhase is OtpPhase.Verifying

    var otpCode by remember(otpPhase is OtpPhase.AwaitingCode) { mutableStateOf("") }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(YellowLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = YellowBorder,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Verificación de teléfono",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (phone.isNotBlank())
                        "Enviamos un código SMS a\n${maskPhone(phone)}"
                    else
                        "Enviando código de verificación...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (isBusy) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Yellow,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = when (otpPhase) {
                                is OtpPhase.Sending -> "Enviando SMS..."
                                is OtpPhase.Resending -> "Reenviando SMS..."
                                is OtpPhase.Verifying -> "Verificando código..."
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    OtpInput(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { onVerify(otpCode) },
                    enabled = otpCode.length == 6 && !isBusy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow,
                        disabledContainerColor = Color(0xFFDDD700).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Verificar",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                TextButton(
                    onClick = onResend,
                    enabled = !isBusy
                ) {
                    Text(
                        text = "Reenviar código",
                        color = if (!isBusy) Color(0xFF2196F3) else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TextButton(
                    onClick = onCancel,
                    enabled = !isBusy
                ) {
                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            if (input.length <= 6 && input.all(Char::isDigit)) onValueChange(input)
        },
        modifier = modifier.focusRequester(focusRequester),
        textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = { innerTextField ->
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(6) { i ->
                        val char = value.getOrNull(i)?.toString() ?: ""
                        val isActive = value.length == i
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .border(
                                    width = if (isActive) 2.dp else 1.dp,
                                    color = when {
                                        isActive -> Yellow
                                        char.isNotEmpty() -> YellowBorder
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (char.isNotEmpty()) YellowLight else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Box(modifier = Modifier.matchParentSize().alpha(0.01f)) { innerTextField() }
            }
        }
    )
}

private fun maskPhone(phone: String): String {
    val digits = phone.filter(Char::isDigit).takeLast(9)
    return if (digits.length >= 3) "+51 *** *** ${digits.takeLast(3)}" else phone
}
