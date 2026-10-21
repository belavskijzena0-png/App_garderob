package com.example.practice1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object ImageUtils {

    // Фоновая обработка изображения: сегментация объекта и установка единого фона #F8FAFC
    suspend fun processAndSaveImage(context: Context, imageUri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return@withContext null
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) return@withContext null

            // Исправление ориентации из EXIF (для фото с камеры)
            bitmap = fixImageOrientation(context, imageUri, bitmap)

            // Масштабирование до 1080px для оптимизации
            bitmap = scaleBitmapIfNeeded(bitmap, 1080)

            // Сегментация и удаление пестрого фона
            val processedBitmap = removeBackgroundAndSetUniform(bitmap)

            saveBitmapToFile(context, processedBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Загрузка и сегментация фотографии по URL из сети
    suspend fun downloadAndProcessWebImage(context: Context, urlString: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val inputStream = connection.getInputStream()
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) return@withContext null

            bitmap = scaleBitmapIfNeeded(bitmap, 1080)
            val processedBitmap = removeBackgroundAndSetUniform(bitmap)

            saveBitmapToFile(context, processedBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Извлечение объекта (одежды) с помощью MLKit Subject Segmentation
    private fun removeBackgroundAndSetUniform(originalBitmap: Bitmap): Bitmap {
        return try {
            val options = SubjectSegmenterOptions.Builder()
                .enableForegroundBitmap()
                .build()
            val segmenter = SubjectSegmentation.getClient(options)
            val inputImage = InputImage.fromBitmap(originalBitmap, 0)

            val task = segmenter.process(inputImage)
            val result = Tasks.await(task) // Выполняется асинхронно на Dispatchers.IO

            val foreground = result.foregroundBitmap
            if (foreground != null) {
                // Создаем холст с единым чистым фоном #F8FAFC
                val targetBitmap = Bitmap.createBitmap(
                    originalBitmap.width,
                    originalBitmap.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(targetBitmap)
                canvas.drawColor(Color.parseColor("#F8FAFC")) // Единый светлый фон
                canvas.drawBitmap(foreground, 0f, 0f, null)
                targetBitmap
            } else {
                createUniformBackgroundBitmap(originalBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            createUniformBackgroundBitmap(originalBitmap)
        }
    }

    private fun createUniformBackgroundBitmap(bitmap: Bitmap): Bitmap {
        val targetBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(targetBitmap)
        canvas.drawColor(Color.parseColor("#F8FAFC"))
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return targetBitmap
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val fileName = "clothing_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun fixImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(inputStream)
            } else {
                null
            }
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }
}