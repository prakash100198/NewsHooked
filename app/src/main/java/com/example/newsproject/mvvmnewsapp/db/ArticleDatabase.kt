package com.example.newsproject.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsproject.mvvmnewsapp.models.Article


@Database(
    entities = [Article::class],
    version=1
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase:RoomDatabase() {  //db class for room always needs to be abstract

    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile //Means other threads can immediately see the changes made in the database
        private var instance: ArticleDatabase? = null
        private val LOCK = Any() //Use that to synchronise setting that instance so we make sure there is only one instance of db anytime

        operator fun invoke(context: Context)= instance?: synchronized(LOCK) { //invoke() is called right after we create an instance of this class ArticleDatabase()
            instance?: createDatabase(context).also{ instance= it}
        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }


}