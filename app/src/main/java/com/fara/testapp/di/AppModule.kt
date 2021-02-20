package com.fara.testapp.di

import com.fara.testapp.api.MovieApi
import com.fara.testapp.repository.MovieRepo
import com.fara.testapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMovieApi(): MovieApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)

    @Singleton
    @Provides
    fun provideMovieRepo(api: MovieApi): MovieRepo = MovieRepo(api)
}