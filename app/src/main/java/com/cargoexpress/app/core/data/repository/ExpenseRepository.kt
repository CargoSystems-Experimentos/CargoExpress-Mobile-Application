package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpensePostDto
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseService: ExpenseService) {

    suspend fun addExpense(expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error("Token is required")
        return@withContext try {
            val response = expenseService.addExpense("Bearer ${Constants.TOKEN}", expense.toExpensePostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error("Failed to add expense")
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
                Resource.Error("Failed to update expense")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
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
}
