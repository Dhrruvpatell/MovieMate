package com.example.moviemate.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviemate.data.model.Movie
import com.example.moviemate.databinding.ActivityMovieDetailBinding

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = intent.getParcelableExtra<Movie>("movie")

        movie?.let {
            binding.tvTitle.text = it.title
            binding.tvStudio.text = it.studio
            binding.tvRating.text = "Rating: ${it.rating}/5"

            Glide.with(this).load(it.posterUrl).into(binding.ivPoster)
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}
