package com.cargoexpress.app.core.data.remote.expense

import com.cargoexpress.app.core.domain.Expense

data class ExpenseDto(
    val fuelAmount: Int,
    val fuelDescription: String,
    val viaticsAmount: Int,
    val viaticsDescription: String,
    val tollsAmount: Int,
    val tollsDescription: String,
    val tripId: Int
)

fun ExpenseDto.toExpense() = Expense(
    id = 0,
    fuelAmount = fuelAmount,
    fuelDescription = fuelDescription,
    viaticsAmount = viaticsAmount,
    viaticsDescription = viaticsDescription,
    tollsAmount = tollsAmount,
    tollsDescription = tollsDescription,
    tripId = tripId
)

fun Expense.toExpenseDto() = ExpenseDto(
    fuelAmount = fuelAmount,
    fuelDescription = fuelDescription,
    viaticsAmount = viaticsAmount,
    viaticsDescription = viaticsDescription,
    tollsAmount = tollsAmount,
    tollsDescription = tollsDescription,
    tripId = tripId
)