package com.fara.testapp.repository

import androidx.lifecycle.ViewModel
import com.fara.testapp.api.MovieApi
import javax.inject.Inject

class MovieRepo @Inject constructor(
    private val api: MovieApi
) : ViewModel() {

    suspend fun getMovies(language: String, page: Int) =
        api.getPopularMovies(language = language, page = page)
}