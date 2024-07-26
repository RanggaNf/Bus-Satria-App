package com.bussatriaapp.ui.screens

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
import com.bussatriaapp.R
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaapp.animate.bounceClick
import com.bussatriaapp.animate.pressClickEffect
import com.bussatriaapp.component.CustomStyleTextField
import com.bussatriaapp.component.isValidPassword
import com.bussatriaapp.navigation.Destination
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.theme.darkTextPrimary
import com.bussatriaapp.ui.theme.lightTextPrimary
import com.bussatriaapp.ui.theme.transparantColor
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val imageList = listOf(R.drawable.kediri1)
    val pagerState = rememberPagerState(pageCount = { imageList.size })
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val pageIndicatorColor = if (isDarkTheme) Color.White else Color.Black
    val textColor = if (isDarkTheme) lightTextPrimary else darkTextPrimary
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val borderColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        systemUiController.setSystemBarsColor(
            color = Color(0xFF000000).copy(alpha = 0.3f),
            darkIcons = !isDarkTheme
        )
        listState.animateScrollToItem(index = listState.layoutInfo.totalItemsCount)
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
                        endY = 1500f
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 22.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(400.dp))
                    Text(
                        text = "Login",
                        color = textColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 40.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Login Untuk Melanjutkan",
                        color = textColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Masukan Email",
                        color = textColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    CustomStyleTextField(
                        placeHolder = "Email",
                        leadingIcon = Icons.Default.Email,
                        trailingIcon = null,
                        keyboardType = KeyboardType.Email,
                        visualTransformation = VisualTransformation.None,
                        value = email,
                        onValueChange = { email = it },
                        textColor = textColor,
                        backgroundColor = transparantColor,
                        borderColor = borderColor,
                        errorColor = Color.Red,
                        errorMessage = null,
                        showTrailingIcon = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Masukan Password",
                        color = textColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    CustomStyleTextField(
                        placeHolder = "Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        value = password,
                        onValueChange = { password = it },
                        textColor = textColor,
                        backgroundColor = transparantColor,
                        borderColor = borderColor,
                        errorColor = Color.Red,
                        errorMessage = if (password.isNotEmpty() && !isValidPassword(password)) "Password must contain at least 6 characters, including at least one letter and one number" else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val annotatedText = buildAnnotatedString {
                        append("Belum Memiliki Akun? ")
                        pushStringAnnotation(tag = "REGISTER", annotation = "Belum Memiliki Akun?Register sekarang")
                        withStyle(style = SpanStyle(color = buttonColor, fontWeight = FontWeight.Bold)) {
                            append("Register sekarang")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedText,
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                                .firstOrNull()?.let {
                                    navController.navigate(Destination.RegisterScreen)
                                }
                        },
                        style = TextStyle(color = textColor, fontSize = 14.sp),
                        modifier = Modifier.bounceClick()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Button(
                onClick = {
                    isLoading = true
                    viewModel.login(email, password, context)
                },
                modifier = Modifier
                    .padding(bottom = 25.dp, start = 20.dp, end = 20.dp)
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
    }

    authState?.let {
        isLoading = false // Atur isLoading menjadi false setelah proses autentikasi selesai atau terjadi kesalahan
        if (it.isSuccess) {
            navController.navigate(Destination.HomeScreen) {
                popUpTo(Destination.LoginScreen) { inclusive = true }
            }
        } else {
            Toast.makeText(context, it.exceptionOrNull()?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }
}


@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}