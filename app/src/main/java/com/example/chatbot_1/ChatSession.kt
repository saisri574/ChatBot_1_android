package com.example.chatbot_1
import java.util.UUID
data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "New Chat",
    val messages: MutableList<MessageModel> = mutableListOf()
)

