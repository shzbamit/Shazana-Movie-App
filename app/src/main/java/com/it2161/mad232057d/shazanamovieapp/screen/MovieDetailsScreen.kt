package com.example.shazanamovieapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shazanamovieapp.data.Movie
import com.example.shazanamovieapp.viewmodel.MovieViewModel
import com.example.shazanamovieapp.viewmodel.FavoritesViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    movieViewModel: MovieViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val movie = movieViewModel.selectedMovie.value

    LaunchedEffect(movieId) {
        movieViewModel.loadMovieDetails(movieId)
    }

    if (movie == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    LaunchedEffect(movie.id) {
        movieViewModel.addRecentlyViewed(movie, uid)
    }

    val cast = movieViewModel.movieCast

    val isFavorite = favoritesViewModel.isFavorite(movie.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title + Favorite
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    favoritesViewModel.toggleFavorite(movie, uid)
                }
            ) {
                Icon(
                    imageVector =
                    if (isFavorite) Icons.Filled.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        Text("⭐ ${movie.rating}")

        Spacer(modifier = Modifier.height(8.dp))

        // Release Date
        Text(
            text = "Release Date: ${movie.releaseDate}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Genres
        Text(
            text = "Genres: " + movie.genres.joinToString { it.name }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Overview
        Text(
            "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(movie.overview)

        Spacer(modifier = Modifier.height(24.dp))

        // Cast
        Text(
            "Cast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cast) { actor ->
                Card(
                    modifier = Modifier.width(120.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AsyncImage(
                            model = actor.profilePath?.let {
                                "https://image.tmdb.org/t/p/w200$it"
                            },
                            contentDescription = actor.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = actor.name,
                            maxLines = 2
                        )

                        Text(
                            text = actor.character,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}