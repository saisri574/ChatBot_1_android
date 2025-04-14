package com.example.chatbot_1

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx. compose. ui. graphics. Brush
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx. compose. runtime. remember
import androidx. compose. runtime. getValue
import androidx.compose.runtime.setValue
import androidx. compose. material3.Icon
import androidx.compose.ui.text.font.FontWeight
import kotlin.collections.isNotEmpty
import kotlin.collections.lastIndex
import kotlin.collections.reversed


@Composable
fun ChatPage(modifier: Modifier = Modifier,viewModel: ChatViewModel){
    val listState = rememberLazyListState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
//            .imePadding()
    ) {
//       importing Appheader
        AppHeader()
        MessageList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            messageList = viewModel.messageList,
            listState = listState
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            })
    }
}
@Composable
fun MessageList(modifier: Modifier = Modifier,
                messageList: List<MessageModel>,
                listState: LazyListState) {
//    val listState = rememberLazyListState()

    // Auto-scroll to bottom whenever messageList changes
    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        state = listState,
        reverseLayout = true
    ) {
        items(messageList.reversed()) { message ->
            MessageRow(messageModel = message)
        }
    }
    }


@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "assistant"
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryLight = primaryColor.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = if (isModel) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        if (isModel) {
            // Bot Message
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBDBDBD),
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = messageModel.message,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        } else {
            // 👤 User Message with dynamic theme-based gradient
            val gradientBrush = Brush.horizontalGradient(
                listOf(primaryColor, primaryLight)
            )

            Box(
                modifier = Modifier
                    .background(
                        brush = gradientBrush,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = messageModel.message,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "💬 Chat Bot",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



@Composable
fun MessageInput(onMessageSend: (String)-> Unit) {
    var message by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
//          what ever we type in the text box
            })
        IconButton(onClick = {
            onMessageSend(message)
            message = ""
        }) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
        }
    }
}

