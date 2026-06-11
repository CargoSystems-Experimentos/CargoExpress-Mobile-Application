package com.cargoexpress.app.core.data.repository

import android.util.Log
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpensePostDto
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTrip
import com.cargoexpress.app.core.data.remote.trip.TripService
import com.cargoexpress.app.core.data.remote.trip.toTrip
import com.cargoexpress.app.core.data.remote.trip.toTripDetailsUpdateDto
import com.cargoexpress.app.core.data.remote.trip.toTripPostDto
import com.cargoexpress.app.core.data.remote.trip.toTripScheduleUpdateDto
import com.cargoexpress.app.core.data.remote.trip.TripStateUpdateDto
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.OngoingTrip
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripRepository(private val tripService: TripService, private val expenseService: ExpenseService) {

    suspend fun getTrips(token: String, entrepreneurId: Int): Resource<List<Trip>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.getTripsByEntrepreneur(entrepreneurId, "Bearer $token")
            Log.d("TripRepository", "Response code: ${response.code()}")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toTrip() } ?: emptyList())
            } else {
                Log.e("TripRepository", "Failed to fetch trips: ${response.errorBody()?.string()}")
                Resource.Error(message = "Failed to fetch trips")
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Exception occurred: ${e.message}", e)
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getTripsByClientId(token: String, clientId: Int): Resource<List<Trip>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.getTripsByClient(clientId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toTrip() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch trips")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addTrip(trip: Trip): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.addTrip("Bearer ${Constants.TOKEN}", trip.toTripPostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toTrip() ?: trip)
            } else {
                Resource.Error(message = "Failed to add trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateTripDetails(trip: Trip): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.updateTripDetails(trip.id, "Bearer ${Constants.TOKEN}", trip.toTripDetailsUpdateDto())
            if (response.isSuccessful) {
                Resource.Success(data = trip)
            } else {
                Resource.Error(message = "Failed to update trip details")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateTripSchedule(trip: Trip): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.updateTripSchedule(trip.id, "Bearer ${Constants.TOKEN}", trip.toTripScheduleUpdateDto())
            if (response.isSuccessful) {
                Resource.Success(data = trip)
            } else {
                Resource.Error(message = "Failed to update trip schedule")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateTripState(tripId: Int, state: String): Resource<String> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.updateTripState(tripId, "Bearer ${Constants.TOKEN}", TripStateUpdateDto(state))
            if (response.isSuccessful) {
                Resource.Success(data = state)
            } else {
                Resource.Error(message = "Failed to update trip state")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getTripById(tripId: Int): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.getTrip(tripId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toTrip())
            } else {
                Resource.Error(message = "Failed to fetch trip details")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getOngoingTripByTripId(tripId: Int): Resource<OngoingTrip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.getOngoingTripByTripId(tripId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Resource.Success(data = dto.toOngoingTrip())
                else Resource.Error(message = "Respuesta vacía")
            } else {
                Resource.Error(message = "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getExpenseByTripId(tripId: Int): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = tripService.getExpenseByTripId(tripId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Resource.Success(data = dto.toExpense())
                else Resource.Error(message = "No expense found for trip")
            } else {
                Resource.Error(message = "Failed to fetch expense")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addExpense(expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = expenseService.addExpense("Bearer ${Constants.TOKEN}", expense.toExpensePostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error(message = "Failed to add expense")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
