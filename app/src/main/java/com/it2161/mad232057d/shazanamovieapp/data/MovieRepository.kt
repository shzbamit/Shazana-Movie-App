package com.example.shazanamovieapp.data

class MovieRepository {

    suspend fun getTrendingMovies(): List<Movie> {
        return RetrofitInstance.api
            .getTrendingMovies("48abd9d4e85022f5795d909d7e09390b")
            .results
    }

    suspend fun getTopRatedMovies(): List<Movie> {
        return RetrofitInstance.api
            .getTopRatedMovies("48abd9d4e85022f5795d909d7e09390b")
            .results
    }
}