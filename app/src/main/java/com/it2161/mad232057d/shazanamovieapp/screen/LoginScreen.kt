package com.example.shazanamovieapp.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx. compose. runtime. Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.shazanamovieapp.auth.GoogleAuthClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx. compose. foundation. layout. Column
import androidx.compose.ui.Modifier
import androidx. compose. foundation. layout. fillMaxSize
import androidx. compose. foundation. layout. Arrangement
import androidx. compose. ui. Alignment
import androidx. compose. material3.Button
import androidx. compose. material3.Text
import androidx.compose.ui.graphics.Color
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authClient = remember { GoogleAuthClient(context) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)

            authClient.signInWithGoogle(account) { success ->
                if (success) {
                    onLoginSuccess()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ================= LOGO =================
        Text(
            text = "ShazMovies",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Column {
            Text(
                text = "Discover movies today",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Button(onClick = {
            launcher.launch(authClient.getSignInIntent())
        }) {
            Text("Sign in with Google")
        }
    }
}