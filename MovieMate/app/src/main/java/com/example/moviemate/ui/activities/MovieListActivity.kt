package com.example.moviemate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviemate.R
import com.example.moviemate.data.model.Movie
import com.example.moviemate.databinding.ActivityMovieListBinding
import com.example.moviemate.ui.adapter.MovieAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MovieListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieListBinding
    private lateinit var movieAdapter: MovieAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var movieListener: ListenerRegistration? = null

    private val movies = mutableListOf<Movie>()
    private val allMovies = mutableListOf<Movie>() // unfiltered master list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        setupSearchListener()
        loadMoviesFromFirestore()
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_favorites -> {
                    startActivity(Intent(this, FavoriteMoviesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            movies,
            onEdit = { movie ->
                startActivity(
                    Intent(this, AddEditMovieActivity::class.java)
                        .putExtra("movie", movie)
                )
            },
            onDelete = { movie ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Movie")
                    .setMessage("Are you sure you want to delete \"${movie.title}\"?")
                    .setPositiveButton("Yes") { _, _ ->
                        db.collection("movies").document(movie.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Movie deleted", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            onFavorite = { movie ->
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Toast.makeText(this, "Please login to favorite movies", Toast.LENGTH_SHORT).show()
                } else {
                    val favRef = db.collection("users").document(userId)
                        .collection("favorites").document(movie.id)

                    favRef.set(mapOf("favorited" to true))
                        .addOnSuccessListener {
                            Toast.makeText(this, "${movie.title} added to favorites", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            onClick = { movie ->
                startActivity(Intent(this, MovieDetailActivity::class.java).putExtra("movie", movie))
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MovieListActivity)
            adapter = movieAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditMovieActivity::class.java))
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                filterMovies(query.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterMovies(query: String) {
        val filtered = allMovies.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.studio.contains(query, ignoreCase = true)
        }
        movies.clear()
        movies.addAll(filtered)
        movieAdapter.notifyDataSetChanged()
    }

    private fun loadMoviesFromFirestore() {
        movieListener = db.collection("movies")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                allMovies.clear()
                movies.clear()

                for (doc in snapshots!!) {
                    val movie = Movie(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        studio = doc.getString("studio") ?: "",
                        posterUrl = doc.getString("posterUrl") ?: "",
                        rating = doc.getDouble("rating")?.toFloat() ?: 0f
                    )
                    allMovies.add(movie)
                    movies.add(movie)
                }
                movieAdapter.notifyDataSetChanged()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        movieListener?.remove()
    }
}
