package com.example.practice1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practice1.ui.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen()
                }
            }
        }
    }
}

enum class Screen {
    LOGIN,
    REGISTER,
    MAIN,
    ADMIN // Добавлен экран администратора
}

@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    // Авто-вход: если пользователь залогинен, проверяем роль и открываем нужный экран
    var currentScreen by remember {
        mutableStateOf(
            if (userPreferences.getUserId() != -1) {
                if (userPreferences.isAdmin()) Screen.ADMIN else Screen.MAIN
            } else {
                Screen.LOGIN
            }
        )
    }

    val authViewModel: AuthViewModel = viewModel()

    when (currentScreen) {
        Screen.LOGIN -> {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { currentScreen = Screen.REGISTER },
                onLoginSuccess = { isAdmin ->
                    currentScreen = if (isAdmin) Screen.ADMIN else Screen.MAIN
                }
            )
        }
        Screen.REGISTER -> {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { currentScreen = Screen.LOGIN },
                onRegisterSuccess = {
                    // При успешной регистрации проверяем статус админа из UserPreferences
                    currentScreen = if (userPreferences.isAdmin()) Screen.ADMIN else Screen.MAIN
                }
            )
        }
        Screen.MAIN -> {
            MainScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    currentScreen = Screen.LOGIN
                }
            )
        }
        Screen.ADMIN -> {
            AdminScreen(
                authViewModel = authViewModel,
                onLogout = {
                    currentScreen = Screen.LOGIN
                }
            )
        }
    }
}