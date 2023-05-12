package com.rkh24.starexchange

sealed class CalculatorAction {
    data class Number(val number: Int): CalculatorAction()
    object Clear: CalculatorAction()
    data class Operation(val operation: CalculatorOperation): CalculatorAction()
    object Calculate: CalculatorAction()
    object Decimal: CalculatorAction()
    object ConvertToUSD : CalculatorAction()
    object ConvertToLBP : CalculatorAction()

}
