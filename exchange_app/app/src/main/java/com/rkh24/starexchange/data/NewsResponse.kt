package com.rkh24.starexchange.data

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val source: NewsSource
)

data class NewsSource(
    val id: String?,
    val name: String
)
