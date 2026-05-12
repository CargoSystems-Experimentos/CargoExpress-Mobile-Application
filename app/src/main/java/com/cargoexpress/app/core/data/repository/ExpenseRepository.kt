package com.cargoexpress.app.core.data.repository

import android.util.Log
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpenseDto
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseService: ExpenseService) {


    suspend fun addExpense(expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if(Constants.TOKEN.isBlank()){
            return@withContext Resource.Error("Token is required")
        }
        return@withContext try {
            val response = expenseService.addExpense("Bearer ${Constants.TOKEN}", expense.toExpenseDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error("Failed to add expense")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
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
