package com.fara.testapp.api.entity

data class MovieResponse(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: MutableList<Movie>
)
