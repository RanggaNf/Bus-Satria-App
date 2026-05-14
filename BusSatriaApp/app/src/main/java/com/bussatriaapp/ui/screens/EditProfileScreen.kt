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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.theme.surface
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    context: Context
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconColor = if (isDarkTheme) abuMuda else abuTua
    val cardColor = if (isDarkTheme) surface else onSurface
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray
    val transparantColor = if (isDarkTheme) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val roleOptions = listOf("Pelajar", "Mahasiswa", "Pekerja", "Umum")

    val userData by viewModel.userData.collectAsState()

    LaunchedEffect(userData) {
        name = userData?.get("name") as? String ?: ""
        email = userData?.get("email") as? String ?: ""
        selectedRole = userData?.get("role") as? String ?: ""
    }

    systemUiController.setSystemBarsColor(color = backgroundColor)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin ingin memperbarui profil?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(name, email, selectedRole, newPassword).also {
                            showToast = true
                            navController.popBackStack() // Kembali ke halaman profil
                        }
                        showDialog = false
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    if (showToast) {
        Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Edit Profile",
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
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
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
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Nama", color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = textColor,
                        focusedBorderColor = buttonColor,
                        unfocusedBorderColor = iconColor
                    )
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email", color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = textColor,
                        focusedBorderColor = buttonColor,
                        unfocusedBorderColor = iconColor
                    )
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, shape = RoundedCornerShape(8.dp))
                ) {
                    TextField(
                        value = selectedRole.ifEmpty { "Pilih Peran" },  // Menambahkan placeholder text
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            backgroundColor = transparantColor,
                            textColor = if (selectedRole.isEmpty()) iconColor else textColor,  // Mengubah warna teks placeholder
                            cursorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roleOptions.forEach { roleOption ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedRole = roleOption
                                    expanded = false
                                }
                            ) {
                                Text(text = roleOption, color = textColor)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(text = "Password Baru", color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = textColor,
                        focusedBorderColor = buttonColor,
                        unfocusedBorderColor = iconColor
                    ),
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(
                                imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password",
                                tint = iconColor
                            )
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = "Konfirmasi Password", color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = textColor,
                        focusedBorderColor = buttonColor,
                        unfocusedBorderColor = iconColor
                    ),
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                tint = iconColor
                            )
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Button(
                    onClick = {
                        if (name.isEmpty() || email.isEmpty() || selectedRole.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(context, "Silakan isi semua field terlebih dahulu", Toast.LENGTH_SHORT).show()
                        } else if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                        } else {
                            showDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = buttonColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Simpan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
