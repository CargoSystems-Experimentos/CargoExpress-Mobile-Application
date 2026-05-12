package com.cargoexpress.app.core.presentation.phoneauth

sealed class OtpPhase {
    object Idle : OtpPhase()
    data class Sending(val phone: String) : OtpPhase()
    data class Resending(val phone: String) : OtpPhase()
    data class AwaitingCode(val phone: String, val error: String = "") : OtpPhase()
    object Verifying : OtpPhase()
}
