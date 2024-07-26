package com.bussatriaapp.component

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBusFilled
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
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
import com.bussatriaapp.navigation.Destination
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.theme.darkTextPrimary
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.lightTextPrimary
import com.bussatriaapp.ui.theme.onSurface
import com.bussatriaapp.ui.theme.surface
import androidx.compose.material3.*
import com.bussatriaapp.ui.theme.white

@Composable
fun CustomsBottomBar(navController: NavHostController, selectedIndex: MutableState<Int>) {
    val listItems = listOf("Home", "Pergi", "Jelajahi")
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
                        1 -> navController.navigate(Destination.DepartScreen)
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
        CustomsBottomBar(navController = navController, selectedIndex = selectedIndex)
    }
}
