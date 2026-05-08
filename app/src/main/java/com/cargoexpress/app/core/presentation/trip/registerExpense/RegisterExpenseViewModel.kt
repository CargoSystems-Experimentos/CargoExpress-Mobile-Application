package com.cargoexpress.app.core.presentation.trip.registerExpense


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource

class RegisterExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    suspend fun registerExpense(expense: Expense): Resource<Expense> {
        return expenseRepository.addExpense(Constants.TOKEN, expense)
    }
}