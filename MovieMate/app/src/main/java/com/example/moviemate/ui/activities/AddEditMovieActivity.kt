package com.example.moviemate.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate.data.model.Movie
import com.example.moviemate.databinding.ActivityAddEditMovieBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddEditMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditMovieBinding
    private val db = FirebaseFirestore.getInstance()

    private var isEditMode = false
    private var movieId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.hasExtra("movie")
        val movie = intent.getParcelableExtra<Movie>("movie")

        if (isEditMode && movie != null) {
            movieId = movie.id
            binding.etTitle.setText(movie.title)
            binding.etStudio.setText(movie.studio)
            binding.etPoster.setText(movie.posterUrl)
            binding.etRating.setText(movie.rating.toString())
            binding.btnSave.text = "Update Movie"
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val studio = binding.etStudio.text.toString().trim()
            val poster = binding.etPoster.text.toString().trim()
            val rating = binding.etRating.text.toString().toFloatOrNull() ?: 0f

            if (title.isEmpty() || studio.isEmpty() || poster.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val movieData = hashMapOf(
                "title" to title,
                "studio" to studio,
                "posterUrl" to poster,
                "rating" to rating
            )

            if (isEditMode && movieId != null) {
                db.collection("movies").document(movieId!!)
                    .update(movieData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Movie updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                db.collection("movies")
                    .add(movieData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Movie added", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
