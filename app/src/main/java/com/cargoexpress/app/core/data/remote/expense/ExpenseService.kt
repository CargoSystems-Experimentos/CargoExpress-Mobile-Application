package com.cargoexpress.app.core.data.remote.expense

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExpenseService {
    @GET("expenses")
    suspend fun getExpenses(
        @Header("Authorization") token: String
    ): Response<List<ExpenseDto>>

    @GET("expenses/{expenseId}")
    suspend fun getExpense(
        @Path("expenseId") expenseId: Int,
        @Header("Authorization") token: String
    ): Response<ExpenseDto>

    @POST("expenses")
    suspend fun addExpense(
        @Header("Authorization") token: String,
        @Body expense: ExpensePostDto
    ): Response<ExpenseDto>

    @PUT("expenses/{expenseId}")
    suspend fun updateExpense(
        @Path("expenseId") expenseId: Int,
        @Header("Authorization") token: String,
        @Body expense: ExpensePostDto
    ): Response<ExpenseDto>

    @PUT("expenses/{expenseId}/state")
    suspend fun updateExpenseState(
        @Path("expenseId") expenseId: Int,
        @Header("Authorization") token: String,
        @Body update: ExpenseStateUpdateDto
    ): Response<ExpenseStateUpdateDto>
}
