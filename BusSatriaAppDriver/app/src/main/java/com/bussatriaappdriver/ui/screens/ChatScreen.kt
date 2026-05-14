package com.bussatriaappdriver.ui.screens

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bussatriaappdriver.data.model.ChatMessage
import com.bussatriaappdriver.ui.theme.bluewa
import com.bussatriaappdriver.ui.viewmodel.AuthViewModel
import com.bussatriaappdriver.ui.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    val storagePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121B22) else Color(0xFFECE5DD)

    LaunchedEffect(Unit) {
        authViewModel
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        Column {
            ChatTopBar(navController)

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                var previousDate: String? = null
                items(messages.reversed()) { message ->
                    val currentDate = getDateLabel(message.timestamp)
                    if (currentDate != previousDate) {
                        DateSeparator(date = currentDate, isDarkTheme = isDarkTheme)
                        previousDate = currentDate
                    }
                    ChatMessageItem(message, isDarkTheme)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Spacer to push content up
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Menampilkan pesan bahwa driver hanya dapat melihat pesan
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Text(
                text = "Driver hanya dapat melihat pesan",
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }

    LaunchedEffect(messages) {
        scrollState.animateScrollToItem(0)
    }
}

fun getDateLabel(timestamp: Date): String {
    val calendar = Calendar.getInstance()
    val today = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.time

    return when {
        timestamp >= today -> "Hari ini"
        timestamp >= yesterday -> "Kemarin"
        else -> SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(timestamp)
    }
}

@Composable
fun DateSeparator(date: String, isDarkTheme: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = date,
            color = if (isDarkTheme) Color.LightGray else Color.DarkGray,
            fontSize = 12.sp,
            modifier = Modifier
                .background(
                    color = if (isDarkTheme) Color(0xFF121B22) else Color(0xFFECE5DD),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .align(Alignment.Center)
        )
    }
}
@Composable
fun ChatTopBar(
    navController: NavController
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else bluewa
    val textColor = Color.White

    TopAppBar(
        title = { Text(text = "Obrolan", color = textColor) },
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
        elevation = 4.dp,
        modifier = Modifier.statusBarsPadding()
    )
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isCurrentUser = message.senderId == currentUser?.uid
    val bubbleColor = if (isCurrentUser) Color(0xFF25D366) else Color.White
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        if (!isCurrentUser) {
            Text(
                text = message.senderName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .background(
                        color = bubbleColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (message.type == "image") {
                    Image(
                        painter = rememberImagePainter(message.imageUrl),
                        contentDescription = "Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = if (isCurrentUser) 0.dp else 4.dp)
        )
    }
}
