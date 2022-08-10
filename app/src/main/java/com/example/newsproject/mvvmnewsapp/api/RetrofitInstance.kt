package com.example.newsproject.mvvmnewsapp.api

import com.example.newsproject.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//This is a retrofit builder
class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            //used to log responses of retrofit, used for better debugging
            val logging= HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)   //this will show us the body of response
            val client= OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            //Now we will use our client to pass it to retrofit instance
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())  //used to determine how the responses should be interpreted and converted to kotlin objects
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}