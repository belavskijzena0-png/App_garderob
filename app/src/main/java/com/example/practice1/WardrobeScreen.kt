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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Заголовок "МОЙ ШКАФ"
        Text(
            text = "МОЙ ШКАФ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Панель категорий
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedCategory = category.name }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Color(0xFFBDDCFF) else Color(0xFFF1F5F9).copy(alpha = 0.8f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            tint = if (isSelected) {
                                if (category.name == "Избранное") Color(0xFFE11D48) else Color(0xFF0284C7)
                            } else {
                                Color(0xFF64748B)
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Сетка карточек
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
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList) { clothing ->
                    val isFavorite = favoriteIds.contains(clothing.clothingId)

                    ClothingGridCard(
                        clothing = clothing,
                        isFavorite = isFavorite,
                        onClick = { onClothingClick(clothing) },
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
}

@Composable
fun ClothingGridCard(
    clothing: ClothingEntity,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = clothing.imageUrl,
                contentDescription = clothing.type,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.Checkroom),
                placeholder = rememberVectorPainter(Icons.Default.Checkroom)
            )

            // Крестик удаления
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить вещь",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Избранное
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .clickable { onFavoriteToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Избранное",
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${clothing.color} ${clothing.type}".trim(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${clothing.season}, ${clothing.size}",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}