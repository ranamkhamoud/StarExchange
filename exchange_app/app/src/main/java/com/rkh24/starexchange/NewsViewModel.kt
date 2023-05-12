package com.rkh24.starexchange

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkh24.starexchange.api.NewsService
import com.rkh24.starexchange.data.NewsApiResponse
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    val categories = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")
    private val newsService = NewsService.newsApi()

    private val _newsApiResponse = MutableLiveData<NewsApiResponse>()
    val newsApiResponse: LiveData<NewsApiResponse> get() = _newsApiResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Replace this with your actual API key
    private val apiKey = "804c2a55bed94812ac927dc068193e53"

    // Default country value, you can change it as per your requirement
    private val country = "us"
    init {
        fetchHeadlines(category = "technology")
    }


    private fun fetchHeadlines(category: String, query: String? = null) {
        viewModelScope.launch {
            try {
                val response = newsService.getTopHeadlines(apiKey, country, category, query)
                if (response.status == "ok") {
                    _newsApiResponse.value = response
                    Log.d("NewsViewModel", "Fetched articles: ${response.articles}")
                } else {
                    _errorMessage.value = "Error fetching news: ${response.status}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching news: ${e.message}"
            }
        }
    }

    fun onCategoryButtonClick(category: String) {
        fetchHeadlines(category)
    }
}