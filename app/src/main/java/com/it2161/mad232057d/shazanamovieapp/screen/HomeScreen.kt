package com.example.shazanamovieapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx. lifecycle. viewmodel. compose. viewModel
import androidx. compose. foundation. layout. padding
import androidx. compose. foundation. lazy. LazyRow
import com. example. shazanamovieapp. ui. components. MovieCard
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.shazanamovieapp.data.Movie
import androidx. compose. foundation. verticalScroll
import androidx. compose. foundation. layout. Row
import androidx. compose. foundation. layout. fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx. compose. material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.example.shazanamovieapp.viewmodel.FavoritesViewModel
import com.example.shazanamovieapp.viewmodel.MovieViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    email: String,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    movieViewModel: MovieViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {

    val scrollState = rememberScrollState()

    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    val recentMovies = remember(movieViewModel.recentlyViewedMovies) {
        movieViewModel.recentlyViewedMovies.take(5)
    }

    LaunchedEffect(uid) {
        movieViewModel.loadRecentlyViewed(uid)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // ================= LOGO =================
        Text(
            text = "ShazMovies",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Hello, $email 👋",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Discover movies today",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // ================= PROFILE ICON =================
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // ================= SEARCH =================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSearchClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFEFEFEF),
            tonalElevation = 2.dp
        ) {
            Text(
                text = "🔍 Search movies...",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ================= RECENTLY VIEWED =================
        Text("Recently Viewed")

        Spacer(modifier = Modifier.height(8.dp))

        if (recentMovies.isEmpty()) {
            Button(onClick = onSearchClick) {
                Text("Explore Movies 🍿")
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentMovies) { movie ->

                    // check favorite state
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

        Spacer(modifier = Modifier.height(24.dp))

        // ================= TRENDING =================
        Text("Trending Movies")

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(movieViewModel.trendingMovies) { movie ->

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

        Spacer(modifier = Modifier.height(24.dp))

        // ================= TOP RATED =================
        Text("Top Rated Movies")

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(movieViewModel.topRatedMovies) { movie ->

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

        Spacer(modifier = Modifier.height(24.dp))

        // ================= FAVORITES =================
        Text("❤️ Favorites")

        Spacer(modifier = Modifier.height(8.dp))

        if (favoritesViewModel.favorites.isEmpty()) {
            Text("No favorites yet 💔")
        } else {
            LazyRow {
                items(favoritesViewModel.favorites) { movie ->
                    MovieCard(
                        movie = movie,
                        isFavorite = true,   // <- always true in favorites
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
}