package com.example.practice1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class ClothingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "clothing_id") val clothingId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int? = null,
    val type: String,
    val size: String,
    val color: String,
    val season: String,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
)