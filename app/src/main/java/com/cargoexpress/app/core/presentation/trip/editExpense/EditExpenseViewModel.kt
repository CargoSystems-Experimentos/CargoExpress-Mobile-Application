package com.cargoexpress.app.core.presentation.trip.editExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditExpenseUiState())
    val uiState: StateFlow<EditExpenseUiState> = _uiState

    var fuelAmount: Double = 0.0
    var fuelDescription: String = ""
    var viaticsAmount: Double = 0.0
    var viaticsDescription: String = ""
    var tollsAmount: Double = 0.0
    var tollsDescription: String = ""
    private var tripId: Int = 0

    fun loadExpense(expenseId: Int) {
        viewModelScope.launch {
            _uiState.value = EditExpenseUiState(isLoading = true)
            when (val result = expenseRepository.getExpense(expenseId)) {
                is Resource.Success -> {
                    val expense = result.data
                    if (expense != null) {
                        fuelAmount = expense.fuelAmount
                        fuelDescription = expense.fuelDescription
                        viaticsAmount = expense.viaticsAmount
                        viaticsDescription = expense.viaticsDescription
                        tollsAmount = expense.tollsAmount
                        tollsDescription = expense.tollsDescription
                        tripId = expense.tripId
                        _uiState.value = EditExpenseUiState(expense = expense)
                    } else {
                        _uiState.value = EditExpenseUiState(message = "No se encontró el gasto")
                    }
                }
                is Resource.Error -> {
                    _uiState.value = EditExpenseUiState(message = result.message ?: "Error al cargar el gasto")
                }
            }
        }
    }

    fun updateExpense(expenseId: Int, onResult: (Resource<Expense>) -> Unit) {
        viewModelScope.launch {
            val expense = Expense(
                id = expenseId,
                fuelAmount = fuelAmount,
                fuelDescription = fuelDescription,
                viaticsAmount = viaticsAmount,
                viaticsDescription = viaticsDescription,
                tollsAmount = tollsAmount,
                tollsDescription = tollsDescription,
                state = true,
                tripId = tripId
            )
            val result = expenseRepository.updateExpense(expenseId, expense)
            if (result is Resource.Success) {
                expenseRepository.updateExpenseState(expenseId, false)
            }
            onResult(result)
        }
    }
}

data class EditExpenseUiState(
    val expense: Expense? = null,
    val isLoading: Boolean = false,
    val message: String = ""
)
