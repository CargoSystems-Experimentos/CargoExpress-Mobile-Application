package com.cargoexpress.app.core.data.remote.expense

import com.cargoexpress.app.core.domain.Expense

data class ExpenseDto(
    val id: Int,
    val fuelAmount: Double,
    val fuelDescription: String,
    val viaticsAmount: Double,
    val viaticsDescription: String,
    val tollsAmount: Double,
    val tollsDescription: String,
    val state: Boolean,
    val tripId: Int
)

data class ExpensePostDto(
    val fuelAmount: Double,
    val fuelDescription: String,
    val viaticsAmount: Double,
    val viaticsDescription: String,
    val tollsAmount: Double,
    val tollsDescription: String,
    val tripId: Int
)

data class ExpenseStateUpdateDto(val state: Boolean)

fun ExpenseDto.toExpense() = Expense(
    id = id,
    fuelAmount = fuelAmount,
    fuelDescription = fuelDescription,
    viaticsAmount = viaticsAmount,
    viaticsDescription = viaticsDescription,
    tollsAmount = tollsAmount,
    tollsDescription = tollsDescription,
    state = state,
    tripId = tripId
)

fun Expense.toExpensePostDto() = ExpensePostDto(
    fuelAmount = fuelAmount,
    fuelDescription = fuelDescription,
    viaticsAmount = viaticsAmount,
    viaticsDescription = viaticsDescription,
    tollsAmount = tollsAmount,
    tollsDescription = tollsDescription,
    tripId = tripId
)
