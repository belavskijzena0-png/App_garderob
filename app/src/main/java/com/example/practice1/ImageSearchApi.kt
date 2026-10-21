package com.example.practice1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Модель ответа от Unsplash API
data class UnsplashResponse(
    val results: List<UnsplashPhoto>?
)

data class UnsplashPhoto(
    val urls: PhotoUrls?
)

data class PhotoUrls(
    val regular: String, // Прямая ссылка на качественное фото
    val small: String
)

// 2. Интерфейс Retrofit запроса
interface ImageSearchApi {
    @GET("search/photos")
    suspend fun searchClothingImage(
        @Query("query") query: String,
        @Query("client_id") clientId: String = "DEMO_KEY", // Можно использовать бесплатный ключ Unsplash
        @Query("per_page") perPage: Int = 12
    ): UnsplashResponse
}

// 3. Синглтон для вызова сетевых запросов
object RetrofitClient {
    private const val BASE_URL = "https://api.unsplash.com/"

    val api: ImageSearchApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageSearchApi::class.java)
    }
}