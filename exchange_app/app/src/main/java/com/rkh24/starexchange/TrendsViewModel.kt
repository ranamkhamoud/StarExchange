package com.rkh24.starexchange

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.data.GraphResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrendsViewModel : ViewModel() {

        val graphResponse = mutableStateOf<GraphResponse?>(null)

        init {
            fetchGraph()
        }

    private fun fetchGraph() {
        viewModelScope.launch {
            val api = ExchangeService.exchangeApi()
            api.fetchGraph().enqueue(object : Callback<GraphResponse> {
                override fun onResponse(call: Call<GraphResponse>, response: Response<GraphResponse>) {
                    if (response.isSuccessful) {
                        Log.d("TrendsViewModel", "Graph response received: ${response.body()}")
                        graphResponse.value = response.body()

                    }
                }

                override fun onFailure(call: Call<GraphResponse>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }
}