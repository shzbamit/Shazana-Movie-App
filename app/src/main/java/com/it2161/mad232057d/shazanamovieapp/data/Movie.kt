package com.example.shazanamovieapp.data

import com.google.gson.annotations.SerializedName

data class Movie(
    var id: Int = 0,
    var title: String = "",

    @SerializedName("poster_path")
    val posterPath: String = "",

    @SerializedName("vote_average")
    val rating: Double = 0.0,

    val overview: String = "",

    @SerializedName("release_date")
    val releaseDate: String = "",

    val genres: List<Genre> = emptyList()
)

data class Genre(
    val id: Int = 0,
    val name: String = ""
)