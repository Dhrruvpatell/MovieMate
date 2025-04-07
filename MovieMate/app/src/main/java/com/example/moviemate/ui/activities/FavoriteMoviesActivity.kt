package com.example.moviemate.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviemate.data.model.Movie
import com.example.moviemate.databinding.ActivityFavoriteMoviesBinding
import com.example.moviemate.ui.adapter.MovieAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent

class FavoriteMoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteMoviesBinding
    private lateinit var adapter: MovieAdapter
    private val favoriteMovies = mutableListOf<Movie>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MovieAdapter(
            favoriteMovies,
            onEdit = { /* optional */ },
            onDelete = { /* optional */ },
            onFavorite = { /* optional */ },
            onClick = { movie ->
                startActivity(Intent(this, MovieDetailActivity::class.java).putExtra("movie", movie))
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteMoviesActivity)
            adapter = this@FavoriteMoviesActivity.adapter
        }

        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { favDocs ->
                val favIds = favDocs.map { it.id }

                if (favIds.isEmpty()) return@addOnSuccessListener

                db.collection("movies")
                    .whereIn(FieldPath.documentId(), favIds)
                    .get()
                    .addOnSuccessListener { movieDocs ->
                        favoriteMovies.clear()
                        for (doc in movieDocs) {
                            val movie = Movie(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                studio = doc.getString("studio") ?: "",
                                posterUrl = doc.getString("posterUrl") ?: "",
                                rating = doc.getDouble("rating")?.toFloat() ?: 0f
                            )
                            favoriteMovies.add(movie)
                        }
                        adapter.notifyDataSetChanged()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load favorites: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
