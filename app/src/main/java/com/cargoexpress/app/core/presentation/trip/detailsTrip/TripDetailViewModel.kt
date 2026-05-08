package com.cargoexpress.app.core.presentation.trip.detailsTrip


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource

class TripDetailViewModel(
    private val repository: TripRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> get() = _trip

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses

    fun loadTripDetails(tripId: Int) {
        viewModelScope.launch {
            when (val result = repository.getTripById(tripId)) {
                is Resource.Success -> _trip.value = result.data
                is Resource.Error -> _trip.value = null
            }
        }
    }

    fun loadExpensesByTripId(tripId: Int) {
        viewModelScope.launch {
            when (val result = expenseRepository.getExpensesByTripId(Constants.TOKEN, tripId)) {
                is Resource.Success -> _expenses.value = result.data ?: emptyList()
                is Resource.Error -> _expenses.value = emptyList()
            }
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            when (val result = expenseRepository.addExpense(Constants.TOKEN, expense)) {
                is Resource.Success -> loadExpensesByTripId(expense.tripId)
                is Resource.Error -> { }
            }
        }
    }
}