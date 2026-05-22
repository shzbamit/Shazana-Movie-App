package com.example.shazamovieapp.data

import com.example.shazanamovieapp.data.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String? {
        return auth.currentUser?.uid
    }

    fun addFavorite(movie: Movie) {
        val userId = uid() ?: return

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(movie.id.toString())
            .set(movie)
    }

    fun removeFavorite(movieId: Int) {
        val userId = uid() ?: return

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(movieId.toString())
            .delete()
    }

    fun getFavorites(onResult: (List<Movie>) -> Unit) {
        val userId = uid()

        if (userId == null) {
            onResult(emptyList())
            return
        }

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull {
                    it.toObject(Movie::class.java)
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}