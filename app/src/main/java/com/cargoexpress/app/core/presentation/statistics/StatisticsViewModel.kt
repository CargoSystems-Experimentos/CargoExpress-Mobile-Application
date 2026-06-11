package com.cargoexpress.app.core.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<List<Trip>>(isLoading = true))
    val uiState: StateFlow<UIState<List<Trip>>> = _uiState

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val tripsResult = tripRepository.getTripsByClientId(Constants.TOKEN, Constants.CLIENT_ID)
            if (tripsResult is Resource.Success) {
                val tripList = tripsResult.data ?: emptyList()
                _trips.value = tripList
                _uiState.value = UIState(isLoading = false, data = tripsResult.data)

                val fetchedExpenses = mutableListOf<Expense>()
                tripList.forEach { trip ->
                    val expenseResult = tripRepository.getExpenseByTripId(trip.id)
                    if (expenseResult is Resource.Success && expenseResult.data != null) {
                        fetchedExpenses.add(expenseResult.data)
                    }
                }
                _expenses.value = fetchedExpenses
            } else {
                _uiState.value = UIState(isLoading = false, message = tripsResult.message ?: "")
            }
        }
    }
}
