package com.example.practice1.auth

import android.util.Patterns

object AuthValidation {
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidCode(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
}