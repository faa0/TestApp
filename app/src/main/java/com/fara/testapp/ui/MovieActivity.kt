package com.fara.testapp.ui

import android.content.Context
import android.content.Context.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fara.testapp.adapter.MovieAdapter
import com.fara.testapp.databinding.ActivityMovieBinding
import com.fara.testapp.repository.MovieRepo
import com.fara.testapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.fara.testapp.util.Resource
import com.fara.testapp.viewmodel.MovieViewModel
import com.fara.testapp.viewmodel.MovieViewModelProviderFactory

class MovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MovieAdapter
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieRepo = MovieRepo()
        val viewModelProviderFactory = MovieViewModelProviderFactory(movieRepo)
        viewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[MovieViewModel::class.java]

        setupRecyclerView()

        if (isOnline(this)) {
            viewModel.getMovies("us")
        } else {
            binding.btnRetry.visibility = View.VISIBLE
            Toast.makeText(this, "Turn on the Internet", Toast.LENGTH_SHORT).show()
        }

        onClickButtonRetry()

        viewModel.movies.observe(this, { it ->
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let {
                        adapter.differ.submitList(it.results.toList())
                        val totalPages = it.total_pages / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.moviesPage == totalPages
                        if (isLastPage) {
                            binding.rvPremiere.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let {
                        Log.d("TAG", "An error occured: $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(TRANSPORT_WIFI) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = false
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvPremiere.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getMovies("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter()
        binding.apply {
            rvPremiere.adapter = adapter
            rvPremiere.addOnScrollListener(this@MovieActivity.scrollListener)
        }
    }

    private fun onClickButtonRetry() {
        binding.btnRetry.setOnClickListener {
            if (isOnline(this)) {
                viewModel.getMovies("us")
                binding.btnRetry.visibility = View.GONE
            } else {
                Toast.makeText(this, "Turn on the Internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}