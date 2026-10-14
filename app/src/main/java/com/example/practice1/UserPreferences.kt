package com.example.practice1

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_ADMIN = "is_admin" // Ключ для хранения статуса админа
    }

    // Получить ID сохраненного пользователя (-1 если не залогинен)
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // Сохранить ID при регистрации или входе
    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    // Сохранить статус администратора
    fun saveAdminStatus(isAdmin: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ADMIN, isAdmin).apply()
    }

    // Проверить, является ли текущий пользователь админом
    fun isAdmin(): Boolean {
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    // Очистить сессию при выходе из аккаунта
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}