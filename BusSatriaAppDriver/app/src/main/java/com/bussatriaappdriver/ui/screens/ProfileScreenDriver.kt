package com.bussatriaappdriver.ui.screens

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
import com.bussatriaappdriver.ui.theme.DarkPrimaryPurple
import com.bussatriaappdriver.ui.theme.LightPrimaryPurple
import com.bussatriaappdriver.ui.theme.darkTextPrimary
import com.bussatriaappdriver.ui.theme.gray
import com.bussatriaappdriver.ui.theme.lightTextPrimary
import com.bussatriaappdriver.ui.theme.onSurface
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
import com.bussatriaappdriver.R
import com.bussatriaappdriver.animate.bounceClick
import com.bussatriaappdriver.animate.pressClickEffect
import com.bussatriaappdriver.component.CustomStyleTextField
import com.bussatriaappdriver.component.isValidPassword
import com.bussatriaappdriver.navigation.Destination
import com.bussatriaappdriver.ui.theme.DarkPrimaryPurple
import com.bussatriaappdriver.ui.theme.LightPrimaryPurple
import com.bussatriaappdriver.ui.viewmodel.AuthViewModel
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
import com.bussatriaappdriver.ui.theme.transparantColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.bussatriaappdriver.ui.theme.abuMuda
import com.bussatriaappdriver.ui.theme.abuTua
import com.bussatriaappdriver.ui.theme.surface

@Composable
fun ProfileScreenDriver(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    context: Context,
    selectedIndex: MutableState<Int>
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val iconColor = if (isDarkTheme) abuMuda else abuTua
    val cardColor = if (isDarkTheme) surface else onSurface
    val text2Color = if (isDarkTheme) Color.Black else Color.Black
    var showLogoutDialog by remember { mutableStateOf(false) }
    val textColor = if (isDarkTheme) Color.White else Color.Black

    systemUiController.setSystemBarsColor(color = backgroundColor)

    CompositionLocalProvider(LocalContext provides context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .navigationBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                backgroundColor = backgroundColor,
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Ensures padding respects status bar
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

                ProfileInfoSectionDriver("Bus", "Bus 1", Icons.Default.DirectionsBus, textColor, iconColor)
                ProfileInfoSectionDriver("Plat Nomor", "AB 12345 AN", Icons.Default.Numbers, textColor, iconColor)
                ProfileInfoSectionDriver("Nama Driver", "mohammad.rangga.n.f@gmail.com", Icons.Default.Person, textColor, iconColor)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    backgroundColor = cardColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileOptionItemDriver("Edit Profil", Icons.Default.Edit, text2Color, iconColor, onClick = {
                            // Handle Edit Profile Click
                        })
                        ProfileOptionItemDriver("Butuh Bantuan", Icons.Default.HelpCenter, text2Color, iconColor, onClick = {
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

    if (showLogoutDialog) {
        LogoutConfirmationDialogDriver(
            onConfirmLogout = {
                viewModel.logoutDriver(context)
                showLogoutDialog = false
                navController.navigate(Destination.LoginScreenDriver) {
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

    LaunchedEffect(viewModel.driverAuthState) {
        val authState = viewModel.driverAuthState.value
        if (authState == null) {
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

@Composable
fun ProfileInfoSectionDriver(title: String, info: String, icon: ImageVector, textColor: Color, iconColor: Color) {
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
fun ProfileOptionItemDriver(title: String, icon: ImageVector, textColor: Color, iconColor: Color, onClick: () -> Unit) {
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
fun LogoutConfirmationDialogDriver(
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
fun ScreenPreviewProfileDriver() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(0) }
    ProfileScreenDriver(
        navController = navController,
        viewModel = hiltViewModel(), // Or provide a mock ViewModel for the preview
        context = LocalContext.current,
        selectedIndex = selectedIndex
    )
}