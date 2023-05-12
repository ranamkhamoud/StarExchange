package com.rkh24.starexchange
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.data.InsightsResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsightsViewModel : ViewModel() {
    val insightsResponse = mutableStateOf<InsightsResponse?>(null)

    init {
        fetchInsights()
    }

    private fun fetchInsights() {
        viewModelScope.launch {
            val api = ExchangeService.exchangeApi()
            api.fetchInsights().enqueue(object : Callback<InsightsResponse> {
                override fun onResponse(call: Call<InsightsResponse>, response: Response<InsightsResponse>) {
                    if (response.isSuccessful) {
                        Log.d("InsightsViewModel", "Insights response received: ${response.body()}")

                        insightsResponse.value = response.body()
                    }
                }

                override fun onFailure(call: Call<InsightsResponse>, t: Throwable) {
                    Log.e("Insights", "Error", t)
                }
            })
        }
    }
}
