package com.example.newsproject.mvvmnewsapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsproject.R
import com.example.newsproject.databinding.FragmentSavedNewsBinding
import com.example.newsproject.mvvmnewsapp.adapters.NewsAdapter
import com.example.newsproject.mvvmnewsapp.ui.NewsActivity
import com.example.newsproject.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter:NewsAdapter
    lateinit var binding: FragmentSavedNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel
        binding= FragmentSavedNewsBinding.bind(view)

        setupRecyclerView()
        newsAdapter.setOnItemCLickListener {
            //Android Bundles are generally used for passing data from one activity to another. Here concept of key-value pair is used where the data that one wants to pass is the value of the map, which can be later retrieved by using the key.
            val bundle= Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
        }

        //code for swiping and deleting articles from saved news
        val itemTouchHelperCallback= object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position= viewHolder.absoluteAdapterPosition
                val article= newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "Article Deleted",Snackbar.LENGTH_LONG).apply {
                    //setup and undo action for in snackbar to undo deleted article
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        //Whenever the data changes in the database(here in the saved news fragment the Observe{} is called.
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles->
            //we just want to update our recycler view
            newsAdapter.differ.submitList(articles)

        })
    }


    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        binding.rvSavedNews.apply {
            adapter= newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}