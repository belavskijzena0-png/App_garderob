package com.example.practice1

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class AddCategoryItem(val name: String, val icon: ImageVector, val defaultType: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingScreen(
    onSaveClick: (ClothingEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf("Верх") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingPhoto by remember { mutableStateOf(false) }

    // Поиск фото в интернете
    var showOnlineSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var isSearchingOnline by remember { mutableStateOf(false) }

    // Камера
    var cameraTempUri by remember { mutableStateOf<Uri?>(null) }

    // Поля ввода
    var itemType by remember { mutableStateOf("Футболка") }
    var color by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var sizeDropdownExpanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val isFormValid = imageUri != null &&
            itemType.isNotBlank() &&
            color.isNotBlank() &&
            brand.isNotBlank() &&
            size.isNotBlank() &&
            material.isNotBlank()

    val sizeOptions = when (selectedCategory) {
        "Обувь" -> listOf("35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45")
        "Аксессуары" -> listOf("ONE SIZE", "S", "M", "L")
        else -> listOf("XS", "S", "M", "L", "XL", "XXL", "3XL")
    }

    // Выбор из Галереи с фоновой сегментацией
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            coroutineScope.launch {
                isLoadingPhoto = true
                val savedUri = ImageUtils.processAndSaveImage(context, sourceUri)
                imageUri = savedUri ?: sourceUri
                isLoadingPhoto = false
            }
        }
    }

    // Съёмка на Камеру с фоновой сегментацией
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraTempUri?.let { tempUri ->
                coroutineScope.launch {
                    isLoadingPhoto = true
                    val savedUri = ImageUtils.processAndSaveImage(context, tempUri)
                    imageUri = savedUri ?: tempUri
                    isLoadingPhoto = false
                }
            }
        }
    }

    fun launchCamera() {
        try {
            val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            cameraTempUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val categories = listOf(
        AddCategoryItem("Верх", Icons.Default.Checkroom, "Футболка"),
        AddCategoryItem("Низ", Icons.Default.DryCleaning, "Джинсы"),
        AddCategoryItem("Обувь", Icons.Default.RollerSkating, "Кроссовки"),
        AddCategoryItem("Аксессуары", Icons.Default.Headphones, "Сумка")
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. Шапка AURA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AURA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Wardrobe Stylist",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ДОБАВИТЬ ВЕЩЬ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Блок выбора фото вещи (Галерея, Камера, Поиск в интернете)
        Text(
            text = "ФОТО ВЕЩИ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingPhoto) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF7E22CE))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Обработка и удаление фона...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else if (imageUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Выбранное фото",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { imageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .background(Color.White, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить фото",
                                tint = Color(0xFFE11D48)
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Загрузите фото вещи",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Кнопка Галерея
                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Галерея",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Галерея", fontSize = 12.sp)
                            }

                            // Кнопка Камера
                            Button(
                                onClick = { launchCamera() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Камера",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Камера", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Кнопка "Поиск в интернете"
                        OutlinedButton(
                            onClick = {
                                searchQuery = if (color.isNotBlank()) "$color $itemType" else itemType
                                showOnlineSearchDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D9488)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF0D9488)))),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Поиск в интернете",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Найти фото в интернете", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Выбор Категории (стилизовано под шкафчик с подсветкой)
        Text(
            text = "КАТЕГОРИЯ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (selectedCategory != category.name) {
                                selectedCategory = category.name
                                itemType = category.defaultType
                                size = ""
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(
                                elevation = if (isSelected) 6.dp else 1.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = if (isSelected) Color(0xFF9333EA) else Color.Black.copy(alpha = 0.05f),
                                spotColor = if (isSelected) Color(0xFFA855F7) else Color.Black.copy(alpha = 0.05f)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFE9D5FF),
                                            Color(0xFFD8B4FE),
                                            Color(0xFFC084FC)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.85f),
                                            Color.White.copy(alpha = 0.65f)
                                        )
                                    )
                                }
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                brush = if (isSelected) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7),
                                            Color(0xFF7E22CE)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color.White.copy(alpha = 0.8f)
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            tint = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF64748B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF7E22CE) else Color(0xFF64748B),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Поля ввода характеристик ("Детали вещи")
        Text(
            text = "ДЕТАЛИ ВЕЩИ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomInputField(
            value = itemType,
            onValueChange = { itemType = it },
            placeholder = "Тип вещи (например: Футболка, Джинсы)"
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomInputField(value = color, onValueChange = { color = it }, placeholder = "Цвет")
        Spacer(modifier = Modifier.height(8.dp))
        CustomInputField(value = brand, onValueChange = { brand = it }, placeholder = "Бренд")
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = sizeDropdownExpanded,
            onExpandedChange = { sizeDropdownExpanded = !sizeDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = size,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = if (selectedCategory == "Обувь") "Размер обуви" else "Размер",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeDropdownExpanded) },
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF0F172A)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            )

            ExposedDropdownMenu(
                expanded = sizeDropdownExpanded,
                onDismissRequest = { sizeDropdownExpanded = false },
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .background(Color.White)
            ) {
                sizeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option, fontSize = 14.sp) },
                        onClick = {
                            size = option
                            sizeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        CustomInputField(value = material, onValueChange = { material = it }, placeholder = "Материал")
        Spacer(modifier = Modifier.height(8.dp))
        CustomInputField(value = notes, onValueChange = { notes = it }, placeholder = "Заметки / Сезон")

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка сохранения вещи
        Button(
            onClick = { showConfirmDialog = true },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7E22CE),
                disabledContainerColor = Color(0xFFCBD5E1)
            )
        ) {
            Text(
                text = "СОХРАНИТЬ ВЕЩЬ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFormValid) Color.White else Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Диалог поиска фото в интернете
    if (showOnlineSearchDialog) {
        AlertDialog(
            onDismissRequest = { showOnlineSearchDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Поиск фото в интернете",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    IconButton(onClick = { showOnlineSearchDialog = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть", tint = Color(0xFF64748B))
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Запрос (например: черная футболка)", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    coroutineScope.launch {
                                        isSearchingOnline = true
                                        try {
                                            val response = withContext(Dispatchers.IO) {
                                                RetrofitClient.api.searchClothingImage(searchQuery.trim())
                                            }
                                            searchResults = response.results ?: emptyList()
                                        } catch (e: Exception) {
                                            searchResults = emptyList()
                                        }
                                        isSearchingOnline = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Искать", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSearchingOnline) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF0D9488))
                        }
                    } else if (searchResults.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Введите название вещи и нажмите «Искать»", fontSize = 13.sp, color = Color(0xFF64748B))
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            items(searchResults) { photo ->
                                val imageUrl = photo.urls?.small ?: photo.urls?.regular
                                if (imageUrl != null) {
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clickable {
                                                coroutineScope.launch {
                                                    isLoadingPhoto = true
                                                    showOnlineSearchDialog = false
                                                    val savedUri = ImageUtils.downloadAndProcessWebImage(context, imageUrl)
                                                    if (savedUri != null) {
                                                        imageUri = savedUri
                                                    }
                                                    isLoadingPhoto = false
                                                }
                                            }
                                    ) {
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Интернет фото",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Подтверждение перед сохранением
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Подтверждение",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Превью вещи",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("• Категория: $selectedCategory", fontSize = 14.sp)
                        Text("• Тип: $itemType", fontSize = 14.sp)
                        Text("• Цвет: $color", fontSize = 14.sp)
                        Text("• Бренд: $brand", fontSize = 14.sp)
                        Text("• Размер: $size", fontSize = 14.sp)
                        Text("• Материал: $material", fontSize = 14.sp)
                        if (notes.isNotEmpty()) Text("• Заметки: $notes", fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        val newClothing = ClothingEntity(
                            type = itemType,
                            color = color,
                            size = size,
                            season = notes.ifEmpty { "Всесезон" },
                            imageUrl = imageUri?.toString() ?: ""
                        )
                        onSaveClick(newClothing)
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7E22CE),
                        disabledContainerColor = Color(0xFFCBD5E1)
                    )
                ) {
                    Text(
                        text = "Подтвердить",
                        color = if (isFormValid) Color.White else Color(0xFF64748B)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Редактировать", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF64748B),
                fontSize = 14.sp
            )
        },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF0F172A)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
    )
}