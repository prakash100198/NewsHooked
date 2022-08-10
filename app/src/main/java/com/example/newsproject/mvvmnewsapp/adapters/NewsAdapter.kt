package com.example.newsproject.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsproject.R
import com.example.newsproject.mvvmnewsapp.models.Article

class NewsAdapter(
    //list:List<Article>  In case of real world apps using this parameter not very efficient because this'll update all the entries in the list of articles even if there is change in one article
    //To overcome this we use diffutil package, this makes sure not to recycle other items, but only recycle that item that is to be changed.
    //This process happens in the background i.e not on the main thread hence making it efficient.


): RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallBack= object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    //Async List Differ that will check old and new article list to see changes and update only those which are new.
    val differ= AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article= differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(findViewById(R.id.ivArticleImage))
            findViewById<TextView>(R.id.tvSource).text= article.source?.name
            val datePublished: String= article.publishedAt?.slice(0..9) ?: ""

            findViewById<TextView>(R.id.tvPublishedAt).text= datePublished
            findViewById<TextView>(R.id.tvDescription).text= article.description
            findViewById<TextView>(R.id.tvTitle).text= article.title
            setOnClickListener {
                onItemClickListener?.let{ it(article) }
            }
        }
    }

    private var onItemClickListener:((Article)-> Unit)?= null   //Setting onclick listeners when an single article is clicked in the UI

    fun setOnItemCLickListener(listener:(Article)-> Unit){
        onItemClickListener= listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}