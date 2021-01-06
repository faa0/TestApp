package com.fara.testapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.fara.testapp.api.entity.Movie
import com.fara.testapp.databinding.ItemMovieBinding
import com.fara.testapp.util.Constants.Companion.POSTER_PATH_URL

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(
        ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MovieAdapter.MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.binding.apply {
            ivMovie.load(POSTER_PATH_URL + movie.poster_path)
            tvTitleMovie.text = movie.title
        }
    }

    override fun getItemCount() = differ.currentList.size
}