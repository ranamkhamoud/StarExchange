package com.rkh24.starexchange

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rkh24.starexchange.api.Authentication
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.api.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserViewModel : ViewModel() {

    fun getUser(
        onResponse: (User) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val token = Authentication.getToken()
        val authHeader = if (token != null) "Bearer $token" else null
        if (authHeader != null) {
            ExchangeService.exchangeApi().getUser(authHeader).enqueue(object :
                Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            onResponse(user)
                            Log.d("User", "$user")

                        }
                    } else {
                        onFailure(RuntimeException("Error: ${response.code()} ${response.message()}"))
                    }
                }


                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("User", "Error getting user:", t)
                }
            })
        }
    }
}