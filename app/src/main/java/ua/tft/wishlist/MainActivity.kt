package ua.tft.wishlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.tft.wishlist.data.WishDbHelper
import ua.tft.wishlist.model.Holidays
import ua.tft.wishlist.model.WishItem
import ua.tft.wishlist.ui.AddEditWishSheet
import ua.tft.wishlist.ui.WishCard
import ua.tft.wishlist.ui.WishColors
import ua.tft.wishlist.util.formatPrice

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                WishListScreen()
            }
        }
    }
}

@Composable
fun WishListScreen() {
    val context = LocalContext.current
    val dbHelper = remember { WishDbHelper(context) }

    var wishes by remember { mutableStateOf(dbHelper.getAllWishes()) }
    var selectedFilter by remember { mutableStateOf("Всі") }
    var showSheet by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<WishItem?>(null) }

    val filteredWishes = remember(wishes, selectedFilter) {
        if (selectedFilter == "Всі") wishes
        else wishes.filter { it.holiday == selectedFilter }
    }

    val totalBudget = remember(filteredWishes) {
        filteredWishes.filter { !it.isFulfilled }.sumOf { it.price }
    }
    val fulfilledCount = remember(filteredWishes) {
        filteredWishes.count { it.isFulfilled }
    }

    fun refreshList() {
        wishes = dbHelper.getAllWishes()
    }

    Scaffold(
        containerColor = WishColors.Background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingItem = null
                    showSheet = true
                },
                containerColor = WishColors.GoldAccent,
                contentColor = WishColors.Background,
                shape = RoundedCornerShape(18.dp),
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Додати", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Шапка з бюджетом (без градієнтів, з прогрес-баром)
            val progressFraction = if (filteredWishes.isEmpty()) 0f
            else fulfilledCount.toFloat() / filteredWishes.size.toFloat()

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                color = WishColors.SurfaceCard,
                border = BorderStroke(1.5.dp, WishColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WishList",
                                color = WishColors.TextPrimary,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Збулося $fulfilledCount із ${filteredWishes.size}",
                                color = WishColors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "БЮДЖЕТ",
                                color = WishColors.TextSecondary,
                                fontSize = 10.sp,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatPrice(totalBudget),
                                color = WishColors.GoldAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Прогрес-бар виконання бажань
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(WishColors.Background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(WishColors.SuccessGreen)
                        )
                    }
                }
            }

            // Горизонтальний фільтр по святах
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Всі") + Holidays.list
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) WishColors.GoldAccent else WishColors.SurfaceCard,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) WishColors.GoldAccent else WishColors.BorderSubtle
                        )
                    ) {
                        val label = if (filter == "Всі") "Всі"
                        else "${Holidays.emojiFor(filter)} $filter"
                        Text(
                            text = label,
                            color = if (isSelected) WishColors.Background else WishColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Список бажань або порожній екран
            if (filteredWishes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.CardGiftcard,
                            contentDescription = null,
                            tint = WishColors.GoldAccent.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Список поки порожній",
                            color = WishColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Натисни «+ Додати», щоб записати перше бажання",
                            color = WishColors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 6.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredWishes, key = { it.id }) { wish ->
                        WishCard(
                            item = wish,
                            onClick = {
                                editingItem = wish
                                showSheet = true
                            },
                            onToggleFulfilled = {
                                dbHelper.toggleFulfilled(wish.id, !wish.isFulfilled)
                                refreshList()
                            },
                            onDelete = {
                                dbHelper.deleteWish(wish.id)
                                refreshList()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        AddEditWishSheet(
            editingItem = editingItem,
            onDismiss = { showSheet = false },
            onSave = { item ->
                if (item.id == 0L) {
                    dbHelper.insertWish(item)
                } else {
                    dbHelper.updateWish(item)
                }
                refreshList()
                showSheet = false
            }
        )
    }
}