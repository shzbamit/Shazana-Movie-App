package com.example.shazamamovieapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shazanamovieapp.ui.components.MovieCard
import com.example.shazanamovieapp.viewmodel.FavoritesViewModel
import com.example.shazanamovieapp.viewmodel.MovieViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    movieViewModel: MovieViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    var query by remember { mutableStateOf("") }

    val searchResults = movieViewModel.searchResults.value

    val historyList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBackClick) {
            Text("⬅ Back to Home")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it

                if (it.isNotBlank()) {
                    movieViewModel.searchMovies(it)

                    if (!historyList.contains(it)) {
                        historyList.add(0, it)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search movies...") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            query.isEmpty() -> {
                Text("Start typing to search 🎬")
            }

            searchResults.isEmpty() -> {
                Text("No results found 😢")
            }

            else -> {
                LazyColumn {
                    items(searchResults) { movie ->

                        val isFav = favoritesViewModel.isFavorite(movie.id)

                        MovieCard(
                            movie = movie,
                            isFavorite = isFav,
                            onFavoriteClick = {
                                favoritesViewModel.toggleFavorite(movie, uid)
                            },
                            onClick = {
                                onMovieClick(movie.id)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent searches")

        LazyRow {
            items(historyList) { item ->
                AssistChip(
                    onClick = { query = item },
                    label = { Text(item) }
                )
            }
        }
    }
}