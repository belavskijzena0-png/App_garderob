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
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE (email = :loginOrEmail OR login = :loginOrEmail) AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(loginOrEmail: String, password: String): UserEntity?

    // --- ОДЕЖДА ---

    @Query("SELECT * FROM clothes ORDER BY clothing_id DESC")
    fun getAllClothes(): Flow<List<ClothingEntity>>

    @Query("SELECT * FROM clothes WHERE user_id = :userId ORDER BY clothing_id DESC")
    fun getClothesByUserId(userId: Int): Flow<List<ClothingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(clothing: ClothingEntity)

    @Delete
    suspend fun deleteClothing(clothing: ClothingEntity)

    // --- АУТФИТЫ ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitItemCrossRef(crossRef: OutfitItemCrossRef)

    @Transaction
    @Query("SELECT * FROM outfits_ai ORDER BY created_at DESC")
    fun getOutfitsWithClothes(): Flow<List<OutfitWithClothes>>

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE is_admin = 1)")
    suspend fun hasAdminUser(): Boolean

    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteUserById(userId: Int): Int

    @Query("SELECT * FROM users WHERE is_admin = 0")
    suspend fun getAllRegularUsers(): List<UserEntity>

    @Query("DELETE FROM users WHERE is_admin = 0")
    suspend fun clearNonAdminUsers(): Int

    @Query("DELETE FROM clothes")
    suspend fun clearClothes(): Int

    @Query("DELETE FROM outfits_ai")
    suspend fun clearOutfits(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE login = :login LIMIT 1)")
    suspend fun checkLoginExists(login: String): Boolean

    // Проверка существования Email
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email LIMIT 1)")
    suspend fun checkEmailExists(email: String): Boolean
}