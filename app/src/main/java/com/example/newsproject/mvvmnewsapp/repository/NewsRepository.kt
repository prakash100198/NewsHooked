package com.example.newsproject.mvvmnewsapp.repository

import com.example.newsproject.mvvmnewsapp.api.RetrofitInstance
import com.example.newsproject.mvvmnewsapp.db.ArticleDatabase
import com.example.newsproject.mvvmnewsapp.models.Article
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Retrofit

//will be used to access article db and handle network calls
class NewsRepository(val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int)=
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article)= db.getArticleDao().upsert(article)

    fun getSavedNews()= db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article)= db.getArticleDao().deleteArticle(article)
}