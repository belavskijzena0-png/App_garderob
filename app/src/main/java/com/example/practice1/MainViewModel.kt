package com.example.practice1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).appDao()
    private val userPreferences = UserPreferences(application)

    val clothes: StateFlow<List<ClothingEntity>> = flow {
        val currentUserId = userPreferences.getUserId()
        val clothesFlow = if (currentUserId != -1) {
            dao.getClothesByUserId(currentUserId)
        } else {
            dao.getAllClothes()
        }
        emitAll(clothesFlow)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addClothing(
        type: String,
        color: String,
        size: String,
        season: String,
        userPhotoUri: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = userPreferences.getUserId()
            val clothing = ClothingEntity(
                userId = if (currentUserId != -1) currentUserId else null,
                type = type,
                size = size,
                color = color,
                season = season,
                imageUrl = userPhotoUri ?: ""
            )
            dao.insertClothing(clothing)
        }
    }

    // Вызов метода удаления из AppDao
    fun deleteClothing(clothing: ClothingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteClothing(clothing)
        }
    }
}