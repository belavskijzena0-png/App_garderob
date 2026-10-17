package com.example.practice1

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class AddCategoryItem(val name: String, val icon: ImageVector, val defaultType: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingScreen(
    onSaveClick: (ClothingEntity) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("Верх") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val savedUri = ImageUtils.processAndSaveImage(context, sourceUri)
            imageUri = savedUri ?: sourceUri
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ДОБАВИТЬ ВЕЩЬ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.5f))
                .drawDottedBorder(
                    strokeWidth = 2.dp,
                    color = if (imageUri != null) Color(0xFF0284C7) else Color(0xFF94A3B8),
                    cornerRadius = 20.dp
                )
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Выбранное фото",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Камера",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Сделать фото или\nвыбрать из галереи",
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "КАТЕГОРИЯ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (selectedCategory != category.name) {
                            selectedCategory = category.name
                            itemType = category.defaultType
                            size = ""
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp, 56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color(0xFFBDDCFF) else Color.White
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            tint = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ДЕТАЛИ ВЕЩИ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomInputField(
            value = itemType,
            onValueChange = { itemType = it },
            placeholder = "Тип вещи"
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
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                ),
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
        CustomInputField(value = notes, onValueChange = { notes = it }, placeholder = "Заметки / Теги")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { showConfirmDialog = true },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
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
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f)),
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
                        containerColor = Color(0xFF3B82F6),
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
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color(0xFF0F172A)
        ),
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

fun Modifier.drawDottedBorder(
    strokeWidth: Dp = 2.dp,
    color: Color = Color(0xFF94A3B8),
    cornerRadius: Dp = 20.dp
): Modifier = this.drawWithContent {
    drawContent()
    val strokePx = strokeWidth.toPx()
    val radiusPx = cornerRadius.toPx()
    drawRoundRect(
        color = color,
        style = Stroke(
            width = strokePx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
        ),
        cornerRadius = CornerRadius(radiusPx, radiusPx)
    )
}