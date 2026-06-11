package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.ExpenseStateUpdateDto
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpensePostDto
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseService: ExpenseService) {

    suspend fun getExpense(expenseId: Int): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.getExpense(expenseId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                val expense = response.body()?.toExpense()
                if (expense != null) Resource.Success(expense)
                else Resource.Error("No se encontró el gasto")
            } else {
                Resource.Error(parseBackendError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addExpense(expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.addExpense("Bearer ${Constants.TOKEN}", expense.toExpensePostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error(parseBackendError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateExpense(expenseId: Int, expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.updateExpense(expenseId, "Bearer ${Constants.TOKEN}", expense.toExpensePostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error(parseBackendError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateExpenseState(expenseId: Int, state: Boolean): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.updateExpenseState(expenseId, "Bearer ${Constants.TOKEN}", ExpenseStateUpdateDto(state))
            if (response.isSuccessful) {
                Resource.Success(state)
            } else {
                Resource.Error(parseBackendError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getExpenses(token: String): Resource<List<Expense>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.getExpenses("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()?.map { it.toExpense() } ?: emptyList())
            } else {
                Resource.Error("Failed to fetch expenses")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    private fun parseBackendError(body: String?): String {
        if (body.isNullOrBlank()) return "Error desconocido"
        return try {
            org.json.JSONObject(body).optString("message", body)
        } catch (_: Exception) { body }
    }
}
