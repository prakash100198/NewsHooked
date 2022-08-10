package com.example.newsproject.mvvmnewsapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int?= null,
    val author: String?,  //if not made nullable, and try to assign a null(since not all articles will have author name or other prop.) to non-nullable, it will crash the app
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,  //Json only understands primitive datatype hence we need a type convertor to convert from source(an other datatype) to string and vice versa.
    val title: String?,
    val url: String?,
    val urlToImage: String?
): Serializable // marked as serializable because Article is not a primitive data type, to pass these through fragments we mark it serializable.