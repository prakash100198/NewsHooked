package com.example.newsproject.mvvmnewsapp.ui

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsproject.R
import com.example.newsproject.databinding.ActivityNewsBinding
import com.example.newsproject.mvvmnewsapp.NewsApplication
import com.example.newsproject.mvvmnewsapp.db.ArticleDatabase
import com.example.newsproject.mvvmnewsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository= NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory= NewsViewModelProviderFactory(newsRepository,
            application as NewsApplication
        )
        viewModel= ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        //connecting our bottom menu with the fragments
        //val newsNavHostFragment= supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
        //val navController= newsNavHostFragment!!.findNavController()
        //binding.bottomNavigationView.setupWithNavController(newsNavHostFragment!!.findNavController())

        supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
            ?.let { binding.bottomNavigationView.setupWithNavController(it.findNavController()) }

    }



}