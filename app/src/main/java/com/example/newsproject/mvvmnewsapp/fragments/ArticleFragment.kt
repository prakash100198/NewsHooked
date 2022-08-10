package com.example.newsproject.mvvmnewsapp.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.newsproject.R
import com.example.newsproject.databinding.FragmentArticleBinding
import com.example.newsproject.mvvmnewsapp.models.Article
import com.example.newsproject.mvvmnewsapp.ui.NewsActivity
import com.example.newsproject.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding
    val args: ArticleFragmentArgs by navArgs()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.getSerializable("article")?.let { article_dataclass->
//            article=article_dataclass as Article
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel
        binding= FragmentArticleBinding.bind(view)

        //Here we will receive the articles(bundle) that was sent from 3 different fragment.

        val article = args.article
        binding.webView.apply {
            webViewClient= WebViewClient() //to makesure our articles open in the webview not the browser of phone
            article.url?.let { loadUrl(it) }
            //settings.javaScriptEnabled= true

        }
        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved", Snackbar.LENGTH_SHORT).show()
        }

    }
}