package com.example.newsproject.mvvmnewsapp.util


//class recommended by google to wrap around owr network responses.(to differentiate btw successful and error responses and also helps us to handle the loading state)
sealed class Resource<T>(    //sealed classes only allow certain classes to inherit from this base class where sealed type is mentioned.
    val data: T?= null,
    val message: String?=null
) {

    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T?= null): Resource<T>(data,message)
    class Loading<T> : Resource<T>()


}