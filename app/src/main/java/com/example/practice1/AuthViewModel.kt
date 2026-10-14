package com.example.practice1

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest

// Вспомогательная функция хеширования пароля (SHA-256)
fun hashPassword(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).appDao()
    private val userPreferences = UserPreferences(application)
    private val client = OkHttpClient()

    // Храним сгенерированный 6-значный код
    var generatedCode by mutableStateOf("")
        private set

    // 1. Генерация и отправка 6-значного кода через EmailJS
    fun sendSixDigitCode(recipientEmail: String, onResult: (Boolean, String?) -> Unit) {
        val code = (100000..999999).random().toString()
        generatedCode = code

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serviceId = "service_zungqas"
                val templateId = "template_6pofkws"
                val publicKey = "wbZ0DOuMSLin_oRrt"

                val jsonBody = JSONObject().apply {
                    put("service_id", serviceId)
                    put("template_id", templateId)
                    put("user_id", publicKey)
                    put("template_params", JSONObject().apply {
                        put("to_email", recipientEmail)
                        put("reply_to_code", code)
                    })
                }.toString()

                val request = Request.Builder()
                    .url("https://api.emailjs.com/api/v1.0/email/send")
                    .addHeader("origin", "http://localhost")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()

                launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Ошибка отправки: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onResult(false, e.localizedMessage ?: "Ошибка сети")
                }
            }
        }
    }

    // 2. Проверка введенного пользователем кода
    fun verifyCode(inputCode: String): Boolean {
        return inputCode.trim() == generatedCode
    }

    // Предварительная проверка доступности логина, email и прав администратора
    fun checkCanRegister(login: String, email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isTryingAdmin = login.equals("admin", ignoreCase = true)

            // 1. Проверка на единственного админа
            if (isTryingAdmin && dao.hasAdminUser()) {
                launch(Dispatchers.Main) {
                    onResult(false, "Нельзя зарегистрироваться под логином admin: администратор уже существует!")
                }
                return@launch
            }

            // 2. Проверка существования логина
            if (dao.checkLoginExists(login.trim())) {
                launch(Dispatchers.Main) {
                    onResult(false, "Пользователь с таким логином уже существует!")
                }
                return@launch
            }

            // 3. Проверка существования Email
            if (dao.checkEmailExists(email.trim())) {
                launch(Dispatchers.Main) {
                    onResult(false, "Пользователь с таким Email уже существует!")
                }
                return@launch
            }

            // Успех
            launch(Dispatchers.Main) {
                onResult(true, null)
            }
        }
    }

    // 3. Регистрация пользователя в БД Room
    fun registerUser(
        gender: String,
        username: String,
        email: String,
        login: String,
        password: String,
        age: Int,
        stylePreference: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isTryingAdmin = login.equals("admin", ignoreCase = true)

            // Повторная защита перед записью в БД
            if (isTryingAdmin && dao.hasAdminUser()) {
                launch(Dispatchers.Main) {
                    onResult(false, "Администратор в системе уже зарегистрирован!")
                }
                return@launch
            }

            if (dao.checkLoginExists(login.trim())) {
                launch(Dispatchers.Main) {
                    onResult(false, "Логин уже занят!")
                }
                return@launch
            }

            if (dao.checkEmailExists(email.trim())) {
                launch(Dispatchers.Main) {
                    onResult(false, "Email уже занят!")
                }
                return@launch
            }

            // Хешируем пароль перед сохранением
            val hashedPassword = hashPassword(password)

            val newUser = UserEntity(
                gender = gender,
                username = username,
                email = email.trim(),
                login = login.trim(),
                password = hashedPassword, // <--- Сохраняем захешированный пароль
                age = age,
                stylePreference = stylePreference,
                isAdmin = isTryingAdmin
            )
            dao.insertUser(newUser)

            val createdUser = dao.getUserByCredentials(login.trim(), hashedPassword)
            if (createdUser != null) {
                userPreferences.saveUserId(createdUser.userId)
                userPreferences.saveAdminStatus(createdUser.isAdmin)
            }

            launch(Dispatchers.Main) {
                onResult(true, null)
            }
        }
    }

    // 4. Вход пользователя
    fun loginUser(
        emailOrLogin: String,
        password: String,
        onResult: (Boolean, Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Хешируем введенный пароль для сравнения
            val hashedPassword = hashPassword(password)
            val user = dao.getUserByCredentials(emailOrLogin.trim(), hashedPassword)

            if (user != null) {
                userPreferences.saveUserId(user.userId)
                userPreferences.saveAdminStatus(user.isAdmin)
            }

            launch(Dispatchers.Main) {
                if (user != null) {
                    onResult(true, user.isAdmin, null)
                } else {
                    onResult(false, false, "Неверный логин/email или пароль")
                }
            }
        }
    }

    // 5. Получение списка всех обычных пользователей для панели админа
    fun getRegularUsers(onResult: (List<UserEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val users = dao.getAllRegularUsers()
            launch(Dispatchers.Main) {
                onResult(users)
            }
        }
    }

    // 6. Точечное удаление конкретного пользователя по ID
    fun deleteUser(userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteUserById(userId)
            launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    // 7. Очистка только гардеробов и аутфитов без удаления пользователей
    fun clearWardrobeOnly(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearClothes()
            dao.clearOutfits()

            launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    // 8. Полная очистка данных в БД (с сохранением учетной записи админа)
    fun clearAllData(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearNonAdminUsers()
            dao.clearClothes()
            dao.clearOutfits()

            userPreferences.clearSession()

            launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    // 9. Выход из аккаунта
    fun logout() {
        userPreferences.clearSession()
    }
}