package com.rkh24.starexchange

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.Authentication
import com.rkh24.starexchange.api.ExchangeService
import com.rkh24.starexchange.api.model.Token
import com.rkh24.starexchange.api.model.User
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistrationViewModel : ViewModel() {
    private val exchangeService = ExchangeService.exchangeApi()

    fun addUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = User().apply {
            this.username = username
            this.password = password
        }

        viewModelScope.launch {
            exchangeService.addUser(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        val errorMessage =
                            "Registration failed. Status code: ${response.code()}, message: ${response.message()}"
                        Log.e("RegistrationError", errorMessage)
                        onFailure(errorMessage)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    val errorMessage = "Registration failed. Error: ${t.message}"
                    Log.e("RegistrationError", errorMessage)
                    onFailure(errorMessage)
                }
            })
        }
    }


    fun authenticate(
        username: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = User().apply {
            this.username = username
            this.password = password
        }

        viewModelScope.launch {
            exchangeService.authenticate(user).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        response.body()?.token?.let { token ->
                            Authentication.saveToken(token)
                            onSuccess(token)
                        } ?: run {
                            onFailure("Authentication failed. Please try again.")
                        }
                    } else {
                        onFailure("Authentication failed. Please try again.")
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    onFailure("Authentication failed. Please check your network connection and try again.")
                }
            })
        }
    }
}
