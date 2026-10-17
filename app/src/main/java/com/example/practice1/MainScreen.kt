package com.example.practice1.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practice1.AddClothingScreen
import com.example.practice1.ClothingEntity
import com.example.practice1.MainViewModel

data class TabItem(val title: String, val symbol: String)

@Composable
fun MainScreen(
    onLogoutClick: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0C3FC),
            Color(0xFF8EC5FC),
            Color(0xFFFFD1DC)
        )
    )

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> EmptyScreen("ГЛАВНАЯ")
                1 -> WardrobeScreen(viewModel = viewModel)

                // Вкладка 2: Добавление вещи
                2 -> AddClothingScreen(
                    onSaveClick = { newClothing ->
                        viewModel.addClothing(
                            type = newClothing.type,
                            color = newClothing.color,
                            size = newClothing.size,
                            season = newClothing.season,
                            userPhotoUri = newClothing.imageUrl
                        )
                        selectedTab = 1
                    }
                )

                3 -> EmptyScreen("АУТФИТЫ")
                4 -> ProfileScreen(onLogoutClick = onLogoutClick)
            }
        }
    }
}

@Composable
fun WardrobeScreen(viewModel: MainViewModel) {
    val clothes by viewModel.clothes.collectAsStateWithLifecycle(initialValue = emptyList())

    com.example.practice1.WardrobeScreen(
        clothesList = clothes,
        onClothingClick = { item -> },
        onDeleteClick = { item ->
            viewModel.deleteClothing(item)
        },
        onFavoriteToggle = { item -> }
    )
}

@Composable
fun ProfileScreen(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ПРОФИЛЬ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3142)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogoutClick) {
            Text("Выйти из аккаунта")
        }
    }
}

@Composable
fun CustomBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val leftItems = listOf(
        TabItem("ГЛАВНАЯ", "\uD83C\uDFE0"),
        TabItem("ШКАФ", "\uD83D\uDDC4\uFE0F")
    )
    val rightItems = listOf(
        TabItem("АУТФИТЫ", "✨"),
        TabItem("ПРОФИЛЬ", "👤")
    )

    val targetSlot = selectedTab.toFloat()

    val animatedSlot by animateFloatAsState(
        targetValue = targetSlot,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TopWaveAnimation"
    )

    val waveHeight = 10.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp + waveHeight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val slotWidth = size.width / 5f
            val centerX = (animatedSlot + 0.5f) * slotWidth
            val waveWidth = slotWidth * 0.75f
            val wavePx = waveHeight.toPx()
            val barTop = wavePx

            val path = Path().apply {
                moveTo(0f, barTop)
                lineTo(centerX - waveWidth / 2, barTop)

                cubicTo(
                    centerX - waveWidth / 4, barTop,
                    centerX - waveWidth / 4, 0f,
                    centerX, 0f
                )
                cubicTo(
                    centerX + waveWidth / 4, 0f,
                    centerX + waveWidth / 4, barTop,
                    centerX + waveWidth / 2, barTop
                )

                lineTo(size.width, barTop)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftItems.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val color = if (isSelected) Color.Black else Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(index) }
                ) {
                    Text(
                        text = tab.symbol,
                        fontSize = 20.sp,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        color = color
                    )
                }
            }

            val isAddSelected = selectedTab == 2
            val addTextColor = if (isAddSelected) Color.Black else Color.Gray

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(2) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF5CA0F2),
                                    Color(0xFFB26BFF),
                                    Color(0xFFFF7BB0)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = "+",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ДОБАВИТЬ ВЕЩЬ",
                    fontSize = 8.sp,
                    color = addTextColor,
                    fontWeight = if (isAddSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            rightItems.forEachIndexed { index, tab ->
                val tabIndex = index + 3
                val isSelected = selectedTab == tabIndex
                val color = if (isSelected) Color.Black else Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(tabIndex) }
                ) {
                    Text(
                        text = tab.symbol,
                        fontSize = 20.sp,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3142)
        )
    }
}