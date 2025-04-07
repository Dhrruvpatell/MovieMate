package com.example.moviemate.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String = "",
    val title: String = "",
    val studio: String = "",
    val posterUrl: String = "",
    val rating: Float = 0f
) : Parcelable
