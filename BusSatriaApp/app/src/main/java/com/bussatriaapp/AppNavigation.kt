package com.bussatriaapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bussatriaapp.ui.screens.ExploreScreen
import com.bussatriaapp.ui.theme.BusSatriaAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bussatriaapp.navigation.Destination
import com.bussatriaapp.navigation.Destination.LoginScreenDriver
import com.bussatriaapp.ui.screens.DepartScreen
import com.bussatriaapp.ui.screens.InfoScreen
import com.bussatriaapp.ui.screens.LoginScreen
import com.bussatriaapp.ui.screens.ProfileScreen
import com.bussatriaapp.ui.screens.RegisterScreen
import com.bussatriaapp.ui.screens.StartScreen
import com.bussatriaapp.utils.PreferenceUtil
import com.bussatriaapp.component.CustomTopBar
import com.bussatriaapp.component.CustomsBottomBar
import com.bussatriaapp.ui.screens.ChatScreen
import com.bussatriaapp.ui.screens.HomeScreen
import com.bussatriaapp.ui.screens.ScheduleScreen
import com.bussatriaapp.ui.viewmodel.ChatViewModel

@Composable
fun SatriaNavigation() {
    val isDarkTheme = isSystemInDarkTheme()
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(0) }
    val shouldShowBottomBar = remember { mutableStateOf(true) }
    val shouldShowTopBar = remember { mutableStateOf(true) }
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val context = LocalContext.current

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
            val shouldShowBars = when (destination?.route) {
                Destination.StartScreen,
                Destination.LoginScreen,
                Destination.RoleScreen,
                Destination.LoginScreenDriver,
                Destination.ProfileScreen,
                Destination.RegisterScreen,
                Destination.InfoScreen,
                Destination.ScheduleScreen,
                Destination.ChatScreen-> false
                else -> true
            }
            shouldShowBottomBar.value = shouldShowBars
            shouldShowTopBar.value = shouldShowBars
            selectedIndex.value = when (destination?.route) {
                Destination.HomeScreen -> 0
                Destination.DepartScreen -> 1
                Destination.ExploreScreen -> 2
                else -> selectedIndex.value
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    val isLoggedIn = PreferenceUtil.isLoggedIn(context)
    if (isLoggedIn) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate(Destination.HomeScreen)
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                if (shouldShowTopBar.value) {
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val isHomeScreen = currentBackStackEntry?.destination?.route == Destination.HomeScreen
                    val isDepartScreen = currentBackStackEntry?.destination?.route == Destination.DepartScreen
                    CustomTopBar(navController, isHomeScreen = isHomeScreen, isDepartScreen = isDepartScreen)
                }
            },
            bottomBar = {
                if (shouldShowBottomBar.value) {
                    CustomsBottomBar(navController, selectedIndex)
                }
            },
            backgroundColor = backgroundColor
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Destination.StartScreen,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destination.StartScreen) {
                    StartScreen(navController = navController)
                }
                composable(Destination.LoginScreen) {
                    LoginScreen(navController = navController)
                }
                composable(Destination.RegisterScreen) {
                    RegisterScreen(navController = navController)
                }
                composable(Destination.ExploreScreen) {
                    ExploreScreen(navController = navController)
                }
                composable(Destination.DepartScreen) {
                    DepartScreen(navController = navController)
                }
                composable(Destination.InfoScreen) {
                    InfoScreen(navController = navController, context = LocalContext.current)
                }
                composable(Destination.HomeScreen) {
                    HomeScreen(navController = navController)
                }
                composable(Destination.ScheduleScreen) {
                    ScheduleScreen(navController = navController)
                }
                composable(Destination.ProfileScreen) {
                    ProfileScreen(navController = navController, context = context, selectedIndex = selectedIndex)
                }
                composable(Destination.ChatScreen) {
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    ChatScreen(
                        viewModel = chatViewModel,
                        onImagePick = {
                            // Implement image picking logic here
                            // For example, you could use an ActivityResultLauncher
                        } )
                }
            }
        }
    }
}
