package com.rkh24.starexchange

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
class LoginViewModel : ViewModel() {
    private val exchangeService = ExchangeService.exchangeApi()

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
                            onFailure("Login failed. Please check your credentials and try again.")
                        }
                    } else {
                        onFailure("Login failed. Please check your credentials and try again.")
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    onFailure("Login failed. Please check your network connection and try again.")
                }
            })
        }
    }
}
