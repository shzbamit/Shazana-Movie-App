package com.example.shazanamovieapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.shazamovieapp.data.FavoritesRepository
import com.example.shazanamovieapp.data.Movie
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesViewModel : ViewModel() {

    private val repo = FavoritesRepository()

    var favorites = mutableStateListOf<Movie>()
        private set

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        repo.getFavorites {
            favorites.clear()
            favorites.addAll(it)
        }
    }

    fun toggleFavorite(movie: Movie, uid: String) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(movie.id.toString())

        ref.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                ref.delete()
            } else {
                ref.set(movie)
            }
        }
    }

    fun isFavorite(id: Int): Boolean {
        return favorites.any { it.id == id }
    }
}