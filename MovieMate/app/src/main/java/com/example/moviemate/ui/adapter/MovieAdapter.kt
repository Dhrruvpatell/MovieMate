package com.example.moviemate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviemate.data.model.Movie
import com.example.moviemate.databinding.ItemMovieBinding

class MovieAdapter(
    private val movieList: List<Movie>,
    private val onEdit: (Movie) -> Unit,
    private val onDelete: (Movie) -> Unit,
    private val onFavorite: (Movie) -> Unit,
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        with(holder.binding) {
            tvTitle.text = movie.title
            tvStudio.text = movie.studio
            tvRating.text = "⭐ ${movie.rating}"

            Glide.with(ivPoster.context).load(movie.posterUrl).into(ivPoster)

            btnEdit.setOnClickListener { onEdit(movie) }
            btnDelete.setOnClickListener { onDelete(movie) }
            btnFavorite.setOnClickListener { onFavorite(movie) }
            root.setOnClickListener { onClick(movie) }
        }
    }

    override fun getItemCount() = movieList.size
}
