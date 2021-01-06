package com.fara.testapp.viewmodel

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
    private var movieResponse: MovieResponse? = null

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
}