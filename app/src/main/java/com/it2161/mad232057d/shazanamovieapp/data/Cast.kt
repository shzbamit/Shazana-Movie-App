package com.example.shazanamovieapp.data

import com.google.gson.annotations.SerializedName

data class CastResponse(
    val cast: List<Cast>
)

data class Cast(
    val id: Int,
    val name: String,

    @SerializedName("character")
    val character: String,

    @SerializedName("profile_path")
    val profilePath: String?
)