package com.example.newsproject.mvvmnewsapp.models

import com.example.newsproject.mvvmnewsapp.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,//change from list to mutable list so that when we do our pagination we add our first page response to 2nd page, and so on because we need to store the old loaded previous page articles after pagination.
    val status: String,
    val totalResults: Int
)