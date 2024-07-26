package com.bussatriaapp.ui.screens


import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bussatriaapp.ui.theme.BusSatriaAppTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bussatriaapp.data.model.ChatMessage
import com.bussatriaapp.data.repository.ChatRepository
import com.bussatriaapp.ui.theme.BusSatriaAppTheme
import com.bussatriaapp.ui.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onImagePick: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        ChatTopBar()

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatMessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        ChatInputField(
            messageText = messageText,
            onMessageChange = { messageText = it },
            onSendClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(messageText)
                    messageText = ""
                }
            },
            onImageClick = {
                when (storagePermissionState.status) {
                    PermissionStatus.Granted -> onImagePick()
                    is PermissionStatus.Denied -> storagePermissionState.launchPermissionRequest()
                }
            }
        )
    }

    LaunchedEffect(messages) {
        scrollState.animateScrollToItem(0)
    }
}

@Composable
fun ChatTopBar() {
    TopAppBar(
        title = { Text("Chat") },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    )
}

@Composable
fun ChatInputField(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onImageClick) {
            Icon(Icons.Default.Image, contentDescription = "Send image")
        }
        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendClick,
            enabled = messageText.isNotBlank()
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isCurrentUser = message.senderId == FirebaseAuth.getInstance().currentUser?.uid
    val backgroundColor = if (isCurrentUser) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
    val textColor = if (isCurrentUser) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = message.senderName,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            when (message.type) {
                "text" -> Text(text = message.content, color = textColor)
                "image" -> AsyncImage(
                    model = message.imageUrl,
                    contentDescription = "Sent image",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.timestamp),
            fontSize = 10.sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}