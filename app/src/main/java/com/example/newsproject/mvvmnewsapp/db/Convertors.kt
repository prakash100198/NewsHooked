package com.example.newsproject.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.example.newsproject.mvvmnewsapp.models.Source

class Convertors {

    @TypeConverter
    fun fromSource(source: Source): String? {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}