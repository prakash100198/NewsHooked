package com.example.newsproject.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsproject.mvvmnewsapp.models.Article
import retrofit2.http.Query


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @androidx.room.Query("Select * from articles")   // It'll return live data object(Live Data is a class of Android Architecture Component which enables the activities or fragments to subscribe to that live data), and will not work with suspend function because suspend funcs don't work with live data.
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article): Void

}