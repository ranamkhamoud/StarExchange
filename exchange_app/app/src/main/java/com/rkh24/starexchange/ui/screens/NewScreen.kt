package com.rkh24.starexchange.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.rkh24.starexchange.NewsViewModel
import com.rkh24.starexchange.data.NewsApiResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsUI(newsViewModel: NewsViewModel) {
    val newsApiResponse by newsViewModel.newsApiResponse.observeAsState(NewsApiResponse(status = "", totalResults = 0, articles = emptyList()))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Black
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(text = "")
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = White,
                        titleContentColor = Gray
                    )
                )
            }
        ) { values ->
            Column {
                LazyRow(Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                    itemsIndexed(newsViewModel.categories) { index, category ->
                        Button(onClick = { newsViewModel.onCategoryButtonClick(category) }) {
                            Text("Category $index: $category",color=Black)
                        }
                    }
                }

                LazyColumn(contentPadding = values) {
                    items(newsApiResponse.articles.size) { index ->
                        val article = newsApiResponse.articles[index]
                        ImageCard(
                            title = article.title,
                            description = article.description ?: "",
                            modifier = Modifier.padding(16.dp),
                            url = article.url,
                            urlToImage = article.urlToImage
                        )
                    }
                }
            }
        }
    }
}
