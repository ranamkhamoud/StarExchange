package com.rkh24.starexchange

    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.setValue
    import androidx.lifecycle.ViewModel

object ExchangeViewModelHolder {
    lateinit var exchangeViewModel: ExchangeViewModel
}

class CalculatorViewModel : ViewModel() {
    private val exchangeViewModel = ExchangeViewModelHolder.exchangeViewModel
        var state by mutableStateOf(CalculatorState())

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.ConvertToUSD -> convertToUSD()
            is CalculatorAction.ConvertToLBP -> convertToLBP()
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
        }
    }

    private fun calculate() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()

        if (number1 != null && number2 != null) {
            val result = when(state.operation) {
                is CalculatorOperation.Add -> number1 + number2
                is CalculatorOperation.Subtract -> number1 - number2
                is CalculatorOperation.Multiply -> number1 * number2
                is CalculatorOperation.Divide -> number1 / number2
                is CalculatorOperation.Sell-> convertToLBP()
                is CalculatorOperation.Buy -> convertToUSD()
                null -> return
            }
            state = state.copy(
                number1 = result.toString().take(15),
                operation = null,
                number2 = ""
            )
        }
    }

     private fun convertToUSD() {
        val number1 = state.number1.toDoubleOrNull()
        val buyRate = exchangeViewModel.buyRate.value?.toDouble() ?: return

        if (number1 != null) {
            val result = number1 / buyRate
            state = state.copy(number1 = result.toString().take(15))
        }
    }

    private fun convertToLBP() {
        val number1 = state.number1.toDoubleOrNull()
        val sellRate = exchangeViewModel.sellRate.value?.toDouble() ?: return

        if (number1 != null) {
            val result = number1 * sellRate
            state = state.copy(number1 = result.toString().take(15))
        }
    }



    private fun enterDecimal() {
        if(state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank()) {
            state = state.copy(
                number1 = state.number1 + "."
            )
            return
        } else if(!state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(
                number2 = state.number2 + "."
            )
        }
    }
    private fun enterNumber(number: Int) {
        if(state.operation == null) {
            if(state.number1.length >= MAX_NUM_LENGTH) {
                return
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if(state.number2.length >= MAX_NUM_LENGTH) {
            return
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }
        companion object {
            private const val MAX_NUM_LENGTH = 8
        }
    }
