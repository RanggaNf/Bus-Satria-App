package com.bussatriaapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.bussatriaapp.data.AuthState
import com.bussatriaapp.navigation.NavAnimation
import com.bussatriaapp.ui.screens.ChatScreen
import com.bussatriaapp.ui.screens.EditProfileScreen
import com.bussatriaapp.ui.screens.HomeScreen
import com.bussatriaapp.ui.screens.ScheduleScreen
import com.bussatriaapp.ui.screens.SettingsScreen
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.bussatriaapp.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

@Composable
fun SatriaNavigation() {
    val isDarkTheme = isSystemInDarkTheme()
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(0) }
    val shouldShowBottomBar = remember { mutableStateOf(false) }
    val shouldShowTopBar = remember { mutableStateOf(false) }
    val showScaffold = remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val context = LocalContext.current
    val viewModel: AuthViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.checkAuthStatus()
    }

    LaunchedEffect(authState) {
        startDestination = when (authState) {
            AuthState.Authenticated -> Destination.HomeScreen
            AuthState.Unauthenticated -> Destination.StartScreen
            else -> null
        }
        if (startDestination != null) {
            // Add a 2-second delay here
            delay(2000)
            isLoading = false
            // Additional delay before showing Scaffold
            delay(100)
            showScaffold.value = true
        }
    }


    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val shouldShowBars = when (destination.route) {
                Destination.StartScreen, Destination.LoginScreen, Destination.RoleScreen,
                Destination.LoginScreenDriver, Destination.ProfileScreen, Destination.RegisterScreen,
                Destination.InfoScreen, Destination.ScheduleScreen, Destination.ChatScreen,
                Destination.EditProfileScreen, Destination.SettingsScreen -> false

                else -> true
            }
            shouldShowBottomBar.value = shouldShowBars
            shouldShowTopBar.value = shouldShowBars
            selectedIndex.value = when (destination.route) {
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

    MaterialTheme {
        if (isLoading) {
            AnimatedLoadingScreen()
        } else {
            AnimatedVisibility(
                visible = showScaffold.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Scaffold(
                    topBar = {
                        AnimatedVisibility(visible = shouldShowTopBar.value) {
                            val currentBackStackEntry by navController.currentBackStackEntryAsState()
                            val isHomeScreen =
                                currentBackStackEntry?.destination?.route == Destination.HomeScreen
                            val isDepartScreen =
                                currentBackStackEntry?.destination?.route == Destination.DepartScreen
                            CustomTopBar(
                                navController,
                                isHomeScreen = isHomeScreen,
                                isDepartScreen = isDepartScreen
                            )
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(visible = shouldShowBottomBar.value) {
                            CustomsBottomBar(navController, selectedIndex)
                        }
                    },
                    backgroundColor = backgroundColor
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination!!
                        ) {
                            composable(
                                Destination.StartScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                StartScreen(navController = navController)
                            }
                            composable(
                                Destination.LoginScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                LoginScreen(navController = navController)
                            }
                            composable(
                                Destination.RegisterScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                RegisterScreen(navController = navController)
                            }
                            composable(
                                Destination.ExploreScreen,
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                ExploreScreen(navController = navController)
                            }
                            composable(
                                Destination.DepartScreen,
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                DepartScreen(navController = navController)
                            }
                            composable(
                                Destination.InfoScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                InfoScreen(
                                    navController = navController,
                                    context = LocalContext.current
                                )
                            }
                            composable(
                                Destination.HomeScreen,
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                HomeScreen(navController = navController)
                            }
                            composable(
                                Destination.ScheduleScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                ScheduleScreen(navController = navController)
                            }
                            composable(
                                Destination.ProfileScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                ProfileScreen(
                                    navController = navController,
                                    context = context,
                                    selectedIndex = selectedIndex
                                )
                            }
                            composable(
                                Destination.ChatScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                val chatViewModel: ChatViewModel = hiltViewModel()
                                ChatScreen(
                                    viewModel = chatViewModel,
                                    onImagePick = {
                                        // Implement image picking logic here
                                    },
                                    navController = navController
                                )
                            }
                            composable(
                                Destination.EditProfileScreen,
                                enterTransition = { NavAnimation.slideInFromRight() },
                                exitTransition = { NavAnimation.slideOutToLeft() },
                                popEnterTransition = { NavAnimation.slideInFromLeft() },
                                popExitTransition = { NavAnimation.slideOutToRight() }
                            ) {
                                EditProfileScreen(navController = navController, context = context)
                            }
                            composable(Destination.SettingsScreen) {
                                SettingsScreen(
                                    viewModel = hiltViewModel(),
                                    modifier = Modifier.fillMaxSize(),
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
