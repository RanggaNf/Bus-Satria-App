package com.bussatriaappdriver

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
import com.bussatriaappdriver.ui.screens.ExploreScreen
import com.bussatriaappdriver.ui.theme.BusSatriaAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bussatriaappdriver.navigation.Destination
import com.bussatriaappdriver.navigation.Destination.LoginScreenDriver
import com.bussatriaappdriver.ui.screens.DriverLoginScreen
import com.bussatriaappdriver.ui.screens.InfoScreen
import com.bussatriaappdriver.ui.screens.StartScreen
import com.bussatriaappdriver.utils.PreferenceUtil
import com.bussatriaappdriver.component.CustomTopBarDriver
import com.bussatriaappdriver.component.CustomsBottomBarDriver
import com.bussatriaappdriver.navigation.Destination.DepartDriverScreen
import com.bussatriaappdriver.ui.screens.ChatScreen
import com.bussatriaappdriver.ui.screens.DepartScreenDriver
import com.bussatriaappdriver.ui.screens.HomeScreen
import com.bussatriaappdriver.ui.screens.ProfileScreenDriver
import com.bussatriaappdriver.ui.screens.ScheduleScreen

@Composable
fun SatriaNavigationDriver() {
    val isDarkTheme = isSystemInDarkTheme()
    val navController = rememberNavController()
    val selectIndex = remember { mutableStateOf(0) }
    val shouldShowBottomBar = remember { mutableStateOf(true) }
    val shouldShowTopBar = remember { mutableStateOf(true) }
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (PreferenceUtil.isDriverLoggedIn(context)) {
            navController.navigate(Destination.HomeScreen)
        }
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val shouldShowBars = when (destination.route) {
                Destination.StartScreen,
                Destination.LoginScreenDriver,
                Destination.ProfileScreenDriver,
                Destination.ScheduleScreen,
                Destination.InfoScreen-> false
                else -> true
            }
            shouldShowBottomBar.value = shouldShowBars
            shouldShowTopBar.value = shouldShowBars
            selectIndex.value = when (destination.route) {
                Destination.HomeScreen -> 0
                Destination.DepartDriverScreen -> 1
                Destination.ExploreScreen -> 2
                else -> selectIndex.value
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                if (shouldShowTopBar.value) {
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val isHomeScreen = currentBackStackEntry?.destination?.route == Destination.HomeScreen
                    val isDepartScreen = currentBackStackEntry?.destination?.route == Destination.DepartDriverScreen
                    CustomTopBarDriver(navController, isHomeScreen = isHomeScreen, isDepartScreen = isDepartScreen)
                }
            },
            bottomBar = {
                if (shouldShowBottomBar.value) {
                    CustomsBottomBarDriver(navController, selectIndex)
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
                composable(Destination.LoginScreenDriver) {
                    DriverLoginScreen(
                        navController = navController,
                        onLoginSuccess = {
                            navController.navigate(Destination.HomeScreen) {
                                popUpTo(Destination.LoginScreenDriver) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Destination.ExploreScreen) {
                    ExploreScreen(navController = navController)
                }
                composable(Destination.InfoScreen) {
                    InfoScreen(navController = navController, context = LocalContext.current)
                }
                composable(Destination.HomeScreen) {
                    HomeScreen(navController = navController)
                }
                composable(Destination.ProfileScreenDriver) {
                    ProfileScreenDriver(navController = navController, context = context, selectedIndex = selectIndex)
                }
                composable(Destination.ScheduleScreen) {
                    ScheduleScreen(navController = navController)
                }
                composable(Destination.DepartDriverScreen) {
                    DepartScreenDriver(navController = navController)
                }
            }
        }
    }
}