package com.example.newsproject.mvvmnewsapp.api

import com.example.newsproject.mvvmnewsapp.models.NewsResponse
import com.example.newsproject.mvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query



interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String= "in",
        @Query("page")
        pageNumber: Int= 1,
        @Query("apiKey")
        apiKey: String= API_KEY
    ): retrofit2.Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int= 1,
        @Query("apiKey")
        apiKey: String= API_KEY
    ): retrofit2.Response<NewsResponse>

}