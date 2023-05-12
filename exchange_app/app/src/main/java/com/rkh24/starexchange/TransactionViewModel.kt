package com.rkh24.starexchange

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.Authentication
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.api.model.Transaction
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TransactionViewModel : ViewModel() {

    fun addTransaction(transaction: Transaction, onResponse: () -> Unit, onFailure: () -> Unit) {
        ExchangeService.exchangeApi().addTransaction(
            transaction,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null
        ).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Log.d("ExchangeService", "Transaction added successfully")
                    onResponse()
                } else {
                    Log.d("ExchangeService", "Error adding transaction: ${response.errorBody()}")
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure()
            }
        })
    }

    fun getTransactions(
        onResponse: (List<Transaction>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val token = Authentication.getToken()
        val authHeader = if (token != null) "Bearer $token" else null

        if (authHeader != null) {
            ExchangeService.exchangeApi().getTransactions(authHeader).enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        onResponse(transactions)
                    } else {
                        onFailure(RuntimeException("Error: ${response.code()} ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    onFailure(t)
                }
            })
        }
    }
    val transactions = MutableLiveData<List<Transaction>?>(null)
    val error = MutableLiveData<Throwable?>(null)

    fun fetchTransactions() {
        viewModelScope.launch {
            getTransactions(
                onResponse = { transactionList ->
                    transactions.postValue(transactionList)
                },
                onFailure = { throwable ->
                    error.postValue(throwable)
                }
            )
        }
    }
}
