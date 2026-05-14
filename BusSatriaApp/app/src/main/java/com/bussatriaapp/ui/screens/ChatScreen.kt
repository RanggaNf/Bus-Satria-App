@file:OptIn(ExperimentalFoundationApi::class)

package com.bussatriaapp.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bussatriaapp.R
import com.bussatriaapp.data.model.ChatMessage
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.bussatriaapp.ui.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onImagePick: () -> Unit,
    navController: NavController
) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121B22) else Color(0xFFECE5DD)
    val appBarColor = if (isDarkTheme) Color(0xFF1F2C34) else Color(0xFF075E54)

    var selectedMessage by remember { mutableStateOf<ChatMessage?>(null) }

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        ChatTopBar(
            backgroundColor = appBarColor,
            navController = navController,
            selectedMessage = selectedMessage,
            onDeleteSelectedMessage = {
                selectedMessage?.let {
                    viewModel.deleteMessage(it)
                    selectedMessage = null
                }
            },
            onCopySelectedMessage = {
                selectedMessage?.let {
                    clipboardManager.setText(AnnotatedString(it.content))
                    selectedMessage = null
                }
            },
            onCancelSelection = {
                selectedMessage = null
            }
        )

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            val groupedMessages = messages.groupBy { message ->
                getDateLabel(message.timestamp)
            }.toList().asReversed()

            groupedMessages.forEach { (date, messagesForDate) ->
                items(messagesForDate.asReversed()) { message ->
                    ChatMessageItem(
                        message = message,
                        isDarkTheme = isDarkTheme,
                        onSelectMessage = { selectedMessage = it },
                        selectedMessage = selectedMessage
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    DateSeparator(date, isDarkTheme)
                }
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
            },
            isDarkTheme = isDarkTheme,
            context = LocalContext.current
        )
    }

    LaunchedEffect(messages) {
        scrollState.animateScrollToItem(0)
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    onSelectMessage: (ChatMessage) -> Unit,
    selectedMessage: ChatMessage?
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isCurrentUser = message.senderId == currentUser?.uid
    val bubbleColor = if (isCurrentUser) Color(0xFF25D366) else Color.White
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(if (message == selectedMessage) Color.LightGray else Color.Transparent)
            .combinedClickable(
                onClick = {},
                onLongClick = { onSelectMessage(message) }
            ),
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
    backgroundColor: Color,
    navController: NavController,
    selectedMessage: ChatMessage?,
    onDeleteSelectedMessage: () -> Unit,
    onCopySelectedMessage: () -> Unit,
    onCancelSelection: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = Color.White
    val currentUser = FirebaseAuth.getInstance().currentUser

    TopAppBar(
        title = { Text(text = if (selectedMessage != null) "" else "Obrolan", color = textColor) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }
        },
        actions = {
            selectedMessage?.let {
                IconButton(onClick = onCancelSelection) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = textColor)
                }
                IconButton(onClick = onCopySelectedMessage) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = textColor)
                }
                if (it.senderId == currentUser?.uid) {
                    IconButton(onClick = onDeleteSelectedMessage) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = textColor)
                    }
                }
            }
        },
        backgroundColor = backgroundColor,
        elevation = 8.dp,
        modifier = Modifier.statusBarsPadding()
    )
}

@Composable
fun ChatInputField(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onImageClick: () -> Unit,
    isDarkTheme: Boolean,
    context: Context // Add context as a parameter
) {
    var showEmojiSelector by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val backgroundColor = if (isDarkTheme) Color(0xFF1F2C34) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val iconTint = if (isDarkTheme) Color.LightGray else Color.Gray

    Column {
        if (showEmojiSelector) {
            EmojiSelector(
                onEmojiSelected = { emoji ->
                    onMessageChange(messageText + emoji)
                    showEmojiSelector = false
                },
                isDarkTheme = isDarkTheme
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showEmojiSelector = !showEmojiSelector }) {
                Icon(
                    if (showEmojiSelector) Icons.Default.KeyboardArrowDown else Icons.Default.EmojiEmotions,
                    contentDescription = "Emoji",
                    tint = iconTint
                )
            }
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = { Text("Ketik pesan", color = if (isDarkTheme) Color.LightGray else Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    cursorColor = if (isDarkTheme) Color.White else Color(0xFF075E54),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (messageText.isEmpty()) {
                IconButton(onClick = {
                    Toast.makeText(context, "Fitur belum dikonfigurasi", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = iconTint)
                }
                IconButton(onClick = {
                    Toast.makeText(context, "Fitur belum dikonfigurasi", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Camera, contentDescription = "Camera", tint = iconTint)
                }
            }

            IconButton(
                onClick = onSendClick,
                enabled = messageText.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = if (isDarkTheme) Color(0xFF00A884) else Color(0xFF075E54))
            }
        }
    }
}


@Composable
fun EmojiSelector(onEmojiSelected: (String) -> Unit, isDarkTheme: Boolean) {
    val emojis = listOf("😀", "😂", "😍", "🥳", "😎", "🤔", "👍", "👎", "❤️", "🎉")
    val backgroundColor = if (isDarkTheme) Color(0xFF1F2C34) else Color.White

    LazyRow(
        modifier = Modifier.background(backgroundColor)
    ) {
        items(emojis) { emoji ->
            Text(
                text = emoji,
                fontSize = 30.sp,
                modifier = Modifier
                    .clickable { onEmojiSelected(emoji) }
                    .padding(8.dp)
            )
        }
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
