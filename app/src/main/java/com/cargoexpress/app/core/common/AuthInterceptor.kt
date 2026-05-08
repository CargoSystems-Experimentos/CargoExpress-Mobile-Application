package com.cargoexpress.app.core.common

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Añadir el header Authorization con el token
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")  // El token de admin
            .build()

        return chain.proceed(newRequest)
    }
}