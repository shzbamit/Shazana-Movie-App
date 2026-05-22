package com.example.shazamovieapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun ref() =
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("searchHistory")

    fun saveQuery(query: String) {
        val data = hashMapOf(
            "query" to query,
            "timestamp" to System.currentTimeMillis()
        )

        ref().add(data)
    }

    fun getHistory(onResult: (List<String>) -> Unit) {
        ref()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull {
                    it.getString("query")
                }
                onResult(list)
            }
    }
}