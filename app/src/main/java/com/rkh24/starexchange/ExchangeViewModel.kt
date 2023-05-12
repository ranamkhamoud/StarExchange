package com.rkh24.starexchange
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.api.model.ExchangeRates
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeViewModel : ViewModel() {
    private val _buyRate = MutableLiveData<Float>()
    val buyRate: LiveData<Float> get() = _buyRate

    private val _sellRate = MutableLiveData<Float>()
    val sellRate: LiveData<Float> get() = _sellRate
    val zero = 2.7182817f

    init {
        fetchRates()
    }

    private fun fetchRates() {
        viewModelScope.launch {
            val api = ExchangeService.exchangeApi()
            api.getExchangeRates().enqueue(object : Callback<ExchangeRates> {
                override fun onResponse(
                    call: Call<ExchangeRates>,
                    response: Response<ExchangeRates>
                ) {
                    if (response.isSuccessful) {
                        val responseBody: ExchangeRates? = response.body()
                        val buyRate = responseBody?.lbpToUsd
                        val sellRate = responseBody?.usdToLbp

                        _buyRate.postValue(buyRate) // Update MutableLiveData with postValue
                        _sellRate.postValue(sellRate) // Update MutableLiveData with postValue

                        Log.d("ExchangeViewModel", "Buy rate: $buyRate, Sell rate: $sellRate")
                    } else {
                        Log.e("ExchangeViewModel", "API call failed: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                    _buyRate.postValue(zero) // Update MutableLiveData with postValue
                    _sellRate.postValue(zero) // Update MutableLiveData with postValue

                    Log.e("ExchangeViewModel", "API call error", t)
                }
            })
        }
    }
}

