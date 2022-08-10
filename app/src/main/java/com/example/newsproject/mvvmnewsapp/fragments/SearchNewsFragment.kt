package com.example.newsproject.mvvmnewsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsproject.R
import com.example.newsproject.databinding.FragmentSearchNewsBinding
import com.example.newsproject.mvvmnewsapp.adapters.NewsAdapter
import com.example.newsproject.mvvmnewsapp.ui.NewsActivity
import com.example.newsproject.mvvmnewsapp.ui.NewsViewModel
import com.example.newsproject.mvvmnewsapp.util.Constants
import com.example.newsproject.mvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsproject.mvvmnewsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentSearchNewsBinding
    lateinit var newsAdapter: NewsAdapter
    val TAG="SearchNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel
        binding= FragmentSearchNewsBinding.bind(view)
        setupRecyclerView()



        newsAdapter.setOnItemCLickListener {
            //Android Bundles are generally used for passing data from one activity to another. Here concept of key-value pair is used where the data that one wants to pass is the value of the map, which can be later retrieved by using the key.
            val bundle= Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle)
        }

        //We'll add a little delay to our search query, because if we don't then there would be too many requests for each letter being typed in the search bar.
        //We can easily implement the delay using CoRoutines
        var job: Job?= null
        //Main is the main thread dispatcher.
        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch {
                delay( SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success-> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //Code to check for if we have queried the last page from the newsAPI and update the isLastPage to true
//                        val totalPages= newsResponse.totalResults/20 +2 //+1 for integer correction ad +1 for the last queried page is always empty
//                        isLastPage= totalPages == viewModel.searchNewsPage
//                        if(isLastPage){
//                            binding.rvSearchNews.setPadding(0,0,0,0)//50dp of padding will be left at last if we don't do this
//                        }
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let { message->
                        Log.e(TAG, "An Error Occurred: $message")
                        Toast.makeText(activity, "An Error Occurred: $message", Toast.LENGTH_SHORT).show()

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
//        isLoading= false
    }
    private fun showProgressBar(){
        binding.paginationProgressBar.visibility= View.VISIBLE
//        isLoading= true
    }

    //PAGINATION SECTION
    //Here we detect about scrolling of recyclerview, if it has scrolled to last article of 1st page then load next page etc..
//    var isLoading= false
//    var isLastPage= false
//    var isScrolling= false
//
//    val scrollListener= object : RecyclerView.OnScrollListener() {
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){//just means if the screen os scrolling or not.
//                isScrolling= true
//            }
//        }
//
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//
//            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
//            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
//            val visibleItemCount= layoutManager.childCount
//            val totalItemCount= layoutManager.itemCount
//
//            val isNotLoadingAndNotLastPage= !isLoading && !isLastPage
//            val isAtLastItem= firstVisibleItemPosition+visibleItemCount >= totalItemCount
//            val isNotAtBeginning= firstVisibleItemPosition >= 0
//            val isTotalMoreThanVisible= totalItemCount >= Constants.QUERY_PAGE_SIZE //20
//            val shouldPaginate= isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
//                    && isTotalMoreThanVisible && isScrolling
//            if(shouldPaginate){
//                viewModel.searchNews(binding.etSearch.text.toString())
//                isScrolling= false
//            }
//
//        }
//    }

    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        binding.rvSearchNews.apply {
            adapter= newsAdapter
            layoutManager= LinearLayoutManager(activity)
//            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }


}