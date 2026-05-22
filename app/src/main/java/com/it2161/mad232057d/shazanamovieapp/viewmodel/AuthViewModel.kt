package com.example.shazanamovieapp.viewmodel

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.example.shazanamovieapp.auth.GoogleAuthClient
import com.example.shazanamovieapp.ui.state.AuthState

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authClient = GoogleAuthClient(application)

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    init {
        val user = authClient.getCurrentUser()
        _authState.value = AuthState(isLoggedIn = user != null)
    }

    fun signIn(account: GoogleSignInAccount) {
        _authState.value = AuthState(isLoading = true)

        authClient.signInWithGoogle(account) { success ->
            if (success) {
                _authState.value = AuthState(isLoggedIn = true)
            } else {
                _authState.value = AuthState(error = "Login failed")
            }
        }
    }

    fun signOut() {
        authClient.signOut()
        _authState.value = AuthState(isLoggedIn = false)
    }
}