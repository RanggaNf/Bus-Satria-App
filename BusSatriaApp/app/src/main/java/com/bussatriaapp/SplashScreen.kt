package com.bussatriaapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bussatriaapp.navigation.Destination
import kotlinx.coroutines.delay
import com.bussatriaapp.ui.theme.BusSatriaAppTheme


@Composable
fun SplashScreen(navController: NavHostController) {
    val isVisible = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000) // Tunda selama 3 detik sebelum menavigasi
        isVisible.value = false
        navController.navigate(Destination.StartScreen) {
            popUpTo(Destination.SplashScreen) { inclusive = true }
        }
    }

    AnimatedVisibility(visible = isVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF800080)), // Warna ungu
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.busmerah),
                    contentDescription = "Logo Bus Merah",
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = "SATRIA",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
