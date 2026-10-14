package com.example.practice1

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class OutfitWithClothes(
    @Embedded val outfit: OutfitEntity,
    @Relation(
        parentColumn = "outfit_id",
        entityColumn = "clothing_id",
        associateBy = Junction(OutfitItemCrossRef::class)
    )
    val clothes: List<ClothingEntity>
)