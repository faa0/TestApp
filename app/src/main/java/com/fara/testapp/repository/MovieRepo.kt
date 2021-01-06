package com.fara.testapp.repository

import com.fara.testapp.api.RetrofitInstance

class MovieRepo {

    suspend fun getMovies(language: String, page: Int) =
        RetrofitInstance.api.getPopularMovies(language = language, page = page)
}