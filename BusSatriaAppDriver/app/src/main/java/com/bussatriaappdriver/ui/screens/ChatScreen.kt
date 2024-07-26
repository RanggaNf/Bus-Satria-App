package com.bussatriaappdriver.ui.screens

import com.bussatriaappdriver.ui.viewmodel.ChatViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bussatriaappdriver.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bussatriaappdriver.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
    currentUserId: String,
    currentUserName: String
) {
    val messages by viewModel.messages.collectAsState()
    val (inputText, setInputText) = remember { mutableStateOf("") }
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val iconColor = if (isDarkTheme) abuMuda else abuTua

    systemUiController.setSystemBarsColor(color = backgroundColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Chat",
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
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = backgroundColor)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true  // To show newest messages at the bottom
        ) {
            items(messages) { message ->
                ChatMessageItem(message, currentUserId, textColor, buttonColor)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(onClick = {
                // Implement image picker logic here
            }) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Send Image",
                    tint = iconColor
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = setInputText,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = iconColor
                ),
                textStyle = TextStyle(color = textColor)
            )

            IconButton(onClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText, currentUserId, currentUserName)
                    setInputText("")
                }
            }) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = buttonColor
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, currentUserId: String, textColor: Color, bubbleColor: Color) {
    val isCurrentUser = message.senderId == currentUserId
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bubbleColor
        ) {
            when (message.type) {
                "text" -> Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White
                )
                "image" -> AsyncImage(
                    model = message.imageUrl,
                    contentDescription = "Chat Image",
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        Text(
            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

//@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun ChatScreenPreview() {
//    val navController = rememberNavController()
//    val viewModel = hiltViewModel<ChatViewModel>()
//    ChatScreen(
//        navController = navController,
//        viewModel = viewModel,
//        currentUserId = "user1",
//        currentUserName = "John Doe"
//    )
//}