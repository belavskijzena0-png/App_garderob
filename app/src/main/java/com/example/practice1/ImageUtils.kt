package com.example.practice1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    // Функция обработки и сохранения фото во внутреннюю память
    fun processAndSaveImage(context: Context, imageUri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            val fileName = "clothing_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // Сохраняем во внутреннее хранилище приложения
            originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}