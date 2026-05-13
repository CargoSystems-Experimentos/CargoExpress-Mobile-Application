package com.cargoexpress.app.core.presentation.phoneauth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object PhoneAuthHelper {

    private val auth = FirebaseAuth.getInstance()
    var storedVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun enviarCodigo(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onAutoVerified: () -> Unit = {},
        onError: (String) -> Unit
    ) {
        val callbacks = buildCallbacks(onAutoVerified, onCodeSent, onError)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun reenviarCodigo(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onAutoVerified: () -> Unit = {},
        onError: (String) -> Unit
    ) {
        val token = resendToken
        if (token == null) {
            enviarCodigo(phoneNumber, activity, onCodeSent, onAutoVerified, onError)
            return
        }
        val callbacks = buildCallbacks(onAutoVerified, onCodeSent, onError)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verificarCodigo(
        codigo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val verificationId = storedVerificationId ?: run {
            onError("No hay verificación activa")
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, codigo)
        iniciarSesionConCredencial(credential, onSuccess, onError)
    }

    fun reset() {
        storedVerificationId = null
        resendToken = null
    }

    private fun buildCallbacks(
        onAutoVerified: () -> Unit,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            iniciarSesionConCredencial(credential, onSuccess = onAutoVerified, onError = onError)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            onError(e.message ?: "Error al enviar el código")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token
            onCodeSent()
        }
    }

    private fun iniciarSesionConCredencial(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Código incorrecto") }
    }
}
