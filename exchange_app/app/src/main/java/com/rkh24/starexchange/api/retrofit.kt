package com.rkh24.starexchange.api

import com.rkh24.starexchange.api.model.*
import com.rkh24.starexchange.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object NewsService {
    fun newsApi(): NewsApiService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NewsApiService::class.java)
    }
}

    interface NewsApiService {

        @GET("v2/top-headlines")
        suspend fun getTopHeadlines(
            @Query("apiKey") apiKey: String,
            @Query("country") country: String,
            @Query("category") category: String,
            @Query("q") query: String?
        ): NewsApiResponse
    }

object ExchangeService {
private const val API_URL: String = "http://10.0.2.2:5000"

    fun exchangeApi(): Exchange {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(Exchange::class.java)
    }

    interface Exchange {
        @GET("/exchangeRate")
       fun getExchangeRates(): Call<ExchangeRates>

        @POST("/transaction")
        fun addTransaction(@Body transaction: Transaction,
                           @Header("Authorization") authorization: String?): Call<Any>
        @GET("/transaction")
        fun getTransactions(@Header("Authorization") authorization: String):
                Call<List<Transaction>>

        @POST("/user")
        fun addUser(@Body user: User): Call<User>
        @GET("/getuser")
        fun getUser( @Header("Authorization") authorization: String?
        ): Call<User>
        @POST("/authentication")
        fun authenticate(@Body user: User): Call<Token>

        @GET ("/statistics")
        fun fetchInsights(): Call<InsightsResponse>

        @GET ("/graph")
        fun fetchGraph(): Call<GraphResponse>
        @POST("/exchangeTransaction")
      fun addExchangeTransaction(
            @Header("Authorization") authorization: ExchangeCard,
            @Body exchangeCard: String?
        ): Call <Any>

        @GET("/exchangeTransaction")
      fun getExchangeTransactions(
            @Header("Authorization") authorization: String?
        ): Call<List<ExchangeCard>>

        @HTTP(method = "DELETE", path = "deleteExchange", hasBody = true)
        fun deleteExchange(
            @Header("Authorization") token: String,
            @Body transactionId: Map<String, Int>
        ): Call<Map<String, String>>
    }

}






