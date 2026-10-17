package com.example.practice1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).appDao()

    val clothes: StateFlow<List<ClothingEntity>> = dao.getAllClothes()
        .stateIn(
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
            val clothing = ClothingEntity(
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