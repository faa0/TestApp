package com.fara.testapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fara.testapp.api.entity.MovieResponse
import com.fara.testapp.repository.MovieRepo
import com.fara.testapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieViewModel(
    private val movieRepo: MovieRepo,
) : ViewModel() {

    val movies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var moviesPage = 1
    var movieResponse: MovieResponse? = null

    fun getMovies(language: String) = viewModelScope.launch {
        movies.postValue(Resource.Loading())
        val response = movieRepo.getMovies(language, moviesPage)
        movies.postValue(handleMoviesResponse(response))
    }

    private fun handleMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { it ->
                moviesPage++
                if (movieResponse == null) {
                    movieResponse = it
                } else {
                    val oldMovies = movieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(movieResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}