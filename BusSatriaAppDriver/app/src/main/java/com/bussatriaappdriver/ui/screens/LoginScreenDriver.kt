package com.bussatriaappdriver.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bussatriaappdriver.R
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaappdriver.animate.bounceClick
import com.bussatriaappdriver.animate.pressClickEffect
import com.bussatriaappdriver.component.CustomStyleTextField
import com.bussatriaappdriver.component.isValidPassword
import com.bussatriaappdriver.navigation.Destination
import com.bussatriaappdriver.ui.theme.DarkPrimaryPurple
import com.bussatriaappdriver.ui.theme.LightPrimaryPurple
import com.bussatriaappdriver.ui.theme.darkTextPrimary
import com.bussatriaappdriver.ui.theme.lightTextPrimary
import com.bussatriaappdriver.ui.theme.transparantColor
import com.bussatriaappdriver.ui.viewmodel.AuthViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DriverLoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val imageList = listOf(R.drawable.kediri1)
    val pagerState = rememberPagerState(pageCount = { imageList.size })
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) lightTextPrimary else darkTextPrimary
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val borderColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple

    var passcode by remember { mutableStateOf("") }
    var isPasscodeVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    val driverAuthState by viewModel.driverAuthState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        systemUiController.setSystemBarsColor(
            color = Color(0xFF000000).copy(alpha = 0.3f),
            darkIcons = !isDarkTheme
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Full-screen image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, backgroundColor),
                        startY = 2f,
                        endY = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(500.dp))
            Text(
                text = "Driver Login",
                color = textColor,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Login Untuk Driver",
                color = textColor,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Masukan Kode Unik",
                color = textColor,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            CustomStyleTextField(
                placeHolder = "Kode Unik",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = if (isPasscodeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (isPasscodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                value = passcode,
                onValueChange = { passcode = it },
                textColor = textColor,
                backgroundColor = transparantColor,
                borderColor = borderColor,
                errorColor = Color.Red,
                errorMessage = if (passcode.isNotEmpty() && passcode.length < 6) "Passcode harus terdiri dari minimal 6 karakter" else null,
                showTrailingIcon = true,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isLoading = true
                    viewModel.loginDriver(passcode, context)
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
                if (isLoading) {
                    CircularProgressIndicator(color = backgroundColor)
                } else {
                    Text(
                        text = "Login",
                        color = backgroundColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                }
            }
        }

        // Handle navigation based on driverAuthState
        driverAuthState?.let { result ->
            if (result.isSuccess) {
                // Call the onLoginSuccess callback on successful login
                onLoginSuccess()
            } else if (result.isFailure) {
                // Display error message on login failure
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DriverLoginScreenPreview() {
    val navController = rememberNavController()
    DriverLoginScreen(navController, onLoginSuccess = {})
}
