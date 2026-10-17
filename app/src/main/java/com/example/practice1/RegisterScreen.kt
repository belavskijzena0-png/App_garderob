package com.example.practice1

import android.os.CountDownTimer
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Вспомогательная функция маскировки email
fun maskEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email
    val name = parts[0]
    val domain = parts[1]
    val visibleLength = (name.length / 2).coerceAtLeast(1)
    val maskedName = name.take(visibleLength) + "*".repeat(name.length - visibleLength)
    return "$maskedName@$domain"
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var stylePreference by remember { mutableStateOf("") }

    var generalError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // --- Переменные подтверждения кода (OTP) ---
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    var attemptsLeft by remember { mutableIntStateOf(5) }
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var isTimerActive by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    // Очистка всех временных данных подтверждения
    fun resetOtpState() {
        timer?.cancel()
        timer = null
        otpInput = ""
        otpError = null
        attemptsLeft = 5
        secondsRemaining = 60
        isTimerActive = false
    }

    // Функция запуска таймера 60 сек
    fun startResendTimer() {
        timer?.cancel()
        secondsRemaining = 60
        isTimerActive = true
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                isTimerActive = false
                secondsRemaining = 0
            }
        }.start()
    }

    // --- Валидация на клиенте ---
    val usernameError = when {
        username.isBlank() -> null
        username.trim().length < 2 -> "Имя должно содержать не менее 2 символов"
        else -> null
    }

    val emailError = when {
        email.isBlank() -> null
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Введите корректный E-mail адрес"
        else -> null
    }

    val loginError = when {
        login.isBlank() -> null
        login.trim().length < 3 -> "Логин должен быть не короче 3 символов"
        else -> null
    }

    val passwordError = when {
        password.isBlank() -> null
        password.length < 6 -> "Пароль должен содержать минимум 6 символов"
        else -> null
    }

    val confirmPasswordError = when {
        confirmPassword.isBlank() -> null
        confirmPassword != password -> "Пароли не совпадают"
        else -> null
    }

    val genderError = when {
        gender.isBlank() -> null
        gender.trim().length < 2 -> "Укажите ваш пол"
        else -> null
    }

    val ageInt = age.toIntOrNull()
    val ageError = when {
        age.isBlank() -> null
        ageInt == null || ageInt !in 7..120 -> "Укажите корректный возраст (от 7 до 120)"
        else -> null
    }

    val stylePreferenceError = when {
        stylePreference.isBlank() -> null
        stylePreference.trim().length < 2 -> "Укажите предпочтение в стиле"
        else -> null
    }

    val isFormValid = username.isNotBlank() && usernameError == null &&
            email.isNotBlank() && emailError == null &&
            login.isNotBlank() && loginError == null &&
            password.isNotBlank() && passwordError == null &&
            confirmPassword.isNotBlank() && confirmPasswordError == null &&
            gender.isNotBlank() && genderError == null &&
            age.isNotBlank() && ageError == null &&
            stylePreference.isNotBlank() && stylePreferenceError == null

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0C3FC),
            Color(0xFF8EC5FC),
            Color(0xFFFFD1DC)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "РЕГИСТРАЦИЯ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3142)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it; generalError = "" },
                label = { Text("Имя пользователя *") },
                isError = usernameError != null,
                supportingText = { usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; generalError = "" },
                label = { Text("Email *") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = login,
                onValueChange = { login = it; generalError = "" },
                label = { Text("Логин *") },
                isError = loginError != null,
                supportingText = { loginError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; generalError = "" },
                label = { Text("Пароль *") },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; generalError = "" },
                label = { Text("Повторите пароль *") },
                isError = confirmPasswordError != null,
                supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it; generalError = "" },
                label = { Text("Пол (Мужской / Женский) *") },
                isError = genderError != null,
                supportingText = { genderError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it; generalError = "" },
                label = { Text("Возраст *") },
                isError = ageError != null,
                supportingText = { ageError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = stylePreference,
                onValueChange = { stylePreference = it; generalError = "" },
                label = { Text("Предпочтение в стиле *") },
                isError = stylePreferenceError != null,
                supportingText = { stylePreferenceError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            if (generalError.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = generalError,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    isLoading = true
                    generalError = ""

                    authViewModel.checkCanRegister(login, email) { canRegister, error ->
                        if (!canRegister) {
                            isLoading = false
                            generalError = error ?: "Ошибка валидации данных!"
                        } else {
                            authViewModel.sendSixDigitCode(email) { sent, sendError ->
                                isLoading = false
                                if (sent) {
                                    resetOtpState()
                                    startResendTimer()
                                    showOtpDialog = true
                                } else {
                                    generalError = sendError ?: "Ошибка отправки кода"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Зарегистрироваться", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onNavigateToLogin,
                enabled = !isLoading
            ) {
                Text("Уже есть аккаунт? Войти", color = Color(0xFF2D3142))
            }
        }
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = {
                resetOtpState()
                showOtpDialog = false
            },
            title = { Text("Подтверждение Email") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Код отправлен на: ${maskEmail(email)}",
                        fontSize = 14.sp,
                        color = Color(0xFF2D3142)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OtpInputField(
                        otpText = otpInput,
                        onOtpTextChange = {
                            if (attemptsLeft > 0) {
                                otpInput = it
                                otpError = null
                            }
                        }
                    )

                    otpError?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            if (!isTimerActive) {
                                authViewModel.sendSixDigitCode(email) { sent, _ ->
                                    if (sent) {
                                        resetOtpState()
                                        startResendTimer()
                                        Toast.makeText(context, "Новый код отправлен", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = !isTimerActive
                    ) {
                        if (isTimerActive) {
                            Text("Повторный код через $secondsRemaining сек.")
                        } else {
                            Text("Отправить код повторно")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = attemptsLeft > 0 && otpInput.length == 6,
                    onClick = {
                        if (authViewModel.verifyCode(otpInput)) {
                            authViewModel.registerUser(
                                gender = gender,
                                username = username,
                                email = email,
                                login = login,
                                password = password,
                                age = age.toIntOrNull() ?: 0,
                                stylePreference = stylePreference,
                                onResult = { isSuccess, error ->
                                    if (isSuccess) {
                                        resetOtpState()
                                        showOtpDialog = false
                                        Toast.makeText(
                                            context,
                                            "Успешная регистрация!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onRegisterSuccess()
                                    } else {
                                        otpError = error ?: "Ошибка регистрации"
                                    }
                                }
                            )
                        } else {
                            attemptsLeft -= 1
                            if (attemptsLeft <= 0) {
                                otpError = "Превышено 5 попыток ввода! Запросите новый код."
                            } else {
                                otpError = "Неверный код! Осталось попыток: $attemptsLeft"
                            }
                        }
                    }
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        resetOtpState()
                        showOtpDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun OtpInputField(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    otpLength: Int = 6
) {
    BasicTextField(
        value = otpText,
        onValueChange = { if (it.length <= otpLength) onOtpTextChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    val char = when {
                        index < otpText.length -> otpText[index].toString()
                        else -> ""
                    }
                    val isFocused = index == otpText.length

                    Box(
                        modifier = Modifier
                            .size(38.dp, 48.dp)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) Color(0xFF2D3142) else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3142),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}