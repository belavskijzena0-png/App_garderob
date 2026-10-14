package com.example.practice1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "outfit_items",
    primaryKeys = ["outfit_id", "clothing_id"],
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["outfit_id"],
            childColumns = ["outfit_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClothingEntity::class,
            parentColumns = ["clothing_id"],
            childColumns = ["clothing_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OutfitItemCrossRef(
    @ColumnInfo(name = "outfit_id") val outfitId: Int,
    @ColumnInfo(name = "clothing_id") val clothingId: Int
)