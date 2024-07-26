package com.bussatriaappdriver.component

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBusFilled
import androidx.compose.material.icons.filled.EmojiTransportation
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussatriaappdriver.navigation.Destination
import com.bussatriaappdriver.ui.theme.DarkPrimaryPurple
import com.bussatriaappdriver.ui.theme.LightPrimaryPurple
import com.bussatriaappdriver.ui.theme.darkTextPrimary
import com.bussatriaappdriver.ui.theme.gray
import com.bussatriaappdriver.ui.theme.lightTextPrimary
import com.bussatriaappdriver.ui.theme.onSurface
import com.bussatriaappdriver.ui.theme.surface
import com.bussatriaappdriver.ui.theme.white

@Composable
fun CustomsBottomBarDriver(navController: NavHostController, selectedIndex: MutableState<Int>) {
    val listItems = listOf("Home", "Pergi", "Jelajah")
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) lightTextPrimary else darkTextPrimary
    val selectedColor = if (isDarkTheme) white else LightPrimaryPurple

    NavigationBar(
        containerColor = backgroundColor,
        contentColor = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        listItems.forEachIndexed { index, label ->
            val isSelected = selectedIndex.value == index
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (index) {
                            0 -> if (isSelected) Icons.Filled.Home else Icons.Default.Home
                            1 -> if (isSelected) Icons.Filled.Directions else Icons.Default.Directions
                            2 -> if (isSelected) Icons.Filled.Explore else Icons.Default.Explore
                            else -> Icons.Default.Error
                        },
                        contentDescription = label,
                        tint = if (isSelected) selectedColor else Color.Gray
                    )
                },
                selected = isSelected,
                label = {
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) selectedColor else textColor,
                        fontSize = 12.sp
                    )
                },
                onClick = {
                    selectedIndex.value = index
                    when (index) {
                        0 -> navController.navigate(Destination.HomeScreen)
                        1 -> navController.navigate(Destination.DepartDriverScreen)
                        2 -> navController.navigate(Destination.ExploreScreen)
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun CustomsBottomBarPreview() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(0) }

    Surface {
        CustomsBottomBarDriver(navController = navController, selectedIndex = selectedIndex)
    }
}
