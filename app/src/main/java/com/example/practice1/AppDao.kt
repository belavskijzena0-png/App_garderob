package com.example.practice1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE (email = :loginOrEmail OR login = :loginOrEmail) AND password = :password LIMIT 1")
    fun getUserByCredentials(loginOrEmail: String, password: String): UserEntity?


    // --- ОДЕЖДА ---

    @Query("SELECT * FROM clothes ORDER BY clothing_id DESC")
    fun getAllClothes(): Flow<List<ClothingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClothing(clothing: ClothingEntity)

    @Delete
    fun deleteClothing(clothing: ClothingEntity)

    // --- АУТФИТЫ ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOutfit(outfit: OutfitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOutfitItemCrossRef(crossRef: OutfitItemCrossRef)

    @Transaction
    @Query("SELECT * FROM outfits_ai ORDER BY created_at DESC")
    fun getOutfitsWithClothes(): Flow<List<OutfitWithClothes>>

    @Delete
    fun deleteOutfit(outfit: OutfitEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE is_admin = 1)")
    fun hasAdminUser(): Boolean
    @Query("DELETE FROM users WHERE user_id = :userId")
    fun deleteUserById(userId: Int)

    @Query("SELECT * FROM users WHERE is_admin = 0")
    fun getAllRegularUsers(): List<UserEntity>
    @Query("DELETE FROM users WHERE is_admin = 0")
    fun clearNonAdminUsers()

    @Query("DELETE FROM clothes")
    fun clearClothes()

    @Query("DELETE FROM outfits_ai")
    fun clearOutfits()

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE login = :login LIMIT 1)")
    fun checkLoginExists(login: String): Boolean

    // Проверка существования Email
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email LIMIT 1)")
    fun checkEmailExists(email: String): Boolean



}