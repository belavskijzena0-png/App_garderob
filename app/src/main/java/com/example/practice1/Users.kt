package com.example.practice1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") val userId: Int = 0,
    val gender: String,
    val username: String,
    val email: String,
    val login: String,
    val password: String,
    val age: Int,
    @ColumnInfo(name = "style_preference") val stylePreference: String,
    @ColumnInfo(name = "is_admin") val isAdmin: Boolean = false
)