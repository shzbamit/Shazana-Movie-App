package com.example.shazanamovieapp.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shazanamovieapp.data.Cast
import com.example.shazanamovieapp.data.Movie
import com.example.shazanamovieapp.data.MovieRepository
import com.example.shazanamovieapp.data.RetrofitInstance.api
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository()

    // Trending
    val trendingMovies = mutableStateListOf<Movie>()

    // Top rated
    val topRatedMovies = mutableStateListOf<Movie>()

    // Search results (FIXED TYPE)
    private val _searchResults = mutableStateOf<List<Movie>>(emptyList())
    val searchResults: State<List<Movie>> = _searchResults

    val selectedMovie = mutableStateOf<Movie?>(null)
    val movieCast = mutableStateListOf<Cast>()

    val recentlyViewedMovies = mutableStateListOf<Movie>()

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val trending = repository.getTrendingMovies()
                val topRated = repository.getTopRatedMovies()

                // 🔥 ADD LOGS HERE
                trending.forEach {
                    Log.d(
                        "TMDB_TRENDING",
                        "title=${it.title}, poster=${it.posterPath}, rating=${it.rating}"
                    )
                }

                topRated.forEach {
                    Log.d(
                        "TMDB_TOPRATED",
                        "title=${it.title}, poster=${it.posterPath}, rating=${it.rating}"
                    )
                }

                trendingMovies.clear()
                trendingMovies.addAll(trending)

                topRatedMovies.clear()
                topRatedMovies.addAll(topRated)

            } catch (e: Exception) {
                Log.e("TMDB", "Error loading movies", e)
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val response = api.searchMovies(query)

                response.results.forEach {
                    Log.d(
                        "TMDB_SEARCH",
                        "title=${it.title}, poster=${it.posterPath}, rating=${it.rating}"
                    )
                }

                _searchResults.value = response.results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val details = api.getMovieDetails(movieId)
                val credits = api.getMovieCredits(movieId)

                selectedMovie.value = details

                movieCast.clear()
                movieCast.addAll(credits.cast.take(10))
            } catch (e: Exception) {
                Log.e("TMDB", "Details load failed", e)
            }
        }
    }

    fun loadRecentlyViewed(uid: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("recentlyViewed")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->

                recentlyViewedMovies.clear()

                val list = result.map { doc ->
                    Movie(
                        id = doc.id.toInt(),
                        title = doc.getString("title") ?: "",
                        posterPath = doc.getString("posterPath") ?: ""
                    )
                }

                recentlyViewedMovies.addAll(list)
            }
    }

    fun addRecentlyViewed(movie: Movie, uid: String) {
        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "title" to movie.title,
            "posterPath" to movie.posterPath,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid)
            .collection("recentlyViewed")
            .document(movie.id.toString())
            .set(data)
    }

    fun getRecentlyViewed(uid: String, onResult: (List<Movie>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("recentlyViewed")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    Movie(
                        id = doc.id.toInt(),
                        title = doc.getString("title") ?: "",
                        posterPath = doc.getString("posterPath") ?: ""
                    )
                }
                onResult(list)
            }
    }
}