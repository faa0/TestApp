package com.fara.testapp.api

import com.fara.testapp.api.entity.MovieResponse
import com.fara.testapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("language")
        language: String = "en_US",
        @Query("page")
        page: Int = 1
    ): Response<MovieResponse>
}