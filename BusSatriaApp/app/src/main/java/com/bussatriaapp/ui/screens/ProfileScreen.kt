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
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
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
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.theme.surface

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
    val borderColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) abuMuda else abuTua
    val cardColor = if (isDarkTheme) surface else onSurface
    val text2Color = if (isDarkTheme)  Color.Black else Color.Black
    // State for showing logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    systemUiController.setSystemBarsColor(color = backgroundColor)

    // Provide the context using CompositionLocalProvider
    CompositionLocalProvider(LocalContext provides context) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(text = "Profile", color = textColor) },
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
                    modifier = Modifier.statusBarsPadding() // Add this line
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.size(150.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
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
                                .size(50.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(buttonColor)
                                .border(2.dp, backgroundColor, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change profile picture",
                                tint = backgroundColor,
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    ProfileInfoSection("Nama", "Mohammad Rangga Nur Faizin", Icons.Default.Person, textColor, iconColor)
                    ProfileInfoSection("Peran", "Mahasiswa", Icons.Default.Groups, textColor, iconColor)
                    ProfileInfoSection("Email", "mohammad.rangga.n.f@gmail.com", Icons.Default.Email, textColor, iconColor)

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        backgroundColor = cardColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            ProfileOptionItem("Edit Profil", Icons.Default.Edit, text2Color, iconColor, onClick = {
                                // Handle Edit Profile Click
                            })
                            ProfileOptionItem("Butuh Bantuan", Icons.Default.HelpCenter, text2Color, iconColor, onClick = {
                                // Handle Report List Click
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            showLogoutDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .pressClickEffect(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                        shape = RoundedCornerShape(percent = 20),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp
                        ),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Text(
                            text = "Logout",
                            color = backgroundColor,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                // Logout action
                viewModel.logout(context)
                // Close the dialog
                showLogoutDialog = false
                // Navigate to LoginScreen after logout
                navController.navigate(Destination.LoginScreen) {
                    // Pop up to the start destination of the graph to
                    // remove all back stack entries
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
                // Reset selectedIndex to Home
                selectedIndex.value = 0
            },
            onDismiss = {
                // Dismiss the dialog if user clicks Cancel
                showLogoutDialog = false
            }
        )
    }

    // Deklarasi LaunchedEffect untuk melakukan navigasi saat nilai authState berubah
    LaunchedEffect(viewModel.authState) {
        val authState = viewModel.authState.value
        if (authState == null) {
            navController.navigate(Destination.StartScreen) {
                // Pop up to the start destination of the graph to
                // remove all back stack entries
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
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