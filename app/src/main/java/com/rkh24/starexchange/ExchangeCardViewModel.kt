package com.rkh24.starexchange

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rkh24.starexchange.api.Authentication
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.api.model.ExchangeCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExchangeCardViewModel : ViewModel() {


    fun addExchangeTransaction(exchangeCard: ExchangeCard, onResponse: () -> Unit, onFailure: () -> Unit) {
        ExchangeService.exchangeApi().addExchangeTransaction(
            exchangeCard,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null
        ).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Log.d("ExchangeService", "Transaction added successfully")
                    onResponse()
                } else {
                    Log.d("ExchangeService", "Error adding transaction: ${response.code()} - ${response.message()} - ${response.errorBody()}")
                    onFailure()
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                onFailure()
            }
        })
    }
    fun getExchangeTransactions(
        onResponse: (List<ExchangeCard>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val token = Authentication.getToken()
        val authHeader = if (token != null) "Bearer $token" else null

        if (authHeader != null) {
            ExchangeService.exchangeApi().getExchangeTransactions(authHeader).enqueue(object : Callback<List<ExchangeCard>> {
                override fun onResponse(call: Call<List<ExchangeCard>>, response: Response<List<ExchangeCard>>) {
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        onResponse(transactions)
                    } else {
                        onFailure(RuntimeException("Error: ${response.code()} ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<ExchangeCard>>, t: Throwable) {
                    onFailure(t)
                }
            })
        }
    }


    fun deleteExchange(
        transactionId: Int,
        onResponse: () -> Unit,
        onFailure: () -> Unit
    ) {
        val token = Authentication.getToken()
        val authHeader = if (token != null) "Bearer $token" else null

        if (authHeader != null) {
            val requestBody = mapOf("transaction_id" to transactionId)
            ExchangeService.exchangeApi().deleteExchange(authHeader, requestBody).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        onResponse()
                    } else {
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Log.d("ExchangeService", "Error deleting exchange transaction: ", t)
                    onFailure()
                }
            })
        }
    }

}