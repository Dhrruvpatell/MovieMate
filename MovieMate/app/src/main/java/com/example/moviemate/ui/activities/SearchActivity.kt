package com.example.moviemate.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSearchMessage.text = "Search screen coming soon!"
    }
}
