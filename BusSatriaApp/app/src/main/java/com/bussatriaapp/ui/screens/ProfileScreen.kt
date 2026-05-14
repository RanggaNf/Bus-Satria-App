package com.bussatriaapp.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.theme.darkTextPrimary
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.lightTextPrimary
import com.bussatriaapp.ui.theme.onSurface
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaapp.R
import com.bussatriaapp.animate.bounceClick
import com.bussatriaapp.animate.pressClickEffect
import com.bussatriaapp.component.CustomStyleTextField
import com.bussatriaapp.component.isValidPassword
import com.bussatriaapp.navigation.Destination
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import com.bussatriaapp.ui.theme.transparantColor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.bussatriaapp.data.AuthState
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.theme.surface
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    context: Context,
    selectedIndex: MutableState<Int>
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val cardColor = if (isDarkTheme) surface else onSurface
    val userData by viewModel.userData.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val textColorBox = Color.Black
    var isCheckingAuth by remember { mutableStateOf(true) }
    val authState by viewModel.authState.collectAsState()

    systemUiController.setSystemBarsColor(color = backgroundColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                backgroundColor = backgroundColor,
                elevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    ProfileHeader(
                        name = userData?.get("name") as? String ?: "Loading...",
                        role = userData?.get("role") as? String ?: "Loading...",
                        email = userData?.get("email") as? String ?: "Loading...",
                        textColor = textColor,
                        iconColor = iconColor,
                        context = context
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    ProfileInfoCard(userData, textColor, iconColor, cardColor)
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    ProfileOptionsCard(
                        navController = navController,
                        textColor = textColor,
                        iconColor = iconColor,
                        cardColor = cardColor
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                item {
                    LogoutButton(
                        onClick = { showLogoutDialog = true },
                        backgroundColor = buttonColor,
                        textColor = backgroundColor
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                viewModel.logout(context)
                showLogoutDialog = false
                navController.navigate(Destination.LoginScreen) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                selectedIndex.value = 0
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    LaunchedEffect(viewModel.authState) {
        val authState = viewModel.authState.value
        Log.d("ProfileScreen", "AuthState: $authState")
        if (authState == null) {
            Log.d("ProfileScreen", "Navigating to StartScreen due to null authState")
            navController.navigate(Destination.StartScreen) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAuthStatus()
    }
    when (authState) {
        AuthState.Initial, AuthState.Loading -> {
            CircularProgressIndicator()
        }
        AuthState.Authenticated -> {
            // Your existing ProfileScreen content
            viewModel.fetchUserData()
        }
        AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                Log.d("ProfileScreen", "Navigating to StartScreen due to Unauthenticated state")
                navController.navigate(Destination.StartScreen) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, role: String, email: String, textColor: Color, iconColor: Color, context: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, iconColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PermIdentity,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = iconColor
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(iconColor)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable {
                        Toast.makeText(context, "Fitur belum dikonfigurasi", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change profile picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = name,
            color = textColor,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
        )
        Text(
            text = role,
            color = textColor.copy(alpha = 0.7f),
            style = TextStyle(fontSize = 16.sp)
        )
        Text(
            text = email,
            color = textColor.copy(alpha = 0.7f),
            style = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
fun ProfileInfoCard(userData: Map<String, Any>?, textColor: Color, iconColor: Color, cardColor: Color) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColorBox = Color.Black
    Card(
        backgroundColor = cardColor,
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoSection("Nama", userData?.get("name") as? String ?: "Loading...", Icons.Default.Person, textColorBox, iconColor)
            ProfileInfoSection("Peran", userData?.get("role") as? String ?: "Loading...", Icons.Default.Groups, textColorBox, iconColor)
            ProfileInfoSection("Email", userData?.get("email") as? String ?: "Loading...", Icons.Default.Email, textColorBox, iconColor)
        }
    }
}

@Composable
fun ProfileOptionsCard(navController: NavController, textColor: Color, iconColor: Color, cardColor: Color) {
    val textColorBox = Color.Black
    Card(
        backgroundColor = cardColor,
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            ProfileOptionItem("Edit Profil", Icons.Default.Edit, textColorBox, iconColor, onClick = {
                navController.navigate(Destination.EditProfileScreen)
            })
        }
    }
}
@Composable
fun ProfileInfoSection(title: String, info: String, icon: ImageVector, textColor: Color, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, color = textColor, style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = info, color = textColor)
        }
    }
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector, textColor: Color, iconColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = textColor)
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) lightTextPrimary else darkTextPrimary
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val borderColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val backgroundBottomColor = if (isDarkTheme) gray else onSurface

    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = {
            Text(
                text = "Konfirmasi Logout",
                color = textColor
            )
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin logout?",
                color = textColor
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirmLogout.invoke() },
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Text(text = "Ya", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss.invoke() },
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Text(text = "Batal", color = Color.White)
            }
        },
        backgroundColor = backgroundBottomColor
    )
}
@Composable
fun LogoutButton(onClick: () -> Unit, backgroundColor: Color, textColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .pressClickEffect(),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(percent = 20),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(8.dp)
    ) {
        Text(
            text = "Logout",
            color = textColor,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        )
    }
}
@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScreenPreviewProfile() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(0) }
    ProfileScreen(
        navController = navController,
        viewModel = hiltViewModel(), // Or provide a mock ViewModel for the preview
        context = LocalContext.current,
        selectedIndex = selectedIndex
    )
}