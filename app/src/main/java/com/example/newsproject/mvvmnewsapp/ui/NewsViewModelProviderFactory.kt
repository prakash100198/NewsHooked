package com.example.newsproject.mvvmnewsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.newsproject.mvvmnewsapp.NewsApplication
import com.example.newsproject.mvvmnewsapp.adapters.NewsAdapter
import com.example.newsproject.mvvmnewsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    val newsRepository: NewsRepository,
    val appContext: NewsApplication
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, appContext) as T
    }
}