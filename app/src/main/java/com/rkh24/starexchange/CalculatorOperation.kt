package com.rkh24.starexchange

sealed class CalculatorOperation(val symbol: String) {
    object Buy: CalculatorOperation("Buy")
    object Sell: CalculatorOperation("Sell")
    object Multiply: CalculatorOperation("x")
    object Divide: CalculatorOperation("/")
    object Add: CalculatorOperation("+")
    object Subtract: CalculatorOperation("-")


}