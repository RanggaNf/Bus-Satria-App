package com.bussatriaapp.component

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.bussatriaapp.navigation.Destination
import com.bussatriaapp.ui.theme.BusSatriaAppTheme
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.onSurface
import com.bussatriaapp.ui.viewmodel.AuthViewModel
@Composable
fun CustomTopBar(
    navController: NavHostController,
    isHomeScreen: Boolean = false,
    isDepartScreen: Boolean = false,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val context = LocalContext.current
    val userData by viewModel.userData.collectAsState()

    LaunchedEffect(userData) {
        Log.d("CustomTopBar", "UserData: $userData")
    }

    TopAppBar(
        backgroundColor = backgroundColor,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Text or Search Box based on screen flags
            when {
                isHomeScreen -> {
                    Text(
                        text = "Hallo ${
                            userData?.let {
                                it["name"] as? String
                            } ?: "Pengguna"
                        }",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                }
                isDepartScreen -> {
                    Text(
                        text = "Perjalanan Pengguna",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                }
                else -> {
                    Text(
                        text = "Jelajahi Rute",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                }
            }

            // Profile Picture or User Icon
            Box(modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate(Destination.ProfileScreen)
                        }
                )
            }
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomTopBarPreview() {
    val navController = rememberNavController()
    BusSatriaAppTheme { // Gantikan dengan tema aplikasi Anda
        CustomTopBar(navController = navController)
    }
}