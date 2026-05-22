package com.it2161.mad232057d.shazanamovieapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.it2161.mad232057d.shazanamovieapp.ui.theme.ShazanaMovieAppTheme
import com.example.shazanamovieapp.screen.HomeScreen
import com.example.shazanamovieapp.screen.LoginScreen
import com.example.shazanamovieapp.screen.MovieDetailsScreen
import com.example.shazamamovieapp.screen.SearchScreen
import com.example.shazanamovieapp.screen.ProfileScreen
import com.example.shazanamovieapp.ui.navigation.Screen
import com.example.shazanamovieapp.viewmodel.FavoritesViewModel
import com.example.shazanamovieapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ShazanaMovieAppTheme {

                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()

                val movieViewModel: MovieViewModel = viewModel()
                val favoritesViewModel: FavoritesViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination =
                    if (auth.currentUser == null)
                        Screen.Login.route
                    else
                        Screen.Home.route
                ) {

                    // ================= LOGIN =================
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // ================= HOME =================
                    composable(Screen.Home.route) {
                        HomeScreen(
                            email = auth.currentUser?.email ?: "",
                            movieViewModel = movieViewModel,
                            favoritesViewModel = favoritesViewModel,
                            onSearchClick = {
                                navController.navigate(Screen.Search.route)
                            },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            },
                            onMovieClick = { movieId ->
                                navController.navigate("details/$movieId")
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            email = auth.currentUser?.email ?: "",
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()

                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    // ================= SEARCH =================
                    composable(Screen.Search.route) {
                        SearchScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },

                            onMovieClick = { movieId ->
                                navController.navigate("details/$movieId")
                            }
                        )
                    }

                    // ================= MOVIE DETAILS =================
                    composable(
                        route = "details/{movieId}",
                        arguments = listOf(
                            navArgument("movieId") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->

                        val movieId =
                            backStackEntry.arguments?.getInt("movieId") ?: 0

                        MovieDetailsScreen(
                            movieId = movieId,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            movieViewModel = movieViewModel,
                            favoritesViewModel = favoritesViewModel
                        )
                    }
                }
            }
        }
    }
}
