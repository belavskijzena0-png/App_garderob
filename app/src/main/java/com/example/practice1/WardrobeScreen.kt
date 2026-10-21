package com.example.practice1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

data class CategoryItem(val name: String, val icon: ImageVector)

@Composable
fun WardrobeScreen(
    clothesList: List<ClothingEntity>,
    onClothingClick: (ClothingEntity) -> Unit = {},
    onDeleteClick: (ClothingEntity) -> Unit = {},
    onFavoriteToggle: (ClothingEntity) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Всё") }
    var selectedClothingForDetail by remember { mutableStateOf<ClothingEntity?>(null) }
    val favoriteIds = remember { mutableStateListOf<Int>() }

    val categories = listOf(
        CategoryItem("Всё", Icons.Default.GridView),
        CategoryItem("Верх", Icons.Default.Checkroom),
        CategoryItem("Низ", Icons.Default.DryCleaning),
        CategoryItem("Обувь", Icons.Default.RollerSkating),
        CategoryItem("Аксессуары", Icons.Default.Headphones),
        CategoryItem("Избранное", Icons.Default.Favorite)
    )

    val filteredList = when (selectedCategory) {
        "Верх" -> clothesList.filter { clothing ->
            listOf("Футболка", "Рубашка", "Худи", "Свитшот", "Свитер", "Куртка", "Пальто", "Пиджак", "Топ")
                .any { clothing.type.contains(it, ignoreCase = true) }
        }
        "Низ" -> clothesList.filter { clothing ->
            listOf("Джинсы", "Брюки", "Шорты", "Юбка", "Штаны", "Джоггеры")
                .any { clothing.type.contains(it, ignoreCase = true) }
        }
        "Обувь" -> clothesList.filter { clothing ->
            listOf("Кроссовки", "Кеды", "Ботинки", "Туфли", "Сапоги", "Сандалии", "Слипоны")
                .any { clothing.type.contains(it, ignoreCase = true) }
        }
        "Аксессуары" -> clothesList.filter { clothing ->
            listOf("Сумка", "Рюкзак", "Кепка", "Шапка", "Ремень", "Очки", "Шарф")
                .any { clothing.type.contains(it, ignoreCase = true) }
        }
        "Избранное" -> clothesList.filter { clothing ->
            favoriteIds.contains(clothing.clothingId)
        }
        else -> clothesList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. Шапка AURA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
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

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Фильтры */ }) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Фильтр",
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Поиск */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Заголовок "МОЙ ШКАФ"
        Text(
            text = "МОЙ ШКАФ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Вкладки категорий (строго одинаковые отступы между всеми иконками)
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
                        .clickable { selectedCategory = category.name }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(
                                elevation = if (isSelected) 6.dp else 1.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = if (isSelected) Color(0xFF9333EA) else Color.Black.copy(alpha = 0.05f),
                                spotColor = if (isSelected) Color(0xFFA855F7) else Color.Black.copy(alpha = 0.05f)
                            )
                            .clip(RoundedCornerShape(14.dp))
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
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            tint = if (isSelected) {
                                if (category.name == "Избранное") Color(0xFFE11D48) else Color(0xFF1D4ED8)
                            } else {
                                Color(0xFF64748B)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF7E22CE) else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Сетка карточек товаров
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedCategory == "Избранное") "В избранном пока нет вещей" else "В этой категории пока нет вещей",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList) { clothing ->
                    val isFavorite = favoriteIds.contains(clothing.clothingId)

                    ClothingGridCard(
                        clothing = clothing,
                        isFavorite = isFavorite,
                        onClick = {
                            selectedClothingForDetail = clothing
                            onClothingClick(clothing)
                        },
                        onDeleteClick = { onDeleteClick(clothing) },
                        onFavoriteToggle = {
                            if (isFavorite) {
                                favoriteIds.remove(clothing.clothingId)
                            } else {
                                favoriteIds.add(clothing.clothingId)
                            }
                            onFavoriteToggle(clothing)
                        }
                    )
                }
            }
        }
    }

    // Модальное окно деталей карточки при клике
    selectedClothingForDetail?.let { clothing ->
        val isFav = favoriteIds.contains(clothing.clothingId)
        ClothingDetailDialog(
            clothing = clothing,
            isFavorite = isFav,
            onDismiss = { selectedClothingForDetail = null },
            onDeleteClick = {
                onDeleteClick(clothing)
                selectedClothingForDetail = null
            },
            onFavoriteToggle = {
                if (isFav) {
                    favoriteIds.remove(clothing.clothingId)
                } else {
                    favoriteIds.add(clothing.clothingId)
                }
                onFavoriteToggle(clothing)
            }
        )
    }
}

@Composable
fun ClothingGridCard(
    clothing: ClothingEntity,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF8FAFC))
            ) {
                AsyncImage(
                    model = clothing.imageUrl,
                    contentDescription = clothing.type,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.Checkroom),
                    placeholder = rememberVectorPainter(Icons.Default.Checkroom)
                )

                // Кнопка «Избранное»
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .shadow(2.dp, CircleShape)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${clothing.color} ${clothing.type}".trim(),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${clothing.season}, ${clothing.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun ClothingDetailDialog(
    clothing: ClothingEntity,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Карточка вещи",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Большое изображение вещи
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8FAFC))
                ) {
                    AsyncImage(
                        model = clothing.imageUrl,
                        contentDescription = clothing.type,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.Checkroom),
                        placeholder = rememberVectorPainter(Icons.Default.Checkroom)
                    )

                    // Избранное на фото
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .shadow(2.dp, CircleShape)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onFavoriteToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${clothing.color} ${clothing.type}".trim(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ХАРАКТЕРИСТИКИ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Карточки характеристик
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailChip(
                            label = "Тип",
                            value = clothing.type,
                            icon = Icons.Default.Checkroom,
                            modifier = Modifier.weight(1f)
                        )
                        DetailChip(
                            label = "Цвет",
                            value = clothing.color,
                            icon = Icons.Default.Palette,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailChip(
                            label = "Размер",
                            value = clothing.size,
                            icon = Icons.Default.Straighten,
                            modifier = Modifier.weight(1f)
                        )
                        DetailChip(
                            label = "Сезон",
                            value = clothing.season,
                            icon = Icons.Default.WbSunny,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDeleteClick()
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE11D48)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Удалить")
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7E22CE)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Закрыть", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun DetailChip(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF7E22CE),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobeScreenPreview() {
    val sampleClothes = listOf(
        ClothingEntity(clothingId = 1, type = "Футболка", size = "M", color = "черный", season = "Всесезон", imageUrl = ""),
        ClothingEntity(clothingId = 2, type = "Худи", size = "L", color = "белый", season = "Зима", imageUrl = "")
    )
    Box(modifier = Modifier.background(Color(0xFFE0E7FF))) {
        WardrobeScreen(clothesList = sampleClothes)
    }
}