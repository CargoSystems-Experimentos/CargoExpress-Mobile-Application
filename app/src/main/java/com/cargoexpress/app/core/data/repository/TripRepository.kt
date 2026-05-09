package com.cargoexpress.app.core.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.remote.expense.toExpense
import com.cargoexpress.app.core.data.remote.expense.toExpenseDto
import com.cargoexpress.app.core.data.remote.trip.TripService
import com.cargoexpress.app.core.data.remote.trip.toTrip
import com.cargoexpress.app.core.data.remote.trip.toTripDto
import com.cargoexpress.app.core.data.remote.trip.toTripLegacy
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class TripRepository(private val tripService: TripService, private val expenseService: ExpenseService) {

    suspend fun getTrips(token: String, entrepreneurId: Int): Resource<List<Trip>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val response = tripService.getTrips(entrepreneurId, token)
            Log.d("TripRepository", "Response code: ${response.code()}")
            Log.d("TripRepository", "Response message: ${response.message()}")
            if (response.isSuccessful) {
                val trips = response.body()?.map { tripDto ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tripDto.toTrip()
                    } else {
                        tripDto.toTripLegacy()
                    }
                } ?: emptyList()
                Log.d("TripRepository", "Mapped trips: $trips")
                Resource.Success(data = trips)
            } else {
                Log.e("TripRepository", "Failed to fetch trips: ${response.errorBody()?.string()}")
                Resource.Error(message = "Failed to fetch trips")
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Exception occurred: ${e.message}", e)
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addTrip(trip: Trip): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val tripDtoPost = trip.toTripDto()
            val response = tripService.addTrip("Bearer ${Constants.TOKEN}", tripDtoPost)
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toTrip() ?: tripDtoPost.toTrip())
            } else {
                Resource.Error(message = "Failed to add trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateTrip(trip: Trip): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val tripDtoPost = trip.toTripDto()
            val response = tripService.updateTrip(trip.id, "Bearer ${Constants.TOKEN}", tripDtoPost)
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toTrip() ?: tripDtoPost.toTrip())
            } else {
                Resource.Error(message = "Failed to update trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getTripById(tripId: Int): Resource<Trip> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error<Trip>(message = "Token is required")
        }
        return@withContext try {
            val response = tripService.getTrip(tripId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                val trip = response.body()?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.toTrip()
                    } else {
                        it.toTripLegacy()
                    }
                }
                Resource.Success(data = trip)
            } else {
                Resource.Error<Trip>(message = "Failed to fetch trip details")
            }
        } catch (e: Exception) {
            Resource.Error<Trip>(message = e.message ?: "An unknown error occurred")
        } as Resource<Trip>
    }

    suspend fun getExpenseByTripId(tripId: Int): Resource<Expense> {
        //falta la logica,nd
        return Resource.Error<Expense>(message = "Not implemented")
    }

    suspend fun addExpense(expense: Expense): Resource<Expense> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error<Expense>(message = "Token is required")
        }
        return@withContext try {
            val expenseDto = expense.toExpenseDto()
            val response = expenseService.addExpense("Bearer ${Constants.TOKEN}", expenseDto)
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toExpense() ?: expense)
            } else {
                Resource.Error<Expense>(message = "Failed to add expense")
            }
        } catch (e: Exception) {
            Resource.Error<Expense>(message = e.message ?: "An unknown error occurred")
        }
    }
}