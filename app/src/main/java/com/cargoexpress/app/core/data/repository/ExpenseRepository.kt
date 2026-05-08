package com.cargoexpress.app.core.data.repository

import android.util.Log
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpenseDto
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource

class ExpenseRepository(private val expenseService: ExpenseService) {


    suspend fun addExpense(token: String, expense: Expense): Resource<Expense> {
        return try {
            val expenseDto = expense.toExpenseDto()
            val response = expenseService.addExpense(token, expenseDto)
            if (response.isSuccessful) {
                Log.d("ExpenseRepository", "POST successful: ${response.body()}")
                Resource.Success(response.body()!!.toExpense())
            } else {
                Log.d("ExpenseRepository", "POST failed: ${response.message()}")
                Resource.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.d("ExpenseRepository", "Exception: ${e.message}")
            Resource.Error("Exception: ${e.message}")
        }
    }


    suspend fun getExpenses(token: String): Resource<List<Expense>> {
        return try {
            val response = expenseService.getExpenses("Bearer $token")
            if (response.isSuccessful) {
                val expenses = response.body()?.map { it.toExpense() } ?: emptyList()
                Resource.Success(expenses)
            } else {
                Resource.Error("Failed to fetch expenses")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getExpensesByTripId(token: String, tripId: Int): Resource<List<Expense>> {
        return when (val result = getExpenses(token)) {
            is Resource.Success -> {
                val filteredExpenses = result.data?.filter { it.tripId == tripId } ?: emptyList()
                Resource.Success(filteredExpenses)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Failed to fetch expenses")
        }
    }

}
