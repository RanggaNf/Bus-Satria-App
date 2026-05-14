package com.bussatriaappdriver.ui.screens


import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaappdriver.R
import com.bussatriaappdriver.animate.pressClickEffect
import com.bussatriaappdriver.component.PageIndicator
import com.bussatriaappdriver.navigation.Destination
import com.bussatriaappdriver.ui.theme.BusSatriaAppTheme
import com.bussatriaappdriver.ui.theme.DarkPrimaryPurple
import com.bussatriaappdriver.ui.theme.LightPrimaryPurple
import com.bussatriaappdriver.ui.viewmodel.LocationDriverViewModel
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPagerIndicator

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LocationDriverViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)
    val todayStats by viewModel.todayStats.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    LaunchedEffect(key1 = true) {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = !isDarkTheme
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                // Sliding Banner
                SlidingBanner()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Quick Actions
                Text(
                    text = "Aksi Cepat",
                    style = MaterialTheme.typography.h6,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.DirectionsBus,
                        text = "Halte",
                        color = Color(0xFF4CAF50),
                        onClick = { navController.navigate(Destination.InfoScreen) }
                    )

                    QuickActionCard(
                        icon = Icons.Default.Schedule,
                        text = "Jadwal",
                        color = Color(0xFFFFA000),
                        onClick = { navController.navigate(Destination.ScheduleScreen) }
                    )

                    QuickActionCard(
                        icon = Icons.Default.Message,
                        text = "Obrolan",
                        color = Color(0xFF2196F3),
                        onClick = { navController.navigate(Destination.ChatScreen) }
                    )
                    QuickActionCard(
                        icon = Icons.Default.Settings,
                        text = "Settings",
                        color = Color(0xFF9C27B0),
                        onClick = {navController.navigate(Destination.SettingsScreen)  }
                    )
                    QuickActionCard(
                        icon = Icons.Default.DriveEta,
                        text = "Driver",
                        color = Color.LightGray,
                        onClick = {navController.navigate(Destination.ProfileScreenDriver)  }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Statistics
                Text(
                    text = "Statistik Hari Ini",
                    style = MaterialTheme.typography.h6,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticCard(
                        title = "Berangkat",
                        value = todayStats.trips.toString(),
                        color = cardColor,
                        textColor = textColor,
                        iconColor = Color(0xFF4CAF50)
                    )
                    StatisticCard(
                        title = "Penumpang",
                        value = todayStats.passengers.toString(),
                        color = cardColor,
                        textColor = textColor,
                        iconColor = Color(0xFF2196F3)
                    )
                    StatisticCard(
                        title = "Waktu",
                        value = currentTime,
                        color = cardColor,
                        textColor = textColor,
                        iconColor = Color(0xFFFFA000)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Recent Activity
                Text(
                    text = "Aktivitas Terakhir",
                    style = MaterialTheme.typography.h6,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (todayStats.activities.isEmpty()) {
                item {
                    NoActivityCard(textColor = textColor, cardColor = cardColor)
                }
            } else{
                items(todayStats.activities.sortedByDescending { it.time }.take(10)) { activity ->
                    ActivityItem(
                        title = activity.title,
                        description = "${activity.description}", // Bahasa Indonesia
                        time = activity.time,
                        color = cardColor,
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityItem(title: String, description: String, time: String, color: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        backgroundColor = color,
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
            Text(
                text = time,
                style = MaterialTheme.typography.caption,
                color = textColor.copy(alpha = 0.5f)
            )
        }
    }
}
@Composable
fun NoActivityCard(textColor: Color, cardColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        backgroundColor = cardColor,
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada aktivitas terbaru",
                style = MaterialTheme.typography.body1,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingBanner() {
    val bannerImages = listOf(
        R.drawable.kediri2,
        R.drawable.kediri1,
        R.drawable.kediri3
    )

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)) // Rounded corner shape added here
            .background(Color.White) // Background color if needed
    ) {
        HorizontalPager(
            count = bannerImages.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = bannerImages[page]),
                contentDescription = "Banner Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}

@Composable
fun QuickActionCard(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(70.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        backgroundColor = color.copy(alpha = 0.1f),
        elevation = 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.caption,
                color = color,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String, color: Color, textColor: Color, iconColor: Color) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        backgroundColor = color,
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (title) {
                    "Berangkat" -> Icons.Default.DirectionsBus
                    "Penumpang" -> Icons.Default.Person
                    else -> Icons.Default.Schedule
                },
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.h5,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.caption,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}



@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenPreview() {
    BusSatriaAppTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}