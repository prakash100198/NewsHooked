package com.example.newsproject.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.newsproject.mvvmnewsapp.NewsApplication
import com.example.newsproject.mvvmnewsapp.models.Article
import com.example.newsproject.mvvmnewsapp.models.NewsResponse
import com.example.newsproject.mvvmnewsapp.repository.NewsRepository
import com.example.newsproject.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

//We can't use constructor parameters in our view model, for that we need ViewModelProviderFactory to define how our own viewmodel should be created.
class NewsViewModel(
    val newsRepository: NewsRepository,
    appContext: Application
): AndroidViewModel(appContext){ //Let it inherit from AndroidViewModel() not from ViewModel() because former has the android app context that we need to check internet connectivity(the last func)

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage= 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage= 1
//    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
//        breakingNews.postValue(Resource.Loading())
//        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
//        breakingNews.postValue(handleBreakingNewsResponse(response))
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String)= viewModelScope.launch {
//        searchNews.postValue(Resource.Loading())
//        val response= newsRepository.searchNews(searchQuery, searchNewsPage)
//        searchNews.postValue(handleSearchNewsResponse(response))
        safeSearchNewsCall(searchQuery)
    }

    //fun for handling breaking news response, is the response successor or error...
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {  resultResponse ->
                breakingNewsPage++        //following lines handling pagination i.e adding previously loaded articles to new page of articles.
                if(breakingNewsResponse==null){
                    breakingNewsResponse= resultResponse
                }else{
                    val oldArticles= breakingNewsResponse?.articles
                    val newArticles= resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {  resultResponse ->
//                searchNewsPage++        //following lines handling PAGINATION i.e adding previously loaded articles to new page of articles.
//                if(searchNewsResponse==null){
//                    searchNewsResponse= resultResponse
//                }else{
//                    val oldArticles= searchNewsResponse?.articles
//                    val newArticles= resultResponse.articles
//                    oldArticles?.addAll(newArticles)
//                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)  = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews()= newsRepository.getSavedNews()

    fun deleteArticle(article:Article)= viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){//when there is a retrofit error, or any error while fetching the data from api
            when(t){
                is IOException-> breakingNews.postValue(Resource.Error("Network Failure"))//any exception in retrofit
                else-> breakingNews.postValue(Resource.Error("Conversion Error"))  //error while conversion of json to kotlin objects.
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){//when there is a retrofit error, or any error while fetching the data from api
            when(t){
                is IOException-> searchNews.postValue(Resource.Error("Network Failure"))//any exception in retrofit
                else-> searchNews.postValue(Resource.Error("Conversion Error"))  //error while conversion of json to kotlin objects.
            }
        }
    }

    private fun hasInternetConnection(): Boolean{
        //will check if user has internet connection or not
        val connectivityManager= getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork= connectivityManager.activeNetwork ?: return false //return false means mob has no internet conn..
            val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false //type of internet, wifi or mob int, etc..
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)-> true
                capabilities.hasTransport(TRANSPORT_ETHERNET)-> true
                else -> false
            }
        } else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI-> true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else-> false

                }
            }
        }
        return false
    }

}