package com.cargoexpress.app.core.data.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ApiErrorParserTest {

    @Test
    fun `parse returns message when backend sends message field`() {
        val response = errorResponse(400, "{\"message\":\"Credenciales invalidas\"}")
        val result = ApiErrorParser.parse(response)
        assertTrue(result.contains("Credenciales invalidas"))
    }

    @Test
    fun `parse returns concatenated validation errors`() {
        val response = errorResponse(
            400,
            """
            {
              "errors": {
                "Email": ["El correo no es valido"],
                "Password": ["La contrasena es muy debil"]
              }
            }
            """.trimIndent()
        )
        val result = ApiErrorParser.parse(response)
        assertTrue(result.contains("correo no es valido") || result.contains("correo"))
        assertTrue(result.contains("contrasena") || result.contains("Password"))
    }

    @Test
    fun `parse returns generic code when body is empty`() {
        val response = errorResponse(500, "")
        val result = ApiErrorParser.parse(response)
        assertTrue(result.contains("500"))
    }

    @Test
    fun `parse returns raw response when body is not json`() {
        val response = errorResponse(502, "bad gateway")
        val result = ApiErrorParser.parse(response)
        assertTrue(result.contains("502"))
        assertTrue(result.contains("bad gateway"))
    }

    @Test
    fun `parse handles multiple error keys`() {
        val response = errorResponse(
            400,
            """
            {
              "errors": {
                "Username": ["required"],
                "Email": ["required"],
                "Phone": ["required"]
              }
            }
            """.trimIndent()
        )
        val result = ApiErrorParser.parse(response)
        assertTrue(result.contains("required"))
    }

    private fun errorResponse(code: Int, body: String): Response<Unit> {
        return Response.error(
            code,
            body.toResponseBody("application/json".toMediaType())
        )
    }
}