package com.nairobi.absensi.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nairobi.absensi.model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authRouter()
    }

    private fun authRouter() {
        setContent {
            val navController = rememberNavController()
            val userViewModel: UserViewModel = hiltViewModel()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController, userViewModel)
                }
                composable("forgot-password") {
                    ForgotPasswordScreen(navController, userViewModel)
                }
            }
        }
    }
}