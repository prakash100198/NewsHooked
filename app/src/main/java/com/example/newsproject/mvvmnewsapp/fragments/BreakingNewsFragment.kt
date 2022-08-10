package com.example.newsproject.mvvmnewsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsproject.R
import com.example.newsproject.databinding.FragmentBreakingNewsBinding
import com.example.newsproject.mvvmnewsapp.adapters.NewsAdapter
import com.example.newsproject.mvvmnewsapp.ui.NewsActivity
import com.example.newsproject.mvvmnewsapp.ui.NewsViewModel
import com.example.newsproject.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsproject.mvvmnewsapp.util.Resource
import retrofit2.http.Tag

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentBreakingNewsBinding

    val TAG= "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel
        binding= FragmentBreakingNewsBinding.bind(view)

        setupRecyclerView()

        newsAdapter.setOnItemCLickListener {
            //Android Bundles are generally used for passing data from one activity to another. Here concept of key-value pair is used where the data that one wants to pass is the value of the map, which can be later retrieved by using the key.
            val bundle= Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }

        //we'll now subscribe to the live data, by using observe object,LiveData notifies Observer objects when underlying data changes.
        // You can consolidate your code to update the UI in these Observer objects. That way, you don't need to update the UI every time the app data changes because the observer does it for you.
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success-> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //Code to check for if we have queried the last page from the newsAPI and update the isLastPage to true
                        val totalPages= newsResponse.totalResults/20 +2 //+1 for integer correction ad +1 for the last queried page is always empty
                        isLastPage= totalPages == viewModel.breakingNewsPage
                        if(isLastPage){
                            binding.rvBreakingNews.setPadding(0,0,0,0)//50dp of padding will be left at last if we don't do this
                        }
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let { message->
                        Log.e(TAG, "An Error Occurred: $message")
                        Toast.makeText(activity, "An Error Occurred: $message",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility= View.INVISIBLE
        isLoading= false
    }
    private fun showProgressBar(){
        binding.paginationProgressBar.visibility= View.VISIBLE
        isLoading= true
    }

    //PAGINATION SECTION
    //Here we detect about scrolling of recyclerview, if it has scrolled to last article of 1st page then load next page etc..
    var isLoading= false
    var isLastPage= false
    var isScrolling= false

    val scrollListener= object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){//just means if the screen os scrolling or not.
                isScrolling= true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount= layoutManager.childCount
            val totalItemCount= layoutManager.itemCount

            val isNotLoadingAndNotLastPage= !isLoading && !isLastPage
            val isAtLastItem= firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible= totalItemCount >= QUERY_PAGE_SIZE //20
            val shouldPaginate= isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling= false
            }

        }
    }

    //RecyclerView Function
    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter= newsAdapter
            layoutManager= LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}