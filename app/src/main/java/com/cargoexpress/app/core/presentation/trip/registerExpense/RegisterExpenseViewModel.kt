package com.cargoexpress.app.core.presentation.trip.registerExpense


import androidx.lifecycle.ViewModel
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class RegisterExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    suspend fun registerExpense(expense: Expense): Resource<Expense> {
        return expenseRepository.addExpense(Constants.TOKEN, expense)
    }
}