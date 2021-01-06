package com.fara.testapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fara.testapp.repository.MovieRepo

@Suppress("UNCHECKED_CAST")
class MovieViewModelProviderFactory(
    private val movieRepo: MovieRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieViewModel(movieRepo) as T
    }
}